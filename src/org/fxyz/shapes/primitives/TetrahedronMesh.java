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

public class TetrahedronMesh extends MeshView{
	
	private static final double DEFAULT_HEIGHT = 100.0D;
	
	public TetrahedronMesh(){
		this(DEFAULT_HEIGHT);
	}
	
	public TetrahedronMesh(double height  ) { 
		setHeight(height);
    }
	
	
	private TriangleMesh createTetrahedron(double height){
		
		TriangleMesh mesh = new TriangleMesh();
		
		float he = (float)height;
		
		mesh.getPoints().addAll(
				0  ,  			 0 	    	   , (float)(-he/4) ,	///point O
			    0  , (float)(he/(Math.sqrt(3))) , (float)(he/4),	///point A
		(float)(-he/2) , (float)(-he/(2*Math.sqrt(3))) , (float)(he/4) , ///point B
		(float)(he/2)  , (float)(-he/(2*Math.sqrt(3))) , (float)(he/4)   ///point C
				);
		
		
		mesh.getTexCoords().addAll(0,0);
		
		mesh.getFaces().addAll(
				1 , 0 , 0 , 0 , 2 , 0 ,		// A-O-B
				2 , 0 , 0 , 0 , 3 , 0 ,		// B-O-C
				3 , 0 , 0 , 0 , 1 , 0 ,		// C-O-A
				1 , 0 , 2 , 0 , 3 , 0  		// A-B-C
				);
		
		
		return mesh;
		
	}
	
	
	 /*
    	Properties
	  */
	
	private final DoubleProperty height = new SimpleDoubleProperty(){
        @Override
        protected void invalidated() {
			setMesh(createTetrahedron((float)getHeight()));
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
