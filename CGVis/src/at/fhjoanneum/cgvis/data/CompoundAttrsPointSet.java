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
 * Joins several pointsets into a bigger one. Original pointsets MUST USE the
 * same sets of elements! Element labels are the element labels of the
 * <em>first</em> pointset.
 * 
 * @author Ilya Boyandin
 */
public class CompoundAttrsPointSet implements IPointSet {

    private IPointSet[] pointSets;
    private int dimension;
    private String name;
    private int size;

    public CompoundAttrsPointSet(IPointSet[] pointSets) {
        if (pointSets.length == 0) {
            throw new IllegalArgumentException("No pointSets to join");
        }
        checkElements(pointSets);
        this.pointSets = pointSets.clone();
        StringBuffer sb = new StringBuffer();
        int dim = 0;
        int size = 0;
        for (int i = 0, n = pointSets.length; i < n; i++) {
            final IPointSet pset = pointSets[i];
            final int sz = pset.getSize();
            if (sz > size) {
                size = sz;
            }
            dim += pset.getDimension();
            sb.append(pset.getName());
            if (i < n - 1) {
                sb.append("+");
            }
        }
        this.name = sb.toString();
        this.dimension = dim;
        this.size = size;
    }

    /**
     * @return Must not be modified
     */
    public IPointSet[] getPointSets() {
        return pointSets;
    }

    private void checkElements(IPointSet[] pointSets) {
        final IPointSet pset0 = pointSets[0];
        final int size = pset0.getSize();
        for (IPointSet pset : pointSets) {
            if (pset.getSize() != size) {
                throw new IllegalArgumentException(
                        "Pointsets have different sizes");
            }
            for (int i = 0; i < size; i++) {
                if (!pset0.getElementId(i).equals(pset.getElementId(i))) {
                    throw new IllegalArgumentException(
                            "Pointsets must have the same elements");
                }
            }
        }
    }

    private class Tuple {
        int pointSetIndex;
        int attr;

        public Tuple(int psetIndex, int offset) {
            this.pointSetIndex = psetIndex;
            this.attr = offset;
        }
    }

    private Tuple indexToPointSet(int attr) {
        int c = attr;
        for (int i = 0, n = pointSets.length; i < n; i++) {
            final IPointSet pset = pointSets[i];
            final int d = pset.getDimension();
            if (c >= d) {
                c -= d;
            } else {
                return new Tuple(i, c);
            }
        }
        throw new IllegalArgumentException("Invalid attribute index");
    }

    public String getAttributeLabel(int attribute) {
        final Tuple t = indexToPointSet(attribute);
        return pointSets[t.pointSetIndex].getAttributeLabel(t.attr);
    }

    public int getDimension() {
        return dimension;
    }

    public String getElementLabel(int element) {
        for (IPointSet pset : pointSets) {
            if (element < pset.getSize()) {
                return pset.getElementLabel(element);
            }
        }
        throw new IllegalArgumentException("Invalid element index");
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public double getValue(int element, int attribute) {
        final Tuple t = indexToPointSet(attribute);
        final IPointSet pset = pointSets[t.pointSetIndex];
        if (element < pset.getSize()) {
            return pset.getValue(element, t.attr);
        } else {
            return Double.NaN;
        }
    }

    public DataUID getAttributeId(int attribute) {
        final Tuple t = indexToPointSet(attribute);
        return pointSets[t.pointSetIndex].getAttributeId(t.attr);
    }

    public DataUID getElementId(int element) {
        return pointSets[0].getElementId(element);
    }

    public int getAttrById(DataUID attrId) {
        return -1; // TODO: getAttrById
    }

    public int getElementById(DataUID elementId) {
        return pointSets[0].getElementById(elementId);
    }
}
