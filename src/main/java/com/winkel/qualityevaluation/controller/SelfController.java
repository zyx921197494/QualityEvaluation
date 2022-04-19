package com.winkel.qualityevaluation.controller;
/*
  @ClassName UserServiceController
  @Description 幼儿园评估账户
  @Author winkel
  @Date 2022-03-14 14:43
  */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.winkel.qualityevaluation.config.mq.Producer;
import com.winkel.qualityevaluation.entity.Authority;
import com.winkel.qualityevaluation.entity.User;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex3;
import com.winkel.qualityevaluation.entity.task.EvaluateReportFile;
import com.winkel.qualityevaluation.entity.task.EvaluateSubmit;
import com.winkel.qualityevaluation.entity.task.EvaluateSubmitFile;
import com.winkel.qualityevaluation.entity.task.EvaluateTask;
import com.winkel.qualityevaluation.service.api.*;
import com.winkel.qualityevaluation.util.*;
import com.winkel.qualityevaluation.pojo.vo.ConsumerVo;
import com.winkel.qualityevaluation.pojo.vo.Index2Vo;
import com.winkel.qualityevaluation.pojo.vo.Index3Vo;
import com.winkel.qualityevaluation.pojo.vo.SubmitVo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/evaluate/self")
public class SelfController {

    @Autowired
    private UserService userService;

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

    @Autowired
    private MailUtil mailUtil;

    @Autowired
    private Producer producer;

    /**
     * desc: 判断园长是否是第一次登录(以是否启动评估任务来区分)
     * params: [request]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @GetMapping("/isFirstLogin")
    public ResponseUtil isFirstLogin(HttpServletRequest request) {
        Integer taskId = taskService.getTaskIdByUserId(getTokenUser(request).getId(), Const.TASK_TYPE_SELF);
        EvaluateTask task = taskService.getById(taskId);
        if (task.getStartTime() != null || !Objects.equals(task.getStatus(), Const.TASK_NOT_START)) {
            return new ResponseUtil(200, "成功", false);
        }
        return new ResponseUtil(200, "成功", true);
    }


    /**
     * desc: 园长启动自评
     * params: [request]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @GetMapping("/startEvaluation")
    public ResponseUtil startEvaluation(HttpServletRequest request) {
        String id = getTokenUser(request).getId();
        Integer taskId = taskService.getTaskIdByUserId(id, Const.TASK_TYPE_SELF);
        EvaluateTask task = taskService.getById(taskId);
        if (task.getStartTime() != null || !Objects.equals(task.getStatus(), Const.TASK_NOT_START)) {
            return new ResponseUtil(500, "自评已经启动，不能重复启动");
        }
        if (taskService.update(new UpdateWrapper<EvaluateTask>()
                .eq("evaluate_task_id", taskId)
                .set("task_status", Const.TASK_IN_EVALUATION)
                .set("evaluate_task_start_time", LocalDateTime.now())
                .set("evaluate_task_end_time", LocalDateTime.now().plusDays(15)))) {
            List<User> userList = userService.getAllUserByTaskId(taskId);
            ConsumerVo vo = new ConsumerVo().setTaskId(taskId).setUserList(userList);
            producer.sendMsg("delayexchange", "delaykey", vo);
            return new ResponseUtil(200, "开启自评成功");
        }
        return new ResponseUtil(500, "开启自评失败");
    }


    /**
     * desc: 获取各2级指标的完成情况(某指标有一道及以上问题未评估则视为未完成)
     * params: [request]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @GetMapping("/getCompleteIndex")
    public ResponseUtil getCompleteIndex(HttpServletRequest request) {
        Integer taskId = taskService.getTaskIdByUserId(getTokenUser(request).getId(), Const.TASK_TYPE_SELF);
        List<Index2Vo> voList = submitService.getComplete(taskId);  // 各指标完成情况
        List<Index2Vo> index2s = submitService.getIndex2ByEvaluateId(taskService.getById(taskId).getEvaluateId());  // 各指标对应的问题数量
        ArrayList<Index2Vo> result = new ArrayList<>(index2s);
        for (Index2Vo vo : result) {
            vo.setIsComplete("未完成");
            for (Index2Vo index2Vo : voList) {
                if (Objects.equals(vo.getIndex2Id(), index2Vo.getIndex2Id())) {
                    vo.setIsComplete("已完成");
                }
            }
        }
        if (result.isEmpty()) {
            return new ResponseUtil(500, "未完成任何评估指标");
        }
        return new ResponseUtil(200, "查找指标完成情况成功", result);
    }


    /**
     * desc: 查看已填写的评估数据。其余未填写则返回三级指标
     * params: [request]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @GetMapping("/getSubmittedEvaluation")
    public ResponseUtil getSubmittedEvaluation(HttpServletRequest request) {
        Integer taskId = taskService.getTaskIdByUserId(getTokenUser(request).getId(), Const.TASK_TYPE_SELF);
        List<EvaluateSubmit> submits = submitService.list(new QueryWrapper<EvaluateSubmit>().eq("evaluate_task_id", taskId));  // 已提交数据
        List<EvaluateIndex3> index3s = index3Service.listIndex3ByEvaluateId(taskService.getById(taskId).getEvaluateId());  // 所有题目
        List<Index3Vo> result = new ArrayList<>(index3s.size());  // 最终返回的VoList
        int current = 0;
        for (int i = 0; i < 40; i++) {
            EvaluateIndex3 index3 = index3s.get(i);
            Index3Vo vo = new Index3Vo()
                    .setIndex3id(index3.getIndex3Id())
                    .setIndex3Name(index3.getIndex3Name())
                    .setIndex3Content(index3.getIndex3Content())
                    .setType(String.valueOf(index3.getType()))
                    .setMemo(index3.getMemo());
            if (current < submits.size() && Objects.equals(submits.get(current).getIndex3Id(), index3.getIndex3Id())) {  // 已经回答该题
                vo.setContent(submits.get(current).getContent());
                ++current;
            }
            result.add(vo);
        }
        if (result.isEmpty()) {
            return new ResponseUtil(200, "无记录");
        }
        return new ResponseUtil(200, "查询已填写评估数据成功", result);
    }


    /**
     * desc: 提交评估数据
     * params: [request, submitVos]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @PostMapping("/submitEvaluation")
    public ResponseUtil submitEvaluation(HttpServletRequest request, @RequestBody List<SubmitVo> submitVos) {
        Integer taskId = taskService.getTaskIdByUserId(getTokenUser(request).getId(), Const.TASK_TYPE_SELF);
        if (!Objects.equals(taskService.getOne(new QueryWrapper<EvaluateTask>().eq("evaluate_task_id", taskId)).getStatus(), Const.TASK_IN_EVALUATION)) {
            return new ResponseUtil(403, "现在还不能提交评估数据");
        }
        List<EvaluateSubmit> list = new ArrayList<>(submitVos.size());

        for (SubmitVo submitVo : submitVos) {
            EvaluateIndex3 index3 = index3Service.getById(submitVo.getIndex3Id());
            String[] scores = index3.getIndex3Score().split("\\|");
            Integer type = index3.getType();
            int score = 0;
            String content = submitVo.getContent();
            if (type == 1) {  // 判断
                if ("A".equalsIgnoreCase(content)) score = Integer.parseInt(scores[0]);
                else score = Integer.parseInt(scores[1]);
            } else if (type == 2) {  // 单选
                if ("A".equalsIgnoreCase(content)) score = Integer.parseInt(scores[0]);
                else if ("B".equalsIgnoreCase(content)) score = Integer.parseInt(scores[1]);
                else if ("C".equalsIgnoreCase(content)) score = Integer.parseInt(scores[2]);
                else score = Integer.parseInt(scores[3]);
            } else if (type == 3) {  // 多选
                if (content.contains("A")) score += Integer.parseInt(scores[0]);
                if (content.contains("B")) score += Integer.parseInt(scores[1]);
                if (content.contains("C")) score += Integer.parseInt(scores[2]);
                if (content.contains("D")) score += Integer.parseInt(scores[3]);
            }

            if (!submitService.update(new UpdateWrapper<EvaluateSubmit>()
                    .eq("evaluate_task_id", taskId)
                    .eq("evaluate_index3_id", submitVo.getIndex3Id())
                    .set("submit_content", content)
                    .set("score", score)
                    .set("submit_time", LocalDateTime.now()))) {  // 不存在这条评估记录(更新失败)则加入list批量插入
                list.add(new EvaluateSubmit()
                        .setTaskId(taskId)
                        .setIndex3Id(submitVo.getIndex3Id())
                        .setContent(content)
                        .setScore(score)
                        .setSubmitTime(LocalDateTime.now()));
            }
        }

        if (list.isEmpty()) {
            return new ResponseUtil(200, "提交评估数据成功");
        }
        if (submitService.saveOrUpdateBatch(list)) {
            return new ResponseUtil(200, "提交评估数据成功");
        }
        return new ResponseUtil(500, "提交评估数据失败");
    }


    /**
     * desc: 园长上传评估证据文件(在in_evaluation状态下进行)
     * params: [request, file]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @PostMapping("/uploadEvidence")
    public ResponseUtil uploadEvidence(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
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
                return new ResponseUtil(200, "上传自评证据成功", null);
            }
            return new ResponseUtil(500, "文件记录写入数据库时出错");
        }

        return new ResponseUtil(500, "上传文件时失败", result.getMsg());
    }

    /**
     * desc: 园长完成自评
     * params: [request]
     * return: boolean
     * exception:
     **/
    @GetMapping("/finishEvaluation")
    public ResponseUtil finishEvaluation(HttpServletRequest request, @RequestParam String email, @RequestParam String code) {
        // 校验邮箱验证码
        RedisResult result = mailUtil.validateEmailCode(email, code);
        if (!Objects.equals(result.getStatus(), Const.REDIS_CODE_RIGHT)) {
            return new ResponseUtil(500, result.getMsg());
        }
        User user = getTokenUser(request);
        User dbUser = userService.getOne(new QueryWrapper<User>().eq("id", user.getId()).select("school_code", "cycle"));
        Integer taskId = taskService.getTaskIdByUserId(user.getId(), Const.TASK_TYPE_SELF);

        boolean success = taskService.update(new UpdateWrapper<EvaluateTask>().eq("evaluate_task_id", taskId).set("task_status", Const.TASK_DATA_SUBMITTED)) &&
                userService.lockUserBySchoolCodeAndType(dbUser.getSchoolCode(), Const.ROLE_EVALUATE_SELF);
        if (success) {
            return new ResponseUtil(200, "完成自评任务");
        }
        return new ResponseUtil(500, "提交自评任务失败");
    }


    /**
     * desc: 园长导出评估数据
     * params: [request]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @SneakyThrows
    @GetMapping("/exportEvaluation")
    public ResponseUtil exportEvaluation(HttpServletRequest request) {
        Integer taskId = taskService.getAllTaskIdByUserId(getTokenUser(request).getId(), Const.TASK_TYPE_SELF);
        List<EvaluateSubmit> submits = submitService.list(new QueryWrapper<EvaluateSubmit>().eq("evaluate_task_id", taskId));

        List<Index3Vo> index3VoList = new ArrayList<>(submits.size());
        for (EvaluateSubmit submit : submits) {
            EvaluateIndex3 index = index3Service.getOne(new QueryWrapper<EvaluateIndex3>().eq("evaluate_index3_id", submit.getIndex3Id()));
            index3VoList.add(new Index3Vo()
                    .setIndex3id(index.getIndex3Id())
                    .setIndex3Name(index.getIndex3Name())
                    .setIndex3Content(index.getIndex3Content())
                    .setType(index.getType() == 1 ? "判断" : index.getType() == 2 ? "单选" : "多选")
                    .setMemo(index.getMemo())
                    .setSubmitTime(submit.getSubmitTime())
                    .setContent(submit.getContent()));
        }

        String directoryPath = "C:\\Users\\Public\\Downloads\\";
        File path = new File(directoryPath);
        File file = new File(directoryPath + "评估数据.xlsx");

        if (path.isDirectory()) {  // 路径为目录则创建Excel文件
            ossUtil.downloadSimple("评估数据.xlsx", directoryPath);
            log.info("下载文件模板文件 评估数据.xlsx");
        }
        if (file.exists()) {
            ExcelUtil.writeExcel(index3VoList, file.getAbsolutePath(), true);
            return new ResponseUtil(200, "导出评估数据文件成功");
        }
        return new ResponseUtil(200, "导出评估数据文件失败");
    }

    /**
     * desc: 园长导出评估证据文件
     * params: [request]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @GetMapping("/exportEvidence")
    public ResponseUtil exportEvidence(HttpServletRequest request) {
        User user = getTokenUser(request);
//        EvaluateTask task = taskService.getTaskByUserId(user.getId(), getTaskTypeFromTokenUser(request));
//        System.out.println("task = " + task);
        Integer taskId = taskService.getAllTaskIdByUserId(user.getId(), Const.TASK_TYPE_SELF);
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
            log.info("下载评估证据文件 {} 至 {}", submitFile.getFileName(), directoryPath);
            count++;
        }
        if (count == files.size()) return new ResponseUtil(200, "下载证据文件成功");
        return new ResponseUtil(500, "下载证据文件失败");
    }

    /**
     * desc: 园长上传自评报告
     * params: [request, file]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @PostMapping("/uploadSelfReport")
    public ResponseUtil uploadSelfReport(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        User user = getTokenUser(request);
        Integer taskId = taskService.getAllTaskIdByUserId(user.getId(), Const.TASK_TYPE_SELF);
        Integer taskStatus = taskService.getOne(new QueryWrapper<EvaluateTask>().eq("evaluate_task_id", taskId)).getStatus();

        if (taskStatus.equals(Const.TASK_REPORT_ACCEPTED)) return new ResponseUtil(500, "报告已审核通过，无法再次提交");
        else if (taskStatus.equals(Const.TASK_NOT_START) || taskStatus.equals(Const.TASK_IN_EVALUATION)) {
            return new ResponseUtil(403, "还不能提交报告，请先完成评估");
        }

        UploadResult result;
        if (file.getSize() < 1000000L) {
            result = ossUtil.uploadWithSignature(file, OssUtil.REPORT_SUFFIX);
            log.info("签名上传报告，当前报告大小 {} MB", file.getSize() >> 20);
        } else {
            result = ossUtil.uploadWithMultipart(file, OssUtil.REPORT_SUFFIX);
            log.info("分片上传报告，当前报告大小 {} MB", file.getSize() >> 20);
        }
        EvaluateReportFile dbReportFile = reportFileService.getOne(new QueryWrapper<EvaluateReportFile>().eq("task_id", taskId));

        boolean isReWrite = dbReportFile != null;
        if (Objects.equals(result.getStatus(), UploadStatus.DONE.getStatus())) {
            if (isReWrite) {
                if (reportFileService.update(new UpdateWrapper<EvaluateReportFile>().eq("report_file_id", dbReportFile.getId())
                        .set("report_file_name", result.getFilename())
                        .set("report_file_path", result.getUrl())
                        .set("report_file_size", (int) file.getSize())
                        .set("report_file_upload_time", LocalDateTime.now())
                        .set("report_file_memo", file.getOriginalFilename()))
                        && taskService.update(new UpdateWrapper<EvaluateTask>().eq("evaluate_task_id", taskId).set("task_status", Const.TASK_REPORT_SUBMITTED))
                        && ossUtil.deleteFile(dbReportFile.getFileName())) {  // 修改task为已提交报告状态
                    return new ResponseUtil(200, "覆盖报告成功");
                }
                return new ResponseUtil(500, "报告记录更新数据库时出错");
            } else {
                EvaluateReportFile reportFile = new EvaluateReportFile()
                        .setTaskId(taskId)
                        .setFileName(result.getFilename())
                        .setFilePath(result.getUrl())
                        .setSize((int) file.getSize())
                        .setUploadTime(LocalDateTime.now())
                        .setMemo(file.getOriginalFilename());
                if (reportFileService.save(reportFile) &&
                        taskService.update(new UpdateWrapper<EvaluateTask>().eq("evaluate_task_id", taskId).set("task_status", Const.TASK_REPORT_SUBMITTED))) {  // 修改task为已提交报告状态
                    return new ResponseUtil(200, "上传自评报告成功");
                }
                return new ResponseUtil(500, "报告记录写入数据库时出错");
            }
        }

        return new ResponseUtil(500, "上传报告时失败", result.getMsg());
    }


    /**
     * desc: 园长下载督评和各级复评报告
     * params: [request, type] type：意见书类型，督评2、县复评3、市复评4、省复评5
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @GetMapping("/downloadReport")
    public ResponseUtil downloadReport(HttpServletRequest request, Integer type) {
        if (type < 2 || type > 5) {
            return new ResponseUtil(500, "参数范围错误");
        }
        Integer taskId = taskService.getAllTaskIdByUserId(getTokenUser(request).getId(), type);
        EvaluateTask task = taskService.getById(taskId);
        if (!Objects.equals(task.getStatus(), Const.TASK_REPORT_ACCEPTED)) {
            return new ResponseUtil(200, "报告暂未上传或未通过审核");
        }
        EvaluateReportFile report = reportFileService.getOne(new QueryWrapper<EvaluateReportFile>().eq("task_id", taskId));
        boolean success = ossUtil.downloadSimple(report.getFileName(), null);
        if (success) {
            return new ResponseUtil(200, "下载报告成功");
        }
        return new ResponseUtil(200, "下载报告时出错");
    }


    private User getTokenUser(HttpServletRequest request) {
        return JWTUtil.parseJWTUser(request.getHeader(Const.TOKEN_HEADER).substring(Const.STARTS_WITH.length()));
    }

    private Integer getTaskTypeFromTokenUser(HttpServletRequest request) {
        User user = getTokenUser(request);
        List<Authority> authorities = userService.getAuthorities(user.getUsername());
        for (Authority authority : authorities) {
            if (authority.getAuthority().equalsIgnoreCase("ROLE_EVALUATE_SELF")) //前 ROLE_EVALUATE_LEADER_SELF
                return Const.TASK_TYPE_SELF;
            else if (authority.getAuthority().equalsIgnoreCase("ROLE_EVALUATE_LEADER_SUPERVISOR"))
                return Const.TASK_TYPE_SUPERVISOR;
        }
        return 0;
    }

}
