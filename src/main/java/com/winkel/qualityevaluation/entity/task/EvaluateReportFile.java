package com.winkel.qualityevaluation.entity.task;
/*
  @ClassName ReportFile
  @Description 提交各级意见书
  @Author winkel
  @Date 2022-03-19 19:34
  */

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
@TableName("evaluatereportfiles")
public class EvaluateReportFile {

    @TableId("report_file_id")
    private Integer id;

    @TableField("task_id")
    private Integer taskId;  //对应的评估任务id

    @TableField("report_file_name")
    private String fileName;

    @TableField("report_file_path")
    private String filePath;

    @TableField("report_file_size")
    private Integer size;

    @TableField("report_file_upload_time")
    private LocalDateTime uploadTime;

    @TableField("report_file_memo")
    private String memo;

}
