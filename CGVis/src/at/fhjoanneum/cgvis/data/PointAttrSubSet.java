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

import java.util.Iterator;

/**
 * @author Ilya Boyandin
 */
public class PointAttrSubSet implements IPointSet {

    private IPointSet pointSet;
    private int[] indices;

    public PointAttrSubSet(AttrSelection cs) {
        this.pointSet = cs.getPointSet();
        final int[] indices = new int[cs.getSize()];
        int cnt = 0;
        final Iterator<Integer> it = cs.iterator();
        while (it.hasNext()) {
            indices[cnt++] = it.next();
        }
        this.indices = indices;
    }

    public String getAttributeLabel(int attribute) {
        return pointSet.getAttributeLabel(indices[attribute]);
    }

    public int getDimension() {
        return indices.length;
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
        return pointSet.getValue(element, indices[attribute]);
    }

    public DataUID getAttributeId(int attribute) {
        return pointSet.getAttributeId(indices[attribute]);
    }

    public DataUID getElementId(int element) {
        return pointSet.getElementId(element);
    }

    public int getAttrById(DataUID attrId) {
        // TODO getAttrById
        return -1;
    }

    public int getElementById(DataUID elementId) {
        return pointSet.getElementById(elementId);
    }

}
