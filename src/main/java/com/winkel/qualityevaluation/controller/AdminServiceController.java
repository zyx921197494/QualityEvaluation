package com.winkel.qualityevaluation.controller;
/*
  @ClassName AdminServiceController
  @Description
  @Author winkel
  @Date 2022-03-14 14:43
  */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.winkel.qualityevaluation.entity.School;
import com.winkel.qualityevaluation.entity.User;
import com.winkel.qualityevaluation.service.api.SchoolService;
import com.winkel.qualityevaluation.service.api.UserService;
import com.winkel.qualityevaluation.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminServiceController {

    @Autowired
    private UserService userService;

    @Autowired
    private SchoolService schoolService;

    @GetMapping("/ccc")
    public User ddd() {
        QueryWrapper queryWrapper = new QueryWrapper<User>().select("username", "password", "is_locked").eq("username", "user");
        User user = userService.getOne(queryWrapper);
        return user;
    }

    @PostMapping("/createAdmins")
    public ResponseUtil createAdmins() {
        if (userService.createAdmins()) {
            return ResponseUtil.response(200, "创建省市县级管理员成功", null);
        } else return ResponseUtil.response(200, "创建省市县级管理员失败", null);
    }


    //TODO 上传Excel文件并解析为school对象
    //TODO 创建用户后为其添加权限
    @PostMapping("/createRegisterUsers")
    public ResponseUtil createRegisterUsers(@RequestBody List<School> schoolList, @RequestParam("authorityType") Integer authorityType) {
        //先查询是否存在幼儿园(以标识码为准)，存在则更新，不存在则增加幼儿园
        if (schoolService.saveOrUpdateBatch(schoolList)) {
            //为每个幼儿园创建用户
            if (userService.createRegisterUsers(schoolList, authorityType)) {
                return ResponseUtil.response(200, "创建在册园账号成功", null);
            }
            return ResponseUtil.response(200, "创建在册园账号失败", null);
        }
        return ResponseUtil.response(200, "请求json：schoolList有误，创建在册园账号失败", null);
    }


    @PostMapping("/createNotRegisterUsers")
    public ResponseUtil createNotRegisterUsers(String locationCode, int num) {
        if (userService.createNotRegisterUsers(locationCode, num)) {
            return ResponseUtil.response(200, "创建未在册园账号成功", null);
        }
        return ResponseUtil.response(200, "创建未在册园账号失败", null);
    }

    //6.按条件搜索幼儿园
//    @PostMapping("/schools")
//    public List<School> schools() {
//
//    }

    //7.查看、修改幼儿园信息

    //8.未在册转为在册

    //9.幼儿园归属地修改

    //12.省市县管理员修改本账号密码

    //12.县级管理员更换幼儿园密码：选择在册园/未在册园，填写幼儿园名称/标识码进行查询。选中一行中左侧的复选框，选择更换自评、督评、复评密码，密码由系统自动生成

    //4.删除幼儿园账号


}
