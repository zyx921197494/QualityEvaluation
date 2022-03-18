package com.winkel.qualityevaluation.controller;
/*
  @ClassName UserServiceController
  @Description 幼儿园评估账户
  @Author winkel
  @Date 2022-03-14 14:43
  */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.winkel.qualityevaluation.entity.User;
import com.winkel.qualityevaluation.service.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/evaluate")
public class UserServiceController {

    @Autowired
    private UserService userService;

    @GetMapping("/aaa")
    public User aaa() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>().eq("username", "user").select("username", "password", "is_locked");
        return userService.getOne(queryWrapper);
    }


    //园长提交幼儿园信息

    //园长提交个人信息

    //园长开启自评

    //园长/教师提交评估问题答案

    //园长提交评估数据

    //园长上传文件

    //园长下载文件



    //督评组长提交幼儿园信息

    //督评组长提交个人信息

    //督评组长开启督评

    //督评组长/督评人员提交评估问题答案

    //督评组长提交评估数据

    //督评组长上传文件

    //督评组长下载文件



    //开启复评



}
