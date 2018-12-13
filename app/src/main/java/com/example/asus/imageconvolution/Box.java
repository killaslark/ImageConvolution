package com.example.asus.imageconvolution;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

/**
 * Created by Winarto on 11/23/2018.
 */

public class Box {
    public float left;
    public float right;
    public float top;
    public float bottom;
    public float size;

    public Box(){
        left = -1;
        right = -1;
        top = -1;
        bottom = -1;
    }

    public Box(float x,float y){
        left = x;
        right = x;
        top = y;
        bottom = y;
    }

    public Box(float top, float bottom, float left, float right){
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }

    public Bitmap drawBox(Bitmap bitmap, int color){
        Log.d("Position", Float.toString(left));
        int left = (int)this.left;
        int right = (int)this.right;
        int top = (int)this.top;
        int bottom = (int)this.bottom;
        int topEye = top + Math.round((bottom-top)*0.25f);
        int bottomEye = top + Math.round((bottom-top)*0.5f);
        if(left != -1 && right != -1 && top != -1 && bottom != -1) {
            for (int i = left; i <= right; i++) {
                for (int j = top; j <= bottom; j++) {
                    if (i == left || i == right || j == top || j == bottom || j == topEye || j == bottomEye) {
                        bitmap.setPixel((int) i, (int) j, color);
                    }
                }
            }
        }
        return bitmap;
    }

    public Bitmap drawBox(Bitmap bitmap, int offsetX, int offsetY, int color){
        Log.d("Position", Float.toString(left));
        int left = offsetX+(int)this.left;
        int right = offsetX+(int)this.right;
        int top = offsetY+(int)this.top;
        int bottom = offsetY+(int)this.bottom;
        if(left != -1 && right != -1 && top != -1 && bottom != -1) {
            for (int i = left; i <= right; i++) {
                for (int j = top; j <= bottom; j++) {
                    if (i == left || i == right || j == top || j == bottom) {
                        bitmap.setPixel((int) i, (int) j, color);
                    }
                }
            }
        }
        return bitmap;
    }

    public boolean valid(float size){
        return ((right-left)*(bottom-top)) > size;
    }

    public float getSize(){
        return (right-left)*(bottom-top);
    }
}
