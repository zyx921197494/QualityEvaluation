package com.winkel.qualityevaluation.security;

import com.winkel.qualityevaluation.util.SecurityResponseUtil;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName MyAuthenticationEntryPoint
 * @Description 未登录的匿名用户请求处理
 * @Author zyx
 * @Date 2020/4/14 19:42
 * @Blog www.winkelblog.top
 */
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        System.out.println("MyAuthenticationEntryPoint");
        if (authException instanceof LockedException) {
            System.out.println("执行MyAuthenticationEntryPoint");
            SecurityResponseUtil.fail(response, 403, authException.getMessage());
        } else {
            SecurityResponseUtil.fail(response, 401, "请先登录");
        }
    }
}
