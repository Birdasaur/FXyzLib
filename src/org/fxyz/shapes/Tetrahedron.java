/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fxyz.shapes;

import javafx.beans.property.DoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;

import org.fxyz.shapes.containers.ShapeContainer;
import org.fxyz.shapes.primitives.TetrahedronMesh;

/**
 *
 * @author Moussaab AMRINE <dy_amrine@esi.dz>
 * @author  Yehya BELHAMRA <dy_belhamra@esi.dz>
 */
public class Tetrahedron extends ShapeContainer<TetrahedronMesh> {

	private TetrahedronMesh mesh;
	
	public Tetrahedron() {
		super(new TetrahedronMesh());
		this.mesh = getShape();
	}
	
	public Tetrahedron(double height) {
		this();
		mesh.setHeight(height);
	}
	
	public Tetrahedron(Color c){
        this();
        this.setDiffuseColor(c);
    }
    
	public Tetrahedron(double height, Color c){
        super(new TetrahedronMesh(height));
        this.mesh = getShape();
        this.setDiffuseColor(c);
    }
	
    public final void setHeight(double value) {
        mesh.setHeight(value);
    }
    public final void setMaterial(Material value) {
        mesh.setMaterial(value);
    }
    public final void setDrawMode(DrawMode value) {
        mesh.setDrawMode(value);
    }
    public final void setCullFace(CullFace value) {
        mesh.setCullFace(value);
    }
    public final double getHeight() {
        return mesh.getHeight();
    }
    public DoubleProperty heightProperty() {
        return mesh.heightProperty();
    }
	
}
