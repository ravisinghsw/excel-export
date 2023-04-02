package com.ravi.excel.pojo;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

@Setter
@Getter
public class DataType {

    private String declareHeaderVal;

    private String fieldName;

    private Method accessor;

    private Class type;

    private String childObj;

    private Class clazz;



}
