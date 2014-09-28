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
import javafx.scene.transform.Translate;
import org.fxyz.geometry.Vector3D;
import org.fxyz.utils.MathUtils;

/**
 *
 * @author Dub
 */
public class FPSController extends CameraController {

    private boolean fwd, strafeL, strafeR, back, up, down, shift, mouseLookEnabled;
    private double speed = 5.0;

    public FPSController() {
        super(true);
    }

    @Override
    public void update() {

        if (fwd && !back) {
            moveForward();
        }
        if (strafeL) {
            strafeLeft();
        }
        if (strafeR) {
            strafeRight();
        }
        if (back && !fwd) {
            moveBack();
        }
        if (up && !down) {
            moveUp();
        }
        if (down && !up) {
            moveDown();
        }

    }

    @Override
    public void handleKeyEvent(KeyEvent event, boolean handle) {
        if (event.getEventType() == KeyEvent.KEY_PRESSED) {
            switch (event.getCode()) {
                case W:
                    fwd = true;
                    break;
                case S:
                    back = true;
                    break;
                case A:
                    strafeL = true;
                    break;
                case D:
                    strafeR = true;
                    break;
                case SHIFT:
                    speed = 15.0;
                    shift = true;
                    break;
                case SPACE:
                    if (!shift) {
                        up = true;
                    } else if (shift) {
                        down = true;
                    }
                    break;
            }
        } else if (event.getEventType() == KeyEvent.KEY_RELEASED) {
            switch (event.getCode()) {
                case W:
                    fwd = false;
                    break;
                case S:
                    back = false;
                    break;
                case A:
                    strafeL = false;
                    break;
                case D:
                    strafeR = false;
                    break;
                case SHIFT:
                    speed = 5.0;
                    shift = false;
                    break;
                case SPACE:
                    up = false;
                    down = false;
                    break;
            }
        }
    }

    @Override
    public void handlePrimaryMouseDrag(MouseEvent event, Point2D dragDelta, double modifier) {
        //do nothing  
        if (!mouseLookEnabled){
            Translate tr = new Translate(affine.getTx(), affine.getTy(), affine.getTz());
            
            affine.setToIdentity();
            
            rotateY.setAngle(
                    MathUtils.clamp(((rotateY.getAngle() + dragDelta.getX() * (speed * 0.5)) % 360 + 540) % 360 - 180, -360, 360)
            ); // horizontal                
            rotateX.setAngle(
                    MathUtils.clamp(((rotateX.getAngle() - dragDelta.getY() * (speed * 0.5)) % 360 + 540) % 360 - 180, -90, 90)
            ); // vertical
            
            affine.prepend(tr.createConcatenation(rotateY.createConcatenation(rotateX)));
        }
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
    public void handleMouseMoved(MouseEvent event, Point2D moveDelta, double speed) {
        if (mouseLookEnabled) {
            Translate tr = new Translate(affine.getTx(), affine.getTy(), affine.getTz());
            
            affine.setToIdentity();
            
            rotateY.setAngle(
                    MathUtils.clamp(((rotateY.getAngle() + moveDelta.getX() * (speed * 0.05)) % 360 + 540) % 360 - 180, -360, 360)
            ); // horizontal                
            rotateX.setAngle(
                    MathUtils.clamp(((rotateX.getAngle() - moveDelta.getY() * (speed * 0.05)) % 360 + 540) % 360 - 180, -90, 90)
            ); // vertical
            
            affine.prepend(tr.createConcatenation(rotateY.createConcatenation(rotateX)));
        }
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
        if (getCamera() != null) {
            return getCamera();
        } else {
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

    public void setMouseLookEnabled(boolean b) {
        mouseLookEnabled = b;
    }

    @Override
    public void handlePrimaryMouseClick(MouseEvent t) {
        System.out.println("Primary Button Clicked!");
    }

    @Override
    public void handleMiddleMouseClick(MouseEvent t) {
        System.out.println("Middle Button Clicked!");
    }

    @Override
    public void handleSecondaryMouseClick(MouseEvent t) {
        System.out.println("Secondary Button Clicked!");
    }

}
