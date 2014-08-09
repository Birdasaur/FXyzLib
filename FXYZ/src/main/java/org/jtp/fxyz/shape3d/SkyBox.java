/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jtp.fxyz.shape3d;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

/**
 *
 * @author Dub-Laptop
 */
public class SkyBox extends Group {
   
    private final CubeMesh skyBox;
    private final PhongMaterial skyMaterial = new PhongMaterial();
    private final Image texImg;
    private PerspectiveCamera camera = null;
    private final Affine affineTransform = new Affine();
    
    private static final AmbientLight ambientLighting = new AmbientLight();
    private static final PointLight pointLight = new PointLight();
    
    private final InvalidationListener cameraTransformedListener = (Observable observable) -> {
        Transform ct = (camera != null) ? camera.getLocalToSceneTransform() : null;
        if(ct != null){
            affineTransform.setTx(ct.getTx());
            affineTransform.setTy(ct.getTy());
            affineTransform.setTz(ct.getTz());
            //debuging
            //System.out.println(ct);
        }else{
            throw new UnsupportedOperationException("Camera must not be null!");
        }
    }; 
    
    // is 100,000 pixels too big?
    public SkyBox(PerspectiveCamera cam, Image diffMap){
        this(cam, 100000.0, diffMap);
    }

    public SkyBox(PerspectiveCamera cam, double size, Image diffMap) {         
        this.camera = cam;        
        this.skyBox = new CubeMesh(size);
        this.texImg = diffMap;
        
        initSkyBoxMesh();
        initLighting();
        initCameraTransformedListener();        
        
    }   

    /*
        Private Methods
    */
       
    private void initSkyBoxMesh(){
        skyBox.getTransforms().add(affineTransform);        
        skyBox.setMaterial(skyMaterial);
        skyBox.setCullFace(CullFace.NONE);
        skyBox.setDepthTest(DepthTest.ENABLE); // do we need depthTesting? better safe than sorry?
        
        
        skyMaterial.setSpecularColor(Color.TRANSPARENT); // prevents unwanted light reflections
        skyMaterial.setDiffuseMap(texImg);
        
        getChildren().add(skyBox);
    }
    
    private void initLighting(){
        // self illumination / ensures skybox is visible        
        ambientLighting.getScope().add(SkyBox.this);
        bindPointLightToAmbientLight(true);
        
        getChildren().addAll(ambientLighting, pointLight);
    }
    
    private void initCameraTransformedListener(){
        // Keep Skybox centered on Camera tx,ty,tz
        camera.localToSceneTransformProperty().addListener(cameraTransformedListener);
    }
    
    
    /*
        Properties
    */
    
    /*
        ambientLighting
    */
    
    public AmbientLight getAmbientLighting() {
        return ambientLighting;
    }

    public final void setAmbientLightColor(Color value) {
        ambientLighting.setColor(value);
    }

    public final Color getAmbientLightColor() {
        return ambientLighting.getColor();
    }

    public final ObjectProperty<Color> ambientLightColorProperty() {
        return ambientLighting.colorProperty();
    }

    public final void setAmbientLightOn(boolean value) {
        ambientLighting.setLightOn(value);
    }

    public ObservableList<Node> getAmbientLightingScope() {
        return ambientLighting.getScope();
    }
    
    public void setAmbientLightingPosition(Point3D point){
        ambientLighting.setTranslateX(point.getX());
        ambientLighting.setTranslateY(point.getY());
        ambientLighting.setTranslateZ(point.getZ());
    }

    public Point3D getAmbientLightPosition(){
        return new Point3D(
                ambientLighting.getTranslateX(), 
                ambientLighting.getTranslateY(), 
                ambientLighting.getTranslateZ()
        );
    }
    
    
    /*
        enable PointLight, disable Ambient / vicea versa
    */    
    private BooleanProperty pointLightEnabled;
    public boolean isPointLightEnabled() {
        return pointLightEnabledProperty().get();
    }
    public void setPointLightEnabled(boolean value) {
        pointLightEnabledProperty().set(value);
    }
    public BooleanProperty pointLightEnabledProperty() {
        if(pointLightEnabled == null) {
            pointLightEnabled = new SimpleBooleanProperty(true){
                @Override
                protected void invalidated() {
                    if(isPointLightEnabled()){
                        getPointLight().setLightOn(true);
                    }else{
                        getPointLight().setLightOn(false);
                    }
                }                
            };
        }
        return pointLightEnabled;
    }

    /*
        Optional PointLight
    */
    
    public PointLight getPointLight() {
        return pointLight;
    }

    public final void setPointLightColor(Color value) {
        pointLight.setColor(value);
    }
    
    public final Color getPointLightColor() {
        return pointLight.getColor();
    }

    public final ObjectProperty<Color> pointLightColorProperty() {
        return pointLight.colorProperty();
    }

    public final void setPointLightOn(boolean value) {
        pointLight.setLightOn(value);
    }

    public ObservableList<Node> getPointLightScope() {
        return pointLight.getScope();
    }
    
    public void setPointLightPosition(Point3D point){
        pointLight.setTranslateX(point.getX());
        pointLight.setTranslateY(point.getY());
        pointLight.setTranslateZ(point.getZ());
    }
    public Point3D getPointLightPosition(){
        return new Point3D(
            pointLight.getTranslateX(), 
            pointLight.getTranslateY(), 
            pointLight.getTranslateZ()
        );
    }
    
    public void bindPointLightToAmbientLight(boolean b){
        if(b){// we want to bind them
            if(!pointLight.translateXProperty().isBound()){// are they already bound?
                
                pointLight.translateXProperty().bind(ambientLighting.translateXProperty());
                pointLight.translateYProperty().bind(ambientLighting.translateYProperty());
                pointLight.translateZProperty().bind(ambientLighting.translateZProperty());
            }
        }else{// we dont want them bound
            if(pointLight.translateXProperty().isBound()){// are they already bound?
                
                pointLight.translateXProperty().unbind();
                pointLight.translateYProperty().unbind();
                pointLight.translateZProperty().unbind();                
            }
        }
    }
    
    /*
        Material
    */
    public final void setSkyMap(Image value) {
        skyMaterial.setDiffuseMap(value);
    }

    public final Image getSkyMap() {
        return skyMaterial.getDiffuseMap();
    }

    public final ObjectProperty<Image> skyMapProperty() {
        return skyMaterial.diffuseMapProperty();
    }

    public final void setSkyIlluminationMap(Image value) {
        skyMaterial.setSelfIlluminationMap(value);
    }

    public final Image getSkyIlluminationMap() {
        return skyMaterial.getSelfIlluminationMap();
    }

    public final ObjectProperty<Image> skyIlluminationMapProperty() {
        return skyMaterial.selfIlluminationMapProperty();
    }

    public TriangleMesh getSkyBoxMesh() {
        return (TriangleMesh)skyBox.getMesh();
    }
    
    
    
}
