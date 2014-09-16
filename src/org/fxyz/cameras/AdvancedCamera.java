/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.cameras;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import org.fxyz.geometry.Vector3D;

/**
 *
 * @author Dub
 */
public class AdvancedCamera extends PerspectiveCamera {

    // Wrapper for "World" movement and lighting
    private final Group wrapper = new Group();
    private final PointLight headLight = new PointLight();
    private final AmbientLight ambientLight = new AmbientLight();

    //transform for more percise directional movement
    private final Affine affine = new Affine();
    
    private CameraController controller;

    public AdvancedCamera() {
        super(true);
        setNearClip(0.1);
        setFarClip(10000);
        setFieldOfView(42);
        //setVerticalFieldOfView(true);
        
        ambientLight.setLightOn(false);
        wrapper.getChildren().addAll(AdvancedCamera.this, headLight, ambientLight);
        wrapper.getTransforms().add(affine);
        
        updateMatrix();
    }

    private void updateMatrix() {
        affine.setToIdentity();
        
        Vector3D forward = getForwardDirection().sub(getPosition()).toNormal(),
                up = getUpDirection(),
                right = up.crossProduct(forward).toNormal();
        up = forward.crossProduct(right).toNormal();
        
        //translates
        double mxt = -getPosX() * right.x + -getPosY() * up.x + -getPosZ() * forward.x;
        double myt = -getPosX() * right.y + -getPosY() * up.y + -getPosZ() * forward.y;
        double mzt = -getPosX() * right.z + -getPosY() * up.z + -getPosZ() * forward.z;
        
        //affine.setTx(getPosX());
        //affine.setTy(getPosY());
        //affine.setTz(getPosZ());
        
        affine.setMxx(right.x);   affine.setMxy(up.x);  affine.setMxz(forward.x);
        affine.setMyx(right.y);   affine.setMyy(up.y);  affine.setMyz(forward.y);
        affine.setMzx(right.z);   affine.setMzy(up.z);  affine.setMzz(forward.z);

        //affine.setTx(getPosX() * (1 - affine.getMxx()) - getPosY() * affine.getMxy() - getPosZ() * affine.getMxz());
        //affine.setTy(getPosY() * (1 - affine.getMyy()) - getPosX() * affine.getMyx() - getPosZ() * affine.getMyz());
        //affine.setTz(getPosZ() * (1 - affine.getMzz()) - getPosX() * affine.getMzx() - getPosY() * affine.getMzy());
        
        affine.setTx(mxt);
        affine.setTy(myt);
        affine.setTz(mzt);
        
    }
    
    public CameraController getController() {
        return controller;
    }

    public void setController(CameraController controller) {
        controller.setCamera(this);
        this.controller = controller;        
    }   

    public Group getWrapper() {
        if (wrapper.getChildren().isEmpty()) {
            wrapper.getChildren().add(this);
        }
        return wrapper;
    }

    public PointLight getHeadLight() {
        return headLight;
    }

    public AmbientLight getAmbientLight() {
        return ambientLight;
    }

    
    public Affine getAffineTransform() {
        return affine;
    }

    public void setPosition(double x, double y, double z) {
        setPosX(x);
        setPosY(y);
        setPosZ(z);
    }
    public void setPosition(Vector3D v) {
        setPosX(v.x);
        setPosY(v.y);
        setPosZ(v.z);
    }

    public void setForward(double x, double y, double z) {
        setForwardX(x);
        setForwardY(y);
        setForwardZ(z); 
    }
    
    public void setForward(Vector3D v) {
        setForwardX(v.x);
        setForwardY(v.y);
        setForwardZ(v.z); 
    }

    public void setUp(double x, double y, double z) {
        setUpX(x);
        setUpY(y);
        setUpZ(z);
    }

    /*
     * returns 3D direction from the Camera position to the mouse
     * in the Scene space 
     */
    public Vector3D mouseDirection(double sceneX, double sceneY, double sceneW, double sceneH) {
        
        double tanHFov = Math.tan(Math.toRadians(getFieldOfView()) * 0.5f);
        Vector3D vMouse = new Vector3D(
                2 * sceneX / sceneW - 1,
                2 * sceneY / sceneW - sceneH / sceneW,
                1
        );
        vMouse.x *= tanHFov;
        vMouse.y *= tanHFov;        

        Vector3D result = localToSceneDirection(vMouse, new Vector3D());

        return result.toNormal();
    }

    public Vector3D getPosition() {
        return new Vector3D(getPosX(), getPosY(), getPosZ());
    }

    public Vector3D getForward() {
        return new Vector3D(getForwardX(), getForwardY(), getForwardZ());
    }

    public Vector3D localToScene(Vector3D pt, Vector3D result) {
        Point3D res = getLocalToSceneTransform().transform(pt.x, pt.y, pt.z);
        //if (getParent() != null) {
            //res = getParent().getLocalToSceneTransform().transform(res);
        //}
        result.setValues(res.getX(), res.getY(), res.getZ());
        return result;
    }

    public Vector3D localToSceneDirection(Vector3D dir, Vector3D result) {
        localToScene(dir, result);
        result.sub(localToScene(new Vector3D(0, 0, 0), new Vector3D()));
        return result;
    }

    public Vector3D getForwardDirection() {
        Vector3D res = localToSceneDirection(Vector3D.FORWARD, new Vector3D());

        return res.toNormal();
    }

    public Vector3D getUpDirection() {
        Vector3D res = localToSceneDirection(Vector3D.UP, new Vector3D());

        return res.toNormal();
    }

    public Vector3D getRightDirection() {
        Vector3D res = localToSceneDirection(Vector3D.RIGHT, new Vector3D());

        return res.toNormal();
    }

    /*
     Properties
     */
    private final DoubleProperty upX = new SimpleDoubleProperty(Vector3D.UP.x) {
        @Override
        protected void invalidated() {
            updateMatrix();
        }
    };

    public final double getUpX() {
        return upX.getValue();
    }

    public final void setUpX(double value) {
        upX.setValue(value);
    }

    public final DoubleProperty upXProperty() {
        return upX;
    }

    private final DoubleProperty upY = new SimpleDoubleProperty(Vector3D.UP.y) {
        @Override
        protected void invalidated() {
            updateMatrix();
        }
    };

    public final double getUpY() {
        return upY.getValue();
    }

    public final void setUpY(double value) {
        upY.setValue(value);
    }

    public final DoubleProperty upYProperty() {
        return upY;
    }

    private final DoubleProperty upZ = new SimpleDoubleProperty(Vector3D.UP.z) {
        @Override
        protected void invalidated() {
            updateMatrix();
        }
    };

    public final double getUpZ() {
        return upZ.getValue();
    }

    public final void setUpZ(double value) {
        upZ.setValue(value);
    }

    public final DoubleProperty upZProperty() {
        return upZ;
    }

    private final DoubleProperty forwardX = new SimpleDoubleProperty(Vector3D.FORWARD.x) {
        @Override
        protected void invalidated() {
            updateMatrix();
        }
    };

    public final double getForwardX() {
        return forwardX.getValue();
    }

    public final void setForwardX(double value) {
        forwardX.setValue(value);
    }

    public final DoubleProperty forwardXProperty() {
        return forwardX;
    }

    private final DoubleProperty forwardY = new SimpleDoubleProperty(Vector3D.FORWARD.y) {
        @Override
        protected void invalidated() {
            updateMatrix();
        }
    };

    public final double getForwardY() {
        return forwardY.getValue();
    }

    public final void setForwardY(double value) {
        forwardY.setValue(value);
    }

    public final DoubleProperty forwardYProperty() {
        return forwardY;
    }

    private final DoubleProperty forwardZ = new SimpleDoubleProperty(Vector3D.FORWARD.z) {
        @Override
        protected void invalidated() {
            updateMatrix();
        }
    };

    public final double getForwardZ() {
        return forwardZ.getValue();
    }

    public final void setForwardZ(double value) {
        forwardZ.setValue(value);
    }

    public final DoubleProperty forwardZProperty() {
        return forwardZ;
    }

    private final DoubleProperty posX = new SimpleDoubleProperty(0) {
        @Override
        protected void invalidated() {
            updateMatrix();
        }
    };

    public final double getPosX() {
        return posX.getValue();
    }

    public final void setPosX(double value) {
        posX.setValue(value);
    }

    public final DoubleProperty posXProperty() {
        return posX;
    }

    private final DoubleProperty posY = new SimpleDoubleProperty(0) {
        @Override
        protected void invalidated() {
            updateMatrix();
        }
    };

    public final double getPosY() {
        return posY.getValue();
    }

    public final void setPosY(double value) {
        posY.setValue(value);
    }

    public final DoubleProperty posYProperty() {
        return posY;
    }

    private final DoubleProperty posZ = new SimpleDoubleProperty(0) {
        @Override
        protected void invalidated() {
            updateMatrix();
        }
    };

    public final double getPosZ() {
        return posZ.getValue();
    }

    public final void setPosZ(double value) {
        posZ.setValue(value);
    }

    public final DoubleProperty posZProperty() {
        return posZ;
    }

    @Override
    public String toString() {
        return "camera3D.setPos(" + posX.get() + ", " + posY.get() + ", "
                + posZ.get() + ");\n"
                + "camera3D.setTarget(" + forwardX.get() + ", "
                + forwardY.get() + ", " + forwardZ.get() + ");\n"
                + "camera3D.setUp(" + upX.get() + ", " + upY.get() + ", "
                + upZ.get() + ");";
    }
}
