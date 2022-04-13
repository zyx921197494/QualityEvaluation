package com.winkel.qualityevaluation.controller;

/*
  @ClassName UserController
  @Description
  @Author winkel
  @Date 2022-03-14 14:16
  */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.winkel.qualityevaluation.entity.Authority;
import com.winkel.qualityevaluation.entity.User;
import com.winkel.qualityevaluation.exception.AccountException;
import com.winkel.qualityevaluation.exception.AuthorityNotFoundException;
import com.winkel.qualityevaluation.service.impl.UserServiceImpl;
import com.winkel.qualityevaluation.util.Const;
import com.winkel.qualityevaluation.util.JWTUtil;
import com.winkel.qualityevaluation.util.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/login")
    public ResponseUtil login(@RequestBody User loginUser) {
        System.out.println("登录。。。。。。");
        String username = loginUser.getUsername();
        String password = loginUser.getPassword();
        if (!StringUtils.isNotBlank(username) || !StringUtils.isNotBlank(password)) {
            return new ResponseUtil(500, "用户名或密码不能为空");
        }

        //数据库检查登录用户合法性
        int result = userService.checkPassword(username, password);

        if (result > 0) {
            User user = userService.getOne(new QueryWrapper<User>().select("id", "is_locked").eq("username", username));
            if (user.getIsLocked() != 0) {
                throw new LockedException("账户已被锁定");
            }
            Map<String, Object> claims = new HashMap<>(3);
            claims.put("username", username);
            claims.put("password", password);
            claims.put("id", user.getId());

            //数据库查找当前用户权限
            List<Authority> authorities = userService.getAuthorities(username);
            System.out.println("authorities = " + authorities);
            if (authorities.isEmpty()) {
                throw new AuthorityNotFoundException("查找权限失败");
            }
            claims.put("authorities", authorities);

            Map<String, Object> tokenMap = JWTUtil.createJWT(claims);
            tokenMap.put("token_type", getTokenType(authorities.get(0)));
            //TODO 存入Redis
            System.out.println("登录成功");

//            //用户认证
//            AuthenticationToken authentication = new AuthenticationToken(String.valueOf(tokenMap.get("JWT")), authorities, new User(username, password));
//            //存储认证信息
//            SecurityContextHolder.getContext().setAuthentication(authentication);

            return new ResponseUtil(200, "登陆成功", tokenMap);
        }
        return new ResponseUtil(500, "用户名或密码错误");
    }

    private String getTokenType(Authority authoritie) {
        Integer id = authoritie.getId();
        if (id > 0 && id < 5) {
            return Const.TOKEN_TYPE_ADMIN;
        } else if (id > 4 && id < 10) {
            return Const.TOKEN_TYPE_USER;
        } else {
            return Const.TOKEN_TYPE_LEADER;
        }
    }

}
