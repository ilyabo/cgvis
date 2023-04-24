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
 * Once created the pointSet data cannot be modified, because the pointSet can
 * be wrapped into other pointSets that will not be aware of the changes.
 *
 * @author Ilya Boyandin
 */
public interface IPointSet extends IDataValues {

    String getName();

    String getElementLabel(int element);

    String getAttributeLabel(int attribute);

    DataUID getElementId(int element);

    DataUID getAttributeId(int attribute);

    /**
     * @return Index of the element with the given elementId or -1 if there is
     *         no such element in the pointSet
     */
    int getElementById(DataUID elementId);

    /**
     * @return Index of the attribute with the given attrId or -1 if there is no
     *         such attribute in the pointSet
     */
    int getAttrById(DataUID attrId);
}
