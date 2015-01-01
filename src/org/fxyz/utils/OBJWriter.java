/*
 * Copyright (C) 2013-2015 jpereda, 
 * Sean Phillips, Jason Pollastrini
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.geometry.Point2D;
import javafx.scene.shape.TriangleMesh;
import org.fxyz.geometry.Point3D;

/**
 *
 * @author jpereda
 */
public class OBJWriter {
    
    private final String newline = System.getProperty("line.separator");
    private float[] points0, texCoord0;
    private int[] faces0;
    private BufferedWriter writer = null;
        
    public OBJWriter(TriangleMesh mesh, String fileName){
        File objFile = new File(fileName+".obj");
        try{
            writer = new BufferedWriter(new FileWriter(objFile));
            
            points0=new float[mesh.getPoints().size()];
            mesh.getPoints().toArray(points0);
            List<Point3D> points1 = IntStream.range(0, points0.length/3)
                .mapToObj(i -> new Point3D(points0[3*i], points0[3*i+1], points0[3*i+2]))
                .collect(Collectors.toList());
            
            writer.write("# Vertices ("+points1.size()+")"+newline);
            points1.forEach(p->{
                try {
                    writer.write("v "+p.x+" "+p.y+" "+p.z+""+newline);
                } catch (IOException ex) {
                    System.out.println("Error writting vertex "+ex);
                }
            });
            writer.write("# End Vertices"+newline);
            writer.write(newline);
            
            texCoord0=new float[mesh.getTexCoords().size()];
            mesh.getTexCoords().toArray(texCoord0);
            
            List<Point2D> texCoord1 = IntStream.range(0, texCoord0.length/2)
                    .mapToObj(i -> new Point2D(texCoord0[2*i], texCoord0[2*i+1]))
                    .collect(Collectors.toList());
            
            writer.write("# Textures Coordinates ("+texCoord1.size()+")"+newline);
            texCoord1.forEach(t->{
                try {
                    writer.write("vt "+((float)t.getX())+" "+((float)t.getY())+""+newline);
                } catch (IOException ex) {
                    System.out.println("Error writting texture coordinate "+ex);
                }
            });
            writer.write("# End Texture Coordinates "+newline);
            writer.write(newline);
            
            faces0=new int[mesh.getFaces().size()];
            mesh.getFaces().toArray(faces0);
            List<Integer[]> faces1 = IntStream.range(0, faces0.length/6)
                    .mapToObj(i -> new Integer[]{faces0[6*i], faces0[6*i+1], 
                        faces0[6*i+2], faces0[6*i+3], 
                        faces0[6*i+4], faces0[6*i+5]})
                    .collect(Collectors.toList());
            
            writer.write("# Faces ("+faces1.size()+")"+newline);
            faces1.forEach(f->{
                try {
                    writer.write("f "+(f[0]+1)+"/"+(f[1]+1)+
                                 " "+(f[2]+1)+"/"+(f[3]+1)+
                                 " "+(f[4]+1)+"/"+(f[5]+1)+""+newline);
                } catch (IOException ex) {
                    System.out.println("Error writting face "+ex);
                }
            });
            writer.write("# End Faces "+newline);
            writer.write(newline);
            
        } catch(IOException io){
             System.out.println("Error creating writer "+io);
        } finally {
            try {
                if(writer!=null){
                    writer.close();
                }
            } catch (Exception e) {}
        }
    }
}
