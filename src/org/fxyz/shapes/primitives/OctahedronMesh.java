package org.fxyz.shapes.primitives;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/**
 * @author Moussaab AMRINE {dm_amrine@esi.dz}
 * @author Yehya BELHAMRA  {dy_belhamra@esi.dz} {http://www.yahiab.com}
 */

public class OctahedronMesh extends MeshView {

    private static final double DEFAULT_HEIGHT = 100.0D;
    private static final double DEFAULT_HYPOTENUSE = 100.0D;
    private final DoubleProperty height = new SimpleDoubleProperty() {
        @Override
        protected void invalidated() {
            setMesh(createOctahedron(getHypotenuse(), (float) getHeight()));
        }
    };
    /*
       Properties
     */
    private final DoubleProperty hypotenuse = new SimpleDoubleProperty() {
        @Override
        protected void invalidated() {
            setMesh(createOctahedron(getHypotenuse(), (float) getHeight()));
        }
    };


    public OctahedronMesh() {
        this(DEFAULT_HEIGHT, DEFAULT_HYPOTENUSE);
    }


    public OctahedronMesh(double height, double hypotenuse) {
        setHypotenuse(hypotenuse);
        setHeight(height);
    }

    private TriangleMesh createOctahedron(double hypotenuse, double height) {

        TriangleMesh mesh = new TriangleMesh();

        float hy = (float) hypotenuse;
        float he = (float) height;

        mesh.getPoints().addAll(
                0, 0, 0,    //point O
                0, he, -hy / 2,  //point A
                -hy / 2, he, 0,    //point B
                hy / 2, he, 0,    //point C
                0, he, hy / 2,    //point D
                0, 2 * he, 0     //point E
        );


        mesh.getTexCoords().addAll(0, 0);

        mesh.getFaces().addAll(
                0, 0, 2, 0, 1, 0,        // O-B-A
                0, 0, 1, 0, 3, 0,        // O-A-C
                0, 0, 3, 0, 4, 0,        // O-C-D
                0, 0, 4, 0, 2, 0,        // O-D-B
                2, 0, 5, 0, 1, 0,     // B-E-A
                4, 0, 5, 0, 2, 0,        // D-E-B
                3, 0, 5, 0, 4, 0,     // C-E-D
                1, 0, 5, 0, 3, 0       // A-E-C
        );


        return mesh;

    }

    public final double getHypotenuse() {
        return hypotenuse.get();
    }

    public final void setHypotenuse(double value) {
        hypotenuse.set(value);
    }

    public DoubleProperty hypotenuseProperty() {
        return hypotenuse;
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

}
