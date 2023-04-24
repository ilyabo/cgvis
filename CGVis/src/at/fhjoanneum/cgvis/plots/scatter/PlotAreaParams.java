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

import java.awt.geom.Rectangle2D;

class PlotAreaParams {
    double minX;
    double maxX;
    double maxY;
    double minY;
    double minR;
    double maxR;
    double maxRplus;
    double minC;
    double maxC;
    double defaultR;
    double scaleR;
    double scaleX;
    double scaleY;
    private Rectangle2D bounds;
    Rectangle2D outerBounds;

    public void setBounds(Rectangle2D bounds) {
        this.bounds = bounds;
    }

    public Rectangle2D getBounds() {
        return bounds;
    }
}