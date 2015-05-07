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
package org.fxyz.utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javax.imageio.ImageIO;

/**
 *
 * @author jpereda
 */
public class Palette {
    
    public enum ColorPalette {
        HSB,GREEN
    }
    
    private final List<Color> GREEN_COLORS = Arrays.asList(Color.rgb(0,0,0,1),Color.rgb(30,49,29,1),
            Color.rgb(35,80,33,1),Color.rgb(56,122,54,1),Color.rgb(45,187,40,1),Color.rgb(8,231,0,1));
    
    private final int numColors;
    private final ColorPalette colorPalette;
    private int width;
    private int height;
    private Image imgPalette;
    
    private final static ColorPalette DEFAULT_COLOR_PALETTE = ColorPalette.HSB;
    private final static int DEFAULT_NUMCOLORS = 10000; // 100x100 palette image
    
    public Palette(){
        this(DEFAULT_NUMCOLORS, DEFAULT_COLOR_PALETTE);
    }
    
    public Palette(int numColors){
        this(numColors, DEFAULT_COLOR_PALETTE);
    }
    
    public Palette(int numColors, ColorPalette colorPalette){
        this.numColors=numColors;
        this.colorPalette = colorPalette;
    }
    
    public Image createPalette(boolean save){
        if(numColors<1){
            return null;
        }
        // try to create a square image
        width=(int)Math.sqrt(numColors);
        height=numColors/width;
        
        imgPalette = new WritableImage(width, height);
        PixelWriter   pw  = ((WritableImage)imgPalette).getPixelWriter();
        AtomicInteger count = new AtomicInteger();
        IntStream.range(0, height).boxed()
                .forEach(y->IntStream.range(0, width).boxed()
                        .forEach(x->pw.setColor(x, y, getColor(count.getAndIncrement()))));
        if(save){
            saveImage();
        }
        return imgPalette;
    }
    
    public DoubleStream getTextureLocation(int iPoint){
        if(width==0 || height==0){
            return DoubleStream.of(0f,0f);
        }
        int y = iPoint/width; 
        int x = iPoint-width*y;
        // add 0.5 to interpolate colors from the middle of the pixel
        return DoubleStream.of((((float)x+0.5f)/((float)width)),(((float)y+0.5f)/((float)height)));
    }
    
    private void saveImage(){
        try {
            // save
            ImageIO.write(SwingFXUtils.fromFXImage(imgPalette, null), "png", new File("palette_"+numColors+".png"));
        } catch (IOException ex) { 
            System.out.println("Error saving image");
        }
    }
    
    /*
        int iColor [0-numColors]
    */
    private Color getColor(int iColor){
        double fact=(double) iColor / (double) numColors;
        switch(colorPalette){
            case HSB:
                // There are 6*255=1530 distinct pure colors, 255 colors every 60º, with 100% saturation and value
                return Color.hsb(360d*fact, 1d, 1d);
            case GREEN:
                return GREEN_COLORS.get((int)(fact*GREEN_COLORS.size()));
        } 
        return Color.rgb((iColor >> 16) & 0xFF, (iColor >> 8) & 0xFF, iColor & 0xFF);
    }
    
    public int getNumColors() {
        return numColors;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Image getPaletteImage() {
        return imgPalette;
    }
    
    public ColorPalette getColorPalette(){
        return colorPalette;
    }
    
    public static void main(String[] args){
        Palette p=new Palette(4,ColorPalette.HSB);
        p.createPalette(true);
        System.out.println("p: "+p.getWidth()+" "+p.getHeight());
        IntStream.range(0,10).boxed().forEach(i->p.getTextureLocation(i).forEach(d->System.out.print(" "+d)));
    }
}
