package com.winkel.qualityevaluation.security;
/*
  @ClassName MyCorsFilter
  @Description
  @Author winkel
  @Date 2022-04-12 19:32
  */

import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class MyCorsFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        System.out.println("MyCorsFilter");
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        response.setHeader("Access-Control-Allow-Origin", "*");  // Origin为“*”则Credentials必须为false
        response.setHeader("Access-Control-Allow-Methods", "POST,OPTIONS,GET");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Authorization,accept,x-requested-with,Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "false");
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(200);
            System.out.println("检测到options请求");
            return;
        }
        chain.doFilter(req, res);
    }

}
