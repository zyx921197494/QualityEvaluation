package com.winkel.qualityevaluation.vo;
/*
  @ClassName LocationVo
  @Description
  @Author winkel
  @Date 2022-04-14 14:48
  */

import com.winkel.qualityevaluation.entity.Location;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class LocationVo {

    private String value;  // locationCode
    private String label;  // locationName;

    private List<LocationVo> list;  // 下一级location

}
