package com.example.asus.imageconvolution;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Winarto on 12/13/2018.
 */

public class ControlPoint {
    Point[] points;

    public ControlPoint(){
        points = new Point[8];
    }

    public ControlPoint(Point[] p) { points = p; }

    public Bitmap drawControlPoint(Bitmap bitmap, int offsetX, int offsetY, int color){
        Bitmap tempBitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true);
        for(int i = 0; i < points.length;i++){
            tempBitmap.setPixel(offsetX+points[i].x, offsetY+points[i].y, color);
        }
        return tempBitmap;
    }

    public boolean validateSumDistance(ControlPoint c) {
        float sum = calculateSumDistance(c);
        if (sum < 40) {
            return true;
        } else
            return false;
    }

    public float calculateSumDistance(ControlPoint c) {
        float sum = 0;
        for(int i = 0; i < points.length; i++) {
            sum += Math.sqrt(Math.pow(points[i].x - c.points[i].x, 2) + Math.pow(points[i].y - c.points[i].y , 2));
        }
        return sum;
    }
}
