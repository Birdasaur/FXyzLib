/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fxyz.cameras;

import javafx.geometry.Point2D;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import org.fxyz.geometry.Vector3D;

/**
 *
 * @author Dub
 */
public class FPSCameraController extends CameraController{

    private boolean fwd,back,left,right,up,down;
    private Vector3D newPos;
    private double speed = 1;
    
    @Override
    public void update() {
        if(fwd && !back){
            newPos = camera.getPosition().sub(camera.getForwardDirection());
            camera.setPosition(newPos.x, newPos.y, newPos.z);       
            camera.setForward(camera.getForward().add(newPos));
        }
        if(back && !fwd){
            newPos = camera.getPosition().add(camera.getForwardDirection());
            camera.setPosition(newPos.x, newPos.y, newPos.z);
            camera.setForward(camera.getForward().add(newPos));
        }
        if(left && !right){
            newPos = camera.getPosition().sub(camera.getUpDirection().crossProduct(camera.getForwardDirection()).toNormal());
            camera.setPosition(newPos.x, newPos.y, newPos.z);
            camera.setForward(camera.getForward().add(newPos));
        }
        if(right && !left){
            newPos = camera.getPosition().add(camera.getUpDirection().crossProduct(camera.getForwardDirection()).toNormal());
            camera.setPosition(newPos.x, newPos.y, newPos.z);
            camera.setForward(camera.getForward().add(newPos));
        }
        if(up){
            newPos = getCamera().getPosition().sub(getCamera().getForwardDirection().crossProduct(getCamera().getRightDirection()).toNormal());
            getCamera().setPosition(getCamera().getPosX(), newPos.y , getCamera().getPosZ());
            camera.setForward(camera.getForward().add(newPos));
        }
        if(down){
            newPos = getCamera().getPosition().add(getCamera().getForwardDirection().crossProduct(getCamera().getRightDirection()).toNormal());
            getCamera().setPosition(getCamera().getPosX(), newPos.y , getCamera().getPosZ());
            camera.setForward(camera.getForward().add(newPos));
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
                case A: left = true;
                    break;
                case D: right = true;
                    break;
                case SPACE:
                    if(fwd){
                        up = true;
                    }else if(back){                        
                        down = true;
                    }
            }
        }else if(event.getEventType() == KeyEvent.KEY_RELEASED){
            switch(event.getCode()){
                case W: fwd = false;
                    break;
                case S: back = false;
                    break;
                case A: left = false;
                    break;
                case D: right = false;
                    break;
                case SPACE: 
                    up = false;
                    down = false;
            }
        }
        //System.out.println(getCamera().getPosition());
        //speed = getSpeedModifier(event);
        event.consume();
    }

    @Override
    public void handlePrimaryMouseDrag(MouseEvent event, Point2D dragDelta, double modifier) {
        
    }

    @Override
    public void handleMiddleMouseDrag(MouseEvent event, Point2D dragDelta, double modifier) {
        
    }

    @Override
    public void handleSecondaryMouseDrag(MouseEvent event, Point2D dragDelta, double modifier) {
        
    }

    // use for mouse look
    @Override
    public void handleMouseMoved(MouseEvent event, double modifier) {
        
    }

    @Override
    public void handleScrollEvent(ScrollEvent event) {
        
    }

    @Override
    public double getSpeedModifier(KeyEvent event) {
        if(event.isShiftDown()){
            return 20;
        }else{
            return 5;
        }
    }
    
    
}
