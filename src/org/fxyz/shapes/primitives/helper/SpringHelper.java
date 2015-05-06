/*
 * Copyright (C) 2013-2015 F(X)yz, 
 * Sean Phillips, Jason Pollastrini and Jose Pereda
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fxyz.shapes.primitives.helper;

import java.util.ArrayList;
import java.util.List;
import org.fxyz.geometry.Point3D;
import org.fxyz.utils.GaussianQuadrature;

/** Spring base on the curve of a torus stretched over a cylinder
 * 
 * Ecuation: r[t]={R*Cos[t],R*Sin[t],h t};
 *
 * Tube around helix: S[t,u]=r[t]+a·cos(u)·n[t]+a·sin(u)·b[t] according
 * Frenet-Serret trihedron
 * http://www.usciences.edu/~lvas/math430/Curves.pdf
 * 
 * @author jpereda
 */
public class SpringHelper {
    
    public static final int tR = 0;
    public static final int tN = 1;
    public static final int tB = 2;
    
    private final double R, h;
    
    private double arc;
    private List<Point3D[]> trihedrons;
    private int subDivLength;
    
    public SpringHelper(double R, double h){
        this.R=R;
        this.h=h;
    }

    public void calculateTrihedron(int subDivLength, double arc){
        // Create points
        trihedrons=new ArrayList<>();
        this.subDivLength=subDivLength;
        this.arc=arc;
        for (int t = 0; t <= subDivLength; t++) {  // 0 - length
            trihedrons.add(getTrihedron((float) t / subDivLength * arc));
        }
    }
    /*
    t [0,<=2Pi]
    */
    private Point3D[] getTrihedron(double t){
        // r[t]
        Point3D vR = new Point3D((float)(R*Math.cos(t)),
                                 (float)(R*Math.sin(t)),
                                 (float)(h*t));
        
        // r'[t]
        Point3D dR= new Point3D((float)(-R*Math.sin(t)),
                                (float)(R*Math.cos(t)),
                                (float)(h)); 
        
        float nT=dR.magnitude(); // || r'[t] ||
        
        // r''[t]
        Point3D ddR= new Point3D((float)(-R*Math.cos(t)),
                                 (float)(-R*Math.sin(t)),
                                 (float)(0)); 
        // (|| r'[t] ||^2)'[t]
        float dn=0f;
        // T'[t]=r''[t]/||r't||-r'[t]*(|| r'[t] ||^2)'[t]/2/|| r'[t] ||^3
        Point3D dT=ddR.multiply(1f/nT).substract(dR.multiply(dn/((float)(Math.pow(nT,3d)*2d))));
        
        // T[t]=r'[t]/||r'[t]||
        Point3D T=dR.normalize();
        // N[t]=T'[t]/||T'[t]||
        Point3D N=dT.normalize();
        // B[t]=T[t]xN[t]/||T[t]xN[t]||
        Point3D B=T.crossProduct(N).normalize();
        
        // R,N,B
        return new Point3D[]{vR,N,B};
    }
    
    
    public Point3D getS(int t, float cu, float su){
        Point3D[] trihedron = trihedrons.get(t);
        // S[t,u]
        Point3D p = trihedron[SpringHelper.tR]
                        .add(trihedron[SpringHelper.tN].multiply(cu)
                        .add(trihedron[SpringHelper.tB].multiply(su)));
        p.f=((float)(t*arc)/(float)subDivLength); // [0-<=2Pi]
        return p;
        
    }
    
    public double getLength(double arc){ // [0-<=2Pi]
        GaussianQuadrature gauss = new GaussianQuadrature(5,0,arc);
        // || r'[t] ||
        return gauss.NIntegrate(t->Math.sqrt(R*R+h*h));
    }
    
    public double getKappa(double t){
        // r'[t]
        Point3D dR= new Point3D((float)(-R*Math.sin(t)),
                                (float)(R*Math.cos(t)),
                                (float)(h)); 
        float nT=dR.magnitude(); // || r'[t] ||
        
        // r''[t]
        Point3D ddR= new Point3D((float)(-R*Math.cos(t)),
                                 (float)(-R*Math.sin(t)),
                                 (float)(0)); 
        // || r''[t]xr'[t] ||
        float nddRxdR=ddR.crossProduct(dR).magnitude();
        // kappa[t] = || r''[t]xr'[t] || / || r'[t] ||^3
        return nddRxdR/(float)Math.pow(nT,3d);
        
    }
    
    public double getTau(double t){
        Point3D dR= new Point3D((float)(-R*Math.sin(t)),
                                (float)(R*Math.cos(t)),
                                (float)(h)); 
        // r''[t]
        Point3D ddR= new Point3D((float)(-R*Math.cos(t)),
                                 (float)(-R*Math.sin(t)),
                                 (float)(0)); 
        // r'''[t]
        Point3D dddR= new Point3D((float)(R*Math.sin(t)),
                                 (float)(-R*Math.cos(t)),
                                 (float)(0)); 
        //  r'[t]xr''[t] . r'''[t]
        float dRxddRxdddR=dR.crossProduct(ddR).dotProduct(dddR);
        // || r''[t]xr'[t] ||
        float ndRxddR=dR.crossProduct(ddR).magnitude();
        
        // tau[t] = r'[t]xr''[t].r'''[t] / || r''[t]xr'[t] ||^2
        return Math.abs(dRxddRxdddR/(float)Math.pow(ndRxddR,2d));
    }
    
}
