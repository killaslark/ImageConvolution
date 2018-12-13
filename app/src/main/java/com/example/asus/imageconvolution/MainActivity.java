package com.example.asus.imageconvolution;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import static java.lang.Math.floor;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;

public class MainActivity extends AppCompatActivity {

    private ArrayList<double[][]> selectedKernel = new ArrayList<>();
    private List<Box> newBoundingBox = new ArrayList<>();
    private ImageView imageViewBefore,imageViewAfter;
    private EditText[][] matrixView = new EditText[3][3];
    private Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;
    private Bitmap bitmap,secondBitmap, bufferBitmap, drawBitmap;
    private int[][] beforeMinkowski;
    private int[][] afterMinkowski;
    private int labelCount = 0;
    private double[][] blur = {{0.0625, 0.125, 0.0625},
            {0.125, 0.25, 0.125},
            {0.0625, 0.125, 0.0625}};

    private double[][] gaussian_blur = {{0.0039, 0.015625, 0.0234375, 0.015625, 0.0039},
            {0.015625, 0.0625, 0.09375, 0.0625, 0.015625},
            {0.0234375, 0.09375, 0.140625, 0.09375, 0.0234375},
            {0.015625, 0.0625, 0.09375, 0.0625, 0.015625},
            {0.0039, 0.015625, 0.0234375, 0.015625, 0.0039}};

    private double[][] identity = {{0, 0, 0},
            {0, 1, 0},
            {0, 0, 0}};

    private double[][] edge_detection0 = {{1, 0, -1},
            {0, 0, 0},
            {-1, 0, 1}};

    private double[][] edge_detection1 =  {{0, 1, 0},
            {1, -4, 1},
            {0, 1, 0}};

    private double[][] outline =  {{-1, -1, -1},
            {-1, 8, -1},
            {-1, -1, -1}};

    private double[][] leftSobel =  {{1, 0, -1},
            {2, 0, -2},
            {1, 0, -1}};

    private double[][] rightSobel =  {{-1, 0, 1},
            {-2, 0, 2},
            {-1, 0, 1}};


    private double[][] topSobel =  {{1, 2, 1},
            {0, 0, 0},
            {-1, -2, -1}};

    private double[][] bottomSobel =  {{-1, -2, -1},
            {0, 0, 0},
            {1, 2, 1}};

    private double[][] sharpen =  {{0, -1, 0},
            {-1, 5, -1},
            {0, -1, 0}};

    private double[][] robert1 =  {{0, 1},
            {-1, 0}};

    private double[][] robert2 =  {{1, 0},
            {0, -1}};

    private double[][] prewitt_vertical =  {{-1, -1, -1},
            {0, 0, 0},
            {1, 1, 1}};

    private double[][] prewitt_horizontal =  {{1, 0, -1},
            {1, 0, -1},
            {1, 0, -1}};

    private double[][] custom_matrix =  {{0, 0, 0},
            {0, 0, 0},
            {0, 0, 0}};

    // Minkowski Kernel
    private int[][][] operand = {
            {{1, 1, 1},
             {1, 1, 1},
             {1, 1, 1}},

            {{0, 1, 0},
             {1, 1, 1},
             {0, 1, 0}},

            {{1, 1, 1, 1, 1},
             {1, 1, 1, 1, 1},
             {1, 1, 1, 1, 1},
             {1, 1, 1, 1, 1},
             {1, 1, 1, 1, 1}},

            {{0, 0, 1, 0, 0},
             {0, 1, 1, 1, 0},
             {1, 1, 1, 1, 1},
             {0, 1, 1, 1, 1},
             {0, 0, 1, 1, 0}},

            {{0, 0, 1, 1, 0, 0},
             {0, 1, 1, 1, 1, 0},
             {1, 1, 1, 1, 1, 1},
             {1, 1, 1, 1, 1, 1},
             {0, 1, 1, 1, 1, 0},
             {0, 0, 1, 1, 0, 0}}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button menu = (Button) findViewById(R.id.button_upload);
        Button feature = (Button) findViewById(R.id.button_features);
        imageViewBefore = (ImageView) findViewById(R.id.image_view_before);
        imageViewAfter = (ImageView) findViewById(R.id.image_view_after);
        matrixView[0][0] = (EditText) findViewById(R.id.m00);
        matrixView[0][1] = (EditText) findViewById(R.id.m10);
        matrixView[0][2] = (EditText) findViewById(R.id.m20);
        matrixView[1][0] = (EditText) findViewById(R.id.m01);
        matrixView[1][1] = (EditText) findViewById(R.id.m11);
        matrixView[1][2] = (EditText) findViewById(R.id.m21);
        matrixView[2][0] = (EditText) findViewById(R.id.m02);
        matrixView[2][1] = (EditText) findViewById(R.id.m12);
        matrixView[2][2] = (EditText) findViewById(R.id.m22);
//        for(int i = 0;i < matrixView.length;i++){
//            for (int j = 0;j < matrixView[0].length;j++){
//                matrixView[i][j] = (EditText) findViewById(getResources().getIdentifier("m"+Integer.toString(j)+Integer.toString(i), "layout", getPackageName()));
//                Log.d("OKAAY", Integer.toString(getResources().getIdentifier("m00", "layout", getBaseContext().getPackageName())));
//                //Log.d("OKAAY", Integer.toString(R.id.m00));
//            }
//        }

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        feature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectFeature();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Bundle bundle = data.getExtras();
                bitmap = (Bitmap) bundle.get("data");
                secondBitmap = bitmap;
                imageViewBefore.setImageBitmap(bitmap);
                bufferBitmap = null;

            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                InputStream inputStream;
                try {
                    inputStream = getContentResolver().openInputStream(selectedImageUri);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    secondBitmap = bitmap;
                    imageViewBefore.setImageBitmap(bitmap);
                    bufferBitmap = null;

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
                }
                imageViewBefore.setImageURI(selectedImageUri);
            }
        }
    }

    private Bitmap resizeBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int resizedWidth, resizedHeight;

        if (width >= height) {
            resizedWidth = Math.min(width, 300);
            resizedHeight = resizedWidth * height / width;
        } else {
            resizedHeight = Math.min(height, 400);
            resizedWidth = resizedHeight *  width / height;
        }

        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, true);
    }

    private  Bitmap resizeBitmap(Bitmap bitmap, int widthResized, int heightResized) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int resizedWidth, resizedHeight;

        if (width >= height) {
            resizedWidth = Math.min(width, widthResized);
            resizedHeight = resizedWidth * height / width;
        } else {
            resizedHeight = Math.min(height, heightResized);
            resizedWidth = resizedHeight *  width / height;
        }

        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, true);
    }

    private void erode(int _x, int _y, int size) {
        boolean fit = true;
        int mid = operand.length / 2;
        Log.d("MID is: ", Integer.toString(mid));
        for(int i = 0; i < operand[size].length; i++) {
            int x = _x + i - mid;
            for(int j = 0; j < operand[size].length; j++) {
                int y = _y + j - mid;
                if(x >= 0 && y >= 0 && x < beforeMinkowski.length && y < beforeMinkowski[0].length) {
                    if (operand[size][i][j] == 1) {
                        if (beforeMinkowski[x][y] != (operand[size][i][j] == 1 ? Color.WHITE : Color.BLACK))
                            fit = false;
                    }
                } else {
                    fit = false;
                }
                if (!fit) break;
            }
            if (!fit) break;
        }
        afterMinkowski[_x][_y] = fit ? Color.WHITE : Color.BLACK;
    }

    private Bitmap erosion(Bitmap input, int size) {
        Bitmap output = Bitmap.createBitmap(input.getWidth(), input.getHeight(), Bitmap.Config.ARGB_8888);
        final int height = input.getHeight();
        final int width = input.getWidth();

        beforeMinkowski = new int[width][height];
        afterMinkowski = new int[width][height];
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                beforeMinkowski[i][j] = input.getPixel(i, j);
                Log.d("Minkowski Info: ",Integer.toString(beforeMinkowski[i][j]));
            }
        }
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                if(beforeMinkowski[i][j] == Color.WHITE) {
                    erode(i, j, size);
                } else {
                    afterMinkowski[i][j] = beforeMinkowski[i][j];
                }
            }
        }
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                output.setPixel(i, j, afterMinkowski[i][j]);
                //Log.d("OUTPIXEL: ", Integer.toString(afterMinkowski[i][j]));
                //Log.d("PIXEL: ", Integer.toString(beforeMinkowski[i][j]));
            }
        }
        return output;
    }

    private void dilate(int _x, int _y, int size) {
        int mid = operand.length / 2;
        Log.d("MID is: ", Integer.toString(mid));
        for(int i = 0; i < operand[size].length; i++) {
            int x = _x + i - mid;
            for(int j = 0; j < operand[size].length; j++) {
                int y = _y + j - mid;
                if(x >= 0 && y >= 0 && x < beforeMinkowski.length && y < beforeMinkowski[0].length) {
                    if(operand[size][i][j] == 1) {
                        afterMinkowski[x][y] = operand[size][i][j] == 1 ? Color.WHITE : Color.BLACK;
                    }
                    //Log.d("X Y is: ", Integer.toString(x) + " " + Integer.toString(y));
                    //Log.d("I J is: ", Integer.toString(i) + " " + Integer.toString(j));
                }
            }
        }
    }

    private Bitmap dilation(Bitmap input, int size) {
        Bitmap output = Bitmap.createBitmap(input.getWidth(), input.getHeight(), Bitmap.Config.ARGB_8888);
        final int height = input.getHeight();
        final int width = input.getWidth();

        beforeMinkowski = new int[width][height];
        afterMinkowski = new int[width][height];
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                beforeMinkowski[i][j] = input.getPixel(i, j);
                Log.d("Minkowski Info: ",Integer.toString(beforeMinkowski[i][j]));
            }
        }
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                if(beforeMinkowski[i][j] == Color.WHITE) {
                    dilate(i, j, size);
                } else {
                    afterMinkowski[i][j] = beforeMinkowski[i][j];
                }
            }
        }
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                output.setPixel(i, j, afterMinkowski[i][j]);
                //Log.d("OUTPIXEL: ", Integer.toString(afterMinkowski[i][j]));
                //Log.d("PIXEL: ", Integer.toString(beforeMinkowski[i][j]));
            }
        }
        return output;
    }

    Bitmap preprocessSkinColor(Bitmap bitmap) {
        final Bitmap originalBitmap = resizeBitmap(bitmap);
        final Bitmap processedBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);


        final int height = originalBitmap.getHeight();
        final int width = originalBitmap.getWidth();

        Parallel.For(0, height, new LoopBody<Integer>() {
            @Override
            public void run(Integer y) {
                int[] resultPixels = new int[width];
                originalBitmap.getPixels(resultPixels, 0, width, 0, y, width, 1);

                for (int x = 0; x < width; ++x) {
                    resultPixels[x] = isSkinColor(resultPixels[x]) ? Color.WHITE : Color.BLACK;
                }
                processedBitmap.setPixels(resultPixels, 0, width, 0, y, width, 1);
            }
        });

        return processedBitmap;
    }

    Bitmap getFaceFromBitmap(Bitmap bitmap) {
        final Bitmap originalBitmap = resizeBitmap(bitmap);
        final Bitmap processedBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);


        final int height = originalBitmap.getHeight();
        final int width = originalBitmap.getWidth();

        Parallel.For(0, height, new LoopBody<Integer>() {
            @Override
            public void run(Integer y) {
                int[] resultPixels = new int[width];
                originalBitmap.getPixels(resultPixels, 0, width, 0, y, width, 1);

                for (int x = 0; x < width; ++x) {
                    resultPixels[x] = isSkinColor(resultPixels[x]) ? Color.WHITE : Color.BLACK;
                }
                processedBitmap.setPixels(resultPixels, 0, width, 0, y, width, 1);
            }
        });

        //final int[] faceBox = getFaceBox(processedBitmap);
        int[][] label = getLabel(bufferBitmap, 0, bitmap.getHeight(), 0, bitmap.getWidth(), Color.WHITE);
        Box[] boundingBox = getRegion(label);
        newBoundingBox = getValidBoxRatio(boundingBox);
        newBoundingBox = removeInsideBox(newBoundingBox);

        //final int[] mouthBox = getmouthBox(processedBitmap,faceBox);
        Bitmap newBitmap = processedBitmap;
        for(int i = 0; i < newBoundingBox.size();i++){
            if(newBoundingBox.get(i).valid(50))
                newBitmap = newBoundingBox.get(i).drawBox(newBitmap, Color.RED);
        }
        return newBitmap;
    }

    private Bitmap getEye(Bitmap bitmap){
        List<Box[]> eyeBox = new ArrayList<>();
        List<Integer> idx = new ArrayList<>();
        for(int i = 0; i < newBoundingBox.size();i++) {
            int topEye = (int)newBoundingBox.get(i).top + Math.round((newBoundingBox.get(i).bottom-newBoundingBox.get(i).top)*0.25f);
            int bottomEye = (int)newBoundingBox.get(i).top + Math.round((newBoundingBox.get(i).bottom-newBoundingBox.get(i).top)*0.5f);
//            if (newBoundingBox.get(i).valid(50)) {
                int[][] label = getLabel(bufferBitmap, (int) newBoundingBox.get(i).top, (int) newBoundingBox.get(i).bottom, (int) newBoundingBox.get(i).left, (int) newBoundingBox.get(i).right, Color.BLACK);
                eyeBox.add(getRegion(label));
                idx.add(i);
//            }
        }

        for(int i = 0; i < eyeBox.size();i++){
            int topEye = (int)newBoundingBox.get(idx.get(i)).top + Math.round((newBoundingBox.get(idx.get(i)).bottom-newBoundingBox.get(idx.get(i)).top)*0.25f);
            int bottomEye = (int)newBoundingBox.get(idx.get(i)).top + Math.round((newBoundingBox.get(idx.get(i)).bottom-newBoundingBox.get(idx.get(i)).top)*0.5f);
            Box box = new Box((int)newBoundingBox.get(idx.get(i)).top,(int)newBoundingBox.get(idx.get(i)).bottom,newBoundingBox.get(idx.get(i)).left, newBoundingBox.get(idx.get(i)).right);
//            List<Box> validEye = removeInsideBox(eyeBox.get(i));
            List<Box> validEye = getFaceComponent(Arrays.asList(eyeBox.get(i)), newBoundingBox.get(i));
            for (int j = 0; j < validEye.size();j++){
                int y = (int)newBoundingBox.get(idx.get(i)).top;
                int x = (int)newBoundingBox.get(idx.get(i)).left;
//                Log.d("SIZEXYVALIDEYE", Float.toString(validEye.get(i).left) + " " + Float.toString(validEye.get(i).top));
//                Log.d("SIZEVALIDEYE", Float.toString(validEye.get(j).getSize()));
                bitmap = validEye.get(j).drawBox(bitmap, x, y , Color.BLUE);
            }

            validEye = getValidEye(validEye, box.getSize());

            for (int j = 0; j < validEye.size();j++){
                int y = (int)newBoundingBox.get(idx.get(i)).top;
                int x = (int)newBoundingBox.get(idx.get(i)).left;
//                Log.d("SIZEXYVALIDEYE", Float.toString(validEye.get(i).left) + " " + Float.toString(validEye.get(i).top));
//                Log.d("SIZEVALIDEYE", Float.toString(validEye.get(j).getSize()));
                bitmap = validEye.get(j).drawBox(bitmap, x, y , Color.GREEN);
            }
            if(validEye.size() == 2)
                bitmap = newBoundingBox.get(idx.get(i)).drawBox(bitmap, 0, 0, Color.RED);
        }
        return bitmap;
    }

    private List<Box> getValidEye(List<Box> eyeBox, float outerBoxSize){
        List<Box> box = new ArrayList<>();
        List<Box> finalBox = new ArrayList<>();
        for(int i = 0;i < eyeBox.size();i++){
            float width = eyeBox.get(i).right - eyeBox.get(i).left;
            float height = eyeBox.get(i).bottom - eyeBox.get(i).top;
            Log.d("WIDTHHEIGHTSIZE", Float.toString(width) + " " + Float.toString(height));
            Log.d("BOXSIZE", "IN:" + Float.toString(eyeBox.get(i).getSize()) + " OUT:" + Float.toString(outerBoxSize));
            if(eyeBox.get(i).valid(15)){
                Log.d("BOXSIZEPASS", "IN:" + Float.toString(eyeBox.get(i).getSize()) + " OUT:" + Float.toString(outerBoxSize));
                box.add(eyeBox.get(i));
            }
        }
        Log.d("BOXSIZES", Integer.toString(box.size()));
        if (box.size() > 2) {
            int firstIdx = 0;
            int secondIdx = 0;
            float minDistance = -1;
            for(int i = 0; i < box.size();i++){
                for(int j = i+1; j < box.size();j++){
                    if(i != j){
                        Log.d("DIS", Float.toString(box.get(i).top) + " " + Float.toString(box.get(j).top));
                        Log.d("SIZEMINDIS", Float.toString(boxHeightDistance(box.get(i),box.get(j))));
                        if(boxHeightDistance(box.get(i),box.get(j)) < minDistance || minDistance == -1){
                            minDistance = boxHeightDistance(box.get(i),box.get(j));
                            firstIdx = i;
                            secondIdx = j;
                        }
                    }
                }
            }
            Log.d("DISFINAL", Float.toString(box.get(firstIdx).top) + " " + Float.toString(box.get(secondIdx).top));
            finalBox.add(box.get(firstIdx));
            finalBox.add(box.get(secondIdx));
        } else if(box.size() == 2) {
            finalBox.add(box.get(0));
            finalBox.add(box.get(1));
        }
        Log.d("SIZEFINAL", Integer.toString(finalBox.size()));
        return finalBox;
    }

    private List<Box> getFaceComponent(List<Box> faceComponentCandidate, Box faceBox){
        List<Box> box = new ArrayList<>();
        List<Box> finalBox = new ArrayList<>();
        for(int i = 0;i < faceComponentCandidate.size();i++){

            float width = Math.abs(faceBox.left - faceBox.right);
            float height = Math.abs(faceBox.bottom - faceBox.top);

            //Kalau kotak komponen Berada di sisi wajah
            if ( Math.abs(faceBox.right - (faceBox.left + faceComponentCandidate.get(i).right))  < (0.05*width)   ||
                Math.abs(faceBox.bottom- (faceBox.top + faceComponentCandidate.get(i).bottom))  < (0.05*height) ||
                Math.abs (faceBox.top - (faceBox.top +faceComponentCandidate.get(i).top)) < (0.05*height)  ||
                Math.abs(faceBox.left -(faceBox.left+ faceComponentCandidate.get(i).left)) < (0.05*width)) {

            } else {
                finalBox.add(faceComponentCandidate.get(i));
            }
        }
        return finalBox;
    }



    private float boxHeightDistance(Box box1, Box box2){
        return Math.abs(box1.top - box2.top);
    }

    private List<Box> removeInsideBox(Box[] box){
        List<Integer> idxToRemove = new ArrayList<>();
        List<Box> boxes = new ArrayList<>();
        for(int i = 0; i < box.length;i++){
            for(int j = 0;j < box.length;j++){
                if(i != j && box2Insidebox1(box[i],box[j])){
                    idxToRemove.add(j);
                }
            }
        }
        for(int i=0;i<box.length;i++){
            if(!idxToRemove.contains(i)){
                boxes.add(box[i]);
            }
        }
        return boxes;
    }

    private List<Box> removeInsideBox(List<Box> box){
        List<Integer> idxToRemove = new ArrayList<>();
        List<Box> boxes = new ArrayList<>();
        for(int i = 0; i < box.size();i++){
            for(int j = 0;j < box.size();j++){
                if(i != j && box2Insidebox1(box.get(i),box.get(j))){
                    idxToRemove.add(j);
                }
            }
        }
        for(int i=0;i<box.size();i++){
            if(!idxToRemove.contains(i)){
                boxes.add(box.get(i));
            }
        }
        return boxes;
    }

    private boolean box2Insidebox1(Box box1, Box box2){
        return (box2.top >= box1.top && box2.bottom <= box1.bottom && box2.left >= box1.left && box2.right <= box1.right);
    }

    private int[] getFaceBox(Bitmap bitmap) {

        int minWidth = bitmap.getWidth() + 1;
        int maxWidth = 0;
        int minHeight = bitmap.getWidth() + 1 ;
        int maxHeight = 0;
        for (int j = 0; j < bitmap.getWidth(); j++) {
            for (int i = 0; i < bitmap.getHeight(); i++) {
                int pixel = bitmap.getPixel(j,i);
                int grayColor = Color.red(pixel) + Color.green(pixel) + Color.blue(pixel);
                grayColor /= 3;
                if (grayColor == 255) {
                    if (j > maxWidth) maxWidth = j;
                    if (j < minWidth) minWidth = j;
                    if (i > maxHeight) maxHeight = i;
                    if (i < minHeight) minHeight = i;

                }
            }
        }
        int[] borderPoint = new int[]{minWidth,minHeight,maxHeight,maxWidth};
        Log.d("X", ""+borderPoint[0]+" "+borderPoint[1]+" "+borderPoint[2]+ " "+ borderPoint[3]);
        return borderPoint;
    }

    private List<Box> getValidBoxRatio(Box[] box){
        List<Box> newBoundingBox = new ArrayList<>();
        for(int i = 0;i < box.length;i++){
            float height = box[i].bottom - box[i].top;
            float width = box[i].right - box[i].left;
            Log.d("RATIO", Float.toString(height/width));
            if(height/width >= 0.8f){
                if(height/width > 1.4f){
                    height = width * 1.4f;
                    box[i].bottom = box[i].top + height;
                }
                newBoundingBox.add(box[i]);
            }
        }
        return newBoundingBox;
    }

    private Box[] getRegion(int[][] label){
        Box[] boundingBox = new Box[labelCount];
        for(int i = 0;i < boundingBox.length;i++){
            boundingBox[i] = new Box();
        }
        Log.d("BOXSIZE", "" + boundingBox.length);
        for(int i = 0; i < label.length;i++){
            for(int j = 0; j < label[i].length;j++) {
                if(label[i][j] != 0){
                    if(boundingBox[label[i][j]-1].left < 0 || j < boundingBox[label[i][j]-1].left){
                        boundingBox[label[i][j]-1].left = j;
                    }
                    if(boundingBox[label[i][j]-1].right < 0 || j > boundingBox[label[i][j]-1].right){
                        boundingBox[label[i][j]-1].right = j;
                    }
                    if(boundingBox[label[i][j]-1].top < 0 || i < boundingBox[label[i][j]-1].top){
                        boundingBox[label[i][j]-1].top = i;
                    }
                    if(boundingBox[label[i][j]-1].bottom < 0 || i > boundingBox[label[i][j]-1].bottom){
                        boundingBox[label[i][j]-1].bottom = i;
                    }
                }
            }
        }
        return boundingBox;
    }

    private int[][] getLabel(Bitmap bitmap, int top, int bottom, int left, int right, int color){
        ArrayList<ArrayList<Integer>> linked = new ArrayList<>();
        //int label = 0;
        int heightSize = bottom-top;
        int widthSize = right-left;
        int[][] label = new int[heightSize][widthSize];
        int currentLabel = 1;
        for(int i = 0; i < heightSize;i++){
            for(int j = 0; j < widthSize;j++){
                if(isColor(bitmap.getPixel(left+j,top+i), color)){
                    ArrayList<Integer> neighbors = getNeighbor(label, i, j);
                    if(neighbors.size() == 0){
                        label[i][j] = currentLabel;
                        linked.add(new ArrayList<Integer>());
                        linked.get(linked.size()-1).add(currentLabel);
                        currentLabel++;
                    } else {
                        label[i][j] = minLabel(neighbors);
                        for(int neighbor : neighbors){
                            linked.get(neighbor-1).addAll(neighbors);
                        }
                    }
                }
            }
        }
        int newLabelCount = 0;
        for(int i = 0; i < heightSize;i++) {
            for (int j = 0; j < widthSize; j++) {
                if(isColor(bitmap.getPixel(left+j,top+i),color)){
                    ArrayList<Integer> EquivalentLabels = linked.get(label[i][j]-1);
                    label[i][j] = minLabel(EquivalentLabels);
                    if(newLabelCount < label[i][j]){
                        newLabelCount = label[i][j];
                    }
                }
            }
        }
        labelCount = newLabelCount;
        return label;
    }

    private int minLabel(List<Integer> labels){
        int min = labels.get(0);
        for(int i = 1; i < labels.size();i++){
            if(min > labels.get(i)){
                min = labels.get(i);
            }
        }
        return min;
    }

    private ArrayList<Integer> getNeighbor(int[][] label, int x, int y){
        ArrayList<Integer> white = new ArrayList<Integer>();
        boolean top = y-1 >= 0;
        boolean right = x+1 < label.length;
        boolean left = x-1 >= 0;
        boolean bottom = y+1 < label[0].length;
        if(top && label[x][y-1] != 0)
            white.add(label[x][y-1]);
        if(right && top && label[x+1][y-1] != 0)
            white.add(label[x+1][y-1]);
        if(right && label[x+1][y] != 0)
            white.add(label[x+1][y]);
        if(right && bottom && label[x+1][y+1] != 0)
            white.add(label[x+1][y+1]);
        if(bottom && label[x][y+1] != 0)
            white.add(label[x][y+1]);
        if(left && bottom && label[x-1][y+1] != 0)
            white.add(label[x-1][y+1]);
        if(left && label[x-1][y] != 0)
            white.add(label[x-1][y]);
        if(left && top && label[x-1][y-1] != 0)
            white.add(label[x-1][y-1]);
        return white;
    }

//    private List<Point> getWhiteNeighbor(Bitmap bitmap, int x, int y){
//        List<Point> white = new ArrayList<Point>();
//        if(isWhite(bitmap.getPixel(x,y-1)))
//            white.add(new Point(x,y-1));
//        if(isWhite(bitmap.getPixel(x+1,y-1)))
//            white.add(new Point(x+1,y-1));
//        if(isWhite(bitmap.getPixel(x+1,y)))
//            white.add(new Point(x+1,y));
//        if(isWhite(bitmap.getPixel(x+1,y+1)))
//            white.add(new Point(x+1,y+1));
//        if(isWhite(bitmap.getPixel(x,y+1)))
//            white.add(new Point(x,y+1));
//        if(isWhite(bitmap.getPixel(x-1,y-1)))
//            white.add(new Point(x-1,y+1));
//        if(isWhite(bitmap.getPixel(x-1,y)))
//            white.add(new Point(x-1,y));
//        if(isWhite(bitmap.getPixel(x-1,y-1)))
//            white.add(new Point(x-1,y-1));
//        return white;
//    }

    private boolean isColor(int pixel, int color){
        return (pixel == color);
    }

    private int sumRegion(int[] borderPoint, Bitmap bitmap) {
        int sum = 0;
        for (int j = borderPoint[0]; j <= borderPoint[3]; j++) {
            for (int i = borderPoint[1]; i < borderPoint[2]; i++) {
                int pixel = bitmap.getPixel(j,i);
                int grayColor = Color.red(pixel) + Color.green(pixel) + Color.blue(pixel);
                grayColor /= 3;
                if (grayColor == 0) {
                    sum += 1;
                }

            }
        }
        return  sum;
    }

//    private int[] getmouthBox(Bitmap bitmap, int[] faceBox) {
//        int minWidth = faceBox[0];
//        int maxWidth = faceBox[3] + 1 ;
//        int minHeight = faceBox[2] + 1 ;
//        int maxHeight = 0;
//        for (int j = 0; j < bitmap.getWidth(); j++) {
//            for (int i = 0; i < bitmap.getHeight(); i++) {
//                int pixel = bitmap.getPixel(j,i);
//                int grayColor = Color.red(pixel) + Color.green(pixel) + Color.blue(pixel);
//                grayColor /= 3;
//                if (grayColor == 255) {
//                    if (j > maxWidth) maxWidth = j;
//                    if (j < minWidth) minWidth = j;
//                    if (i > maxHeight) maxHeight = i;
//                    if (j < minHeight) minHeight = i;
//
//                }
//            }
//        }
//        int[] borderPoint = new int[]{minWidth,minHeight,maxHeight,maxWidth};
//        Log.d("X", ""+borderPoint[0]+" "+borderPoint[1]+" "+borderPoint[2]+ " "+ borderPoint[3]);
//        return borderPoint;
//    }

    private boolean isSkinColor(int pixel) {
        int[] YCbCr = getYCbCr(pixel);
        return (YCbCr[0] > 80 && YCbCr[1] > 85 && YCbCr[1] < 135 &&
                YCbCr[2] > 135 && YCbCr[2] < 180);
    }

    private int[] getYCbCr(int pixel) {
        int[] YCbCr = new int[3];

        int[] RGB = getRGB(pixel);
        YCbCr[0] = (int) (0.299 * RGB[0] + 0.587 * RGB[1] + 0.114 * RGB[2]);
        YCbCr[1] = (int) (128 - 0.169 * RGB[0] - 0.331 * RGB[1] + 0.5 * RGB[2]);
        YCbCr[2] = (int) (128 + 0.5 * RGB[0] - 0.419 * RGB[1] - 0.081 * RGB[2]);
        return YCbCr;
    }

    private int[] getRGB(int pixel) {
        int[] RGB = new int[3];

        RGB[0] = (pixel & 0x00FF0000) >> 16;
        RGB[1] = (pixel & 0x0000FF00) >> 8;
        RGB[2] = (pixel & 0x000000FF);

        return RGB;
    }

    private void SelectFeature() {
        final CharSequence[] items ={"Face Detect",
                "Preprocess Skin Color",
                "Erode",
                "Dilate",
                "Get Eye",
                "Identity",
                "Blur",
                "Gaussian blur 5 x 5",
                "Sharpen",
                "Edge Detection 0",
                "Edge Detection 1",
                "Outline",
                "Bottom Sobel",
                "Top Sobel",
                "Left Sobel",
                "Right Sobel",
                "Sobel",
                "Robert",
                "Prewitt",
                "Custom",
                "Greyscale",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Feature");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(items[which].equals("Face Detect")) {
                    drawBitmap = getFaceFromBitmap(bitmap);
                    imageViewAfter.setImageBitmap(drawBitmap);
                    imageViewAfter.setVisibility(View.VISIBLE);
                } else if(items[which].equals("Preprocess Skin Color")) {
                    imageViewAfter.setImageBitmap(preprocessSkinColor(bitmap));
                    imageViewAfter.setVisibility(View.VISIBLE);
                } else if(items[which].equals("Erode")) {
                    if (bufferBitmap == null) {
                        imageViewBefore.setImageBitmap(preprocessSkinColor(bitmap));
                        bufferBitmap = erosion(preprocessSkinColor(bitmap), 3);
                    } else {
                        imageViewBefore.setImageBitmap(bufferBitmap);
                        bufferBitmap = erosion(bufferBitmap, 3);
                    }
                    imageViewAfter.setImageBitmap(bufferBitmap);
                    imageViewAfter.setVisibility(View.VISIBLE);
                } else if(items[which].equals("Dilate")) {
                    if (bufferBitmap == null) {
                        imageViewBefore.setImageBitmap(preprocessSkinColor(bitmap));
                        bufferBitmap = dilation(preprocessSkinColor(bitmap), 3);
                    } else {
                        imageViewBefore.setImageBitmap(bufferBitmap);
                        bufferBitmap = dilation(bufferBitmap, 3);
                    }
                    imageViewAfter.setImageBitmap(bufferBitmap);
                    imageViewAfter.setVisibility(View.VISIBLE);
                } else if(items[which].equals("Get Eye")){
                    drawBitmap = getEye(bufferBitmap);
                    imageViewAfter.setImageBitmap(drawBitmap);
                } else if(items[which].equals("Identity")) {
                    convoluteImage(bitmap,secondBitmap,identity);
                } else if (items[which].equals("Blur")) {
                    convoluteImage(bitmap,secondBitmap,blur);
                } else if (items[which].equals("Gaussian blur 5 x 5")) {
                    convoluteImage(bitmap,secondBitmap,gaussian_blur);
                } else if (items[which].equals("Sharpen")) {
                    convoluteImage(bitmap,secondBitmap,sharpen);
                } else if (items[which].equals("Edge Detection 0")){
                    convoluteImage(bitmap,secondBitmap,edge_detection0);
                } else if (items[which].equals("Edge Detection 1")){
                    convoluteImage(bitmap,secondBitmap,edge_detection1);
                } else if (items[which].equals("Outline")){
                    convoluteImage(bitmap,secondBitmap,outline);
                } else if (items[which].equals("Bottom Sobel")) {
                    convoluteImage(bitmap,secondBitmap,bottomSobel);
                } else if (items[which].equals("Right Sobel")){
                    convoluteImage(bitmap,secondBitmap,rightSobel);
                } else if (items[which].equals("Left Sobel")){
                    convoluteImage(bitmap,secondBitmap,leftSobel);
                } else if (items[which].equals("Top Sobel")) {
                    convoluteImage(bitmap, secondBitmap, topSobel);
                } else if (items[which].equals("Sobel")){
                    selectedKernel.clear();
                    selectedKernel.add(topSobel);
                    selectedKernel.add(rightSobel);
                    convoluteImage(bitmap,secondBitmap,selectedKernel);
                }else if (items[which].equals("Robert")){
                    selectedKernel.clear();
                    selectedKernel.add(robert1);
                    selectedKernel.add(robert2);
                    convoluteImage(bitmap,secondBitmap,selectedKernel);
                } else if (items[which].equals("Prewitt")) {
                    selectedKernel.clear();
                    selectedKernel.add(prewitt_horizontal);
                    selectedKernel.add(prewitt_vertical);
                    convoluteImage(bitmap, secondBitmap,selectedKernel);
                } else if (items[which].equals("Custom")) {
                    updateCustomMatrix();
                    convoluteImage(bitmap, secondBitmap, custom_matrix);
                } else if (items[which].equals("Greyscale")){
                    convertGreyscale();
                } else if (items[which].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void uploadImage() {
        final CharSequence[] items ={"Camera","Gallery","Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(items[which].equals("Camera")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                    imageViewBefore.setVisibility(View.VISIBLE);
                } else if (items[which].equals("Gallery")) {

                    Intent photopickerIntent = new Intent(Intent.ACTION_PICK);
                    File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    String pictureDirectoryPath = pictureDirectory.getPath();
                    Uri data = Uri.parse(pictureDirectoryPath);
                    photopickerIntent.setDataAndType(data, "image/*");
                    startActivityForResult(photopickerIntent,SELECT_FILE);
                    imageViewBefore.setVisibility(View.VISIBLE);
                } else if (items[which].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void updateCustomMatrix() {
        for(int i = 0;i < matrixView.length;i++) {
            for (int j = 0; j < matrixView[0].length; j++) {
                if(matrixView[i][j].getText().toString().equals("")){
                    custom_matrix[i][j] = 0;
                } else {
                    custom_matrix[i][j] = Integer.parseInt(matrixView[i][j].getText().toString());
                }
            }
        }
    }
    private void convertGreyscale() {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        for (int j = 0; j < bitmap.getWidth(); j++) {
            for (int i = 0; i < bitmap.getHeight(); i++) {
                int pixel = bitmap.getPixel(j,i);
                int alpha = (int) Color.alpha(pixel);
                int grayColor = Color.red(pixel) + Color.green(pixel) + Color.blue(pixel);
                grayColor /= 3;
                pixel = 0;
                pixel = pixel | grayColor;
                pixel = pixel | (grayColor << 8);
                pixel = pixel | (grayColor << 16);
                pixel = pixel | (alpha << 24);
                newBitmap.setPixel(j, i, pixel);
            }
        }
        bitmap = newBitmap;
        imageViewBefore.setImageBitmap(bitmap);
    }

    private void convoluteImage(Bitmap bitmap,Bitmap secondBitmap, ArrayList<double[][]> _kernel) {
        int pixel;
        int red, green, blue, alpha, pix;
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        double[][] kernel = _kernel.get(0);
        int[][][] neighbour = new int[_kernel.size()][kernel.length][kernel[0].length];
        int[][][] access_neighbourx = new int[_kernel.size()][kernel.length][kernel[0].length];
        int[][][] access_neighboury = new int[_kernel.size()][kernel.length][kernel[0].length];

        // Initiate matrix offset
        for (int nkernel = 0; nkernel < _kernel.size(); nkernel++) {
            kernel = _kernel.get(nkernel);
            for(int i = 0; i < kernel.length; i++) {
                for(int j = 0; j < kernel[i].length; j++) {
                    access_neighboury[nkernel][i][j] = kernel.length/2 - i;
                    access_neighbourx[nkernel][i][j] = kernel.length/2 - j;
                }
            }
        }

        Log.d("Width Heigth ", ""+bitmap.getWidth()+", "+bitmap.getHeight());

        int[][] convolutionSum = new int[3][_kernel.size()];

        for (int j = 0; j < bitmap.getWidth(); j++) {
            for (int i = 0; i < bitmap.getHeight(); i++) {

                // Set zero all operation
                int step = 0;
                for(int m = 0; m < 3; m++) {
                    for (int n = 0; n < convolutionSum[0].length; n++) {
                        convolutionSum[m][n] = 0;
                    }
                }

                for (int nkernel = 0; nkernel < _kernel.size(); nkernel++) {
                    kernel = _kernel.get(nkernel);
                    // Initiate matrix padding
                    for(int a = 0; a < kernel.length; a++) {
                        for(int b = 0; b < kernel[0].length; b++) {
                            neighbour[nkernel][a][b] = 1;
                        }
                    }
                    if (i < kernel.length/2) {
                        for(int k = 0; k < kernel.length/2 - i; k++) {
                            for(int l = 0; l < kernel.length; l++){
                                neighbour[nkernel][k][l]=0;
                            }
                        }
                    }
                    if (j < kernel.length/2) {
                        for(int k = 0; k < kernel.length; k++) {
                            for(int l = 0; l < kernel.length/2 - j; l++){
                                neighbour[nkernel][k][l]=0;
                            }
                        }
                    }
                    if (j > bitmap.getWidth()- kernel.length/2 - 1) {
                        for(int k = 0; k < kernel.length; k++) {
                            for(int l = kernel.length/2 + bitmap.getWidth() - j ; l < kernel.length; l++){
                                neighbour[nkernel][k][l]=0;
                            }
                        }
                    }
                    if (i > bitmap.getHeight()- kernel.length/2 - 1) {
                        for(int k = kernel.length/2 + bitmap.getHeight() - i; k < kernel.length; k++) {
                            for(int l = 0; l < kernel.length; l++){
                                neighbour[nkernel][k][l]=0;
                            }
                        }
                    }

                    // Intiate pixel with 0
                    red = 0;
                    green = 0;
                    blue = 0;
                    pix = 0;
                    alpha = 0;

                    // Replace pixel intensity
                    for (int k = 0; k < kernel.length; k++){
                        for (int l = 0; l < kernel[0].length; l++){
                            if (neighbour[nkernel][k][l] == 1) {
                                pixel = bitmap.getPixel(j - access_neighbourx[nkernel][k][l],(i - access_neighboury[nkernel][k][l]));
                                red +=  (Color.red(pixel) * kernel[k][l]);
                                green +=  (Color.green(pixel) * kernel[k][l]);
                                blue +=  (Color.blue(pixel) * kernel[k][l]);
                                convolutionSum[0][step] = red;
                                convolutionSum[1][step] = green;
                                convolutionSum[2][step] = blue;
                            }
                        }
                    }
                    step++;

                    double gradient[] = new double[3];
                    for(int m = 0; m < 3; m++) {
                        gradient[m] = 0;
                    }
                    for(int m = 0; m < 3; m++) {
                        for(int n = 0; n < convolutionSum[0].length; n++) {
                            gradient[m] += convolutionSum[m][n] * convolutionSum[m][n];
                        }
                    }
                    for(int m = 0; m < 3; m++) {
                        gradient[m] = sqrt(gradient[m]);
                    }
                    red = (int)gradient[0];
                    green = (int)gradient[1];
                    blue = (int)gradient[2];

                    alpha = (int) Color.alpha(bitmap.getPixel(j,i));
                    if(red  > 255) red = 255;
                    if(green > 255) green = 255;
                    if(blue > 255) blue = 255;
                    if(red < 0) red = 0;
                    if(green < 0) green = 0;
                    if(blue < 0) blue = 0;

                    pix = pix | blue;
                    pix = pix | (green << 8);
                    pix = pix | (red << 16);
                    pix = pix | (alpha << 24);

                    newBitmap.setPixel(j, i, pix);
                }
            }
        }





        imageViewAfter.setImageBitmap(newBitmap);
        imageViewAfter.setVisibility(View.VISIBLE);
    }

    private void convoluteImage(Bitmap bitmap,Bitmap secondBitmap, double[][] kernel) {
        int pixel;
        int red, green, blue, alpha, pix;
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        int[][] neigbour = new int[kernel.length][kernel[0].length];
        int[][] access_neighbourx = new int[kernel.length][kernel[0].length];
        int[][] access_neighboury = new int[kernel.length][kernel[0].length];

        for(int i = 0; i < kernel.length; i++) {
            for(int j = 0; j < kernel[i].length; j++) {
                access_neighboury[i][j] = kernel.length/2 - i;
                access_neighbourx[i][j] = kernel.length/2 - j;
            }
        }




        Log.d("Width Heigth ", ""+bitmap.getWidth()+", "+bitmap.getHeight());

        for (int j = 0; j < bitmap.getWidth(); j++) {
            for (int i = 0; i < bitmap.getHeight(); i++) {

//                Log.d("FROM :", ""+j+", "+i);
                for(int a = 0; a < kernel.length; a++) {
                    for(int b = 0; b < kernel[0].length; b++) {
                        neigbour[a][b] = 1;
//                        Log.d("X, Y" , ""+(j - access_neighbourx[a][b]) +", "+(i - access_neighboury[a][b]));
                    }
                }

                if (i < kernel.length/2) {
                    for(int k = 0; k < kernel.length/2 - i; k++) {
                        for(int l = 0; l < kernel.length; l++){
                            neigbour[k][l]=0;
                        }
                    }
                }
                if (j < kernel.length/2) {
                    for(int k = 0; k < kernel.length; k++) {
                        for(int l = 0; l < kernel.length/2 - j; l++){
                            neigbour[k][l]=0;
                        }
                    }
                }
                if (j > bitmap.getWidth()- kernel.length/2 - 1) {
                    for(int k = 0; k < kernel.length; k++) {
                        for(int l = kernel.length/2 + bitmap.getWidth() - j ; l < kernel.length; l++){
                            neigbour[k][l]=0;
                        }
                    }
                }
                if (i > bitmap.getHeight()- kernel.length/2 - 1) {
                    for(int k = kernel.length/2 + bitmap.getHeight() - i; k < kernel.length; k++) {
                        for(int l = 0; l < kernel.length; l++){
                            neigbour[k][l]=0;
                        }
                    }
                }


                red = 0;
                green = 0;
                blue = 0;
                pix = 0;
                alpha = 0;
//                sumpixel = 0;
                for (int k = 0; k < kernel.length; k++){
                    for (int l = 0; l < kernel[0].length; l++){
                        if (neigbour[k][l] == 1) {
                            pixel = bitmap.getPixel(j - access_neighbourx[k][l],(i - access_neighboury[k][l]));
                            red +=  (Color.red(pixel) * kernel[k][l]);
                            green +=  (Color.green(pixel) * kernel[k][l]);
                            blue +=  (Color.blue(pixel) * kernel[k][l]);
//                            alpha +=  (Color.alpha(pixel) * kernel[k][l]);
//                            Log.d("RGB " , ""+ red +" " + green + " " +blue);
//                            sumpixel += (pixel * kernel[k][l]);
                        }
                    }
                }
                alpha = (int) Color.alpha(bitmap.getPixel(j,i));
                if(red  > 255) red = 255;
                if(green > 255) green = 255;
                if(blue > 255) blue = 255;
                if(red < 0) red = 0;
                if(green < 0) green = 0;
                if(blue < 0) blue = 0;

                pix = pix | blue;
                pix = pix | (green << 8);
                pix = pix | (red << 16);
                pix = pix | (alpha << 24);
//                Log.d("pix " , ""+ red +" " + green +" "+ blue);

                newBitmap.setPixel(j, i, pix);
            }
        }
        imageViewAfter.setImageBitmap(newBitmap);
        imageViewAfter.setVisibility(View.VISIBLE);
    }

}
