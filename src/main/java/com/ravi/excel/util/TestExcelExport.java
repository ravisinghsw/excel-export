package com.ravi.excel.util;

import com.ravi.excel.pojo.Address;
import com.ravi.excel.pojo.Student;
import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestExcelExport {

    public static void main(String[] args) throws Exception {

        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Start Process");
        String userName = myObj.nextLine();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<Student> students = IntStream.rangeClosed(1,50000)
                .mapToObj(a-> {
                    Student student = new Student();
                    student.setId(""+a);
                    student.setName("Ravi"+a);
                    student.setGpa(a*0.5f);
                    student.setEmail(a+"xyz@gmail.com");
                    student.setStudentNumber(Integer.hashCode(a));
                    student.setCourseList(Arrays.asList("Maths"+a,"English"+a));

                    Address address = new Address();
                    address.setAddress(a+"-85 Quay Wharf");
                    address.setCity("BARKING");
                    address.setZipCode("IG1145"+a);

                    student.setAddress(address);
                    return student;
                })
                .collect(Collectors.toList());


        //ExportUtil.exportData(students);
       // ExportUtil.exportDataPrint(students);
        stopWatch.stop();
        System.out.println("TimeTaken :: "+stopWatch.getLastTaskTimeMillis() +" ms");

        System.out.println("End Process");
        myObj.nextLine();
    }
}
