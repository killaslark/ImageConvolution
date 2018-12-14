package com.example.asus.imageconvolution;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
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
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import static java.lang.Math.floor;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;

public class MainActivity extends AppCompatActivity {

    private List<ControlPoint> cp1 = new ArrayList<>();
    private List<ControlPoint> cp2 = new ArrayList<>();

    private ArrayList<double[][]> selectedKernel = new ArrayList<>();
    private List<Box> newBoundingBox = new ArrayList<>();
    private List<Box> faceComponentCandidate = new ArrayList<>();
    private TextView recognitionPrediction;
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

    private void updateTextView (String input) {
        TextView text = (TextView) findViewById(R.id.recognition_predict);
        text.setText(input);
    }

    private void testControlPoint(){
        Point[] p = new Point[8];
        p[0] = new Point(1, 2);
        p[1] = new Point(1, 8);
        p[2] = new Point(1, 2);
        p[3] = new Point(1, 2);
        p[4] = new Point(1, 2);
        p[5] = new Point(1, 2);
        p[6] = new Point(3, 2);
        p[7] = new Point(1, 2);
        cp1.add(new ControlPoint(p));
        Point[] q = new Point[8];
        q[0] = new Point(1, 2);
        q[1] = new Point(2, 2);
        q[2] = new Point(1, 2);
        q[3] = new Point(1, 2);
        q[4] = new Point(2, 1);
        q[5] = new Point(1, 2);
        q[6] = new Point(4, 2);
        q[7] = new Point(1, 2);
        cp1.add(new ControlPoint(q));
        Point[] r = new Point[8];
        r[0] = new Point(1, 2);
        r[1] = new Point(1, 2);
        r[2] = new Point(1, 2);
        r[3] = new Point(1, 2);
        r[4] = new Point(1, 2);
        r[5] = new Point(1, 2);
        r[6] = new Point(1, 2);
        r[7] = new Point(1, 2);
        cp1.add(new ControlPoint(r));

        Point[] s = new Point[8];
        s[0] = new Point(1, 2);
        s[1] = new Point(1, 2);
        s[2] = new Point(1, 2);
        s[3] = new Point(1, 2);
        s[4] = new Point(1, 2);
        s[5] = new Point(1, 2);
        s[6] = new Point(1, 2);
        s[7] = new Point(1, 2);
        cp2.add(new ControlPoint(s));
        Point[] t = new Point[8];
        t[0] = new Point(1, 2);
        t[1] = new Point(2, 2);
        t[2] = new Point(1, 2);
        t[3] = new Point(1, 2);
        t[4] = new Point(1, 5);
        t[5] = new Point(1, 2);
        t[6] = new Point(1, 2);
        t[7] = new Point(1, 2);
        cp2.add(new ControlPoint(t));
        Point[] u = new Point[8];
        u[0] = new Point(1, 2);
        u[1] = new Point(3, 2);
        u[2] = new Point(1, 2);
        u[3] = new Point(1, 5);
        u[4] = new Point(1, 2);
        u[5] = new Point(1, 2);
        u[6] = new Point(1, 2);
        u[7] = new Point(7, 2);
        cp2.add(new ControlPoint(u));

        float sum = CalculateCPDistance(cp1, cp2);
        Log.d("DISTANCE OF CONTROL POINTS: ", Float.toString(sum));
    }

    private float CalculateCPDistance(List<ControlPoint> a, List<ControlPoint> b) {
        float sum = 0;
        for(int i = 0; i < a.size(); i++) {
            sum += a.get(i).calculateSumDistance(b.get(i));
        }
        return sum;
    }

    private void RecognizeFace() {
        int n = 3; //Face Reference Size
        List<List<ControlPoint>> ref = new ArrayList<>(); //Control Point Reference
        List<ControlPoint> current = new ArrayList<>(); //Current tested control point
        float min = 999999;
        int idx = 0;
        for(int i = 0; i < n; i++) {
            if (i == 0) {
                min = CalculateCPDistance(current, ref.get(i));
                idx = i;
            } else {
                if (CalculateCPDistance(current, ref.get(i)) < min)
                    min = CalculateCPDistance(current, ref.get(i));
                    idx = i;
            }
        }

        updateTextView(ref.get(idx).toString()); //Get COntrol point name
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

        Bitmap newBitmap = processedBitmap;
        for(int i = 0; i < newBoundingBox.size();i++){
            if(newBoundingBox.get(i).valid(50))
                newBitmap = newBoundingBox.get(i).drawBox(newBitmap, Color.RED);
        }
        return newBitmap;
    }

    private Bitmap drawEyesComponentCandidate(Bitmap bitmap){
        List<Box[]> eyesComponentBoxList = new ArrayList<>();
        List<Integer> idx = new ArrayList<>();
        for(int i = 0; i < newBoundingBox.size();i++) {
                int[][] label = getLabel(bufferBitmap, (int) newBoundingBox.get(i).top, (int) newBoundingBox.get(i).bottom, (int) newBoundingBox.get(i).left, (int) newBoundingBox.get(i).right, Color.BLACK);
                eyesComponentBoxList.add(getRegion(label));
                idx.add(i);
        }

        for(int i = 0; i < eyesComponentBoxList.size();i++){
            Box box = new Box((int)newBoundingBox.get(idx.get(i)).top,(int)newBoundingBox.get(idx.get(i)).bottom,newBoundingBox.get(idx.get(i)).left, newBoundingBox.get(idx.get(i)).right);
            List<Box> validEye = getFaceComponent(Arrays.asList(eyesComponentBoxList.get(i)), newBoundingBox.get(i));
            validEye = getValidEye(validEye, box.getSize());

            for (int j = 0; j < validEye.size();j++){
                int y = (int)newBoundingBox.get(idx.get(i)).top;
                int x = (int)newBoundingBox.get(idx.get(i)).left;
                bitmap = validEye.get(j).drawBox(bitmap, x, y , Color.GREEN);
            }
            if(validEye.size() == 2)
                bitmap = newBoundingBox.get(idx.get(i)).drawBox(bitmap, 0, 0, Color.RED);
        }
        return bitmap;
    }

    private Bitmap getFaceComponentCandidate(Bitmap bitmap) {
        List<Box[]> faceComponentsBoxList = new ArrayList<>();
        List<Integer> idx = new ArrayList<>();
        for(int i = 0; i < newBoundingBox.size();i++) {
            int[][] label = getLabel(bufferBitmap, (int) newBoundingBox.get(i).top, (int) newBoundingBox.get(i).bottom, (int) newBoundingBox.get(i).left, (int) newBoundingBox.get(i).right, Color.BLACK);
            faceComponentsBoxList.add(getRegion(label));
            idx.add(i);
        }
        for(int i = 0; i < faceComponentsBoxList.size();i++){
            List<Box> validFaceComponentsBoxList = getFaceComponent(Arrays.asList(faceComponentsBoxList.get(i)), newBoundingBox.get(i));


            for (int j = 0; j < validFaceComponentsBoxList.size();j++){
                int y = (int)newBoundingBox.get(idx.get(i)).top;
                int x = (int)newBoundingBox.get(idx.get(i)).left;
                bitmap = validFaceComponentsBoxList.get(j).drawBox(bitmap, x, y , Color.BLUE);
            }
            if (validFaceComponentsBoxList.size() > 3) {
                bitmap = newBoundingBox.get(idx.get(i)).drawBox(bitmap, 0, 0, Color.RED);
            }
        }
        return bitmap;
    }

    private Bitmap showComponentWithSameRow(Bitmap bitmap) {
        List<Box[]> faceComponentsBoxList = new ArrayList<>();

        List<Integer> idx = new ArrayList<>();

        for(int i = 0; i < newBoundingBox.size();i++) {
            int[][] label = getLabel(bufferBitmap, (int) newBoundingBox.get(i).top, (int) newBoundingBox.get(i).bottom, (int) newBoundingBox.get(i).left, (int) newBoundingBox.get(i).right, Color.BLACK);
            faceComponentsBoxList.add(getRegion(label));
            idx.add(i);
        }

        for(int i = 0; i < faceComponentsBoxList.size();i++){
            List<Box> validFaceComponentsBoxList = getFaceComponent(Arrays.asList(faceComponentsBoxList.get(i)), newBoundingBox.get(i));
            validFaceComponentsBoxList = getComponentWithSameRow(validFaceComponentsBoxList, newBoundingBox.get(i));
            for (int j = 0; j < validFaceComponentsBoxList.size();j++){
                int y = (int)newBoundingBox.get(idx.get(i)).top;
                int x = (int)newBoundingBox.get(idx.get(i)).left;
                bitmap = validFaceComponentsBoxList.get(j).drawBox(bitmap, x, y , Color.BLUE);
            }
            if (validFaceComponentsBoxList.size() > 3) {
                bitmap = newBoundingBox.get(idx.get(i)).drawBox(bitmap, 0, 0, Color.RED);
            }
        }
        return bitmap;
    }

    private Bitmap showEyes(Bitmap bitmap) {
        List<Box[]> faceComponentsBoxList = new ArrayList<>();

        List<Integer> idx = new ArrayList<>();

        for(int i = 0; i < newBoundingBox.size();i++) {
            int[][] label = getLabel(bufferBitmap, (int) newBoundingBox.get(i).top, (int) newBoundingBox.get(i).bottom, (int) newBoundingBox.get(i).left, (int) newBoundingBox.get(i).right, Color.BLACK);
            faceComponentsBoxList.add(getRegion(label));
            idx.add(i);
        }

        for(int i = 0; i < faceComponentsBoxList.size();i++){
            // 1. seleksi dengan memilih kandidat komponen wajah
            List<Box> validFaceComponentsBoxList = getFaceComponent(Arrays.asList(faceComponentsBoxList.get(i)), newBoundingBox.get(i));
            List<Box> eyesComponentsBoxList;
            List<Box> noseComponentsBoxList;
            if (validFaceComponentsBoxList.size() > 3) {
                // 2. seleksi komponen yang punya tinggi sama
                validFaceComponentsBoxList = getComponentWithSameRow(validFaceComponentsBoxList, newBoundingBox.get(i));

                // 3. eliminasi komponen yang ukurannya sama
                validFaceComponentsBoxList = eleminatePairSameRow(validFaceComponentsBoxList, newBoundingBox.get(i));

                // 4. Mata Merah
                eyesComponentsBoxList = getEyesFromPairSameRowandSize(validFaceComponentsBoxList);
                for (int j = 0; j < eyesComponentsBoxList.size(); j++) {
                    int y = (int) newBoundingBox.get(idx.get(i)).top;
                    int x = (int) newBoundingBox.get(idx.get(i)).left;
                    bitmap = eyesComponentsBoxList.get(j).drawBox(bitmap, x, y, Color.RED);
                }

                bitmap = newBoundingBox.get(idx.get(i)).drawBox(bitmap, 0, 0, Color.RED);
            }
        }
        return bitmap;
    }


    private Bitmap showAllFaceComponent(Bitmap bitmap) {
        List<Box[]> faceComponentsBoxList = new ArrayList<>();

        List<Integer> idx = new ArrayList<>();

        for(int i = 0; i < newBoundingBox.size();i++) {
            int[][] label = getLabel(bufferBitmap, (int) newBoundingBox.get(i).top, (int) newBoundingBox.get(i).bottom, (int) newBoundingBox.get(i).left, (int) newBoundingBox.get(i).right, Color.BLACK);
            faceComponentsBoxList.add(getRegion(label));
            idx.add(i);
        }

        for(int i = 0; i < faceComponentsBoxList.size();i++){
            // 1. seleksi dengan memilih kandidat komponen wajah
            List<Box> validFaceComponentsBoxList = getFaceComponent(Arrays.asList(faceComponentsBoxList.get(i)), newBoundingBox.get(i));
            List<Box> mouthComponentBoxList = getFaceComponent(Arrays.asList(faceComponentsBoxList.get(i)), newBoundingBox.get(i));
            List<Box> eyesComponentsBoxList;
            List<Box> noseComponentsBoxList;

            faceComponentCandidate.removeAll(faceComponentCandidate);

            for (int x =0; x < 5; x++) {
                faceComponentCandidate.add(new Box());

            }

            if (validFaceComponentsBoxList.size() > 3) {

                // 2. seleksi komponen yang punya tinggi sama
                validFaceComponentsBoxList = getComponentWithSameRow(validFaceComponentsBoxList, newBoundingBox.get(i));

                // 3. eliminasi komponen yang ukurannya sama
                validFaceComponentsBoxList = eleminatePairSameRow(validFaceComponentsBoxList, newBoundingBox.get(i));


                // 4. Mata Merah
                eyesComponentsBoxList = getEyesFromPairSameRowandSize(validFaceComponentsBoxList);
                for (int j = 0; j < eyesComponentsBoxList.size(); j++) {
                    int y = (int) newBoundingBox.get(idx.get(i)).top;
                    int x = (int) newBoundingBox.get(idx.get(i)).left;
                    bitmap = eyesComponentsBoxList.get(j).drawBox(bitmap, x, y, Color.RED);
                }

                faceComponentCandidate.set(0,eyesComponentsBoxList.get(0));
                faceComponentCandidate.set(1,eyesComponentsBoxList.get(1));

                // 5. Hidung hijau
                noseComponentsBoxList = getNoseFromPairSameRowandSize(validFaceComponentsBoxList);
                for (int j = 0; j < noseComponentsBoxList.size(); j++) {
                    int y = (int) newBoundingBox.get(idx.get(i)).top;
                    int x = (int) newBoundingBox.get(idx.get(i)).left;
                    bitmap = noseComponentsBoxList.get(j).drawBox(bitmap, x, y, Color.GREEN);
                }

                faceComponentCandidate.set(2,noseComponentsBoxList.get(0));
                faceComponentCandidate.set(3,noseComponentsBoxList.get(1));


                // 6. Kandidat Mulut
//                sameRowComponentsBoxList = getComponentWithSameRow(mouthComponentBoxList, newBoundingBox.get(i));
                mouthComponentBoxList.removeAll(eyesComponentsBoxList);
                mouthComponentBoxList.removeAll(noseComponentsBoxList);
                mouthComponentBoxList = eleminateMouthCandidatebySize(mouthComponentBoxList,newBoundingBox.get(i));
                for (int j = 0; j < mouthComponentBoxList.size(); j++) {
                    int y = (int) newBoundingBox.get(idx.get(i)).top;
                    int x = (int) newBoundingBox.get(idx.get(i)).left;
                    bitmap = mouthComponentBoxList.get(j).drawBox(bitmap, x, y, Color.BLUE);
                }

                faceComponentCandidate.set(4,mouthComponentBoxList.get(0));
                
                bitmap = newBoundingBox.get(idx.get(i)).drawBox(bitmap, 0, 0, Color.RED);
            }
        }
        return bitmap;
    }


    private Bitmap showNose(Bitmap bitmap) {
        List<Box[]> faceComponentsBoxList = new ArrayList<>();

        List<Integer> idx = new ArrayList<>();

        for(int i = 0; i < newBoundingBox.size();i++) {
            int[][] label = getLabel(bufferBitmap, (int) newBoundingBox.get(i).top, (int) newBoundingBox.get(i).bottom, (int) newBoundingBox.get(i).left, (int) newBoundingBox.get(i).right, Color.BLACK);
            faceComponentsBoxList.add(getRegion(label));
            idx.add(i);
        }

        for(int i = 0; i < faceComponentsBoxList.size();i++){
            // 1. seleksi dengan memilih kandidat komponen wajah
            List<Box> validFaceComponentsBoxList = getFaceComponent(Arrays.asList(faceComponentsBoxList.get(i)), newBoundingBox.get(i));
            List<Box> noseComponentsBoxList;
            if (validFaceComponentsBoxList.size() > 3) {
                // 2. seleksi komponen yang punya tinggi sama
//                validFaceComponentsBoxList = getComponentWithSameRow(validFaceComponentsBoxList, newBoundingBox.get(i));


                // 3. eliminasi komponen yang ukurannya sama
//                validFaceComponentsBoxList = eleminatePairSameRow(validFaceComponentsBoxList, newBoundingBox.get(i));

                // 4. Hidung hijau ( Kalau dapat hidung )
                noseComponentsBoxList = getNoseFromPairSameRowandSize(validFaceComponentsBoxList);

                for (int j = 0; j < noseComponentsBoxList.size(); j++) {
                    int y = (int) newBoundingBox.get(idx.get(i)).top;
                    int x = (int) newBoundingBox.get(idx.get(i)).left;
                    bitmap = noseComponentsBoxList.get(j).drawBox(bitmap, x, y, Color.GREEN);
                }
                bitmap = newBoundingBox.get(idx.get(i)).drawBox(bitmap, 0, 0, Color.RED);
            }
        }
        return bitmap;
    }

    private Bitmap showMouthCandidate(Bitmap bitmap) {
        List<Box[]> faceComponentsBoxList = new ArrayList<>();
        List<Integer> idx = new ArrayList<>();
        for(int i = 0; i < newBoundingBox.size();i++) {
            int[][] label = getLabel(bufferBitmap, (int) newBoundingBox.get(i).top, (int) newBoundingBox.get(i).bottom, (int) newBoundingBox.get(i).left, (int) newBoundingBox.get(i).right, Color.BLACK);
            faceComponentsBoxList.add(getRegion(label));
            idx.add(i);
        }
        for(int i = 0; i < faceComponentsBoxList.size();i++){
            // 1. seleksi kandidat komponen wajah
            List<Box> validFaceComponentsBoxList = getFaceComponent(Arrays.asList(faceComponentsBoxList.get(i)), newBoundingBox.get(i));
            List<Box> sameRowComponentsBoxList;
            if (validFaceComponentsBoxList.size() > 3) {
                // 2. simpan komponen yang punya tinggi sama
                sameRowComponentsBoxList = getComponentWithSameRow(validFaceComponentsBoxList, newBoundingBox.get(i));
                // 3. eliminasi komponen dengan tinggi sama (hidung dan mata)
                validFaceComponentsBoxList.removeAll(sameRowComponentsBoxList);
                validFaceComponentsBoxList = eleminateMouthCandidatebySize(validFaceComponentsBoxList,newBoundingBox.get(i));

                for (int j = 0; j < validFaceComponentsBoxList.size(); j++) {
                    int y = (int) newBoundingBox.get(idx.get(i)).top;
                    int x = (int) newBoundingBox.get(idx.get(i)).left;
                    bitmap = validFaceComponentsBoxList.get(j).drawBox(bitmap, x, y, Color.BLUE);
                }
                bitmap = newBoundingBox.get(idx.get(i)).drawBox(bitmap, 0, 0, Color.RED);
            }
        }
        return bitmap;
    }


    private List<Box> eleminateMouthCandidatebySize(List<Box> faceComponentsBoxList, Box faceBox) {
        List<Box> candidateBoxList = new ArrayList<>();
        List<Box> mouthCandidate = new ArrayList<>();


        float width = Math.abs(faceBox.left - faceBox.right);
        float minWidthDistance = 0.05f* width;

        for(int i = 0;i < faceComponentsBoxList.size();i++){
            candidateBoxList.add(faceComponentsBoxList.get(i));
        }

        // ambil seluruh kandidate dengan ukuran yang mungkin
        for(int i = 0; i < candidateBoxList.size() - 1; i++){
            if ( Math.abs(candidateBoxList.get(i).right-candidateBoxList.get(i).left) > width*0.3f) {
                mouthCandidate.add(candidateBoxList.get(i));
            }
        }
        return mouthCandidate;
    }


    // Mengambil  PAIR kandidat yang posisi nya sejajar
    private List<Box> getComponentWithSameRow(List<Box> faceComponentsBoxList, Box faceBox){
        List<Box> candidateBoxList = new ArrayList<>();
        List<Box> pairComponentsWithSameRow = new ArrayList<>();
        float height = Math.abs(faceBox.bottom - faceBox.top);

        for(int i = 0;i < faceComponentsBoxList.size();i++){
            candidateBoxList.add(faceComponentsBoxList.get(i));
        }


        // dipakai width aja karna gambar masih nangkap leher
        float minWidthDistance = 0.05f* height;

            // ambil seluruh pair yang sejajar dengan threshold 5% dari tinggi faceBox
            for(int i = 0; i < candidateBoxList.size();i++){
                for(int j = i+1; j < candidateBoxList.size();j++){
                    if(i != j){
                        if(boxHeightDistance(candidateBoxList.get(i),candidateBoxList.get(j)) < minWidthDistance ){
                            if( !box2Insidebox1(candidateBoxList.get(i),candidateBoxList.get(j)) &&
                                !box2Insidebox1(candidateBoxList.get(j),candidateBoxList.get(i))) {
                                pairComponentsWithSameRow.add(candidateBoxList.get(i));
                                pairComponentsWithSameRow.add(candidateBoxList.get(j));
                            }
                        }
                    }
                }
            }

        return pairComponentsWithSameRow;
    }


    private List<Box> eleminatePairSameRow(List<Box> faceComponentsBoxList, Box faceBox) {
        List<Box> candidateBoxList = new ArrayList<>();
        List<Box> pairComponentsWithSameRowandSimilarSize = new ArrayList<>();


        float width = Math.abs(faceBox.left - faceBox.right);

        for(int i = 0;i < faceComponentsBoxList.size();i++){
            candidateBoxList.add(faceComponentsBoxList.get(i));
        }

        // ambil seluruh pair yang sejajar dengan threshold 5% dari tinggi faceBox
        for(int i = 0; i < candidateBoxList.size() - 1; i=i+2){
            if(isHasSameSize(candidateBoxList.get(i), candidateBoxList.get(i+1),faceBox)){
                if (candidateBoxList.get(i).valid(width*0.1f)) {
                    pairComponentsWithSameRowandSimilarSize.add(candidateBoxList.get(i));
                    pairComponentsWithSameRowandSimilarSize.add(candidateBoxList.get(i+1));
                }
            }
        }
        return pairComponentsWithSameRowandSimilarSize;
    }

    private List<Box> getEyesFromPairSameRowandSize(List<Box> faceComponentsBoxList) {
        List<Box> candidateBoxList = new ArrayList<>();
        List<Box> pairEyes = new ArrayList<>();

        float height1,height2;
        if (faceComponentsBoxList.size() == 4) {
            for (int i = 0; i < 2; i++) {
                candidateBoxList.add(faceComponentsBoxList.get(i));
            }
        } else {
            for (int i = 0; i < 4; i++) {
                candidateBoxList.add(faceComponentsBoxList.get(i));
            }
        }

        return candidateBoxList;
    }

    private List<Box> getNoseFromPairSameRowandSize(List<Box> faceComponentsBoxList) {
        List<Box> candidateBoxList = new ArrayList<>();
        List<Box> noseBox = new ArrayList<>();

        float height1,height2;
        if (faceComponentsBoxList.size() == 4) {
            for (int i = 2; i < 4; i++) {
                candidateBoxList.add(faceComponentsBoxList.get(i));
            }
        } else if (faceComponentsBoxList.size() >6) {
            for (int i = 4; i < 6; i++) {
                candidateBoxList.add(faceComponentsBoxList.get(i));
            }
        }

        return candidateBoxList;
    }


    private boolean isHasSameSize(Box box1, Box box2, Box faceBox) {
        float boxHeight = Math.abs(faceBox.left - faceBox.right);
        float boxWidth = Math.abs(faceBox.left - faceBox.right);

        float widthBox1 = Math.abs(box1.bottom - box1.top);
        float heightBox1 = Math.abs(box1.right - box1.left);
        float widthBox2 = Math.abs(box2.bottom - box2.top);
        float heightBox2 = Math.abs(box2.right - box2.left);

        if (Math.abs(heightBox1*widthBox1- heightBox2*widthBox2) < 0.05*boxHeight*boxWidth) {
            return true;
        } else {
            return false;
        }
    }

    private List<Box> getValidEye(List<Box> eyeBox, float outerBoxSize){
        List<Box> box = new ArrayList<>();
        List<Box> finalBox = new ArrayList<>();
        for(int i = 0;i < eyeBox.size();i++){
            float width = eyeBox.get(i).right - eyeBox.get(i).left;
            float height = eyeBox.get(i).bottom - eyeBox.get(i).top;
            if(eyeBox.get(i).valid(15)){
                box.add(eyeBox.get(i));
            }
        }
        if (box.size() > 2) {
            int firstIdx = 0;
            int secondIdx = 0;
            float minDistance = -1;
            for(int i = 0; i < box.size();i++){
                for(int j = i+1; j < box.size();j++){
                    if(i != j){
                        if(boxHeightDistance(box.get(i),box.get(j)) < minDistance || minDistance == -1){
                            minDistance = boxHeightDistance(box.get(i),box.get(j));
                            firstIdx = i;
                            secondIdx = j;
                        }
                    }
                }
            }
            finalBox.add(box.get(firstIdx));
            finalBox.add(box.get(secondIdx));
        } else if(box.size() == 2) {
            finalBox.add(box.get(0));
            finalBox.add(box.get(1));
        }
        return finalBox;
    }


    //Mendapatkan Calon semua komponen wajah
    private List<Box> getFaceComponent(List<Box> faceComponentCandidate, Box faceBox){
        List<Box> finalBox = new ArrayList<>();
        float width = Math.abs(faceBox.left - faceBox.right);
        float height = Math.abs(faceBox.bottom - faceBox.top);


        for(int i = 0;i < faceComponentCandidate.size();i++){
            //Kalau kotak komponen Berada di sisi wajah
            if ( Math.abs(faceBox.right - (faceBox.left + faceComponentCandidate.get(i).right))  < (0.05*width)   ||
                Math.abs(faceBox.bottom - (faceBox.top + faceComponentCandidate.get(i).bottom))  < (0.05*height) ||
                Math.abs (faceBox.top - (faceBox.top +faceComponentCandidate.get(i).top)) < (0.05*height)  ||
                Math.abs(faceBox.left -(faceBox.left+ faceComponentCandidate.get(i).left)) < (0.05*width)) {

            } else {
                    finalBox.add(faceComponentCandidate.get(i));
            }
        }
        return finalBox;
    }

    private float boxHeightDistance(Box box1, Box box2){
        return Math.abs( (box1.top + ((box1.bottom-box1.top)/2)) - (box2.top + ((box2.bottom-box2.top)/2)) );
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

    private List<Box> getValidBoxRatio(Box[] box){
        List<Box> newBoundingBox = new ArrayList<>();
        for(int i = 0;i < box.length;i++){
            float height = box[i].bottom - box[i].top;
            float width = box[i].right - box[i].left;
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
        final CharSequence[] items ={"Face Candidate",
                "Preprocess Skin Color",
                "Test Control Points",
                "Erode",
                "Dilate",
                "Face Component Candidate",
                "Component with Same Row",
                "Mouth Candidate",
                "Eyes",
                "Nose",
                "All Face Component",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Feature");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(items[which].equals("Face Candidate")) {
                    drawBitmap = getFaceFromBitmap(bitmap);
                    imageViewAfter.setImageBitmap(drawBitmap);
                    imageViewAfter.setVisibility(View.VISIBLE);
                } else if(items[which].equals("Test Control Points")) {
                    testControlPoint();
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
                } else if (items[which].equals(("Eyes"))) {
                    drawBitmap = showEyes(bufferBitmap);
                    imageViewAfter.setImageBitmap(drawBitmap);
                }
                else if(items[which].equals("Nose")){
                    drawBitmap = showNose(bufferBitmap);
                    imageViewAfter.setImageBitmap(drawBitmap);
                }
                else if(items[which].equals("Mouth Candidate")){
                    drawBitmap = showMouthCandidate(bufferBitmap);
                    imageViewAfter.setImageBitmap(drawBitmap);
                }
                else if(items[which].equals("All Face Component")){
                    drawBitmap = showAllFaceComponent(bufferBitmap);
                    imageViewAfter.setImageBitmap(drawBitmap);
                }
                else if (items[which].equals(("Face Component Candidate"))) {
                    drawBitmap = getFaceComponentCandidate(bufferBitmap);
                    imageViewAfter.setImageBitmap(drawBitmap);
                } else if (items[which].equals(("Component with Same Row"))) {
                    drawBitmap = showComponentWithSameRow(bufferBitmap);
                    imageViewAfter.setImageBitmap(drawBitmap);
                }
                else if (items[which].equals("Cancel")) {
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

        for (int j = 0; j < bitmap.getWidth(); j++) {
            for (int i = 0; i < bitmap.getHeight(); i++) {

                for(int a = 0; a < kernel.length; a++) {
                    for(int b = 0; b < kernel[0].length; b++) {
                        neigbour[a][b] = 1;
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
                for (int k = 0; k < kernel.length; k++){
                    for (int l = 0; l < kernel[0].length; l++){
                        if (neigbour[k][l] == 1) {
                            pixel = bitmap.getPixel(j - access_neighbourx[k][l],(i - access_neighboury[k][l]));
                            red +=  (Color.red(pixel) * kernel[k][l]);
                            green +=  (Color.green(pixel) * kernel[k][l]);
                            blue +=  (Color.blue(pixel) * kernel[k][l]);
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
                newBitmap.setPixel(j, i, pix);
            }
        }
        imageViewAfter.setImageBitmap(newBitmap);
        imageViewAfter.setVisibility(View.VISIBLE);
    }

}
