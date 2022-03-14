package com.winkel.qualityevaluation.security.authorization;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.winkel.qualityevaluation.entity.Authority;
import com.winkel.qualityevaluation.exception.AuthorityNotFoundException;
import com.winkel.qualityevaluation.service.api.AuthorityService;
import lombok.SneakyThrows;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @ClassName MySecurityMetadataSource
 * @Description 获取当前请求需要的访问权限
 * @Author zyx
 * @Date 2020/4/17 18:42
 * @Blog www.winkelblog.top
 */

@Component
public class MySecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    //TODO 获取数据库中所有权限Service

    @Autowired
    private AuthorityService authorityService;

    public static MySecurityMetadataSource mySecurityMetadataSource;

    @PostConstruct
    public void init() {
        mySecurityMetadataSource = this;
        mySecurityMetadataSource.authorityService = this.authorityService;
    }

    //("/addmin", "ROLE_ADMIN")

//    static private Map<String, Collection<ConfigAttribute>> map = new HashMap<>();
//
//    static {
//        ConfigAttribute configAttribute1 = new SecurityConfig("ROLE_ADMIN");
//        ArrayList<ConfigAttribute> collection1 = new ArrayList<>();
//        collection1.add(configAttribute1);
//        ConfigAttribute configAttribute2 = new SecurityConfig("ROLE_USER");
//        ArrayList<ConfigAttribute> collection2 = new ArrayList<>();
//        collection1.add(configAttribute2);
//
//        map.put("/admin/**", collection1);
//        map.put("/user/**", collection2);
//    }


    /**
     * @return java.util.Collection<org.springframework.security.access.ConfigAttribute>
     * @description 判定用户请求的需要的权限
     * 如果在权限表中，则返回给DecisionManger的decide()方法，用来判定用户是否有此权限
     * 如果不在权限表中则放行
     * getAttributes(Object o)方法返回null的，意味着当前这个请求不需要任何角色就能访问，甚至不需要登录
     * @params [object]
     */

    @SneakyThrows
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        System.out.println("MySecurityMetadataSource");

        String requestUrl = ((FilterInvocation) object).getRequestUrl();
        AntPathMatcher pathMatcher = new AntPathMatcher();

        Map<String, Collection<ConfigAttribute>> map = loadAllAuthority();
        if (map.isEmpty()) {
            throw new AuthorityNotFoundException("查找权限失败");
        }
        System.out.println("查找数据库所有权限成功" + map);
        for ( String url : map.keySet() ) {
            if (pathMatcher.match(url, requestUrl)) {
                return map.get(url);
            }
        }

        return null;
    }

    //加载数据库中所有权限
    public Map<String, Collection<ConfigAttribute>> loadAllAuthority() {
        List<Authority> authorities = authorityService.list(new QueryWrapper<Authority>().select("distinct url", "authority"));
        Map<String, Collection<ConfigAttribute>> map = new HashMap<>(3);

        for ( Authority authority : authorities ) {
            ConfigAttribute configAttribute = new SecurityConfig(authority.getAuthority());
            ArrayList<ConfigAttribute> collection = new ArrayList<>();
            collection.add(configAttribute);
            map.put(authority.getUrl(), collection);
        }
        return map;
    }

    //返回了定义的权限列表
    //Spring Security会在启动时校验每个ConfigAttribute是否配置正确
    //如果不需要校验，方法体直接返回null
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    //返回类对象是否支持校验
    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }


    @Test
    public void test() {
        System.out.println(authorityService == null);
        Collection<Object> auuthorities = authorityService.getMap(new QueryWrapper<Authority>().select("distinct authority")).values();
        System.out.println(auuthorities);
    }
}
