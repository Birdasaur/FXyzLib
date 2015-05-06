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
package org.fxyz.utils;

import eu.mihosoft.vrl.v3d.CSG;
import eu.mihosoft.vrl.v3d.Polygon;
import eu.mihosoft.vrl.v3d.PropertyStorage;
import eu.mihosoft.vrl.v3d.Vector3d;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableFloatArray;
import javafx.geometry.Point3D;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.ObservableFaceArray;
import javafx.scene.shape.TriangleMesh;
import org.fxyz.shapes.primitives.TexturedMesh;

/**
 * Loads a CSG from TriangleMesh based on JCSG from Michael Hoffer
 * 
 * @author José Pereda
 */
public class MeshUtils {
    /**
     * Loads a CSG from TriangleMesh.
     * @param mesh
     * @return CSG
     * @throws IOException if loading failed
     */
    public static CSG mesh2CSG(MeshView mesh) throws IOException {
        return mesh2CSG(mesh.getMesh());
    }
    public static CSG mesh2CSG(Mesh mesh) throws IOException {

        List<Polygon> polygons = new ArrayList<>();
        List<Vector3d> vertices = new ArrayList<>();
        if(mesh instanceof TriangleMesh){
            // Get faces
            ObservableFaceArray faces = ((TriangleMesh)mesh).getFaces();
            int[] f=new int[faces.size()];
            faces.toArray(f);

            // Get vertices
            ObservableFloatArray points = ((TriangleMesh)mesh).getPoints();
            float[] p = new float[points.size()];
            points.toArray(p);

            // convert faces to polygons
            for(int i=0; i<faces.size()/6; i++){
                int i0=f[6*i], i1=f[6*i+2], i2=f[6*i+4];
                vertices.add(new Vector3d(p[3*i0], p[3*i0+1], p[3*i0+2]));
                vertices.add(new Vector3d(p[3*i1], p[3*i1+1], p[3*i1+2]));
                vertices.add(new Vector3d(p[3*i2], p[3*i2+1], p[3*i2+2]));
                polygons.add(Polygon.fromPoints(vertices));
                vertices = new ArrayList<>();
            }
        }

        return CSG.fromPolygons(new PropertyStorage(),polygons);
    }
    
    public static void mesh2STL(String fileName, Mesh mesh) throws IOException{

        if(!(mesh instanceof TriangleMesh)){
            return;
        }
        // Get faces
        ObservableFaceArray faces = ((TriangleMesh)mesh).getFaces();
        int[] f=new int[faces.size()];
        faces.toArray(f);

        // Get vertices
        ObservableFloatArray points = ((TriangleMesh)mesh).getPoints();
        float[] p = new float[points.size()];
        points.toArray(p);

        StringBuilder sb = new StringBuilder();
        sb.append("solid meshFX\n");

        // convert faces to polygons
        for(int i=0; i<faces.size()/6; i++){
            int i0=f[6*i], i1=f[6*i+2], i2=f[6*i+4];
            Point3D pA=new Point3D(p[3*i0], p[3*i0+1], p[3*i0+2]);
            Point3D pB=new Point3D(p[3*i1], p[3*i1+1], p[3*i1+2]);
            Point3D pC=new Point3D(p[3*i2], p[3*i2+1], p[3*i2+2]);
            Point3D pN=pB.subtract(pA).crossProduct(pC.subtract(pA)).normalize();

            sb.append("  facet normal ").append(pN.getX()).append(" ").append(pN.getY()).append(" ").append(pN.getZ()).append("\n");
            sb.append("    outer loop\n");
            sb.append("      vertex ").append(pA.getX()).append(" ").append(pA.getY()).append(" ").append(pA.getZ()).append("\n");
            sb.append("      vertex ").append(pB.getX()).append(" ").append(pB.getY()).append(" ").append(pB.getZ()).append("\n");
            sb.append("      vertex ").append(pC.getX()).append(" ").append(pC.getY()).append(" ").append(pC.getZ()).append("\n");
            sb.append("    endloop\n");
            sb.append("  endfacet\n");
        }

        sb.append("endsolid meshFX\n");

        // write file
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName), Charset.forName("UTF-8"),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write(sb.toString());
        }
    }
}
