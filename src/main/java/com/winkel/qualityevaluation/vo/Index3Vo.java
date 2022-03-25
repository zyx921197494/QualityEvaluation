package com.winkel.qualityevaluation.vo;
/*
  @ClassName Index3Vo
  @Description
  @Author winkel
  @Date 2022-03-25 16:59
  */

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Index3Vo {

    private String index3Name;

    private String index3Content;  //问题

    private String type;  // 1判断(A好B坏 20分) 2单选(0、10、20、30分) 3多选(满分40分) 4文本

    private String memo;  // 备注

    private String content; //选择的结果

    private LocalDateTime submitTime;

}
