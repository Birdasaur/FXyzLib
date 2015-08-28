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
package org.fxyz.shapes.primitives;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Translate;
import org.fxyz.geometry.Point3D;
import org.fxyz.shapes.primitives.helper.LineSegment;
import org.fxyz.shapes.primitives.helper.MeshHelper;
import org.fxyz.shapes.primitives.helper.Text3DHelper;
import org.fxyz.shapes.primitives.helper.TextureMode;
import org.fxyz.utils.Palette.ColorPalette;
import org.fxyz.utils.Patterns;

/**
 *
 * @author José Pereda 
 */
public class Text3DMesh extends Group implements TextureMode {
    
    private final static String DEFAULT_TEXT3D = "F(X)yz 3D";
    private final static String DEFAULT_FONT = "Arial";
    private final static int DEFAULT_FONT_SIZE = 100;
    private final static double DEFAULT_HEIGHT = 10d;
    private final static double DEFAULT_GAP = 0d;
    private final static int DEFAULT_LEVEL = 1;
    private final static boolean DEFAULT_JOIN_SEGMENTS = true;
    private final static char SPACE = 32;
    
    private ObservableList<TexturedMesh> meshes=null;
    private List<Point3D> offset;
    
    public Text3DMesh(){
        this(DEFAULT_TEXT3D,DEFAULT_FONT,DEFAULT_FONT_SIZE,DEFAULT_JOIN_SEGMENTS,DEFAULT_HEIGHT,DEFAULT_GAP,DEFAULT_LEVEL);
    }
    
    public Text3DMesh(String text3D){
        this(text3D,DEFAULT_FONT,DEFAULT_FONT_SIZE,DEFAULT_JOIN_SEGMENTS,DEFAULT_HEIGHT,DEFAULT_GAP,DEFAULT_LEVEL);
    }
    
    public Text3DMesh(String text3D, String font,int fontSize){
        this(text3D,font,fontSize,DEFAULT_JOIN_SEGMENTS,DEFAULT_HEIGHT,DEFAULT_GAP,DEFAULT_LEVEL);
    }
    
    public Text3DMesh(String text3D, String font,int fontSize, boolean joinSegments){
        this(text3D,font,fontSize,joinSegments,DEFAULT_HEIGHT,DEFAULT_GAP,DEFAULT_LEVEL);
    }
    
    public Text3DMesh(String text3D, double height){
        this(text3D,DEFAULT_FONT,DEFAULT_FONT_SIZE,DEFAULT_JOIN_SEGMENTS,height,DEFAULT_GAP, DEFAULT_LEVEL);
    }
    public Text3DMesh(String text3D, double height, double gap){
        this(text3D,DEFAULT_FONT,DEFAULT_FONT_SIZE,DEFAULT_JOIN_SEGMENTS,height,gap, DEFAULT_LEVEL);
    }
    
    public Text3DMesh(String text3D, double height, int level){
        this(text3D,DEFAULT_FONT,DEFAULT_FONT_SIZE,DEFAULT_JOIN_SEGMENTS,height,DEFAULT_GAP,level);
    }
    public Text3DMesh(String text3D, double height, double gap, int level){
        this(text3D,DEFAULT_FONT,DEFAULT_FONT_SIZE,DEFAULT_JOIN_SEGMENTS,height,gap,level);
    }
    
    public Text3DMesh(String text3D, String font, int fontSize, boolean joinSegments, double height, double gap, int level){
        setText3D(text3D);
        setFont(font);
        setFontSize(fontSize);
        setJoinSegments(joinSegments);
        setHeight(height);
        setGap(gap);
        setLevel(level);
        
        updateMesh();
    }
    private final StringProperty text3D = new SimpleStringProperty(DEFAULT_TEXT3D){

        @Override
        protected void invalidated() {
            if(meshes!=null){
                updateMesh();
            }
        }
    };

    public String getText3D() {
        return text3D.get();
    }

    public final void setText3D(String value) {
        text3D.set(value);
    }

    public StringProperty text3DProperty() {
        return text3D;
    }
    private final StringProperty font = new SimpleStringProperty(DEFAULT_FONT){
        @Override
        protected void invalidated() {
            if(meshes!=null){
                updateMesh();
            }
        }
    };

    public String getFont() {
        return font.get();
    }

    public final void setFont(String value) {
        font.set(value);
    }

    public StringProperty fontProperty() {
        return font;
    }
    private final IntegerProperty fontSize = new SimpleIntegerProperty(DEFAULT_FONT_SIZE){
        @Override
        protected void invalidated() {
            if(meshes!=null){
                updateMesh();
            }
        }
    };

    public int getFontSize() {
        return fontSize.get();
    }

    public final void setFontSize(int value) {
        fontSize.set(value);
    }

    public IntegerProperty fontSizeProperty() {
        return fontSize;
    }
    private final DoubleProperty height = new SimpleDoubleProperty(DEFAULT_HEIGHT){
        @Override
        protected void invalidated() {
            if(meshes!=null){
                updateMesh();
            }
        }
    };

    public double getHeight() {
        return height.get();
    }

    public final void setHeight(double value) {
        height.set(value);
    }

    public DoubleProperty heightProperty() {
        return height;
    }
    private final DoubleProperty gap = new SimpleDoubleProperty(DEFAULT_GAP){

        @Override
        protected void invalidated() {
            if(meshes!=null){
                updateMesh();
            }
        }

    };

    public double getGap() {
        return gap.get();
    }

    public final void setGap(double value) {
        gap.set(value);
    }

    public DoubleProperty gapProperty() {
        return gap;
    }

    
    private final IntegerProperty level = new SimpleIntegerProperty(DEFAULT_LEVEL){

        @Override
        protected void invalidated() {
            if(meshes!=null){
                updateMesh();
            }
        }

    };
    
    public final int getLevel() {
        return level.get();
    }

    public final void setLevel(int value) {
        level.set(value);
    }

    public final IntegerProperty levelProperty() {
        return level;
    }
    private final BooleanProperty joinSegments = new SimpleBooleanProperty(DEFAULT_JOIN_SEGMENTS){
        @Override
        protected void invalidated() {
            if(meshes!=null){
                updateMesh();
            }
        }
    };

    public boolean isJoinSegments() {
        return joinSegments.get();
    }

    public final void setJoinSegments(boolean value) {
        joinSegments.set(value);
    }

    public BooleanProperty joinSegmentsProperty() {
        return joinSegments;
    }
    
    protected final void updateMesh() {
        // 1. Full Text to get position of each letter
        Text3DHelper helper = new Text3DHelper(text3D.get(), font.get(), fontSize.get());
        offset=helper.getOffset();

        // 2. Create mesh for each LineSegment        
        meshes=FXCollections.<TexturedMesh>observableArrayList();
        indLetters=new AtomicInteger();
        indSegments=new AtomicInteger();
        letterPath=new Path();
        
        text3D.get().chars().mapToObj(i->(char)i).filter(c->c!=SPACE)
                .forEach(letter->createLetter(letter.toString()));
        
        getChildren().setAll(meshes);
        updateTransforms();
    }
    
    private AtomicInteger indSegments, indLetters;
    private Shape letterPath=new Path();
    private void createLetter(String letter) {
        
        Text3DHelper helper = new Text3DHelper(letter, font.get(), fontSize.get());
        List<Point3D> origin = helper.getOffset();
        
        final int ind=indSegments.get();
        helper.getLineSegment().stream().map(poly->poly.getPath()).forEach(path->letterPath=Shape.union(letterPath, path));
        helper.getLineSegment().stream().forEach(poly->{
            final List<Point3D> points=poly.getPoints();
            List<List<Point3D>> holes=null;
            if(poly.getHoles().size()>0){
                holes=poly.getHoles().stream().map(LineSegment::getPoints).collect(Collectors.toList());
            }
            List<Point3D> invert = IntStream.range(0,points.size())
                    .mapToObj(i->points.get(points.size()-1-i))
                    .distinct().collect(Collectors.toList());
            Bounds bounds = null;
            if(joinSegments.get()){
                bounds=letterPath.getBoundsInParent();
            }
            TriangulatedMesh polyMesh = new TriangulatedMesh(invert,holes,level.get(),height.get(),0d,bounds);
            if(indSegments.get()>ind && joinSegments.get()){
                /*
                Combine new polyMesh with previous polyMesh into one single polyMesh
                */
                MeshHelper mh = new MeshHelper((TriangleMesh)meshes.get(meshes.size()-1).getMesh());
                MeshHelper mh1 = new MeshHelper((TriangleMesh)polyMesh.getMesh());
                mh1.addMesh(mh);
                polyMesh.updateMesh(mh1);
                meshes.set(meshes.size()-1,polyMesh);
            } else {
                meshes.add(polyMesh);
            }
            polyMesh.getTransforms().addAll(new Translate(offset.get(ind).x-origin.get(0).x+indLetters.get()*gap.doubleValue(),0,0));
            polyMesh.setCullFace(CullFace.BACK);
            polyMesh.setDrawMode(DrawMode.FILL);
            polyMesh.setDepthTest(DepthTest.ENABLE);
            polyMesh.setId(poly.getLetter());
            System.out.println("l "+poly.getLetter());
            indSegments.getAndIncrement();
        });
        indLetters.getAndIncrement();
    }

    @Override
    public void setTextureModeNone() {
        meshes.stream().forEach(m->m.setTextureModeNone());
    }

    @Override
    public void setTextureModeNone(Color color) {
        meshes.stream().forEach(m->m.setTextureModeNone(color));
    }

    @Override
    public void setTextureModeNone(Color color, String image) {
        meshes.stream().forEach(m->m.setTextureModeNone(color,image));
    }

    @Override
    public void setTextureModeImage(String image) {
        meshes.stream().forEach(m->m.setTextureModeImage(image));
    }

    @Override
    public void setTextureModePattern(Patterns.CarbonPatterns pattern, double scale) {
        meshes.stream().forEach(m->m.setTextureModePattern(pattern, scale));
    }

    @Override
    public void setTextureModeVertices3D(int colors, Function<Point3D, Number> dens) {
        meshes.stream().forEach(m->m.setTextureModeVertices3D(colors, dens));
    }

    @Override
    public void setTextureModeVertices3D(ColorPalette palette, int colors, Function<Point3D, Number> dens) {
        meshes.stream().forEach(m->m.setTextureModeVertices3D(palette, colors, dens));
    }

    @Override
    public void setTextureModeVertices3D(int colors, Function<Point3D, Number> dens, double min, double max) {
        meshes.stream().forEach(m->m.setTextureModeVertices3D(colors, dens, min, max));
    }

    @Override
    public void setTextureModeVertices1D(int colors, Function<Number, Number> function) {
        meshes.stream().forEach(m->m.setTextureModeVertices1D(colors, function));
    }

    @Override
    public void setTextureModeVertices1D(ColorPalette palette, int colors, Function<Number, Number> function) {
        meshes.stream().forEach(m->m.setTextureModeVertices1D(palette, colors, function));
    }

    @Override
    public void setTextureModeVertices1D(int colors, Function<Number, Number> function, double min, double max) {
        meshes.stream().forEach(m->m.setTextureModeVertices1D(colors, function, min, max));
    }

    @Override
    public void setTextureModeFaces(int colors) {
        meshes.stream().forEach(m->m.setTextureModeFaces(colors));
    }
    
    @Override
    public void setTextureModeFaces(ColorPalette palette, int colors) {
        meshes.stream().forEach(m->m.setTextureModeFaces(palette, colors));
    }
    
    @Override
    public void updateF(List<Number> values) {
         meshes.stream().forEach(m->m.updateF(values));
    }
    
    public void setDrawMode(DrawMode mode) {
        meshes.stream().forEach(m->m.setDrawMode(mode));
    }
    
    private void updateTransforms() {
        meshes.stream().forEach(m->m.updateTransforms());
    }
    
    public TexturedMesh getMeshFromLetter(String letter){
        return meshes.stream().filter(p->p.getId().equals(letter)).findFirst().orElse(meshes.get(0));
    }
    
    public TexturedMesh getMeshFromLetter(String letter, int order){
        return meshes.stream().filter(p->p.getId().equals(letter)).skip(order-1).findFirst().orElse(meshes.get(0));
    }
    
}
