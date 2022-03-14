package com.winkel.qualityevaluation.security.authorization;

import com.winkel.qualityevaluation.security.AuthenticationToken;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.AbstractAccessDecisionManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * @ClassName MyAccessDecisionManager
 * @Description 由拦截器调用，负责鉴定用于是否有访问资源的权限
 * @Author zyx
 * @Date 2020/4/17 18:32
 * @Blog www.winkelblog.top
 */
//@Component
public class MyAccessDecisionManager extends AbstractAccessDecisionManager {

    public MyAccessDecisionManager(List<AccessDecisionVoter<?>> decisionVoters) {
        super(decisionVoters);
    }

//    @Bean
//    public List<AccessDecisionVoter<?>> list() {.
//        return Arrays.asList(new MyAccessDecisionVoter(), new WebExpressionVoter(), new AuthenticatedVoter());
//    }


    /**
     * @param authentication   当前正在请求受包含对象的Authentication
     * @param object           FilterInvocation 受保护对象，可以是一个MethodInvocation、JoinPoint或FilterInvocation
     * @param configAttributes 本次访问需要的权限，即上一步的 MyFilterInvocationSecurityMetadataSource 中查询得到的权限列表
     * @return void
     * @description 决定是否有访问权限
     * @params [authentication, object, configAttributes]
     */
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        System.out.println("MyAccessDecisionManager");
        System.out.println("MyAccessDecisionManager: configAttributes"+ configAttributes);
        Collection<GrantedAuthority> authorities = ((AuthenticationToken) authentication).getAuthorities();

        for ( ConfigAttribute configAttribute : configAttributes ) {
            //当前请求需要的权限
            String roleNeed = (String.valueOf(configAttribute)).split("'")[1];
            //遍历当前用户拥有的权限
            for ( GrantedAuthority authority : authorities ) {
                if (roleNeed.trim().equals(authority.getAuthority()) || "ROLE_ANONYMOUS".equals(roleNeed)) {
                    return;
                }
            }
        }
        throw new AccessDeniedException("无访问权限");
    }

    /**
     * @return boolean
     * @description 当前AccessDecisionManager是否支持对应的ConfigAttribute
     * @params [attribute]
     */
    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    /**
     * @return boolean
     * @description 当前AccessDecisionManager是否支持对应的受保护对象类型
     * @params [clazz]
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

}
