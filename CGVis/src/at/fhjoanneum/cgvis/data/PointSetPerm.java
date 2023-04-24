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
 * 
 * @author Ilya Boyandin
 */
public class PointSetPerm implements IPointSet {

    private IPointSet pointSet;
    private int[] elementPerm;
    private int[] elementPermInverted;
    private int[] attrPerm;
    private String name;

    public PointSetPerm(IPointSet pointSet, int[] elementPerm, int[] attrPerm) {
        if (elementPerm.length != pointSet.getSize()) {
            throw new IllegalArgumentException("Invalid element permutation");
        }
        if (attrPerm.length != pointSet.getDimension()) {
            throw new IllegalArgumentException("Invalid attribute permutation");
        }
        this.pointSet = pointSet;
        this.elementPerm = elementPerm;
        this.attrPerm = attrPerm;
        this.name = pointSet.getName();

        this.elementPermInverted = new int[elementPerm.length];
        for (int i = 0; i < elementPerm.length; i++) {
            final int pi = elementPerm[i];
            if (pi < 0 || pi >= elementPerm.length) {
                throw new IllegalArgumentException(
                        "Invalid element permutation");
            }
            elementPermInverted[pi] = i;
        }
    }

    public String getAttributeLabel(int attribute) {
        return pointSet.getAttributeLabel(attrPerm[attribute]);
    }

    public int getDimension() {
        return pointSet.getDimension();
    }

    public String getElementLabel(int element) {
        return pointSet.getElementLabel(elementPerm[element]);
    }

    public int getSize() {
        return pointSet.getSize();
    }

    public double getValue(int element, int attribute) {
        return pointSet.getValue(elementPerm[element], attrPerm[attribute]);
    }

    public String getName() {
        return name;
    }

    public DataUID getAttributeId(int attribute) {
        return pointSet.getAttributeId(attrPerm[attribute]);
    }

    public DataUID getElementId(int element) {
        return pointSet.getElementId(elementPerm[element]);
    }

    public int getAttrById(DataUID attrId) {
        return -1; // TODO: getAttrById
    }

    public int getElementById(DataUID elementId) {
        final int idx = pointSet.getElementById(elementId);
        if (idx == -1) {
            return -1;
        } else {
            return elementPermInverted[idx];
        }
    }

}
