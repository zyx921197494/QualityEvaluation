package com.winkel.qualityevaluation.controller;
/*
  @ClassName AdminServiceController
  @Description
  @Author winkel
  @Date 2022-03-14 14:43
  */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.winkel.qualityevaluation.entity.School;
import com.winkel.qualityevaluation.entity.User;
import com.winkel.qualityevaluation.service.api.SchoolService;
import com.winkel.qualityevaluation.service.api.UserService;
import com.winkel.qualityevaluation.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminServiceController {

    @Autowired
    private UserService userService;

    @Autowired
    private SchoolService schoolService;

    @GetMapping("/ccc")
    public User ddd() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>().select("username", "password", "is_locked").eq("username", "user");
        return userService.getOne(queryWrapper);
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
    @PostMapping("/schools")
    public IPage<School> schools(String schoolCode, String keyName, String keyLocation, String locationCode, Integer isCity, Integer isPublic, Integer isRegister, Integer isGB, @RequestParam("current") Integer current, @RequestParam("pageSize") Integer pageSize) {
        IPage<School> page = new Page<>(current, pageSize);

        Map<String, Object> queryMap = new HashMap<>(2);
        queryMap.put("is_register", isRegister);
        queryMap.put("is_generally_beneficial", isGB);
        QueryWrapper<School> wrapper = new QueryWrapper<School>().allEq(queryMap, false);
        wrapper
                .like("school_name", keyName)
                .like("school_location", keyLocation)
                .like("school_location_code", locationCode)
                .likeRight("school_location_type_code", isCity != null && isCity == 1 ? 1 : 2);
        if (schoolCode != null) {
            wrapper.like("school_code", schoolCode);
        }
        return schoolService.page(page, isPublic != null && isPublic == 1 ? wrapper.ne("school_host_code", 999) : wrapper.eq("school_host_code", 999));
    }

    //7.查看、修改幼儿园信息
    // todo 同步修改其他评估数据
    @PostMapping("/updateSchool")
    public boolean updateSchool(@RequestParam String schoolCode, String name, String location, String locationCode, String locationTypeCode, String typeCode, String hostCode, Integer isRegister, Integer isGB) {
//        Map<String, Object> map = new HashMap<>(9);
        UpdateWrapper<School> wrapper = new UpdateWrapper<School>().eq("school_code", schoolCode);
        School school = new School()
                .setCode(schoolCode)
                .setName(name)
                .setLocation(location)
                .setLocationCode(locationCode)
                .setLocationTypeCode(locationTypeCode)
                .setTypeCode(typeCode)
                .setHostCode(hostCode)
                .setIsRegister(isRegister)
                .setIsGenerallyBeneficial(isGB);
        return schoolService.update(school, wrapper);
    }

    //8.未在册转为在册
    @PostMapping("/registerSchool")
    public boolean registerSchool(@RequestParam String schoolCode) {
        return schoolService.update(new UpdateWrapper<School>().eq("school_code", schoolCode).set("is_register", 1));
    }

    //9.幼儿园归属地修改 school_location_code前6位改为新县代码
    @PostMapping("/changeSchoolLocation")
    public boolean changeSchoolLocation(@RequestParam String schoolCode, @RequestParam String locationCode) {
        String oldCode = schoolService.getOne(new QueryWrapper<School>().eq("school_code", schoolCode)).getLocationCode();
        String newCode = locationCode.substring(0, 6) + oldCode.substring(6, 12);
        return schoolService.update(new UpdateWrapper<School>().eq("school_code", schoolCode).set("school_location_code", newCode));
    }

    //12.省市县管理员修改本账号密码
    @PostMapping("/changeAdminPassword")
    public boolean changePassword(@RequestParam String userId, @RequestParam String newPwd) {
        return userService.update(new UpdateWrapper<User>().eq("id",userId).set("password", newPwd));
    }

    //12.县级管理员更换幼儿园密码"：选择在册园/未在册园，填写幼儿园名称/标识码进行查询。选中一行中左侧的复选框，选择更换自评、督评、复评密码，密码由系统自动生成
    @PostMapping("/changeUserPassword")
    public boolean changeUserPassword(@RequestParam String schoolCode, @RequestParam Integer authorityId, @RequestParam String newPwd) {
        return userService.changeUserPassword(schoolCode, authorityId, newPwd);
    }

    //4.删除幼儿园账号
    //todo 同时删除评估数据：代码删除 或 触发器
    @PostMapping("/deleteUser")
    public boolean deleteUser(@RequestParam String schoolCode) {
        return userService.remove(new QueryWrapper<User>().eq("school_code", schoolCode));
    }





    //定义评价任务

    //定义一级评价指标

    //定义耳机评价指标

    //定义三级评价指标及对应各选项的分数


    //县内所有幼儿园的评估完成后，市级管理员选择县，启动一个新的评估周期
    //冻结以往周期所有的督评、复评数据
    //督评、复评账号随周期更换

    //导出评估数据

    //重启评估(解锁submit，修改对应task status)
    @PostMapping("/resetEvaluation")
    public void resetEvaluation(@RequestParam String evaluateSubmitId) {

    }

    //审核省市县的复评意见书

    //导出自评、督评、复评账号




}
