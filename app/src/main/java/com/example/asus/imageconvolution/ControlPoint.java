package com.example.asus.imageconvolution;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Winarto on 12/13/2018.
 */

public class ControlPoint {
    Point[] points;

    public ControlPoint(){
        points = new Point[8];
    }

    public Bitmap drawControlPoint(Bitmap bitmap, int offsetX, int offsetY, int color){
        Bitmap tempBitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true);
        for(int i = 0; i < points.length;i++){
            tempBitmap.setPixel(offsetX+points[i].x, offsetY+points[i].y, color);
        }
        return tempBitmap;
    }
}
