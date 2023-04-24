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
 * This is a wrapper pointSet that normalizes the original pointSet by attrs
 * 
 * @author Ilya Boyandin
 */
public class PointSetNorm implements IPointSet {

    public enum NormType {
        UNIT_VECTOR, SCALE_0_1
    };

    private IPointSet pointSet;
    private double[] alpha;
    private double[] r;
    private NormType type;

    public PointSetNorm(IPointSet pointSet, NormType type) {
        this.pointSet = pointSet;
        this.type = type;
        final int dim = pointSet.getDimension();
        this.alpha = new double[dim];
        this.r = new double[dim];
        renormalize();
    }

    /**
     * This method should be called each time when the data is changed.
     */
    protected void renormalize() {
        switch (type) {
        case UNIT_VECTOR:
            for (int c = 0, dim = pointSet.getDimension(), size = pointSet
                    .getSize(); c < dim; c++) {
                double sum = 0;
                for (int e = 0; e < size; e++) {
                    final double v = pointSet.getValue(e, c);
                    if (!Double.isNaN(v)) {
                        sum += v * v;
                    }
                }
                final double a = Math.sqrt(sum);
                if (Double.isInfinite(a)) {
                    throw new RuntimeException("Data couldn't be normalized");
                }
                alpha[c] = 1.0 / a;
                r[c] = 0;
            }
            break;

        case SCALE_0_1:
            for (int c = 0, dim = pointSet.getDimension(), size = pointSet
                    .getSize(); c < dim; c++) {
                double max = Double.NEGATIVE_INFINITY, min = Double.POSITIVE_INFINITY;
                int vCnt = 0;
                for (int e = 0; e < size; e++) {
                    final double v = pointSet.getValue(e, c);
                    if (!Double.isNaN(v)) {
                        if (v > max)
                            max = v;
                        if (v < min)
                            min = v;
                        vCnt++;
                    }
                }
                if (vCnt > 0) {
                    if (Double.isInfinite(max) || Double.isInfinite(min)) {
                        throw new RuntimeException(
                                "Data couldn't be normalized");
                    }
                    alpha[c] = 1.0 / (max - min);
                    r[c] = -min;
                } else {
                    alpha[c] = 1.0;
                    r[c] = 0.0;
                }
            }
            break;
        }
    }

    public String getAttributeLabel(int attribute) {
        return pointSet.getAttributeLabel(attribute);
    }

    public int getDimension() {
        return pointSet.getDimension();
    }

    public String getElementLabel(int element) {
        return pointSet.getElementLabel(element);
    }

    public String getName() {
        return pointSet.getName();
    }

    public int getSize() {
        return pointSet.getSize();
    }

    public double getValue(int element, int attribute) {
        return (pointSet.getValue(element, attribute) + r[attribute])
                * alpha[attribute];
    }

    public DataUID getAttributeId(int attribute) {
        return pointSet.getAttributeId(attribute);
    }

    public DataUID getElementId(int element) {
        return pointSet.getElementId(element);
    }

    public int getAttrById(DataUID attrId) {
        return pointSet.getAttrById(attrId);
    }

    public int getElementById(DataUID elementId) {
        return pointSet.getElementById(elementId);
    }

}
