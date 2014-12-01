/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.utils;

import org.fxyz.geometry.Point3D;

/**
 *
 * @author jpereda
 */
@FunctionalInterface
public interface DensityFunction {
    float eval(Point3D p);
}
