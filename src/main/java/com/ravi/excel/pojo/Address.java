package com.ravi.excel.pojo;

import com.ravi.excel.export.ExcelData;
import com.ravi.excel.export.ExcelField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class Address implements ExcelData, Serializable {

    @ExcelField("ADDRESS")
    private String address;

    @ExcelField("ZIP_CODE")
    private String zipCode;

    @ExcelField("CITY")
    private String city;


}
