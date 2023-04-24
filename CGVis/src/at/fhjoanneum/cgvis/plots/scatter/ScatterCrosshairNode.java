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
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * @author Ilya Boyandin
 */
public class ScatterCrosshairNode extends PNode {

    private static final long serialVersionUID = 4271592732140183213L;
    private static final Color CROSSHAIR_COLOR = new Color(55, 55, 55, 170);
    private Point2D position;

    public ScatterCrosshairNode() {
        setPickable(false);
    }

    public void setCrosshairPosition(Point2D position) {
        if ((this.position == null && position != null)
                || (this.position != null && !this.position.equals(position))) {
            this.position = position;
            repaint();
        }
    }

    @Override
    protected void paint(PPaintContext paintContext) {
        super.paint(paintContext);

        if (position != null) {
            final Graphics2D g2 = paintContext.getGraphics();

            final PBounds cb = getBoundsReference();

            final int bx = (int) Math.round(cb.x);
            final int by = (int) Math.round(cb.y);
            final int bw = (int) Math.round(cb.width);
            final int bh = (int) Math.round(cb.height);

            final int x = (int) Math.round(position.getX());
            final int y = (int) Math.round(position.getY());

            g2.setColor(CROSSHAIR_COLOR);
            g2.drawLine(x, by, x, by + bh);
            g2.drawLine(bx, y, bx + bw, y);
        }
    }

}
