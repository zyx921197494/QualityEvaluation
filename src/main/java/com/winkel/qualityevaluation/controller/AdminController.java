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
import com.winkel.qualityevaluation.entity.*;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex1;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex2;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex3;
import com.winkel.qualityevaluation.entity.task.EvaluateReportFile;
import com.winkel.qualityevaluation.entity.task.EvaluateSubmit;
import com.winkel.qualityevaluation.entity.task.EvaluateSubmitFile;
import com.winkel.qualityevaluation.entity.task.EvaluateTask;
import com.winkel.qualityevaluation.pojo.dto.CycleDTO;
import com.winkel.qualityevaluation.pojo.dto.SchoolTaskDTO;
import com.winkel.qualityevaluation.pojo.vo.*;
import com.winkel.qualityevaluation.service.api.*;
import com.winkel.qualityevaluation.util.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
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
    private SubmitService submitService;

    @Autowired
    private IndexService indexService;

    @Autowired
    private Index1Service index1Service;

    @Autowired
    private Index2Service index2Service;

    @Autowired
    private Index3Service index3Service;

    @Autowired
    private SubmitFileService submitFileService;

    @Autowired
    private OssUtil ossUtil;

    @GetMapping("/getIndex")
    public ResponseUtil getIndex() {
        List<EvaluateIndex> list = indexService.list();
        if (list.isEmpty()) {
            return new ResponseUtil(500, "评价体系为空");
        }
        return new ResponseUtil(200, "查找评价体系", list);
    }

    @GetMapping("/getIndex1/{indexId}")
    public ResponseUtil getIndex1(@PathVariable String indexId) {
        List<EvaluateIndex1> list = index1Service.list(new QueryWrapper<EvaluateIndex1>().eq("evaluate_id", indexId));
        if (list.isEmpty()) {
            return new ResponseUtil(500, "一级指标为空");
        }
        return new ResponseUtil(200, "查找二级指标成功", list);
    }

    @GetMapping("/getIndex2/{index1Id}")
    public ResponseUtil getIndex2(@PathVariable String index1Id) {
        List<EvaluateIndex2> list = index2Service.list(new QueryWrapper<EvaluateIndex2>().eq("evaluate_index1_id", index1Id));
        if (list.isEmpty()) {
            return new ResponseUtil(500, "二级指标为空");
        }
        return new ResponseUtil(200, "查找二级指标成功", list);
    }

    @GetMapping("/getIndex3/{index2Id}")
    public ResponseUtil getIndex3(@PathVariable String index2Id) {
        List<EvaluateIndex3> list = index3Service.list(new QueryWrapper<EvaluateIndex3>().eq("evaluate_index2_id", index2Id));
        if (list.isEmpty()) {
            return new ResponseUtil(500, "三级指标为空");
        }
        return new ResponseUtil(200, "查找三级指标成功", list);
    }

    @GetMapping("/newIndex")
    public ResponseUtil newIndex(@RequestParam String name, @RequestParam String memo) {
        boolean save = indexService.save(new EvaluateIndex(null, name, memo));
        if (save) {
            return new ResponseUtil(200, "创建评价体系成功");
        }
        return new ResponseUtil(500, "创建评价体系失败");
    }

    @PostMapping("/newIndex1")
    public ResponseUtil newIndex1(@RequestParam Integer indexId, @RequestBody List<EvaluateIndex1> list) {
        for (EvaluateIndex1 index1 : list) {
            index1.setEvaluateIndexId(indexId);
            index1.setIndex1Name("A" + RandomUtil.randomString(6));
        }
        boolean saveBatch = index1Service.saveBatch(list);
        if (saveBatch) {
            return new ResponseUtil(200, "创建一级指标成功");
        }
        return new ResponseUtil(500, "创建一级指标失败");
    }

    @PostMapping("/newIndex2")
    public ResponseUtil newIndex2(@RequestParam Integer index1Id, @RequestBody List<EvaluateIndex2> list) {
        for (EvaluateIndex2 index2 : list) {
            index2.setIndex1Id(index1Id);
            index2.setIndex2Name("B" + RandomUtil.randomString(6));
        }
        boolean saveBatch = index2Service.saveBatch(list);
        if (saveBatch) {
            return new ResponseUtil(200, "创建二级指标成功");
        }
        return new ResponseUtil(500, "创建二级指标失败");
    }

    @PostMapping("/newIndex3")
    public ResponseUtil newIndex3(@RequestParam Integer index2Id, @RequestBody List<EvaluateIndex3> list) {
        for (EvaluateIndex3 index3 : list) {
            String[] content = index3.getIndex3Content().split("\\|");
            if ((index3.getType() == 1 && content.length != 2) || (index3.getType() == 2 && content.length != 4)) {
                return new ResponseUtil(500, "指标选项有误");
            }
            String[] score = index3.getIndex3Score().split("\\|");
            if ((index3.getType() == 1 && score.length != 2) || (index3.getType() == 2 && score.length != 4)) {
                return new ResponseUtil(500, "指标得分有误");
            }
            index3.setIndex2Id(index2Id);
        }
        boolean saveBatch = index3Service.saveBatch(list);
        if (saveBatch) {
            return new ResponseUtil(200, "创建三级指标成功");
        }
        return new ResponseUtil(500, "创建三级指标失败");
    }


//    /**
//     * desc: 启动评估周期页面：查询所有评价体系
//     * params: []
//     * return: com.winkel.qualityevaluation.util.ResponseUtil
//     * exception:
//     **/
//    @GetMapping("/listEvaluateIndex")
//    public ResponseUtil listEvaluateIndex() {
//        List<EvaluateIndex> list = indexService.list();
//        if (list.isEmpty()) {
//            return new ResponseUtil(200, "评价体系为空");
//        }
//        return new ResponseUtil(200, "查询评价体系成功", list);
//    }

//    /**
//     * desc: 创建一套评价体系
//     * params: [vo]
//     * return: com.winkel.qualityevaluation.util.ResponseUtil
//     * exception:
//     **/
//    @PostMapping("/newEvaluateIndex")
//    public ResponseUtil newEvaluateIndex(@RequestBody EvaluateIndexVo vo) {
//        EvaluateIndex index = vo.getEvaluateIndex();
//        indexService.save(index);
//
//        List<Index1Vo> index1List = vo.getIndex1List();
//        for (Index1Vo index1Vo : index1List) {
//            EvaluateIndex1 index1 = index1Vo.getEvaluateIndex1();
//            index1Service.save(index1.setEvaluateIndexId(index.getEvaluateId()));
//
//            List<Index2Vo> index2VoList = index1Vo.getIndex2VoList();
//            for (Index2Vo index2Vo : index2VoList) {
//                EvaluateIndex2 index2 = index2Vo.getEvaluateIndex2();
//                index2Service.save(index2.setIndex1Id(index1.getIndex1Id()));
//
//                List<EvaluateIndex3> index3List = index2Vo.getIndex3List();
//                for (EvaluateIndex3 index3 : index3List) {
//                    index3Service.save(index3.setIndex2Id(index2.getIndex2Id()));
//                }
//            }
//        }
//
//        return new ResponseUtil(200, "新增评价体系成功");
//    }

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
     * params: [schools, userType] schools：学校的标识码列表 userType：自评(5、10)、督评(6、11)
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @SneakyThrows
    @PostMapping("/exportUser")
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
    @GetMapping("/schools")
    public ResponseUtil schools(String schoolCode, String keyName, String keyLocation, String locationCode, Integer
            isCity, Integer isPublic, Integer isRegister, Integer isGB, @RequestParam("current") Integer
                                        current, @RequestParam("pageSize") Integer pageSize) {
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

        IPage<School> list;
        if (isPublic != null) {
            list = schoolService.page(page, isPublic == 1 ? wrapper.ne("school_host_code", "999") : wrapper.eq("school_host_code", "999"));
        } else {
            list = schoolService.page(page, wrapper);
        }
        if (list == null || list.getTotal() == 0) {
            return new ResponseUtil(500, "数据为空");
        }
        for (School school : list.getRecords()) {
            String county = locationService.getOne(new QueryWrapper<Location>().eq("code", school.getLocationCode())).getName();
            school.setLocationCode(county);
        }
        return new ResponseUtil(200, "查找成功", list);
    }


    /**
     * desc: 按照省、市、区、城市/农村、公办/民办、普惠/非普惠、各级评估（自评、督评、县级复评 1、县级复评 2、市级复评、省级复评）的评估状态（未开始 / 评估中 /数据已提交 / 报告已提交）、评估启动时间、幼儿园名称中的关键字搜索幼儿园。
     * params: [schoolTaskDTO]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @PostMapping("/schoolTask")
    public ResponseUtil schoolTask(@RequestBody SchoolTaskDTO schoolTaskDTO) {
        if (schoolTaskDTO.getCurrentPage() == null || schoolTaskDTO.getPageSize() == null) {
            return new ResponseUtil(500, "请求中未包含分页参数");
        }
        schoolTaskDTO.setCurrentPage(schoolTaskDTO.getCurrentPage() - 1);
        List<SchoolTaskVo> schoolTaskVos = taskService.listAllBySort(schoolTaskDTO);
        if (schoolTaskVos.isEmpty()) {
            return new ResponseUtil(500, "未查询到此类型数据");
        }
        for (SchoolTaskVo vo : schoolTaskVos) {
            vo.setLastSubmit(taskService.getLastSubmitTimeByTaskId(vo.getTaskId()));
            vo.setFirstSubmit(taskService.getFirstSubmitTimeByTaskId(vo.getTaskId()));
            List<EvaluateSubmit> submits = submitService.list(new QueryWrapper<EvaluateSubmit>().eq("evaluate_task_id", vo.getTaskId()));  // 已提交数据
            List<EvaluateIndex3> index3s = index3Service.listIndex3ByEvaluateId(taskService.getById(vo.getTaskId()).getEvaluateId());  // 所有题目
            List<Index3Vo> result = new ArrayList<>(index3s.size());  // 最终返回的VoList
            int current = 0;
            for (int i = 0; i < 40; i++) {
                EvaluateIndex3 index3 = index3s.get(i);
                Index3Vo index3Vo = new Index3Vo()
                        .setIndex3id(index3.getIndex3Id())
                        .setIndex3Name(index3.getIndex3Name())
                        .setIndex3Content(index3.getIndex3Content())
                        .setType(String.valueOf(index3.getType()))
                        .setMemo(index3.getMemo());
                if (current < submits.size() && Objects.equals(submits.get(current).getIndex3Id(), index3.getIndex3Id())) {  // 已经回答该题
                    index3Vo.setContent(submits.get(current).getContent());
                    ++current;
                }
                result.add(index3Vo);
            }
            vo.setSubmits(result);
        }
        return new ResponseUtil(200, "查询成功", schoolTaskVos);
    }


    /**
     * desc: 评估数据管理 3、查看评估数据和证据文件。不能修改评估数据
     * params: [schoolCode, taskType]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @GetMapping("/getEvaluationData")
    public ResponseUtil getEvaluationData(@RequestParam String schoolCode, @RequestParam Integer taskType) {
        Integer taskId = taskService.getTaskIdByBySchoolcodeAndType(schoolCode, taskType);
        List<EvaluateSubmit> submits = submitService.list(new QueryWrapper<EvaluateSubmit>().eq("evaluate_task_id", taskId));

        List<Index3Vo> index3VoList = new ArrayList<>(submits.size());
        for (EvaluateSubmit submit : submits) {
            EvaluateIndex3 index = index3Service.getOne(new QueryWrapper<EvaluateIndex3>().eq("evaluate_index3_id", submit.getIndex3Id()));
            index3VoList.add(new Index3Vo()
                    .setIndex3id(index.getIndex3Id())
                    .setIndex3Name(index.getIndex3Name())
                    .setIndex3Content(index.getIndex3Content())
                    .setType(index.getType() == 1 ? "判断" : index.getType() == 2 ? "单选" : "多选")
                    .setMemo(index.getMemo())
                    .setSubmitTime(submit.getSubmitTime())
                    .setContent(submit.getContent()));
        }
        List<EvaluateSubmitFile> fileList = submitFileService.list(new QueryWrapper<EvaluateSubmitFile>().eq("evaluate_task_id", taskId));
        HashMap<String, List> resultMap = new HashMap<>();
        resultMap.put("评估数据", index3VoList);
        resultMap.put("证据文件", fileList);
        return new ResponseUtil(200, "查询幼儿园评估数据成功", resultMap);
    }


    /**
     * desc: 评估数据管理 4、导出地区内评估数据
     * params: [schools, taskType] schools:对象包括 code标识码 和 name学校名称
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @SneakyThrows
    @PostMapping("/exportEvaluationData")
    public ResponseUtil exportEvaluationData(@RequestBody List<String> schools, @RequestParam Integer taskType) {
        ArrayList<List<Index3Vo>> data = new ArrayList<>();
        ArrayList<SimpleSchoolVo> schoolList = new ArrayList<>();
        for (String code : schools) {
            Integer taskId = taskService.getTaskIdByBySchoolcodeAndType(code, taskType);
            List<EvaluateSubmit> submits = submitService.list(new QueryWrapper<EvaluateSubmit>().eq("evaluate_task_id", taskId));

            List<Index3Vo> index3VoList = new ArrayList<>(submits.size());
            for (EvaluateSubmit submit : submits) {
                EvaluateIndex3 index = index3Service.getOne(new QueryWrapper<EvaluateIndex3>().eq("evaluate_index3_id", submit.getIndex3Id()));
                index3VoList.add(new Index3Vo()
                        .setIndex3id(index.getIndex3Id())
                        .setIndex3Name(index.getIndex3Name())
                        .setIndex3Content(index.getIndex3Content())
                        .setType(index.getType() == 1 ? "判断" : index.getType() == 2 ? "单选" : "多选")
                        .setMemo(index.getMemo())
                        .setSubmitTime(submit.getSubmitTime())
                        .setContent(submit.getContent()));
            }
            if (!index3VoList.isEmpty()) {  // 有的学校评估未启动没有数据，但仍在请求参数中。新开list避免Excel写入空数据
                data.add(index3VoList);
                schoolList.add(new SimpleSchoolVo().setCode(code).setName(schoolService.getOne(new QueryWrapper<School>().eq("school_code", code)).getName()));
                log.info("list大小{}   {}", data.size(), schoolList.size());
            }
        }

        String directoryPath = "C:\\Users\\Public\\Downloads\\评估数据批量导出\\";
        File path = new File(directoryPath);
        File file = new File(directoryPath + "评估数据.xlsx");
        path.mkdir();

        ossUtil.downloadSimple("评估数据.xlsx", directoryPath);
        log.info("下载文件模板文件 评估数据.xlsx");
        if (file.exists()) {
            for (int i = 0; i < data.size(); i++) {
                SimpleSchoolVo simpleSchoolVo = schoolList.get(i);
                ExcelUtil.writeObjectToExcel(simpleSchoolVo, file.getAbsolutePath(), true);
                if (i == 1) {
                    ExcelUtil.writeExcel(data.get(i), file.getAbsolutePath(), true);  // 第一次写入评估数据时加入表头
                }
                ExcelUtil.writeExcel(data.get(i), file.getAbsolutePath(), false);
            }
            return new ResponseUtil(200, "导出评估数据文件成功");
        }

        return new ResponseUtil(200, "导出幼儿园评估数据成功");
    }


    /**
     * desc: 批量下载报告文件
     * params: [schoolCodes, type] type：报告类型(1-5)
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @PostMapping("/downloadReport")
    public ResponseUtil downloadReport(@RequestBody List<String> schoolCodes, @RequestParam Integer type) {
        List<String> filenames = taskService.getFileNameBySchoolcodeAndType(schoolCodes, type);
        if (filenames.isEmpty()) {
            return new ResponseUtil(500, "当前无报告可下载");
        }
        String path = "C:\\Users\\Public\\Downloads\\报告下载\\";
        File file = new File(path);
        file.mkdir();
        ossUtil.downloadList(filenames, path);
        return new ResponseUtil(200, "下载报告成功");
    }


    /**
     * desc: 修改幼儿园信息
     * params: [school]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @PostMapping("/updateSchool")
    public ResponseUtil updateSchool(@RequestBody School school) {
        System.out.println("school = " + school);
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
        if (school.getIsCentral() != null) wrapper.set("is_central", school.getIsCentral());

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
    @PostMapping("/changeSchoolLocation")
    public ResponseUtil changeSchoolLocation(@RequestBody List<String> schoolCodes, @RequestParam String
            locationCode) {
        boolean success;
        for (String schoolCode : schoolCodes) {
            success = schoolService.update(new UpdateWrapper<School>().eq("school_code", schoolCode).set("school_location_code", locationCode));
            if (!success) {
                return new ResponseUtil(500, "修改归属地失败");
            }
        }
        return new ResponseUtil(200, "修改归属地成功");
    }


    /**
     * desc: 省市县管理员修改本账号密码
     * params: [request, newPwd]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @PostMapping("/changeAdminPassword")
    public ResponseUtil changePassword(HttpServletRequest request, @RequestBody List<String> newPwd) {
        String userId = getTokenUser(request).getId();
        User user = userService.getOne(new QueryWrapper<User>().eq("id", userId).select("password"));
        if (StringUtils.equals(newPwd.get(0), user.getPassword())) {
            return new ResponseUtil(500, "旧密码不能和原密码相同");
        }
        String s = newPwd.get(0);
        String[] split = s.split("");
        for (String t : split) {
            if (" ".equals(t)) {
                return new ResponseUtil(500, "密码不能包含空格");
            }
        }
        if (userService.update(new UpdateWrapper<User>().eq("id", userId).set("password", newPwd.get(0)))) {
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
    @PostMapping("/changeUserPassword")
    public ResponseUtil changeUserPassword(@RequestBody List<String> schoolCodes, @RequestParam Integer
            authorityId) {
        for (String schoolCode : schoolCodes) {
            School school = schoolService.getOne(new QueryWrapper<School>().eq("school_code", schoolCode));
            Integer currentCycle = taskService.getCurrentCycle(school.getLocationCode().substring(0, 6) + "000000");

            if (userService.changeUserPassword(schoolCode, authorityId, RandomUtil.randomNums(8), currentCycle) &&
                    userService.changeUserPassword(schoolCode, authorityId + 5, RandomUtil.randomNums(8), currentCycle)) {
                return new ResponseUtil(200, "更换评估密码成功");
            }
        }
        return new ResponseUtil(500, "更换评估密码失败");
    }


    /**
     * desc: 删除幼儿园账号
     * params: [schoolCode]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    //todo 废弃 同时删除评估数据：代码删除 或 触发器
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


    @GetMapping("/getCycle")
    public ResponseUtil getCycle(@RequestParam String locationCode) {
        CycleDTO dto = new CycleDTO().setLocationCode(locationCode);
        // 判断行政区标识码的类型是省/市/县
        if (locationCode.endsWith("0000000000")) {
            dto.setType(1);
        } else if (locationCode.endsWith("00000000")) {
            dto.setType(2);
        } else {
            dto.setType(3);
        }
        List<CycleVo> cycle = taskService.getCycleByLocationAndRegionType(dto);
        return new ResponseUtil(200, "查询周期成功", cycle);
    }


    /**
     * desc: 县内所有幼儿园的评估完成后，市级管理员选择县，启动一个新的评估周期
     * 冻结以往周期所有的督评、复评数据
     *       todo 校验是否督评、复评账号随周期更换 解锁 开启新周期/创建新用户时
     * params: [locationCode]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @PostMapping("/startCycle")
    public ResponseUtil startCycle(@RequestBody List<String> locationCodes, @RequestParam Integer
            evaluateIndexId) {
        if (locationCodes.isEmpty()) {
            return new ResponseUtil(500, "请至少选择一所学校");
        }
        for (String locationCode : locationCodes) {
            // 校验县下属的学校是否全部完成评估；冻结过往周期数据；冻结账号
            Integer cycle = taskService.getCurrentCycle(locationCode);
            List<School> schoolList = schoolService.list(new QueryWrapper<School>().likeRight("school_location_code", locationCode.substring(0, 6)));
            for (School school : schoolList) {
                if (school.getIsLocked() == 1) {  // 删除的幼儿园不参与新周期的评估
                    continue;
                }
                List<EvaluateTask> tasks = taskService.list(new QueryWrapper<EvaluateTask>().eq("school_code", school.getCode()).eq("task_cycle", cycle));
                for (EvaluateTask task : tasks) {
                    if ((Objects.equals(task.getType(), Const.TASK_TYPE_SELF) && !Objects.equals(task.getStatus(), Const.TASK_REPORT_SUBMITTED)) || (!Objects.equals(task.getType(), Const.TASK_TYPE_SELF) && !Objects.equals(task.getStatus(), Const.TASK_REPORT_ACCEPTED))) {
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
                        .setEvaluateId(evaluateIndexId)
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
            userService.unlockSelfUserByLocationCode(locationCode);

            //开启新周期
            if (success[0] == success[1] == success[2] == success[3] == success[4] && taskService.startCycle(locationCode)) {
                log.info("启动 {} 幼儿园第 {} 周期的教学质量评估", locationCode, currentCycle);
            } else {
                return new ResponseUtil(500, "开启新评估周期失败");
            }
        }
        return new ResponseUtil(200, "成功启动新一轮评估周期");
    }


    /**
     * @param schoolCodeList 重启评估的类型(5--9)：如重启自评
     * @param type           幼儿园标识编码List
     * @desc: params:
     * @return: com.winkel.qualityevaluation.util.ResponseUtil
     * @exception:
     **/
    @PostMapping("/resetEvaluation")
    public ResponseUtil resetEvaluation(@RequestBody List<String> schoolCodeList, @RequestParam("type") Integer
            type) {
        for (String schoolCode : schoolCodeList) {
            School school = schoolService.getOne(new QueryWrapper<School>().eq("school_code", schoolCode));
            Integer cycle = taskService.getCurrentCycle(school.getLocationCode().substring(0, 6) + "000000");
            EvaluateTask task = taskService.getOne(new QueryWrapper<EvaluateTask>().eq("school_code", schoolCode).eq("task_cycle", cycle).eq("task_type", type - 4));

            if (Objects.equals(task.getStatus(), Const.TASK_NOT_START) || task.getStatus() > Const.TASK_REPORT_SUBMITTED) {
                return new ResponseUtil(403, "不能重启未开始或已通过的评估");
            }

            if (!(userService.unlockUserBySchoolCode(schoolCode, type)
                    && taskService.update(new UpdateWrapper<EvaluateTask>().eq("evaluate_task_id", task.getId())
                    .set("task_status", Const.TASK_IN_EVALUATION)
                    .set("evaluate_task_start_time", LocalDateTime.now())
                    .set("evaluate_task_end_time", LocalDateTime.now().plusDays(15))))) {
                return new ResponseUtil(500, "重启评估时账号错误");
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
                if (ids.isEmpty()) return new ResponseUtil(200, "没有已完成的评估任务及待审核报告");
                Collection<EvaluateReportFile> reportFiles = reportFileService.list(new QueryWrapper<EvaluateReportFile>().in("task_id", ids));
                if (reportFiles.isEmpty()) {
                    return new ResponseUtil(200, "没有待审核的督评或县复评报告");
                }
                return new ResponseUtil(200, "查询督评或县复评报告成功", reportFiles);
            }
            case "ROLE_ADMIN_CITY": {
                List<Integer> ids = taskService.getFinishTaskIdByCityAdminId(getTokenUser(request).getId());
                if (ids.isEmpty()) return new ResponseUtil(200, "没有已完成的评估任务及待审核报告");
                Collection<EvaluateReportFile> reportFiles = reportFileService.list(new QueryWrapper<EvaluateReportFile>().in("task_id", ids));
                if (reportFiles.isEmpty()) {
                    return new ResponseUtil(200, "没有待审核的督评或县复评报告");
                }
                return new ResponseUtil(200, "查询市复评报告成功", reportFiles);
            }
            case "ROLE_ADMIN_PROVINCE": {
                List<Integer> ids = taskService.getFinishTaskIdByProvinceAdminId(getTokenUser(request).getId());
                if (ids.isEmpty()) return new ResponseUtil(200, "没有已完成的评估任务及待审核报告");
                Collection<EvaluateReportFile> reportFiles = reportFileService.list(new QueryWrapper<EvaluateReportFile>().in("task_id", ids));
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
    @PostMapping("/auditReport")
    public ResponseUtil auditReport(@RequestBody List<String> schoolCodes, @RequestParam Integer
            type, @RequestParam Integer isAccept) {
        for (String schoolCode : schoolCodes) {
            Integer taskId = taskService.getTaskIdByBySchoolcodeAndType(schoolCode, type);
            if (taskId == null) {
                return new ResponseUtil(500, "标识码 " + schoolCode + " 学校暂未上传报告，请核对后审核");
            }
            EvaluateTask task = taskService.getById(taskId);
            if (task.getStatus() < Const.TASK_REPORT_SUBMITTED || Objects.equals(task.getStatus(), Const.TASK_REPORT_ACCEPTED)) {
                return new ResponseUtil(500, "报告已经审核，不能重复审核");
            }

            boolean update = taskService.update(new UpdateWrapper<EvaluateTask>()
                    .eq("evaluate_task_id", taskId)
                    .set("task_status", isAccept == 1 ? Const.TASK_REPORT_ACCEPTED : Const.TASK_REPORT_REFUSED));
            if (update) {
                log.info("审核报告成功，taskId={}", taskId);
            } else {
                log.info("审核报告失败，taskId={}", taskId);
                return new ResponseUtil(500, "审核报告失败");
            }
        }
        return new ResponseUtil(200, "审核报告成功");
    }


    /**
     * desc: 上传区域报告
     * params: [request, year, file] year：本地区哪一年的报告
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @PostMapping("/uploadLocationReport")
    public ResponseUtil uploadLocationReport(HttpServletRequest request, @RequestParam Integer
            year, @RequestParam("file") MultipartFile file) {
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
    public ResponseUtil listLocationReport(HttpServletRequest request, Integer year) {
        String locationCode = userService.getOne(new QueryWrapper<User>().eq("id", getTokenUser(request).getId()).select("location_code")).getLocationCode();
        Integer role = getAdminRole(request);
        List<LocationReport> resultList = new ArrayList<>();
        QueryWrapper<LocationReport> wrapper = new QueryWrapper<LocationReport>().eq("location_code", locationCode);
        if (year != null) {
            wrapper.eq("year", year);
        }

        if (role.equals(Const.ROLE_ADMIN_COUNTY)) {
            resultList = locationReportService.list(wrapper);
        } else if (role.equals(Const.ROLE_ADMIN_CITY)) {
            resultList.addAll(locationReportService.list(wrapper));  //市级报告
            List<Location> locations = locationService.list(new QueryWrapper<Location>().eq("p_code", locationCode));
            for (Location location : locations) {
                QueryWrapper<LocationReport> queryWrapper = new QueryWrapper<LocationReport>().eq("location_code", location.getCode());
                if (year != null) {
                    queryWrapper.eq("year", year);
                }
                List<LocationReport> reports = locationReportService.list(queryWrapper);
                if (!reports.isEmpty()) {
                    resultList.addAll(reports);
                }
            }
        } else {
            resultList.addAll(locationReportService.list(wrapper)); // 省级报告
            List<Location> cities = locationService.list(new QueryWrapper<Location>().eq("p_code", locationCode));
            for (Location city : cities) {
                QueryWrapper<LocationReport> queryWrapper = new QueryWrapper<LocationReport>().eq("location_code", city.getCode());
                if (year != null) {
                    queryWrapper.eq("year", year);
                }
                resultList.addAll(locationReportService.list(queryWrapper));  // 市级报告
                List<Location> counties = locationService.list(new QueryWrapper<Location>().eq("p_code", city.getCode()));
                for (Location county : counties) {
                    QueryWrapper<LocationReport> queryWrapper1 = new QueryWrapper<LocationReport>().eq("location_code", county.getCode());
                    if (year != null) {
                        queryWrapper1.eq("year", year);
                    }
                    List<LocationReport> reports = locationReportService.list(queryWrapper1);
                    if (!reports.isEmpty()) {
                        resultList.addAll(reports);
                    }
                }
            }
        }

        if (resultList.isEmpty()) {
            return new ResponseUtil(500, "当前辖区内未上传任何区域报告");
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


    /**
     * desc: 批量下载区域报告文件
     * params: [ids]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @PostMapping("/downloadLocationReport")
    public ResponseUtil downloadLocationReport(@RequestBody List<Integer> ids) {
        ArrayList<String> pathList = new ArrayList<>();
        for (Integer id : ids) {
            LocationReport report = locationReportService.getById(id);
            if (report == null) {
                return new ResponseUtil(500, "找不到id对应的区域报告，请检查参数");
            }
            pathList.add(report.getFilePath().substring(43));
        }
        String directoryPath = "C:\\Users\\Public\\Downloads\\区域报告导出\\";
        File dir = new File(directoryPath);
        dir.mkdir();
        ossUtil.downloadList(pathList, directoryPath);


        return new ResponseUtil(200, "批量下载区域报告成功");

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
