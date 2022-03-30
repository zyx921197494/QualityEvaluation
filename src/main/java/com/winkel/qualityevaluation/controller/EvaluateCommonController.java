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
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex1;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex2;
import com.winkel.qualityevaluation.entity.task.EvaluateSubmitFile;
import com.winkel.qualityevaluation.service.api.*;
import com.winkel.qualityevaluation.util.*;
import com.winkel.qualityevaluation.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/evaluate/common")
public class EvaluateCommonController {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

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
     * desc: 点击用户信息栏，更新用户
     * params: [userVo]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @PostMapping("/updateUserInfo")
    public ResponseUtil updateUser(@RequestBody UserVo userVo) {
        if (userService.update(new UpdateWrapper<User>()
                .eq("id", userVo.getId())
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
     * desc: 通用下载文件接口。默认保存在 C:\Users\Public\Downloads\ 路径下
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
        } else {
            if (ossUtil.downloadSimple(filename, null)) {
                log.info("直接下载文件 {}", filename);
                return new ResponseUtil(200, "下载成功");
            }

        }
        return new ResponseUtil(500, "下载失败");
    }

    /**
     * desc: 发送验证码
     * params: [email]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/

    @GetMapping("/sendEmail")
    public ResponseUtil sendEmail(@RequestParam("email") String email) {
        if (mailUtil.sendEmail(email)) {
            return new ResponseUtil(200, "邮件发送成功");
        }
        return new ResponseUtil(500, "邮件发送失败，请重试");
    }

    private User getTokenUser(HttpServletRequest request) {
        return JWTUtil.parseJWTUser(request.getHeader(Const.TOKEN_HEADER).substring(Const.STARTS_WITH.length()));
    }

}
