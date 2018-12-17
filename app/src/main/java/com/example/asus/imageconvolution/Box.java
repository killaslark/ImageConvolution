package com.example.asus.imageconvolution;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

/**
 * Created by Winarto on 11/23/2018.
 */

public class Box {
    public float left;
    public float right;
    public float top;
    public float bottom;
    public float size;
    private ControlPoint edges;
    final private int[][] nbrs = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1} ,{-1, 1} ,{-1, 0}, {-1, -1}, {0, -1}};

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

    public Bitmap drawBox(Bitmap bitmap, int offsetX, int offsetY, int color){
        Bitmap tempBitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true);
        Log.d("Position", Float.toString(left));
        int left = offsetX+(int)this.left;
        int right = offsetX+(int)this.right;
        int top = offsetY+(int)this.top;
        int bottom = offsetY+(int)this.bottom;
        if(left != -1 && right != -1 && top != -1 && bottom != -1) {
            for (int i = left; i <= right; i++) {
                for (int j = top; j <= bottom; j++) {
                    if (i == left || i == right || j == top || j == bottom) {
                        tempBitmap.setPixel((int) i, (int) j, color);
                    }
                }
            }
        }
        return tempBitmap;
    }

    public boolean valid(float size){
        return ((right-left)*(bottom-top)) > size;
    }

    public float getSize(){
        return (right-left)*(bottom-top);
    }

    public ControlPoint getEdges(Bitmap bitmap, int offsetX, int offsetY){
        if(edges == null){
            Log.d("CONTORLPOINT", "INSIDE");
            edges = getControlPoint(bitmap, offsetX, offsetY);
        }
        return edges;
    }

    public boolean haveEdges(){
        return (left != -1 && right != -1 && top != -1 && bottom != -1);
    }

    private ControlPoint getControlPoint(Bitmap bitmap, int offsetX, int offsetY){
        ControlPoint controlPoint = new ControlPoint();
        int left = offsetX+(int)this.left;
        int right = offsetX+(int)this.right;
        int top = offsetY+(int)this.top;
        int bottom = offsetY+(int)this.bottom;
        int halfVertical = (int)(this.top+((this.bottom-this.top)/2));
        int halfHorizontal = (int)(this.left+((this.right-this.left)/2));
        Log.d("TOPCOORDINATE", "" + this.top);
        Log.d("BOTTOMCOORDINATE", "" + this.bottom);
        Log.d("HALFCOORDINATE", "" + halfVertical);
        Point centerPoint = new Point(offsetX+halfHorizontal, offsetY+halfVertical);
        controlPoint.points[0] = getFirstWhitePixel(bitmap, centerPoint, new Point(left, offsetY+halfVertical), offsetX, offsetY);
        controlPoint.points[1] = getFirstWhitePixel(bitmap, centerPoint, new Point(left, top), offsetX, offsetY);;
        controlPoint.points[2] = getFirstWhitePixel(bitmap, centerPoint, new Point(offsetX+halfHorizontal, top), offsetX, offsetY);
        controlPoint.points[3] = getFirstWhitePixel(bitmap, centerPoint, new Point(right, top), offsetX, offsetY);;
        controlPoint.points[4] = getFirstWhitePixel(bitmap, centerPoint, new Point(right, offsetY+halfVertical), offsetX, offsetY);
        controlPoint.points[5] = getFirstWhitePixel(bitmap, centerPoint, new Point(right, bottom), offsetX, offsetY);
        controlPoint.points[6] = getFirstWhitePixel(bitmap, centerPoint, new Point(offsetX+halfHorizontal, bottom), offsetX, offsetY);
        controlPoint.points[7] = getFirstWhitePixel(bitmap, centerPoint, new Point(left, bottom), offsetX, offsetY);
        return controlPoint;
    }

    private Point getFirstWhitePixel(Bitmap bitmap, Point from, Point to, int offsetX, int offsetY){
        int x0 = from.x; int x1 = to.x;
        int y0 = from.y; int y1 = to.y;
        int dx = abs(x1-x0), sx = x0<x1 ? 1 : -1;
        int dy = abs(y1-y0), sy = y0<y1 ? 1 : -1;
        int err = (dx>dy ? dx : -dy)/2, e2;

        for(;;){
            int color = bitmap.getPixel(x0,y0);
            if(color == Color.WHITE)
                break;
            else if (x0==x1 && y0==y1)
                break;
            e2 = err;
            if (e2 >-dx) { err -= dy; x0 += sx; }
            if (e2 < dy) { err += dx; y0 += sy; }
        }
        return new Point(x0-offsetX,y0-offsetY);
    }

//    @Override
//    public boolean equals(Object obj) {
//        if (!(obj instanceof Box)) {
//            return false;
//        }
//        Box obj2 = (Box)obj;
//        return (
//                obj2.left == left &&
//                obj2.top == top &&
//                obj2.right == right &&
//                obj2.bottom == bottom
//                );
//    }
}
