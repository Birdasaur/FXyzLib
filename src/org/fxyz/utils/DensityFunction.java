/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.utils;

/**
 *
 * @author jpereda
 * @param <T> Point3D, Double
 */
@FunctionalInterface
public interface DensityFunction<T> {
    Double eval(T p);
}
