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
import com.winkel.qualityevaluation.service.api.UserService;
import com.winkel.qualityevaluation.util.Const;
import com.winkel.qualityevaluation.util.JWTUtil;
import com.winkel.qualityevaluation.util.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/universal")
public class CommonController {

    @Autowired
    private UserService userService;

    @Autowired
    private LocationService locationService;

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
     * desc:用于页面获取并区分用户类型，如 ADMIN_COUNTY
     * params: [request]
     * return: java.lang.String
     * exception:
     **/
    @GetMapping("/currentRole")
    public String currentRole(HttpServletRequest request) {
        return StringUtils.substringAfter(userService.getAuthorities(getTokenUser(request).getUsername()).get(0).getAuthority(), "_");
    }

    private User getTokenUser(HttpServletRequest request) {
        return JWTUtil.parseJWTUser(request.getHeader(Const.TOKEN_HEADER).substring(Const.STARTS_WITH.length()));
    }

}
