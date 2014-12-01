package org.fxyz.shapes.primitives;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.DepthTest;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.TriangleMesh;
import org.fxyz.geometry.Point3D;
import org.fxyz.utils.TriangleMeshHelper;
import org.fxyz.utils.TriangleMeshHelper.TextureType;

/**
 *  Spring based on this model:  http://math.stackexchange.com/a/461637
 *  Wrapped around a torus: http://math.stackexchange.com/a/324553
 *  Using Frenet-Serret trihedron: http://mathematica.stackexchange.com/a/18612
 */
public class CurvedSpringMesh extends TexturedMesh {

    private static final double DEFAULT_MAJOR_RADIUS = 10.0D;
    private static final double DEFAULT_MINOR_RADIUS = 2.0D;
    private static final double DEFAULT_WIRE_RADIUS = 0.2D;
    private static final double DEFAULT_PITCH = 5.0D;
    private static final double DEFAULT_LENGTH = 100.0D;
    
    private static final int DEFAULT_LENGTH_DIVISIONS = 200;
    private static final int DEFAULT_WIRE_DIVISIONS = 50;
    private static final int DEFAULT_LENGTH_CROP = 0;
    private static final int DEFAULT_WIRE_CROP = 0;
    
    private static final double DEFAULT_START_ANGLE = 0.0D;
    private static final double DEFAULT_X_OFFSET = 0.0D;
    private static final double DEFAULT_Y_OFFSET = 0.0D;
    private static final double DEFAULT_Z_OFFSET = 1.0D;
    
    public CurvedSpringMesh() {
        this(DEFAULT_MAJOR_RADIUS, DEFAULT_MINOR_RADIUS, DEFAULT_WIRE_RADIUS, DEFAULT_PITCH, DEFAULT_LENGTH,
             DEFAULT_LENGTH_DIVISIONS, DEFAULT_WIRE_DIVISIONS, DEFAULT_LENGTH_CROP, DEFAULT_WIRE_CROP);
    }

    public CurvedSpringMesh(double majorRadius, double minorRadius, double wireRadius, double pitch, double length) {
        this(majorRadius, minorRadius, wireRadius, pitch, length, 
             DEFAULT_LENGTH_DIVISIONS, DEFAULT_WIRE_DIVISIONS, DEFAULT_LENGTH_CROP, DEFAULT_WIRE_CROP);
    }

    public CurvedSpringMesh(double majorRadius, double minorRadius, double wireRadius, double pitch, double length, 
                      int rDivs, int tDivs, int lengthCrop, int wireCrop) {
        
        setMajorRadius(majorRadius);
        setMinorRadius(minorRadius);
        setWireRadius(wireRadius);
        setPitch(pitch);
        setLength(length);
        setLengthDivisions(rDivs);
        setWireDivisions(tDivs);
        setLengthCrop(lengthCrop);
        setWireCrop(wireCrop);
        
        updateMesh();
        setCullFace(CullFace.BACK);
        setDrawMode(DrawMode.FILL);
        setDepthTest(DepthTest.ENABLE);
    }

    @Override
    protected final void updateMesh(){   
        setMesh(null);
        mesh=createSpring((float) getMajorRadius(), (float) getMinorRadius(), (float) getWireRadius(), (float) getPitch(), (float) getLength(),
            getLengthDivisions(), getWireDivisions(), getLengthCrop(), getWireCrop(),
            (float) getTubeStartAngleOffset(), (float)getxOffset(),(float)getyOffset(), (float)getzOffset());
        setMesh(mesh);
    }
    
    private final DoubleProperty majorRadius = new SimpleDoubleProperty(DEFAULT_MAJOR_RADIUS){

        @Override protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public final double getMajorRadius() {
        return majorRadius.get();
    }

    public final void setMajorRadius(double value) {
        majorRadius.set(value);
    }

    public DoubleProperty majorRadiusProperty() {
        return majorRadius;
    }

    private final DoubleProperty minorRadius = new SimpleDoubleProperty(DEFAULT_MINOR_RADIUS){

        @Override protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public final double getMinorRadius() {
        return minorRadius.get();
    }

    public final void setMinorRadius(double value) {
        minorRadius.set(value);
    }

    public DoubleProperty minorRadiusProperty() {
        return minorRadius;
    }
    
    private final DoubleProperty wireRadius = new SimpleDoubleProperty(DEFAULT_WIRE_RADIUS){

        @Override protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public final double getWireRadius() {
        return wireRadius.get();
    }

    public final void setWireRadius(double value) {
        wireRadius.set(value);
    }

    public DoubleProperty wireRadiusProperty() {
        return wireRadius;
    }

    private final DoubleProperty length = new SimpleDoubleProperty(DEFAULT_LENGTH){

        @Override protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public final double getLength() {
        return length.get();
    }

    public final void setLength(double value) {
        length.set(value);
    }

    public DoubleProperty lengthProperty() {
        return length;
    }

    private final DoubleProperty pitch = new SimpleDoubleProperty(DEFAULT_PITCH){

        @Override protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public final double getPitch() {
        return pitch.get();
    }

    public final void setPitch(double value) {
        pitch.set(value);
    }

    public DoubleProperty pitchProperty() {
        return pitch;
    }
    
    private final IntegerProperty lengthDivisions = new SimpleIntegerProperty(DEFAULT_LENGTH_DIVISIONS){

        @Override protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public final int getLengthDivisions() {
        return lengthDivisions.get();
    }

    public final void setLengthDivisions(int value) {
        lengthDivisions.set(value);
    }

    public IntegerProperty lengthDivisionsProperty() {
        return lengthDivisions;
    }

    private final IntegerProperty wireDivisions = new SimpleIntegerProperty(DEFAULT_WIRE_DIVISIONS){

        @Override protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public final int getWireDivisions() {
        return wireDivisions.get();
    }

    public final void setWireDivisions(int value) {
        wireDivisions.set(value);
    }

    public IntegerProperty wireDivisionsProperty() {
        return wireDivisions;
    }

    private final IntegerProperty lengthCrop = new SimpleIntegerProperty(DEFAULT_LENGTH_CROP){

        @Override protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };
    
    public final int getLengthCrop() {
        return lengthCrop.get();
    }

    public final void setLengthCrop(int value) {
        lengthCrop.set(value);
    }

    public IntegerProperty lengthCropProperty() {
        return lengthCrop;
    }

    private final IntegerProperty wireCrop = new SimpleIntegerProperty(DEFAULT_WIRE_CROP){

        @Override protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };
    
    public final int getWireCrop() {
        return wireCrop.get();
    }

    public final void setWireCrop(int value) {
        wireCrop.set(value);
    }

    public IntegerProperty wireCropProperty() {
        return wireCrop;
    }
    
    private final DoubleProperty tubeStartAngleOffset = new SimpleDoubleProperty(DEFAULT_START_ANGLE){

        @Override protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public final double getTubeStartAngleOffset() {
        return tubeStartAngleOffset.get();
    }

    public void setTubeStartAngleOffset(double value) {
        tubeStartAngleOffset.set(value);
    }

    public DoubleProperty tubeStartAngleOffsetProperty() {
        return tubeStartAngleOffset;
    }
    private final DoubleProperty xOffset = new SimpleDoubleProperty(DEFAULT_X_OFFSET){

        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
        
    };

    public final double getxOffset() {
        return xOffset.get();
    }

    public void setxOffset(double value) {
        xOffset.set(value);
    }

    public DoubleProperty xOffsetProperty() {
        return xOffset;
    }
    private final DoubleProperty yOffset = new SimpleDoubleProperty(DEFAULT_Y_OFFSET){

        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
        
    };

    public final double getyOffset() {
        return yOffset.get();
    }

    public void setyOffset(double value) {
        yOffset.set(value);
    }

    public DoubleProperty yOffsetProperty() {
        return yOffset;
    }
    private final DoubleProperty zOffset = new SimpleDoubleProperty(DEFAULT_Z_OFFSET){

        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
        
    };

    public final double getzOffset() {
        return zOffset.get();
    }

    public void setzOffset(double value) {
        zOffset.set(value);
    }

    public DoubleProperty zOffsetProperty() {
        return zOffset;
    }
    
    private TriangleMesh createSpring(float majorRadius, float minorRadius, float wireRadius, float pitch, float length, 
            int subDivLength, int subDivWire, int cropLength, int cropWire,
            float startAngle, float xOffset, float yOffset, float zOffset) {
 
        listVertices.clear();
        listFaces.clear();
        
        final int pointSize = 3;
        final int texCoordSize = 2;
        int numDivLength = subDivLength + 1-2*cropLength;
        int numDivWire = subDivWire + 1-2*cropWire;
        int numVerts = numDivWire * numDivLength;
        float pointX, pointY, pointZ;
        float texCoords[] = new float[numVerts * texCoordSize];
        int index=0;
        double R=majorRadius;
        double r=minorRadius;
        double h=pitch;
        double a=wireRadius;
        
        double h2=h*h, h4=h2*h2, r2=r*r, r3=r2*r, r4=r3*r, R2=R*R, R3=R2*R, R4=R3*R;
        // Create points and texCoords
        for (int u = cropWire; u <= subDivWire-cropWire; u++) { // -Pi - +Pi
            float du = (float) (((double)u)*2d*Math.PI / ((double)subDivWire));
            if(cropWire>0 || (cropWire==0 && u<subDivWire)){
                double cdu=Math.cos(du), sdu=Math.sin(du); 
                for (int t = cropLength; t <= subDivLength-cropLength; t++) {  // 0 - length
                    float dt = (float) t / subDivLength * length/pitch;
                    double cdt=Math.cos(dt), chdt=Math.cos(h*dt), c2hdt=Math.cos(2*h*dt), c3hdt=Math.cos(3*h*dt), c4hdt=Math.cos(4*h*dt);
                    double sdt=Math.sin(dt), shdt=Math.sin(h*dt);
                    pointX=(float)(cdt*(R + r*chdt) + (a*cdu*(-(cdt*(2*(3 + 5*h2)*r2*R + 4*R3 + 
                        r*((3 + 8*h2 + 4*h4)*r2 + 4*(3 + h2)*R2)*chdt + 2*(3 + h2)*r2*R*c2hdt + 
                        r3*c3hdt)) + 2*h*r*(r2 + 4*h2*r2 + 2*R2 + 4*r*R*chdt + r2*c2hdt)*sdt*shdt))/
                        (Math.sqrt(r2 + 2*h2*r2 + 2*R2 + 4*r*R*chdt + r2*c2hdt)*
                        Math.sqrt((3 + h2)*(1 + 4*h2 + 8*h4)*r4 + 4*(6 + 11*h2 + 2*h4)*r2*R2 + 8*R4 + 
                        8*r*R*((3 + 8*h2 + 4*h4)*r2 + 2*(2 + h2)*R2)*chdt + 
                        4*r2*((1 + 3*h2 - h4)*r2 + 3*(2 + h2)*R2)*c2hdt + 
                        r3*(8*R*c3hdt - (-1 + h2)*r*c3hdt))) + 
                        (2*Math.sqrt(2)*a*h*r*(chdt*(R + (1 + h2)*r*chdt)*sdt + h*cdt*(-R + r*chdt)*shdt + h2*r*sdt*Math.pow(shdt,2))*sdu)/
                        Math.sqrt((3 + h2)*(1 + 4*h2 + 8*h4)*r4 + 4*(6 + 11*h2 + 2*h4)*r2*R2 + 8*R4 + 
                        8*r*R*((3 + 8*h2 + 4*h4)*r2 + 2*(2 + h2)*R2)*chdt + 
                        4*r2*((1 + 3*h2 - h4)*r2 + 3*(2 + h2)*R2)*c2hdt + 
                        r3*(8*R*c3hdt - (-1 + h2)*r*c3hdt)));

                    pointY=(float)((R + r*chdt)*sdt + (a*cdu*((-2*(3 + 5*h2)*r2*R - 4*R3 - 
                        r*((3 + 8*h2 + 4*h4)*r2 + 4*(3 + h2)*R2)*chdt - 2*(3 + h2)*r2*R*c2hdt - 
                        r3*c3hdt)*sdt - 2*h*r*cdt*(r2 + 4*h2*r2 + 2*R2 + 4*r*R*chdt + r2*c2hdt)*shdt))/
                        (Math.sqrt(r2 + 2*h2*r2 + 2*R2 + 4*r*R*chdt + r2*c2hdt)*
                        Math.sqrt((3 + h2)*(1 + 4*h2 + 8*h4)*r4 + 4*(6 + 11*h2 + 2*h4)*r2*R2 + 8*R4 + 
                        8*r*R*((3 + 8*h2 + 4*h4)*r2 + 2*(2 + h2)*R2)*chdt + 
                        4*r2*((1 + 3*h2 - h4)*r2 + 3*(2 + h2)*R2)*c2hdt + 
                        r3*(8*R*c3hdt - (-1 + h2)*r*c3hdt))) - 
                        (2*Math.sqrt(2)*a*h*r*((cdt*(2*R*chdt + r*(1 + 2*h2 + c2hdt)))/2. + h*(R - r*chdt)*sdt*shdt)*sdu)/
                        Math.sqrt((3 + h2)*(1 + 4*h2 + 8*h4)*r4 + 4*(6 + 11*h2 + 2*h4)*r2*R2 + 8*R4 + 
                        8*r*R*((3 + 8*h2 + 4*h4)*r2 + 2*(2 + h2)*R2)*chdt + 
                        4*r2*((1 + 3*h2 - h4)*r2 + 3*(2 + h2)*R2)*c2hdt + 
                        r3*(8*R*c3hdt - (-1 + h2)*r*c3hdt)));

                    pointZ=(float)(r*shdt - (4*a*h2*r*(h2*r2 + R2 + r*R*chdt)*cdu*shdt)/
                        (Math.sqrt(r2 + 2*h2*r2 + 2*R2 + 4*r*R*chdt + r2*c2hdt)*
                        Math.sqrt((3 + h2)*(1 + 4*h2 + 8*h4)*r4 + 4*(6 + 11*h2 + 2*h4)*r2*R2 + 8*R4 + 
                        8*r*R*((3 + 8*h2 + 4*h4)*r2 + 2*(2 + h2)*R2)*chdt + 
                        4*r2*((1 + 3*h2 - h4)*r2 + 3*(2 + h2)*R2)*c2hdt + 
                        r3*(8*R*c3hdt - (-1 + h2)*r*c3hdt))) + 
                        (Math.sqrt(2)*a*(r2 + 3*h2*r2 + 2*R2 + 2*(2 + h2)*r*R*chdt - (-1 + h2)*r2*c2hdt)*sdu)/
                        Math.sqrt((3 + h2)*(1 + 4*h2 + 8*h4)*r4 + 4*(6 + 11*h2 + 2*h4)*r2*R2 + 8*R4 + 
                        8*r*R*((3 + 8*h2 + 4*h4)*r2 + 2*(2 + h2)*R2)*chdt + 
                        4*r2*((1 + 3*h2 - h4)*r2 + 3*(2 + h2)*R2)*c2hdt + 
                        r3*(8*R*c3hdt - (-1 + h2)*r*c3hdt)));                    
                    listVertices.add(new Point3D(pointX, pointY, pointZ));
                    if(getTextureType().equals(TextureType.IMAGE)){
                        texCoords[index] = (((float)(t-cropLength))/((float)(subDivLength-2f*cropLength)));
                        texCoords[index + 1] = (((float)(u-cropWire))/((float)(subDivWire-2f*cropWire)));
                        index+=2;
                    }
                }
            }
        }
        // Create faces
        for (int u = cropWire; u < subDivWire-cropWire; u++) { // -Pi - +Pi
            for (int t = cropLength; t < subDivLength-cropLength; t++) { // 0 - length
                int p00 = (u-cropWire) * numDivLength + (t-cropLength);
                int p01 = p00 + 1;
                int p10 = p00 + numDivLength;
                if(cropWire==0 && u==subDivWire-1){
                    p10-=subDivWire*numDivLength;
                }
                int p11 = p10 + 1;
                listFaces.add(new Point3D(p00,p10,p11));
                listFaces.add(new Point3D(p11,p01,p00));            
            }
        }
        if(getTextureType().equals(TextureType.IMAGE)){
            return createMesh(texCoords);
        }
        return createMesh();
    }
    
}
