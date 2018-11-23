package com.example.asus.imageconvolution;

/**
 * Created by Winarto on 11/23/2018.
 */

public class Label {
    int label;
    float x, y;

    public Label(){
        this.x = 0;
        this.y = 0;
        this.label = 0;
    }

    public Label(float x, float y) {
        this.x = x;
        this.y = y;
        this.label = 0;
    }

    public Label(float x, float y, int label) {
        this.x = x;
        this.y = y;
        this.label = label;
    }

    public void setPoint(float x, float y){
        this.x = x;
        this.y = y;
    }

    public void setLabel(int label){
        this.label = label;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getLabel(){
        return label;
    }
}
