/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fxyz.shapes;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import org.fxyz.shapes.containers.ShapeContainer;
import javafx.scene.paint.Color;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import org.fxyz.shapes.primitives.SpheroidMesh;

/**
 *
 * @author jdub1581
 */
public class Spheroid extends ShapeContainer<SpheroidMesh> {
    private SpheroidMesh mesh;
    public Spheroid() {
        super(new SpheroidMesh());
        mesh = getShape();        
    }

    public Spheroid(double radius) {
        this();
        mesh.setMinorRadius((float) radius);
        mesh.setMajorRadius((float) radius);
    }

    public Spheroid(double minorRadius, double majorRadius) {
        this();
        mesh.setMinorRadius((float) minorRadius);
        mesh.setMajorRadius((float) majorRadius);
    }
    
    public Spheroid(int divisions, double minorRadius, double majorRadius) {
        this();
        mesh.setDivisions(divisions);
        mesh.setMinorRadius((float) minorRadius);
        mesh.setMajorRadius((float) majorRadius);
    }
    
    public Spheroid(Color c) {
        this();
        this.setDiffuseColor(c);
    }

    public Spheroid(double radius, Color c) {
        this(radius);
        this.setDiffuseColor(c);
    }

    public Spheroid(double minorRadius, double majorRadius, Color c) {
        this(minorRadius, majorRadius);
        this.setDiffuseColor(c);
    }
    
    public Spheroid(int divisions, double minorRadius, double majorRadius, Color c) {
        this(divisions, minorRadius, majorRadius);
        this.setDiffuseColor(c);
    }

    public final void setMajorRadius(float value) {
        mesh.setMajorRadius(value);
    }

    public final void setMinorRadius(float value) {
        mesh.setMinorRadius(value);
    }

    public final void setDivisions(int value) {
        mesh.setDivisions(value);
    }

    public final void setDrawMode(DrawMode value) {
        mesh.setDrawMode(value);
    }

    public final void setCullFace(CullFace value) {
        mesh.setCullFace(value);
    }

    public boolean isSphere() {
        return mesh.isSphere();
    }

    public boolean isOblateSpheroid() {
        return mesh.isOblateSpheroid();
    }

    public boolean isProlateSpheroid() {
        return mesh.isProlateSpheroid();
    }

    public final float getMajorRadius() {
        return mesh.getMajorRadius();
    }

    public FloatProperty majorRadiusProperty() {
        return mesh.majorRadiusProperty();
    }

    public final float getMinorRadius() {
        return mesh.getMinorRadius();
    }

    public FloatProperty minorRadiusProperty() {
        return mesh.minorRadiusProperty();
    }

    public final int getDivisions() {
        return mesh.getDivisions();
    }

    public IntegerProperty divisionsProperty() {
        return mesh.divisionsProperty();
    }
    
    
    
}
