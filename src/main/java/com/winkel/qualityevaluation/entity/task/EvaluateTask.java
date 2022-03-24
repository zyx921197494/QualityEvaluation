package com.winkel.qualityevaluation.entity.task;
/*
  @ClassName EvaluateTask
  @Description 评价任务定义
  一个Task对应一个周期内一个学校的一次自评/督评/自评
  @Author winkel
  @Date 2022-03-16 11:31
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

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("evaluatetask")
public class EvaluateTask implements Serializable {

    @TableId(value = "evaluate_task_id", type = IdType.INPUT)
    private Integer id;  //评估

    @TableField("school_code")
    private String schoolCode;  //评估的学校

    @TableField("evaluate_task_name")
    private String name;  //任务名

    @TableField("evaluate_id")
    private Integer evaluateId;  //评价体系id

    @TableField("evaluate_task_start_time")
    private LocalDateTime startTime;

    @TableField("evaluate_task_end_time")
    private LocalDateTime endTIme;

    @TableField("evaluate_task_content")
    private String content;

    // 评估任务的状态：
    // 自评->未开始、评估中、数据已提交、报告已提交(自评完成)、报告审核通过、报告审核未通过
    // 督评->未开始、评估中、数据已提交、报告已提交、报告审核通过(督评完成)、报告审核未通过
    @TableField("task_status")
    private Integer status;

    @TableField("task_cycle")
    private Integer cycle; //评估任务的周期

    @TableField("task_type")
    private Integer type; //任务是自评/督评/县复评/市复评/省复评

    @TableField("task_is_locked")
    private Integer isLocked;  //该项评估任务是否已完成

}
