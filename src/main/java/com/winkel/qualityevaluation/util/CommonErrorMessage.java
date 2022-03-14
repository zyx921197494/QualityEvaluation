package com.winkel.qualityevaluation.util;

import lombok.Data;

@Data
public class CommonErrorMessage {
    
    /**
     * 错误代码
     */
    private Integer code;
    
    /**
     * 错误详细信息
     */
    private String msg;
    
    private CommonErrorMessage() {
    }
    
    public static CommonErrorMessage fail(String msg) {
        return fail(500,msg);
    }
    
    public static CommonErrorMessage fail(int code, String msg) {
        CommonErrorMessage commonErrorMessage = new CommonErrorMessage();
        commonErrorMessage.setCode(code);
        commonErrorMessage.setMsg(msg);
        return commonErrorMessage;
    }
}
