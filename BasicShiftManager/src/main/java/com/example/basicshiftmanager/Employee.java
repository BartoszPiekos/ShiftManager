package com.example.basicshiftmanager;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Employee {
    private String id;
    private String firstName;
    private String lastName;
    private String pesel;
    private String city;
    private String address;
    private String post;
    private Date birthDate;
    private float monthlyHours;
    private Shift[] employeeShiftPlan = new Shift[31];


    public Employee(String id, String firstName, String lastName, String pesel, String city, String address, String post, Date birthDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.pesel = pesel;
        this.city = city;
        this.address = address;
        this.post = post;
        this.birthDate = birthDate;
        for(int i=0;i<employeeShiftPlan.length;i++) {
            this.employeeShiftPlan[i] = new Shift();

        }

    }
    public void readShiftDataFromFile(String fileName) {
        Path path = Paths.get(fileName);
        int lineCount = 0;

        if (Files.exists(path)) {
            try {
                List<String> lines = Files.readAllLines(path);

                for (String line : lines) {
                    if (lineCount >= 31) {
                        break; // Przerywa pętlę po przeczytaniu 31 linii
                    }

                    String[] parts = line.split("\t");
                    if (parts.length == 4) {
                        int startHour = Integer.parseInt(parts[0]);
                        int startMinute = Integer.parseInt(parts[1]);
                        int endHour = Integer.parseInt(parts[2]);
                        int endMinute = Integer.parseInt(parts[3]);

                        employeeShiftPlan[lineCount] = new Shift(startHour, startMinute, endHour, endMinute);
                    } else {
                        employeeShiftPlan[lineCount] = new Shift(-1, -1, -1, -1);
                    }

                    lineCount++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (int i = lineCount; i < 31; i++) {
            employeeShiftPlan[i] = new Shift(-1, -1, -1, -1);
        }
    }
    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPesel() {
        return pesel;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public String getPost() {
        return post;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }
    public float getMonthlyHours(){
        for (int i=0;i<employeeShiftPlan.length;i++) {
            monthlyHours += employeeShiftPlan[i].getShiftDuration();
        }
        return monthlyHours;
    }

    public Shift[] getEmployeeShiftPlan() {
        return employeeShiftPlan;
    }
}
