package com.winkel.qualityevaluation.security;

import com.winkel.qualityevaluation.util.SecurityResponseUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * @ClassName MyAuthenticationSuccessHandler
 * @Description 认证成功处理器
 * @Author zyx
 * @Date 2020/4/14 19:41
 * @Blog www.winkelblog.top
 */

@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("MyAuthenticationSuccessHandler");
        AuthenticationToken authenticationToken = (AuthenticationToken) authentication;
        String jwt = authenticationToken.getJwt();

        HashMap<String, Object> result = new HashMap<>(2);
        result.put("token", jwt);
        result.put("user",authenticationToken.getDetails());

        SecurityResponseUtil.success(response, result);

    }
}
