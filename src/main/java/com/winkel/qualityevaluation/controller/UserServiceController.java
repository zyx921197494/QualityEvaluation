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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/evaluate")
public class UserServiceController {

    @Autowired
    private UserService userService;

    @GetMapping("/aaa")
    public User aaa() {
        QueryWrapper queryWrapper = new QueryWrapper<User>().eq("username", "user").select("username", "password", "is_locked");
        User user = userService.getOne(queryWrapper);
        return user;
    }
}
