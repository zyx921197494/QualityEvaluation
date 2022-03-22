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
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.winkel.qualityevaluation.entity.School;
import com.winkel.qualityevaluation.entity.User;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex3;
import com.winkel.qualityevaluation.entity.task.EvaluateSubmit;
import com.winkel.qualityevaluation.entity.task.EvaluateTask;
import com.winkel.qualityevaluation.service.api.*;
import com.winkel.qualityevaluation.util.ResponseUtil;
import com.winkel.qualityevaluation.vo.SubmitVo;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
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
        return userService.update(new UpdateWrapper<User>().eq("id", userId).set("password", newPwd));
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

    //县内所有幼儿园的评估完成后，市级管理员选择县，启动一个新的评估周期
    //冻结以往周期所有的督评、复评数据
    //督评、复评账号随周期更换
    @GetMapping("/startCycle")
    public boolean startCycle(@RequestParam String locationCode) {
        //todo 校验县下属的学校是否全部完成评估；冻结过往周期数据；冻结账号
        boolean[] success = new boolean[5];
        List<School> schoolList = schoolService.list(new QueryWrapper<School>().eq("school_location_code", locationCode));
        ArrayList<EvaluateTask> taskList = new ArrayList<>(schoolList.size());
        for (School school : schoolList) {
            System.out.println(school.getLocationCode());
            Integer currentCycle = taskService.getCurrentCycle(school.getLocationCode().substring(0, 6) + "000000") + 1;
            taskList.add(new EvaluateTask()
                    .setSchoolCOde(school.getCode())
                    .setEvaluateId(1)
                    .setName("自评")
                    .setContent(school.getName() + "第 " + currentCycle + " 周期教学质量评估")
                    .setCycle(currentCycle)
                    .setStatus(0)
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
        //开启新周期
        if (success[0] == success[1] == success[2] == success[3] == success[4]) {
            return taskService.startCycle(locationCode);
        } else {
            return false;
        }
    }

    //提交评估问题及附件 todo 附件
    @PostMapping("/submitEvaluation")
    public boolean submitEvaluation(@RequestBody List<SubmitVo> submitVos) {  // , @RequestParam("files")MultipartFile[] files
        List<EvaluateSubmit> list = new ArrayList<>(submitVos.size());

        for (SubmitVo submitVo : submitVos) {
            int score = 0;
            int type = submitVo.getType();
            String content = submitVo.getContent();
            if (type == 1 && "A".equalsIgnoreCase(content)) {
                score = 20;
            } else if (type == 2) {
                if ("B".equalsIgnoreCase(content)) score = 10;
                else if ("C".equalsIgnoreCase(content)) score = 20;
                else if ("D".equalsIgnoreCase(content)) score = 30;
            } else if (type == 3) {
                score = submitVo.getContent().length() * 10;
            }
            list.add(new EvaluateSubmit()
                    .setTaskId(submitVo.getTaskId())
                    .setIndex3Id(submitVo.getIndex3Id())
                    .setContent(content)
                    .setScore(score)
                    .setSubmitTime(LocalDateTime.now())
                    .setIsLocked(0));
        }

        return submitService.saveBatch(list);
    }



    //定义评价任务

    //定义一级评价指标

    //定义耳机评价指标

    //定义三级评价指标及对应各选项的分数


    //导出评估数据

    //重启评估(解锁submit，修改对应task status)
    @PostMapping("/resetEvaluation")
    public void resetEvaluation(@RequestParam String evaluateSubmitId) {

    }

    //审核省市县的复评意见书

    //导出自评、督评、复评账号



    @Test
    public void test() {
        System.out.println(taskService==null);
        System.out.println(taskService.getCurrentCycle("3100000000") + 1);
    }


}
