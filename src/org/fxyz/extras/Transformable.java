/*
 * 
 */
package org.fxyz.extras;

import javafx.scene.Node;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

/**
 * An Interface implementation of Xform found in the Molecule Sample
 * 
 * @author Dub
 * @param <T> Node type to be used
 */
public interface Transformable<T extends Node> {

    public enum RotateOrder {
        XYZ,
        XZY,
        YXZ,
        YZX,
        ZXY,
        ZYX,
        USE_AFFINE;

        private RotateOrder() {
        }
    }

    // Simple Transforms
    
    //Rotates
    public Rotate 
            rotateX = new Rotate(0.0, Rotate.X_AXIS),
            rotateY = new Rotate(0.0, Rotate.Y_AXIS),
            rotateZ = new Rotate(0.0, Rotate.Z_AXIS);
    
    public default void setRotate(double x, double y, double z) {
        rotateX.setAngle(x);
        rotateY.setAngle(y);
        rotateZ.setAngle(z);
    }    
    public default void setRotateX(double x) { rotateX.setAngle(x); }
    public default void setRotateY(double y) { rotateY.setAngle(y); }
    public default void setRotateZ(double z) { rotateZ.setAngle(z); }
    
    // Translates
    public Translate 
            t = new Translate(),
            p = new Translate(),
            ip = new Translate();
    
    public default void setTx(double x) { t.setX(x); }
    public default void setTy(double y) { t.setY(y); }
    public default void setTz(double z) { t.setZ(z); }
    public default double getTx() { return t.getX(); }
    public default double getTy() { return t.getY(); }
    public default double getTz() { return t.getZ(); }
    
    // Scale
    public Scale s = new Scale();
    public default void setScale(double scaleFactor) {
        s.setX(scaleFactor);
        s.setY(scaleFactor);
        s.setZ(scaleFactor);
    }
    public default void setScale(double x, double y, double z) {
        s.setX(x);
        s.setY(y);
        s.setZ(z);
    }
    // Transform methods
    public default void setPivot(double x, double y, double z) {
        p.setX(x);
        p.setY(y);
        p.setZ(z);
        ip.setX(-x);
        ip.setY(-y);
        ip.setZ(-z);
    }
    
    public Affine affine = new Affine();
    
    public default void reset() {
        t.setX(0.0);
        t.setY(0.0);
        t.setZ(0.0);
        rotateX.setAngle(0.0);
        rotateY.setAngle(0.0);
        rotateZ.setAngle(0.0);
        s.setX(1.0);
        s.setY(1.0);
        s.setZ(1.0);
        p.setX(0.0);
        p.setY(0.0);
        p.setZ(0.0);
        ip.setX(0.0);
        ip.setY(0.0);
        ip.setZ(0.0);
        affine.setMxx(1);
        affine.setMxy(0);
        affine.setMxz(0);
        affine.setMyx(0);
        affine.setMyy(1);
        affine.setMyz(0);
        affine.setMzx(0);
        affine.setMzy(0);
        affine.setMzz(1);
    }

    public default void resetTSP() {
        t.setX(0.0);
        t.setY(0.0);
        t.setZ(0.0);
        s.setX(1.0);
        s.setY(1.0);
        s.setZ(1.0);
        p.setX(0.0);
        p.setY(0.0);
        p.setZ(0.0);
        ip.setX(0.0);
        ip.setY(0.0);
        ip.setZ(0.0);
    }

    public default void debug() {
        System.out.println("t = (" +
                           t.getX() + ", " +
                           t.getY() + ", " +
                           t.getZ() + ")  " +
                           "r = (" +
                           rotateX.getAngle() + ", " +
                           rotateY.getAngle() + ", " +
                           rotateZ.getAngle() + ")  " +
                           "s = (" +
                           s.getX() + ", " + 
                           s.getY() + ", " + 
                           s.getZ() + ")  " +
                           "p = (" +
                           p.getX() + ", " + 
                           p.getY() + ", " + 
                           p.getZ() + ")  " +
                           "ip = (" +
                           ip.getX() + ", " + 
                           ip.getY() + ", " + 
                           ip.getZ() + ")");
    }
    
    
    /**
     * Toggle Transforms on / off
     * @param b 
     */
    default void enableTransforms(boolean b) {
        // if true, check if node is a camera
        if (b) {
            if (getRotateOrder() != null) {
                    switch (getRotateOrder()) {
                        case XYZ:
                            getTransformableNode().getTransforms().addAll(t, p, rotateZ, rotateY, rotateX, s, ip);
                            break;
                        case XZY:
                            getTransformableNode().getTransforms().addAll(t, p, rotateY, rotateZ, rotateX, s, ip);
                            break;
                        case YXZ:
                            getTransformableNode().getTransforms().addAll(t, p, rotateZ, rotateX, rotateY, s, ip);
                            break;
                        case YZX:
                            getTransformableNode().getTransforms().addAll(t, p, rotateX, rotateZ, rotateY, s, ip);
                            break;
                        case ZXY:
                            getTransformableNode().getTransforms().addAll(t, p, rotateY, rotateX, rotateZ, s, ip);
                            break;
                        case ZYX:
                            getTransformableNode().getTransforms().addAll(t, p, rotateX, rotateY, rotateZ, s, ip);
                            break;
                        case USE_AFFINE:
                            getTransformableNode().getTransforms().addAll(affine);
                            break;
                    }
                
            }
        // if false clear transforms from Node.    
        } else if(!b){
            getTransformableNode().getTransforms().clear();
            reset();
        }
    }
    
    public default void initialize(){
        if(getTransformableNode() != null){
            enableTransforms(true);
        }
    }

    public T getTransformableNode();
    public RotateOrder getRotateOrder();
}
