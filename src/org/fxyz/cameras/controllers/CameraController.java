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
import org.fxyz.cameras.AdvancedCamera;

/**
 *
 * @author Dub
 */
public abstract class CameraController {

    public AdvancedCamera camera;    
    private Scene scene;
    private SubScene subScene;
    private double previousX, previousY, speedModifier = 1.0;
    private final AnimationTimer timer;
    
    
    public CameraController() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
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
    public abstract void handleMouseMoved(MouseEvent event, double modifier);
    public abstract void handleScrollEvent(ScrollEvent event);    
    public abstract double getSpeedModifier(KeyEvent event);
    
    
    //Self contained Methods
    private void handleKeyEvent(KeyEvent t) {
        if (t.getEventType() == KeyEvent.KEY_PRESSED) {
            handleKeyEvent(t, true);
        } else if (t.getEventType() == KeyEvent.KEY_RELEASED) {
            handleKeyEvent(t, true);
        }
        speedModifier = getSpeedModifier(t);
    }
    
    private void handleMouseEvent(MouseEvent t) {
        
        if (t.getEventType() == MouseEvent.MOUSE_PRESSED) {
            handleMousePress(t);
        } else if (t.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            Point2D d = getDragDelta(t);
            
            switch (t.getButton()) {
                case PRIMARY:
                    handlePrimaryMouseDrag(t, d, speedModifier);
                    break;
                case MIDDLE:
                    handleMiddleMouseDrag(t, d, speedModifier);
                    break;
                case SECONDARY:
                    handleSecondaryMouseDrag(t, d, speedModifier);
                    break;
                default:
                    throw new AssertionError();
            }
        }else if(t.getEventType() == MouseEvent.MOUSE_MOVED){
            handleMouseMoved(t, speedModifier);
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
    
    private Point2D getDragDelta(MouseEvent event) {
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
    
    
}
