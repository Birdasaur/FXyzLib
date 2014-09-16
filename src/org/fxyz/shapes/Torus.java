/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.shapes;

import javafx.scene.AmbientLight;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/**
 *
 * @author JDub https://github.com/jdub1581
 * @refactored by Sean
 */
public class Torus extends Group {

    public boolean selfLightEnabled = true;
    public AmbientLight selfLight = new AmbientLight(Color.WHITE);
    public float radius;
    public float tubeRadius;
    public int tubeDivisions;
    public int radiusDivisions;
    public Color color;
    public boolean ambient;
    public boolean fill;

    public TriangleMesh mesh;
    public MeshView meshView;

    public Torus(float radius, float tubeRadius, int tubeDivisions, int radiusDivisions,
            Color color, boolean ambient, boolean fill) {
        this.radius = radius;
        this.tubeRadius = tubeRadius;
        this.tubeDivisions = tubeDivisions;
        this.radiusDivisions = radiusDivisions;
        this.color = color;
        this.ambient = ambient;
        this.fill = fill;
        this.mesh = new TriangleMesh();
        setDepthTest(DepthTest.ENABLE);
        
        
        int numVerts = tubeDivisions * radiusDivisions;
        int faceCount = numVerts * 2;
        float[] points = new float[numVerts * mesh.getPointElementSize()],
                texCoords = new float[numVerts * mesh.getTexCoordElementSize()];
        int[] faces = new int[faceCount * mesh.getFaceElementSize()];

        int pointIndex = 0, texIndex = 0, faceIndex = 0;
        float tubeFraction = 1.0f / tubeDivisions;
        float radiusFraction = 1.0f / radiusDivisions;
        float x, y, z;

        int p0 = 0, p1 = 0, p2 = 0, p3 = 0, t0 = 0, t1 = 0, t2 = 0, t3 = 0;

        // create points
        for (int tubeIndex = 0; tubeIndex < tubeDivisions; tubeIndex++) {

            float radian = tubeFraction * tubeIndex * 2.0f * 3.141592653589793f;

            for (int radiusIndex = 0; radiusIndex < radiusDivisions; radiusIndex++) {

                float localRadian = radiusFraction * radiusIndex * 2.0f * 3.141592653589793f;

                points[pointIndex + 0] = x = (radius + tubeRadius * ((float) Math.cos(radian))) * ((float) Math.cos(localRadian));
                points[pointIndex + 1] = y = (radius + tubeRadius * ((float) Math.cos(radian))) * ((float) Math.sin(localRadian));
                points[pointIndex + 2] = z = (tubeRadius * (float) Math.sin(radian));

                pointIndex += 3;

                float r = radiusIndex < tubeDivisions ? tubeFraction * radiusIndex * 2.0F * 3.141592653589793f : 0.0f;
                texCoords[texIndex] = (0.5F + (float) (Math.sin(r) * 0.5D));
                texCoords[texIndex + 1] = ((float) (Math.cos(r) * 0.5D) + 0.5F);

                texIndex += 2;

            }

        }
        //create faces
        for (int point = 0; point < (tubeDivisions); point++) {
            for (int crossSection = 0; crossSection < (radiusDivisions); crossSection++) {
                p0 = point * radiusDivisions + crossSection;
                p1 = p0 >= 0 ? p0 + 1 : p0 - (radiusDivisions);
                p1 = p1 % (radiusDivisions) != 0 ? p0 + 1 : p0 - (radiusDivisions - 1);
                p2 = (p0 + radiusDivisions) < ((tubeDivisions * radiusDivisions)) ? p0 + radiusDivisions : p0 - (tubeDivisions * radiusDivisions) + radiusDivisions;
                p3 = p2 < ((tubeDivisions * radiusDivisions) - 1) ? p2 + 1 : p2 - (tubeDivisions * radiusDivisions) + 1;
                p3 = p3 % (radiusDivisions) != 0 ? p2 + 1 : p2 - (radiusDivisions - 1);

                t0 = point * (radiusDivisions) + crossSection;
                t1 = t0 >= 0 ? t0 + 1 : t0 - (radiusDivisions);
                t1 = t1 % (radiusDivisions) != 0 ? t0 + 1 : t0 - (radiusDivisions - 1);
                t2 = (t0 + radiusDivisions) < ((tubeDivisions * radiusDivisions)) ? t0 + radiusDivisions : t0 - (tubeDivisions * radiusDivisions) + radiusDivisions;
                t3 = t2 < ((tubeDivisions * radiusDivisions) - 1) ? t2 + 1 : t2 - (tubeDivisions * radiusDivisions) + 1;
                t3 = t3 % (radiusDivisions) != 0 ? t2 + 1 : t2 - (radiusDivisions - 1);

                try {
                    faces[faceIndex] = (p2);
                    faces[faceIndex + 1] = (t3);
                    faces[faceIndex + 2] = (p0);
                    faces[faceIndex + 3] = (t2);
                    faces[faceIndex + 4] = (p1);
                    faces[faceIndex + 5] = (t0);

                    faceIndex += mesh.getFaceElementSize();

                    faces[faceIndex] = (p2);
                    faces[faceIndex + 1] = (t3);
                    faces[faceIndex + 2] = (p1);
                    faces[faceIndex + 3] = (t0);
                    faces[faceIndex + 4] = (p3);
                    faces[faceIndex + 5] = (t1);

                    faceIndex += mesh.getFaceElementSize();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        mesh.getPoints().addAll(points);
        mesh.getTexCoords().addAll(texCoords);
        mesh.getFaces().addAll(faces);

        //Create a viewable MeshView to be added to the scene
        //To add a TriangleMesh to a 3D scene you need a MeshView container object
        meshView = new MeshView(mesh);
        //The MeshView allows you to control how the TriangleMesh is rendered
        if (fill) {
            meshView.setDrawMode(DrawMode.FILL);
        } else {
            meshView.setDrawMode(DrawMode.LINE); //show lines only by default
        }
        meshView.setCullFace(CullFace.BACK); //Removing culling to show back lines
        PhongMaterial phong = new PhongMaterial();
        phong.setSpecularColor(color);
        phong.setDiffuseColor(color);
        meshView.setMaterial(phong);
        if (ambient) {
            AmbientLight light = new AmbientLight(Color.WHITE);
            light.getScope().add(meshView);
            getChildren().add(light);
        }
        getChildren().add(meshView);
    }
}
