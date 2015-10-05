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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.IntStream;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import org.fxyz.cameras.CameraTransformer;
import org.fxyz.geometry.Point3D;
import org.fxyz.shapes.primitives.CuboidMesh;
import org.fxyz.utils.Axes;
import org.fxyz.utils.OBJWriter;

/**
 *
 * @author jpereda
 */
public class CuboidTest extends Application {
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
    private CuboidMesh cuboid;
    private Rotate rotateY;
    
    private Function<Point3D, Number> dens = p->p.x;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Group sceneRoot = new Group();
        Scene scene = new Scene(sceneRoot, sceneWidth, sceneHeight, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.BLACK);
        camera = new PerspectiveCamera(true);        
     
        //setup camera transform for rotational support
        cameraTransform.setTranslate(0, 0, 0);
        cameraTransform.getChildren().add(camera);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(-30);
        cameraTransform.ry.setAngle(-45.0);
        cameraTransform.rx.setAngle(-10.0);
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
        cuboid = new CuboidMesh(10f,12f,4f,5, new Point3D(0f,0f,0f));
//        cuboid.setDrawMode(DrawMode.LINE);
//        cuboid.setCullFace(CullFace.NONE);
    // NONE
        cuboid.setTextureModeNone(Color.ROYALBLUE);
        // IMAGE
//        cuboid.setTextureModeImage(getClass().getResource("res/netCuboid.png").toExternalForm());
    // DENSITY
//        cuboid.setTextureModeVertices3D(256*256,p->(double)p.x*p.y*p.z);
    // FACES
//        cuboid.setTextureModeFaces(1530);

        
        cuboid.getTransforms().addAll(new Rotate(0,Rotate.X_AXIS),rotateY);
        group.getChildren().add(cuboid);
        
        boolean testRayIntersection=false;
        
        if(testRayIntersection){
            /*
            RAY INTERSECTION
            */
            javafx.geometry.Point3D gloOrigin=new javafx.geometry.Point3D(4,-7,-4);
            javafx.geometry.Point3D gloTarget = new javafx.geometry.Point3D(2,3,4);

            javafx.geometry.Point3D gloDirection=gloTarget.subtract(gloOrigin).normalize();
            javafx.geometry.Point3D gloOriginInLoc = cuboid.sceneToLocal(gloOrigin);

            Bounds locBounds = cuboid.getBoundsInLocal();
            Bounds gloBounds = cuboid.localToScene(locBounds);

            Sphere s=new Sphere(0.05d);
            s.getTransforms().add(new Translate(gloOrigin.getX(), gloOrigin.getY(), gloOrigin.getZ()));
            s.setMaterial(new PhongMaterial(Color.GREENYELLOW));
            group.getChildren().add(s);
            s=new Sphere(0.05d);
            s.getTransforms().add(new Translate(gloTarget .getX(), gloTarget .getY(), gloTarget .getZ()));
            s.setMaterial(new PhongMaterial(Color.GREENYELLOW));
            group.getChildren().add(s);

            javafx.geometry.Point3D dir=gloTarget .subtract(gloOrigin).crossProduct(new javafx.geometry.Point3D(0,-1,0));
            double angle=Math.acos(gloTarget .subtract(gloOrigin).normalize().dotProduct(new javafx.geometry.Point3D(0,-1,0)));
            double h1=gloTarget .subtract(gloOrigin).magnitude();
            Cylinder c=new Cylinder(0.03d,h1);
            c.getTransforms().addAll(new Translate(gloOrigin.getX(), gloOrigin.getY()-h1/2d, gloOrigin.getZ()),
                    new Rotate(-Math.toDegrees(angle), 0d,h1/2d,0d,
                            new javafx.geometry.Point3D(dir.getX(),-dir.getY(),dir.getZ())));
            c.setMaterial(new PhongMaterial(Color.GREEN));
            group.getChildren().add(c);

            group.getChildren().add(new Axes(0.02));
            Box box=new Box(gloBounds.getWidth(), gloBounds.getHeight(), gloBounds.getDepth());
            box.setDrawMode(DrawMode.LINE);
            box.setMaterial(new PhongMaterial(Color.BLUEVIOLET));
            box.getTransforms().add(new Translate(gloBounds.getMinX()+gloBounds.getWidth()/2d, 
                    gloBounds.getMinY()+gloBounds.getHeight()/2d, gloBounds.getMinZ()+gloBounds.getDepth()/2d));
    //        group.getChildren().add(box);

            /*
            FIRST STEP; Check the ray crosses the bounding box of the shape at any of
            its 6 faces
            */
            List<javafx.geometry.Point3D> normals=Arrays.asList(new javafx.geometry.Point3D(-1,0,0),new javafx.geometry.Point3D(1,0,0),new javafx.geometry.Point3D(0,-1,0),
                    new javafx.geometry.Point3D(0,1,0), new javafx.geometry.Point3D(0,0,-1), new javafx.geometry.Point3D(0,0,1));
            List<javafx.geometry.Point3D> positions=Arrays.asList(new javafx.geometry.Point3D(locBounds.getMinX(),0,0),new javafx.geometry.Point3D(locBounds.getMaxX(),0,0),
                    new javafx.geometry.Point3D(0,locBounds.getMinY(),0), new javafx.geometry.Point3D(0,locBounds.getMaxY(),0), 
                    new javafx.geometry.Point3D(0,0,locBounds.getMinZ()), new javafx.geometry.Point3D(0,0,locBounds.getMaxZ()));
            AtomicInteger counter = new AtomicInteger();
            IntStream.range(0, 6).forEach(i->{
                // ray[t]= ori+t.dir; t/ray[t]=P in plane
                // plane P·N+d=0->(ori+t*dir)·N+d=0->t=-(ori.N+d)/(dir.N)
                // P=P(x,y,z), N={a,b,c}, d=-a·x0-b·y0-c·z0
                double d=-normals.get(i).dotProduct(positions.get(i));
                double t=-(gloOriginInLoc.dotProduct(normals.get(i))+d)/(gloDirection.dotProduct(normals.get(i)));
                javafx.geometry.Point3D locInter=gloOriginInLoc.add(gloDirection.multiply(t));
                if(locBounds.contains(locInter)){
                    counter.getAndIncrement();

                    javafx.geometry.Point3D gloInter=cuboid.localToScene(locInter);
                    Sphere s2=new Sphere(0.1d);
                    s2.getTransforms().add(new Translate(gloInter .getX(), gloInter .getY(), gloInter .getZ()));
                    s2.setMaterial(new PhongMaterial(Color.GOLD));
    //                group.getChildren().add(s2);
                }
            });
            if(counter.get()>0){
                /*
                SECOND STEP: Check if the ray crosses any of the triangles of the mesh
                */
                // triangle mesh
                org.fxyz.geometry.Point3D gloOriginInLoc1 = new org.fxyz.geometry.Point3D((float)gloOriginInLoc.getX(),(float)gloOriginInLoc.getY(),(float)gloOriginInLoc.getZ());
                org.fxyz.geometry.Point3D gloDirection1 = new org.fxyz.geometry.Point3D((float)gloDirection.getX(),(float)gloDirection.getY(),(float)gloDirection.getZ());

                System.out.println("number of intersections: "+cuboid.getIntersections(gloOriginInLoc1, gloDirection1));
            }
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
        
        primaryStage.setTitle("F(X)yz - Cuboid Test");
        primaryStage.setScene(scene);
        primaryStage.show();        
        
        OBJWriter writer=new OBJWriter((TriangleMesh) cuboid.getMesh(),"cuboid");
//        writer.setMaterialColor(Color.AQUA);
//        writer.setTextureImage(getClass().getResource("res/netCuboid.png").toExternalForm());
        writer.setTextureColors(256*256);
        writer.exportMesh();
        
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }    
}
