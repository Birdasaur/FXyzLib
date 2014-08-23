/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jtp.fxyz.experimental;

import javafx.geometry.Point3D;

/**
 *
 * @author Dub
 */
public class Direction3D {
    public static Point3D forward(){
        return new Point3D(0,0,1);
    }
    public static Point3D backward(){
        return new Point3D(0,0,-1);
    }
    public static Point3D up(){
        return new Point3D(0,1,0);
    }
    public static Point3D down(){
        return new Point3D(0,-1,0);
    }
    public static Point3D right(){
        return new Point3D(1,0,0);
    }
    public static Point3D left(){
        return new Point3D(-1,0,0);
    }
}
