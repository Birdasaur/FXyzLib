/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fxyz.shapes.primitives;

import eu.mihosoft.vrl.v3d.CSG;
import eu.mihosoft.vrl.v3d.Vertex;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.DepthTest;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.TriangleMesh;
import org.fxyz.geometry.Face3;
import org.fxyz.geometry.Point3D;

/**
 *
 * @author José Pereda Llamas
 * Created on 01-may-2015 - 12:20:06
 */
public class CSGMesh extends TexturedMesh {
    
    private final CSG primitive;
    
    public CSGMesh(CSG primitive){
        this.primitive=primitive;
        
        updateMesh();
        setCullFace(CullFace.BACK);
        setDrawMode(DrawMode.FILL);
        setDepthTest(DepthTest.ENABLE);
    }

    @Override
    protected final void updateMesh() {
        setMesh(null);
        mesh=createCSGMesh();
        setMesh(mesh);
    }
    
    private TriangleMesh createCSGMesh(){
        List<Vertex> vertices = new ArrayList<>();
        List<List<Integer>> indices = new ArrayList<>();

        listVertices.clear();
        primitive.getPolygons().forEach(p -> {
            List<Integer> polyIndices = new ArrayList<>();
            
            p.vertices.forEach(v -> {
                if (!vertices.contains(v)) {
                    vertices.add(v);
                    listVertices.add(new Point3D((float)v.pos.x, (float)v.pos.y, (float)v.pos.z));
                    polyIndices.add(vertices.size());
                } else {
                    polyIndices.add(vertices.indexOf(v) + 1);
                }
            });

            indices.add(polyIndices);
            
        });
        
        textureCoords=new float[]{0f,0f};
        listTextures.clear();
        listFaces.clear();
        indices.forEach(pVerts-> {
            int index1 = pVerts.get(0);
            for (int i = 0; i < pVerts.size() - 2; i++) {
                int index2 = pVerts.get(i + 1);
                int index3 = pVerts.get(i + 2);

                listTextures.add(new Face3(0, 0, 0));
                listFaces.add(new Face3(index1-1, index2-1, index3-1));
            }
        });
        int[] faceSmoothingGroups = new int[listFaces.size()];
        smoothingGroups=faceSmoothingGroups;
        
        return createMesh();
    }
}
