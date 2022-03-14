package com.winkel.qualityevaluation.security;

import com.winkel.qualityevaluation.util.SecurityResponseUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName MyAccessDeniedHandler
 * @Description 权限不足，拒绝访问处理器
 * @Author zyx
 * @Date 2020/4/14 19:41
 * @Blog www.winkelblog.top
 */
public class MyAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
//        String message = accessDeniedException.getMessage();
        System.out.println("MyAccessDeniedHandler");
        SecurityResponseUtil.fail(response, 403, "权限不足，无法访问此接口");
    }

}
