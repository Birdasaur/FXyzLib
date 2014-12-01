package org.fxyz.shapes.primitives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import org.fxyz.geometry.Point3D;
import org.fxyz.utils.DensityFunction;
import org.fxyz.utils.TriangleMeshHelper;
import static org.fxyz.utils.TriangleMeshHelper.DEFAULT_COLORS;
import static org.fxyz.utils.TriangleMeshHelper.DEFAULT_DENSITY_FUNCTION;
import org.fxyz.utils.TriangleMeshHelper.TextureType;

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
    
    private final TriangleMeshHelper helper = new TriangleMeshHelper();
    protected TriangleMesh mesh;
    
    protected final List<Point3D> listVertices = new ArrayList<>();
    protected final List<Point3D> listTextures = new ArrayList<>();
    protected final List<Point3D> listFaces = new ArrayList<>();
    
    protected TexturedMesh(){
        textureType.set(TextureType.NONE);
    }
    
    private final ObjectProperty<TriangleMeshHelper.TextureType> textureType = new SimpleObjectProperty<TriangleMeshHelper.TextureType>(){

        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
        
    };

    public void setTextureModeNone() {
        setTextureType(TriangleMeshHelper.TextureType.NONE);
    }
    
    public void setTextureModeNone(Color color) {
        if(color!=null){
            setMaterial(helper.getMaterialWithColor(color));
        }
        setTextureType(TriangleMeshHelper.TextureType.NONE);
    }
    
    public void setTextureModeImage(String image) {
        if(image!=null && !image.isEmpty()){
            setMaterial(helper.getMaterialWithImage(image));
        }
        setTextureType(TriangleMeshHelper.TextureType.IMAGE);
    }
    public void setTextureModeVertices(int colors, DensityFunction dens) {
        setColors(colors);
        setDensity(dens);
        setTextureType(TriangleMeshHelper.TextureType.COLORED_VERTICES);
    }
    
    public void setTextureModeFaces(int colors) {
        setColors(colors);
        setTextureType(TriangleMeshHelper.TextureType.COLORED_FACES);
    }
    
    public TriangleMeshHelper.TextureType getTextureType() {
        return textureType.get();
    }

    public void setTextureType(TriangleMeshHelper.TextureType value) {
        textureType.set(value);
    }

    public ObjectProperty textureTypeProperty() {
        return textureType;
    }
    
    
    private final IntegerProperty colors = new SimpleIntegerProperty(DEFAULT_COLORS){

        @Override protected void invalidated() {
            createPalette(getColors());
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

    private void createPalette(int colors) {
        helper.createPalette(colors,false);        
        setMaterial(helper.getMaterialWithPalette());
    }
    
    public void updateVertices(float factor){
        if(mesh!=null){
            mesh.getPoints().setAll(helper.updateVertices(listVertices, factor));
        }
    }
    private void updateTexture(){
        if(mesh!=null){
            mesh.getTexCoords().setAll(helper.getTextureArray());
            switch(textureType.get()){
                case NONE: 
                    mesh.getTexCoords().setAll(0f,0f);
                    break;
                case IMAGE: 
                    break;
                case COLORED_VERTICES:
                    mesh.getTexCoords().setAll(helper.getTextureArray());
                    break;
                case COLORED_FACES:
                    mesh.getTexCoords().setAll(helper.getTextureArray());
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
                case COLORED_VERTICES:
                    mesh.getFaces().setAll(helper.updateFacesWithDensityMap(listVertices, listFaces));
                    break;
                case COLORED_FACES:
                    mesh.getFaces().setAll(helper.updateFacesWithFaces(listFaces));
                    break;
            }
        }
    }
    
    protected abstract void updateMesh();
    
    protected TriangleMesh createMesh(){
        return createMesh(null);
    }
    protected TriangleMesh createMesh(float[] textureCoords){
        TriangleMesh triangleMesh = new TriangleMesh();
        triangleMesh.getPoints().setAll(helper.updateVertices(listVertices));
        
        switch(textureType.get()){
            case NONE: 
                triangleMesh.getTexCoords().setAll(0f,0f);
                triangleMesh.getFaces().setAll(helper.updateFacesWithoutTexture(listFaces));
                break;
            case IMAGE: 
                triangleMesh.getTexCoords().setAll(textureCoords);
                if(listTextures.size()>0){
                    triangleMesh.getFaces().setAll(helper.updateFacesWithTextures(listFaces,listTextures));
                } else { 
                    triangleMesh.getFaces().setAll(helper.updateFacesWithVertices(listFaces));
                }
                break;
            case COLORED_VERTICES:
                triangleMesh.getTexCoords().setAll(helper.getTextureArray());
                triangleMesh.getFaces().setAll(helper.updateFacesWithDensityMap(listVertices, listFaces));
                break;
            case COLORED_FACES:
                triangleMesh.getTexCoords().setAll(helper.getTextureArray());
                triangleMesh.getFaces().setAll(helper.updateFacesWithFaces(listFaces));
                break;
        }
        int[] faceSmoothingGroups = new int[listFaces.size()];
        Arrays.fill(faceSmoothingGroups, 1);
 
        triangleMesh.getFaceSmoothingGroups().addAll(faceSmoothingGroups);
        
        System.out.println("nodes: "+listVertices.size()+", faces: "+listFaces.size());
        return triangleMesh;
    }
}
