package com.winkel.qualityevaluation.entity;
/*
  @ClassName LocationReport
  @Description
  @Author winkel
  @Date 2022-03-30 13:49
  */

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("locationreport")
public class LocationReport {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("year")
    private Integer year;

    @TableField("location_code")
    private String locationCode;

    @TableField("location_name")
    private String locationName;

    @TableField("file_name")
    private String fileName;

    @TableField("file_path")
    private String filePath;

    @TableField("upload_time")
    private LocalDateTime uploadTime;

}
