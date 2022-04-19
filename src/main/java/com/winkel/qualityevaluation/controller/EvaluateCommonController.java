package com.winkel.qualityevaluation.controller;
/*
  @ClassName EvaluateCommonController
  @Description 评估类账户通用接口
  @Author winkel
  @Date 2022-03-27 11:54
  */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.winkel.qualityevaluation.entity.User;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex1;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex2;
import com.winkel.qualityevaluation.entity.task.EvaluateSubmitFile;
import com.winkel.qualityevaluation.entity.task.EvaluateTask;
import com.winkel.qualityevaluation.service.api.*;
import com.winkel.qualityevaluation.util.*;
import com.winkel.qualityevaluation.pojo.vo.SchoolVo;
import com.winkel.qualityevaluation.pojo.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/evaluate/common")
public class EvaluateCommonController {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private Index1Service index1Service;

    @Autowired
    private Index2Service index2Service;

    @Autowired
    private Index3Service index3Service;

    @Autowired
    private SubmitFileService submitFileService;

    @Autowired
    private OssUtil ossUtil;

    @Autowired
    private MailUtil mailUtil;

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

    /**
     * desc: 获取当前用户
     * params: [request]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @GetMapping("/getCurrentUser")
    public ResponseUtil getCurrentUser(HttpServletRequest request) {
        String id = getTokenUser(request).getId();
        User user = userService.getOne(new QueryWrapper<User>().eq("id", id).select("id", "name", "email"));
        if (user != null) {
            return new ResponseUtil(200, "获取当前用户成功", user);
        }
        return new ResponseUtil(500, "获取当前用户失败");
    }


    /**
     * desc: 点击用户信息栏，更新用户 或 园长/组长第一次登录时填写用户
     * params: [userVo]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @PostMapping("/updateUserInfo")
    public ResponseUtil updateUserInfo(HttpServletRequest request, @RequestBody UserVo userVo) {
        if (userService.update(new UpdateWrapper<User>()
                .eq("id", getTokenUser(request).getId())
                .set("name", userVo.getName())
                .set("email", userVo.getEmail()))) {
            return new ResponseUtil(200, "提交个人信息成功");
        }
        return new ResponseUtil(500, "提交个人信息失败");
    }


    /**
     * desc: 获取已上传的自评证据文件列表
     * params: [request]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @GetMapping("/getSelfEvidence")
    public ResponseUtil getSelfEvidence(HttpServletRequest request) {
        Integer selfTaskId = taskService.getTaskIdByUserId(getTokenUser(request).getId(), Const.TASK_TYPE_SELF);
        List<EvaluateSubmitFile> selfEvidence = submitFileService.list(new QueryWrapper<EvaluateSubmitFile>().eq("evaluate_task_id", selfTaskId));
        if (selfEvidence.isEmpty()) {
            return new ResponseUtil(200, "自评未提交任何证据文件");
        }
        return new ResponseUtil(200, "查找自评证据成功", selfEvidence);
    }


    /**
     * desc: 发送验证码
     * params: [email]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/

    @GetMapping("/sendEmail")
    public ResponseUtil sendEmail(HttpServletRequest request, @RequestParam("email") String email) {
        String dbEmail = userService.getById(getTokenUser(request).getId()).getEmail();
        if (!StringUtils.equals(email, dbEmail)) {
            return new ResponseUtil(500, "请填写个人信息中的邮箱");
        }
        if (mailUtil.sendEmail(email)) {
            return new ResponseUtil(200, "邮件发送成功");
        }
        return new ResponseUtil(500, "邮件发送失败，请重试");
    }


    /**
     * desc: 幼儿园园长第一次登录时查看幼儿园信息
     * params: [request]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @GetMapping("/getSchoolInfo")
    public ResponseUtil getSchoolInfo(HttpServletRequest request) {
        String schoolCode = userService.getById(getTokenUser(request).getId()).getSchoolCode();
        SchoolVo school = schoolService.getSchoolVoBySchoolCode(schoolCode);
        if (school == null) {
            return new ResponseUtil(200, "幼儿园信息为空");
        }
        return new ResponseUtil(200, "查询幼儿园信息成功", school);
    }

    /**
     * desc: 查询任务周期
     * params: [request]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @GetMapping("/getTaskTime")
    public ResponseUtil getTaskTime(HttpServletRequest request) {
        String id = getTokenUser(request).getId();
        Integer taskId = taskService.getTaskIdByUserId(id, Const.TASK_TYPE_SELF);
        EvaluateTask task = taskService.getById(taskId);
        HashMap<String, Object> result = new HashMap<>();
        LocalDateTime start = task.getStartTime();
        LocalDateTime end = task.getEndTIme();
        if (start == null) {
            return new ResponseUtil(500, "评估任务暂未启动");
        }
        boolean close = (task.getStatus() >= Const.TASK_DATA_SUBMITTED) || LocalDateTime.now().isAfter(task.getEndTIme());
        result.put("start", start);
        result.put("end", end);
        result.put("close", close);

        return new ResponseUtil(200, "获取任务周期成功", result);
    }


//    /**
//     * desc: 未注册幼儿园首次登录时填写学校数据
//     * params: [school]
//     * return: com.winkel.qualityevaluation.util.ResponseUtil
//     * exception:
//     **/
//    @PostMapping("/updateSchool")
//    public ResponseUtil updateSchool(@RequestBody School school) {
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("school_name", school.getName());
//        map.put("school_location", school.getLocation());
//        map.put("school_location_code", school.getLocationCode());
//        map.put("school_location_type_code", school.getLocationTypeCode());
//        map.put("school_type_code", school.getTypeCode());
//        map.put("school_host_code", school.getHostCode());
//        map.put("is_generally_beneficial", school.getIsGenerallyBeneficial());
//        map.put("is_central", school.getIsCentral());
//
//        boolean success = schoolService.update(new QueryWrapper<School>()
//                .eq("school_code", school.getCode())
//                .allEq(map, false));
//        if (success) {
//            return new ResponseUtil(200, "更新学校信息成功");
//        }
//        return new ResponseUtil(500, "更新学校信息失败");
//    }

    private User getTokenUser(HttpServletRequest request) {
        return JWTUtil.parseJWTUser(request.getHeader(Const.TOKEN_HEADER).substring(Const.STARTS_WITH.length()));
    }

}
