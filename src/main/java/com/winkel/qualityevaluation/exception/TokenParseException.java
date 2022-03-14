package com.winkel.qualityevaluation.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @ClassName TokenParseException
 * @Description Token解析异常
 * @Author zyx
 * @Date 2020/4/16 12:46
 * @Blog www.winkelblog.top
 */
public class TokenParseException extends AuthenticationException {
    public TokenParseException(String msg) {
        super(msg);
    }
}
