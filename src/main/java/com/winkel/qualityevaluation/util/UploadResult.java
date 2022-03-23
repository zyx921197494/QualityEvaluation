package com.winkel.qualityevaluation.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @ClassName PicUploadResult
 * @Description
 * @Author zyx
 * @Date 2020-10-19 17:20
 * @Blog www.winkelblog.top
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadResult {

    private String uid;

    private String filename;

    private String url;

    private String status;

    private String msg;

}
