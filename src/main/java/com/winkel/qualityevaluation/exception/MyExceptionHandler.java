package com.winkel.qualityevaluation.exception;


import com.winkel.qualityevaluation.util.ResponseUtil;
import com.winkel.qualityevaluation.util.SecurityResponseUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

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

    @ResponseBody
    @ExceptionHandler(AuthorityNotFoundException.class)
    public ResponseUtil handleAuthorityNotFoundException(Exception e) {
        return ResponseUtil.response(401, e.getMessage(), null);
    }

    @ResponseBody
    @ExceptionHandler(AccountException.class)
    public ResponseUtil handleAccountException(Exception e) {
        return new ResponseUtil(500, e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(LockedException.class)
    public ResponseUtil handleLockedException(Exception e) {
        return ResponseUtil.response(500, e.getMessage(), null);
    }

    @ResponseBody
    @ExceptionHandler(ExcelException.class)
    public ResponseUtil handleExcelException(Exception e) {
        return ResponseUtil.response(500, e.getMessage(), null);
    }

    @ResponseBody
    @ExceptionHandler(TaskException.class)
    public ResponseUtil handleTaskException(Exception e) {
        return ResponseUtil.response(500, e.getMessage(), null);
    }

    @ResponseBody
    @ExceptionHandler(NullPointerException.class)
    public ResponseUtil handleNullPointerException(Exception e) {
        return ResponseUtil.response(500, "空指针异常", null);
    }

    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseUtil handleMissingServletRequestParameterException(Exception e) {
        return ResponseUtil.response(500, ((MissingServletRequestParameterException) e).getMessage(), null);
    }

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseUtil handleConstraintViolationException(Exception e) {
        return ResponseUtil.response(500, ((MethodArgumentNotValidException) e).getBindingResult().getFieldError().getDefaultMessage(), null);
    }

    @ResponseBody
    @ExceptionHandler(ConsumerException.class)
    public ResponseUtil handleConsumerException(Exception e) {
        return ResponseUtil.response(500, e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    public ResponseUtil handleRuntimeException(Exception e) {
        return ResponseUtil.response(500, e.getMessage());
    }

}
