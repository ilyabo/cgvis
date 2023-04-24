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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Ilya Boyandin
 */
public class ElementSelection {

    private Set<Integer> selectedElems;
    private IPointSet pointSet;

    public ElementSelection(IPointSet pointSet) {
        this.selectedElems = new HashSet<Integer>();
        this.pointSet = pointSet;
    }

    public IPointSet getPointSet() {
        return pointSet;
    }

    /**
     * Note: Setting a new pointSet clears the selection <br>
     * TODO: keep selection, find elements with the same IDs
     * 
     * @param pointSet
     */
    public void setPointSet(IPointSet pointSet) {
        if (this.pointSet != pointSet) {
            this.pointSet = pointSet;
            this.selectedElems.clear();
        }
    }

    public int getSize() {
        return selectedElems.size();
    }

    public void select(int index) {
        checkIndex(index);
        selectedElems.add(new Integer(index));
    }

    public void selectRange(int start, int end) {
        if (start < 0 || end >= pointSet.getSize() || start > end) {
            throw new IllegalArgumentException("Invalid selection range: "
                    + start + ".." + end);
        }
        for (int i = start; i <= end; i++) {
            selectedElems.add(new Integer(i));
        }
    }

    /**
     * @return Whether the element is selected now
     */
    public boolean invert(int index) {
        checkIndex(index);
        final Integer obj = new Integer(index);
        if (selectedElems.contains(obj)) {
            selectedElems.remove(obj);
            return false;
        } else {
            selectedElems.add(obj);
            return true;
        }
    }

    public void deselect(int index) {
        checkIndex(index);
        selectedElems.remove(new Integer(index));
    }

    public boolean isSelected(int index) {
        checkIndex(index);
        return selectedElems.contains(new Integer(index));
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= pointSet.getSize()) {
            throw new IllegalArgumentException(
                    "No such element in the poinset: " + index);
        }
    }

    public Iterator<Integer> iterator() {
        return selectedElems.iterator();
    }

    public void selectAll() {
        // selectedElems.clear();
        for (int i = 0; i < pointSet.getSize(); i++) {
            selectedElems.add(new Integer(i));
        }
    }

    public void clearAll() {
        selectedElems.clear();
    }

    public void addSelectionListener() {
        // TODO:

    }

    public DataUID[] getSelectionUIDs() {
        final int size = selectedElems.size();
        final DataUID[] sel = new DataUID[size];
        int i = 0;
        for (Integer idx : selectedElems) {
            sel[i++] = pointSet.getElementId(idx);
        }
        return sel;
    }

    public void setSelectionUIDs(DataUID[] selection) {
        selectedElems.clear();
        for (int i = 0; i < selection.length; i++) {
            final int elemIdx = pointSet.getElementById(selection[i]);
            if (elemIdx != -1) {
                selectedElems.add(elemIdx);
            }
        }
    }

}
