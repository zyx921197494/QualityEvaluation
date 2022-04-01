package com.winkel.qualityevaluation.vo;
/*
  @ClassName SchoolVo
  @Description
  @Author winkel
  @Date 2022-04-01 14:37
  */

import com.winkel.qualityevaluation.entity.School;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SchoolVo extends School {

    private String locationTypeName;  // 驻地城乡类型名称

    private String typeName;  // 办学类型名称

    private String hostName;  // 举办者名称

}
