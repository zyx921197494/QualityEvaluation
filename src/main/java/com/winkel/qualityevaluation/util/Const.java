package com.winkel.qualityevaluation.util;
/*
  @ClassName Const
  @Description
  @Author winkel
  @Date 2022-03-21 15:08
  */

public class Const {

    // 任务状态
    public final static Integer TASK_NOT_START = 1;
    public final static Integer TASK_IN_EVALUATION = 2;
    public final static Integer TASK_DATA_SUBMITTED = 3;
    public final static Integer TASK_REPORT_SUBMITTED = 4;
    public final static Integer TASK_REPORT_ACCEPTED = 5;
    public final static Integer TASK_REPORT_REFUSED = 6;

    // 任务种类
    public final static Integer TASK_TYPE_SELF = 1;
    public final static Integer TASK_TYPE_SUPERVISOR = 2;
    public final static Integer TASK_TYPE_COUNTY = 3;
    public final static Integer TASK_TYPE_CITY = 4;
    public final static Integer TASK_TYPE_PROVINCE = 5;

    // 用户、记录锁定状态
    public final static Integer NOT_LOCKED = 0;
    public final static Integer LOCKED = 1;

    // 角色分类
    public final static Integer ROLE_ADMIN_PROVINCE = 1;
    public final static Integer ROLE_ADMIN_CITY = 2;
    public final static Integer ROLE_ADMIN_COUNTY = 3;
    public final static Integer ROLE_ADMIN_EXPERT = 4;
    public final static Integer ROLE_EVALUATE_SELF = 5;
    public final static Integer ROLE_EVALUATE_SUPERVISOR = 6;
    public final static Integer ROLE_EVALUATE_COUNTY = 7;
    public final static Integer ROLE_EVALUATE_CITY = 8;
    public final static Integer ROLE_EVALUATE_PROVINCE = 9;
    public final static Integer ROLE_EVALUATE_LEADER_SELF = 10;
    public final static Integer ROLE_EVALUATE_LEADER_SUPERVISOR = 11;
    public final static Integer ROLE_EVALUATE_LEADER_COUNTY = 12;
    public final static Integer ROLE_EVALUATE_LEADER_CITY = 13;
    public final static Integer ROLE_EVALUATE_LEADER_PROVINCE = 14;

}
