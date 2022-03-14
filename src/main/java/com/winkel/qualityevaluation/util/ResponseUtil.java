package com.winkel.qualityevaluation.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @ClassName ResponseUtil
 * @Description
 * @Author zyx
 * @Date 2020/4/24 17:02
 * @Blog www.winkelblog.top
 */

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseUtil {

    private int statusCode;

    private String message;

    private Object data;

    public static ResponseUtil response(int statusCode, String message, Object data) {
       return new ResponseUtil(statusCode,message, data);
    }

}
