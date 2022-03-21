package com.winkel.qualityevaluation.entity.task;
/*
  @ClassName EvaluateSubmit
  @Description 评估问题填写
  @Author winkel
  @Date 2022-03-16 12:03
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
@TableName("evaluatesubmit")
public class EvaluateSubmit {

    @TableId("submit_id")
    private Integer id;

    @TableId("evaluate_task_id")
    private Integer taskId;

    @TableId("submit_time")
    private LocalDateTime submitTime;

    @TableId("submit_index3_id")
    private Integer index3Id;

    @TableId("submit_content")
    private String content;

    @TableId("submit_is_locked")
    private Integer isLocked;

    @TableField("selected")
    private String selected;  // 单选/多选的结果

    @TableField("score")
    private Integer score;  // index3问题对应的得分

}
