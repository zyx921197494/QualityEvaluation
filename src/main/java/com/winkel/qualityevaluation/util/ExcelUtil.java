package com.winkel.qualityevaluation.util;
/*
  @ClassName ExcelUtil
  @Description
  @Author winkel
  @Date 2022-03-18 12:20
  */


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.winkel.qualityevaluation.entity.School;
import lombok.SneakyThrows;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

public class ExcelUtil {

    /**
     * 获取Excel，将数据转换成 List<T> 的形式
     * Excel 数据要求第一行第一列起为对象的属性名称
     *
     * @param filePath  文件路径
     * @param fileName  文件名称
     * @param sheetName sheet名称
     * @param tClass    要转换成的实体类
     * @param <T>       泛型参数
     * @return List对象数组
     */
    public static <T> List<T> readListFromExcel(String filePath, String fileName, String sheetName, Class<T> tClass) throws IOException {
        List<String> resultMapList = new ArrayList<>();
        File file = new File(filePath + File.separator + fileName);
        FileInputStream inputStream = new FileInputStream(file);
        // 使用工厂模式 根据文件扩展名 创建对应的Workbook
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheet(sheetName);
        int rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();
        JSONObject jsonObject;
        Map<String, Object> resultMap;
        for (int i = 1; i < rowCount + 1; i++) {
            Row row = sheet.getRow(i);
            resultMap = new HashMap<>();
            for (int j = 0; j < row.getLastCellNum(); j++) {
                if (Objects.equals(row.getCell(j).getCellType(), CellType.STRING)) {
                    resultMap.put(sheet.getRow(0).getCell(j).toString(), row.getCell(j).getStringCellValue());
                } else if (Objects.equals(row.getCell(j).getCellType(), CellType.NUMERIC)) {
                    resultMap.put(sheet.getRow(0).getCell(j).toString(), row.getCell(j).getNumericCellValue());
                } else {
                    resultMap.put(sheet.getRow(0).getCell(j).toString(), row.getCell(j));
                }
            }
            jsonObject = new JSONObject(resultMap);
            resultMapList.add(jsonObject.toJSONString());
        }
        System.out.println(resultMapList);
        return JSONArray.parseArray(resultMapList.toString(), tClass);
    }

//    static <T> void writeToExcel(List<T> list, String path, boolean writeTitle) throws Exception {
//        if (list == null || list.size() == 0) return;
//        Workbook workbook = new HSSFWorkbook();
//        int offset = writeTitle ? 1 : 0;
//        FileOutputStream fos = null;
//
//        Sheet sheet = workbook.createSheet();
//        for (int i = 0; i < list.size() + offset; ++i) {
//            if (writeTitle && i == 0) {
//                createTitle(list, sheet);
//                continue;
//            }
//            Row row = sheet.createRow(i);
//            T bean = list.get(i - offset);
//            Field[] fields = bean.getClass().getDeclaredFields();
//            for (int j = 0; j < fields.length; j++) {
//                Field field = fields[j];
//                field.setAccessible(true);
//                Cell cell = row.createCell(j);
//                //Date,Calender都可以 使用  +"" 操作转成字符串
//                cell.setCellValue(field.get(bean) + "");
//            }
//        }
//         fos = new FileOutputStream(path);
//        workbook.write(fos);
//
//        fos.close();
//        workbook.close();
//    }

    public static <T> void writeExcel(List<T> list, String filePath, boolean writeTitle) throws Exception {
        OutputStream out;
        File file = new File(filePath);
        Workbook workbook = getWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        int begin = 0;
        // 添加表头
        if (writeTitle) {
            ++begin;
            Field[] fields = list.get(0).getClass().getDeclaredFields();
            Row title = sheet.createRow(0);
            for (int i = 0; i < fields.length; i++) {
                Cell cell = title.createCell(i);
                cell.setCellValue(fields[i].getName());
            }
        }

        for (int i = 0; i < list.size(); i++) { // 每个对象
            Row row = sheet.createRow(i + begin);
            T bean = list.get(i);
            Field[] fields = bean.getClass().getDeclaredFields();
            for (int j = 0; j < fields.length; j++) { // 对象中的每个属性
                Cell cell = row.createCell(j);
                fields[j].setAccessible(true);
                cell.setCellValue(fields[j].get(bean).toString());
            }
        }

        out = new FileOutputStream(filePath);
        workbook.write(out);

    }

//    public static <T> void createTitle(List<T> beans, Sheet sheet) {
//        Row row = sheet.createRow(0);
//        T bean = beans.get(0);
//        Field[] fields = bean.getClass().getDeclaredFields();
//        for (int i = 0; i < fields.length; i++) {
//            Field field = fields[i];
//            field.setAccessible(true);
//            Cell cell = row.createCell(i);
//            cell.setCellValue(field.getName());
//        }
//    }

    public static Workbook getWorkbook(File file) throws IOException {
        Workbook workbook = null;
        FileInputStream in = new FileInputStream(file);
        if (file.getName().endsWith("xls")) {  // Excel 2003
            workbook = new HSSFWorkbook(in);
        } else if (file.getName().endsWith("xlsx")) {  // Excel 2007/2010
            workbook = new XSSFWorkbook(in);
        }
        return workbook;
    }


    @SneakyThrows
    @Test
    public void test() {
        System.out.println(readListFromExcel("F:\\", "school.xlsx", "Sheet1", School.class));

        List<School> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(new School()
                    .setCode("666666" + i)
                    .setName(String.valueOf(i))
                    .setLocation("11")
                    .setLocationCode("666")
                    .setLocationTypeCode("4654")
                    .setTypeCode("213132")
                    .setHostCode("111")
                    .setIsRegister(1)
                    .setIsGenerallyBeneficial(1)
            );
        }
//        writeExcel(list, "F:\\school.xlsx", true);

    }


}
