/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fxyz.shapes.primitives.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.fxyz.geometry.Point3D;

/**
 *
 * @author José Pereda Llamas
 * Created on 06-may-2015 - 13:20:42
 */
public class Text3DHelper {
    
    private final String text;
    private final List<List<Point3D>> paths;
    private List<Point3D> list;
    private final List<Point3D> origin;
    private Point3D p0;
    
    public Text3DHelper(String text, String font, int size){
        this.text=text;
        list=new ArrayList<>();
        paths=new ArrayList<>();
        origin=new ArrayList<>();
        
        Text textNode = new Text(text);
        textNode.setFont(new Font(font,size));
        Path subtract = (Path)(Shape.subtract(textNode, new Rectangle(0, 0)));
        subtract.getElements().forEach(this::getPoints);
    }
    
    public List<List<Point3D>> getPaths() { return paths; }
    
    public List<Point3D> getOffset(){
        return origin.stream().skip(origin.size()-text.toCharArray().length+text.chars().filter(c->c==' ').count())
                .sorted((p1,p2)->(int)(p1.x-p2.x)).collect(Collectors.toList());
        
    }
    
    private void getPoints(PathElement elem){
        if(elem instanceof MoveTo){
            list=new ArrayList<>();
            p0=new Point3D((float)((MoveTo)elem).getX(),(float)((MoveTo)elem).getY(),0f);
            list.add(p0);
            origin.add(p0);
        } else if(elem instanceof LineTo){
            list.add(new Point3D((float)((LineTo)elem).getX(),(float)((LineTo)elem).getY(),0f));
        } else if(elem instanceof CubicCurveTo){
            Point3D ini = (list.size()>0?list.get(list.size()-1):p0);
            IntStream.range(1, 11).forEach(i->list.add(evalBezier((CubicCurveTo)elem, ini, ((double)i)/10d)));
        } else if(elem instanceof ClosePath){
            paths.add(list);
        } 
    }
    
    private Point3D evalBezier(CubicCurveTo c, Point3D ini, double t){
        Point3D p=new Point3D((float)(Math.pow(1-t,3)*ini.x+
                3*t*Math.pow(1-t,2)*c.getControlX1()+
                3*(1-t)*t*t*c.getControlX2()+
                Math.pow(t, 3)*c.getX()),
                (float)(Math.pow(1-t,3)*ini.y+
                3*t*Math.pow(1-t, 2)*c.getControlY1()+
                3*(1-t)*t*t*c.getControlY2()+
                Math.pow(t, 3)*c.getY()),
                0f);
        return p;
    }

}
