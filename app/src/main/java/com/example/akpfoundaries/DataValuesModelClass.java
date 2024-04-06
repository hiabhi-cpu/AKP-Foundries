package com.example.akpfoundaries;

public class DataValuesModelClass {

    int x;
    int y;
    int batch;

    public DataValuesModelClass(int x, int y,int batch) {
        this.x = x;
        this.y = y;
        this.batch=batch;
    }

    public DataValuesModelClass(){

    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getBatch() {
        return batch;
    }

    public void setBatch(int batch) {
        this.batch = batch;
    }
}
