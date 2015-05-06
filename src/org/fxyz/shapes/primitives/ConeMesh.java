/*
 * Copyright (C) 2013-2015 F(X)yz, 
 * Sean Phillips, Jason Pollastrini and Jose Pereda
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fxyz.shapes.primitives;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/**
 *
 * @author Birdasaur
 * @adapted Dub's CapsuleMesh example
 */
public class ConeMesh extends MeshView{
    /*
        Field vars
    */
    private static final int DEFAULT_DIVISIONS = 32;    
    private static final double DEFAULT_RADIUS = 25.0D;
    private static final double DEFAULT_HEIGHT = 50.0D;
    
    /*
    Constructors
     */
    public ConeMesh() {
        this(DEFAULT_DIVISIONS, DEFAULT_RADIUS, DEFAULT_HEIGHT);
    }
    
    public ConeMesh(double radius, double height){
        this(DEFAULT_DIVISIONS, radius, height);
    }
    
    public ConeMesh(int divisions, double radius, double height) {    
        setDivisions(divisions);
        setRadius(radius);
        setHeight(height);    
        setMesh(createCone(getDivisions(), (float)getRadius(), (float)getHeight()));        
    }

    /*
    Methods
     */
    private TriangleMesh createCone(int divisions, float radius, float height) {
        TriangleMesh mesh = new TriangleMesh();
        //Start with the top of the cone, later we will build our faces from these
        mesh.getPoints().addAll(0,0,0); //Point 0: Top of the Cone        
        //Generate the segments of the bottom circle (Cone Base)
        double segment_angle = 2.0 * Math.PI / divisions;
        float x, z;
        double angle;
        double halfCount = (Math.PI / 2 - Math.PI / (divisions / 2)); 
        // Reverse loop for speed!! der
        for(int i=divisions+1;--i >= 0; ) {
            angle = segment_angle * i;
            x = (float)(radius * Math.cos(angle - halfCount));
            z = (float)(radius * Math.sin(angle - halfCount));
            mesh.getPoints().addAll(x,height,z); 
        }   
        mesh.getPoints().addAll(0,height,0); //Point N: Center of the Cone Base

        //@TODO Birdasaur for now we'll just make an empty texCoordinate group
        //@DUB HELP ME DUBi Wan Kanobi, you are my only hope!
        //I'm not good at determining Texture Coordinates
        mesh.getTexCoords().addAll(0,0); 
        //Add the faces "winding" the points generally counter clock wise
        //Must loop through each face, not including first and last points
        for(int i=1;i<=divisions;i++) {
            mesh.getFaces().addAll( //use dummy texCoords, @TODO Upgrade face code to be real 
                0,0,i+1,0,i,0,           // Vertical Faces "wind" counter clockwise
                divisions+2,0,i,0,i+1,0   // Base Faces "wind" clockwise
            ); 
        }
        return mesh;
    }
    /*
        Properties
    */
    private final DoubleProperty radius = new SimpleDoubleProperty(){
        @Override
        protected void invalidated() {
            setMesh(createCone(getDivisions(), (float)getRadius(), (float)getHeight()));
        }
    };

    public final double getRadius() {
        return radius.get();
    }

    public final void setRadius(double value) {
        radius.set(value);
    }

    public DoubleProperty radiusProperty() {
        return radius;
    }
    
    private final DoubleProperty height = new SimpleDoubleProperty(){
        @Override
        protected void invalidated() {
            setMesh(createCone(getDivisions(), (float)getRadius(), (float)getHeight()));
        }        
    };

    public final double getHeight() {
        return height.get();
    }

    public final void setHeight(double value) {
        height.set(value);
    }

    public DoubleProperty heightProperty() {
        return height;
    }
    private final IntegerProperty divisions = new SimpleIntegerProperty(){
        @Override
        protected void invalidated() {
            setMesh(createCone(getDivisions(), (float)getRadius(), (float)getHeight()));
        }        
    };    
    public final int getDivisions() {
        return divisions.get();
    }

    public final void setDivisions(int value) {
        divisions.set(value);
    }
    public IntegerProperty divisionsProperty() {
        return divisions;
    }    
}