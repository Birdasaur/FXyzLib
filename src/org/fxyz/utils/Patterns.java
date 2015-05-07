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
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javax.imageio.ImageIO;

/**
 *
 * @author @jpereda
 */
public class Patterns {
    
    public enum CarbonPatterns {
        DARK_CARBON,
        LIGHT_CARBON,
        CARBON_KEVLAR;
    }
    
    private final int width;
    private final int height;
    
    private Image imgPattern;
    
    public Patterns(int width, int height){
        this.width=width;
        this.height=height;
    }
    
    public Image createPattern(boolean save) {
        return createPattern(CarbonPatterns.DARK_CARBON, save);
    }

    public Image createPattern(CarbonPatterns cp, boolean save) {
        ImagePattern pattern;
        switch (cp) {
            case DARK_CARBON:
                pattern = createCarbonPattern();
                break;
            case LIGHT_CARBON:
                pattern = createLightCarbonPattern();
                break;
            case CARBON_KEVLAR:
                pattern = createCarbonKevlarPattern();
                break;
            default:
                pattern = createCarbonPattern();
                break;
        }

        Rectangle rectangle = new Rectangle(width, height);
        if (pattern != null) {
            rectangle.setFill(pattern);            
        }
        rectangle.setStrokeWidth(0);
        imgPattern = rectangle.snapshot(new SnapshotParameters(), null);
        if (save) {
            saveImage();
        }
        return imgPattern;
    }
    
    private void saveImage(){
        try {
            // save
            ImageIO.write(SwingFXUtils.fromFXImage(imgPattern, null), "png", new File("pattern_"+width+"x"+height+".png"));
        } catch (IOException ex) { 
            System.out.println("Error saving image");
        }
    }
    
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Image getPatternImage() {
        return imgPattern;
    }
    
    public static final ImagePattern createCarbonPattern() {
        final double WIDTH = 12;
        final double HEIGHT = 12;
        final Canvas CANVAS = new Canvas(WIDTH, HEIGHT);
        final GraphicsContext CTX = CANVAS.getGraphicsContext2D();

        double offsetY = 0;

        CTX.beginPath();
        CTX.rect(0, 0, WIDTH * 0.5, HEIGHT * 0.5);
        CTX.closePath();

        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.5 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(35, 35, 35)),
                new Stop(1, Color.rgb(23, 23, 23))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.083333, 0, WIDTH * 0.333333, HEIGHT * 0.416666);
        CTX.closePath();
        offsetY = 0;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.416666 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(38, 38, 38)),
                new Stop(1, Color.rgb(30, 30, 30))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.5, HEIGHT * 0.5, WIDTH * 0.5, HEIGHT * 0.5);
        CTX.closePath();
        offsetY = 0.5;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.5 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(35, 35, 35)),
                new Stop(1, Color.rgb(23, 23, 23))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.583333, HEIGHT * 0.5, WIDTH * 0.333333, HEIGHT * 0.416666);
        CTX.closePath();
        offsetY = 0.5;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.416666 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(38, 38, 38)),
                new Stop(1, Color.rgb(30, 30, 30))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.5, 0, WIDTH * 0.5, HEIGHT * 0.5);
        CTX.closePath();
        offsetY = 0;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.5 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(48, 48, 48)),
                new Stop(1, Color.rgb(40, 40, 40))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.583333, HEIGHT * 0.083333, WIDTH * 0.333333, HEIGHT * 0.416666);
        CTX.closePath();
        offsetY = 0.083333;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.416666 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(53, 53, 53)),
                new Stop(1, Color.rgb(45, 45, 45))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(0, HEIGHT * 0.5, WIDTH * 0.5, HEIGHT * 0.5);
        CTX.closePath();
        offsetY = 0.5;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.5 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(48, 48, 48)),
                new Stop(1, Color.rgb(40, 40, 40))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.083333, HEIGHT * 0.583333, WIDTH * 0.333333, HEIGHT * 0.416666);
        CTX.closePath();
        offsetY = 0.583333;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.416666 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(53, 53, 53)),
                new Stop(1, Color.rgb(45, 45, 45))));
        CTX.fill();

        final Image PATTERN_IMAGE = CANVAS.snapshot(new SnapshotParameters(), null);
        final ImagePattern PATTERN = new ImagePattern(PATTERN_IMAGE, 0, 0, WIDTH, HEIGHT, false);

        return PATTERN;
    }

    public static final ImagePattern createLightCarbonPattern() {
        final double WIDTH = 12;
        final double HEIGHT = 12;
        final Canvas CANVAS = new Canvas(WIDTH, HEIGHT);
        final GraphicsContext CTX = CANVAS.getGraphicsContext2D();

        double offsetY = 0;

        CTX.beginPath();
        CTX.rect(0, 0, WIDTH * 0.5, HEIGHT * 0.5);
        CTX.closePath();

        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.5 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(108, 108, 108)),
                new Stop(1, Color.rgb(100, 100, 100))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.083333, 0, WIDTH * 0.333333, HEIGHT * 0.416666);
        CTX.closePath();
        offsetY = 0;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.416666 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(142, 142, 142)),
                new Stop(1, Color.rgb(130, 130, 130))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.5, HEIGHT * 0.5, WIDTH * 0.5, HEIGHT * 0.5);
        CTX.closePath();
        offsetY = 0.5;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.5 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(108, 108, 108)),
                new Stop(1, Color.rgb(100, 100, 100))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.583333, HEIGHT * 0.5, WIDTH * 0.333333, HEIGHT * 0.416666);
        CTX.closePath();
        offsetY = 0.5;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.416666 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(142, 142, 142)),
                new Stop(1, Color.rgb(130, 130, 130))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.5, 0, WIDTH * 0.5, HEIGHT * 0.5);
        CTX.closePath();
        offsetY = 0;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.5 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(152, 152, 152)),
                new Stop(1, Color.rgb(146, 146, 146))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.583333, HEIGHT * 0.083333, WIDTH * 0.333333, HEIGHT * 0.416666);
        CTX.closePath();
        offsetY = 0.083333;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.416666 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(160, 160, 160)),
                new Stop(1, Color.rgb(152, 152, 152))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(0, HEIGHT * 0.5, WIDTH * 0.5, HEIGHT * 0.5);
        CTX.closePath();
        offsetY = 0.5;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.5 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(152, 152, 152)),
                new Stop(1, Color.rgb(146, 146, 146))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.083333, HEIGHT * 0.583333, WIDTH * 0.333333, HEIGHT * 0.416666);
        CTX.closePath();
        offsetY = 0.583333;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.416666 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(160, 160, 160)),
                new Stop(1, Color.rgb(152, 152, 152))));
        CTX.fill();

        final Image PATTERN_IMAGE = CANVAS.snapshot(new SnapshotParameters(), null);
        final ImagePattern PATTERN = new ImagePattern(PATTERN_IMAGE, 0, 0, WIDTH, HEIGHT, false);

        return PATTERN;
    }

    public static final ImagePattern createCarbonKevlarPattern() {
        final double WIDTH = 12;
        final double HEIGHT = 12;
        final Canvas CANVAS = new Canvas(WIDTH, HEIGHT);
        final GraphicsContext CTX = CANVAS.getGraphicsContext2D();

        double offsetY = 0;
        /// 1= border=yellow=dark========================================================
        CTX.beginPath();
        CTX.rect(0, 0, WIDTH * 0.5, HEIGHT * 0.5);
        CTX.closePath();

        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.5 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(105, 105, 0)),
                new Stop(1, Color.rgb(98, 98, 0))));
        CTX.fill();
        //  2=body=yellow==============================
        CTX.beginPath();
        CTX.rect(WIDTH * 0.083333, 0, WIDTH * 0.333333, HEIGHT * 0.416666);
        CTX.closePath();
        offsetY = 0;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.416666 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(138, 138, 0)),
                new Stop(1, Color.rgb(130, 130, 0))));
        CTX.fill();
        //  3=border=yellow=dark=============================
        CTX.beginPath();
        CTX.rect(WIDTH * 0.5, HEIGHT * 0.5, WIDTH * 0.5, HEIGHT * 0.5);
        CTX.closePath();
        offsetY = 0.5;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.5 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(105, 105, 0)),
                new Stop(1, Color.rgb(98, 98, 0))));
        CTX.fill();
        //  4=body=yellow============================================================
        CTX.beginPath();
        CTX.rect(WIDTH * 0.583333, HEIGHT * 0.5, WIDTH * 0.333333, HEIGHT * 0.416666);
        CTX.closePath();
        offsetY = 0.5;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.416666 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(138, 138, 0)),
                new Stop(1, Color.rgb(130, 130, 0))));
        CTX.fill();
        //  5= border=gray=dark============================
        CTX.beginPath();
        CTX.rect(WIDTH * 0.5, 0, WIDTH * 0.5, HEIGHT * 0.5);
        CTX.closePath();
        offsetY = 0;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.5 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(48, 48, 48)),
                new Stop(1, Color.rgb(30, 30, 30))));
        CTX.fill();
        //  6=body=gray=============================
        CTX.beginPath();
        CTX.rect(WIDTH * 0.583333, HEIGHT * 0.083333, WIDTH * 0.333333, HEIGHT * 0.416666);
        CTX.closePath();
        offsetY = 0.083333;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.416666 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(53, 53, 53)),
                new Stop(1, Color.rgb(45, 45, 45))));
        CTX.fill();
        //  7= border=gray=dark=============================
        CTX.beginPath();
        CTX.rect(0, HEIGHT * 0.5, WIDTH * 0.5, HEIGHT * 0.5);
        CTX.closePath();
        offsetY = 0.5;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.5 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(48, 48, 48)),
                new Stop(1, Color.rgb(40, 40, 40))));
        CTX.fill();
        //  8= body=gray=light==============================
        CTX.beginPath();
        CTX.rect(WIDTH * 0.083333, HEIGHT * 0.583333, WIDTH * 0.333333, HEIGHT * 0.416666);
        CTX.closePath();
        offsetY = 0.583333;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.416666 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(53, 53, 53)),
                new Stop(1, Color.rgb(45, 45, 45))));
        CTX.fill();

        final Image PATTERN_IMAGE = CANVAS.snapshot(new SnapshotParameters(), null);
        final ImagePattern PATTERN = new ImagePattern(PATTERN_IMAGE, 0, 0, WIDTH, HEIGHT, false);

        return PATTERN;
    }
    
}
