package com.winkel.qualityevaluation.security;

import com.winkel.qualityevaluation.util.SecurityResponseUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName MyLogoutSuccessHandler
 * @Description 退出登录成功处理器
 * @Author zyx
 * @Date 2020/4/17 21:36
 * @Blog www.winkelblog.top
 */

public class MyLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        SecurityResponseUtil.fail(response,"退出登录成功");
    }
}
