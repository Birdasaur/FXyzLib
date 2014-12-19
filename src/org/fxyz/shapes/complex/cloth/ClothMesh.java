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


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Affine;
import javafx.util.Duration;
import org.fxyz.geometry.Point3D;
import org.fxyz.utils.FloatCollector;

/**
 *  Cloth Mesh is a Cloth Simulator, 
 *  Care should be taken to avoid poor performance.
 *  Please view the Read Me file.
 * 
 * @author Jason Pollastrini aka jdub1581
 */
public class ClothMesh extends MeshView {

    private static final Logger log = Logger.getLogger(ClothMesh.class.getName());

    private final ClothTimer timer;
    private final TriangleMesh mesh = new TriangleMesh();
    private final List<WeightedPoint> pointList = new ArrayList<>();
    private final Affine affine = new Affine();
    
    private BiFunction<Integer, TriangleMesh, int[]> faceValues = (index, m) -> {
        if (index > ((m.getFaces().size() - 1) - m.getFaceElementSize())) {
            return null;
        }
        if (index > 0) {
            index = (index * 6);
            return m.getFaces().toArray(index, null, 6);
        }
        return m.getFaces().toArray(index, null, index + 6);
    };

    /**
     *
     * @param divsX number of points along X axis
     * @param divsY number of points along Y axis
     * @param width desired Width of Mesh
     * @param height desired Height of Mesh
     * @param stiffness constraint elasticity / stiffness
     */
    public ClothMesh(int divsX, int divsY, double width, double height, double stiffness) {
        this.timer = new ClothTimer(ClothMesh.this);

        this.setDivisionsX(divsX);
        this.setDivisionsY(divsY);
        this.setClothWidth(width);
        this.setClothHeight(height);
        this.setStiffness(stiffness);

        this.getTransforms().add(affine);

        this.buildMesh(getDivisionsX(), getDivisionsY(), getClothWidth(), getClothHeight(), getStiffness());

        this.setOnMousePressed((MouseEvent me) -> {
            if (me.isPrimaryButtonDown()) {
                PickResult pr = me.getPickResult();
                if (pr.getIntersectedFace() != -1) {
                    int[] vals = faceValues.apply(pr.getIntersectedFace(), mesh);
                    if (me.isControlDown()) {
                        pointList.get(vals[0]).setOldPosition(pointList.get(vals[0]).getOldPosition().add(0, 0, 20));
                        pointList.get(vals[2]).setOldPosition(pointList.get(vals[2]).getOldPosition().add(0, 0, 20));
                        pointList.get(vals[4]).setOldPosition(pointList.get(vals[4]).getOldPosition().add(0, 0, 20));
                    } else {
                        pointList.get(vals[0]).setOldPosition(pointList.get(vals[0]).getOldPosition().add(0, 0, -20));
                        pointList.get(vals[2]).setOldPosition(pointList.get(vals[2]).getOldPosition().add(0, 0, -20));
                        pointList.get(vals[4]).setOldPosition(pointList.get(vals[4]).getOldPosition().add(0, 0, -20));
                    }
                }
            }
        });
    }


    /*==========================================================================
     Updating Methods
     */
    /**
     *
     */
    private void updatePoints() {
        float[] points = pointList.stream()
                .flatMapToDouble(wp -> {
                    return wp.getPosition().getCoordinates();
                })
                .collect(() -> new FloatCollector(pointList.size() * 3), FloatCollector::add, FloatCollector::join)
                .toArray();

        mesh.getPoints().setAll(points, 0, points.length);
    }

    /**
     *
     */
    public void updateUI() {
        updatePoints();
    }
    /*==========================================================================
     Mesh Creation
     *///=======================================================================

    /**
     * @param divsX number of points along X axis
     * @param divsY number of points along Y axis
     * @param width desired Width of Mesh
     * @param height desired Height of Mesh
     * @param stiffness constraint elasticity / stiffness
     */
    private void buildMesh(int divsX, int divsY, double width, double height, double stiffness) {
        float minX = (float) (-width / 2f),
                maxX = (float) (width / 2f),
                minY = (float) (-height / 2f),
                maxY = (float) (height / 2f);

        int sDivX = (divsX - 1),
                sDivY = (divsY - 1);

        //build Points and TexCoords        
        for (int Y = 0; Y <= sDivY; Y++) {

            float currY = (float) Y / sDivY;
            float fy = (1 - currY) * minY + currY * maxY;

            for (int X = 0; X <= sDivX; X++) {

                float currX = (float) X / sDivX;
                float fx = (1 - currX) * minX + currX * maxX;

                //create point: parent, mass, x, y, z
                WeightedPoint p = new WeightedPoint(this, 1.5, fx, fy, Math.random());
                if(Y <= 10)p.setMass(10.0f);
                //Pin Points in place
                if (Y == 0 && X % 25 == 0 || (X == 0 && Y == 0) || (X == sDivX && Y == 0)) {
                    p.setAnchored(true);
                    p.setForceAffected(false);
                } else {
                    p.setForceAffected(true);
                }
                // Create Links    
                if (X != 0) {
                    p.attatchTo((pointList.get(pointList.size() - 1)), (width / divsX), stiffness);
                    //log.log(Level.INFO, "\nLINK-INFO\nOther Index: {0}, This Index: {1}\nLink Distance: {2}\nStiffness: {3}\n", new Object[]{(pointList.size() - 2), pointList.indexOf(p),(width / divsX), stiffness});
                }
                if (Y != 0) {
                    p.attatchTo((pointList.get((Y - 1) * (divsX) + X)), (height / divsY), stiffness);
                    //log.log(Level.INFO, "\nLINK-INFO\nOther Index: {0}, This Index: {1}\nLink Distance: {2}\nStiffness: {3}\n", new Object[]{((Y - 1) * (divsX) + X), pointList.indexOf(p),(height / divsY), stiffness});
                }

                //add to pointList
                pointList.add(p);
                // add Point data into Mesh
                mesh.getPoints().addAll(p.position.x, p.position.y, p.position.z);
                // add texCoords
                mesh.getTexCoords().addAll(currX, currY);
            }
        }
        // build faces
        for (int Y = 0; Y < sDivY; Y++) {
            for (int X = 0; X < sDivX; X++) {
                int p00 = Y * (sDivX + 1) + X;
                int p01 = p00 + 1;
                int p10 = p00 + (sDivX + 1);
                int p11 = p10 + 1;
                int tc00 = Y * (sDivX + 1) + X;
                int tc01 = tc00 + 1;
                int tc10 = tc00 + (sDivX + 1);
                int tc11 = tc10 + 1;

                mesh.getFaces().addAll(p00, tc00, p10, tc10, p11, tc11);
                mesh.getFaces().addAll(p11, tc11, p01, tc01, p00, tc00);
            }
        }

        setMesh(mesh);
    }

    /*==========================================================================
     Properties
     *///=======================================================================
    private final DoubleProperty clothWidth = new SimpleDoubleProperty() {
        @Override
        protected void invalidated() {
            
        }
    };

    protected final double getClothWidth() {
        return clothWidth.get();
    }

    protected final void setClothWidth(double value) {
        clothWidth.set(value);
    }

    protected DoubleProperty clothWidthProperty() {
        return clothWidth;
    }
    /*
    
     */
    private final DoubleProperty clothHeight = new SimpleDoubleProperty() {
        @Override
        protected void invalidated() {
            
        }
    };

    protected final double getClothHeight() {
        return clothHeight.get();
    }

    protected final void setClothHeight(double value) {
        clothHeight.set(value);
    }

    protected DoubleProperty clothHeightProperty() {
        return clothHeight;
    }
    /*
    
     */
    private final IntegerProperty divisionsX = new SimpleIntegerProperty() {
        @Override
        protected void invalidated() {
            
        }
    };

    protected final int getDivisionsX() {
        return divisionsX.get();
    }

    protected final void setDivisionsX(int value) {
        divisionsX.set(value);
    }

    protected final IntegerProperty divisionsXProperty() {
        return divisionsX;
    }
    /*
    
     */
    private final IntegerProperty divisionsY = new SimpleIntegerProperty() {
        @Override
        protected void invalidated() {
            
        }
    };

    protected final int getDivisionsY() {
        return divisionsY.get();
    }

    protected final void setDivisionsY(int value) {
        divisionsY.set(value);
    }

    protected final IntegerProperty divisionsYProperty() {
        return divisionsY;
    }
    /*
    
     */
    private final DoubleProperty stiffness = new SimpleDoubleProperty() {
        @Override
        protected void invalidated() {
            
        }
    };

    protected final double getStiffness() {
        return stiffness.get();
    }

    protected final void setStiffness(double value) {
        stiffness.set(value);
    }

    protected final DoubleProperty stiffnessProperty() {
        return stiffness;
    }

    public List<WeightedPoint> getPointList() {
        return pointList;
    }


    /*==========================================================================
     Delagate "like" methods
     */
    public final void startSimulation() {
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    public final void pauseSimulation() {
        timer.pause();
    }

    public final void stopSimulation() {
        timer.cancel();
    }
    //End ClothMesh=============================================================

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * ClothTimer is a simple timer class for updating points
     *
     * @author Jason Pollastrini aka jdub1581
     */
    private class ClothTimer extends ScheduledService<Void> {

        private final long ONE_NANO = 1000000000L;
        private final double ONE_NANO_INV = 1f / 1000000000L;

        private long startTime, previousTime;
        private double deltaTime;
        private final double fixedDeltaTime = 0.16;
        private int leftOverDeltaTime, timeStepAmt;
        private final NanoThreadFactory tf = new NanoThreadFactory();
        private final ClothMesh mesh;
        private final List<WeightedPoint> points;
        int iterations = 4;
        int accuracyLevel = 4;
        private boolean paused;

        public ClothTimer(ClothMesh mesh) {
            super();
            this.mesh = mesh;
            this.points = pointList;
            this.setPeriod(Duration.millis(16));
            this.setExecutor(Executors.newSingleThreadExecutor(tf));
        }

        /**
         * @return elapsed time as a double
         */
        public double getTimeAsSeconds() {
            return getTime() * ONE_NANO_INV;
        }

        /**
         *
         * @return elapsed time as a long
         */
        public long getTime() {
            return System.nanoTime() - startTime;
        }

        /**
         *
         * @return value of one nano second
         */
        private long getOneNano() {
            return ONE_NANO;
        }

        /**
         *
         * @return deltaTime
         */
        public double getDeltaTime() {
            return deltaTime;
        }

        /**
         *
         * @return updates the timers time values
         */
        private void updateTimer() {
            deltaTime = (getTime() - previousTime) * (10.0f / ONE_NANO);
            previousTime = getTime();
            timeStepAmt = (int) ((deltaTime + leftOverDeltaTime) / fixedDeltaTime);
            timeStepAmt = Math.min(timeStepAmt, 5);
            leftOverDeltaTime = (int) (deltaTime - (timeStepAmt * fixedDeltaTime));
        }

        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    updateTimer();

                    /*
                     Standard updates
                     */
                    points.parallelStream().forEach(p -> {
                        p.applyForce(new Point3D(0, 4.7f, 0));
                    });
                    for (int i = 0; i < accuracyLevel; i++) {
                        points.parallelStream().forEach(WeightedPoint::solveConstraints);
                    }
                    points.parallelStream().forEach(p -> {
                        p.applyForce(new Point3D(0, 4.7f, 0));
                        p.updatePhysics(deltaTime, 1);
                        p.clearForces();
                    });
                    
                    return null;
                }
            };
        }

        @Override
        protected void failed() {
            getException().printStackTrace(System.err);

        }

        @Override
        protected void succeeded() {
            super.succeeded();
            mesh.updateUI();
        }

        @Override
        protected void cancelled() {
            super.cancelled(); 
            reset();
        }

        @Override
        public void start() {
            if (isRunning()) {
                return;
            }

            super.start();

            if (startTime <= 0) {
                startTime = System.nanoTime();
            }

        }

        protected void pause() {
            paused = true;
            if (isRunning()) {
                if (cancel()) {
                    cancelled();
                }
            }
        }

        @Override
        public void reset() {
            super.reset();
            if (!paused) {
                startTime = System.nanoTime();
                previousTime = getTime();
            }
        }

        @Override
        public String toString() {
            return "ClothTimer{" + "startTime=" + startTime + ", previousTime=" + previousTime + ", deltaTime=" + deltaTime + ", fixedDeltaTime=" + fixedDeltaTime + ", leftOverDeltaTime=" + leftOverDeltaTime + ", timeStepAmt=" + timeStepAmt + '}';
        }

        /*==========================================================================
    
         */
        private class NanoThreadFactory implements ThreadFactory {

            public NanoThreadFactory() {
            }

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "ClothTimerThread");
                t.setDaemon(true);
                return t;
            }

        }
    }//End ClothTimer===========================================================

}

