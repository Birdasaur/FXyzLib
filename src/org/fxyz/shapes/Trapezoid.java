package org.fxyz.shapes;

import javafx.beans.property.DoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;

import org.fxyz.shapes.containers.ShapeContainer;
import org.fxyz.shapes.primitives.TrapezoidMesh;

/**
 *
 * @author Moussaab AMRINE <dy_amrine@esi.dz>
 * @author  Yehya BELHAMRA <dy_belhamra@esi.dz>
 */

public class Trapezoid extends ShapeContainer<TrapezoidMesh> {
	
	private TrapezoidMesh mesh;
	
	public Trapezoid() {
        super(new TrapezoidMesh());
        this.mesh = getShape();
    }
	
	public Trapezoid(double smallSize , double bigSize , double height ,double depth){
        this();
        mesh.setSmallSize(smallSize);
        mesh.setBigSize(bigSize);
        mesh.setheight(height); 
        mesh.setDepth(depth);
    }
	
	public Trapezoid(Color c){
        this();
        this.setDiffuseColor(c);
    }
	
	public Trapezoid(double smallSize , double bigSize , double height ,double depth , Color c){
        super(new TrapezoidMesh(smallSize ,bigSize , height , depth));
        this.mesh = getShape();
        this.setDiffuseColor(c);
    }
	
	public final void setSmallSize(double value) {
        mesh.setSmallSize(value);
    }
	public final void setBigSize(double value) {
        mesh.setBigSize(value);
    }
    public final void setHeight(double value) {
        mesh.setheight(value);
    }
    public final void setDepth(int value) {
        mesh.setDepth(value);
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
    public final double getSmallSize() {
        return mesh.getSmallSize();
    }
    public final double getBigSize() {
        return mesh.getBigSize();
    }
    public final double getHeight() {
        return mesh.getHeight();
    }
    public final double getDepth() {
        return mesh.getDepth();
    }
    public DoubleProperty smallSizeProperty() {
        return mesh.sizeSmallProperty();
    }
    public DoubleProperty bigSizeProperty() {
        return mesh.sizeBigProperty();
    }
    public DoubleProperty heightProperty() {
        return mesh.heightProperty();
    }
    public DoubleProperty depthProperty() {
        return mesh.depthProperty();
    }

}
