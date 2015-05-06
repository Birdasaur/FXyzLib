/*
 * Copyright (c) 2013-2015, F(X)yz All rights reserved.
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

import javafx.scene.paint.Color;
import org.fxyz.geometry.Point3D;
import org.fxyz.utils.DensityFunction;

/**
 *
 * @author usuario
 */
public interface TextureMode {
    
    public void setTextureModeNone();
    public void setTextureModeNone(Color color);
    public void setTextureModeNone(Color color, String image);
    public void setTextureModeImage(String image);
    public void setTextureModePattern(double scale);
    public void setTextureModeVertices3D(int colors, DensityFunction<Point3D> dens);
    public void setTextureModeVertices3D(int colors, DensityFunction<Point3D> dens, double min, double max);
    public void setTextureModeVertices1D(int colors, DensityFunction<Double> function);
    public void setTextureModeVertices1D(int colors, DensityFunction<Double> function, double min, double max);
    public void setTextureModeFaces(int colors);
}
