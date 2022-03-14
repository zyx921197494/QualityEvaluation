package com.winkel.qualityevaluation.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.winkel.qualityevaluation.entity.Authority;
import com.winkel.qualityevaluation.entity.User;
import com.winkel.qualityevaluation.exception.AuthorityNotFoundException;
import com.winkel.qualityevaluation.service.api.UserService;
import com.winkel.qualityevaluation.service.impl.UserDetailsServiceImpl;
import com.winkel.qualityevaluation.util.JWTUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName JWTAuthenticationProvider
 * @Description 具体执行认证逻辑
 * 该类注入Spring容器后不需要在SecurityConfig中配置，否则会重复调用
 * @Author zyx
 * @Date 2020/4/14 20:00
 * @Blog www.winkelblog.top
 */

@Component
public class JWTAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        System.out.println("JWTAuthenticationProvider");

        AuthenticationToken authenticationToken = ((AuthenticationToken) authentication);
        User jwtUser = (User) authenticationToken.getDetails();
        //账户是否被锁定
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Integer isLocked = userService.getOne(new QueryWrapper<User>().select("is_locked").eq("username", user.getUsername())).getIsLocked();
        if (isLocked != 0) {
            throw new LockedException("22222222账户已被锁定");
        }

        UserDetails dbUser = userDetailsService.loadUserByUsername(jwtUser.getUsername());

        //查询数据库验证用户
        if (dbUser != null && dbUser.getUsername().equals(jwtUser.getUsername()) && dbUser.getPassword().equals(jwtUser.getPassword())) {
            System.out.println("用户通过验证");
            //加入用户权限
            List<Authority> authorities = userService.getAuthorities(jwtUser.getUsername());
            if (authorities.isEmpty()) {
                throw new AuthorityNotFoundException("查找权限失败");
            }
            AuthenticationToken token = new AuthenticationToken(authenticationToken.getJwt(), authorities, dbUser);
            token.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(token);
            System.out.println("存储token到SecurityContextHolder成功");
            return token;
        }
        return null;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return AuthenticationToken.class.isAssignableFrom(authentication);
    }


    @Test
    public void test() {
//        Map<String, Object> claims = new HashMap<>(3);
//
//        List<Authority> authorities = new ArrayList<>();
//        authorities.add(new Authority("ROLE_ADMIN"));
//        authorities.add(new Authority("ROLE_USER"));
//
//        claims.put("username", "111");
//        claims.put("password", "222");
//        claims.put("authorities",authorities);
//        Map<String, Object> map = JWTUtil.createJWT(claims);
//
//        String jwt = String.valueOf(map.get("JWT"));
//        System.out.println(jwt);
//        System.out.println(JWTUtil.parseJWTAuthorities("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJXaW5rZWwiLCJwYXNzd29yZCI6IjIyMiIsImV4cCI6MTU4NzU1NzY5OCwiaWF0IjoxNTg3MTk3Njk4LCJhdXRob3JpdGllcyI6W3siYXV0aG9yaXR5IjoiUk9MRV9BRE1JTiJ9LHsiYXV0aG9yaXR5IjoiUk9MRV9VU0VSIn1dLCJ1c2VybmFtZSI6IjExMSJ9.hbFiDBv-C9HJ8PElo0eQh7NbaWKHBZOJ59drtIBI41M"));

    }
}