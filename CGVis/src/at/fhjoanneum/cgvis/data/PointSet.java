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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ilya Boyandin
 */
public class PointSet implements IPointSet {

    private String name;
    private double[][] values;
    private String[] elementLabels;
    private String[] attrLabels;
    private int size;
    private int dimension;
    private DataUID[] elementIds;
    private DataUID[] attrIds;
    private Map<DataUID, Integer> elementIdToIdx;
    private Map<DataUID, Integer> attrIdToIdx;

    /**
     * The arrays passed arrays passed in MUST NOT BE MODIFIED afterwards.
     */
    public PointSet(String name, double[][] values, DataUID[] elementIds,
            String[] elementLabels, DataUID[] attrIds, String[] attrLabels) {
        this.name = name;
        this.size = values.length;
        final int dim = values[0].length;
        for (int i = 1; i < values.length; i++) {
            if (values[i].length != dim) {
                throw new IllegalArgumentException("Inconsistent values array");
            }
        }
        this.dimension = dim;
        this.values = values;

        if (elementIds != null && elementIds.length != size) {
            throw new IllegalArgumentException("Wrong element ids number");
        }
        this.elementIds = elementIds;

        if (attrIds != null && attrIds.length != dim) {
            throw new IllegalArgumentException("Wrong attribute ids number");
        }
        this.attrIds = attrIds;

        if (elementLabels != null && elementLabels.length != size) {
            throw new IllegalArgumentException("Wrong element labels number");
        }
        this.elementLabels = elementLabels;

        if (attrLabels != null && attrLabels.length != dimension) {
            throw new IllegalArgumentException("Wrong attrubute labels number");
        }
        this.attrLabels = attrLabels;
        initUIDMaps();
    }

    /**
     * Copy constructor
     * 
     * @param pointSet
     */
    public PointSet(IPointSet pointSet) {
        this.name = pointSet.getName();
        this.size = pointSet.getSize();
        this.dimension = pointSet.getDimension();
        this.values = new double[size][dimension];
        this.elementLabels = new String[size];
        this.attrLabels = new String[dimension];
        this.elementIds = new DataUID[size];
        this.attrIds = new DataUID[dimension];
        for (int i = 0; i < size; i++) {
            elementIds[i] = pointSet.getElementId(i);
            elementLabels[i] = pointSet.getElementLabel(i);
            for (int j = 0; j < dimension; j++) {
                if (i == 0) {
                    attrIds[j] = pointSet.getAttributeId(j);
                    attrLabels[j] = pointSet.getAttributeLabel(j);
                }
                values[i][j] = pointSet.getValue(i, j);
            }
        }
        initUIDMaps();
    }

    private void initUIDMaps() {
        attrIdToIdx = new HashMap<DataUID, Integer>(attrIds.length);
        for (int i = 0; i < attrIds.length; i++) {
            attrIdToIdx.put(this.attrIds[i], i);
        }

        elementIdToIdx = new HashMap<DataUID, Integer>(elementIds.length);
        for (int i = 0; i < elementIds.length; i++) {
            elementIdToIdx.put(this.elementIds[i], i);
        }
    }

    public String getName() {
        return name;
    }

    public int getDimension() {
        return dimension;
    }

    public int getSize() {
        return size;
    }

    public double getValue(int element, int attribute) {
        return values[element][attribute];
    }

    public String getElementLabel(int element) {
        return elementLabels[element];
    }

    public String getAttributeLabel(int attribute) {
        return attrLabels[attribute];
    }

    public DataUID getAttributeId(int attribute) {
        return attrIds[attribute];
    }

    public DataUID getElementId(int element) {
        return elementIds[element];
    }

    public int getAttrById(DataUID attrId) {
        final Integer idx = attrIdToIdx.get(attrId);
        if (idx == null) {
            return -1;
        } else {
            return idx;
        }
    }

    public int getElementById(DataUID elementId) {
        final Integer idx = elementIdToIdx.get(elementId);
        if (idx == null) {
            return -1;
        } else {
            return idx;
        }
    }

}
