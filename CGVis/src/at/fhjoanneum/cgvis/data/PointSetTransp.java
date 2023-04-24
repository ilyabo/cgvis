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
public class PointSetTransp implements IPointSet {

    private IPointSet pointSet;
    private String name;

    public PointSetTransp(IPointSet pointSet) {
        this.pointSet = pointSet;
        this.name = pointSet.getName() + " Transp";
    }

    public String getAttributeLabel(int attribute) {
        return pointSet.getElementLabel(attribute);
    }

    public int getDimension() {
        return pointSet.getSize();
    }

    public String getElementLabel(int element) {
        return pointSet.getAttributeLabel(element);
    }

    public int getSize() {
        return pointSet.getDimension();
    }

    public double getValue(int element, int attribute) {
        return pointSet.getValue(attribute, element);
    }

    public String getName() {
        return name;
    }

    public DataUID getAttributeId(int attribute) {
        return pointSet.getElementId(attribute);
    }

    public DataUID getElementId(int element) {
        return pointSet.getAttributeId(element);
    }

    public int getAttrById(DataUID attrId) {
        return pointSet.getElementById(attrId);
    }

    public int getElementById(DataUID elementId) {
        return pointSet.getAttrById(elementId);
    }

}
