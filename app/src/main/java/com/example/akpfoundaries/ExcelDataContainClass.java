package com.example.akpfoundaries;

public class ExcelDataContainClass {
    String date;
    String time;
    int batch;
    int temp;

    public ExcelDataContainClass(String date, String time, int batch, int temp) {
        this.date = date;
        this.time = time;
        this.batch = batch;
        this.temp = temp;
    }

    public ExcelDataContainClass(DataValuesModelClass dataValuesModelClass, String date, String time){
        this.date=date;
        this.time=time;
        batch= dataValuesModelClass.getBatch();
        temp= dataValuesModelClass.getY();
    }

    public ExcelDataContainClass() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getBatch() {
        return batch;
    }

    public void setBatch(int batch) {
        this.batch = batch;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }
}
