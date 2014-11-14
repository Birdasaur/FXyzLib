package org.fxyz.utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
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
    
    private final int numColors;
    private int width;
    private int height;
    private Image imgPalette;
    
    public Palette(int numColors){
        this.numColors=numColors;
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
    
    public float[] getTextureLocation(int iPoint){
        if(width==0 || height==0){
            return new float[]{0,0};
        }
        int y = iPoint/width; 
        int x = iPoint-width*y;
        return new float[]{(((float)x)/((float)width)),(((float)y)/((float)height))};
    }
    
    private void saveImage(){
        try {
            // save
            ImageIO.write(SwingFXUtils.fromFXImage(imgPalette, null), "jpg", new File("palette_"+numColors+".jpg"));
        } catch (IOException ex) { 
            System.out.println("Error saving image");
        }
    }
    
    private Color getColor(int iColor){
        
        //TODO: add already defined nice palettes
        
        java.awt.Color c = java.awt.Color.getHSBColor((float) iColor / (float) numColors, 1.0f, 1.0f);
        return Color.rgb(c.getRed(), c.getGreen(), c.getBlue());
        
//        return Color.rgb((iColor >> 16) & 0xFF, (iColor >> 8) & 0xFF, iColor & 0xFF);
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

    public Image getImgPalette() {
        return imgPalette;
    }
    
    
}
