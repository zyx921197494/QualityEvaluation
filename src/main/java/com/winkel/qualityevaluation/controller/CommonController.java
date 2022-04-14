package com.winkel.qualityevaluation.controller;
/*
  @ClassName CommonController
  @Description
  @Author winkel
  @Date 2022-03-30 17:00
  */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.winkel.qualityevaluation.entity.Location;
import com.winkel.qualityevaluation.entity.User;
import com.winkel.qualityevaluation.service.api.LocationService;
import com.winkel.qualityevaluation.service.api.SchoolService;
import com.winkel.qualityevaluation.service.api.UserService;
import com.winkel.qualityevaluation.util.Const;
import com.winkel.qualityevaluation.util.JWTUtil;
import com.winkel.qualityevaluation.util.OssUtil;
import com.winkel.qualityevaluation.util.ResponseUtil;
import com.winkel.qualityevaluation.vo.LocationVo;
import com.winkel.qualityevaluation.vo.SchoolVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/universal")
public class CommonController {

    @Autowired
    private UserService userService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private OssUtil ossUtil;

    /**
     * desc:
     * params:
     * return:
     * exception:
     **/
    @GetMapping("/schoolInfo")
    public ResponseUtil schoolInfo(@RequestParam String schoolCode) {
        SchoolVo school = schoolService.getSchoolVoBySchoolCode(schoolCode);
        if (school == null) {
            return new ResponseUtil(200, "幼儿园信息为空");
        }
        return new ResponseUtil(200, "查询幼儿园信息成功", school);
    }


    /**
     * desc: 获取省级行政区列表
     * params: []
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @GetMapping("/province")
    public ResponseUtil province() {
        return new ResponseUtil(200, "查询一级行政区成功", locationService.list(new QueryWrapper<Location>().eq("type", 1).select("code", "name")));
    }


    /**
     * desc: 获取locationCode下属的行政区编码及名称
     * params: [locationCode]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @GetMapping("/region/{locationCode}")
    public ResponseUtil region(@PathVariable String locationCode) {
        return new ResponseUtil(200, "查询下级行政区成功", locationService.list(new QueryWrapper<Location>().eq("p_code", locationCode).select("code", "name")));
    }

    /**
     * desc: 获取三级行政区
     * params: [locationCode]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @GetMapping("/location")
    public ResponseUtil region() {
        List<LocationVo> result = new ArrayList<>();

        List<Location> provinces = locationService.list(new QueryWrapper<Location>().eq("type", 1).select("code", "name"));  // 所有省

        for (Location province : provinces) {
            List<Location> cities = locationService.list(new QueryWrapper<Location>().eq("type",2).eq("p_code", province.getCode()).select("code", "name"));  // 省下所有市
            ArrayList<LocationVo> citiesVo = new ArrayList<>();
            for (Location city : cities) {
                List<Location> counties = locationService.list(new QueryWrapper<Location>().eq("type",3).eq("p_code", city.getCode()).select("code", "name"));// 市下所有县
                ArrayList<LocationVo> countiesVo = new ArrayList<>();
                for (Location county : counties) {
                    countiesVo.add(new LocationVo().setLabel(county.getName()).setValue(county.getCode()));
//                    log.info("当前：{} {} {}", province, city, county);
                }
                citiesVo.add(new LocationVo().setLabel(city.getName()).setValue(city.getCode()).setList(countiesVo));
            }
            result.add(new LocationVo().setLabel(province.getName()).setValue(province.getCode()).setList(citiesVo));
        }

        return new ResponseUtil(200, "查询行政区成功", result);
    }


    /**
     * desc:用于页面获取并区分用户类型，如 ADMIN_COUNTY
     * params: [request]
     * return: java.lang.String
     * exception:
     **/
    @GetMapping("/currentRole")
    public String currentRole(HttpServletRequest request) {
        return StringUtils.substringAfter(userService.getAuthorities(getTokenUser(request).getUsername()).get(0).getAuthority(), "_");
    }


    /**
     * desc: 通用下载文件接口。默认保存在 C:\Users\Public\Downloads\ 路径下
     * params: [filename]
     * return: com.winkel.qualityevaluation.util.ResponseUtil
     * exception:
     **/
    @GetMapping("/download")
    public ResponseUtil download(@RequestParam("filename") List<String> filenames) {
        ossUtil.downloadList(filenames);
        return new ResponseUtil(500, "下载成功");
    }

    private User getTokenUser(HttpServletRequest request) {
        return JWTUtil.parseJWTUser(request.getHeader(Const.TOKEN_HEADER).substring(Const.STARTS_WITH.length()));
    }

}
