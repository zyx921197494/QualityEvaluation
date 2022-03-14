package com.winkel.qualityevaluation.controller;
/*
  @ClassName AdminServiceController
  @Description
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
@RequestMapping("/admin")
public class AdminServiceController {

    @Autowired
    private UserService userService;

    @GetMapping("/ccc")
    public User ddd() {
        QueryWrapper queryWrapper = new QueryWrapper<User>().select("username", "password", "is_locked").eq("username", "user");
        User user = userService.getOne(queryWrapper);
        return user;
    }
}
