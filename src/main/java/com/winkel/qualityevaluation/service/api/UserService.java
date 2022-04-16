package com.winkel.qualityevaluation.service.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.winkel.qualityevaluation.entity.Authority;
import com.winkel.qualityevaluation.entity.School;
import com.winkel.qualityevaluation.entity.User;
import com.winkel.qualityevaluation.pojo.vo.AccountVo;

import java.util.List;

public interface UserService extends IService<User> {

    int checkPassword(String username, String password);

    List<Authority> getAuthorities(String username);

    boolean createAdmins();

    boolean createRegisterUsers(List<School> schools, Integer authorityId);

    boolean createNotRegisterUsers(String locationCode, int num, Integer authorityId);

    boolean changeUserPassword(String schoolCode, Integer authorityId, String newPwd, Integer currentCycle);

    boolean unlockSelfUserByLocationCode(String locationCode);

    boolean unlockUserBySchoolCode(String schoolCode, Integer type);

    /**
     * desc: 锁定普通评估账户(非园长、非组长)
     * params: [schoolCode, type]
     * return: boolean
     * exception:
     **/
    boolean lockUserBySchoolCodeAndType(String schoolCode, Integer type);

    /**
     * desc: 导出不同类型的用户
     * params: [schoolCode, authorityId]
     * return: java.util.List<com.winkel.qualityevaluation.vo.AccountVo>
     * exception:
     **/
    List<AccountVo> getAccountBySchoolCodeAndAuthorityType(String schoolCode, Integer authorityId);
    
    /**
     * desc: 由任务id查询该任务的所有评估用户
     * params: [taskId]
     * return: java.util.List<com.winkel.qualityevaluation.entity.User>
     * exception:
     **/
    List<User> getAllUserByTaskId(Integer taskId);

}
