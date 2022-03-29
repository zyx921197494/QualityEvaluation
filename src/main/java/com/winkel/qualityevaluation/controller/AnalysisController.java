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
import com.winkel.qualityevaluation.entity.task.EvaluateTask;
import com.winkel.qualityevaluation.service.api.LocationService;
import com.winkel.qualityevaluation.service.api.SubmitService;
import com.winkel.qualityevaluation.service.api.TaskService;
import com.winkel.qualityevaluation.service.api.UserService;
import com.winkel.qualityevaluation.util.Const;
import com.winkel.qualityevaluation.util.JWTUtil;
import com.winkel.qualityevaluation.util.ResponseUtil;
import com.winkel.qualityevaluation.vo.CountDTO;
import com.winkel.qualityevaluation.vo.ScoreDTO;
import com.winkel.qualityevaluation.vo.ScoreVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
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

    @Autowired
    private SubmitService submitService;

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
     * desc: 市级、省级管理员调用，获取下属所有县的自评、督评完成情况
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
    @GetMapping("/evaluateCountyProcess")
    public ResponseUtil evaluateCountyProcess(HttpServletRequest request, @RequestParam String countyCde) {
//        String locationCode = userService.getOne(new QueryWrapper<User>().eq("id", getTokenUser(request).getId()).select("location_code")).getLocationCode();
        int self1 = 0, self2 = 0, self3 = 0, self4 = 0;
        int sup1 = 0, sup2 = 0, sup3 = 0, sup4 = 0, sup5 = 0, sup6 = 0;
        List<EvaluateTask> tasks = taskService.getCountyTask(countyCde);
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

    /**
     * desc: 查看某县辖区内学校自评 5项一级指标的平均值，以及总分均值 todo 加入督评type
     * params: [request, countyCode]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @GetMapping("/getCountyScore")
    public ResponseUtil getCountyScore(HttpServletRequest request, @RequestParam String countyCode) {
        HashMap<String, String> resultMap = new HashMap<>();
        Double count = submitService.getCountByCountycode(countyCode);
        String[] a = new String[5];
        if (count == 0) {
            return new ResponseUtil(200, "辖区内没有评估记录");
        }
        DecimalFormat df = new DecimalFormat("#0.00");  // 保留2位小数
        a[0] = df.format(submitService.getSumByIndex1IdAndCountycode(Const.INDEX1_A1, countyCode) / count);
        a[1] = df.format(submitService.getSumByIndex1IdAndCountycode(Const.INDEX1_A2, countyCode) / count);
        a[2] = df.format(submitService.getSumByIndex1IdAndCountycode(Const.INDEX1_A3, countyCode) / count);
        a[3] = df.format(submitService.getSumByIndex1IdAndCountycode(Const.INDEX1_A4, countyCode) / count);
        a[4] = df.format(submitService.getSumByIndex1IdAndCountycode(Const.INDEX1_A5, countyCode) / count);
        double sum = 0;
        for (int i = 0; i < 5; i++) {
            resultMap.put("A" + (i + 1), a[i]);
            sum += Double.parseDouble(a[i]);
        }
        resultMap.put("总分均值", df.format(sum / count));

        return new ResponseUtil(200, "查询一级指标得分成功", resultMap);
    }


    /**
     * desc: 查看某区县内不同督评完成情况下幼儿园5项一级指标得分及总分
     * params: [request, taskStatus, countyCode] taskStatus：督评完成状态(只能选择3--6) countyCode：若为市级/升级管理员，需要具体选择到县
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @GetMapping("/getSchoolScore")
    public ResponseUtil getSchoolScore(HttpServletRequest request, @RequestParam Integer taskStatus, String countyCode) {
        int role = getAdminRole(request);
        String locationCode;
        if (role != 1) {  // 省市级管理员
            if (StringUtils.isBlank(countyCode)) {
                return new ResponseUtil(500, "省市管理员要选择到区县");
            }
            locationCode = countyCode;
        } else {  // 县级管理员
            locationCode = userService.getOne(new QueryWrapper<User>().eq("id", getTokenUser(request).getId()).select("location_code")).getLocationCode();
        }
        HashMap<String, Map<String, Object>> resultMap = new HashMap<>();
        List<ScoreVo> indexScore = submitService.getIndex1ScoreByCountycode(locationCode, taskStatus);
        List<ScoreVo> totalScore = submitService.getTotalScoreByCountycode(locationCode, taskStatus);
        int count = 1;
        for (ScoreVo scoreVo : totalScore) {  // 总分从高到低排列，先循环总分
            if (!resultMap.containsKey(scoreVo.getSchoolCode())) {  // 遍历到新学校创建key为学校标识码的map
                HashMap<String, Object> map = new HashMap<>();
                map.put("No", count++);
                map.put("schoolName", scoreVo.getSchoolName());
                resultMap.put(scoreVo.getSchoolCode(), map);
            }
            resultMap.get(scoreVo.getSchoolCode()).put("total", scoreVo.getScore());
        }
        for (ScoreVo scoreVo : indexScore) {
            resultMap.get(scoreVo.getSchoolCode()).put(scoreVo.getName(), scoreVo.getScore());
        }
        return new ResponseUtil(200, "查询评估进度成功", resultMap);
    }


    /**
     * desc: 查看省市县范围内城市园和农村园总分平均值和各一级指标平均值
     * <p>
     * params: [scoreDTO]
     * String locationCode：地址码，必须为省市县
     * Integer locationType：地址码为县则type为1，市则type为2，省则type为3
     * Integer taskType：默认查看督评任务，type=2
     * Integer taskStatus：默认查看完成的督评任务，status=6
     * <p>
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @PostMapping("/getScoreIsCity")
    public ResponseUtil getScoreIsCity(@RequestBody ScoreDTO scoreDTO) {//String locationCode, Integer locationType, Integer taskType, Integer taskStatus
        HashMap<String, Object> resultMap = new HashMap<>(2);
        List<ScoreVo> scoreVos = submitService.getScoreByIsCity(scoreDTO);
        double count = submitService.getCountByLocationCodeAndType(new CountDTO().setLocationCode(scoreDTO.getLocationCode()).setLocationType(scoreDTO.getLocationType()));
        DecimalFormat df = new DecimalFormat("#0.00");
        for (ScoreVo scoreVo : scoreVos) {
            if (scoreVo.getLocationTypeCode().startsWith("1")) {
                resultMap.put("cityTotal", df.format(scoreVo.getScore() / count));
            } else {
                resultMap.put("countrysideTotal", df.format(scoreVo.getScore() / count));
            }
        }
        HashMap<String, String> city = new HashMap<>();
        HashMap<String, String> countryside = new HashMap<>();
        List<ScoreVo> index1s = submitService.getIndex1ByIsCity(scoreDTO);
        for (ScoreVo index : index1s) {
            System.out.println("index = " + index);
            if (index.getLocationTypeCode().startsWith("1")) {
                city.put(index.getName(), df.format(index.getScore() / count));
            } else {
                countryside.put(index.getName(), df.format(index.getScore() / count));
            }
        }
        System.out.println("city = " + city);
        System.out.println("countryside = " + countryside);
        resultMap.put("cityIndex", city);
        resultMap.put("countrysideIndex", countryside);

        return new ResponseUtil(200, "查询城市/农村园评估得分成功", resultMap);
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

    @Test
    public void test() {
    }


}
