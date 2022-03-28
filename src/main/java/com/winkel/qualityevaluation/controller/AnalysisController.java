package com.winkel.qualityevaluation.controller;
/*
  @ClassName AnalysisController
  @Description 各级管理员对本辖区内评估状态和督评结果进行评估
  @Author winkel
  @Date 2022-03-27 20:09
  */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.winkel.qualityevaluation.entity.Authority;
import com.winkel.qualityevaluation.entity.Location;
import com.winkel.qualityevaluation.entity.User;
import com.winkel.qualityevaluation.entity.task.EvaluateReportFile;
import com.winkel.qualityevaluation.entity.task.EvaluateTask;
import com.winkel.qualityevaluation.service.api.LocationService;
import com.winkel.qualityevaluation.service.api.TaskService;
import com.winkel.qualityevaluation.service.api.UserService;
import com.winkel.qualityevaluation.util.Const;
import com.winkel.qualityevaluation.util.JWTUtil;
import com.winkel.qualityevaluation.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/admin/analysis")
public class AnalysisController {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private LocationService locationService;

    /**
     * desc: 年度完成情况：获取下属区域中每年完成自评和督评的幼儿园数量
     * params: []
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @GetMapping("/finishSituation")
    public ResponseUtil finishSituation(HttpServletRequest request) {
        String locationCode = userService.getOne(new QueryWrapper<User>().eq("id", getTokenUser(request).getId()).select("location_code")).getLocationCode();
        HashMap<Integer, Map<Integer, Integer>> resultMap = new HashMap<>();
        HashMap<Integer, Integer> map2022 = new HashMap<>();
        List<EvaluateTask> selfTasks;
        List<EvaluateTask> supTasks;

        int role = getAdminRole(request);
        if (role == 1) {
            selfTasks = taskService.getCountByCountycodeAndTasktypeAndStatus(locationCode, Const.TASK_DATA_SUBMITTED, Const.TASK_TYPE_SELF);
            supTasks = taskService.getCountByCountycodeAndTasktypeAndStatus(locationCode, Const.TASK_REPORT_ACCEPTED, Const.TASK_TYPE_SUPERVISOR);
        } else if (role == 2) {
            selfTasks = taskService.getCountByCitycodeAndTasktypeAndStatus(locationCode, Const.TASK_DATA_SUBMITTED, Const.TASK_TYPE_SELF);
            supTasks = taskService.getCountByCitycodeAndTasktypeAndStatus(locationCode, Const.TASK_REPORT_ACCEPTED, Const.TASK_TYPE_SUPERVISOR);
        } else {
            selfTasks = taskService.getCountByProvincecodeAndTasktypeAndStatus(locationCode, Const.TASK_DATA_SUBMITTED, Const.TASK_TYPE_SELF);
            supTasks = taskService.getCountByProvincecodeAndTasktypeAndStatus(locationCode, Const.TASK_REPORT_ACCEPTED, Const.TASK_TYPE_SUPERVISOR);
        }
        map2022.put(selfTasks.size(), supTasks.size());
        resultMap.put(2022, map2022);
        return new ResponseUtil(200, "查询年度完成情况成功", resultMap);
    }

    /**
     * desc: 市级、升级管理员调用，获取下属所有县的自评、督评完成情况
     * params: [request]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @PreAuthorize("hasAnyRole('ROLE_ADMIN_CITY','ROLE_ADMIN_PROVINCE')")
    @GetMapping("/evaluateProcess")
    public ResponseUtil evaluateProcess(HttpServletRequest request) {
        String locationCode = userService.getOne(new QueryWrapper<User>().eq("id", getTokenUser(request).getId()).select("location_code")).getLocationCode();
        int role = getAdminRole(request);
        List<EvaluateTask> tasks;
        HashMap<String, Map<String, Integer>> resultMap = new HashMap<>();

        if (role == 2) {  // 所属市的自评、督评完成情况
            List<Location> countyList = locationService.list(new QueryWrapper<Location>().eq("p_code", locationCode));
            System.out.println("countyList = " + countyList);
            for (Location county : countyList) {
                int self1 = 0, self2 = 0, self3 = 0, self4 = 0;
                int sup1 = 0, sup2 = 0, sup3 = 0, sup4 = 0, sup5 = 0, sup6 = 0;
                tasks = taskService.getCountyTask(county.getCode());
                for (EvaluateTask task : tasks) {
                    if (Objects.equals(task.getType(), Const.TASK_TYPE_SELF)) {
                        switch (task.getStatus()) {
                            case 1:
                                self1++;
                            case 2:
                                self2++;
                            case 3:
                                self3++;
                            case 4:
                                self4++;
                        }
                    } else if (Objects.equals(task.getType(), Const.TASK_TYPE_SUPERVISOR)) {
                        switch (task.getStatus()) {
                            case 1:
                                sup1++;
                            case 2:
                                sup2++;
                            case 3:
                                sup3++;
                            case 4:
                                sup4++;
                            case 5:
                                sup5++;
                            case 6:
                                sup6++;
                        }
                    }
                }
                if (self1 != 0 || self2 != 0 || self3 != 0 || self4 != 0 || sup1 != 0 || sup2 != 0 || sup3 != 0 || sup4 != 0 || sup5 != 0 || sup6 != 0) {
                    HashMap<String, Integer> map = new HashMap<>();
                    map.put("自评未开始", self1);
                    map.put("自评评估中", self2);
                    map.put("自评数据已提交", self3);
                    map.put("自评报告已提交", self4);
                    map.put("督评未开始", sup1);
                    map.put("督评评估中", sup2);
                    map.put("督评数据已提交", sup3);
                    map.put("督评报告已提交", sup4);
                    map.put("督评报告审核通过", sup5);
                    map.put("督评报告审核未通过", sup6);
                    resultMap.put(county.getName(), map);
                }
            }
        } else {  // 所属省的自评、督评完成情况
            List<Location> cityList = locationService.list(new QueryWrapper<Location>().eq("p_code", locationCode));
            for (Location city : cityList) {
                List<Location> countyList = locationService.list(new QueryWrapper<Location>().eq("p_code", city.getCode()));
                for (Location county : countyList) {
                    int self1 = 0, self2 = 0, self3 = 0, self4 = 0;
                    int sup1 = 0, sup2 = 0, sup3 = 0, sup4 = 0, sup5 = 0, sup6 = 0;
                    tasks = taskService.getCountyTask(county.getCode());
                    for (EvaluateTask task : tasks) {
                        if (Objects.equals(task.getType(), Const.TASK_TYPE_SELF)) {
                            switch (task.getStatus()) {
                                case 1:
                                    self1++;
                                case 2:
                                    self2++;
                                case 3:
                                    self3++;
                                case 4:
                                    self4++;
                            }
                        } else if (Objects.equals(task.getType(), Const.TASK_TYPE_SUPERVISOR)) {
                            switch (task.getStatus()) {
                                case 1:
                                    sup1++;
                                case 2:
                                    sup2++;
                                case 3:
                                    sup3++;
                                case 4:
                                    sup4++;
                                case 5:
                                    sup5++;
                                case 6:
                                    sup6++;
                            }
                        }
                    }
                    if (self1 != 0 || self2 != 0 || self3 != 0 || self4 != 0 || sup1 != 0 || sup2 != 0 || sup3 != 0 || sup4 != 0 || sup5 != 0 || sup6 != 0) {
                        HashMap<String, Integer> map = new HashMap<>();
                        map.put("自评未开始", self1);
                        map.put("自评评估中", self2);
                        map.put("自评数据已提交", self3);
                        map.put("自评报告已提交", self4);
                        map.put("督评未开始", sup1);
                        map.put("督评评估中", sup2);
                        map.put("督评数据已提交", sup3);
                        map.put("督评报告已提交", sup4);
                        map.put("督评报告审核通过", sup5);
                        map.put("督评报告审核未通过", sup6);
                        resultMap.put(county.getName(), map);
                    }
                }
            }
        }
        return new ResponseUtil(200, "查询" + (role == 2 ? "市" : "省") + "评估进度成功", resultMap);
    }


    /**
     * desc: 县级管理员调用，获取县内幼儿园的自评、督评完成情况
     * params: [request]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @PreAuthorize("hasAnyRole('ROLE_ADMIN_COUNTY')")
    @GetMapping("/evaluateCountyProcess")
    public ResponseUtil evaluateCountyProcess(HttpServletRequest request) {
        String locationCode = userService.getOne(new QueryWrapper<User>().eq("id", getTokenUser(request).getId()).select("location_code")).getLocationCode();
        int self1 = 0, self2 = 0, self3 = 0, self4 = 0;
        int sup1 = 0, sup2 = 0, sup3 = 0, sup4 = 0, sup5 = 0, sup6 = 0;
        List<EvaluateTask> tasks = taskService.getCountyTask(locationCode);
        System.out.println("tasks = " + tasks);
        for (EvaluateTask task : tasks) {
            if (Objects.equals(task.getType(), Const.TASK_TYPE_SELF)) {
                switch (task.getStatus()) {
                    case 1:
                        self1++;
                    case 2:
                        self2++;
                    case 3:
                        self3++;
                    case 4:
                        self4++;
                }
            } else if (Objects.equals(task.getType(), Const.TASK_TYPE_SUPERVISOR)) {
                switch (task.getStatus()) {
                    case 1:
                        sup1++;
                    case 2:
                        sup2++;
                    case 3:
                        sup3++;
                    case 4:
                        sup4++;
                    case 5:
                        sup5++;
                    case 6:
                        sup6++;
                }
            }
        }
        HashMap<String, Integer> resultMap = new HashMap<>(10);
        resultMap.put("自评未开始", self1);
        resultMap.put("自评评估中", self2);
        resultMap.put("自评数据已提交", self3);
        resultMap.put("自评报告已提交", self4);
        resultMap.put("督评未开始", sup1);
        resultMap.put("督评评估中", sup2);
        resultMap.put("督评数据已提交", sup3);
        resultMap.put("督评报告已提交", sup4);
        resultMap.put("督评报告审核通过", sup5);
        resultMap.put("督评报告审核未通过", sup6);
        return new ResponseUtil(200, "查询评估进度成功", resultMap);
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

    private User getTokenUser(HttpServletRequest request) {
        return JWTUtil.parseJWTUser(request.getHeader(Const.TOKEN_HEADER).substring(Const.STARTS_WITH.length()));
    }

}
