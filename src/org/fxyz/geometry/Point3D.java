package org.fxyz.geometry;

import java.util.stream.Stream;

/**
 *
 * @author Sean
 * @Description Just a useful data structure for X,Y,Z triplets.

 */
public class Point3D {
    
    public float x = 0;
    public float y = 0;
    public float z = 0;

    public float r = 0;
    public float phi = 0;
    public float theta = 0;
    /* 
    * @param X,Y,Z are all floats to align with TriangleMesh needs 
    */
    public Point3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    
        r=(float)Math.sqrt(x*x+y*y+z*z);
        phi=(float)Math.atan2(y,x);
        theta=(float)Math.acos(z/r);
    }
    
    public Stream<Float> getCoordinates() { return getCoordinates(1f); }
    public Stream<Float> getCoordinates(float fact) { return Stream.of(fact*x,fact*y,fact*z); }
    
    public Point3D add(Point3D point) {
        return add(point.x, point.y, point.z);
    }
    
    public Point3D add(float x, float y, float z) {
        return new Point3D(this.x + x, this.y + y, this.z+ z);
    }
    
    public Point3D multiply(float factor) {
        return new Point3D(this.x * factor, this.y * factor, this.z * factor);
    }
    
    public Point3D normalize() {
        final float mag = magnitude();

        if (mag == 0.0) {
            return new Point3D(0f, 0f, 0f);
        }

        return new Point3D(x / mag, y / mag, z / mag);
    }
    
    public float magnitude() {
        return (float)Math.sqrt(x * x + y * y + z * z);
    }

    @Override
    public String toString() {
        return "Point3D{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }
    
}
