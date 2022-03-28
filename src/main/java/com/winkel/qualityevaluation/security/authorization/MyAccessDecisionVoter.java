package com.winkel.qualityevaluation.security.authorization;

import com.winkel.qualityevaluation.security.AuthenticationToken;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @ClassName MyAccessDecisionVoter
 * @Description 鉴权Voter
 * @Author zyx
 * @Date 2020/4/17 19:19
 * @Blog www.winkelblog.top
 */

public class MyAccessDecisionVoter implements AccessDecisionVoter<Object> {

    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        System.out.println("MyAccessDecisionVoter");
        //匿名用户
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return ACCESS_DENIED;
        }

        Collection<GrantedAuthority> authorities = ((AuthenticationToken) authentication).getAuthorities();
        System.out.println("当前权限" + authorities);
        System.out.println("需要权限" + attributes);

        //需要的权限
        for (ConfigAttribute attribute : attributes) {
            if (attribute == null) {
                continue;
            }
            String[] split = (String.valueOf(attribute)).split("'");
            attribute = new SecurityConfig(split[1]);
            //现有的权限
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals(attribute.getAttribute())) {
                    System.out.println("投票通过");
                    return ACCESS_GRANTED;
                }
            }
        }
        System.out.println("投票不通过");
        return ACCESS_ABSTAIN;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
