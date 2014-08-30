/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.org.jtp.fxyz.tests;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.java.org.jtp.fxyz.shape3d.TorusMesh;
import org.fxyz.composites.CameraTransformer;

/**
 *
 * @author Dub
 */
public class NewTorusTest extends Application {
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;
    private double cameraDistance = 5000;
    
    private PerspectiveCamera camera;
    private CameraTransformer cameraTransform = new CameraTransformer();
    
    @Override
    public void start(Stage stage) throws Exception {
        loadScene(stage);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    
    void loadScene(Stage stage){
        int rDivs = 32, tDivs = 3;
        double rad = 600, trad = 300;
        TorusMesh torus = new TorusMesh(rDivs, tDivs, rad, trad);
        torus.setMaterial(new PhongMaterial(Color.BLUEVIOLET));       
        
        StackPane root = new StackPane();
        root.getChildren().add(torus);
        
        Scene scene = new Scene(root, 1024, 668, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.DEEPSKYBLUE);
        
        camera = new PerspectiveCamera(true);
        cameraTransform.setTranslate(0, 0, 0);
        cameraTransform.getChildren().add(camera);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(-5000);
        cameraTransform.ry.setAngle(-45.0);
        cameraTransform.rx.setAngle(-10.0);
        //add a Point Light for better viewing of the grid coordinate system
        PointLight light = new PointLight(Color.WHITE);
        
        cameraTransform.getChildren().add(light);
        light.setTranslateX(camera.getTranslateX());
        light.setTranslateY(camera.getTranslateY());
        light.setTranslateZ(camera.getTranslateZ());
        scene.setCamera(camera);
        
        root.getChildren().add(cameraTransform);
        
        //First person shooter keyboard movement
        scene.setOnKeyPressed(event -> {
            double change = 10.0;
            //Add shift modifier to simulate "Running Speed"
            if(event.isShiftDown()) { change = 50.0; }
            //What key did the user press?
            KeyCode keycode = event.getCode();
            //Step 2c: Add Zoom controls
            if(keycode == KeyCode.W) { camera.setTranslateZ(camera.getTranslateZ() + change); }
            if(keycode == KeyCode.S) { camera.setTranslateZ(camera.getTranslateZ() - change); }
            //Step 2d: Add Strafe controls
            if(keycode == KeyCode.A) { camera.setTranslateX(camera.getTranslateX() - change); }
            if(keycode == KeyCode.D) { camera.setTranslateX(camera.getTranslateX() + change); }
            if(keycode == KeyCode.UP) {torus.setTubeStartAngleOffset(torus.getTubeStartAngleOffset() + 30);}
        });
        
        scene.setOnMousePressed((MouseEvent me) -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });
        scene.setOnMouseDragged((MouseEvent me) -> {
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseDeltaX = (mousePosX - mouseOldX);
            mouseDeltaY = (mousePosY - mouseOldY);
            
            double modifier = 10.0;
            double modifierFactor = 0.1;
            
            if (me.isControlDown()) {
                modifier = 0.1;
            }
            if (me.isShiftDown()) {
                modifier = 50.0;
            }
            if (me.isPrimaryButtonDown()) {
                cameraTransform.ry.setAngle(((cameraTransform.ry.getAngle() + mouseDeltaX * modifierFactor * modifier * 2.0) % 360 + 540) % 360 - 180); // +
                cameraTransform.rx.setAngle(((cameraTransform.rx.getAngle() - mouseDeltaY * modifierFactor * modifier * 2.0) % 360 + 540) % 360 - 180); // -                
            } else if (me.isSecondaryButtonDown()) {
                double z = camera.getTranslateZ();
                double newZ = z + mouseDeltaX * modifierFactor * modifier;
                camera.setTranslateZ(newZ);
            } else if (me.isMiddleButtonDown()) {
                cameraTransform.t.setX(cameraTransform.t.getX() + mouseDeltaX * modifierFactor * modifier * 0.3); // -
                cameraTransform.t.setY(cameraTransform.t.getY() + mouseDeltaY * modifierFactor * modifier * 0.3); // -
            }
        });
        
        stage.setScene(scene);
        stage.show();
        
        final Timeline t = new Timeline();
        t.getKeyFrames().addAll(new KeyFrame[]{
            new KeyFrame(Duration.seconds(5), new KeyValue[]{// Frame End                
                new KeyValue(torus.tubeStartAngleOffsetProperty(), torus.getTubeStartAngleOffset() - 10, Interpolator.EASE_BOTH),
                new KeyValue(torus.xOffsetProperty(), torus.getxOffset() + 0.5, Interpolator.EASE_BOTH),
                new KeyValue(torus.yOffsetProperty(), torus.getyOffset() + 0.5, Interpolator.EASE_BOTH),
                new KeyValue(torus.zOffsetProperty(), torus.getzOffset() + 2, Interpolator.EASE_BOTH),
            })
        });
        t.setCycleCount(Animation.INDEFINITE);
        t.setAutoReverse(true);
        t.playFromStart();
        
    }
    
    
}
