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

import java.util.ArrayList;
import java.util.HashMap;

// Ilya Stuff: attrLabels are column labels...

/**
 * @author Erik Koerner
 */
public class ExPointSet implements IPointSet {

    private final String name;
    private int colCount;
    private int rowCount;
    private double allocation;
    private final double[][] values;
    private final double[][] normalized;
    private final double[] colMax;
    private final double[] colMin;
    private final ArrayList<String> rowLabels;
    private final ArrayList<String> colLabels;
    private double globalMin, globalMax;
    private final DataUID[] elementIds;
    private final DataUID[] attrIds;

    private final HashMap<DataUID, Integer> elementIdHashMap;
    private final HashMap<DataUID, Integer> attrIdHashMap;

    private final DataUID dataSourceUID;
    private final DataUID pointSetUID;

    public ExPointSet(String name, double[][] values, DataUID[] elementIds,
            String[] elementLabels, DataUID[] attrIds, String[] attrLabels,
            DataUID dataSourceUID, DataUID pointSetUID) {
        int i, x, y;
        double min, max, v;

        this.name = name;
        this.dataSourceUID = dataSourceUID;
        this.pointSetUID = pointSetUID;

        colCount = values[0].length;
        rowCount = values.length;

        for (i = 1; i < values.length; i++) {
            if (values[i].length != colCount) {
                colCount = 0;
                rowCount = 0;
                throw new IllegalArgumentException("Inconsistent values array");
            }
        }
        this.values = values;
        this.normalized = new double[rowCount][colCount];
        for (x = 0; x < colCount; x++) {
            for (y = 0; y < rowCount; y++) {
                normalized[y][x] = values[y][x];
            }
        }

        // **** Element IDs: ****

        if (elementIds != null && elementIds.length != rowCount) {
            throw new IllegalArgumentException("Wrong element ids number");
        }
        this.elementIds = elementIds;
        elementIdHashMap = new HashMap<DataUID, Integer>();
        if (elementIds != null) {
            for (i = 0; i < elementIds.length; i++) {
                elementIdHashMap.put(this.elementIds[i], i);
            }
        }

        // **** Attribute IDs: ****

        if (attrIds != null && attrIds.length != colCount) {
            throw new IllegalArgumentException("Wrong attribute ids number");
        }
        this.attrIds = attrIds;
        attrIdHashMap = new HashMap<DataUID, Integer>();
        if (attrIds != null) {
            for (i = 0; i < attrIds.length; i++) {
                attrIdHashMap.put(this.attrIds[i], i);
            }
        }

        if (elementLabels != null && elementLabels.length != rowCount) {
            throw new IllegalArgumentException("Wrong element labels count");
        }

        this.rowLabels = new ArrayList<String>(elementLabels.length);
        for (i = 0; i < elementLabels.length; i++)
            this.rowLabels.add(elementLabels[i]);

        if (attrLabels != null && attrLabels.length != colCount) {
            throw new IllegalArgumentException("Wrong attr labels count");
        }
        this.colLabels = new ArrayList<String>(attrLabels.length);
        for (i = 0; i < attrLabels.length; i++)
            this.colLabels.add(attrLabels[i]);

        // Determination of Minima and Maxima, as well for Colums and globally.

        colMax = new double[colCount];
        colMin = new double[colCount];
        globalMin = Double.MAX_VALUE;
        globalMax = Double.MIN_VALUE;
        allocation = 0;
        for (x = 0; x < colCount; x++) {
            min = Double.MAX_VALUE;
            max = Double.MIN_VALUE;
            for (y = 0; y < rowCount; y++) {
                v = this.values[y][x];
                if (Double.isNaN(v))
                    continue;
                min = Math.min(min, v);
                max = Math.max(max, v);
                allocation++;
            }
            if (min == Double.MAX_VALUE)
                colMin[x] = Double.NaN;
            else {
                colMin[x] = min;
                globalMin = Math.min(globalMin, min);
            }
            if (max == Double.MIN_VALUE)
                colMax[x] = Double.NaN;
            else {
                colMax[x] = max;
                globalMax = Math.max(globalMax, max);
            }
        }
        allocation /= (rowCount * colCount);
    }

    // =========================================================
    // IPointSet Interface:
    // Methods that are not included in IPointSet currently are
    // marked as "new"
    // =========================================================

    public String getName() {
        return name;
    }

    public int getSize() {
        return rowCount;
    }

    public int getDimension() {
        return colCount;
    }

    public double getValue(int element, int attribute) {
        return normalized[element][attribute];
    }

    public double getRawValue(int element, int attr) {
        return values[element][attr];
    }

    // Ilya Stuff: elements are rows ...

    public boolean existsElement(String elementLabel) { // new
        return rowLabels.contains(elementLabel);
    }

    public String getElementLabel(int element) {
        return rowLabels.get(element);
    }

    public int getElementIndex(String elementLabel) { // new
        if (existsElement(elementLabel) == false)
            return -1;
        return rowLabels.indexOf(elementLabel);
    }

    // Ilya Stuff: attrs are columns...

    public boolean existsAttribute(String attrLabel) { // new
        return colLabels.contains(attrLabel);
    }

    public String getAttributeLabel(int attribute) {
        return colLabels.get(attribute);
    }

    public int getAttributeIndex(String attrLabel) { // new
        if (existsAttribute(attrLabel) == false)
            return -1;
        return colLabels.indexOf(attrLabel);
    }

    // Normalization happens columnwise....

    public void normalize(String attrLabel, Normalization type) { // new
        if (attrLabel == "*") {
            for (int i = 0; i < colCount; i++)
                normalize(i, type);
            return;
        }
        int attr = getAttributeIndex(attrLabel);
        if (attr == -1)
            return;
        normalize(attr, type);
    }

    public void normalize(String attrLabel, Normalization type, double max) { // new
        if (attrLabel == "*") {
            for (int i = 0; i < colCount; i++)
                normalize(i, type, max);
            return;
        }
        int attr = getAttributeIndex(attrLabel);
        if (attr == -1)
            return;
        normalize(attr, type, max);
    }

    public void normalize(String attrLabel, Normalization type, double min,
            double max) { // new
        if (attrLabel == "*") {
            for (int i = 0; i < colCount; i++)
                normalize(i, type, min, max);
            return;
        }
        int attr = getAttributeIndex(attrLabel);
        if (attr == -1)
            return;
        normalize(attr, type, min, max);
    }

    public void normalize(int attr, Normalization type) { // new
        switch (type) {
        case RAW:
            restoreRawData(attr);
            break;
        case RANGE:
            normalizeRange(attr, 0.0, 1.0);
            break;
        case VECTOR:
            normalizeVector(attr, 1.0);
            break;
        }
    }

    public void normalize(int attr, Normalization type, double max) { // new
        switch (type) {
        case RAW:
            restoreRawData(attr);
            break;
        case RANGE:
            normalizeRange(attr, 0.0, max);
            break;
        case VECTOR:
            normalizeVector(attr, max);
            break;
        }
    }

    public void normalize(int attr, Normalization type, double min, double max) { // new
        switch (type) {
        case RAW:
            restoreRawData(attr);
            break;
        case RANGE:
            normalizeRange(attr, min, max);
            break;
        case VECTOR:
            normalizeVector(attr, max);
            break;
        }
    }

    public double getAllocation() { // new
        return allocation;
    }

    public double getMaxValue() {
        return globalMax;
    }

    public double getMinValue() {
        return globalMin;
    }

    // =========================================================
    // Normalization Methods (Pointset internal)
    // =========================================================

    /**
     * Restores the raw data after a normalization
     *
     * @param col
     *            Index of Column to restore
     */
    private void restoreRawData(int col) {
        if (col < 0 || col >= colCount)
            return;
        for (int i = 0; i < rowCount; i++)
            normalized[i][col] = values[i][col];
    }

    /**
     * Normalizes column between min and max values
     *
     * @param col
     *            Index of Column to normalize
     */
    private void normalizeRange(int col, double min, double max) {
        if (col < 0 || col >= colCount)
            return;
        double minData = Double.POSITIVE_INFINITY;
        double maxData = Double.NEGATIVE_INFINITY;
        double val;
        for (int i = 0; i < rowCount; i++) {
            val = values[i][col];
            if (Double.isNaN(val))
                continue;
            minData = Math.min(minData, val);
            maxData = Math.max(maxData, val);
        }
        if (Double.isInfinite(minData)  || Double.isInfinite(maxData))
            return;
        double factor = 1.0 / (maxData - minData);
        if (maxData != minData) {
            for (int i = 0; i < rowCount; i++) {
                if (Double.isNaN(values[i][col]))
                    normalized[i][col] = Double.NaN;
                else
                    normalized[i][col] = (values[i][col] - minData) * factor;
            }
        } else {
            for (int i = 0; i < rowCount; i++) {
                if (Double.isNaN(values[i][col]))
                    normalized[i][col] = Double.NaN;
                else
                    normalized[i][col] = 1.0;
            }
        }
    }

    /**
     * Normalizes Column according unit vector style
     *
     * @param col
     *            Index of Column to normalize
     * @param length
     *            Length of vector (set to 1 for unit vector)
     */
    private void normalizeVector(int col, double length) {
        if (col < 0 || col >= colCount)
            return;
        double sum = 0;
        double val;
        for (int i = 0; i < rowCount; i++) {
            val = values[i][col];
            if (Double.isNaN(val))
                continue;
            sum += val * val;
        }
        if (Double.isInfinite(sum))
            throw new RuntimeException("Unable to normalize column "
                    + Integer.toString(col) + " as unit vector");
        sum = Math.sqrt(sum);
        if (sum != 0) {
            for (int i = 0; i < rowCount; i++) {
                if (Double.isNaN(values[i][col]))
                    normalized[i][col] = Double.NaN;
                else
                    normalized[i][col] = values[i][col] / sum * length;
            }
        } else {
            for (int i = 0; i < rowCount; i++) {
                if (Double.isNaN(values[i][col]))
                    normalized[i][col] = Double.NaN;
                else
                    normalized[i][col] = 0.0;
            }
        }
    }

    public DataUID getAttributeId(int attribute) {
        return attrIds[attribute];
    }

    public DataUID getElementId(int element) {
        return elementIds[element];
    }

    public int getAttrById(DataUID attrId) {
        if (attrIdHashMap.containsKey(attrId) == false)
            return -1;
        return attrIdHashMap.get(attrId);
    }

    public int getElementById(DataUID elementId) {
        if (elementIdHashMap.containsKey(elementId) == false)
            return -1;
        return elementIdHashMap.get(elementId);
    }

    public DataUID getDataSourceId() {
        return dataSourceUID;
    }

    public DataUID getPointSetId() {
        return pointSetUID;
    }
}
