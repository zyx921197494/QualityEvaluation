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
import com.winkel.qualityevaluation.util.RandomUtil;
import com.winkel.qualityevaluation.util.ResponseUtil;
import com.winkel.qualityevaluation.vo.UserAuthority;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        QueryWrapper<Location> queryWrapper = new QueryWrapper<Location>().select("code", "type").in("type", 1, 2, 3);
        List<Location> locationList = locationDao.selectList(queryWrapper);
        if (!locationList.isEmpty()) {
            System.out.println("产生管理员账号：" + locationList.size());
            List<User> userList = new ArrayList<>(locationList.size());
            List<UserAuthority> userAuthorities = new ArrayList<>(locationList.size());

            for (Location location : locationList) {
                String userId = RandomUtil.randomString(11);
                userList.add(new User()
                        .setId(userId)
                        .setUsername(RandomUtil.randomNums(6))
                        .setPassword(RandomUtil.randomNums(8))
                        .setLocationCode(location.getCode())
                        .setIsLocked(0)
                        .setCreateTime(LocalDateTime.now()));
                userAuthorities.add(new UserAuthority().setUserId(userId).setAuthorityId(location.getType()));
                break;
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
                    .setSchoolCode(school.getCode())
                    .setIsLocked(0)
                    .setCreateTime(LocalDateTime.now()));
            userAuthorities.add(new UserAuthority().setUserId(userId).setAuthorityId(authorityType));
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


}
