/*
 * This file is part of CGVis.
 *
 * Copyright 2008 Ilya Boyandin, Erik Koerner
 * 
 * CGVis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CGVis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with CGVis.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.fhjoanneum.cgvis.util;

/**
 * @author Ilya Boyandin
 */
public class DoubleAxisMarks {

    private double minDist;
    private double maxDist;

    private double start;
    private double step;

    // private int numSteps;

    /**
     * @param minDist
     *            Min distance (in pixels) between two adjacent marks
     * @param maxDist
     *            Max distance between two adjacent marks
     */
    public DoubleAxisMarks(double minDist, double maxDist) {
        this.minDist = minDist;
        this.maxDist = maxDist;
    }

    public double getMaxDist() {
        return maxDist;
    }

    public void setMaxDist(double maxDist) {
        this.maxDist = maxDist;
    }

    public double getMinDist() {
        return minDist;
    }

    public void setMinDist(double minDist) {
        this.minDist = minDist;
    }

    public double getStart() {
        return start;
    }

    public double getStep() {
        return step;
    }

    /**
     * @param minV
     *            Value to start from
     * @param maxV
     *            Value to end with
     * @param width
     *            Total width (in pixels) of the area where the marks are to be
     *            placed
     */
    public void calc(double minV, double maxV, double width) {
        if (width <= 0) {
            return;
        }
        if (minV == maxV /* Math.abs(minV - maxV) < EPS */) { // equal
            start = minV;
            step = 0;
            return;
        } else if (maxV < minV) {
            // Wrong parameters order, so just swap them
            final double t = minV;
            minV = maxV;
            maxV = t;
        }

        // Step
        final double len = (maxV - minV);
        int stepsEstim = (int) Math.round(width / ((maxDist + minDist) / 2));
        if (stepsEstim <= 0) {
            stepsEstim = (int) Math.round(width / minDist);
            if (stepsEstim <= 0) {
                stepsEstim = 1;
            }
        }

        final double d = len / stepsEstim;

        final double ord = ordAlpha(d);
        final double ord10 = ordAlpha(d * 10);
        final double ord01 = ordAlpha(d / 10);

        final double ord10Dist = width / (len / ord10);
        final double ordDist = width / (len / ord);
        final double ord01Dist = width / (len / ord01);

        // Look first if one of the ord's suits straight away
        double _step = 0;
        if (ord10Dist >= minDist && ord10Dist <= maxDist) {
            _step = ord10;
        } else if (ordDist >= minDist && ordDist <= maxDist) {
            _step = ord;
        } else if (ord01Dist >= minDist && ord01Dist <= maxDist) {
            _step = ord01;
        }

        if (_step == 0) {
            // Try a multiplied ord
            if (ord10Dist <= minDist) {
                _step = ord10;
                int i = 2;
                while (width / (len / _step) < minDist) {
                    _step = ord10 * i++;
                }
            } else if (ordDist <= minDist) {
                _step = ord;
                int i = 2;
                while (width / (len / _step) < minDist) {
                    _step = ord * i++;
                }
            } else if (ord01Dist <= minDist) {
                _step = ord01;
                int i = 2;
                while (width / (len / _step) < minDist) {
                    _step = ord01 * i++;
                }
            }
        }

        if (_step == 0) { // It seems that all of the ords are too wide,
            // so fallback to the smallest
            _step = ord01;
        }

        // Start
        start = _step * Math.ceil(minV / _step); // start ord must be equal
                                                    // to the
        // ord of _step or zero

        if (start == 0 && (1.0 / start) < 0) {
            start = 0.0; // to avoid -0.0
        }

        if (stepsEstim > 1) {
            step = _step;
        } else {
            step = 0;
        }

        // numSteps = (step != 0 ? (int)Math.floor((maxV - start) / step) : 1);
    }

    public static double ordAlpha(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        if (Double.isInfinite(x)) {
            return x > 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
        }
        int sgn;
        if (x == 0)
            return 0;
        if (x < 0) {
            x = -x;
            sgn = -1;
        } else {
            sgn = 1;
        }
        double ord = 1;
        if (x >= 1) {
            while (x >= 10) {
                x /= 10;
                ord *= 10;
            }
        } else {
            while (x < 1) {
                x *= 10;
                ord /= 10;
            }
        }
        return sgn * ord;
    }
}
