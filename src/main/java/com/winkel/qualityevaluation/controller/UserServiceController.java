package com.winkel.qualityevaluation.controller;
/*
  @ClassName UserServiceController
  @Description 幼儿园评估账户
  @Author winkel
  @Date 2022-03-14 14:43
  */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.winkel.qualityevaluation.entity.User;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex1;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex2;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex3;
import com.winkel.qualityevaluation.entity.task.EvaluateSubmit;
import com.winkel.qualityevaluation.entity.task.EvaluateTask;
import com.winkel.qualityevaluation.service.api.*;
import com.winkel.qualityevaluation.util.Const;
import com.winkel.qualityevaluation.util.JWTUtil;
import com.winkel.qualityevaluation.util.OssUtil;
import com.winkel.qualityevaluation.util.UploadResult;
import com.winkel.qualityevaluation.vo.SubmitVo;
import com.winkel.qualityevaluation.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public List<EvaluateIndex3> getIndex3() {
        return index3Service.list();
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

    //园长上传文件(提交评估数据之后才能进行，修改task状态)
    @PostMapping("/uploadEvidence")
    public UploadResult uploadEvidence(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.getSize() < 1000000L) {
            log.info("签名上传，当前文件大小 {} MB", file.getSize() >> 20);
            return ossUtil.uploadWithSignature(file, OssUtil.EVIDENCE_SUFFIX);
        } else {
            log.info("分片上传，当前文件大小 {} MB", file.getSize() >> 20);
            return ossUtil.uploadWithMultipart(file, OssUtil.EVIDENCE_SUFFIX);
        }
    }

    @GetMapping("/download")
    public String download(@RequestParam("fileUrl") String fileUrl) {
        return ossUtil.downloadWithBreakpoint(fileUrl, null);
    }

    //园长下载文件


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

}
