package com.winkel.qualityevaluation.pojo.vo;
/*
  @ClassName AccountVo
  @Description
  @Author winkel
  @Date 2022-03-31 18:10
  */

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class AccountVo {

    private String username;

    private String password;

    @JsonIgnore
    private Integer authorityId;

    private String accountType;

}
