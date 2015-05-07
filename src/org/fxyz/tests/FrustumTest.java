/*
 * Copyright (C) 2013-2015 F(X)yz, 
 * Sean Phillips, Jason Pollastrini and Jose Pereda
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fxyz.tests;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import org.fxyz.cameras.CameraTransformer;
import org.fxyz.geometry.Point3D;
import org.fxyz.shapes.primitives.FrustumMesh;

/**
 *
 * @author jpereda
 */
public class FrustumTest extends Application {
    private PerspectiveCamera camera;
    private final double sceneWidth = 600;
    private final double sceneHeight = 600;
    private final CameraTransformer cameraTransform = new CameraTransformer();
    
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;
    private FrustumMesh cylinder;
    private Rotate rotateY;
    
    private Function<Point3D, Number> dens = p->p.x;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Group sceneRoot = new Group();
        Scene scene = new Scene(sceneRoot, sceneWidth, sceneHeight, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.WHITESMOKE);
        camera = new PerspectiveCamera(true);        
     
        //setup camera transform for rotational support
        cameraTransform.setTranslate(0, 0, 0);
        cameraTransform.getChildren().add(camera);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(-30);
//        cameraTransform.ry.setAngle(-45.0);
//        cameraTransform.rx.setAngle(-10.0);
        //add a Point Light for better viewing of the grid coordinate system
        PointLight light = new PointLight(Color.WHITE);
        cameraTransform.getChildren().add(light);
        light.setTranslateX(camera.getTranslateX());
        light.setTranslateY(camera.getTranslateY());
        light.setTranslateZ(10*camera.getTranslateZ());        
        scene.setCamera(camera);
        
        rotateY = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
        Group group = new Group();
        group.getChildren().add(cameraTransform);    
//        cylinder = new PrismMesh(2,5,4);
        cylinder = new FrustumMesh(1,0.2,4,3); //,new Point3D(-5,5,0),new Point3D(0,0,5));
//        cylinder.setDrawMode(DrawMode.LINE);
    // SECTION TYPE
//        cylinder.setSectionType(TriangleMeshHelper.SectionType.TRIANGLE);
    // NONE
//        cylinder.setTextureModeNone(Color.ROYALBLUE);
    // IMAGE
//        cylinder.setTextureModeImage(getClass().getResource("res/netCylinder.png").toExternalForm());
        cylinder.setTextureModeVertices1D(1530, t->t);
//        cylinder.setColorPalette(ColorPalette.GREEN);
    // DENSITY
//        cylinder.setTextureModeVertices3D(1530,p->(double)p.y);
//        cylinder.setTextureModeVertices3D(1530,p->(double)cylinder.unTransform(p).magnitude());
    // FACES
//        cylinder.setTextureModeFaces(1530);

        
        cylinder.getTransforms().addAll(new Rotate(0,Rotate.X_AXIS),rotateY);
        group.getChildren().add(cylinder);
        
        boolean showKnots =true;
        if(showKnots){
            Sphere s=new Sphere(cylinder.getMajorRadius()/10d);
            Point3D k0=cylinder.getAxisOrigin();
            s.getTransforms().add(new Translate(k0.x, k0.y, k0.z));
            s.setMaterial(new PhongMaterial(Color.GREENYELLOW));
            group.getChildren().add(s);
            s=new Sphere(cylinder.getMinorRadius()/10d);
            Point3D k3=cylinder.getAxisEnd();
            s.getTransforms().add(new Translate(k3.x, k3.y, k3.z));
            s.setMaterial(new PhongMaterial(Color.ROSYBROWN));
            group.getChildren().add(s);
        }
        
        sceneRoot.getChildren().addAll(group);        
        
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
            //Step 2d:  Add Strafe controls
            if(keycode == KeyCode.A) { camera.setTranslateX(camera.getTranslateX() - change); }
            if(keycode == KeyCode.D) { camera.setTranslateX(camera.getTranslateX() + change); }
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
                cameraTransform.ry.setAngle(((cameraTransform.ry.getAngle() + mouseDeltaX * modifierFactor * modifier * 2.0) % 360 + 540) % 360 - 180);  // +
                cameraTransform.rx.setAngle(((cameraTransform.rx.getAngle() - mouseDeltaY * modifierFactor * modifier * 2.0) % 360 + 540) % 360 - 180);  // -
            } else if (me.isSecondaryButtonDown()) {
                double z = camera.getTranslateZ();
                double newZ = z + mouseDeltaX * modifierFactor * modifier;
                camera.setTranslateZ(newZ);
            } else if (me.isMiddleButtonDown()) {
                cameraTransform.t.setX(cameraTransform.t.getX() + mouseDeltaX * modifierFactor * modifier * 0.3);  // -
                cameraTransform.t.setY(cameraTransform.t.getY() + mouseDeltaY * modifierFactor * modifier * 0.3);  // -
            }
        });
        
        primaryStage.setTitle("F(X)yz - Cylinder Test");
        primaryStage.setScene(scene);
        primaryStage.show();        
        
//        OBJWriter writer=new OBJWriter((TriangleMesh) cylinder.getMesh(),"cylinder2");
////        writer.setMaterialColor(Color.AQUA);
////        writer.setTextureImage(getClass().getResource("res/netCylinder.png").toExternalForm());
//        writer.setTextureColors(6,ColorPalette.GREEN);
//        writer.exportMesh();
        
        lastEffect = System.nanoTime();
        AtomicInteger count=new AtomicInteger(0);
        AnimationTimer timerEffect = new AnimationTimer() {

            @Override
            public void handle(long now) {
                if (now > lastEffect + 100_000_000l) {
                    func=t->Math.pow(Math.sin(8d*Math.PI*(10d*(1-t.doubleValue())+count.get()%11)/20d),6); //<=1/6d)?1d:0d;
                    cylinder.setFunction(func);
                    
//                    mapBez.values().forEach(b->b.setDensity(dens));
                    count.getAndIncrement();
                    lastEffect = now;
                }
            }
        };
        timerEffect.start();
    }
    private Function<Number, Number> func = t->(double)t;
    private long lastEffect;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }    
}
