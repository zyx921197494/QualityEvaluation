package com.winkel.qualityevaluation.security;

import com.winkel.qualityevaluation.util.SecurityResponseUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName MyAuthenticationFailureHandler
 * @Description 认证失败处理器
 * @Author zyx
 * @Date 2020/4/14 19:42
 * @Blog www.winkelblog.top
 */
@Component
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        System.out.println("MyAuthenticationFailureHandler");
        SecurityResponseUtil.fail(response, 401, exception.getMessage());
    }
}
