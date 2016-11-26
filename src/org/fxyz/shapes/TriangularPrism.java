package org.fxyz.shapes;

import javafx.beans.property.DoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import org.fxyz.shapes.containers.ShapeContainer;
import org.fxyz.shapes.primitives.TriangularPrismMesh;

/**
 * @author Moussaab AMRINE {dm_amrine@esi.dz}
 * @author Yehya BELHAMRA  {dy_belhamra@esi.dz} {http://www.yahiab.com}
 */

public class TriangularPrism extends ShapeContainer<TriangularPrismMesh> {

    private TriangularPrismMesh mesh;

    public TriangularPrism(){
        super(new TriangularPrismMesh());
        this.mesh = getShape();
    }

    public TriangularPrism(double width, double depth, double height) {
        this();
        mesh.setWidth(width);
        mesh.setDepth(depth);
        mesh.setHeight(height);
    }

    public TriangularPrism(Color c){
        this();
        this.setDiffuseColor(c);
    }

    public TriangularPrism(double width, double depth, double height, Color c){
        super(new TriangularPrismMesh(width , depth, height));
        this.mesh = getShape();
        this.setDiffuseColor(c);
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

    public final double getWidth() {
        return mesh.getWidth();
    }

    public final void setWidth(double value) {
        mesh.setWidth(value);
    }

    public final double getHeight() {
        return mesh.getHeight();
    }

    public final void setHeight(double value) {
        mesh.setHeight(value);
    }

    public final double getDepth() {
        return mesh.getDepth();
    }

    public final void setDepth(double value) {
        mesh.setDepth(value);
    }

    public DoubleProperty heightProperty() {
        return mesh.heightProperty();
    }
    public DoubleProperty depthProperty() {
        return mesh.depthProperty();
    }
    public DoubleProperty widthProperty() {
        return mesh.widthProperty();
    }
}
