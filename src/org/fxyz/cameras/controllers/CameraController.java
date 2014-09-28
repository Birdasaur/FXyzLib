/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.cameras.controllers;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.input.KeyEvent;
import static javafx.scene.input.MouseButton.MIDDLE;
import static javafx.scene.input.MouseButton.PRIMARY;
import static javafx.scene.input.MouseButton.SECONDARY;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Affine;
import javafx.util.Callback;
import org.fxyz.cameras.AdvancedCamera;
import org.fxyz.extras.Transformable;
import org.fxyz.geometry.Vector3D;

/**
 *
 * @author Dub
 */
public abstract class CameraController implements Transformable {

    public AdvancedCamera camera;
    private Scene scene;
    private SubScene subScene;
    private double previousX, previousY, speed = 1.0;
    private final AnimationTimer timer;
    private boolean enable;
    
    private Vector3D fd,fr,ud,ur,rd,rr;
    public Callback<Affine, Vector3D> forwardDir = (a) -> {
        fd = new Vector3D(a.getMzx(), a.getMzy(), a.getMzz());
        return fd;
    };
    public Callback<Affine, Vector3D> getForwardMatrixRow = (a) -> {
        fr = new Vector3D(a.getMxz(), a.getMyz(), a.getMzz());
        return fr;
    };
    
    public Callback<Affine, Vector3D> upDir = (a) -> {
        ud = new Vector3D(a.getMyx(), a.getMyy(), a.getMyz());
        return ud;
    };
    public Callback<Affine, Vector3D> getUpMatrixRow = (a) -> {
        ur = new Vector3D(a.getMxy(), a.getMyy(), a.getMzy());
        return ur;
    };
    
    public Callback<Affine, Vector3D> rightDir = (a) -> {
        fd = new Vector3D(a.getMxx(), a.getMxy(), a.getMxz());
        return fd;
    };
    public Callback<Affine, Vector3D> getRightMatrixRow = (a) -> {
        fd = new Vector3D(a.getMxx(), a.getMyx(), a.getMzx());
        return fd;
    };

    public CameraController(boolean enableTransforms) {
        enable = enableTransforms;
        timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                if (enable) {
                    initialize();
                    enable = false;
                }
                update();
            }
        };
    }

    //Abstract Methods
    public abstract void update(); // called each frame handle movement/ button clicks here

    // Following methods should update values for use in update method etc...
    public abstract void handleKeyEvent(KeyEvent event, boolean handle);

    public abstract void handlePrimaryMouseDrag(MouseEvent event, Point2D dragDelta, double modifier);

    public abstract void handleMiddleMouseDrag(MouseEvent event, Point2D dragDelta, double modifier);

    public abstract void handleSecondaryMouseDrag(MouseEvent event, Point2D dragDelta, double modifier);
    
    public abstract void handlePrimaryMouseClick(MouseEvent e);
    
    public abstract void handleSecondaryMouseClick(MouseEvent e);
    
    public abstract void handleMiddleMouseClick(MouseEvent e);

    public abstract void handleMouseMoved(MouseEvent event, Point2D moveDelta, double modifier);

    public abstract void handleScrollEvent(ScrollEvent event);

    public abstract double getSpeedModifier(KeyEvent event);

    //Self contained Methods
    private void handleKeyEvent(KeyEvent t) {
        if (t.getEventType() == KeyEvent.KEY_PRESSED) {
            handleKeyEvent(t, true);
        } else if (t.getEventType() == KeyEvent.KEY_RELEASED) {
            handleKeyEvent(t, true);
        }
        speed = getSpeedModifier(t);
    }

    private void handleMouseEvent(MouseEvent t) {

        if (t.getEventType() == MouseEvent.MOUSE_PRESSED) {
            handleMousePress(t);
        } else if (t.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            Point2D d = getMouseDelta(t);

            switch (t.getButton()) {
                case PRIMARY:
                    handlePrimaryMouseDrag(t, d, speed);
                    break;
                case MIDDLE:
                    handleMiddleMouseDrag(t, d, speed);
                    break;
                case SECONDARY:
                    handleSecondaryMouseDrag(t, d, speed);
                    break;
                default:
                    throw new AssertionError();
            }
        } else if (t.getEventType() == MouseEvent.MOUSE_MOVED) {
            handleMouseMoved(t, getMouseDelta(t), speed);
        } else if(t.getEventType() == MouseEvent.MOUSE_CLICKED){
            switch (t.getButton()) {
                case PRIMARY:
                    handlePrimaryMouseClick(t);
                    break;
                case MIDDLE:
                    handleMiddleMouseClick(t);
                    break;
                case SECONDARY:
                    handleSecondaryMouseClick(t);
                    break;
                default:
                    throw new AssertionError();
            }
        }
    }

    private void setEventHandlers(Scene scene) {
        scene.addEventHandler(KeyEvent.ANY, k -> handleKeyEvent(k));
        scene.addEventHandler(MouseEvent.ANY, m -> handleMouseEvent(m));
        scene.addEventHandler(ScrollEvent.ANY, s -> handleScrollEvent(s));
    }

    private void setEventHandlers(SubScene scene) {
        scene.addEventHandler(KeyEvent.ANY, k -> handleKeyEvent(k));
        scene.addEventHandler(MouseEvent.ANY, m -> handleMouseEvent(m));
        scene.addEventHandler(ScrollEvent.ANY, s -> handleScrollEvent(s));
    }

    private void handleMousePress(MouseEvent event) {
        previousX = event.getSceneX();
        previousY = event.getSceneY();
        event.consume();
    }

    private Point2D getMouseDelta(MouseEvent event) {
        Point2D res = new Point2D(event.getSceneX() - previousX, event.getSceneY() - previousY);
        previousX = event.getSceneX();
        previousY = event.getSceneY();

        return res;
    }

    public AdvancedCamera getCamera() {
        return camera;
    }

    public void setCamera(AdvancedCamera camera) {
        this.camera = camera;
        timer.start();
    }

    public void setScene(Scene scene) {
        this.scene = scene;
        setEventHandlers(scene);
    }

    public void setSubScene(SubScene subScene) {
        this.subScene = subScene;
        setEventHandlers(subScene);
    }

    public Scene getScene() {
        return scene;
    }

    public SubScene getSubScene() {
        return subScene;
    }

    @Override
    public RotateOrder getRotateOrder() {
        return RotateOrder.USE_AFFINE;
    }

}
