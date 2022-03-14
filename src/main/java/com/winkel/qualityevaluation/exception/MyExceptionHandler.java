package com.winkel.qualityevaluation.exception;


import com.winkel.qualityevaluation.util.ResponseUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @ClassName MyExceptionHandler
 * @Description 异常处理器
 * @Author zyx
 * @Date 2020/4/15 17:26
 * @Blog www.winkelblog.top
 */

@ResponseBody
@ControllerAdvice
public class MyExceptionHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return false;
    }

    //supports()为true时，在每个返回值中添加Object
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        return ResponseUtil.response(200, "OK", body);
    }

    @ExceptionHandler(AuthorityNotFoundException.class)
    public ResponseUtil handleAuthorityNotFoundException(Exception e) {
        return ResponseUtil.response(401, e.getMessage(), null);
    }

    @ExceptionHandler(AccountException.class)
    public ResponseUtil handleAccountException(Exception e) {
        return ResponseUtil.response(403, e.getMessage(), null);
    }


    @ExceptionHandler(LockedException.class)
    public ResponseUtil handleLockedException(Exception e) {
        return ResponseUtil.response(403, e.getMessage(), null);
    }


}
