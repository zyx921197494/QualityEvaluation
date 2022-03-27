package com.winkel.qualityevaluation.service.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.winkel.qualityevaluation.entity.Authority;
import com.winkel.qualityevaluation.entity.School;
import com.winkel.qualityevaluation.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserService extends IService<User> {

    int checkPassword(String username, String password);

    List<Authority> getAuthorities(String username);

    boolean createAdmins();

    boolean createRegisterUsers(List<School> schools, int authorityType);

    boolean createNotRegisterUsers(String locationCode, int num);

    boolean changeUserPassword(String schoolCode, Integer authorityId, String newPwd, Integer currentCycle);

    boolean unlockSelfUserByLocationCode(String locationCode);

    boolean unlockUserBySchoolCode(String schoolCode, Integer type);

    /**
     * desc: 锁定普通评估账户(非园长、组长)
     * params: [schoolCode, type]
     * return: boolean
     * exception:
     **/
    boolean lockUserBySchoolCodeAndType(String schoolCode, Integer type);

}
