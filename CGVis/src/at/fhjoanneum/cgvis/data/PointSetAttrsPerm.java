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
public class PointSetAttrsPerm implements IPointSet {

    private IPointSet pointSet;
    private int[] perm;
    private String name;

    public PointSetAttrsPerm(IPointSet pointSet, int[] perm) {
        if (perm.length != pointSet.getDimension()) {
            throw new IllegalArgumentException("Invalid permutation array");
        }
        this.pointSet = pointSet;
        this.perm = perm;
        this.name = pointSet.getName();
    }

    public String getAttributeLabel(int attribute) {
        return pointSet.getAttributeLabel(perm[attribute]);
    }

    public int getDimension() {
        return pointSet.getDimension();
    }

    public String getElementLabel(int element) {
        return pointSet.getElementLabel(element);
    }

    public int getSize() {
        return pointSet.getSize();
    }

    public double getValue(int element, int attribute) {
        return pointSet.getValue(element, perm[attribute]);
    }

    public String getName() {
        return name;
    }

    public DataUID getAttributeId(int attribute) {
        return pointSet.getAttributeId(perm[attribute]);
    }

    public DataUID getElementId(int element) {
        return pointSet.getElementId(element);
    }

    public int getAttrById(DataUID attrId) {
        return -1; // TODO: getAttrById
    }

    public int getElementById(DataUID elementId) {
        return pointSet.getElementById(elementId);
    }

}
