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
public class PointSetStats {

    public static class MinMax {
        public final double min;
        public final double max;

        public MinMax(double minValue, double maxValue) {
            this.min = minValue;
            this.max = maxValue;
        }
    }

    public static MinMax getStats(IPointSet ps) {
        final int size = ps.getSize();
        final int dimension = ps.getDimension();

        double max = Double.NaN;
        double min = Double.NaN;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < dimension; j++) {
                final double v = ps.getValue(i, j);
                if (Double.isNaN(max)  ||   v > max)
                    max = v;
                if (Double.isNaN(min)  ||   v < min)
                    min = v;
            }
        }
        return new MinMax(min, max);
    }

    public static MinMax getStats(IPointSet ps, int attrIndex) {
        final int size = ps.getSize();

        double max = Double.NaN;
        double min = Double.NaN;

        for (int i = 0; i < size; i++) {
            final double v = ps.getValue(i, attrIndex);
            if (Double.isNaN(max)  ||  v > max)
                max = v;
            if (Double.isNaN(min)  ||  v < min)
                min = v;
        }
        return new MinMax(min, max);
    }

    /*
     * public static double attrCorrelation(IPointSet ps, int attrI, int attrJ) {
     * final int N = ps.getDimension(); double sum_sq_x = 0; double sum_sq_y =
     * 0; double sum_coproduct = 0; double mean_x = ps.getValue(0, attrI);
     * double mean_y = ps.getValue(0, attrJ); for (int i = 1; i < N; i++) {
     * final double sweep = (i - 1.0) / i; final double delta_x = ps.getValue(i,
     * attrI) - mean_x; final double delta_y = ps.getValue(i, attrJ) - mean_y;
     * sum_sq_x += delta_x * delta_x * sweep; sum_sq_y += delta_y * delta_y *
     * sweep; sum_coproduct += delta_x * delta_y * sweep; mean_x += delta_x / i;
     * mean_y += delta_y / i; } final double pop_sd_x = Math.sqrt( sum_sq_x / N );
     * final double pop_sd_y = Math.sqrt( sum_sq_y / N ); final double cov_x_y =
     * sum_coproduct / N; return cov_x_y / (pop_sd_x * pop_sd_y); }
     */

}
