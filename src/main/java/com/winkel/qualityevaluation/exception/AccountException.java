package com.winkel.qualityevaluation.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @ClassName AccountException
 * @Description 用户名或密码异常
 * @Author zyx
 * @Date 2020/4/17 16:12
 * @Blog www.winkelblog.top
 */

public class AccountException extends AuthenticationException {
    public AccountException(String message) {
        super(message);
    }
}
