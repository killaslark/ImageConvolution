package com.example.asus.imageconvolution;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static java.lang.Math.abs;

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
            Log.d("CONTROLPOINTS", "" + points[i].toString());
            Point from = new Point(offsetX+points[i].x, offsetY+points[i].y);
            int j = i == points.length-1 ? 0 : i+1;
            Point to = new Point(offsetX+points[j].x, offsetY+points[j].y);
            tempBitmap = drawLine(tempBitmap, from, to,color);
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

    private Bitmap drawLine(Bitmap bitmap, Point from, Point to, int color){
        int x0 = from.x; int x1 = to.x;
        int y0 = from.y; int y1 = to.y;
        int dx = abs(x1-x0), sx = x0<x1 ? 1 : -1;
        int dy = abs(y1-y0), sy = y0<y1 ? 1 : -1;
        int err = (dx>dy ? dx : -dy)/2, e2;

        for(;;){
            bitmap.setPixel(x0,y0,color);
            if (x0==x1 && y0==y1)
                break;
            e2 = err;
            if (e2 >-dx) { err -= dy; x0 += sx; }
            if (e2 < dy) { err += dx; y0 += sy; }
        }
        return bitmap;
    }
}
