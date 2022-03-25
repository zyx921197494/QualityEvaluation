package com.winkel.qualityevaluation.exception;
/*
  @ClassName ExcelException
  @Description
  @Author winkel
  @Date 2022-03-25 12:15
  */

import java.io.FileNotFoundException;
import java.io.IOException;

public class ExcelException extends RuntimeException {

    public ExcelException(String msg) {
        super(msg);
    }

}
