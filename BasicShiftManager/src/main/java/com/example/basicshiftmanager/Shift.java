package com.example.basicshiftmanager;

public class Shift {
    private int startHour;
    private int endHour;
    private int startMinute;
    private int endMinute;

    public Shift() {
        this.startHour = -1;
        this.endHour = -1;
        this.startMinute = -1;
        this.endMinute = -1;
    }
    public Shift(int startHour, int startMinute, int endHour, int endMinute) {
        this.startHour = startHour;
        this.endHour = endHour;
        this.startMinute = startMinute;
        this.endMinute = endMinute;
    }
    public float getShiftDuration(){
        return hoursCounter()+((float)minutesCounter()/60);
    }
    public int hoursCounter(){
        if(startHour>endHour) return 24-startHour+endHour;
        else return endHour-startHour;
    }
    public int minutesCounter() {
        if (startMinute > endMinute) return startMinute - endMinute - 60;
        else return endMinute - startMinute;
    }
    public int getStartHour() {
        return startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public String getFormattedStartHour() {
        if (startHour == -1 || startMinute == -1) {
            return ""; // Zwraca pusty ciąg znaków, jeśli godzina lub minuta ma wartość -1
        }
        return String.format("%02d:%02d", startHour, startMinute);
    }
    public String getFormattedEndHour() {
        if (endHour == -1 || endMinute == -1) {
            return ""; // Zwraca pusty ciąg znaków, jeśli godzina lub minuta ma wartość -1
        }
        return String.format("%02d:%02d", endHour, endMinute);
    }
}
