package com.winkel.qualityevaluation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winkel.qualityevaluation.dao.AuthorityDao;
import com.winkel.qualityevaluation.entity.Authority;
import com.winkel.qualityevaluation.service.api.AuthorityService;
import org.springframework.stereotype.Service;

@Service
public class AuthorityServiceImpl extends ServiceImpl<AuthorityDao, Authority> implements AuthorityService {
}
