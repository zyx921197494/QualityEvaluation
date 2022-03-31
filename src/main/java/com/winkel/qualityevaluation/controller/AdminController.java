package com.winkel.qualityevaluation.controller;
/*
  @ClassName AdminServiceController
  @Description
  @Author winkel
  @Date 2022-03-14 14:43
  */

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.winkel.qualityevaluation.entity.*;
import com.winkel.qualityevaluation.entity.task.EvaluateReportFile;
import com.winkel.qualityevaluation.entity.task.EvaluateTask;
import com.winkel.qualityevaluation.service.api.*;
import com.winkel.qualityevaluation.util.*;
import com.winkel.qualityevaluation.vo.AccountVo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private ReportFileService reportFileService;

    @Autowired
    private LocationReportService locationReportService;

    @Autowired
    private OssUtil ossUtil;

    /**
     * desc: 县级管理员获取当前周期
     * params: [request]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @RequestMapping("/getCurrentCycle")
    public ResponseUtil getCurrentCycle(HttpServletRequest request) {
        String locationCode = userService.getOne(new QueryWrapper<User>().eq("id", getTokenUser(request).getId()).select("location_code")).getLocationCode();
        return new ResponseUtil(200, "查询当前周期", taskService.getCurrentCycle(locationCode));
    }


    /**
     * desc: 为每个省市县创建一个有对应权限的管理员
     * params: []
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @PostMapping("/createAdmins")
    public ResponseUtil createAdmins() {
        if (userService.createAdmins()) {
            return ResponseUtil.response(200, "创建省市县级管理员成功");
        } else return ResponseUtil.response(200, "创建省市县级管理员失败");
    }


    /**
     * desc: 生成在册幼儿园自评、督评评估账号
     * params: [schoolList, authorityType]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @SneakyThrows
    @PostMapping("/createRegisterUsers")
    public ResponseUtil createRegisterUsers(@RequestParam("file") MultipartFile file) {
        if (!StringUtils.equals(file.getOriginalFilename(), "园所信息表.xlsx")) {
            return ResponseUtil.response(500, "上传文件名必须为 园所信息表.xlsx");
        }
        // 读取Excel
        List<School> schools = ExcelUtil.readListFromExcel(file, "Sheet1", School.class);
        if (schools == null || schools.isEmpty()) {
            return ResponseUtil.response(500, "Excel为空，请填写后上传");
        }
        //先查询Excel中幼儿园是否与数据库内一致
        for (School school : schools) {
            School dbSchool = schoolService.getOne(new QueryWrapper<>(school));
            if (dbSchool == null) {
                return new ResponseUtil(500, "数据匹配失败：学校标识码 " + school.getCode() + " ，请校验后重新提交");
            }
        }
        //为每个幼儿园创建用户
        userService.createRegisterUsers(schools, Const.ROLE_EVALUATE_SELF);  // 自评
        userService.createRegisterUsers(schools, Const.ROLE_EVALUATE_SUPERVISOR);  // 督评
//        userService.createRegisterUsers(schools, Const.ROLE_EVALUATE_COUNTY);  // 复评
        return ResponseUtil.response(200, "创建在册园评估账号成功");
    }


//    /**
//     * desc: 省市级管理员选择到县，县级管理员无需选择。创建未在册学校的各级评估账户
//     * params: [locationCode, num]
//     * return: com.winkel.qualityevaluation.util.ResponseUtil
//     * exception:
//     **/
//    @PostMapping("/createNotRegisterUsers")
//    public ResponseUtil createNotRegisterUsers(String countyCode, int num) {
//        if (userService.createNotRegisterUsers(countyCode, num, Const.ROLE_EVALUATE_SELF)
//                && userService.createNotRegisterUsers(countyCode, num, Const.ROLE_EVALUATE_SUPERVISOR)) {
//            return ResponseUtil.response(200, "创建未在册园账号成功");
//        }
//        return ResponseUtil.response(500, "创建未在册园评估账号失败");
//    }


    /**
     * desc: 导出自评、督评账号
     * params: [schools, userType] schools：学校的标识码列表userType：自评(5、10)、督评(6、11)
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @SneakyThrows
    @GetMapping("/exportUser")
    public ResponseUtil exportUser(@RequestBody List<String> schoolCodes, Integer authorityId) {
        for (String schoolCode : schoolCodes) {
            String schoolName = schoolService.getOne(new QueryWrapper<School>().eq("school_code", schoolCode)).getName();
            List<AccountVo> accounts = userService.getAccountBySchoolCodeAndAuthorityType(schoolCode, authorityId);
            String path = "C:\\Users\\Public\\Downloads\\";
            File file = new File(path + schoolCode + schoolName + ".txt");
            file.createNewFile();
            FileWriter fw = new FileWriter(file, false);
            PrintWriter pw = new PrintWriter(fw);
            pw.println("用户名              密码              用户类型");

            for (AccountVo account : accounts) {
                if (account.getAuthorityId() == 5) {
                    account.setAccountType("自评");
                } else if (account.getAuthorityId() == 6) {
                    account.setAccountType("督评");
                } else if (account.getAuthorityId() == 10) {
                    account.setAccountType("自评组长");
                } else if (account.getAuthorityId() == 11) {
                    account.setAccountType("督评组长");
                }
                pw.println(account.getUsername() + "          " + account.getPassword() + "          " + account.getAccountType());
            }
            fw.close();
            pw.close();
        }

        return new ResponseUtil(200, "导出账号成功");
    }


    /**
     * desc: 按条件搜索幼儿园：学校标识码、名称关键字、位置关键字、地址码、城市/农村、是否公办、是否在册、是否普惠
     * params: [schoolCode, keyName, keyLocation, locationCode, isCity, isPublic, isRegister, isGB, current, pageSize] current, pageSize：当前页、每页数量
     * return: com.baomidou.mybatisplus.core.metadata.IPage<com.winkel.qualityevaluation.entity.School>
     * exception:
     **/
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


    /**
     * desc: 修改幼儿园信息
     * params: [school]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
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


    /**
     * desc: 未在册幼儿园转为在册
     * params: [schoolCode]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
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


    /**
     * desc: 幼儿园归属地修改 school_location_code改为区域代码(5级，最少3级)
     * params: [schoolCode, locationCode]
     * return: boolean
     * exception:
     **/
    @GetMapping("/changeSchoolLocation")
    public boolean changeSchoolLocation(@RequestParam String schoolCode, @RequestParam String locationCode) {
        return schoolService.update(new UpdateWrapper<School>().eq("school_code", schoolCode).set("school_location_code", locationCode));
    }


    /**
     * desc: 省市县管理员修改本账号密码
     * params: [request, newPwd]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
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
     * desc: 县级管理员更换幼儿园密码"：选择在册园/未在册园，填写幼儿园名称/标识码进行查询。选中一行中左侧的复选框，选择更换自评、督评、复评(3)密码，密码由系统自动生成
     * params: [schoolCode, authorityId] authorityId：5--9对应五种账户类型
     * return: boolean
     * exception:
     **/
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


    /**
     * desc: 删除幼儿园账号
     * params: [schoolCode]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    //todo 同时删除评估数据：代码删除 或 触发器
    @GetMapping("/deleteUser")
    public ResponseUtil deleteUser(@RequestParam String schoolCode) {
        School school = schoolService.getOne(new QueryWrapper<School>().eq("school_code", schoolCode));
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


    /**
     * desc: 县内所有幼儿园的评估完成后，市级管理员选择县，启动一个新的评估周期
     * 冻结以往周期所有的督评、复评数据
     *       todo 校验是否督评、复评账号随周期更换
     * params: [locationCode]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
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
     * @param schoolCodeList 重启评估的类型(5--9)：如重启自评
     * @param type           幼儿园标识编码List
     * @desc: params:
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


    /**
     * desc: 各级管理员查看所属的督评、复评报告
     * params: [request]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @GetMapping("/getFinishedReport")
    public ResponseUtil getCountyReport(HttpServletRequest request) {
        List<Authority> authorities = userService.getAuthorities(getTokenUser(request).getUsername());
        switch (authorities.get(0).getAuthority()) {
            case "ROLE_ADMIN_COUNTY": {
                List<Integer> ids = taskService.getFinishTaskIdByCountyAdminId(getTokenUser(request).getId());
                Collection<EvaluateReportFile> reportFiles = reportFileService.listByIds(ids);
                if (reportFiles.isEmpty()) {
                    return new ResponseUtil(200, "没有待审核的督评或县复评报告");
                }
                return new ResponseUtil(200, "查询督评或县复评报告成功", reportFiles);
            }
            case "ROLE_ADMIN_CITY": {
                List<Integer> ids = taskService.getFinishTaskIdByCityAdminId(getTokenUser(request).getId());
                Collection<EvaluateReportFile> reportFiles = reportFileService.listByIds(ids);
                if (reportFiles.isEmpty()) {
                    return new ResponseUtil(200, "没有待审核的督评或县复评报告");
                }
                return new ResponseUtil(200, "查询市复评报告成功", reportFiles);
            }
            case "ROLE_ADMIN_PROVINCE": {
                List<Integer> ids = taskService.getFinishTaskIdByProvinceAdminId(getTokenUser(request).getId());
                Collection<EvaluateReportFile> reportFiles = reportFileService.listByIds(ids);
                if (reportFiles.isEmpty()) {
                    return new ResponseUtil(200, "没有待审核的督评或县复评报告");
                }
                return new ResponseUtil(200, "查询省复评报告成功", reportFiles);
            }
        }
        return new ResponseUtil(200, "查询待审核报告失败");
    }


    /**
     * desc: 审核各级评估意见书
     * params: [request, isAccept] isAccept：0为拒绝、1为通过
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @GetMapping("/auditReport")
    public ResponseUtil auditReport(@RequestParam Integer reportFileId, @RequestParam Integer isAccept) {
        Integer taskId = reportFileService.getOne(new QueryWrapper<EvaluateReportFile>().eq("report_file_id", reportFileId).select("task_id")).getTaskId();
        boolean update = taskService.update(new UpdateWrapper<EvaluateTask>()
                .eq("evaluate_task_id", taskId)
                .set("task_status", isAccept == 1 ? Const.TASK_REPORT_ACCEPTED : Const.TASK_REPORT_REFUSED));
        if (update) {
            return new ResponseUtil(200, "审核报告成功");
        }
        return new ResponseUtil(500, "审核报告失败");
    }


    /**
     * desc: 上传区域报告
     * params: [request, year, file] year：本地区哪一年的报告
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @PostMapping("/uploadLocationReport")
    public ResponseUtil uploadLocationReport(HttpServletRequest request, @RequestParam Integer year, @RequestParam("file") MultipartFile file) {
        String locationCode = userService.getOne(new QueryWrapper<User>().eq("id", getTokenUser(request).getId()).select("location_code")).getLocationCode();
        LocationReport report = locationReportService.getOne(new QueryWrapper<LocationReport>().eq("location_code", locationCode).eq("year", year));
        boolean reUpload = false;  // 是否是覆盖区域报告
        String oldFilePath = null;
        if (report != null) {
            reUpload = true;
            oldFilePath = report.getFilePath();  // 旧报告地址，更新后删除旧的报告文件
        }
        UploadResult result = ossUtil.upload(file);
        if (!result.getStatus().equals(UploadStatus.DONE.getStatus())) {
            return new ResponseUtil(500, "上传报告文件至OSS时失败");
        }
        boolean success;
        if (reUpload) {
            success = locationReportService.update(new UpdateWrapper<LocationReport>()
                    .eq("year", report.getYear())
                    .eq("location_code", locationCode)
                    .set("file_name", file.getOriginalFilename())
                    .set("file_path", result.getUrl())
                    .set("upload_time", LocalDateTime.now()));
        } else {
            success = locationReportService.save(new LocationReport()
                    .setYear(year)
                    .setLocationCode(locationCode)
                    .setLocationName(locationService.getOne(new QueryWrapper<Location>().eq("code", locationCode).select("name")).getName())
                    .setFileName(file.getOriginalFilename())
                    .setFilePath(result.getUrl())
                    .setUploadTime(LocalDateTime.now()));
        }
        if (!success) {
            return new ResponseUtil(500, "修改数据库时出错");
        }
        if (reUpload && !ossUtil.deleteFile(oldFilePath.substring(43))) {
            return new ResponseUtil(500, "删除旧报告时出错");
        }
        return new ResponseUtil(200, reUpload ? "覆盖" + year + "年区域报告成功" : "上传" + year + "年区域报告成功");
    }

    /**
     * desc: 查看本区域内各年份的区域报告
     * params: [request]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @GetMapping("/listLocationReport")
    public ResponseUtil listLocationReport(HttpServletRequest request) {
        String locationCode = userService.getOne(new QueryWrapper<User>().eq("id", getTokenUser(request).getId()).select("location_code")).getLocationCode();
        Integer role = getAdminRole(request);
        List<LocationReport> resultList = new ArrayList<>();

        if (role.equals(Const.ROLE_ADMIN_COUNTY)) {
            resultList = locationReportService.list(new QueryWrapper<LocationReport>().eq("location_code", locationCode));
        } else if (role.equals(Const.ROLE_ADMIN_CITY)) {
            resultList.addAll(locationReportService.list(new QueryWrapper<LocationReport>().eq("location_code", locationCode)));  //市级报告
            List<Location> locations = locationService.list(new QueryWrapper<Location>().eq("p_code", locationCode));
            for (Location location : locations) {
                List<LocationReport> reports = locationReportService.list(new QueryWrapper<LocationReport>().eq("location_code", location.getCode()));
                if (!reports.isEmpty()) {
                    resultList.addAll(reports);
                }
            }
        } else {
            resultList.addAll(locationReportService.list(new QueryWrapper<LocationReport>().eq("location_code", locationCode))); // 省级报告
            List<Location> cities = locationService.list(new QueryWrapper<Location>().eq("p_code", locationCode));
            for (Location city : cities) {
                resultList.addAll(locationReportService.list(new QueryWrapper<LocationReport>().eq("location_code", city.getCode())));  // 市级报告
                List<Location> counties = locationService.list(new QueryWrapper<Location>().eq("p_code", city.getCode()));
                for (Location county : counties) {
                    List<LocationReport> reports = locationReportService.list(new QueryWrapper<LocationReport>().eq("location_code", county.getCode()));
                    if (!reports.isEmpty()) {
                        resultList.addAll(reports);
                    }
                }
            }
        }

        if (resultList.isEmpty()) {
            return new ResponseUtil(200, "当前辖区内未上传任何区域报告");
        }
        return new ResponseUtil(200, "查询成功", resultList);
    }


    /**
     * desc: 根据区域报告idList批量删除区域报告
     * params: [ids] List<Integer>
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @PostMapping("/deleteLocationReport")
    public ResponseUtil deleteLocationReport(@RequestBody List<Integer> ids) {
        ArrayList<String> pathList = new ArrayList<>();
        for (Integer id : ids) {
            LocationReport report = locationReportService.getById(id);
            if (report == null) {
                return new ResponseUtil(500, "找不到id对应的区域报告，请检查参数");
            }
            pathList.add(report.getFilePath().substring(43));
        }
        if (locationReportService.removeByIds(ids)) {
            boolean delete = true;
            for (String path : pathList) {
                if (!ossUtil.deleteFile(path)) {
                    delete = false;
                }
            }
            if (!delete) {
                return new ResponseUtil(200, "删除数据库成功，删除服务器文件出错，请刷新后重试");
            }
            return new ResponseUtil(200, "删除成功");
        }
        return new ResponseUtil(500, "删除失败");
    }


    private User getTokenUser(HttpServletRequest request) {
        return JWTUtil.parseJWTUser(request.getHeader(Const.TOKEN_HEADER).substring(Const.STARTS_WITH.length()));
    }

    private Integer getAdminRole(HttpServletRequest request) {
        List<Authority> authorities = userService.getAuthorities(getTokenUser(request).getUsername());
        switch (authorities.get(0).getAuthority()) {
            case "ROLE_ADMIN_COUNTY": {
                return 1;
            }
            case "ROLE_ADMIN_CITY": {
                return 2;
            }
            case "ROLE_ADMIN_PROVINCE": {
                return 3;
            }
        }
        return 0;
    }

    @Test
    public void test() {
        ArrayList<User> users = new ArrayList<>();
        User user = new User().setUsername("111");
        users.add(user);
        user.setUsername("222");
        users.add(user);
        System.out.println(users);
    }


}
