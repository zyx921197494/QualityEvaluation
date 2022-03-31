package com.winkel.qualityevaluation.entity;
/*
  @ClassName School
  @Description 学校（机构）
  @Author winkel
  @Date 2022-03-16 11:15
  */

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

@TableName("tschool")
public class School {

    @JSONField(name = "标识码")
    @TableId(value = "school_code")
    private String code;  //标识码

    @JSONField(name = "学校名称")
    @TableField("school_name")
    private String name;  //名称

    @JSONField(name = "地址")
    @TableField("school_location")
    private String location;  //详细地址

    @JSONField(name = "地址代码")
    @TableField("school_location_code")
    private String locationCode;  //地址代码

    @JSONField(name = "驻地城乡类型代码")
    @TableField("school_location_type_code")
    private String locationTypeCode;  //驻地城乡类型代码

    @JSONField(name = "办学类型代码")
    @TableField("school_type_code")
    private String typeCode;  //办学类型代码

    @JSONField(name = "举办者代码")
    @TableField("school_host_code")
    private String hostCode;  //举办者代码

    @JSONField(name = "是否普惠")
    @TableField("is_generally_beneficial")
    private Integer isGenerallyBeneficial;  //是否普惠

    @JSONField(name = "是否乡镇中心学校")
    @TableField("is_central")
    private Integer isCentral;

    @JsonIgnore
    @TableField("is_register")
    private Integer isRegister;  //是否在册

    @JsonIgnore
    @TableField("is_locked")
    private Integer isLocked;  // 是否锁定


}
