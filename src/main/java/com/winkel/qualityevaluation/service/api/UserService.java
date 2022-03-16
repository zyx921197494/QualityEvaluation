package com.winkel.qualityevaluation.service.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.winkel.qualityevaluation.entity.Authority;
import com.winkel.qualityevaluation.entity.School;
import com.winkel.qualityevaluation.entity.User;

import java.util.List;

public interface UserService extends IService<User> {

    int checkPassword(String username, String password);

    List<Authority> getAuthorities(String username);

    boolean createAdmins();

    boolean createRegisterUsers(List<School> schools);

    boolean createNotRegisterUsers(String locationCode, int num);

}
