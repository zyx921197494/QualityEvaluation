package com.winkel.qualityevaluation.util;
/*
  @ClassName PwdUtil
  @Description
  @Author winkel
  @Date 2022-03-16 14:19
  */

import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

public class RandomUtil {

    public final static String string = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * desc: 随机数字序列
     * params: [length]
     * return: java.lang.String
     * exception:
     **/
    public static String randomNums(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append((RandomUtils.nextInt(0, 10)));
        }
        return builder.toString();
    }

    /**
     * desc: 随机大小写字母序列
     * params: [length]
     * return: java.lang.String
     * exception:
     **/
    public static String randomString(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(string.charAt(RandomUtils.nextInt(0,26)));
        }
        return builder.toString();
    }

}