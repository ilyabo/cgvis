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
package at.fhjoanneum.cgvis.plots.scatter;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.List;

import at.fhjoanneum.cgvis.plots.PPath2;
import edu.umd.cs.piccolo.activities.PActivity;

/**
 * @author Ilya Boyandin
 */
public class PlotAreaNode extends PPath2 {

    private static final long serialVersionUID = 636314565832410494L;
    private PActivity lastActivity;

    public PlotAreaNode() {
        super(new Rectangle2D.Float(0, 0, 1, 1));
        setStrokePaint(Color.gray);
        setPaint(Color.white);
        // setStroke(new PFixedWidthStroke(1.0f));
        setStroke(null);
    }

    // TODO: clip non-NaN point at.fhjoanneum.zscatter.nodes (see PClip)

    @Override
    public boolean addActivity(PActivity activity) {
        if (lastActivity != null && lastActivity.isStepping()) {
            lastActivity.terminate(PActivity.TERMINATE_WITHOUT_FINISHING);
            lastActivity = null;
        }
        if (super.addActivity(activity)) {
            lastActivity = activity;
            return true;
        } else {
            return false;
        }
    }

    public PActivity getLastActivity() {
        return lastActivity;
    }

    public void bringToFront(PointNode pointNode) {
        removeChild(pointNode);
        addChild(pointNode);
    }

    public void bringToFront(List<PointNode> nodeSelection) {
        final int size = nodeSelection.size();
        for (int i = 0; i < size; i++)
            removeChild(nodeSelection.get(i));
        for (int i = 0; i < size; i++)
            addChild(nodeSelection.get(i));
    }

}
