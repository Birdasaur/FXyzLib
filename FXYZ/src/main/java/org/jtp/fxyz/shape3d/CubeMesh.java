/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jtp.fxyz.shape3d;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/**
 *
 * @author Dub
 */
public class CubeMesh extends MeshView {
    
    private final static double DEFAULT_SIZE = 10;

    public CubeMesh() {
        this(DEFAULT_SIZE);
    }
    
    public CubeMesh(double size) {
        setSize(size);
        setMesh(createCube((float)getSize()));
    }    
    
    
    static TriangleMesh createCube(float size) {
        TriangleMesh m = new TriangleMesh();

        float hw = size / 2,
                hh = hw,
                hd = hh;

        //create points
        m.getPoints().addAll(
            hw, hh, hd,
            hw, hh, -hd,
            hw, -hh, hd,
            hw, -hh, -hd,
            -hw, hh, hd,
            -hw, hh, -hd,
            -hw, -hh, hd,
            -hw, -hh, -hd
        );
        float x0 = 0.0f, x1 = 1.0f / 4.0f, x2 = 2.0f / 4.0f, x3 =  3.0f / 4.0f, x4 = 1.0f,
        y0 = 0.0f, y1 = 1.0f /3.0f, y2 = 2.0f / 3.0f, y3 = 1.0f;
        m.getTexCoords().addAll(
            (x1),(y0),
            (x2), y0,
            (x0),(y1),
            (x1),(y1),
            (x2),(y1),
             x3, (y1),
            (x4),(y1),
            (x0),(y2),
            (x1),(y2),
             x2, (y2),
             x3, (y2),
            (x4),(y2),
            (x1),(y3),
             x2, (y3)
        );
        m.getFaces().addAll(
            0, 10, 2, 5, 1, 9,
            2, 5, 3, 4, 1, 9,
            
            4, 7, 5, 8, 6, 2,
            6, 2, 5, 8, 7, 3,
            
            0, 13, 1, 9, 4, 12,
            4, 12, 1, 9, 5, 8,
            
            2, 1, 6, 0, 3, 4,
            3, 4, 6, 0, 7, 3,
            
            0, 10, 4, 11, 2, 5,
            2, 5, 4, 11, 6, 6,
            
            1, 9, 3, 4, 5, 8,
            5, 8, 3, 4, 7, 3
        );
        
        return m;
    }
    private final DoubleProperty size = new SimpleDoubleProperty(DEFAULT_SIZE){

        @Override
        protected void invalidated() {
            setMesh(createCube((float)getSize()));
        }
        
    };

    public final double getSize() {
        return size.get();
    }

    public final void setSize(double value) {
        size.set(value);
    }

    public DoubleProperty sizeProperty() {
        return size;
    }
    
    
}
