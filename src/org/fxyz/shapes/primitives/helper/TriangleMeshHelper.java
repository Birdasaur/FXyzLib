package org.fxyz.shapes.primitives.helper;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import org.fxyz.geometry.Face3;
import org.fxyz.geometry.Point3D;
import org.fxyz.utils.DensityFunction;
import org.fxyz.utils.FloatCollector;
import org.fxyz.utils.NormalMap;
import org.fxyz.utils.Palette;
import org.fxyz.utils.Palette.COLOR_PALETTE;
import org.fxyz.utils.Patterns;

/**
 *
 * @author jpereda
 */
public class TriangleMeshHelper {
    
    public static enum TextureType {
        NONE, // without texture, simple colored
        IMAGE, // an image is loaded 
        PATTERN, // an image from a pattern
        COLORED_FACES, // a palette is used to color faces
        COLORED_VERTICES_3D, // a palette is used to color vertices with a density map (point 3D)
        COLORED_VERTICES_1D // a palette is used to color vertices with a function map (x 1D)
    }
    public static final TextureType DEFAULT_TEXTURE_TYPE= TextureType.NONE;
    private TextureType textureType=DEFAULT_TEXTURE_TYPE;
    private boolean reverseTexture=false;   
    
    public enum SectionType {
        CIRCLE(0),
        TRIANGLE(3),
        QUADRILATERAL(4),
        PENTAGON(5),
        HEXAGON(6),
        HEPTAGON(7),
        OCTAGON(8),
        NONAGON(9),
        DECAGON(10),
        DODECAGON(12);
        
        private final int sides;
        
        SectionType(int sides){
            this.sides=sides;
        }
        
        public int getSides() { return sides; }
        
    }
    
    public static final SectionType DEFAULT_SECTION_TYPE= SectionType.CIRCLE;
    private SectionType sectionType=DEFAULT_SECTION_TYPE;
    
    public TriangleMeshHelper(){
    }
    
    public void setSectionType(SectionType sectionType){
        this.sectionType = sectionType;
    }
    public SectionType getSectionType() { return sectionType; }
    
    public void setTextureType(TextureType textureType){
        this.textureType = textureType;
        
        switch(textureType){
            case COLORED_FACES:
            case COLORED_VERTICES_1D:
                createPalette();
                function=DEFAULT_UNIDIM_FUNCTION;        
                break;
            case COLORED_VERTICES_3D:
                createPalette();
                density=DEFAULT_DENSITY_FUNCTION;        
                break;
            case PATTERN: 
                createPattern();
                break;
        }
    }
    
    public TextureType getTextureType() { return textureType; }
    
    /*
    Patterns
    */
    public static final double DEFAULT_PATTERN_SCALE = 0d;
    public static final int DEFAULT_WIDTH =  12;
    public static final int DEFAULT_HEIGHT = 12;
    public final static boolean DEFAULT_SAVE_PATTERN = false;
    private Patterns patterns;
    private int patternWidth;
    private int patternHeight;
    
    public final void createPattern(){
        createPattern(DEFAULT_WIDTH,DEFAULT_HEIGHT,DEFAULT_SAVE_PATTERN);
    }
    public void createPattern(boolean save){
        createPattern(DEFAULT_WIDTH,DEFAULT_HEIGHT,save);
    }
    public void createPattern(int width, int height, boolean save){
        this.patternWidth=width;
        this.patternHeight=height;
        patterns=new Patterns(width,height);
        patterns.createPattern(save);
    }
    public Image getPatternImage() {
        if(patterns==null){
            createPattern();
        }
        return patterns.getPatternImage();
    }
    
    public Material getMaterialWithPattern(){
        PhongMaterial mat = new PhongMaterial();
        mat.setDiffuseMap(getPatternImage());
        mat.setSpecularPower(32);
        mat.setDiffuseColor(Color.WHITE);
        mat.setSpecularColor(Color.WHITE);
        return mat;
    }
    
    /*
    Colors, palette
    */
    public final static Color DEFAULT_DIFFUSE_COLOR = Color.WHITE;
    public final static int DEFAULT_COLORS = 16;
    public final static boolean DEFAULT_SAVE_PALETTE = false;
    public final static COLOR_PALETTE DEFAULT_COLOR_PALETTE = COLOR_PALETTE.HSB;
    private Palette palette;
    private int colors;
    
    public final void createPalette(){
        createPalette(DEFAULT_COLORS,DEFAULT_SAVE_PALETTE,DEFAULT_COLOR_PALETTE);
    }
    public void createPalette(int colors){
        createPalette(colors,DEFAULT_SAVE_PALETTE,DEFAULT_COLOR_PALETTE);
    }
    public void createPalette(boolean save){
        createPalette(DEFAULT_COLORS,save,DEFAULT_COLOR_PALETTE);
    }
    public void createPalette(int colors,boolean save){
        createPalette(DEFAULT_COLORS,save,DEFAULT_COLOR_PALETTE);
    }
    public void createPalette(int colors, boolean save, COLOR_PALETTE palette_colors){
        this.colors=colors;
        palette=new Palette(colors,palette_colors);
        palette.createPalette(save);
    }

    public Image getPaletteImage() {
        if(palette==null){
            createPalette();
        }
        return palette.getPaletteImage();
    }
    
    public Material getMaterialWithPalette(){
        PhongMaterial mat = new PhongMaterial();
        mat.setDiffuseMap(getPaletteImage());
        mat.setSpecularPower(20);
        mat.setDiffuseColor(Color.WHITE);
        mat.setSpecularColor(Color.WHITE);
        return mat;
    }
    
    public Material getMaterialWithColor(Color color){
        PhongMaterial mat = new PhongMaterial(color);
        mat.setSpecularPower(32);
        mat.setSpecularColor(Color.WHITE);
        return mat;
    }
    
    public Material getMaterialWithColor(Color color, String image){
        PhongMaterial mat = new PhongMaterial(color);
        if(image!=null && !image.isEmpty()){
            Image img = new Image(image);
            mat.setDiffuseMap(img);
            NormalMap normal = new NormalMap(img);
//            normal.setIntensity(10);
//            normal.setIntensityScale(2);
            mat.setBumpMap(normal);
        }
        mat.setSpecularPower(32);
        mat.setSpecularColor(Color.WHITE);
        return mat;
    }
    
    public float[] getTexturePaletteArray(){
        if(palette==null){
            createPalette();
        }
        return IntStream.range(0,colors).boxed()
            .flatMapToDouble(palette::getTextureLocation)
            .collect(()->new FloatCollector(2*colors), FloatCollector::add, FloatCollector::join)
            .toArray();
    }
    
    /*
    density functions
    */
    public final static DensityFunction<Point3D> DEFAULT_DENSITY_FUNCTION= p->0d;
    private DensityFunction<Point3D> density;
    private double min = 0d;
    private double max = 1d;
    
    public void setDensity(DensityFunction<Point3D> density){
        this.density=density;
    }
    
    public int mapDensity(Point3D p){
        int f=(int)(((density.eval(p)-min)/(max-min))*colors);
        if(f<0){
            f=0;
        }
        if(f>=colors){
            f=colors-1;
        }
        return f;
    }

    public final static DensityFunction<Double> DEFAULT_UNIDIM_FUNCTION= x->0d;
    private DensityFunction<Double> function;
    
    public void setFunction(DensityFunction<Double> function){
        this.function=function;
    }
    
    public int mapFunction(double x){
        int f=(int)(((function.eval(x)-min)/(max-min))*colors);
        if(f<0){
            f=0;
        }
        if(f>=colors){
            f=colors-1;
        }
        return f;
    }
    
    public int mapFaces(int face, int numFaces){
        int f=(int)((((double)face)/((double)numFaces)) *colors);
        if(f<0){
            f=0;
        }
        if(f>=colors){
            f=colors-1;
        }
        return f;
    }

    public void updateExtremes(double min, double max){
        this.max=max;
        this.min=min;
        if(max==min){
            this.max=1.0+min;
        }
    }
    
    public void updateExtremes(List<Point3D> points){
        max=points.parallelStream().mapToDouble(density::eval).max().orElse(1.0);
        min=points.parallelStream().mapToDouble(density::eval).min().orElse(0.0);
        max=(float)Math.round(max*1e6)/1e6;
        min=(float)Math.round(min*1e6)/1e6;
        if(max==min){
            max=1.0+min;
        }
//        System.out.println("Min: "+min+", max: "+max);  
    }
    
    public void updateExtremesByFunction(List<Point3D> points){
        max=points.parallelStream().mapToDouble(p->function.eval(new Double(p.f))).max().orElse(1.0);
        min=points.parallelStream().mapToDouble(p->function.eval(new Double(p.f))).min().orElse(0.0);
        max=(float)Math.round(max*1e6)/1e6;
        min=(float)Math.round(min*1e6)/1e6;
        if(max==min){
            max=1.0+min;
        }
//        System.out.println("Min: "+min+", max: "+max);  
    }
    /*
    image
    */
    public Material getMaterialWithImage(String image){
        PhongMaterial mat = new PhongMaterial();
        mat.setDiffuseMap(new Image(image));
        return mat;
    }
    
    /*
    Mesh updating
    */
    public float[] updateVertices(List<Point3D> points){
        return points.stream()
            .flatMapToDouble(Point3D::getCoordinates)
            .collect(()->new FloatCollector(points.size()*3), FloatCollector::add, FloatCollector::join)
            .toArray();       
    }
    
    public float[] updateVertices(List<Point3D> points, float factor){
        return points.stream()
            .flatMapToDouble(p->p.getCoordinates(factor))
            .collect(()->new FloatCollector(points.size()*3), FloatCollector::add, FloatCollector::join)
            .toArray(); 
    }
    
    public float[] createTexCoords(int width, int height){
        reverseTexture=false;
        int index=0;
        float[] textureCoords = new float[(width+1)*(height+1)*2];
        for (int v = 0; v <= height; v++) {
            float dv = (float) v / ((float)(height));
            for (int u = 0; u <= width; u++) {
                textureCoords[index] = (float) u /((float)(width));
                textureCoords[index + 1] = dv;
                index+=2;
            }
        }
        return textureCoords;
    }
    
    public float[] createReverseTexCoords(int width, int height){
        reverseTexture=true;
        int index=0;
        float[] textureCoords = new float[(width+1)*(height+1)*2];
        for (int x = 0; x <= width; x++) {
            float dx = (float) x /((float)(width));
            for (int y = 0; y <= height; y++) {
                float dy = (float) y / ((float)(height));
                textureCoords[index] = dx;
                textureCoords[index + 1] = dy;
                index+=2;
            }
        }
        return textureCoords;
    }
    
    public float[] updateTexCoordsWithPattern(int rectWidth, int rectHeight){
        return updateTexCoordsWithPattern(rectWidth, rectHeight, 1d, 1d);
    }
    
    public float[] updateTexCoordsWithPattern(int rectWidth, int rectHeight, double scale){
        return updateTexCoordsWithPattern(rectWidth, rectHeight, scale, 1d);
    }
    
    public float[] updateTexCoordsWithPattern(int rectWidth, int rectHeight, double scale, double ratio){
        int index=0;
        float[] textureCoords = new float[(rectWidth+1)*(rectHeight+1)*2];
        float restHeight=patternHeight-((float)(1d/(patternHeight/scale)*ratio*rectWidth))%patternHeight;
        float factorHeight = (float)(1d+restHeight/(1d/(patternHeight/scale)*ratio*rectWidth));
        float restWidth=patternWidth-((float)(rectWidth/(patternWidth/scale)))%patternWidth;
        float factorWidth = (float)(1d+restWidth/(rectWidth/(patternWidth/scale)));
        if(reverseTexture){
            for (int x = 0; x <= rectWidth; x++) {
                float dx = (float) ((x)/(patternWidth/scale)*factorWidth);
                for (int y = 0; y <= rectHeight; y++) {
                    float dy = (float) ((y)/(patternHeight/scale)*ratio/rectHeight*rectWidth*factorHeight);
                    textureCoords[index] = dx;
                    textureCoords[index + 1] = dy;
                    index+=2;
                }
            }
        } else {
            for (int y = 0; y <= rectHeight; y++) {
                float dy = (float) ((y)/(patternHeight/scale)*ratio/rectHeight*rectWidth*factorHeight);
                for (int x = 0; x <= rectWidth; x++) {
                    textureCoords[index] = (float) ((x)/(patternWidth/scale)*factorWidth);
                    textureCoords[index + 1] = dy;
                    index+=2;
                }
            }
        }
        return textureCoords;
    }
    
    public int[] updateFacesWithoutTexture(List<Face3> faces){
        return faces.parallelStream().map(Face3::getFace).flatMapToInt(i->i).toArray();
    }
    
    public int[] updateFacesWithVertices(List<Face3> faces){
        return faces.parallelStream().map(f->f.getFace(f)).flatMapToInt(i->i).toArray();
    }
    
    public int[] updateFacesWithTextures(List<Face3> faces, List<Face3> textures){
        if(faces.size()>textures.size()){
            return null;
        }
        AtomicInteger count=new AtomicInteger();
        return faces.stream().map(f->f.getFace(textures.get(count.getAndIncrement()))).flatMapToInt(i->i).toArray();
    }
    
    public int[] updateFacesWithDensityMap(List<Point3D> points, List<Face3> faces){
        updateExtremes(points);
        return faces.parallelStream().map(f->{
                int t0=mapDensity(points.get(f.p0));
                int t1=mapDensity(points.get(f.p1));
                int t2=mapDensity(points.get(f.p2));
                return f.getFace(t0, t1, t2);
            }).flatMapToInt(i->i).toArray();
    }
    
    public int[] updateFacesWithDensityMap(List<Point3D> points, List<Face3> faces, double min, double max){
        updateExtremes(min, max);
        return faces.parallelStream().map(f->{
                int t0=mapDensity(points.get(f.p0));
                int t1=mapDensity(points.get(f.p1));
                int t2=mapDensity(points.get(f.p2));
                return f.getFace(t0, t1, t2);
            }).flatMapToInt(i->i).toArray();
    }
       
    public int[] updateFacesWithFunctionMap(List<Point3D> points, List<Face3> faces){
        updateExtremesByFunction(points);
        return faces.parallelStream().map(f->{
                int t0=mapFunction(points.get(f.p0).f);
                int t1=mapFunction(points.get(f.p1).f);
                int t2=mapFunction(points.get(f.p2).f);
                return f.getFace(t0, t1, t2);
            }).flatMapToInt(i->i).toArray();
    }
    public int[] updateFacesWithFunctionMap(List<Point3D> points, List<Face3> faces, double min, double max){
        updateExtremes(min, max);
        return faces.parallelStream().map(f->{
                int t0=mapFunction(points.get(f.p0).f);
                int t1=mapFunction(points.get(f.p1).f);
                int t2=mapFunction(points.get(f.p2).f);
                return f.getFace(t0, t1, t2);
            }).flatMapToInt(i->i).toArray();
    }
    public int[] updateFacesWithFaces(List<Face3> faces){
        AtomicInteger count=new AtomicInteger();
        return faces.stream().map(f->f.getFace(mapFaces(count.getAndIncrement(),faces.size()))).flatMapToInt(i->i).toArray();
    }
    /*
    utils
    */
    public double getMeshArea(List<Point3D> points, List<Face3> faces){
        return faces.parallelStream().mapToDouble(f->{
                Point3D a = points.get(f.p0);
                Point3D b = points.get(f.p1);
                Point3D c = points.get(f.p2);
                return b.substract(a).crossProduct((c.substract(a))).magnitude()/2.0;
            }).reduce(Double::sum).getAsDouble();
    }
    
    private final float EPS = 0.000001f;
    /*
    Based on Fast, Minimum Storage Ray/Triangle Intersection
    Tomas Möller & Ben Trumbore
    http://www.graphics.cornell.edu/pubs/1997/MT97.pdf

    * origin and direction of a ray, in local coordinates of the shape, 
      to avoid transformation all the triangles to scene coordinates
      
    * This sets the texture of every face: 0 without intersection, 1 intersected
    */
    public int[] updateFacesWithIntersections(Point3D origin, Point3D direction,List<Point3D> points, List<Face3> faces){
        return faces.parallelStream().map(f->{
                Point3D a = points.get(f.p0);
                Point3D b = points.get(f.p1);
                Point3D c = points.get(f.p2);
                
                Point3D edge1 = b.substract(a);
                Point3D edge2 = c.substract(a);
                Point3D pvec=direction.crossProduct(edge2);
                float det=edge1.dotProduct(pvec);
                
                int t0=0;
                if(det<=-EPS || det>=EPS){
                    float inv_det=1f/det;
                    Point3D tvec=origin.substract(a);
                    float u = tvec.dotProduct(pvec)*inv_det;
                    if(u>=0f && u<=1f){
                        Point3D qvec=tvec.crossProduct(edge1);
                        float v = direction.dotProduct(qvec)*inv_det;
                        if(v>=0 && u+v<=1f){
//                            float t = c.dotProduct(qvec)*inv_det;
                            t0=6;
//                            System.out.println("t: "+t+", u: "+u+", v: "+v+" (a: "+a+", b: "+b+", c: "+c+")");
                        }
                    }
                }
                return f.getFace(t0);
            }).flatMapToInt(i->i).toArray();
    }
    
    /*
    Return a list of interesected faces (with their 3 vertices)
    */
    public List<Face3> getListIntersections(Point3D origin, Point3D direction,List<Point3D> points, List<Face3> faces){
        return faces.parallelStream().filter(f->{
            Point3D a = points.get(f.p0);
            Point3D b = points.get(f.p1);
            Point3D c = points.get(f.p2);

            Point3D edge1 = b.substract(a);
            Point3D edge2 = c.substract(a);
            Point3D pvec=direction.crossProduct(edge2);
            float det=edge1.dotProduct(pvec);

            if(det<=-EPS || det>=EPS){
                float inv_det=1f/det;
                Point3D tvec=origin.substract(a);
                float u = tvec.dotProduct(pvec)*inv_det;
                if(u>=0f && u<=1f){
                    Point3D qvec=tvec.crossProduct(edge1);
                    float v = direction.dotProduct(qvec)*inv_det;
                    if(v>=0 && u+v<=1f){
                        return true;
                    }
                }
            }
            return false;
        }).collect(Collectors.toList());
    }
}
