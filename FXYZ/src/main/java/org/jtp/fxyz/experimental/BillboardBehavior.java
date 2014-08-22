package org.jtp.fxyz.experimental;

import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

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
        getBillboardNode().getTransforms().add(affine);
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
     * can change the Translate for fixed distance
     */
    default void updateMatrix(){
        Transform cam = getCamera().getLocalToSceneTransform(),
                 self = getBillboardNode().getLocalToParentTransform();
                
        affine.setMxx(cam.getMxy()); affine.setMxy(cam.getMxx()); affine.setMzx(cam.getMzx()); affine.setTx(self.getTx());
        affine.setMyx(cam.getMyy()); affine.setMyy(cam.getMyx()); affine.setMzy(cam.getMzy()); affine.setTy(self.getTy());
        affine.setMzx(cam.getMzy()); affine.setMzy(cam.getMzx()); affine.setMzz(-cam.getMzz());affine.setTz(self.getTz());
        
    }
}
