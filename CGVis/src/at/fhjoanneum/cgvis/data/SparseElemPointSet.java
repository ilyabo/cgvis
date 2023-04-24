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
import java.util.TreeMap;
import java.util.Map.Entry;

/**
 * @author Ilya Boyandin
 */
public class SparseElemPointSet implements IPointSet {

    private IPointSet pointSet;
    private int[] elemIndices; // sparse idx -> orig idx
    private int[] elemIndicesInverted; // orig idx -> sparse idx
    private String[] elementLabels;
    private DataUID[] elemIds;

    /**
     * @param pointSet
     *            Original non-sparse pointset
     * @param elemIndices
     *            Mapping: (sparse pointset index -> original pointset index, or
     *            -1 if missing)
     * @param elementLabels
     *            Element labels for the full sparse pointset
     */
    public SparseElemPointSet(IPointSet pointSet, int[] elemIndices,
            DataUID[] elementIds, String[] elementLabels) {
        if (elementLabels.length != elemIndices.length) {
            throw new IllegalArgumentException(
                    "Invalid number of element labels");
        }
        if (elementIds.length != elemIndices.length) {
            throw new IllegalArgumentException("Invalid number of element ids");
        }
        this.elemIds = elementIds;
        this.pointSet = pointSet;
        this.elemIndices = elemIndices;
        this.elementLabels = elementLabels;

        this.elemIndicesInverted = new int[elemIndices.length];
        for (int i = 0; i < elemIndices.length; i++) {
            elemIndicesInverted[i] = -1;
        }
        for (int i = 0; i < elemIndices.length; i++) {
            final int idx = elemIndices[i];
            if (idx != -1) {
                elemIndicesInverted[idx] = i;
            }
        }
    }

    public DataUID getAttributeId(int attribute) {
        return pointSet.getAttributeId(attribute);
    }

    public String getAttributeLabel(int attribute) {
        return pointSet.getAttributeLabel(attribute);
    }

    public int getDimension() {
        return pointSet.getDimension();
    }

    public DataUID getElementId(int element) {
        return elemIds[element];
    }

    public String getElementLabel(int element) {
        return elementLabels[element];
    }

    public double getValue(int element, int attribute) {
        final int idx = elemIndices[element];
        if (idx >= 0) {
            return pointSet.getValue(idx, attribute);
        } else {
            return Double.NaN;
        }
    }

    public String getName() {
        return pointSet.getName();
    }

    public int getSize() {
        return elemIndices.length;
    }

    public int getAttrById(DataUID attrId) {
        return pointSet.getAttrById(attrId);
    }

    public int getElementById(DataUID elementId) {
        final int origIndex = pointSet.getElementById(elementId);
        if (origIndex == -1) {
            return -1;
        } else {
            return elemIndicesInverted[origIndex];
        }
    }

    /**
     * Wraps the given pointSets into sparse pointsets sharing the same
     * elements. The new set of elements is obtained by joining the sets of
     * elements of the source pointsets. Elements are identified by thier
     * labels, so if two elements in two pointsets have the same label they will
     * be merged in one element.
     * 
     * @param srcPointSets
     * @return
     */
    public static IPointSet[] joinElementsByLabel(IPointSet[] srcPointSets) {
        // Sort pointSets by element labels
        final TreeMap<String, Integer>[] pst = new TreeMap[srcPointSets.length];
        for (int i = 0; i < srcPointSets.length; i++) {
            final IPointSet ps = srcPointSets[i];
            final TreeMap<String, Integer> tm = new TreeMap<String, Integer>();
            for (int k = 0, size = ps.getSize(); k < size; k++) {
                tm.put(ps.getElementLabel(k), k);
            }
            pst[i] = tm;
        }

        // Make joined indices for sparse ponit sets
        int countDistinct = 0;
        int[][][] sparseIndices = new int[pst.length][][];
        final Iterator<Entry<String, Integer>>[] psti = new Iterator[pst.length];
        final int[] pstIdx = new int[pst.length];
        for (int i = 0, size = pst.length; i < size; i++) {
            psti[i] = pst[i].entrySet().iterator();
            pstIdx[i] = 0;
            sparseIndices[i] = new int[pst[i].size()][];
        }
        final Entry<String, Integer>[] pstNext = new Entry[pst.length];
        for (int i = 0, size = psti.length; i < size; i++) { // init pstNext
            if (psti[i].hasNext())
                pstNext[i] = psti[i].next();
        }
        Entry<String, Integer> next = null, prev = null;
        while (true) {
            next = null;
            int nextI = -1;
            for (int i = 0, size = psti.length; i < size; i++) { // choose
                                                                    // the min
                                                                    // next
                if (pstNext[i] == null) {
                    continue;
                }
                if (next == null
                        || pstNext[i].getKey().compareTo(next.getKey()) < 0) {
                    next = pstNext[i];
                    nextI = i;
                }
            }
            if (next != null) {
                pstNext[nextI] = (psti[nextI].hasNext() ? psti[nextI].next()
                        : null);
                if (prev == null || !prev.getKey().equals(next.getKey())) {
                    countDistinct++;
                }

                sparseIndices[nextI][pstIdx[nextI]] = new int[] { // sparse_pset_idx
                                                                    // ->
                                                                    // actual_pset_idx
                countDistinct - 1, next.getValue() };
                pstIdx[nextI]++;
                prev = next;
            } else {
                break;
            }
        }

        // Make full ids and labels list
        final DataUID[] elemIds = new DataUID[countDistinct];
        final String[] elemLabels = new String[countDistinct];
        for (int psetIdx = 0, size = pst.length; psetIdx < size; psetIdx++) {
            final int[][] si = sparseIndices[psetIdx];
            for (int k = 0; k < si.length; k++) {
                final int sparseIdx = si[k][0];
                final int actualIdx = si[k][1];
                if (elemIds[sparseIdx] == null) {
                    elemIds[sparseIdx] = srcPointSets[psetIdx]
                            .getElementId(actualIdx);
                    elemLabels[sparseIdx] = srcPointSets[psetIdx]
                            .getElementLabel(actualIdx);
                }
            }
        }

        // Make sparse pointsets
        final IPointSet[] result = new IPointSet[pst.length];
        for (int psetIdx = 0, size = pst.length; psetIdx < size; psetIdx++) {
            // convert list of indices to array
            final int[][] si = sparseIndices[psetIdx];
            final int[] fsIndices = new int[countDistinct];
            for (int k = 0; k < countDistinct; k++)
                fsIndices[k] = -1;
            for (int k = 0; k < si.length; k++) {
                final int sparseIdx = si[k][0];
                final int actualIdx = si[k][1];
                fsIndices[sparseIdx] = actualIdx;
            }

            result[psetIdx] = new SparseElemPointSet(srcPointSets[psetIdx],
                    fsIndices, elemIds, elemLabels);
        }

        return result;
    }
}
