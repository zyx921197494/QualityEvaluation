package com.winkel.qualityevaluation.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @ClassName AuthorityNotFoundException
 * @Description 查找权限失败
 * @Author zyx
 * @Date 2020/4/22 14:24
 * @Blog www.winkelblog.top
 */

public class AuthorityNotFoundException extends AuthenticationException {
    public AuthorityNotFoundException(String msg) {
        super(msg);
    }
}
