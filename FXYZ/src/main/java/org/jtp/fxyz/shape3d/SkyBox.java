/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jtp.fxyz.shape3d;

import javafx.collections.FXCollections;
import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableIntegerArray;
import javafx.scene.AmbientLight;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/**
 *
 * @author Dub-Laptop
 */
public class SkyBox extends Group {

    private final ObservableIntegerArray faces = FXCollections.observableIntegerArray();
    private final ObservableFloatArray texCoords = FXCollections.observableFloatArray();
    private final ObservableFloatArray points = FXCollections.observableFloatArray();

    private final double WIDTH, HEIGHT, DEPTH;
    private TriangleMesh cube;
    private MeshView skyBox;
    private float x0, x1, x2, x3, x4, y0, y1, y2, y3;
    private Image texImg;
    
    public SkyBox(Image diff){
        this(10000, diff);
    }

    private SkyBox(double size, Image diffMap) {
        this.WIDTH = size;
        this.HEIGHT = size;
        this.DEPTH = size;
        this.cube = new TriangleMesh();
        this.skyBox = new MeshView();
        this.texImg = diffMap;
        
        PhongMaterial mat = new PhongMaterial();
        mat.setSpecularColor(Color.TRANSPARENT);
        mat.setDiffuseMap(texImg);
        
        calculatePoints();
        calculateTexCoords();
        calculateFaces();
        
        skyBox.setMesh(cube);
        skyBox.setMaterial(mat);
        skyBox.setCullFace(CullFace.NONE);
        
        AmbientLight light = new AmbientLight();
        light.getScope().add(SkyBox.this);
        
        setDepthTest(DepthTest.ENABLE);
        getChildren().addAll(skyBox,light);
    }

    private void calculatePoints() {
        float hw = (float) WIDTH / 2f;
        float hh = (float) HEIGHT / 2f;
        float hd = (float) DEPTH / 2f;

        points.addAll(
                hw, hh, hd,
                hw, hh, -hd,
                hw, -hh, hd,
                hw, -hh, -hd,
                -hw, hh, hd,
                -hw, hh, -hd,
                -hw, -hh, hd,
                -hw, -hh, -hd
        );
        cube.getPoints().addAll(points);

    }

    private void calculateFaces() {
        faces.addAll(
            0, 10, 2, 5, 1, 9,
            2, 5, 3, 4, 1, 9,
            
            4, 7, 5, 8, 6, 2,
            6, 2, 5, 8, 7, 3,
            
            0, 13, 1, 9, 4, 12,
            4, 12, 1, 9, 5, 8,
            
            2, 1, 6, 0, 3, 4,
            3, 4, 6, 0, 7, 3,
            
            0, 10, 4, 11, 2, 5,
            2, 5, 4, 11, 6, 6,
            
            1, 9, 3, 4, 5, 8,
            5, 8, 3, 4, 7, 3
        );
        cube.getFaces().addAll(faces);
    }

    private void calculateTexCoords() {
        x0 = 0.0f; x1 = 1.0f / 4.0f; x2 = 2.0f / 4.0f; x3 =  3.0f / 4.0f; x4 = 1.0f;
        y0 = 0.0f; y1 = 1.0f /3.0f; y2 = 2.0f / 3.0f; y3 = 1.0f;
        //x4 = 0; x3 = iw * 0.25f; x2 = iw / 2.0f; x1 = iw * 0.75f; x0 = iw;
        //y3 = 0; y2 = ih * 0.33f; y1 = ih * 0.66f; y0 = ih;
        float padding = 0.001f;
        texCoords.addAll(
            (x1 + padding), (y0 + padding),
            (x2 - padding), (y0 + padding),
            (x0), (y1 + padding),
            (x1 + padding), (y1 + padding),
            (x2 - padding), (y1 + padding),
            (x3), (y1 + padding),
            (x4), (y1 + padding),
            (x0), (y2 - padding),
            (x1 + padding), (y2 - padding),
            (x2 - padding), (y2 - padding),
            (x3), (y2 - padding),
            (x4), (y2 - padding),
            (x1 + padding), (y3 - padding),
            (x2), (y3 - padding)
        );
        cube.getTexCoords().addAll(texCoords);
    }

    public double getWidth() {
        return WIDTH;
    }

    public double getHeight() {
        return HEIGHT;
    }

    public double getDepth() {
        return DEPTH;
    }
    
}
