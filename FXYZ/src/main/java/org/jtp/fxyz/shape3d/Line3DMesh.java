/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.org.jtp.fxyz.shape3d;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point3D;
import javafx.scene.DepthTest;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/**
 *
 * @author Dub
 */
public class Line3DMesh extends MeshView {

    public Line3DMesh(Point3D startPoint, Point3D endPoint, double width) {
        setStartPoint(startPoint);
        setEndPoint(endPoint);
        setLineWidth((float)width);
        
        setMesh(createLine3D(getStartPoint(), getEndPoint(), getLineWidth()));
        setDepthTest(DepthTest.ENABLE);
        
    }

    /*
     Methods
     */
    

    /**
     *
     * @param start starting point
     * @param end ending point
     * @param lineWidth width of line
     * 
     *
     * @return a TriangleMesh
     */
    public final TriangleMesh createLine3D(Point3D start, Point3D end, float lineWidth) {

        float w2 = lineWidth / 2;

        TriangleMesh m = new TriangleMesh();

        //create startPoints ... default is square     
        Point3D p0 = start.subtract(w2, w2, 0), //top left point
                p1 = start.add(w2, 0, 0).subtract(0, w2, 0), //top Right
                p2 = start.subtract(w2, 0, 0).add(0, w2, 0), //bottom left
                p3 = start.add(w2, w2, 0);                     //bottom Right

        Point3D p4 = end.subtract(w2, w2, 0), //top left point
                p5 = end.add(w2, 0, 0).subtract(0, w2, 0), //top Right
                p6 = end.subtract(w2, 0, 0).add(0, w2, 0), //bottom left
                p7 = end.add(w2, w2, 0);                       //bottom Right

        m.getPoints().addAll(
                //start points
                (float) p0.getX(), (float) p0.getY(), (float) p0.getZ(),//0 top left point
                (float) p1.getX(), (float) p1.getY(), (float) p1.getZ(),//1 top Right
                (float) p2.getX(), (float) p2.getY(), (float) p2.getZ(),//2 bottom left
                (float) p3.getX(), (float) p3.getY(), (float) p3.getZ(),//3 bottom Right
                //endpoints
                (float) p4.getX(), (float) p4.getY(), (float) p4.getZ(),//4 top left point
                (float) p5.getX(), (float) p5.getY(), (float) p5.getZ(),//5 top Right
                (float) p6.getX(), (float) p6.getY(), (float) p6.getZ(),//6 bottom left
                (float) p7.getX(), (float) p7.getY(), (float) p7.getZ() //7 bottom Right                    
        );

        m.getTexCoords().addAll(
                0, 0,
                0, 0,
                0, 0,
                0, 0,
                0, 0,
                0, 0,
                0, 0,
                0, 0,
                0, 0,
                0, 0,
                0, 0,
                0, 0,
                0, 0,
                0, 0
        );

        m.getFaces().addAll(
                0, 10, 2, 5, 1, 9,
                2, 5, 3, 4, 1, 9,
                4, 7, 5, 8, 6, 2,
                6, 2, 5, 8, 7, 3,
                0, 13, 1, 9, 4, 12,
                4, 12, 1, 9, 5, 8,
                2, 1, 6, 0, 3, 4,
                3, 4, 6, 0, 7, 3,
                0, 10, 4, 11, 2, 5,
                2, 5, 4, 11, 6, 6,
                1, 9, 3, 4, 5, 8,
                5, 8, 3, 4, 7, 3
        );

        return m;
    }
    /*
     Properties
     */
    private final ObjectProperty<Point3D> startPoint = new SimpleObjectProperty<Point3D>(Point3D.ZERO){

        @Override
        protected void invalidated() {
            setMesh(createLine3D(getStartPoint(), getEndPoint(), getLineWidth()));
        }
        
    };

    public final Point3D getStartPoint() {
        return startPoint.get();
    }

    public final void setStartPoint(Point3D value) {
        startPoint.set(value);
    }

    public ObjectProperty<Point3D> startPointProperty() {
        return startPoint;
    }
    /*
    
    */
    private ObjectProperty<Point3D> endPoint = new SimpleObjectProperty<Point3D>(Point3D.ZERO){

        @Override
        protected void invalidated() {
            setMesh(createLine3D(getStartPoint(), getEndPoint(), getLineWidth()));
        }
        
    };

    public final Point3D getEndPoint() {
        return endPoint.get();
    }

    public final void setEndPoint(Point3D value) {
        endPoint.set(value);
    }

    public ObjectProperty endPointProperty() {
        return endPoint;
    }
    /*
    
    */    
    private final FloatProperty lineWidth = new SimpleFloatProperty(2.0f){

        @Override
        protected void invalidated() {
            setMesh(createLine3D(getStartPoint(), getEndPoint(), getLineWidth()));
        }
        
    };

    public final float getLineWidth() {
        return lineWidth.get();
    }

    public final void setLineWidth(float value) {
        lineWidth.set(value);
    }

    public FloatProperty lineWidthProperty() {
        return lineWidth;
    }
    
    

}
