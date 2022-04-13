package com.winkel.qualityevaluation.entity.evaluate;
/*
  @ClassName EvaluateIndex1
  @Description
  @Author winkel
  @Date 2022-03-16 10:58
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
@TableName("evaluateindex1")
public class EvaluateIndex1 {

    @TableId(value = "evaluate_index1_id",type = IdType.AUTO)
    private Integer index1Id;
    @TableField("evaluate_id")
    private Integer evaluateIndexId;
    @TableField("evaluate_index1_name")
    private String index1Name;
    @TableField("evaluate_index1_content")
    private String index1Content;

}
