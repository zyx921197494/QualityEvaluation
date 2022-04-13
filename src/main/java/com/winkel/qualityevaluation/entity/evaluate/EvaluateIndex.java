package com.winkel.qualityevaluation.entity.evaluate;
/*
  @ClassName EvaluateIndex
  @Description 评价体系设置
  @Author winkel
  @Date 2022-03-16 11:01
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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("tevaluateindex")
public class EvaluateIndex {

    @TableId(value = "evaluate_id", type = IdType.AUTO)
    private Integer evaluateId;

    @TableField("evaluate_name")
    private String evaluateName;

    @TableField("evaluate_memo")
    private String evaluateMemo;

}
