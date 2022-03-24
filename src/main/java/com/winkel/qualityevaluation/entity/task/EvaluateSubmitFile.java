package com.winkel.qualityevaluation.entity.task;
/*
  @ClassName SubmitFile
  @Description 评估问题附件
  @Author winkel
  @Date 2022-03-19 16:41
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
@TableName("evaluatesubmitfiles")
public class EvaluateSubmitFile {

    @TableId(value = "submit_file_id", type = IdType.INPUT)
    private Integer id;

    @TableField("evaluate_task_id")
    private Integer taskId;

    @TableField("submit_file_name")
    private String fileName;

    @TableField("submit_file_path")
    private String filePath;

    @TableField("submit_file_size")
    private Integer size;

    @TableField("submit_file_upload_time")
    private LocalDateTime uploadTime;

    @TableField("submit_file_memo")
    private String memo;

}
