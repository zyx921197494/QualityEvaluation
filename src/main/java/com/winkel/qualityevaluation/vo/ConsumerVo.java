package com.winkel.qualityevaluation.vo;
/*
  @ClassName ConsumerVo
  @Description
  @Author winkel
  @Date 2022-04-05 20:36
  */

import com.winkel.qualityevaluation.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class ConsumerVo {

    private List<User> userList;

    private Integer taskId;

}
