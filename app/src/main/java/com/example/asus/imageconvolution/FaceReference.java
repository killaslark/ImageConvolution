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
            //Bobby Indra Nainggolan
            {
                //Left eye, right eye, left nose, right nose, mouth
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
            },
            //Kukuh Basuki Rahmat
            {
                    //Left eye, right eye, left nose, right nose, mouth
                    new ControlPoint(
                            new Point[]{
                                    new Point(16, 45),
                                    new Point(17, 42),
                                    new Point(24, 42),
                                    new Point(30, 43),
                                    new Point(33, 45),
                                    new Point(29, 47),
                                    new Point(24, 47),
                                    new Point(17, 48)
                            }),
                    new ControlPoint(
                            new Point[]{
                                    new Point(54, 45),
                                    new Point(54, 43),
                                    new Point(60, 42),
                                    new Point(66, 43),
                                    new Point(69, 45),
                                    new Point(62, 46),
                                    new Point(60, 48),
                                    new Point(55, 47)
                            }),
                    new ControlPoint(
                            new Point[]{
                                    new Point(34, 68),
                                    new Point(34, 67),
                                    new Point(37, 67),
                                    new Point(39, 67),
                                    new Point(40, 68),
                                    new Point(40, 70),
                                    new Point(37, 70),
                                    new Point(34, 70)
                            }),
                    new ControlPoint(
                            new Point[]{
                                    new Point(44, 69),
                                    new Point(44, 67),
                                    new Point(47, 67),
                                    new Point(50, 68),
                                    new Point(51, 69),
                                    new Point(51, 71),
                                    new Point(47, 71),
                                    new Point(44, 71)
                            }),
                    new ControlPoint(
                            new Point[]{
                                    new Point(37, 84),
                                    new Point(28, 80),
                                    new Point(41, 81),
                                    new Point(54, 80),
                                    new Point(54, 84),
                                    new Point(47, 86),
                                    new Point(41, 86),
                                    new Point(38, 85)

                            })
            },
            //Winarto
            {
                    //Left eye, right eye, left nose, right nose, mouth
                    new ControlPoint(
                            new Point[]{
                                    new Point(11, 49),
                                    new Point(15, 46),
                                    new Point(23, 45),
                                    new Point(34, 46),
                                    new Point(36, 49),
                                    new Point(33, 53),
                                    new Point(23, 54),
                                    new Point(19, 51)
                            }),
                    new ControlPoint(
                            new Point[]{
                                    new Point(70, 51),
                                    new Point(70, 46),
                                    new Point(80, 46),
                                    new Point(88, 48),
                                    new Point(92, 51),
                                    new Point(84, 53),
                                    new Point(80, 56),
                                    new Point(73, 54)
                            }),
                    new ControlPoint(
                            new Point[]{
                                    new Point(39, 81),
                                    new Point(39, 79),
                                    new Point(42, 79),
                                    new Point(46, 79),
                                    new Point(46, 81),
                                    new Point(46, 83),
                                    new Point(42, 83),
                                    new Point(39, 83)
                            }),
                    new ControlPoint(
                            new Point[]{
                                    new Point(58, 81),
                                    new Point(58, 79),
                                    new Point(62, 79),
                                    new Point(66, 79),
                                    new Point(66, 81),
                                    new Point(65, 83),
                                    new Point(62, 84),
                                    new Point(58, 84)
                            }),
                    new ControlPoint(
                            new Point[]{
                                    new Point(40, 105),
                                    new Point(38, 99),
                                    new Point(50, 99),
                                    new Point(61, 100),
                                    new Point(62, 105),
                                    new Point(60, 111),
                                    new Point(50, 112),
                                    new Point(42, 110)

                            })
            }

    };

    public FaceReference(){
        controlPoint = new ControlPoint[1][5];


    }
}
