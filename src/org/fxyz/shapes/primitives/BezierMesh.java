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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.DepthTest;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.TriangleMesh;
import org.fxyz.geometry.Face3;
import org.fxyz.shapes.primitives.helper.BezierHelper;

/**
 *  Spring based on this model:  http://en.wikipedia.org/wiki/Trefoil_knot
 *  Wrapped around a torus: http://mathoverflow.net/a/91459
    *  Using Frenet-Serret trihedron: http://mathematica.stackexchange.com/a/18612
 */
public class BezierMesh extends TexturedMesh {

    private static final double DEFAULT_WIRE_RADIUS = 0.2D;
    
    private static final int DEFAULT_LENGTH_DIVISIONS = 200;
    private static final int DEFAULT_WIRE_DIVISIONS = 50;
    private static final int DEFAULT_LENGTH_CROP = 0;
    private static final int DEFAULT_WIRE_CROP = 0;
    
    private static final double DEFAULT_START_ANGLE = 0.0D;
    private static final double DEFAULT_X_OFFSET = 0.0D;
    private static final double DEFAULT_Y_OFFSET = 0.0D;
    private static final double DEFAULT_Z_OFFSET = 1.0D;
    
    public BezierMesh(BezierHelper spline) {
        this(spline, DEFAULT_WIRE_RADIUS, 
             DEFAULT_LENGTH_DIVISIONS, DEFAULT_WIRE_DIVISIONS, DEFAULT_LENGTH_CROP, DEFAULT_WIRE_CROP);
    }

    public BezierMesh(BezierHelper spline, double wireRadius) {
        this(spline, wireRadius, 
             DEFAULT_LENGTH_DIVISIONS, DEFAULT_WIRE_DIVISIONS, DEFAULT_LENGTH_CROP, DEFAULT_WIRE_CROP);
    }

    public BezierMesh(BezierHelper spline, double wireRadius, 
                      int rDivs, int tDivs, int lengthCrop, int wireCrop) {
        
        setSpline(spline);
        setWireRadius(wireRadius);
        setLengthDivisions(rDivs);
        setWireDivisions(tDivs);
        setLengthCrop(lengthCrop);
        setWireCrop(wireCrop);
        
        updateMesh();
        setCullFace(CullFace.BACK);
        setDrawMode(DrawMode.FILL);
        setDepthTest(DepthTest.ENABLE);
    }

    @Override
    protected final void updateMesh(){   
        setMesh(null);
        mesh=createBezier(getSpline(), (float) getWireRadius(), 
            getLengthDivisions(), getWireDivisions(), getLengthCrop(), getWireCrop(),
            (float) getTubeStartAngleOffset(), (float)getxOffset(),(float)getyOffset(), (float)getzOffset());
        setMesh(mesh);
    }
    
    private final ObjectProperty<BezierHelper> spline = new SimpleObjectProperty<BezierHelper>(){
        @Override protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };
    
    public final BezierHelper getSpline(){
        return spline.get();
    }
    
    public final void setSpline(BezierHelper spline){
        this.spline.set(spline);
    }
    
    public ObjectProperty<BezierHelper> splineProperty(){
        return spline;
    }
    
    private final DoubleProperty wireRadius = new SimpleDoubleProperty(DEFAULT_WIRE_RADIUS){

        @Override protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public final double getWireRadius() {
        return wireRadius.get();
    }

    public final void setWireRadius(double value) {
        wireRadius.set(value);
    }

    public DoubleProperty wireRadiusProperty() {
        return wireRadius;
    }

    private final IntegerProperty lengthDivisions = new SimpleIntegerProperty(DEFAULT_LENGTH_DIVISIONS){

        @Override protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public final int getLengthDivisions() {
        return lengthDivisions.get();
    }

    public final void setLengthDivisions(int value) {
        lengthDivisions.set(value);
    }

    public IntegerProperty lengthDivisionsProperty() {
        return lengthDivisions;
    }

    private final IntegerProperty wireDivisions = new SimpleIntegerProperty(DEFAULT_WIRE_DIVISIONS){

        @Override protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public final int getWireDivisions() {
        return wireDivisions.get();
    }

    public final void setWireDivisions(int value) {
        wireDivisions.set(value);
    }

    public IntegerProperty wireDivisionsProperty() {
        return wireDivisions;
    }

    private final IntegerProperty lengthCrop = new SimpleIntegerProperty(DEFAULT_LENGTH_CROP){

        @Override protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };
    
    public final int getLengthCrop() {
        return lengthCrop.get();
    }

    public final void setLengthCrop(int value) {
        lengthCrop.set(value);
    }

    public IntegerProperty lengthCropProperty() {
        return lengthCrop;
    }

    private final IntegerProperty wireCrop = new SimpleIntegerProperty(DEFAULT_WIRE_CROP){

        @Override protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };
    
    public final int getWireCrop() {
        return wireCrop.get();
    }

    public final void setWireCrop(int value) {
        wireCrop.set(value);
    }

    public IntegerProperty wireCropProperty() {
        return wireCrop;
    }
    
    private final DoubleProperty tubeStartAngleOffset = new SimpleDoubleProperty(DEFAULT_START_ANGLE){

        @Override protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public final double getTubeStartAngleOffset() {
        return tubeStartAngleOffset.get();
    }

    public void setTubeStartAngleOffset(double value) {
        tubeStartAngleOffset.set(value);
    }

    public DoubleProperty tubeStartAngleOffsetProperty() {
        return tubeStartAngleOffset;
    }
    private final DoubleProperty xOffset = new SimpleDoubleProperty(DEFAULT_X_OFFSET){

        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
        
    };

    public final double getxOffset() {
        return xOffset.get();
    }

    public void setxOffset(double value) {
        xOffset.set(value);
    }

    public DoubleProperty xOffsetProperty() {
        return xOffset;
    }
    private final DoubleProperty yOffset = new SimpleDoubleProperty(DEFAULT_Y_OFFSET){

        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
        
    };

    public final double getyOffset() {
        return yOffset.get();
    }

    public void setyOffset(double value) {
        yOffset.set(value);
    }

    public DoubleProperty yOffsetProperty() {
        return yOffset;
    }
    private final DoubleProperty zOffset = new SimpleDoubleProperty(DEFAULT_Z_OFFSET){

        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
        
    };

    public final double getzOffset() {
        return zOffset.get();
    }

    public void setzOffset(double value) {
        zOffset.set(value);
    }

    public DoubleProperty zOffsetProperty() {
        return zOffset;
    }
    
    private TriangleMesh createBezier(BezierHelper spline, float wireRadius, 
            int subDivLength, int subDivWire, int cropLength, int cropWire,
            float startAngle, float xOffset, float yOffset, float zOffset) {
 
        listVertices.clear();
        listTextures.clear();
        listFaces.clear();
        
        int numDivLength = subDivLength + 1-2*cropLength;
        int numDivWire = subDivWire + 1-2*cropWire;
        double a=wireRadius;
        
        areaMesh.setWidth(spline.getLength());
        areaMesh.setHeight(polygonalSize(wireRadius));
        
        spline.calculateTrihedron(subDivLength);
        for (int t = cropLength; t <= subDivLength-cropLength; t++) {  // 0 - length
            for (int u = cropWire; u <= subDivWire-cropWire; u++) { // -Pi - +Pi
                if(cropWire>0 || (cropWire==0 && u<subDivWire)){
                    float du = (float) (((double)u)*2d*Math.PI / ((double)subDivWire));
                    double pol = polygonalSection(du);
                    float cu=(float)(a*pol*Math.cos(du)), su=(float)(a*pol*Math.sin(du)); 
                    listVertices.add(spline.getS(t, cu, su));
                }
            }
        }
        // Create texture coordinates
        createReverseTexCoords(subDivLength-2*cropLength,subDivWire-2*cropWire);
        
        // Create textures
        for (int t = cropLength; t < subDivLength-cropLength; t++) { // 0 - length
            for (int u = cropWire; u < subDivWire-cropWire; u++) { // -Pi - +Pi
                int p00 = (u-cropWire) + (t-cropLength)* numDivWire;
                int p01 = p00 + 1;
                int p10 = p00 + numDivWire;
                int p11 = p10 + 1;
                listTextures.add(new Face3(p00,p01,p11));
                listTextures.add(new Face3(p11,p10,p00));            
            }
        }
        // Create faces
        for (int t = cropLength; t < subDivLength-cropLength; t++) { // 0 - length
            for (int u = cropWire; u < subDivWire-cropWire; u++) { // -Pi - +Pi
                int p00 = (u-cropWire) + (t-cropLength)* (cropWire==0?subDivWire:numDivWire);
                int p01 = p00 + 1;
                int p10 = p00 + (cropWire==0?subDivWire:numDivWire);
                int p11 = p10 + 1;
                if(cropWire==0 && u==subDivWire-1){
                    p01-=subDivWire;
                    p11-=subDivWire;
                }
                listFaces.add(new Face3(p00,p01,p11));
                listFaces.add(new Face3(p11,p10,p00));            
            }
        }
        return createMesh();
    }
    
}
