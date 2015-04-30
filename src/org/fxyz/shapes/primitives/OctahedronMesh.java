package org.fxyz.shapes.primitives;

import javafx.scene.shape.MeshView;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.shape.TriangleMesh;

/**
 *
 * @author Moussaab AMRINE <dy_amrine@esi.dz>
 * @author  Yehya BELHAMRA <dy_belhamra@esi.dz>
 */

public class OctahedronMesh extends MeshView{
	
	private static final double DEFAULT_HEIGHT = 100.0D;
    private static final double DEFAULT_HYPOTENUSE = 100.0D;
	
	public OctahedronMesh(){
		this(DEFAULT_HEIGHT,DEFAULT_HYPOTENUSE);
	}
	
	public OctahedronMesh (double height , double hypotenuse ) { 
		setHypotenuse(hypotenuse);
		setHeight(height);
    }
	
	
	private TriangleMesh createOctahedron(double hypotenuse , double height){
		
		TriangleMesh mesh = new TriangleMesh();
		
		float hy = (float)hypotenuse;
		float he = (float)height;
		
		mesh.getPoints().addAll(
				  0 ,   0 ,   0,    //point O
				  0 ,  he , -hy/2,  //point A
				-hy/2, he ,   0,    //point B
				 hy/2, he ,   0,	//point C
				  0 ,  he ,  hy/2,	//point D
				  0 , 2*he ,  0     //point E 
				);
		
		
		mesh.getTexCoords().addAll(0,0);
		
		mesh.getFaces().addAll(
				0 , 0 , 2 , 0 , 1 , 0 ,		// O-B-A
				0 , 0 , 1 , 0 , 3 , 0 ,		// O-A-C
				0 , 0 , 3 , 0 , 4 , 0 ,		// O-C-D
				0 , 0 , 4 , 0 , 2 , 0 ,		// O-D-B
				4 , 0 , 1 , 0 , 2 , 0 ,		// D-A-B
				4 , 0 , 3 , 0 , 1 , 0 ,		// D-C-A
				5 , 0 , 2 , 0 , 1 , 0 ,		// E-B-A
				5 , 0 , 1 , 0 , 3 , 0 ,		// E-A-C
				5 , 0 , 3 , 0 , 4 , 0 ,		// E-C-D
				5 , 0 , 4 , 0 , 2 , 0 		// E-D-B
				);
		
		
		return mesh;
		
	}
	
	
	 /*
    	Properties
	  */
	private final DoubleProperty hypotenuse = new SimpleDoubleProperty(){
		@Override
		protected void invalidated() {
			setMesh(createOctahedron(getHypotenuse(), (float)getHeight()));
		}
	};

	public final double getHypotenuse() {
		return hypotenuse.get();
	}
	
	public final void setHypotenuse(double value) {
		hypotenuse.set(value);
	}

	public DoubleProperty hypotenuseProperty() {
		return hypotenuse;
	}
	
	
	private final DoubleProperty height = new SimpleDoubleProperty(){
        @Override
        protected void invalidated() {
			setMesh(createOctahedron(getHypotenuse(), (float)getHeight()));
		}        
    };

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
