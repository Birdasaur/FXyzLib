package org.fxyz.shapes.primitives;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.DepthTest;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.TriangleMesh;
import org.fxyz.geometry.Face3;
import org.fxyz.geometry.Point3D;

/**
 * SegmentedTorusMesh is based in TorusMesh, but allows cutting the torus in two 
 * directions, in order to have a banner parallel to an uncut torus.
 * Based on a regular 2D TriangleMesh, mapped to a 3D mesh with the torus parametric equations
 * Crop allows cutting/cropping the 2D mesh on the borders
 * If crop ==0  then  a regular torus is formed (thought with slight differences from 
 * TorusMesh)
 */
public class SegmentedSphereMesh extends TexturedMesh {

    private static final int DEFAULT_DIVISIONS = 64;
    private static final int DEFAULT_CROP_Y = 0;
    private static final int DEFAULT_CROP_X = 0;
    private static final double DEFAULT_RADIUS = 5.0D;
    private static final double DEFAULT_START_ANGLE = 0.0D;
    private static final double DEFAULT_X_OFFSET = 0.0D;
    private static final double DEFAULT_Y_OFFSET = 0.0D;
    private static final double DEFAULT_Z_OFFSET = 1.0D;
    private static final boolean DEFAULT_EXTERIOR = true;
    
    public SegmentedSphereMesh() {
        this(DEFAULT_DIVISIONS, DEFAULT_CROP_X, DEFAULT_CROP_Y, DEFAULT_RADIUS);
    }

    public SegmentedSphereMesh(double radius) {
        this(DEFAULT_DIVISIONS, DEFAULT_CROP_X, DEFAULT_CROP_Y, radius);
    }

    public SegmentedSphereMesh(int tDivs, int cropX, int cropY, double radius) {
        setRadiusDivisions(tDivs);
        setRadiusCropX(cropX);
        setRadiusCropY(cropY);
        setRadius(radius);
        setzOffset(1);
        
        updateMesh();
        setCullFace(CullFace.BACK);
        setDrawMode(DrawMode.FILL);
        setDepthTest(DepthTest.ENABLE);
    }

    @Override
    protected final void updateMesh(){       
        setMesh(null);
        mesh=createSegmentedSphere(
            getRadiusDivisions(), 
            getRadiusCropX(),
            getRadiusCropY(),
            (float) getRadius(), 
            (float) getTubeStartAngleOffset(), 
            (float)getxOffset(),
            (float)getyOffset(), 
            (float)getzOffset());
        setMesh(mesh);
    }
    
    private final IntegerProperty radiusDivisions = new SimpleIntegerProperty(DEFAULT_DIVISIONS) {

        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }

    };

    public final int getRadiusDivisions() {
        return radiusDivisions.get();
    }

    public final void setRadiusDivisions(int value) {
        radiusDivisions.set(value);
    }

    public IntegerProperty radiusDivisionsProperty() {
        return radiusDivisions;
    }

    private final IntegerProperty radiusCropX = new SimpleIntegerProperty(DEFAULT_CROP_X) {

        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }

    };
    public final int getRadiusCropX() {
        return radiusCropX.get();
    }

    public final void setRadiusCropX(int value) {
        radiusCropX.set(value);
    }

    public IntegerProperty radiusCropXProperty() {
        return radiusCropX;
    }

    private final IntegerProperty radiusCropY = new SimpleIntegerProperty(DEFAULT_CROP_Y) {

        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }

    };
    public final int getRadiusCropY() {
        return radiusCropY.get();
    }

    public final void setRadiusCropY(int value) {
        radiusCropY.set(value);
    }

    public IntegerProperty radiusCropYProperty() {
        return radiusCropY;
    }

    private final DoubleProperty radius = new SimpleDoubleProperty(DEFAULT_RADIUS) {

        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
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

    private final DoubleProperty tubeStartAngleOffset = new SimpleDoubleProperty(DEFAULT_START_ANGLE) {

        @Override
        protected void invalidated() {
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
    private final DoubleProperty xOffset = new SimpleDoubleProperty(DEFAULT_X_OFFSET) {

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
    private final DoubleProperty yOffset = new SimpleDoubleProperty(DEFAULT_Y_OFFSET) {

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
    private final DoubleProperty zOffset = new SimpleDoubleProperty(DEFAULT_Z_OFFSET) {

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

    public final void setzOffset(double value) {
        zOffset.set(value);
    }

    public DoubleProperty zOffsetProperty() {
        return zOffset;
    }
    private final BooleanProperty exterior = new SimpleBooleanProperty(DEFAULT_EXTERIOR) {

        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }

    };

    public boolean isExterior() {
        return exterior.get();
    }

    public void setExterior(boolean value) {
        exterior.set(value);
    }

    public BooleanProperty exteriorProperty() {
        return exterior;
    }
    
    private TriangleMesh createSegmentedSphere(int subDivY, int cropX, int cropY,
            float radius, float tubeStartAngle, float xOffset, float yOffset, float zOffset) {
 
        listVertices.clear();
        listTextures.clear();
        listFaces.clear();
        
        int subDivX=subDivY;
        
        int numDivX = subDivX + 1-2*cropX;
        float pointX, pointY, pointZ;
        
        areaMesh.setWidth((1-2*cropX/subDivX)*2d*Math.PI*radius);
        areaMesh.setHeight((1-2*cropY/subDivY)*2d*Math.PI*radius);
        
        // Create points
        for (int y = cropY; y <= subDivY-cropY; y++) {
            float dy = (float) y / subDivY;
            for (int x = cropX; x <= subDivX-cropX; x++) {
                float dx = (float) x / subDivX;
                if(cropX>0 || (cropX==0 && x<subDivX)){
                    pointX = (float) ((radius*Math.sin((-1d+dy)*Math.PI))*(Math.cos((-1d+2d*dx)*Math.PI)+ xOffset));
                    pointZ = (float) ((radius*Math.sin((-1d+dy)*Math.PI))*(Math.sin((-1d+2d*dx)*Math.PI)+ yOffset));
                    pointY = (float) (radius*Math.cos((-1d+dy)*Math.PI)*zOffset);
                    listVertices.add(new Point3D(pointX, pointY, pointZ));
                }
            }
        }
        // Create texture coordinates
//        if(exterior.get()){
            createTexCoords(subDivX-2*cropX,subDivY-2*cropY);
//        } else {
//            createReverseTexCoords(subDivX-2*crop,subDivY-2*crop);
//        }
        
        // Create textures indices
        for (int y = cropY; y < subDivY-cropY; y++) {
            for (int x = cropX; x < subDivX-cropX; x++) {
                int p00 = (y-cropY) * numDivX + (x-cropX);
                int p01 = p00 + 1;
                int p10 = p00 + numDivX;
                int p11 = p10 + 1;
                if(exterior.get()){
                    if(y<subDivY-1){
                        listTextures.add(new Face3(p00,p10,p11));   
                    }             
                    if(y>0){
                        listTextures.add(new Face3(p11,p01,p00));
                    }
                } else {
                    if(y<subDivY-1){
                        listTextures.add(new Face3(p00,p11,p10));                
                    }
                    if(y>0){
                        listTextures.add(new Face3(p11,p00,p01));
                    }
                }
            }
        }
        // Create faces indices
        for (int y = cropY; y < subDivY-cropY; y++) {
            for (int x = cropX; x < subDivX-cropX; x++) {
                int p00 = (y-cropY) * ((cropX>0)?numDivX:numDivX-1) + (x-cropX);
                int p01 = p00 + 1;
                if(cropX==0 && x==subDivX-1){
                    p01-=subDivX;
                }
                int p10 = p00 + ((cropX>0)?numDivX:numDivX-1);
//                if(cropY==0 && y==subDivY-1){
//                    p10-=subDivY*((cropX>0)?numDivX:numDivX-1);
//                }
                int p11 = p10 + 1;
                if(cropX==0 && x==subDivX-1){
                    p11-=subDivX;
                }                
                if(exterior.get()){
                    if(y<subDivY-1){
                        listFaces.add(new Face3(p00,p10,p11));   
                    }
                    if(y>0){
                        listFaces.add(new Face3(p11,p01,p00));
                    }
                } else {
                    if(y<subDivY-1){
                        listFaces.add(new Face3(p00,p11,p10));
                    }                
                    if(y>0){
                        listFaces.add(new Face3(p11,p00,p01));
                    }
                }
            }
        }
        return createMesh();
    }

}
