package com.winkel.qualityevaluation.controller;
/*
  @ClassName UserServiceController
  @Description 幼儿园评估账户
  @Author winkel
  @Date 2022-03-14 14:43
  */

import com.aliyun.oss.model.SelectObjectRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.winkel.qualityevaluation.config.oss.OSSConfig;
import com.winkel.qualityevaluation.entity.Authority;
import com.winkel.qualityevaluation.entity.User;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex1;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex2;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex3;
import com.winkel.qualityevaluation.entity.task.EvaluateReportFile;
import com.winkel.qualityevaluation.entity.task.EvaluateSubmit;
import com.winkel.qualityevaluation.entity.task.EvaluateSubmitFile;
import com.winkel.qualityevaluation.entity.task.EvaluateTask;
import com.winkel.qualityevaluation.service.api.*;
import com.winkel.qualityevaluation.util.*;
import com.winkel.qualityevaluation.vo.SubmitVo;
import com.winkel.qualityevaluation.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/evaluate")
public class UserServiceController {

    private final String TOKEN_HEADER = "Authorization";

    private final String STARTS_WITH = "Bearer ";

    @Autowired
    private UserService userService;

    @Autowired
    private Index1Service index1Service;

    @Autowired
    private Index2Service index2Service;

    @Autowired
    private Index3Service index3Service;

    @Autowired
    private TaskService taskService;

    @Autowired
    private SubmitService submitService;

    @Autowired
    private SubmitFileService submitFileService;

    @Autowired
    private ReportFileService reportFileService;

    @Autowired
    private OssUtil ossUtil;

    @GetMapping("/aaa")
    public User aaa() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>().eq("username", "user").select("username", "password", "is_locked");
        return userService.getOne(queryWrapper);
    }

    // 查询一二三级指标
    @GetMapping("/getIndex1")
    public List<EvaluateIndex1> getIndex1() {
        return index1Service.list();
    }

    @GetMapping("/getIndex2")
    public List<EvaluateIndex2> getIndex2() {
        return index2Service.list();
    }

    @GetMapping("/getIndex3")
    public ResponseUtil getIndex3(HttpServletRequest request) {
        User user = getTokenUser(request);
        Integer taskId = taskService.getTaskIdByUserId(user.getId(), Const.TASK_TYPE_SELF);
        HashMap<String, Object> map = new HashMap<>();
        map.put("index3", index3Service.list());
        map.put("taskId", taskId);
        return new ResponseUtil(200, "查找评估问题成功", map);
    }

    // 园长/组长提交个人信息
    @PostMapping("/updateUser")
    public boolean updateUser(@RequestBody UserVo userVo) {
        return userService.update(new UpdateWrapper<User>()
                .eq("id", userVo.getId())
                .set("name", userVo.getName())
                .set("email", userVo.getEmail()));
    }

    //园长开启自评(更新task)
    @GetMapping("/startEvaluation")
    public boolean startEvaluation(HttpServletRequest request) {
        String id = getTokenUser(request).getId();
        Integer taskId = taskService.getTaskIdByUserId(id, Const.TASK_TYPE_SELF);
        return taskService.update(new UpdateWrapper<EvaluateTask>()
                .eq("evaluate_task_id", taskId)
                .set("task_status", Const.TASK_IN_EVALUATION)
                .set("evaluate_task_start_time", LocalDateTime.now())
                .set("evaluate_task_end_time", LocalDateTime.now().plusDays(15)));
    }

    //园长/教师提交评估问题答案(根据index3和selected(接受一个VO(taskId, index3Id, selected)计算得分，插入submit)
    //提交评估数据及附件 todo 附件
    @PostMapping("/submitEvaluation")
    public boolean submitEvaluation(@RequestBody List<SubmitVo> submitVos) {  // , @RequestParam("files")MultipartFile[] files
        List<EvaluateSubmit> list = new ArrayList<>(submitVos.size());

        for (SubmitVo submitVo : submitVos) {
            int score = 0;
            int type = submitVo.getType();
            String content = submitVo.getContent();
            if (type == 1 && "A".equalsIgnoreCase(content)) {
                score = 20;
            } else if (type == 2) {
                if ("B".equalsIgnoreCase(content)) score = 10;
                else if ("C".equalsIgnoreCase(content)) score = 20;
                else if ("D".equalsIgnoreCase(content)) score = 30;
            } else if (type == 3) {
                score = submitVo.getContent().length() * 10;
            }
            list.add(new EvaluateSubmit()
                    .setTaskId(submitVo.getTaskId())
                    .setIndex3Id(submitVo.getIndex3Id())
                    .setContent(content)
                    .setScore(score)
                    .setSubmitTime(LocalDateTime.now())
                    .setIsLocked(0));
        }

        return submitService.saveBatch(list);
    }


    //园长上传文件(提交评估数据之后才能进行，修改task状态)
    @PostMapping("/uploadEvidence")
    public ResponseUtil uploadEvidence(HttpServletRequest request, @RequestParam("file") MultipartFile file) throws Exception {
        User user = getTokenUser(request);
        Integer taskId = taskService.getTaskIdByUserId(user.getId(), Const.TASK_TYPE_SELF);
        if (!Objects.equals(taskService.getOne(new QueryWrapper<EvaluateTask>().eq("evaluate_task_id", taskId)).getStatus(), Const.TASK_IN_EVALUATION)) {
            return new ResponseUtil(403, "现在还不能提交评估证据");
        }

        UploadResult result;
        if (file.getSize() < 1000000L) {
            result = ossUtil.uploadWithSignature(file, OssUtil.EVIDENCE_SUFFIX);
            log.info("签名上传证据，当前文件大小 {} MB", file.getSize() >> 20);
        } else {
            result = ossUtil.uploadWithMultipart(file, OssUtil.EVIDENCE_SUFFIX);
            log.info("分片上传证据，当前文件大小 {} MB", file.getSize() >> 20);
        }

        if (Objects.equals(result.getStatus(), UploadStatus.DONE.getStatus())) {
            EvaluateSubmitFile evaluateSubmitFile = new EvaluateSubmitFile()
                    .setTaskId(taskId)
                    .setFileName(result.getFilename())
                    .setFilePath(result.getUrl())
                    .setSize((int) file.getSize())
                    .setUploadTime(LocalDateTime.now())
                    .setMemo(file.getOriginalFilename());
            if (submitFileService.save(evaluateSubmitFile)) {
                return new ResponseUtil(200, "上传证明文件成功", null);
            }
            return new ResponseUtil(500, "文件记录写入数据库时出错");
        }

        return new ResponseUtil(500, "上传文件时失败", result.getMsg());
    }

    //园长完成自评(修改task状态，锁定submit，锁定教师user)
    @GetMapping("/finishEvaluation")
    public boolean finishEvaluation(HttpServletRequest request) {
        User user = getTokenUser(request);
        User dbUser = userService.getOne(new QueryWrapper<User>().eq("id", user.getId()).select("school_code", "cycle"));
        Integer taskId = taskService.getTaskIdByUserId(user.getId(), Const.TASK_TYPE_SELF);
        return submitService.update(new UpdateWrapper<EvaluateSubmit>().eq("evaluate_task_id", taskId).set("submit_is_locked", Const.LOCKED)) &&
                taskService.update(new UpdateWrapper<EvaluateTask>().eq("evaluate_task_id", taskId).set("task_status", Const.TASK_DATA_SUBMITTED)) &&
                userService.update(new UpdateWrapper<User>()
                        .eq("cycle", dbUser.getCycle())
                        .eq("school_code", dbUser.getSchoolCode())
                        .set("is_locked", Const.LOCKED));
    }

    //园长上传自评报告
    @PostMapping("/uploadSelfReport")
    public ResponseUtil uploadSelfReport(HttpServletRequest request, @RequestParam("file") MultipartFile file) throws Exception {
        User user = getTokenUser(request);
        Integer taskId = taskService.getTaskIdByUserId(user.getId(), Const.TASK_TYPE_SELF);
        Integer taskStatus = taskService.getOne(new QueryWrapper<EvaluateTask>().eq("evaluate_task_id", taskId)).getStatus();

        if (taskStatus.equals(Const.TASK_REPORT_ACCEPTED)) return new ResponseUtil(500, "报告已审核通过，无法再次提交");
        else if (taskStatus.equals(Const.TASK_NOT_START) || taskStatus.equals(Const.TASK_IN_EVALUATION)) {
            return new ResponseUtil(403, "还不能提交报告，请先完成评估");
        }
//        boolean exist = false;
//        EvaluateReportFile reportFile = reportFileService.getOne(new QueryWrapper<EvaluateReportFile>().eq("task_id", taskId).eq("is_audited", 0));
//        if (reportFile != null) {
//            if (reportFile.getIsAudited() == 1) ;
//            else exist = true;
//        }
//        return new ResponseUtil(500, "报告已被审核，无法修改");

        UploadResult result;
        if (file.getSize() < 1000000L) {
            result = ossUtil.uploadWithSignature(file, OssUtil.REPORT_SUFFIX);
            log.info("签名上传报告，当前报告大小 {} MB", file.getSize() >> 20);
        } else {
            result = ossUtil.uploadWithMultipart(file, OssUtil.REPORT_SUFFIX);
            log.info("分片上传报告，当前报告大小 {} MB", file.getSize() >> 20);
        }

        if (Objects.equals(result.getStatus(), UploadStatus.DONE.getStatus())) {
            EvaluateReportFile reportFile = new EvaluateReportFile()
                    .setTaskId(taskId)
                    .setFileName(result.getFilename())
                    .setFilePath(result.getUrl())
                    .setSize((int) file.getSize())
                    .setUploadTime(LocalDateTime.now())
                    .setMemo(file.getOriginalFilename());
            if (reportFileService.save(reportFile) &&
                    taskService.update(new UpdateWrapper<EvaluateTask>().eq("evaluate_task_id", taskId).set("task_status", Const.TASK_REPORT_SUBMITTED))) {  // 修改task为已提交报告状态
                return new ResponseUtil(200, "上传报告成功");
            }
            return new ResponseUtil(500, "报告记录写入数据库时出错");
        }

        return new ResponseUtil(500, "上传报告时失败", result.getMsg());

    }


    /**
     * desc: 通用下载文件接口
     * params: [filename]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @GetMapping("/download")
    public ResponseUtil download(@RequestParam("filename") String filename) {
        if (!ossUtil.isExist(filename)) {
            return new ResponseUtil(500, "文件路径错误，文件不存在");
        }
        if (ossUtil.getFileSize(filename) > 10000000L) {
            if (ossUtil.downloadWithBreakpoint(filename, null)) {
                log.info("断点续传下载文件 {}", filename);
                return new ResponseUtil(200, "下载成功");
            }
            return new ResponseUtil(500, "下载失败");
        } else {
            if (ossUtil.downloadSimple(filename, null)) {
                log.info("直接下载文件 {}", filename);
                return new ResponseUtil(200, "下载成功");
            }

            return new ResponseUtil(500, "下载失败");
        }
    }

    // 园长导出评估数据

    // 园长/督评组长导出评估证据文件 目前只能园长下载
    @GetMapping("/exportEvidence")
    public ResponseUtil exportEvidence(HttpServletRequest request) {
        User user = getTokenUser(request);
//        EvaluateTask task = taskService.getTaskByUserId(user.getId(), getTaskTypeFromTokenUser(request));
//        System.out.println("task = " + task);
        Integer taskId = taskService.getTaskIdByUserId(user.getId(), Const.TASK_TYPE_SELF);
        EvaluateTask task = taskService.getById(taskId);

        if (task.getStatus() < Const.TASK_DATA_SUBMITTED) {
            return new ResponseUtil(500, "请先完成评估");
        }

        String directoryPath = "C:\\Users\\Public\\Downloads\\evidence\\";
        File file = new File(directoryPath);
        if (!file.isDirectory()) {
            file.mkdir();
        }

        List<EvaluateSubmitFile> files = submitFileService.list(new QueryWrapper<EvaluateSubmitFile>().eq("evaluate_task_id", task.getId()));
        int count = 0;
        for (EvaluateSubmitFile submitFile : files) {
            ossUtil.downloadWithBreakpoint(submitFile.getFileName(), directoryPath);
            count++;
        }
        if (count == files.size()) return new ResponseUtil(200, "下载证据文件成功");
        return new ResponseUtil(500, "下载证据文件成功");
    }


    //园长下载督评/复评意见书


    //督评组长提交幼儿园信息

    //督评组长提交个人信息

    //督评组长开启督评

    //督评组长/督评人员提交评估问题答案

    //督评组长提交评估数据

    //督评组长上传文件(提交评估数据之后才能进行)

    //督评组长下载文件


    //开启复评

    @Test
    public void test() {
        System.out.println(547115 >> 20);
    }


    private User getTokenUser(HttpServletRequest request) {
        return JWTUtil.parseJWTUser(request.getHeader(TOKEN_HEADER).substring(STARTS_WITH.length()));
    }

    private Integer getTaskTypeFromTokenUser(HttpServletRequest request) {
        User user = getTokenUser(request);
        List<Authority> authorities = userService.getAuthorities(user.getUsername());
        for (Authority authority : authorities) {
            if (authority.getAuthority().equalsIgnoreCase("ROLE_EVALUATE_SELF"))
                return Const.TASK_TYPE_SELF;  //前 ROLE_EVALUATE_LEADER_SELF
            else if (authority.getAuthority().equalsIgnoreCase("ROLE_EVALUATE_LEADER_SUPERVISOR"))
                return Const.TASK_TYPE_SUPERVISOR;
        }
        return 0;
    }

}
