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

/** knot base on the curve of helix of grade (p,q) around a torus 
 * 
 * Ecuation: r[t]={Cos[p t] (R+r Cos[q t]),(R+r Cos[q t]) Sin[p t],r Sin[q t]};
 *
 * Tube around helix: S[t,u]=r[t]+a·cos(u)·n[t]+a·sin(u)·b[t] according
 * Frenet-Serret trihedron
 * http://www.usciences.edu/~lvas/math430/Curves.pdf
 * 
 * @author jpereda
 */
public class KnotHelper {
    
    public static final int tR = 0;
    public static final int tN = 1;
    public static final int tB = 2;
    
    private final double R, r, p, q;
    
    private List<Point3D[]> trihedrons;
    private int subDivLength;
    
    public KnotHelper(double R, double r, double p, double q){
        this.R=R;
        this.r=r;
        this.p=p;
        this.q=q;
    }

    public void calculateTrihedron(int subDivLength){
        // Create points
        trihedrons=new ArrayList<>();
        this.subDivLength=subDivLength;
        for (int t = 0; t <= subDivLength; t++) {  // 0 - length
            trihedrons.add(getTrihedron((float) t / subDivLength * 2d*Math.PI));
        }
    }
    /*
    t [0,<=2Pi]
    */
    private Point3D[] getTrihedron(double t){
        // r[t]
        Point3D vR = new Point3D((float)(Math.cos(p*t)*(R + r*Math.cos(q*t))),
                                 (float)((R + r*Math.cos(q*t))*Math.sin(p*t)),
                                 (float)(r*Math.sin(q*t)));
        
        // r'[t]
        Point3D dR= new Point3D((float)(-(p*(R + r*Math.cos(q*t))*Math.sin(p*t)) - q*r*Math.cos(p*t)*Math.sin(q*t)),
                                (float)(p*Math.cos(p*t)*(R + r*Math.cos(q*t)) - q*r*Math.sin(p*t)*Math.sin(q*t)),
                                (float)(q*r*Math.cos(q*t))); 
        
        float nT=dR.magnitude(); // || r'[t] ||
        
        // r''[t]
        Point3D ddR= new Point3D((float)(-(q*q*r*Math.cos(p*t)*Math.cos(q*t)) - p*p*Math.cos(p*t)*(R + r*Math.cos(q*t)) + 2*p*q*r*Math.sin(p*t)*Math.sin(q*t)),
                                 (float)(-(q*q*r*Math.cos(q*t)*Math.sin(p*t)) - p*p*(R + r*Math.cos(q*t))*Math.sin(p*t) - 2*p*q*r*Math.cos(p*t)*Math.sin(q*t)),
                                 (float)(-(q*q*r*Math.sin(q*t)))); 
        // (|| r'[t] ||^2)'[t]
        float dn=(float)(-2*p*p*q*r*(R + r*Math.cos(q*t))*Math.sin(q*t));
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
        Point3D p = trihedron[KnotHelper.tR]
                        .add(trihedron[KnotHelper.tN].multiply(cu)
                        .add(trihedron[KnotHelper.tB].multiply(su)));
        p.f=((float)(t*2d*Math.PI)/(float)subDivLength); // [0-2Pi]
        return p;
        
    }
    
    public double getLength(){ // [0-2Pi]
        GaussianQuadrature gauss = new GaussianQuadrature(5,0,2d*Math.PI);
        // || r'[t] ||
        return gauss.NIntegrate(t->Math.sqrt((2*q*q*r*r + p*p*(r*r + 2*R*R) + p*p*r*(4*R*Math.cos(q*t) + r*Math.cos(2*q*t)))/2));
    }
    
    public Point3D getPositionAt(double t){
        return new Point3D((float)(Math.cos(p*t)*(R + r*Math.cos(q*t))),
                            (float)((R + r*Math.cos(q*t))*Math.sin(p*t)),
                            (float)(r*Math.sin(q*t)));
    }
    
    public Point3D getTangentAt(double t){
        return new Point3D((float)(-(p*(R + r*Math.cos(q*t))*Math.sin(p*t)) - q*r*Math.cos(p*t)*Math.sin(q*t)),
                                (float)(p*Math.cos(p*t)*(R + r*Math.cos(q*t)) - q*r*Math.sin(p*t)*Math.sin(q*t)),
                                (float)(q*r*Math.cos(q*t))).normalize();
    }
    
    public double getKappa(double t){
        // r'[t]
        Point3D dR= new Point3D((float)(-(p*(R + r*Math.cos(q*t))*Math.sin(p*t)) - q*r*Math.cos(p*t)*Math.sin(q*t)),
                                (float)(p*Math.cos(p*t)*(R + r*Math.cos(q*t)) - q*r*Math.sin(p*t)*Math.sin(q*t)),
                                (float)(q*r*Math.cos(q*t))); 
        float nT=dR.magnitude(); // || r'[t] ||
        
        // r''[t]
        Point3D ddR= new Point3D((float)(-(q*q*r*Math.cos(p*t)*Math.cos(q*t)) - p*p*Math.cos(p*t)*(R + r*Math.cos(q*t)) + 2*p*q*r*Math.sin(p*t)*Math.sin(q*t)),
                                 (float)(-(q*q*r*Math.cos(q*t)*Math.sin(p*t)) - p*p*(R + r*Math.cos(q*t))*Math.sin(p*t) - 2*p*q*r*Math.cos(p*t)*Math.sin(q*t)),
                                 (float)(-(q*q*r*Math.sin(q*t)))); 
        // || r''[t]xr'[t] ||
        float nddRxdR=ddR.crossProduct(dR).magnitude();
        // kappa[t] = || r''[t]xr'[t] || / || r'[t] ||^3
        return nddRxdR/(float)Math.pow(nT,3d);
        
    }
    
    public double getTau(double t){
        // r'[t]
        Point3D dR= new Point3D((float)(-(p*(R + r*Math.cos(q*t))*Math.sin(p*t)) - q*r*Math.cos(p*t)*Math.sin(q*t)),
                                (float)(p*Math.cos(p*t)*(R + r*Math.cos(q*t)) - q*r*Math.sin(p*t)*Math.sin(q*t)),
                                (float)(q*r*Math.cos(q*t))); 
        // r''[t]
        Point3D ddR= new Point3D((float)(-(q*q*r*Math.cos(p*t)*Math.cos(q*t)) - p*p*Math.cos(p*t)*(R + r*Math.cos(q*t)) + 2*p*q*r*Math.sin(p*t)*Math.sin(q*t)),
                                 (float)(-(q*q*r*Math.cos(q*t)*Math.sin(p*t)) - p*p*(R + r*Math.cos(q*t))*Math.sin(p*t) - 2*p*q*r*Math.cos(p*t)*Math.sin(q*t)),
                                 (float)(-(q*q*r*Math.sin(q*t)))); 
        // r'''[t]
        Point3D dddR = new Point3D((float)(p*(p*p*R + (p*p + 3*q*q)*r*Math.cos(q*t))*Math.sin(p*t) + q*(3*p*p + q*q)*r*Math.cos(p*t)*Math.sin(q*t)),
                                   (float)(-(p*Math.cos(p*t)*(p*p*R + (p*p + 3*q*q)*r*Math.cos(q*t))) + q*(3*p*p + q*q)*r*Math.sin(p*t)*Math.sin(q*t)),
                                   (float)(-(q*q*q*r*Math.cos(q*t))));
        //  r'[t]xr''[t] . r'''[t]
        float dRxddRxdddR=dR.crossProduct(ddR).dotProduct(dddR);
        // || r''[t]xr'[t] ||
        float ndRxddR=dR.crossProduct(ddR).magnitude();
        
        // tau[t] = r'[t]xr''[t].r'''[t] / || r''[t]xr'[t] ||^2
        return Math.abs(dRxddRxdddR/(float)Math.pow(ndRxddR,2d));
    }
    
}
