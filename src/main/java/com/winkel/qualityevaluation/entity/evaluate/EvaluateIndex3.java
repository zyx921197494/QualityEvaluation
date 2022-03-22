package com.winkel.qualityevaluation.entity.evaluate;
/*
  @ClassName EvaluateIndex3
  @Description
  @Author winkel
  @Date 2022-03-16 11:07
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
@TableName("evaluateindex3")
public class EvaluateIndex3 {

    @TableId("evaluate_index3_id")
    private Integer index3Id;

    @TableField("evaluate_index2_id")
    private Integer index2Id;

    @TableField("evaluate_index3_name")
    private String index3Name;

    @TableField("evaluate_index3_content")
    private String index3Content;  //问题

    @TableField("evaluate_type")
    private Integer type;  // 1判断(A好B坏 20分) 2单选(0、10、20、30分) 3多选(满分40分) 4文本

    @TableField("evaluate_length")
    private Integer length;  //长度限制

    @TableField("evaluate_memo")
    private String memo;  // 备注

}
