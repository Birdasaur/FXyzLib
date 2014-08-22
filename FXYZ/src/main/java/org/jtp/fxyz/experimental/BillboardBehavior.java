package org.jtp.fxyz.experimental;

import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

/**
 * Basic interface for Billboard Nodes. 
 * (started with a Region but figured all Nodes should be able to look at the camera)
 * ie: Keeps the Node oriented towards camera.
 * 
 * @author jdub1581
 * @param <T> Type of node to be used.
 */
public interface BillboardBehavior<T extends Node> {
    /**
     * 
     * @return The node to be used for this behavior.
     */
    public T getBillboardNode();    
    /**
     * 
     * @return The camera for node to look at.
     */
    public PerspectiveCamera getCamera();
    /**
     * The transform used to update nodes orientation.
     */
    public Affine affine = new Affine();
    public Rotate rotateX = new Rotate(0,0,0,0,Rotate.X_AXIS), 
            rotateY = new Rotate(0,0,0,0,Rotate.Y_AXIS), 
            rotateZ = new Rotate(0,0,0,0,Rotate.Z_AXIS);
    public Translate translate = new Translate(0,0,1);
    
    BillboardUpdateTimer timer = new BillboardUpdateTimer();
     
    /**
     *  Adds the Affine transform to Node and starts timer.
     */
    default void startBillboardBehavior(){
        if(timer.getUpdateList().isEmpty()){
            timer.addUpdate(() -> {
                updateMatrix();
                return null;
            });
        }
        getBillboardNode().getTransforms().addAll(affine, rotateX, rotateY, rotateZ, translate);
        timer.start();
    }
    /**
     *  Stops timer and removes transform 
     */
    default void stopBillboardBehavior(){
        timer.stop();
        getBillboardNode().getTransforms().clear();
    }
    /**
     * Updates the transformation matrix.
     * can change the Translate for fixed distance     * 
     */
    default void updateMatrix(){
        Transform cam = getCamera().getLocalToSceneTransform(),
                 self = getBillboardNode().getLocalToParentTransform();
         
        Point3D center = new Point3D(cam.getTx(), cam.getTy(), cam.getTz());
        Point3D eye = new Point3D(self.getTx(), self.getTy(), self.getTz());
        Point3D up = cam.deltaTransform(0, 1, 0);
        
        double forwardx, forwardy, forwardz, invMag;
        double upx, upy, upz;
        double sidex, sidey, sidez;

        forwardx = eye.getX() - center.getX();
        forwardy = eye.getY() - center.getY();
        forwardz = eye.getZ() - center.getZ();

        invMag = 1.0 / Math.sqrt(forwardx * forwardx + forwardy * forwardy + forwardz * forwardz);
        forwardx = forwardx * invMag;
        forwardy = forwardy * invMag;
        forwardz = forwardz * invMag;

        invMag = 1.0 / Math.sqrt(up.getX() * up.getX() + up.getY() * up.getY() + up.getZ() * up.getZ());
        upx = up.getX() * invMag;
        upy = up.getY() * invMag;
        upz = up.getZ() * invMag;

        // side = Up cross forward
        sidex = upy * forwardz - forwardy * upz;
        sidey = upz * forwardx - upx * forwardz;
        sidez = upx * forwardy - upy * forwardx;

        invMag = 1.0 / Math.sqrt(sidex * sidex + sidey * sidey + sidez * sidez);
        sidex *= invMag;
        sidey *= invMag;
        sidez *= invMag;

        // recompute up = forward cross side
        upx = forwardy * sidez - sidey * forwardz;
        upy = forwardz * sidex - forwardx * sidez;
        upz = forwardx * sidey - forwardy * sidex;

        // transpose because we calculated the inverse of what we want
        double mxx = sidex;
        double mxy = sidey;
        double mxz = sidez;

        double myx = upx;
        double myy = upy;
        double myz = upz;

        double mzx = forwardx;
        double mzy = forwardy;
        double mzz = forwardz;

        double mxt = -eye.getX() * mxx + -eye.getY() * mxy + -eye.getZ() * mxz;
        double myt = -eye.getX() * myx + -eye.getY() * myy + -eye.getZ() * myz;
        double mzt = -eye.getX() * mzx + -eye.getY() * mzy + -eye.getZ() * mzz;

        
        affine.setMxx(mxx); affine.setMxy(myx); affine.setMzx(mzx); //affine.setTx(mxt);
        affine.setMyx(mxy); affine.setMyy(myy); affine.setMzy(mzy); //affine.setTy(myt);
        affine.setMzx(mxz); affine.setMzy(myz); affine.setMzz(mzz); //affine.setTz(mzt);
        
    }
}
