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

    public Bitmap drawBox(Bitmap bitmap){
        Log.d("Position", Float.toString(left));
        if(left != -1 && right != -1 && top != -1 && bottom != -1) {
            for (float i = left; i <= right; i++) {
                for (float j = top; j <= bottom; j++) {
                    if (i == left || i == right || j == top || j == bottom) {
                        bitmap.setPixel((int) i, (int) j, Color.RED);
                    }
                }
            }
        }
        return bitmap;
    }

    public boolean valid(float size){
        return ((right-left)*(bottom-top)) > size;
    }
}
