package com.winkel.qualityevaluation.entity.evaluate;
/*
  @ClassName EvaluateIndex2
  @Description
  @Author winkel
  @Date 2022-03-16 11:06
  */

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
@TableName("evaluateindex2")
public class EvaluateIndex2 {

    @TableId("evaluate_index2_id")
    private Integer index2Id;

    @TableField("evaluate_index1_id")
    private Integer index1Id;

    @TableField("evaluate_index2_name")
    private String index2Name;

    @TableField("evaluate_index2_content")
    private String index2Content;
}
