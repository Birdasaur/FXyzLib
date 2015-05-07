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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Translate;
import org.fxyz.geometry.Point3D;
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
    private final static int DEFAULT_LEVEL = 1;
    private final static char SPACE = 32;
    
    private List<TriangulatedMesh> meshes=null;
    private List<Point3D> offset;
    
    public Text3DMesh(){
        this(DEFAULT_TEXT3D,DEFAULT_FONT,DEFAULT_FONT_SIZE,DEFAULT_HEIGHT,DEFAULT_LEVEL);
    }
    
    public Text3DMesh(String text3D){
        this(text3D,DEFAULT_FONT,DEFAULT_FONT_SIZE,DEFAULT_HEIGHT,DEFAULT_LEVEL);
    }
    
    public Text3DMesh(String text3D,String font,int fontSize){
        this(text3D,font,fontSize,DEFAULT_HEIGHT,DEFAULT_LEVEL);
    }
    
    public Text3DMesh(String text3D,double height){
        this(text3D,DEFAULT_FONT,DEFAULT_FONT_SIZE,height,DEFAULT_LEVEL);
    }
    
    public Text3DMesh(String text3D,double height, int level){
        this(text3D,DEFAULT_FONT,DEFAULT_FONT_SIZE,height,level);
    }
    
    public Text3DMesh(String text3D, String font, int fontSize, double height, int level){
        setText3D(text3D);
        setFont(font);
        setFontSize(fontSize);
        setHeight(height);
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
    
    protected final void updateMesh() {
        Text3DHelper helper = new Text3DHelper(text3D.get(), font.get(), fontSize.get());
        offset=helper.getOffset();
        
        meshes=new ArrayList<>();
        AtomicInteger index=new AtomicInteger();
        text3D.get().chars().mapToObj(i->(char)i).forEach(letter->{
            if(letter!=SPACE){
                TriangulatedMesh mesh=createLetter(letter.toString(),index.getAndIncrement());
                mesh.setCullFace(CullFace.NONE);
                mesh.setDrawMode(DrawMode.FILL);
                mesh.setDepthTest(DepthTest.ENABLE);
                meshes.add(mesh);
            }
            
        });
        
        getChildren().setAll(meshes);
                
    }
    

    private List<List<Point3D>> holes=null;
    private TriangulatedMesh createLetter(String letter, int index) {
        
        Text3DHelper helper = new Text3DHelper(letter, font.get(), fontSize.get());
        List<List<Point3D>> paths = helper.getPaths();
        List<Point3D> origin = helper.getOffset();
        
        final List<Point3D> points=paths.get(paths.size()-1);
        if(paths.size()>1){
            holes=new ArrayList<>();
            IntStream.range(0,paths.size()-1)
                    .forEach(i->holes.add(paths.get(i).stream().distinct().collect(Collectors.toList())));
        } else {
            holes=null;
        }
        List<Point3D> invert = IntStream.range(0,points.size()).mapToObj(i->points.get(points.size()-1-i)).distinct().collect(Collectors.toList());
        TriangulatedMesh mesh = new TriangulatedMesh(invert,holes,level.get(),height.get(),0d);
        mesh.getTransforms().addAll(new Translate(offset.get(index).x-origin.get(0).x,0,0));

        return mesh;
    }

    @Override
    public void setTextureModeNone() {
        getStream().forEach(m->m.setTextureModeNone());
    }

    @Override
    public void setTextureModeNone(Color color) {
        getStream().forEach(m->m.setTextureModeNone(color));
    }

    @Override
    public void setTextureModeNone(Color color, String image) {
        getStream().forEach(m->m.setTextureModeNone(color,image));
    }

    @Override
    public void setTextureModeImage(String image) {
        getStream().forEach(m->m.setTextureModeImage(image));
    }

    @Override
    public void setTextureModePattern(Patterns.CarbonPatterns pattern, double scale) {
        getStream().forEach(m->m.setTextureModePattern(pattern, scale));
    }

    @Override
    public void setTextureModeVertices3D(int colors, Function<Point3D, Number> dens) {
        getStream().forEach(m->m.setTextureModeVertices3D(colors, dens));
    }

    @Override
    public void setTextureModeVertices3D(ColorPalette palette, int colors, Function<Point3D, Number> dens) {
        getStream().forEach(m->m.setTextureModeVertices3D(palette, colors, dens));
    }

    @Override
    public void setTextureModeVertices3D(int colors, Function<Point3D, Number> dens, double min, double max) {
        getStream().forEach(m->m.setTextureModeVertices3D(colors, dens, min, max));
    }

    @Override
    public void setTextureModeVertices1D(int colors, Function<Number, Number> function) {
        getStream().forEach(m->m.setTextureModeVertices1D(colors, function));
    }

    @Override
    public void setTextureModeVertices1D(ColorPalette palette, int colors, Function<Number, Number> function) {
        getStream().forEach(m->m.setTextureModeVertices1D(palette, colors, function));
    }

    @Override
    public void setTextureModeVertices1D(int colors, Function<Number, Number> function, double min, double max) {
        getStream().forEach(m->m.setTextureModeVertices1D(colors, function, min, max));
    }

    @Override
    public void setTextureModeFaces(int colors) {
        getStream().forEach(m->m.setTextureModeFaces(colors));
    }
    
    @Override
    public void setTextureModeFaces(ColorPalette palette, int colors) {
        getStream().forEach(m->m.setTextureModeFaces(palette, colors));
    }
    
    public void setDrawMode(DrawMode mode) {
        getStream().forEach(m->m.setDrawMode(mode));
    }
    
    private Stream<TriangulatedMesh> getStream() {
        return getChildren().stream().map(TriangulatedMesh.class::cast);
    }
}
