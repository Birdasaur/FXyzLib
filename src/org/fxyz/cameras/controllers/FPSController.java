/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.cameras.controllers;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import org.fxyz.geometry.Vector3D;

/**
 *
 * @author Dub
 */
public class FPSController extends CameraController{
    private boolean fwd, strafeL, strafeR, back, up, down, shift, mouseLookEnabled;
    private double speed = 5.0;
            
    public FPSController() {
        super(true);     
    }    
    
    @Override
    public void update() {
        
        if(fwd && !back){
            moveForward();
        }
        if(strafeL){
            strafeLeft();
        }
        if(strafeR){
            strafeRight();
        }
        if(back && !fwd){
            moveBack();
        }
        if(up && !down){
            moveUp();
        }
        if(down && !up){
            moveDown();
        }
        
    }

    @Override
    public void handleKeyEvent(KeyEvent event, boolean handle) {
        if(event.getEventType() == KeyEvent.KEY_PRESSED){
            switch(event.getCode()){
                case W: fwd = true;
                    break;
                case S: back = true;
                    break;
                case A: strafeL = true;
                    break;
                case D: strafeR = true;
                    break;
                case SHIFT: speed = 15.0;
                            shift = true;
                    break;
                case SPACE: if(!shift){
                                up = true;
                            }else if(shift){
                                down = true;
                            }
                    break;
            }
        }else if(event.getEventType() == KeyEvent.KEY_RELEASED){
            switch(event.getCode()){
                case W: fwd = false;
                    break;
                case S: back = false;
                    break;
                case A: strafeL = false;
                    break;
                case D: strafeR = false;
                    break;
                case SHIFT: speed = 5.0;
                            shift = false;
                    break;
                case SPACE: up = false;
                            down = false;                            
                    break;
            }
        }
    }

    @Override
    public void handlePrimaryMouseDrag(MouseEvent event, Point2D dragDelta, double modifier) {
        /*
            Handling rotations here for now.. Subject to change, as mouse buttons should be used to "fire"/"aim"/ toggle a menu
            Will need to update Controller class to handle ButtonPress/Click Events
        */
        rotateY.setAngle(((rotateY.getAngle() + dragDelta.getX() * (speed * 0.5)) % 360 + 540) % 360 - 180); // +                
        rotateX.setAngle(((rotateX.getAngle() - dragDelta.getY() * (speed * 0.5)) % 360 + 540) % 360 - 180); // -         
    }

    @Override
    public void handleMiddleMouseDrag(MouseEvent event, Point2D dragDelta, double modifier) {
        // do nothing for now        
    }

    @Override
    public void handleSecondaryMouseDrag(MouseEvent event, Point2D dragDelta, double modifier) {
        // do nothing for now
    }

    @Override
    public void handleMouseMoved(MouseEvent event, double modifier) {
        // do nothing for now
        // enable MouseLooking with pitch(xAxis rotation), yaw(yAxis rotation) constraints
    }

    @Override
    public void handleScrollEvent(ScrollEvent event) {
        //do nothing for now, use for Zoom?
    }

    @Override
    public double getSpeedModifier(KeyEvent event) {
        return speed;
    }

    @Override
    public Node getTransformableNode() {
        if(getCamera() != null){            
            return getCamera();
        }else{
            throw new UnsupportedOperationException("Must have a Camera");
        }
    }

    
    
    

    private void moveForward() {     
        Vector3D f = getForwardMatrixRow.call(affine);
        affine.setTx(affine.getTx() + speed * f.x);
        affine.setTy(affine.getTy() + speed * f.y);
        affine.setTz(affine.getTz() + speed * f.z);   
    }

    private void strafeLeft() {
        Vector3D r = getRightMatrixRow.call(affine);
        affine.setTx(affine.getTx() + speed * -r.x);
        affine.setTy(affine.getTy() + speed * -r.y);
        affine.setTz(affine.getTz() + speed * -r.z);
    }

    private void strafeRight() {
        Vector3D r = getRightMatrixRow.call(affine);
        affine.setTx(affine.getTx() + speed * r.x);
        affine.setTy(affine.getTy() + speed * r.y);
        affine.setTz(affine.getTz() + speed * r.z);
    }

    private void moveBack() {
        Vector3D f = getForwardMatrixRow.call(affine);
        affine.setTx(affine.getTx() + speed * -f.x);
        affine.setTy(affine.getTy() + speed * -f.y);
        affine.setTz(affine.getTz() + speed * -f.z);
    }

    private void moveUp() {
        Vector3D u = getUpMatrixRow.call(affine);
        affine.setTx(affine.getTx() + speed * -u.x);
        affine.setTy(affine.getTy() + speed * -u.y);
        affine.setTz(affine.getTz() + speed * -u.z);
    }

    private void moveDown() {
        Vector3D u = getUpMatrixRow.call(affine);
        affine.setTx(affine.getTx() + speed * u.x);
        affine.setTy(affine.getTy() + speed * u.y);
        affine.setTz(affine.getTz() + speed * u.z);
    }

    public void setMouseLookEnabled(boolean b){
        throw new UnsupportedOperationException("this feature is not implemented yet");
    }

    @Override
    public void handlePrimaryMouseClick(MouseEvent t) {
        
    }

    @Override
    public void handleMiddleMouseClick(MouseEvent t) {
       
    }

    @Override
    public void handleSecondaryMouseClick(MouseEvent t) {
        
    }
    
}
