package org.fxyz.shapes.primitives;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/**
 * @author Moussaab AMRINE {dm_amrine@esi.dz}
 * @author Yehya BELHAMRA  {dy_belhamra@esi.dz} {http://www.yahiab.com}
 */

public class TriangularPrismMesh extends MeshView{

    private static final double DEFAULT_HEIGHT = 100.0D;
    private static final double DEFAULT_DEPTH = 100.0D;
    private static final double DEFAULT_WIDTH = 100.0D;
    /*
        Properties
      */
    private final DoubleProperty width = new SimpleDoubleProperty() {
        @Override
        protected void invalidated() {
            setMesh(createPrism(getWidth(), (float) getDepth(), (float) getHeight()));
        }
    };
    private final DoubleProperty height = new SimpleDoubleProperty() {
        @Override
        protected void invalidated() {
            setMesh(createPrism(getWidth(), (float) getDepth(), (float) getHeight()));
        }
    };
    private final DoubleProperty depth = new SimpleDoubleProperty() {
        @Override
        protected void invalidated() {
            setMesh(createPrism(getWidth(), (float) getDepth(), (float) getHeight()));
        }
    };


    public TriangularPrismMesh(){
        this(DEFAULT_WIDTH,DEFAULT_DEPTH,DEFAULT_HEIGHT);
    }

    public TriangularPrismMesh(double width, double depth, double height) {
        setWidth(width);
        setDepth(depth);
        setHeight(height);
    }

    private TriangleMesh createPrism(double width, double depth, double height){

        TriangleMesh mesh = new TriangleMesh();

        float w = (float)width;
        float h = (float)height;
        float d = (float)depth;

        mesh.getPoints().addAll(
                -w / 2, d / 2, -h / 2,    //point A    == 0
                w / 2, d / 2, -h / 2,    //point B    == 1
                0, d / 2, h / 2,    //point C    == 2
                0, -d / 2, h / 2,    //point D    == 3
                w / 2, -d / 2, -h / 2,    //point E    == 4
                -w / 2, -d / 2, -h / 2     //point F    == 5
        );


        mesh.getTexCoords().addAll(0,0);

        mesh.getFaces().addAll(
                0, 0, 1, 0, 4, 0,   //  A-B-E
                0, 0, 4, 0, 5, 0,   //  A-E-F
                0, 0, 5, 0, 3, 0,   //  A-F-D
                0, 0, 3, 0, 2, 0,   //  A-D-C
                0, 0, 2, 0, 1, 0,   //  A-C-B
                1, 0, 2, 0, 3, 0,   //  B-C-D
                1, 0, 3, 0, 4, 0,   //  B-D-E
                4, 0, 3, 0, 5, 0    //  E-D-F
        );


        return mesh;

    }

    public final double getWidth() {
        return width.get();
    }

    public final void setWidth(double value) {
        width.set(value);
    }

    public DoubleProperty widthProperty() {
        return width;
    }

    public final double getHeight() {
        return height.get();
    }

    public final void setHeight(double value) {
        height.set(value);
    }

    public DoubleProperty heightProperty() {
        return height;
    }

    public final double getDepth() {
        return depth.get();
    }

    public final void setDepth(double value) {
        depth.set(value);
    }

    public DoubleProperty depthProperty() {
        return depth;
    }
}
