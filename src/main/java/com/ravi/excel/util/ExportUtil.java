package com.ravi.excel.util;

import com.ravi.excel.export.ExcelData;
import com.ravi.excel.export.ExcelField;
import com.ravi.excel.pojo.DataType;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ReflectionUtils;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ExportUtil {

    private static final Map<String, List<DataType>> exportDataMap = new LinkedHashMap<>();

    static {
        String basePackage = "com.ravi.excel";
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        TypeFilter filter = new AssignableTypeFilter(ExcelData.class);
        scanner.addIncludeFilter(filter);

        for (BeanDefinition bd : scanner.findCandidateComponents(basePackage)) {
            try {
                Class<?> clazz = Class.forName(bd.getBeanClassName());
                exportDataMap.put(clazz.getName(), getDataType(clazz));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static List<DataType> getDataType(Class<?> clazz) {
        List<DataType> dataTypeList = new ArrayList<>();
        Map<String, Method> methodMap = new HashMap<>();
        Arrays.stream(clazz.getMethods()).forEach(a -> {
            methodMap.put(a.getName(), a);
        });
        ReflectionUtils.doWithFields(clazz, a -> {
            DataType dataType = new DataType();
            // System.out.println(a.getName());
            if (Objects.nonNull(a.getAnnotation(ExcelField.class))) {
                ExcelField excelField = a.getAnnotation(ExcelField.class);
                if ("N".equalsIgnoreCase(excelField.isChild())) {
                    dataType.setChildObj("N");
                } else {
                    dataType.setChildObj("Y");
                    dataType.setClazz(a.getType());
                }
                dataType.setFieldName(a.getName());
                dataType.setType(a.getType());
                String fieldName = a.getName();
                String capitalizedFieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                String getterName = "get" + capitalizedFieldName;
                dataType.setAccessor(methodMap.get(getterName));
                dataType.setDeclareHeaderVal(excelField.value());
                dataTypeList.add(dataType);
            }
        });
        return dataTypeList;
    }

    public static <T> void exportData(List<T> objectList) throws Exception {

        Workbook wb = new XSSFWorkbook();
        CreationHelper createHelper = wb.getCreationHelper();
        Sheet sheet = wb.createSheet("new sheet");

        Row headerRow = sheet.createRow(0);
        Object firstObj = objectList.get(0);

        List<DataType> dataTypeList = exportDataMap.get(firstObj.getClass().getName());
        log.info("************** Headers ******************");
        AtomicInteger headerCellCount = new AtomicInteger(0);
        dataTypeList.forEach(a -> {
            if ("Y".equalsIgnoreCase(a.getChildObj())) {
                List<DataType> childList = exportDataMap.get(a.getClazz().getName());
                childList.forEach(child -> {
                    System.out.print(child.getDeclareHeaderVal() + " ");
                    headerRow.createCell(headerCellCount.get()).setCellValue(child.getDeclareHeaderVal());
                    headerCellCount.getAndIncrement();
                });
            } else {
                System.out.print(a.getDeclareHeaderVal() + " ");
                headerRow.createCell(headerCellCount.get()).setCellValue(a.getDeclareHeaderVal());
            }
            headerCellCount.getAndIncrement();
        });
        log.info("**************** Header END ***********************");
        log.info("\n");
        AtomicInteger rowCount = new AtomicInteger(1);
        for (Object row : objectList) {
            Row dataRow = sheet.createRow(rowCount.get());
            AtomicInteger dataCellCount = new AtomicInteger(0);
            for (DataType dataType : dataTypeList) {
                if ("N".equalsIgnoreCase(dataType.getChildObj())) {
                    System.out.print(dataType.getAccessor().invoke(row) + " ");
                    dataRow.createCell(dataCellCount.get()).setCellValue(String
                            .valueOf(dataType.getAccessor().invoke(row)));
                } else {
                    Object childObj = dataType.getAccessor().invoke(row);
                    List<DataType> childDataTypeList = exportDataMap.get(childObj.getClass().getName());
                    for (DataType childDataType : childDataTypeList) {
                        System.out.print(childDataType.getAccessor().invoke(childObj) + " ");
                        dataRow.createCell(dataCellCount.get()).setCellValue(String.valueOf(childDataType.getAccessor()
                                .invoke(childObj)));
                        dataCellCount.getAndIncrement();
                    }
                }
                dataCellCount.getAndIncrement();
            }
            rowCount.getAndIncrement();
        }

        // Write the output to a file
        try (OutputStream fileOut = new FileOutputStream("workbook.xlsx")) {
            wb.write(fileOut);
        }

    }

    public static <T> void exportDataPrint(List<T> objectList) throws Exception {
        Object firstObj = objectList.get(0);
        List<DataType> dataTypeList = exportDataMap.get(firstObj.getClass().getName());
        log.info("************** Headers ******************");
        AtomicInteger headerCellCount = new AtomicInteger(0);
        dataTypeList.forEach(a -> {
            if ("Y".equalsIgnoreCase(a.getChildObj())) {
                List<DataType> childList = exportDataMap.get(a.getClazz().getName());
                childList.forEach(child -> {
                    System.out.print(child.getDeclareHeaderVal() + " ");
                });
            } else {
                System.out.print(a.getDeclareHeaderVal() + " ");
            }
            headerCellCount.getAndIncrement();
        });
        log.info("**************** Header END ***********************");
        log.info("\n");
        AtomicInteger rowCount = new AtomicInteger(1);
        for (Object row : objectList) {
            AtomicInteger dataCellCount = new AtomicInteger(0);
            for (DataType dataType : dataTypeList) {
                if ("N".equalsIgnoreCase(dataType.getChildObj())) {
                    System.out.print(dataType.getAccessor().invoke(row) + " ");
                } else {
                    Object childObj = dataType.getAccessor().invoke(row);
                    List<DataType> childDataTypeList = exportDataMap.get(childObj.getClass().getName());
                    for (DataType childDataType : childDataTypeList) {
                        System.out.print(childDataType.getAccessor().invoke(childObj) + " ");
                        dataCellCount.getAndIncrement();
                    }
                }
                dataCellCount.getAndIncrement();
            }
            rowCount.getAndIncrement();
        }
    }


}
