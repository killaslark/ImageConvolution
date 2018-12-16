package com.example.asus.imageconvolution;

import android.graphics.Point;

/**
 * Created by Winarto on 12/14/2018.
 */

public class FaceReference {

    public static String[] name = {
        "BOBBY",
        "KUKUH",
        "WINARTO"
    };

    public static ControlPoint[][] controlPoint = new ControlPoint[][]{
            //Left eye, right eye, left nose, right nose, mouth
            {
                new ControlPoint(
                    new Point[]{
                            new Point(38, 21),
                            new Point(38, 25),
                            new Point(43, 21),
                            new Point(45, 23),
                            new Point(46, 25),
                            new Point(48, 30),
                            new Point(43, 29),
                            new Point(39, 29)
                    }),
                new ControlPoint(
                        new Point[]{
                                new Point(9, 27),
                                new Point(9, 24),
                                new Point(15, 22),
                                new Point(23, 23),
                                new Point(25, 27),
                                new Point(23, 32),
                                new Point(15, 33),
                                new Point(7, 32)
                        }),
                new ControlPoint(
                        new Point[]{
                                new Point(24, 48),
                                new Point(24, 47),
                                new Point(26, 47),
                                new Point(28, 47),
                                new Point(28, 48),
                                new Point(28, 49),
                                new Point(26, 49),
                                new Point(24, 49)
                        }),
                new ControlPoint(
                        new Point[]{
                                new Point(36, 48),
                                new Point(36, 47),
                                new Point(36, 47),
                                new Point(37, 47),
                                new Point(37, 48),
                                new Point(37, 49),
                                new Point(36, 49),
                                new Point(36, 49)
                        }),
                new ControlPoint(
                        new Point[]{
                                new Point(23, 67),
                                new Point(16, 58),
                                new Point(27, 58),
                                new Point(31, 64),
                                new Point(35, 67),
                                new Point(28, 68),
                                new Point(27, 68),
                                new Point(26, 68)

})
            }
    };

    public FaceReference(){
        controlPoint = new ControlPoint[1][5];


    }
}
