package com.ravi.excel.pojo;


import com.ravi.excel.export.ExcelData;
import com.ravi.excel.export.ExcelField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class Student implements ExcelData, Serializable {

    @ExcelField("ID")
    private String id;

    @ExcelField("NAME")
    private String name;

    @ExcelField("STUDENT_NUMBER")
    private long studentNumber;

    @ExcelField("EMAIL")
    private String email;

    @ExcelField("COURSES")
    private List<String> courseList;

    @ExcelField("GPA")
    private float gpa;

    @ExcelField(value = "ADDRESS",isChild = "Y",type = Address.class)
    private Address address;


    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", studentNumber=" + studentNumber +
                ", email='" + email + '\'' +
                ", courseList=" + courseList +
                ", gpa=" + gpa +
                '}';
    }
}
