package com.winkel.qualityevaluation.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName SecurityResponseUtil
 * @Description
 * @Author zyx
 * @Date 2020/4/14 19:41
 * @Blog www.winkelblog.top
 */
public class SecurityResponseUtil {
    
    private static final ObjectMapper objectMapper = new ObjectMapper().setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
    
    private SecurityResponseUtil() {
    }
    
    public static void fail(HttpServletResponse response, String errorMsg) {
        fail(response, 500, 500, errorMsg);
    }
    
    public static void fail(HttpServletResponse response, int statusCode, String errorMsg) {
        fail(response, statusCode, statusCode, errorMsg);
    }
    
    public static void fail(HttpServletResponse response, int statusCode, int errorCode, String errorMsg) {
        // 设置状态码
        response.setStatus(statusCode);

        // 设置数据为json格式
        response.setContentType("application/json;charset=UTF-8");

        CommonErrorMessage commonErrorMessage = CommonErrorMessage.fail(errorCode, errorMsg);

        try {
            response.getWriter().write(objectMapper.writeValueAsString(commonErrorMessage));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void success(HttpServletResponse response, Object data) {
        // 设置状态码
        response.setStatus(200);
        // 设置数据为json格式
        response.setContentType("application/json;charset=UTF-8");
        
        try {
            response.getWriter().write(objectMapper.writeValueAsString(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void success(HttpServletResponse response, String key, String value) {
        // 设置状态码
        response.setStatus(200);
        // 设置数据为json格式
        response.setContentType("application/json;charset=UTF-8");
        
        Map<String,String> data = new HashMap<>(1);
        data.put(key,value);
        
        try {
            response.getWriter().write(objectMapper.writeValueAsString(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
