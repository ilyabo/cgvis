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
package at.fhjoanneum.cgvis.data;

/**
 * @author Ilya Boyandin
 */
public class Histogram {

    private final int[] quantities;
    private final double max;
    private final double min;

    private Histogram(int[] quantities, double min, double max) {
        this.quantities = quantities;
        this.min = min;
        this.max = max;
    }

    public int getNumOfIntervals() {
        return quantities.length;
    }

    public int getQuantity(int i) {
        return quantities[i];
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public double getIntervalLength() {
        return (max - min) / getNumOfIntervals();
    }

    public static Histogram create(IPointSet ps, int attrIndex,
            int numOfIntervals) {
        if (numOfIntervals <= 0) {
            throw new IllegalArgumentException();
        }

        final int size = ps.getSize();
        final int[] quantities;
        final double ilen;

        double max = Double.NaN;
        double min = Double.NaN;

        if (size > 0) {
            // Find min and max
            max = Double.NaN;
            min = Double.NaN;

            for (int i = 0; i < size; i++) {
                final double v = ps.getValue(i, attrIndex);
                if (Double.isNaN(max)  ||   v > max)
                    max = v;
                if (Double.isNaN(min)  ||   v < min)
                    min = v;
            }

            final double len = (max - min);
            if (len > 0) {
                ilen = len / numOfIntervals;

                quantities = new int[numOfIntervals];

                for (int i = 0; i < size; i++) {
                    final double val = ps.getValue(i, attrIndex);
                    final int interval = (int) Math.floor((val - min) / ilen);
                    quantities[interval]++;
                }
            } else {
                quantities = new int[] { size };
                ilen = 0;
            }
        } else {
            quantities = new int[0];
            ilen = 0;
        }

        return new Histogram(quantities, min, max);
    }

}
