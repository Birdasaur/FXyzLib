package org.jtp.fxyz.experimental;

import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

/**
 * Basic interface for Billboard Nodes. 
 * (started with a Region but figured all Nodes should be able to look at the camera)
 * ie: Keeps the Node oriented towards camera.
 * 
 * @author jdub1581
 * @param <T> Type of node to be used for (this) "Billboard".
 */
public interface BillboardBehavior<T extends Node>{
    static BillboardUpdateTimer timer = new BillboardUpdateTimer();
    /**
     * 
     * @return The node to be used for this behavior.
     */
    public T getBillboardNode();    
    /**
     * 
     * @return The node to look at.
     */
    public Node getOther();
      
    
    public Affine affine = new Affine();         
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
        getBillboardNode().getTransforms().addAll(affine);
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
        Transform cam = getOther().getLocalToSceneTransform(),
                 self = getBillboardNode().getLocalToSceneTransform();
         
        Point3D camPos = new Point3D(cam.getTx(), cam.getTy(), cam.getTz());
        Point3D selfPos = new Point3D(self.getTx(), self.getTy(), self.getTz());
        Point3D up = Direction3D.up();
        
        double forwardx, forwardy, forwardz, invMag;
        double upx, upy, upz;
        double sidex, sidey, sidez;

        forwardx = selfPos.getX() - camPos.getX();
        forwardy = selfPos.getY() - camPos.getY();
        forwardz = selfPos.getZ() - camPos.getZ();

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
        
        affine.setMxx(mxx); affine.setMxy(myx); affine.setMzx(mzx); 
        affine.setMyx(mxy); affine.setMyy(myy); affine.setMzy(mzy); 
        affine.setMzx(mxz); affine.setMzy(myz); affine.setMzz(mzz); 
        
        
    }
    
}
