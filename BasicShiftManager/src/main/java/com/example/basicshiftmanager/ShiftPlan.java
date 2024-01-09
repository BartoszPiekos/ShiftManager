package com.example.basicshiftmanager;

import java.time.format.DateTimeFormatter;

public class ShiftPlan {

    private Shift[] employeeShiftPlan = new Shift[31];
    private float monthlyHours;
    private String data;


    public ShiftPlan(Shift[] employeeShiftPlan) {
        this.employeeShiftPlan = employeeShiftPlan;
    }

    public float getMonthlyHours(){
        for (int i=0;i<employeeShiftPlan.length;i++) {
            monthlyHours += employeeShiftPlan[i].getShiftDuration();
        }
    return monthlyHours;
    }
    public String getData(){
        return data;
    }
    public void saveShiftToFile(){
        for(int i=0;i<employeeShiftPlan.length;i++){
        data = String.format("%d\t%d\t%d\t%d\n",
                employeeShiftPlan[i].getStartHour(),
                employeeShiftPlan[i].getStartMinute(),
                employeeShiftPlan[i].getEndHour(),
                employeeShiftPlan[i].getEndMinute());

        }

    }

}
