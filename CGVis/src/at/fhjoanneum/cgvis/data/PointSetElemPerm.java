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
public class PointSetElemPerm implements IPointSet {

    private IPointSet pointSet;
    private int[] perm;
    private int[] permInverted;
    private String name;

    public PointSetElemPerm(IPointSet pointSet, int[] perm) {
        if (perm.length != pointSet.getSize()) {
            throw new IllegalArgumentException("Invalid permutation array");
        }
        this.pointSet = pointSet;
        this.perm = perm;
        this.permInverted = new int[perm.length];
        for (int i = 0; i < perm.length; i++) {
            final int pi = perm[i];
            if (pi < 0 || pi >= perm.length) {
                throw new IllegalArgumentException("Invalid permutation array");
            }
            permInverted[pi] = i;
        }
        this.name = pointSet.getName();
    }

    public String getAttributeLabel(int attribute) {
        return pointSet.getAttributeLabel(attribute);
    }

    public int getDimension() {
        return pointSet.getDimension();
    }

    public String getElementLabel(int element) {
        return pointSet.getElementLabel(perm[element]);
    }

    public int getSize() {
        return pointSet.getSize();
    }

    public double getValue(int element, int attribute) {
        return pointSet.getValue(perm[element], attribute);
    }

    public String getName() {
        return name;
    }

    public DataUID getAttributeId(int attribute) {
        return pointSet.getAttributeId(attribute);
    }

    public DataUID getElementId(int element) {
        return pointSet.getElementId(perm[element]);
    }

    public int getAttrById(DataUID attrId) {
        return pointSet.getAttrById(attrId);
    }

    public int getElementById(DataUID elementId) {
        final int idx = pointSet.getElementById(elementId);
        if (idx == -1) {
            return -1;
        } else {
            return permInverted[idx];
        }
    }

}