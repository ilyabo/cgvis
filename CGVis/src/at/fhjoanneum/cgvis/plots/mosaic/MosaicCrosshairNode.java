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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import at.fhjoanneum.cgvis.data.IPointSet;
import at.fhjoanneum.cgvis.plots.PValueTooltip;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * @author Ilya Boyandin
 */
public class MosaicCrosshairNode extends PNode {

    private static final long serialVersionUID = -5895460533063143552L;
    private static final Color TOOLTIP_BG_COLOR = new Color(0, 213, 213, 240);
    private static final Color CROSSHAIR_COLOR = new Color(255, 255, 255, 170);

    private final PCanvas canvas;
    private Point selection;
    private CGVisMosaicPlotNode selMosaicNode;
    private final PValueTooltip tooltipBox;

    public MosaicCrosshairNode(PCanvas canvas) {
        this.canvas = canvas;
        setPickable(false);

        tooltipBox = new PValueTooltip();
        tooltipBox.setPaint(TOOLTIP_BG_COLOR);
        addChild(tooltipBox);
        tooltipBox.setVisible(false);
    }

    public void showCrosshair(CGVisMosaicPlotNode mosaic, Point cell) {
        this.selMosaicNode = mosaic;
        this.selection = cell;
        setVisible(true);

        final IPointSet pointSet = mosaic.getPointSet();
        final double value = pointSet.getValue(cell.y, cell.x);

        final StringBuffer headerText = new StringBuffer();
        headerText.append(pointSet.getElementLabel(cell.y));
        headerText.append('\n');
        headerText.append(pointSet.getName());

        final StringBuffer labelText = new StringBuffer();
        labelText.append("Attribute: ");
        labelText.append('\n');
        labelText.append("Value: ");

        final StringBuffer valueText = new StringBuffer();
        valueText.append(pointSet.getAttributeLabel(cell.x));
        valueText.append('\n');
        valueText.append(value);

        final Rectangle cellRect = mosaic.cellToRect(cell);
        canvas.getCamera().viewToLocal(cellRect);

        tooltipBox.setPosition(cellRect.getMaxX() + 8, cellRect.getMaxY() + 8);
        tooltipBox.setText(headerText.toString(), labelText.toString(),
                valueText.toString());

        tooltipBox.setVisible(true);
    }

    public void hideCrosshair() {
        selMosaicNode = null;
        selection = null;
        setVisible(false);
        tooltipBox.setVisible(false);
    }

    @Override
    protected void paint(PPaintContext paintContext) {
        final Graphics2D g2 = paintContext.getGraphics();

        if (selection != null) {
            final PCamera camera = canvas.getCamera();
            // draw the lines
            final Rectangle2D cellRect = selMosaicNode.cellToRect(selection);
            {
                final PBounds cb = camera.getBounds();

                camera.localToView(cb);
                final int cx = (int) Math.round(cb.x);
                final int cy = (int) Math.round(cb.y);
                final int cw = (int) Math.round(cb.width);
                final int ch = (int) Math.round(cb.height);

                final AffineTransform oldTransform = g2.getTransform();
                g2.transform(camera.getViewTransform());

                final int y = (int) cellRect.getY();
                final int x = (int) cellRect.getX();
                final int w = (int) cellRect.getWidth();
                final int h = (int) cellRect.getHeight();

                g2.setColor(CROSSHAIR_COLOR);
                g2.fillRect(x, cy - 1, w, y - cy + 1);
                g2.fillRect(x, y + h, w, ch - (y - cy));
                g2.fillRect(cx - 1, y, x - cx + 1, h);
                g2.fillRect(x + w, y, cw - (x - cx), h);

                g2.setTransform(oldTransform);
            }

            // draw the small cell-bounding rectangle
            {
                camera.viewToLocal(cellRect);

                final int y = (int) cellRect.getY();
                final int x = (int) cellRect.getX();
                int w = (int) cellRect.getWidth();
                if (w < 1)
                    w = 1;
                int h = (int) cellRect.getHeight();
                if (h < 1)
                    h = 1;

                g2.setColor(Color.black);
                g2.drawRect(x, y, w - 1, h - 1);
            }
        }
    }

}
