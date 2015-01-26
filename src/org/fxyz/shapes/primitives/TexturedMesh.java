package org.fxyz.shapes.primitives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.TriangleMesh;
import org.fxyz.geometry.Point3D;
import org.fxyz.utils.DensityFunction;
import org.fxyz.shapes.primitives.helper.TriangleMeshHelper;
import static org.fxyz.shapes.primitives.helper.TriangleMeshHelper.DEFAULT_COLORS;
import static org.fxyz.shapes.primitives.helper.TriangleMeshHelper.DEFAULT_COLOR_PALETTE;
import static org.fxyz.shapes.primitives.helper.TriangleMeshHelper.DEFAULT_DENSITY_FUNCTION;
import static org.fxyz.shapes.primitives.helper.TriangleMeshHelper.DEFAULT_PATTERN_SCALE;
import static org.fxyz.shapes.primitives.helper.TriangleMeshHelper.DEFAULT_UNIDIM_FUNCTION;
import org.fxyz.shapes.primitives.helper.TriangleMeshHelper.SectionType;
import org.fxyz.shapes.primitives.helper.TriangleMeshHelper.TextureType;
import org.fxyz.utils.Palette.COLOR_PALETTE;

/**
 * TexturedMesh is a base class that provides support for different mesh implementations
 * taking into account four different kind of textures
 * - None
 * - Image
 * - Colored vertices
 * - Colored faces
 * 
 * For the last two ones, number of colors and density map have to be provided
 * 
 * Any subclass must use mesh, listVertices and listFaces
 * 
 * @author jpereda
 */
public abstract class TexturedMesh extends MeshView {
    
    private TriangleMeshHelper helper = new TriangleMeshHelper();
    protected TriangleMesh mesh;
    
    protected final List<Point3D> listVertices = new ArrayList<>();
    protected final List<Point3D> listTextures = new ArrayList<>();
    protected final List<Point3D> listFaces = new ArrayList<>();
    protected float[] textureCoords;
    protected int[] smoothingGroups;
    
    protected final Rectangle rectMesh=new Rectangle(0,0);
    protected final Rectangle areaMesh=new Rectangle(0,0);
    
    protected TexturedMesh(){
        sectionType.set(SectionType.CIRCLE);
        textureType.set(TextureType.NONE);
        textureType.addListener((ob,o,o1)->{
            if(mesh!=null){
                updateTexture();
                updateTextureOnFaces();
            }
        });
    }
    private final ObjectProperty<SectionType> sectionType = new SimpleObjectProperty<SectionType>(){

        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
        
    };

    public SectionType getSectionType() {
        return sectionType.get();
    }

    public void setSectionType(SectionType value) {
        sectionType.set(value);
    }

    public ObjectProperty sectionTypeProperty() {
        return sectionType;
    }
    
    private final ObjectProperty<TextureType> textureType = new SimpleObjectProperty<>();

    public void setTextureModeNone() {
        helper.setTextureType(TextureType.NONE);
        setTextureType(TextureType.NONE);
    }
    
    public void setTextureModeNone(Color color) {
        if(color!=null){
            helper.setTextureType(TextureType.NONE);
            setMaterial(helper.getMaterialWithColor(color));
        }
        setTextureType(helper.getTextureType());
    }
    
    public void setTextureModeNone(Color color, String image) {
        if(color!=null){
            helper.setTextureType(TextureType.NONE);
            setMaterial(helper.getMaterialWithColor(color, image));
        }
        setTextureType(helper.getTextureType());
    }
    
    public void setTextureModeImage(String image) {
        if(image!=null && !image.isEmpty()){
            helper.setTextureType(TextureType.IMAGE);
            setMaterial(helper.getMaterialWithImage(image));
            setTextureType(helper.getTextureType());
        }
    }
    
    public void setTextureModePattern(double scale) {
        helper.setTextureType(TextureType.PATTERN);
        patternScale.set(scale);
        setMaterial(helper.getMaterialWithPattern());
        setTextureType(helper.getTextureType());
    }
    
    public void setTextureModeVertices3D(int colors, DensityFunction<Point3D> dens) {
        helper.setTextureType(TextureType.COLORED_VERTICES_3D);
        setColors(colors);
        setDensity(dens);
        setTextureType(helper.getTextureType());
    }
    
    public void setTextureModeVertices3D(int colors, DensityFunction<Point3D> dens, double min, double max) {
        helper.setTextureType(TextureType.COLORED_VERTICES_3D);
        setMinGlobal(min);
        setMaxGlobal(max);
        setColors(colors);
        setDensity(dens);
        setTextureType(helper.getTextureType());
    }
    
    public void setTextureModeVertices1D(int colors, DensityFunction<Double> function) {
        helper.setTextureType(TextureType.COLORED_VERTICES_1D);
        setColors(colors);
        setFunction(function);
        setTextureType(helper.getTextureType());
    }
    public void setTextureModeVertices1D(int colors, DensityFunction<Double> function, double min, double max) {
        helper.setTextureType(TextureType.COLORED_VERTICES_1D);
        setMinGlobal(min);
        setMaxGlobal(max);
        setColors(colors);
        setFunction(function);
        setTextureType(helper.getTextureType());
    }
    
    public void setTextureModeFaces(int colors) {
        helper.setTextureType(TextureType.COLORED_FACES);
        setColors(colors);
        setTextureType(helper.getTextureType());
    }
    
    public TextureType getTextureType() {
        return textureType.get();
    }

    public void setTextureType(TextureType value) {
        textureType.set(value);
    }

    public ObjectProperty textureTypeProperty() {
        return textureType;
    }
    
    private final DoubleProperty patternScale = new SimpleDoubleProperty(DEFAULT_PATTERN_SCALE){

        @Override
        protected void invalidated() {
            updateTexture();
        }
        
    };
    
    public final double getPatternScale(){
        return patternScale.get();
    }
    
    public final void setPatternScale(double scale){
        patternScale.set(scale);
    }
    
    public DoubleProperty patternScaleProperty(){
        return patternScale;
    }
    
    private final IntegerProperty colors = new SimpleIntegerProperty(DEFAULT_COLORS){

        @Override protected void invalidated() {
            createPalette(getColors(),getColorPalette());
            updateTexture();
            updateTextureOnFaces();
        }
    };

    public final int getColors() {
        return colors.get();
    }

    public final void setColors(int value) {
        colors.set(value);
    }

    public IntegerProperty colorsProperty() {
        return colors;
    }
    private final ObjectProperty<COLOR_PALETTE> colorPalette = new SimpleObjectProperty<COLOR_PALETTE>(DEFAULT_COLOR_PALETTE){
        
        @Override protected void invalidated() {
            createPalette(getColors(),getColorPalette());
            updateTexture();
            updateTextureOnFaces();
        }
    };

    public COLOR_PALETTE getColorPalette() {
        return colorPalette.get();
    }

    public void setColorPalette(COLOR_PALETTE value) {
        colorPalette.set(value);
    }

    public ObjectProperty colorPaletteProperty() {
        return colorPalette;
    }
    
    private final ObjectProperty<DensityFunction> density = new SimpleObjectProperty<DensityFunction>(DEFAULT_DENSITY_FUNCTION){
        
        @Override protected void invalidated() {
            helper.setDensity(density.get());
            updateTextureOnFaces();
        }
    };
    
    public final DensityFunction getDensity(){
        return density.get();
    }
    
    public final void setDensity(DensityFunction value){
        this.density.set(value);
    }
    
    public final ObjectProperty<DensityFunction> densityProperty() {
        return density;
    }
    
    private final ObjectProperty<DensityFunction<Double>> function = new SimpleObjectProperty<DensityFunction<Double>>(DEFAULT_UNIDIM_FUNCTION){
        
        @Override protected void invalidated() {
            helper.setFunction(function.get());
            updateTextureOnFaces();
        }
    };

    public DensityFunction<Double> getFunction() {
        return function.get();
    }

    public void setFunction(DensityFunction<Double> value) {
        function.set(value);
    }

    public ObjectProperty functionProperty() {
        return function;
    }
    private final DoubleProperty minGlobal = new SimpleDoubleProperty();

    public double getMinGlobal() {
        return minGlobal.get();
    }

    public void setMinGlobal(double value) {
        minGlobal.set(value);
    }

    public DoubleProperty minGlobalProperty() {
        return minGlobal;
    }
    private final DoubleProperty maxGlobal = new SimpleDoubleProperty();

    public double getMaxGlobal() {
        return maxGlobal.get();
    }

    public void setMaxGlobal(double value) {
        maxGlobal.set(value);
    }

    public DoubleProperty maxGlobalProperty() {
        return maxGlobal;
    }
    
    private void createPalette(int colors, COLOR_PALETTE colorPalette) {
        helper.createPalette(colors,false,colorPalette);        
        setMaterial(helper.getMaterialWithPalette());
    }
    
    public void updateVertices(float factor){
        if(mesh!=null){
            mesh.getPoints().setAll(helper.updateVertices(listVertices, factor));
        }
    }
    private void updateTexture(){
        if(mesh!=null){
            switch(textureType.get()){
                case NONE: 
                    mesh.getTexCoords().setAll(0f,0f);
                    break;
                case IMAGE: 
                    mesh.getTexCoords().setAll(textureCoords);
                    break;
                case PATTERN: 
                    if(areaMesh.getHeight()>0 && areaMesh.getWidth()>0){
                        mesh.getTexCoords().setAll(
                            helper.updateTexCoordsWithPattern((int)rectMesh.getWidth(),
                                    (int)rectMesh.getHeight(),patternScale.get(),
                                    areaMesh.getHeight()/areaMesh.getWidth()));
                    } else {
                        mesh.getTexCoords().setAll(
                            helper.updateTexCoordsWithPattern((int)rectMesh.getWidth(),
                                    (int)rectMesh.getHeight(),patternScale.get()));
                    }
                    break;
                case COLORED_VERTICES_1D:
                    mesh.getTexCoords().setAll(helper.getTexturePaletteArray());
                    break;
                case COLORED_VERTICES_3D:
                    mesh.getTexCoords().setAll(helper.getTexturePaletteArray());
                    break;
                case COLORED_FACES:
                    mesh.getTexCoords().setAll(helper.getTexturePaletteArray());
                    break;
            }
        }
    }
    
    private void updateTextureOnFaces(){
        // textures for level
        if(mesh!=null){
            switch(textureType.get()){
                case NONE: 
                    mesh.getFaces().setAll(helper.updateFacesWithoutTexture(listFaces));
                    break;
                case IMAGE: 
                    if(listTextures.size()>0){
                        mesh.getFaces().setAll(helper.updateFacesWithTextures(listFaces,listTextures));
                    } else { 
                        mesh.getFaces().setAll(helper.updateFacesWithVertices(listFaces));
                    }
                    break;
                case PATTERN: 
                    mesh.getFaces().setAll(helper.updateFacesWithTextures(listFaces,listTextures));
                    break;
                case COLORED_VERTICES_1D:
                    if(minGlobal.get()<maxGlobal.get()){
                        mesh.getFaces().setAll(helper.updateFacesWithFunctionMap(listVertices, listFaces, minGlobal.get(),maxGlobal.get()));
                    } else {
//                        int[] f = helper.updateFacesWithFunctionMap(listVertices, listFaces);
//                        for(int i=0; i<f.length/6; i+=6){
//                            System.out.println("i "+f[i+1]+" "+f[i+3]+" "+f[i+5]);
//                        }
                        mesh.getFaces().setAll(helper.updateFacesWithFunctionMap(listVertices, listFaces));
                    }
                    break;
                case COLORED_VERTICES_3D:
                    if(minGlobal.get()<maxGlobal.get()){
                        mesh.getFaces().setAll(helper.updateFacesWithDensityMap(listVertices, listFaces, minGlobal.get(),maxGlobal.get()));
                    } else {
                        mesh.getFaces().setAll(helper.updateFacesWithDensityMap(listVertices, listFaces));
                    }
                    break;
                case COLORED_FACES:
                    mesh.getFaces().setAll(helper.updateFacesWithFaces(listFaces));
                    break;
            }
        }
    }
    
    protected abstract void updateMesh();
    
    protected void createTexCoords(int width, int height){
        rectMesh.setWidth(width);
        rectMesh.setHeight(height);
        textureCoords=helper.createTexCoords(width, height);
    }
    
    protected void createReverseTexCoords(int width, int height){
        rectMesh.setWidth(width);
        rectMesh.setHeight(height);
        textureCoords=helper.createReverseTexCoords(width, height);
    }
    
    protected TriangleMesh createMesh(){
        TriangleMesh triangleMesh = new TriangleMesh();
        long time=System.nanoTime();
        triangleMesh.getPoints().setAll(helper.updateVertices(listVertices));
        System.out.println("time: "+(System.nanoTime()-time)/1_000_000d);
        switch(textureType.get()){
            case NONE:
                triangleMesh.getTexCoords().setAll(textureCoords);
                triangleMesh.getFaces().setAll(helper.updateFacesWithTextures(listFaces,listTextures));
                break;
            case PATTERN: 
                if(areaMesh.getHeight()>0 && areaMesh.getWidth()>0){
                    triangleMesh.getTexCoords().setAll(
                        helper.updateTexCoordsWithPattern((int)rectMesh.getWidth(),
                                (int)rectMesh.getHeight(),patternScale.get(),
                                areaMesh.getHeight()/areaMesh.getWidth()));
                } else {
                    triangleMesh.getTexCoords().setAll(
                        helper.updateTexCoordsWithPattern((int)rectMesh.getWidth(),
                                (int)rectMesh.getHeight(),patternScale.get()));
                }
                triangleMesh.getFaces().setAll(helper.updateFacesWithTextures(listFaces,listTextures));
                break;
            case IMAGE: 
                triangleMesh.getTexCoords().setAll(textureCoords);
                if(listTextures.size()>0){
                    triangleMesh.getFaces().setAll(helper.updateFacesWithTextures(listFaces,listTextures));
                } else { 
                    triangleMesh.getFaces().setAll(helper.updateFacesWithVertices(listFaces));
                }
                break;
            case COLORED_VERTICES_1D:
                triangleMesh.getTexCoords().setAll(helper.getTexturePaletteArray());
                triangleMesh.getFaces().setAll(helper.updateFacesWithFunctionMap(listVertices, listFaces));
                break;
            case COLORED_VERTICES_3D:
                triangleMesh.getTexCoords().setAll(helper.getTexturePaletteArray());
                triangleMesh.getFaces().setAll(helper.updateFacesWithDensityMap(listVertices, listFaces));
                break;
            case COLORED_FACES:
                triangleMesh.getTexCoords().setAll(helper.getTexturePaletteArray());
                triangleMesh.getFaces().setAll(helper.updateFacesWithFaces(listFaces));
                break;
        }
        
        int[] faceSmoothingGroups = new int[listFaces.size()]; // 0 == hard edges
        Arrays.fill(faceSmoothingGroups, 1); // 1: soft edges, all the faces in same surface
        if(smoothingGroups!=null){
//            for(int i=0; i<smoothingGroups.length; i++){
//                System.out.println("i: "+smoothingGroups[i]);
//            }
            triangleMesh.getFaceSmoothingGroups().addAll(smoothingGroups);
        } else {
            triangleMesh.getFaceSmoothingGroups().addAll(faceSmoothingGroups);
        }
        
//        System.out.println("nodes: "+listVertices.size()+", faces: "+listFaces.size());
//        System.out.println("area: "+helper.getMeshArea(listVertices, listFaces));
        return triangleMesh;
    }
    
    protected double polygonalSection(double angle){
        if(sectionType.get().equals(SectionType.CIRCLE)){
            return 1d;
        }
        int n=sectionType.get().getSides();
        return Math.cos(Math.PI/n)/Math.cos((2d*Math.atan(1d/Math.tan((n*angle)/2d)))/n);
    }
    
    protected double polygonalSize(double radius){
        if(sectionType.get().equals(SectionType.CIRCLE)){
            return 2d*Math.PI*radius;
        }
        int n=sectionType.get().getSides();
        return n*Math.cos(Math.PI/n)*Math.log(-1d - 2d/(-1d + Math.sin(Math.PI/n)))*radius;
    }
    
    public Point3D getOrigin(){
        if(listVertices.size()>0){
            return listVertices.get(0);
        } 
        return new Point3D(0f,0f,0f);
    }
    
    public int getIntersections(Point3D origin, Point3D direction){
        setTextureModeFaces(10);
        
        int[] faces= helper.updateFacesWithIntersections(origin, direction, listVertices, listFaces);
        mesh.getFaces().setAll(faces);
        long time=System.currentTimeMillis();
        List<Point3D> listIntersections = helper.getListIntersections(origin, direction, listVertices, listFaces);
        System.out.println("t: "+(System.currentTimeMillis()-time));
        listIntersections.forEach(System.out::println);
        return listIntersections.size();        
    }
}
