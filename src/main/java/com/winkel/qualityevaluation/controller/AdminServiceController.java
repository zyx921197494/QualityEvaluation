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
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.winkel.qualityevaluation.entity.Location;
import com.winkel.qualityevaluation.entity.School;
import com.winkel.qualityevaluation.entity.User;
import com.winkel.qualityevaluation.entity.task.EvaluateSubmit;
import com.winkel.qualityevaluation.entity.task.EvaluateTask;
import com.winkel.qualityevaluation.service.api.*;
import com.winkel.qualityevaluation.util.Const;
import com.winkel.qualityevaluation.util.JWTUtil;
import com.winkel.qualityevaluation.util.RandomUtil;
import com.winkel.qualityevaluation.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminServiceController {
    @Autowired
    private UserService userService;

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private SubmitService submitService;

    @Autowired
    private Index3Service index3Service;

    @Autowired
    private LocationService locationService;

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

        if (StringUtils.isNotBlank(schoolCode)) wrapper.like("school_code", schoolCode);
        if (StringUtils.isNotBlank(keyName)) wrapper.like("school_name", keyName);
        if (StringUtils.isNotBlank(keyLocation)) wrapper.like("school_location", keyLocation);
        if (StringUtils.isNotBlank(locationCode)) wrapper.like("school_location_code", locationCode);
        if (isCity != null) wrapper.likeRight("school_location_type_code", isCity == 1 ? "1" : "2");

        if (isPublic != null) {
            return schoolService.page(page, isPublic == 1 ? wrapper.ne("school_host_code", "999") : wrapper.eq("school_host_code", "999"));
        }
        return schoolService.page(page, wrapper);
    }

    //7.查看、修改幼儿园信息
    // todo 同步修改其他评估数据
    @PostMapping("/updateSchool")
    public ResponseUtil updateSchool(@RequestBody School school) {
        if (StringUtils.isBlank(school.getCode())) {
            return new ResponseUtil(500, "幼儿园编号不能为空");
        }
        UpdateWrapper<School> wrapper = new UpdateWrapper<School>().eq("school_code", school.getCode());
        if (StringUtils.isNotBlank(school.getName())) wrapper.set("school_name", school.getName());
        if (StringUtils.isNotBlank(school.getLocation())) wrapper.set("school_location", school.getLocation());
        if (StringUtils.isNotBlank(school.getLocationCode()))
            wrapper.set("school_location_code", school.getLocationCode());
        if (StringUtils.isNotBlank(school.getLocationTypeCode()))
            wrapper.set("school_location_type_code", school.getLocationTypeCode());
        if (StringUtils.isNotBlank(school.getTypeCode())) wrapper.set("school_type_code", school.getTypeCode());
        if (StringUtils.isNotBlank(school.getHostCode())) wrapper.set("school_host_code", school.getHostCode());
        if (school.getIsRegister() != null) wrapper.set("is_register", school.getIsRegister());
        if (school.getIsGenerallyBeneficial() != null)
            wrapper.set("is_generally_beneficial", school.getIsGenerallyBeneficial());

        if (schoolService.update(wrapper)) {
            return new ResponseUtil(200, "修改幼儿园信息成功");
        }
        return new ResponseUtil(500, "修改幼儿园信息失败");
    }

    //8.未在册转为在册
    @GetMapping("/registerSchool")
    public ResponseUtil registerSchool(@RequestParam String schoolCode) {
        School school = schoolService.getOne(new QueryWrapper<School>().eq("school_code", schoolCode));
        if (school == null) {
            return new ResponseUtil(500, "对应的幼儿园不存在");
        }
        if (school.getIsLocked() == 1) {
            return new ResponseUtil(500, "幼儿园归属地已发生更改，无法修改");
        }
        if (school.getIsRegister() == 1) {
            return new ResponseUtil(500, "幼儿园已经为在册状态");
        }
        if (schoolService.update(new UpdateWrapper<School>().eq("school_code", schoolCode).set("is_register", 1))) {
            return new ResponseUtil(200, "转在册成功");
        }
        return new ResponseUtil(500, "转在册失败");
    }

    //9.幼儿园归属地修改 school_location_code改为区域代码(5级，最少3级)
    @GetMapping("/changeSchoolLocation")
    public boolean changeSchoolLocation(@RequestParam String schoolCode, @RequestParam String locationCode) {
        return schoolService.update(new UpdateWrapper<School>().eq("school_code", schoolCode).set("school_location_code", locationCode));
    }

    //12.省市县管理员修改本账号密码
    @GetMapping("/changeAdminPassword")
    public ResponseUtil changePassword(HttpServletRequest request, @RequestParam("newPwd") String newPwd) {
        String userId = getTokenUser(request).getId();
        User user = userService.getOne(new QueryWrapper<User>().eq("id", userId).select("password"));
        if (StringUtils.equals(newPwd, user.getPassword())) {
            return new ResponseUtil(500, "旧密码不能和原密码相同");
        }
        if (userService.update(new UpdateWrapper<User>().eq("id", userId).set("password", newPwd))) {
            return new ResponseUtil(200, "修改密码成功");
        }
        return new ResponseUtil(500, "修改密码失败");
    }

    /**
     * desc:
     * params: [schoolCode, authorityId] authorityId：5--9对应五种账户类型
     * return: boolean
     * exception:
     **/
    //12.县级管理员更换幼儿园密码"：选择在册园/未在册园，填写幼儿园名称/标识码进行查询。选中一行中左侧的复选框，选择更换自评、督评、复评(3)密码，密码由系统自动生成
    @GetMapping("/changeUserPassword")
    public ResponseUtil changeUserPassword(@RequestParam String schoolCode, @RequestParam Integer authorityId) {
        School school = schoolService.getOne(new QueryWrapper<School>().eq("school_code", schoolCode));
        Integer currentCycle = taskService.getCurrentCycle(school.getLocationCode().substring(0, 6) + "000000");

        if (userService.changeUserPassword(schoolCode, authorityId, RandomUtil.randomString(8), currentCycle) &&
                userService.changeUserPassword(schoolCode, authorityId + 5, RandomUtil.randomNums(8), currentCycle)) {
            return new ResponseUtil(200, "更换评估密码成功");
        }
        return new ResponseUtil(200, "更换评估密码失败");
    }

    //4.删除幼儿园账号
    //todo 同时删除评估数据：代码删除 或 触发器
    @GetMapping("/deleteUser")
    public ResponseUtil deleteUser(@RequestParam String schoolCode) {
        School school = schoolService.getOne(new QueryWrapper<School>().eq("school_code", schoolCode));
        System.out.println("school = " + school);
        if (school == null || school.getIsLocked() == 1) {
            return new ResponseUtil(500, "幼儿园不存在或已删除");
        }
        String countyCode = school.getLocationCode().substring(0, 6) + "000000";
        Integer currentCycle = taskService.getCurrentCycle(countyCode);
        boolean s1 = schoolService.update(new UpdateWrapper<School>().eq("school_code", schoolCode).set("is_locked", 1));  // 锁定幼儿园记录
        boolean s2 = userService.update(new UpdateWrapper<User>().eq("school_code", schoolCode).eq("cycle", currentCycle).set("is_locked", 1));  // 锁定当前周期的评估账号
        boolean s3 = taskService.remove(new QueryWrapper<EvaluateTask>().eq("school_code", schoolCode).eq("task_cycle", currentCycle));  // 删除幼儿园当前周期的所有评估任务、记录、材料
        if (s1 == s2 == s3) {
            return new ResponseUtil(200, "删除幼儿园账号成功");
        }
        return new ResponseUtil(500, "删除幼儿园账号失败");
    }

    //县内所有幼儿园的评估完成后，市级管理员选择县，启动一个新的评估周期
    //冻结以往周期所有的督评、复评数据
    //督评、复评账号随周期更换
    @GetMapping("/startCycle")
    public ResponseUtil startCycle(@RequestParam String locationCode) {
        // 校验县下属的学校是否全部完成评估；冻结过往周期数据；冻结账号
        Integer cycle = taskService.getCurrentCycle(locationCode);
        List<School> schoolList = schoolService.list(new QueryWrapper<School>().likeRight("school_location_code", locationCode.substring(0, 6)));
        for (School school : schoolList) {
            if (school.getIsLocked() == 1) {  // 删除的幼儿园不参与新周期的评估
                continue;
            }
            List<EvaluateTask> tasks = taskService.list(new QueryWrapper<EvaluateTask>().eq("school_code", school.getCode()).eq("task_cycle", cycle));
            for (EvaluateTask task : tasks) {
                if (!Objects.equals(task.getStatus(), Const.TASK_REPORT_ACCEPTED)) {
                    return new ResponseUtil(500, "县域内有幼儿园未完成全部评估任务");
                }
            }
        }

        // 生成评价任务
        boolean[] success = new boolean[5];
        ArrayList<EvaluateTask> taskList = new ArrayList<>(schoolList.size());
        Integer currentCycle = taskService.getCurrentCycle(schoolList.get(0).getLocationCode().substring(0, 6) + "000000") + 1;
        for (School school : schoolList) {
            taskList.add(new EvaluateTask()
                    .setSchoolCode(school.getCode())
                    .setEvaluateId(1)
                    .setName("自评")
                    .setContent(school.getName() + "第 " + currentCycle + " 周期教学质量评估")
                    .setCycle(currentCycle)
                    .setStatus(Const.TASK_NOT_START)
                    .setType(1)
                    .setIsLocked(0));
        }
        success[0] = taskService.saveBatch(taskList);

        for (int i = 2; i < 6; i++) {
            String name;
            if (i == 2) name = "督评";
            else if (i == 3) name = "县复评";
            else if (i == 4) name = "市复评";
            else name = "省复评";
            for (EvaluateTask task : taskList) {
                task.setType(i);
                task.setName(name);
            }
            success[i - 1] = taskService.saveBatch(taskList);
        }
        // 解锁县域内所有学校的自评账号，因为其不随周期变化而删除
        boolean unlock = userService.unlockSelfUserByLocationCode(locationCode);

        //开启新周期
        if (success[0] == success[1] == success[2] == success[3] == success[4] && taskService.startCycle(locationCode) && unlock) {
            log.info("启动 {} 幼儿园第 {} 周期的教学质量评估", locationCode, currentCycle);
            return new ResponseUtil(200, "成功启动 " + locationService.getOne(new QueryWrapper<Location>().eq("code", locationCode)).getName() + " 第 " + currentCycle + " 周期的教学质量评估");
        }
        return new ResponseUtil(500, "开启新评估周期失败");
    }


    /**
     * @desc:
     * params:
     * @param schoolCodeList 重启评估的类型(5--9)：如重启自评
     * @param type 幼儿园标识编码List
     * @return: com.winkel.qualityevaluation.util.ResponseUtil
     * @exception:
     **/
    @PostMapping("/resetEvaluation")
    public ResponseUtil resetEvaluation(@RequestBody List<String> schoolCodeList, @RequestParam("type") Integer type) {
        for (String schoolCode : schoolCodeList) {
            School school = schoolService.getOne(new QueryWrapper<School>().eq("school_code", schoolCode));
            Integer cycle = taskService.getCurrentCycle(school.getLocationCode().substring(0, 6) + "000000");
            EvaluateTask task = taskService.getOne(new QueryWrapper<EvaluateTask>().eq("school_code", schoolCode).eq("task_cycle", cycle).eq("task_type", type - 4));

            if (Objects.equals(task.getStatus(), Const.TASK_NOT_START) || task.getStatus() > Const.TASK_REPORT_SUBMITTED) {
                return new ResponseUtil(403, "不能重启未开始或已通过的评估");
            }

            if (!(userService.unlockUserBySchoolCode(schoolCode, type) && taskService.update(new UpdateWrapper<EvaluateTask>().eq("evaluate_task_id", task.getId()).set("task_status", Const.TASK_IN_EVALUATION)))) {
                return new ResponseUtil(500, "重启评估账号时错误");
            }
        }
        return new ResponseUtil(200, "重启评估成功");
    }

    //导出各级用户


    //定义评价任务

    //定义一级评价指标

    //定义耳机评价指标

    //定义三级评价指标及对应各选项的分数


    //审核省市县的复评意见书

    //导出自评、督评、复评账号
    private User getTokenUser(HttpServletRequest request) {
        return JWTUtil.parseJWTUser(request.getHeader(Const.TOKEN_HEADER).substring(Const.STARTS_WITH.length()));
    }


    @Test
    public void test() {
        System.out.println(taskService == null);
        System.out.println(taskService.getCurrentCycle("3100000000") + 1);
    }


}
