package com.winkel.qualityevaluation.util;

/**
 * @ClassName UploadStatus
 * @Description
 * @Author zyx
 * @Date 2020-10-19 17:45
 * @Blog www.winkelblog.top
 */

public enum UploadStatus {

    DONE("done"),
    ERROR("error"),
    LOAD("load");


    private final String status;

    UploadStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}
