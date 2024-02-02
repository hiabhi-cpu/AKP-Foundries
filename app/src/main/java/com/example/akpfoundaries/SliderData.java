package com.example.akpfoundaries;

public class SliderData {

    private String imageuri;

    public SliderData(String uri) {
        this.imageuri = uri;
    }

    public SliderData() {
    }

    public String getUri() {
        return imageuri;
    }

    public void setUri(String uri) {
        this.imageuri = uri;
    }
}
