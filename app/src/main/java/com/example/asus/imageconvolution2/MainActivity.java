package com.example.asus.imageconvolution2;

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
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import static java.lang.Math.sqrt;

public class MainActivity extends AppCompatActivity {

    private ArrayList<double[][]> selectedKernel = new ArrayList<>();
    private ImageView imageViewBefore,imageViewAfter;
    private EditText[][] matrixView = new EditText[3][3];
    private Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;
    private Bitmap bitmap,secondBitmap;
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

            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                InputStream inputStream;
                try {
                    inputStream = getContentResolver().openInputStream(selectedImageUri);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    secondBitmap = bitmap;
                    imageViewBefore.setImageBitmap(bitmap);

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

        final int[] faceBox = getFaceBox(processedBitmap);
        final int[] mouthBox = getmouthBox(processedBitmap,faceBox);

        Parallel.For(0, height, new LoopBody<Integer>() {
            @Override
            public void run(Integer y) {
                int[] resultPixels = new int[width];
                processedBitmap.getPixels(resultPixels, 0, width, 0, y, width, 1);

                for (int x = 0; x < width; ++x) {

                    resultPixels[x] = (x == faceBox[3] || x == faceBox[0] || y == faceBox[1] || y == faceBox[2]) ? Color.RED : resultPixels[x];
                }
                processedBitmap.setPixels(resultPixels, 0, width, 0, y, width, 1);
            }
        });

        return processedBitmap;
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

    private int[] getmouthBox(Bitmap bitmap, int[] faceBox) {
        int minWidth = faceBox[0];
        int maxWidth = faceBox[3] + 1 ;
        int minHeight = faceBox[2] + 1 ;
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
                    if (j < minHeight) minHeight = i;

                }
            }
        }
        int[] borderPoint = new int[]{minWidth,minHeight,maxHeight,maxWidth};
        Log.d("X", ""+borderPoint[0]+" "+borderPoint[1]+" "+borderPoint[2]+ " "+ borderPoint[3]);
        return borderPoint;
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
        final CharSequence[] items ={"Face Detect",
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
                    imageViewAfter.setImageBitmap(getFaceFromBitmap(bitmap));
                    imageViewAfter.setVisibility(View.VISIBLE);
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
