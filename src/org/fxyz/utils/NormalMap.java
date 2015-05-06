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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 *	Class represent a Normal Map Image.
 *  When it comes to Scaling think of intensity as the Macro, 
 *  and intensity scale as the micro	
    
    Also if you recieve a lot of "Bright" pixeling viewing object from the side, 
    Apply a SMALL amount of blur to the Image PRIOR to creating this Image.
    It should help smooth things out.

 * @author Jason Pollastrini aka jdub1581
 */
public class NormalMap extends WritableImage {

    private final double DEFAULT_INTENSITY = 5.0, DEFAULT_INTENSITY_SCALE = 5.0;
    private final boolean DEFAULT_INVERTED = false; // new Random().nextBoolean();
    
    private PixelReader pReader;
    private final PixelWriter pWriter;
    private final Image srcImage;
    
    
    public NormalMap(Image src){
        super(src.getPixelReader(),0,0, (int)src.getWidth(), (int)src.getHeight());
        this.srcImage = src;
        this.pReader = getPixelReader();
        this.pWriter = getPixelWriter();
        this.buildNormalMap(DEFAULT_INTENSITY, DEFAULT_INTENSITY_SCALE, DEFAULT_INVERTED);
    }

    
    private void buildNormalMap(double scale, double scaleFactor, boolean invert) {

        pReader = srcImage.getPixelReader();
        final int w = (int) srcImage.getWidth();
        final int h = (int) srcImage.getHeight();

        WritableImage gray = new WritableImage(w, h);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                gray.getPixelWriter().setColor(x, y, pReader.getColor(x, y).grayscale());
            }
        }

        final byte[] heightPixels = new byte[w * h * 4];
        final byte[] normalPixels = new byte[w * h * 4];
        // get pixels
        pReader = gray.getPixelReader();
        pReader.getPixels(0, 0, w, h, PixelFormat.getByteBgraInstance(), heightPixels, 0, w * 4);

        if (invert) {
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    final int pixelIndex = (y * w * 4) + (x * 4);
                    heightPixels[pixelIndex + 0] = (byte) (255 - Byte.toUnsignedInt(heightPixels[pixelIndex]));
                    heightPixels[pixelIndex + 1] = (byte) (255 - Byte.toUnsignedInt(heightPixels[pixelIndex + 1]));
                    heightPixels[pixelIndex + 2] = (byte) (255 - Byte.toUnsignedInt(heightPixels[pixelIndex + 2]));
                    heightPixels[pixelIndex + 3] = (byte) (heightPixels[pixelIndex + 3]);
                }
            }
        }
        // generate normal map
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                final int yAbove = Math.max(0, y - 1);
                final int yBelow = Math.min(h - 1, y + 1);
                final int xLeft = Math.max(0, x - 1);
                final int xRight = Math.min(w - 1, x + 1);

                final int pixelIndex = (y * w * 4) + (x * 4);

                final int pixelAboveIndex = (yAbove * w * 4) + (x * 4);
                final int pixelBelowIndex = (yBelow * w * 4) + (x * 4);
                final int pixelLeftIndex = (y * w * 4) + (xLeft * 4);
                final int pixelRightIndex = (y * w * 4) + (xRight * 4);

                final int pixelAboveHeight = Byte.toUnsignedInt(heightPixels[pixelAboveIndex]);
                final int pixelBelowHeight = Byte.toUnsignedInt(heightPixels[pixelBelowIndex]);
                final int pixelLeftHeight = Byte.toUnsignedInt(heightPixels[pixelLeftIndex]);
                final int pixelRightHeight = Byte.toUnsignedInt(heightPixels[pixelRightIndex]);

                Point3D pixelAbove = new Point3D(x, yAbove, pixelAboveHeight);
                Point3D pixelBelow = new Point3D(x, yBelow, pixelBelowHeight);
                Point3D pixelLeft = new Point3D(xLeft, y, pixelLeftHeight);
                Point3D pixelRight = new Point3D(xRight, y, pixelRightHeight);

                Point3D H = pixelLeft.subtract(pixelRight);
                Point3D V = pixelAbove.subtract(pixelBelow);

                Point3D normal = H.crossProduct(V);
                normal = new Point3D(
                        (normal.getX() / w),
                        (normal.getY() / h),
                        (1 / normal.getZ()) / (Math.max(1.0, scale) / Math.min(10, scaleFactor))
                ).normalize();

                normalPixels[pixelIndex + 0] = (byte) (255 - (normal.getZ()));        //Blue              
                normalPixels[pixelIndex + 1] = (byte) (128 + (normal.getY() * 128.0));//Green                 
                normalPixels[pixelIndex + 2] = (byte) (128 + (normal.getX() * 128.0));//Red                 
                normalPixels[pixelIndex + 3] = (byte) (255);                          //alpha

            }
        }
        // create output image
        pWriter.setPixels(0, 0, w, h, PixelFormat.getByteBgraPreInstance(), normalPixels, 0, w * 4);
    }
    
    
    /*==========================================================================
        Properties
    */
    
    /**
     * 
     */
    private final DoubleProperty intensity = new SimpleDoubleProperty(this, "intensity" , DEFAULT_INTENSITY){

        @Override
        protected void invalidated() {
            buildNormalMap(get(), getIntensityScale(), isInvertNormals());
        }
        
    };
    /**
     * 
     * @return 
     */
    public double getIntensity() {
        return intensity.get();
    }
    /**
     * 
     * @param value
     */
    public void setIntensity(double value) {
        intensity.set(value);
    }
    /**
     * 
     * @return 
     */
    public DoubleProperty intensityProperty() {
        return intensity;
    }
    //=========================================
    /**
     * 
     */
    private final DoubleProperty intensityScale = new SimpleDoubleProperty(this, "intensityScale" , DEFAULT_INTENSITY_SCALE){

        @Override
        protected void invalidated() {
             buildNormalMap(getIntensity(), get(), isInvertNormals());
        }
        
    };
    /**
     * 
     * @return 
     */
    public double getIntensityScale() {
        return intensityScale.get();
    }
    /**
     * 
     * @param value
     */
    public void setIntensityScale(double value) {
        intensityScale.set(value);
    }
    /**
     * 
     * @return 
     */
    public DoubleProperty intensityScaleProperty() {
        return intensityScale;
    }
    //==========================================
    /**
     * 
     */
    private final BooleanProperty invertNormals = new SimpleBooleanProperty(this, "inverted" , DEFAULT_INVERTED){

        @Override
        protected void invalidated() {
             buildNormalMap(getIntensity(), getIntensityScale(), get());
        }
        
    };
    /**
     * 
     * @return 
     */
    public boolean isInvertNormals() {
        return invertNormals.get();
    }
    /**
     * 
     * @param value
     */
    public void setInvertNormals(boolean value) {
        invertNormals.set(value);
    }
    /**
     * 
     * @return 
     */
    public BooleanProperty invertNormalsProperty() {
        return invertNormals;
    }
    
    
    
}