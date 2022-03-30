package com.winkel.qualityevaluation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winkel.qualityevaluation.dao.AuthorityDao;
import com.winkel.qualityevaluation.dao.LocationDao;
import com.winkel.qualityevaluation.dao.SchoolDao;
import com.winkel.qualityevaluation.dao.UserDao;
import com.winkel.qualityevaluation.entity.Authority;
import com.winkel.qualityevaluation.entity.Location;
import com.winkel.qualityevaluation.entity.School;
import com.winkel.qualityevaluation.entity.User;
import com.winkel.qualityevaluation.service.api.UserService;
import com.winkel.qualityevaluation.util.Const;
import com.winkel.qualityevaluation.util.RandomUtil;
import com.winkel.qualityevaluation.vo.UserAuthority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

    @Resource
    private UserDao userDao;

    @Resource
    private LocationDao locationDao;

    @Resource
    private SchoolDao schoolDao;

    @Resource
    private AuthorityDao authorityDao;

    @Override
    public int checkPassword(String username, String password) {
        return userDao.checkPassword(username, password);
    }

    @Override
    public List<Authority> getAuthorities(String username) {
        return userDao.selectAuthorities(username);
    }

    @Override
    public boolean createAdmins() {
        QueryWrapper<Location> queryWrapper = new QueryWrapper<Location>().select("code", "type").in("type", 1, 2, 3);  // 查找所有省市县
        List<Location> locationList = locationDao.selectList(queryWrapper);
        if (!locationList.isEmpty()) {
            log.info("创建管理员账号 {} 个", locationList.size());
            List<User> userList = new ArrayList<>(locationList.size());
            List<UserAuthority> userAuthorities = new ArrayList<>(locationList.size());

            for (Location location : locationList) {            // 为每个地区创建一个的管理员
                String userId = RandomUtil.randomString(11);
                userList.add(new User()
                        .setId(userId)
                        .setUsername(RandomUtil.randomNums(6))
                        .setPassword(RandomUtil.randomNums(8))
                        .setName("")
                        .setLocationCode(location.getCode())
                        .setIsLocked(0)
                        .setCreateTime(LocalDateTime.now()));
                Integer authorityId;
                if (location.getType() == 1) {                  // 地区类型为省
                    authorityId = Const.ROLE_ADMIN_PROVINCE;
                } else if (location.getType() == 2) {           // 市
                    authorityId = Const.ROLE_ADMIN_CITY;
                } else {                                        // 县
                    authorityId = Const.ROLE_ADMIN_COUNTY;
                }
                userAuthorities.add(new UserAuthority().setUserId(userId).setAuthorityId(authorityId));  // 为每个地区的管理员添加对应的权限
                break; // todo 这里只创建一个管理员测试
            }

            return this.saveBatch(userList) && authorityDao.insertUserAuthorityBatch(userAuthorities);
        }
        return false;
    }

    @Override
    public boolean createRegisterUsers(List<School> schoolList, int authorityType) {
        List<User> userList = new ArrayList<>();
        List<UserAuthority> userAuthorities = new ArrayList<>();
        for (School school : schoolList) {
            String userId = RandomUtil.randomString(11);
            userList.add(new User()
                    .setId(userId)
                    .setUsername(RandomUtil.randomNums(6))
                    .setPassword(RandomUtil.randomNums(8))
                    .setName("")
                    .setSchoolCode(school.getCode())
                    .setIsLocked(0)
                    .setCreateTime(LocalDateTime.now()));
            userAuthorities.add(new UserAuthority().setUserId(userId).setAuthorityId(authorityType));
            break;
        }
        return this.saveBatch(userList) && authorityDao.insertUserAuthorityBatch(userAuthorities);
    }

    @Override
    public boolean createNotRegisterUsers(String locationCode, int num) {
        ArrayList<User> userList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            userList.add(new User(RandomUtil.randomString(11), RandomUtil.randomNums(6), RandomUtil.randomNums(8), locationCode, 0, LocalDateTime.now()));
        }
        return this.saveBatch(userList);
    }

    @Override
    public boolean changeUserPassword(String schoolCode, Integer authorityId, String newPwd, Integer currentCycle) {
        return userDao.updateUserPassword(schoolCode, authorityId, newPwd, currentCycle);
    }

    @Override
    public boolean unlockSelfUserByLocationCode(String locationCode) {
        return userDao.unlockSelfUserByLocationCode(locationCode);
    }

    @Override
    public boolean unlockUserBySchoolCode(String schoolCode, Integer type) {
        return userDao.unlockUserBySchoolCodeAndType(schoolCode, type);
    }

    @Override
    public boolean lockUserBySchoolCodeAndType(String schoolCode, Integer type) {
        return userDao.lockUserBySchoolCodeAndType(schoolCode, type);
    }
}
