package com.winkel.qualityevaluation.security.authorization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.web.FilterInvocation;

import javax.servlet.*;
import java.io.IOException;


/**
 * @ClassName MySecurityInterceptor
 * @Description
 * @Author zyx
 * @Date 2020/4/17 18:46
 * @Blog www.winkelblog.top
 */
public class MySecurityInterceptor extends AbstractSecurityInterceptor implements Filter {

    @Autowired
    MySecurityMetadataSource metadataSource;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("MySecurityInterceptor");
        FilterInvocation filterInvocation = new FilterInvocation(request, response, chain);
        InterceptorStatusToken token = super.beforeInvocation(filterInvocation);
        try {
            filterInvocation.getChain().doFilter(filterInvocation.getRequest(), filterInvocation.getResponse());
        }catch (Exception e){
            System.out.println("MySecurityInterceptor异常");
        }
        finally {
            super.afterInvocation(token, null);
        }
    }



    @Autowired
    public void setMyAccesDecisionMaanager(MyAccessDecisionManager myAccessDecisionManager){
        super.setAccessDecisionManager(myAccessDecisionManager);
    }

    @Override
    public Class<?> getSecureObjectClass() {
        return FilterInvocation.class;
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return metadataSource;
    }

}
