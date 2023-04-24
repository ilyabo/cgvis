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
import java.util.Iterator;
import java.util.List;

/**
 * @author Ilya Boyandin
 */
public class AttrSelection {

    private List<Integer> selectedAttrs;
    private IPointSet pointSet;

    public AttrSelection(IPointSet pointSet) {
        this.selectedAttrs = new ArrayList<Integer>();
        this.pointSet = pointSet;
    }

    public IPointSet getPointSet() {
        return pointSet;
    }

    /**
     * Note: Setting a new pointSet clears the selection <br>
     * TODO: keep selection, find attrs with the same IDs
     * 
     * @param pointSet
     */
    public void setPointSet(IPointSet pointSet) {
        if (this.pointSet != pointSet) {
            this.pointSet = pointSet;
            this.selectedAttrs.clear();
        }
    }

    public int getSize() {
        return selectedAttrs.size();
    }

    public void select(int index) {
        checkIndex(index);
        final Integer o = new Integer(index);
        if (!selectedAttrs.contains(o)) {
            selectedAttrs.add(o);
        }
    }

    public void selectRange(int start, int end) {
        if (start < 0 || end >= pointSet.getDimension() || start > end) {
            throw new IllegalArgumentException("Invalid selection range: "
                    + start + ".." + end);
        }
        for (int i = start; i <= end; i++) {
            final Integer o = new Integer(i);
            if (!selectedAttrs.contains(o)) {
                selectedAttrs.add(o);
            }
        }
    }

    /**
     * @return Whether the attr is selected now
     */
    public boolean invert(int index) {
        checkIndex(index);
        final Integer obj = new Integer(index);
        if (selectedAttrs.contains(obj)) {
            selectedAttrs.remove(obj);
            return false;
        } else {
            selectedAttrs.add(obj);
            return true;
        }
    }

    public void deselect(int index) {
        checkIndex(index);
        selectedAttrs.remove(new Integer(index));
    }

    public boolean isSelected(int index) {
        checkIndex(index);
        return selectedAttrs.contains(new Integer(index));
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= pointSet.getDimension()) {
            throw new IllegalArgumentException(
                    "No such attribute in the poinset: " + index);
        }
    }

    public Iterator<Integer> iterator() {
        return selectedAttrs.iterator();
    }

    public void selectAll() {
        selectedAttrs.clear();
        for (int i = 0; i < pointSet.getDimension(); i++) {
            selectedAttrs.add(new Integer(i));
        }
    }

    public void clearAll() {
        selectedAttrs.clear();
    }

    public void addSelectionListener() {
        // TODO:

    }
}
