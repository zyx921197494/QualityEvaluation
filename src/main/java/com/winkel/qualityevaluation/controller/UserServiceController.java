package com.winkel.qualityevaluation.controller;
/*
  @ClassName UserServiceController
  @Description 幼儿园评估账户
  @Author winkel
  @Date 2022-03-14 14:43
  */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.winkel.qualityevaluation.entity.User;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex1;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex2;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex3;
import com.winkel.qualityevaluation.service.api.Index1Service;
import com.winkel.qualityevaluation.service.api.Index2Service;
import com.winkel.qualityevaluation.service.api.Index3Service;
import com.winkel.qualityevaluation.service.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/evaluate")
public class UserServiceController {

    @Autowired
    private UserService userService;

    @Autowired
    private Index1Service index1Service;

    @Autowired
    private Index2Service index2Service;

    @Autowired
    private Index3Service index3Service;

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


    //园长提交幼儿园信息(更新school)

    //园长提交个人信息(更新user)

    //园长开启自评(更新task)

    //园长/教师提交评估问题答案(根据index3和selected(接受一个VO(taskId, index3Id, selected)计算得分，插入submit)

    //园长提交评估数据(修改task状态，锁定submit，锁定教师user)

    //园长上传文件(提交评估数据之后才能进行，修改task状态)

    //园长下载文件


    //督评组长提交幼儿园信息

    //督评组长提交个人信息

    //督评组长开启督评

    //督评组长/督评人员提交评估问题答案

    //督评组长提交评估数据

    //督评组长上传文件(提交评估数据之后才能进行)

    //督评组长下载文件


    //开启复评


}
