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
package at.fhjoanneum.cgvis.plots.mosaic;

import at.fhjoanneum.cgvis.data.IPointSet;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * TODO: finish HistogramNode
 * 
 * @author Ilya Boyandin
 */
public class HistogramNode extends PNode {

    private static final long serialVersionUID = -6276929921709503585L;

    // private IPointSet[] pointSets;

    public HistogramNode(IPointSet[] pointSets) {
        // this.pointSets = pointSets;
    }

    @Override
    protected void paint(PPaintContext paintContext) {
        super.paint(paintContext);

    }
}
