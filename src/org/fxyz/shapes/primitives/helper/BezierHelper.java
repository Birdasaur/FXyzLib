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
import java.util.Arrays;
import java.util.List;
import org.fxyz.geometry.Point3D;
import org.fxyz.utils.GaussianQuadrature;

/** Bezier cubic curve passing through external points (a,d), with control
 * points (b,c)
 * 
 * Ecuation: r[t]=(1-t)^3·a+3(1-t)^2·t·b+3(1-3)·t^2·c+t^3·d
 *
 * Tube around spline: S[t,u]=r[t]+a·cos(u)·n[t]+a·sin(u)·b[t] according
 * Frenet-Serret trihedron
 * http://www.usciences.edu/~lvas/math430/Curves.pdf
 * 
 * @author jpereda
 */
public class BezierHelper {
    
    public static final int R = 0;
    public static final int N = 1;
    public static final int B = 2;
    
    private final List<Point3D> points;
    
    private Point3D ab, bc, cd, abc, bcd, abcd;
    private double length;
    private List<Point3D[]> trihedrons;
    private int subDivLength;
    
    public BezierHelper(Point3D a, Point3D b, Point3D c, Point3D d){
        points=Arrays.asList(a,b,c,d);
    }
    
    public List<Point3D> getPoints() { return points; }

    public void preProcess(){
        ab=points.get(1).substract(points.get(0));
        bc=points.get(2).substract(points.get(1));
        cd=points.get(3).substract(points.get(2));
        
        abc=bc.substract(ab);
        bcd=cd.substract(bc);
        
        abcd=bcd.substract(abc);
        
        length=getLength();
    }
    
    public void calculateTrihedron(int subDivLength){
        // Create points
        trihedrons=new ArrayList<>();
        this.subDivLength=subDivLength;
        for (int t = 0; t <= subDivLength; t++) {  // 0 - length
            trihedrons.add(getTrihedron((float) t / subDivLength));
        }
    }
    /*
    t [0,1]
    */
    private Point3D[] getTrihedron(double t){
        if(ab==null || bc==null || cd==null){
            preProcess();
        }
        
        // r[t]
        Point3D R=points.get(0).multiply((float)Math.pow(1d-t, 3))
                        .add(points.get(1).multiply((float)(3d*Math.pow(1d-t, 2)*t))
                          .add(points.get(2).multiply((float)(3d*(1d-t)*Math.pow(t, 2)))
                              .add(points.get(3).multiply((float)(Math.pow(t, 3)))))); 
        // r'[t]
        Point3D dR=ab.multiply((float)(3d*Math.pow(1d-t, 2)))
                    .add(bc.multiply((float)(6d*(1d-t)*t))
                        .add(cd.multiply((float)(3d*Math.pow(t, 2))))); 
        float nT=dR.magnitude(); // || r'[t] ||
        
        // r''[t]
        Point3D ddR=abc.multiply((float)(6d*(1d-t))).add(bcd.multiply((float)(6d*t))); 
        // (|| r'[t] ||^2)'[t]
        float dn=(float)(2*(6*bc.x*(1-2*t)-6*ab.x*(1-t)+6*cd.x*t)*(3*ab.x*Math.pow(1-t,2)+6*bc.x*(1-t)*t+3*cd.x*Math.pow(t,2))+ 
                         2*(6*bc.y*(1-2*t)-6*ab.y*(1-t)+6*cd.y*t)*(3*ab.y*Math.pow(1-t,2)+6*bc.y*(1-t)*t+3*cd.y*Math.pow(t,2))+ 
                         2*(6*bc.z*(1-2*t)-6*ab.z*(1-t)+6*cd.z*t)*(3*ab.z*Math.pow(1-t,2)+6*bc.z*(1-t)*t+3*cd.z*Math.pow(t,2)));
        // T'[t]=r''[t]/||r't||-r'[t]*(|| r'[t] ||^2)'[t]/2/|| r'[t] ||^3
        Point3D dT=ddR.multiply(1f/nT).substract(dR.multiply(dn/((float)(Math.pow(nT,3d)*2d))));
        
        // T[t]=r'[t]/||r'[t]||
        Point3D T=dR.normalize();
        // N[t]=T'[t]/||T'[t]||
        Point3D N=dT.normalize();
        // B[t]=T[t]xN[t]/||T[t]xN[t]||
        Point3D B=T.crossProduct(N).normalize();
        
        // R,N,B
        return new Point3D[]{R,N,B};
    }
    
    
    public Point3D getS(int t, float cu, float su){
        if(ab==null || bc==null || cd==null){
            preProcess();
        }
        
        Point3D[] trihedron = trihedrons.get(t);
        // S[t,u]
        Point3D p = trihedron[BezierHelper.R]
                        .add(trihedron[BezierHelper.N].multiply(cu)
                        .add(trihedron[BezierHelper.B].multiply(su)));
        p.f=((float)t/(float)subDivLength); // [0-1]
        return p;
        
    }
    
    public double getLength(){
        if(ab==null || bc==null || cd==null){
            preProcess();
        }
        GaussianQuadrature gauss = new GaussianQuadrature(5,0,1);
        // || r'[t] ||
        return gauss.NIntegrate(t->(double)(ab.multiply((float)(3d*Math.pow(1d-t, 2)))
                                    .add(bc.multiply((float)(6d*(1d-t)*t))
                                    .add(cd.multiply((float)(3d*Math.pow(t, 2)))))
                                    .magnitude()));
    }
    
    public double getKappa(double t){
        if(ab==null || bc==null || cd==null){
            preProcess();
        }
        // r'[t]
        Point3D dR=ab.multiply((float)(3d*Math.pow(1d-t, 2)))
                    .add(bc.multiply((float)(6d*(1d-t)*t))
                        .add(cd.multiply((float)(3d*Math.pow(t, 2))))); 
        float nT=dR.magnitude(); // || r'[t] ||
        
        // r''[t]
        Point3D ddR=abc.multiply((float)(6d*(1d-t))).add(bcd.multiply((float)(6d*t))); 
        // || r''[t]xr'[t] ||
        float nddRxdR=ddR.crossProduct(dR).magnitude();
        // kappa[t] = || r''[t]xr'[t] || / || r'[t] ||^3
        return nddRxdR/(float)Math.pow(nT,3d);
        
    }
    
    public double getTau(double t){
        if(ab==null || bc==null || cd==null){
            preProcess();
        }
        // r'[t]
        Point3D dR=ab.multiply((float)(3d*Math.pow(1d-t, 2)))
                    .add(bc.multiply((float)(6d*(1d-t)*t))
                        .add(cd.multiply((float)(3d*Math.pow(t, 2))))); 
        // r''[t]
        Point3D ddR=abc.multiply((float)(6d*(1d-t))).add(bcd.multiply((float)(6d*t))); 
        //  r'[t]xr''[t] . r'''[t]
        float dRxddRxdddR=dR.crossProduct(ddR).dotProduct(abcd.multiply(6f));
        // || r''[t]xr'[t] ||
        float ndRxddR=dR.crossProduct(ddR).magnitude();
        
        // tau[t] = r'[t]xr''[t].r'''[t] / || r''[t]xr'[t] ||^2
        return Math.abs(dRxddRxdddR/(float)Math.pow(ndRxddR,2d));
    }
    
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder("{");
        points.forEach(p->{
            sb.append("{").append(p.x).append(",").append(p.y).append(",").append(p.z).append("}");
        });
        return sb.append("}").toString();
    }
    
}
