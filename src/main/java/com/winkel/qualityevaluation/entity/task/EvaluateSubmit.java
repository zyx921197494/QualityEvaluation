package com.winkel.qualityevaluation.entity.task;
/*
  @ClassName EvaluateSubmit
  @Description 评估问题填写
  @Author winkel
  @Date 2022-03-16 12:03
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
@TableName("evaluatesubmit")
public class EvaluateSubmit implements Serializable {

    @TableId(value = "submit_id",type = IdType.INPUT)
    private Integer id;

    @TableField("evaluate_task_id")
    private Integer taskId;

    @TableField("submit_time")
    private LocalDateTime submitTime;

    @TableField("evaluate_index3_id")
    private Integer index3Id;

    @TableField("submit_content")
    private String content;  // 单选/多选的结果

    @TableField("submit_is_locked")
    private Integer isLocked;

    @TableField("score")
    private Integer score;  // index3问题对应的得分

}
