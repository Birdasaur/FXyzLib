/*
 * Copyright (C) 2013-2014 F(X)yz, 
 * Sean Phillips, Jason Pollastrini
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
package org.fxyz.shapes.complex.cloth;

import org.fxyz.geometry.Point3D;
import org.fxyz.utils.Constraint;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class PointLink implements Constraint{

        private final double distance, stiffness;
        private final WeightedPoint p1, p2;

        public PointLink(WeightedPoint p1, WeightedPoint p2, double distance, double stiffness) {
            this.p1 = p1;
            this.p2 = p2;
            this.distance = distance;
            this.stiffness = stiffness;
        }

        public void solve() {
            // calculate the distance between the two PointMasss
            Point3D diff = new Point3D(
                    p1.getPosition().x - p2.getPosition().x,
                    p1.getPosition().y - p2.getPosition().y,
                    p1.getPosition().z - p2.getPosition().z
            );

            double d = diff.magnitude();

            double difference = (distance - d) / d;

            double im1 = 1 / p1.getMass();
            double im2 = 1 / p2.getMass();
            double scalarP1 = (im1 / (im1 + im2)) * stiffness;
            double scalarP2 = stiffness - scalarP1;

            synchronized (this) {
                p1.position.x = (float) (p1.getPosition().x + diff.x * scalarP1 * difference);
                p1.position.y = (float) (p1.getPosition().y + diff.y * scalarP1 * difference);
                p1.position.z = (float) (p1.getPosition().z + diff.z * scalarP1 * difference);

                p2.position.x = (float) (p2.getPosition().x - diff.x * scalarP2 * difference);
                p2.position.y = (float) (p2.getPosition().y - diff.y * scalarP2 * difference);
                p2.position.z = (float) (p2.getPosition().z - diff.z * scalarP2 * difference);
            }

        }

        public WeightedPoint getAnchorPoint() {
            return p1;
        }

        public WeightedPoint getAttachedPoint() {
            return p2;
        }

        @Override
        public String toString() {
            return "PointLink{" + "distance=" + distance + ", stiffness=" + stiffness + ", p1=" + p1 + ", p2=" + p2 + '}';
        }

    }
