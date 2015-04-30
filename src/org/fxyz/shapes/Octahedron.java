package org.fxyz.shapes;

import javafx.beans.property.DoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;

import org.fxyz.shapes.containers.ShapeContainer;
import org.fxyz.shapes.primitives.OctahedronMesh;

/**
 *
 * @author Moussaab AMRINE <dy_amrine@esi.dz>
 * @author  Yehya BELHAMRA <dy_belhamra@esi.dz>
 */
public class Octahedron extends ShapeContainer<OctahedronMesh> {

	private OctahedronMesh mesh;
	
	public Octahedron() {
		super(new OctahedronMesh());
		this.mesh = getShape();
	}
	
	public Octahedron(double hypotenuse, double height) {
		this();
		mesh.setHypotenuse(hypotenuse);
		mesh.setHeight(height);
	}
	
	public Octahedron(Color c){
        this();
        this.setDiffuseColor(c);
    }
    
	public Octahedron(double hypotenuse, double height, Color c){
        super(new OctahedronMesh(hypotenuse, height));
        this.mesh = getShape();
        this.setDiffuseColor(c);
    }
	
	public final void setHypotenuse(double value) {
        mesh.setHypotenuse(value);
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
    public final double getHypotenuse() {
        return mesh.getHypotenuse();
    }
    public final double getHeight() {
        return mesh.getHeight();
    }
    public DoubleProperty heightProperty() {
        return mesh.heightProperty();
    }
	
}
