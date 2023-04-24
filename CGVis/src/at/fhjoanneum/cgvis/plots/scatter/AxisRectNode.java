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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.log4j.Logger;

import at.fhjoanneum.cgvis.CGVis;
import at.fhjoanneum.cgvis.util.DoubleAxisMarks;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * 
 * @author Ilya Boyandin
 */
public class AxisRectNode extends PNode {

    private static final long serialVersionUID = -5613994377363710789L;

    private Logger logger = Logger.getLogger(getClass().getName());

    private static final Color STROKE_COLOR = new Color(50, 50, 50);
    private static final Color GRID_COLOR = new Color(50, 50, 50, 30);
    private static final Color ORIGIN_MARK_COLOR = new Color(120, 120, 120);

    private static final int AXE_MARK_SIZE = 7;
    private static final int ORIGIN_MARK_SIZE = 15;
    private static final int PADDING = 16;

    private static Font MARKS_FONT = new Font("Helvetica", Font.PLAIN, 11);
    private static NumberFormat NFORMAT = new DecimalFormat("0.0##########");

    private static final AffineTransform rotateLeft = new AffineTransform();
    private static final AffineTransform rotateRight = new AffineTransform();
    static {
        rotateLeft.setToRotation(-Math.PI / 2);
        rotateRight.setToRotation(Math.PI / 2);
    }

    private PPath boundsRect;
    private DoubleAxisMarks xAxisMarks, yAxisMarks;
    private Rectangle2D visiblePlot;
    private Rectangle2D visiblePlotVals;
    // private boolean isDirty;
    private boolean showGrid;

    public AxisRectNode() {
        xAxisMarks = new DoubleAxisMarks(40, 80);
        yAxisMarks = new DoubleAxisMarks(40, 80);

        boundsRect = PPath.createRectangle(0, 0, 1, 1);
        // boundsRect.setStroke(FIXED_STROKE);
        boundsRect.setStrokePaint(STROKE_COLOR);
        boundsRect.setPaint(null);
        // isDirty = true;
        addChild(boundsRect);
        setPickable(false);
        setChildrenPickable(false);
    }

    public boolean getShowGrid() {
        return showGrid;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
        repaint();
    }

    public int getPadding() {
        return PADDING;
    }

    public void update(Rectangle2D outerBounds, Rectangle2D visiblePlot,
            Rectangle2D visiblePlotVals) {
        // this.outerBounds = outerBounds;
        this.visiblePlot = visiblePlot;
        this.visiblePlotVals = visiblePlotVals;
        boundsRect.setBounds(outerBounds.getX() + PADDING, outerBounds.getY()
                + PADDING, outerBounds.getWidth() - PADDING * 2, outerBounds
                .getHeight()
                - PADDING * 2);
        setBounds(outerBounds);
        // isDirty = true;
        repaint();
    }

//    private transient Image buffer = null;
//    private transient int bufferWidth = -1, bufferHeight = -1;
//
//    private transient double cachedWidth = 0;
//    private transient double cachedHeight = 0;
//
//    @Override
//    protected void paint(PPaintContext pc) {
//        super.paint(pc);
//
//        if (visiblePlotVals != null) {
//            if (cachedWidth != outerBounds.getWidth()
//                    || cachedHeight != outerBounds.getHeight()) {
//                cachedWidth = outerBounds.getWidth();
//                cachedHeight = outerBounds.getHeight();
//                isDirty = true;
//            }
//
//            if (isDirty) {
//                final int w = (int) Math.round(outerBounds.getWidth()
//                        * pc.getScale() * 1.1);
//                final int h = (int) Math.round(outerBounds.getHeight()
//                        * pc.getScale() * 1.1);
//                if (bufferWidth < w || bufferHeight < h) {
//                    buffer = canvas.createImage(w, h);
//                    bufferWidth = w;
//                    bufferHeight = h;
//                }
//
//                paintTo((Graphics2D) buffer.getGraphics(), pc.getScale());
//                isDirty = false;
//            }
//            pc.getGraphics().drawImage(buffer, 0, 0, null);
//        }
//    }

    private int xValueToLocal(double xv, double scaleX) {
        return (int) Math.round(visiblePlot.getX()
                + (xv - visiblePlotVals.getX()) * scaleX);
    }

    private int yValueToLocal(double yv, double scaleY) {
        return (int) Math.round(visiblePlot.getY() + visiblePlot.getHeight()
                + (visiblePlotVals.getY() - yv) * scaleY);
    }

    protected void paint(PPaintContext pc) {
        super.paint(pc);

        final double pcScale = pc.getScale();
        final Graphics2D g2 = pc.getGraphics();

        boolean paintXLabels = true;
        boolean paintYLabels = true;

        final double maxX = visiblePlotVals.getMaxX();
        final double maxY = visiblePlotVals.getMaxY();
        final double minX = visiblePlotVals.getMinX();
        final double minY = visiblePlotVals.getMinY();

        if (maxX < minX) {
            paintXLabels = false;
        }
        if (maxY < minY) {
            paintYLabels = false;
        }
        if (!paintXLabels || /* && */!paintYLabels) {
            return;
        }

        double scaleX = 0;
        double scaleY = 0;

        // Draw axes
        g2.setColor(STROKE_COLOR);
        g2.setFont(MARKS_FONT);
        final FontMetrics fm = g2.getFontMetrics(MARKS_FONT);
        final int fontHeight = fm.getHeight();

        int xNumSteps = 0, yNumSteps = 0;
        double xvStep = 0, yvStep = 0;

        // final PBounds bounds = getBoundsReference();

        if (paintXLabels) {
            scaleX = (visiblePlot.getWidth() / visiblePlotVals.getWidth())
                    * pcScale;
            final double ordX = Math.abs(DoubleAxisMarks.ordAlpha(maxX - minX))
                    + Math.abs(DoubleAxisMarks.ordAlpha(Math.max(
                            Math.abs(maxX), Math.abs(minX))));
            final int xMaxStringW = Math.max(fm.stringWidth(NFORMAT
                    .format(ordX)), fm.stringWidth(Double.toString(ordX)));
            xAxisMarks.setMinDist(xMaxStringW + 10);
            xAxisMarks.setMaxDist(visiblePlot.getWidth() / 8);
            xAxisMarks.calc(minX, maxX, visiblePlot.getWidth());
            xvStep = xAxisMarks.getStep();
            xNumSteps = (xvStep > 0 ? (int) Math.ceil((maxX - xAxisMarks
                    .getStart())
                    / xvStep) : 1);

            final int xMaxNumSteps = (int) Math.ceil(visiblePlot.getWidth()
                    / xMaxStringW);
            if (xNumSteps > xMaxNumSteps) {
                paintXLabels = false;
                logger.warn("xNumSteps=" + xNumSteps
                        + ", should be not greater than " + xMaxNumSteps);
            }

            // g2.setClip((int)boundsRect.getX(), (int)bounds.y,
            // (int)boundsRect.getWidth(), (int)bounds.height);
            final int ty1 = (int) Math
                    .round((boundsRect.getY() - AXE_MARK_SIZE / 2) * pcScale);
            final int ty2 = (int) Math
                    .round((boundsRect.getY() + AXE_MARK_SIZE / 2) * pcScale);
            final int by1 = (int) Math.round((boundsRect.getY()
                    + boundsRect.getHeight() - AXE_MARK_SIZE / 2)
                    * pcScale);
            final int by2 = (int) Math.round((boundsRect.getY()
                    + boundsRect.getHeight() + AXE_MARK_SIZE / 2)
                    * pcScale);
            double xv = xAxisMarks.getStart();
            for (int i = 0; i < xNumSteps; i++) {
                final int x = (int) Math.round(visiblePlot.getX()
                        + (xv - visiblePlotVals.getX()) * pcScale * scaleX);

                final String str = NFORMAT.format(xv);
                final int w = fm.stringWidth(str);

                g2.setColor(STROKE_COLOR);

                g2.drawLine(x, ty1, x, ty2);
                g2.drawString(str, x - w / 2, ty1 - 2);

                g2.drawLine(x, by1, x, by2);
                g2.drawString(str, x - w / 2, by2 - 5 + fontHeight);

                xv += xvStep;
                if (xvStep <= 0)
                    break;
            }
            // g2.setClip(bounds);

        }

        if (paintYLabels) {
            scaleY = (visiblePlot.getHeight() / visiblePlotVals.getHeight())
                    * pcScale;
            final double ordY = Math.abs(DoubleAxisMarks.ordAlpha(maxY - minY))
                    + Math.abs(DoubleAxisMarks.ordAlpha(Math.max(
                            Math.abs(maxY), Math.abs(minY))));
            final int yMaxStringW = Math.max(fm.stringWidth(NFORMAT
                    .format(ordY)), fm.stringWidth(Double.toString(ordY)));
            yAxisMarks.setMinDist(yMaxStringW + 10);
            yAxisMarks.setMaxDist(visiblePlot.getHeight() / 8);
            yAxisMarks.calc(minY, maxY, visiblePlot.getHeight());
            yvStep = yAxisMarks.getStep();
            yNumSteps = (yvStep > 0 ? (int) Math.ceil((maxY - yAxisMarks
                    .getStart())
                    / yvStep) : 1);

            final int yMaxNumSteps = (int) Math.ceil(visiblePlot.getHeight()
                    / yMaxStringW);
            if (yNumSteps > yMaxNumSteps) {
                paintYLabels = false;
                logger.warn("yNumSteps=" + yNumSteps
                        + ", should be not greater than " + yMaxNumSteps);
            }

            // g2.setClip((int)bounds.x, (int)boundsRect.getY(),
            // (int)bounds.width, (int)boundsRect.getHeight());
            final int lx1 = (int) Math
                    .round((boundsRect.getX() - AXE_MARK_SIZE / 2) * pcScale);
            final int lx2 = (int) Math
                    .round((boundsRect.getX() + AXE_MARK_SIZE / 2) * pcScale);
            final int rx1 = (int) Math.round((boundsRect.getX()
                    + boundsRect.getWidth() - AXE_MARK_SIZE / 2)
                    * pcScale);
            final int rx2 = (int) Math.round((boundsRect.getX()
                    + boundsRect.getWidth() + AXE_MARK_SIZE / 2)
                    * pcScale);
            double yv = yAxisMarks.getStart();
            for (int i = 0; i < yNumSteps; i++) {
                final int y = (int) Math.round(visiblePlot.getY()
                        + visiblePlot.getHeight()
                        + (visiblePlotVals.getY() - yv) * pcScale * scaleY);

                g2.setColor(STROKE_COLOR);
                g2.drawLine(lx1, y, lx2, y);

                final String str = NFORMAT.format(yv);
                final int w = fm.stringWidth(str);

                final AffineTransform _transform = g2.getTransform();

                g2.transform(rotateLeft);
                g2.drawString(str, -y - w / 2, fontHeight - 4);
                g2.setTransform(_transform);

                g2.transform(rotateRight);
                g2.drawString(str, y - w / 2, -(int) Math.round(boundsRect
                        .getWidth()
                        + AXE_MARK_SIZE)
                        - fontHeight);
                g2.setTransform(_transform);

                g2.drawLine(rx1, y, rx2, y);

                yv = yv + yvStep;
                if (yvStep <= 0)
                    break;
            }
            // g2.setClip(bounds);
        }

        if (paintXLabels && paintYLabels) {
            // Draw origin mark
            g2.setColor(ORIGIN_MARK_COLOR);
            final int ox = xValueToLocal(0, scaleX);
            final int oy = yValueToLocal(0, scaleY);
            g2.drawLine(ox - ORIGIN_MARK_SIZE / 2, oy, ox + ORIGIN_MARK_SIZE
                    / 2, oy);
            g2.drawLine(ox, oy - ORIGIN_MARK_SIZE / 2, ox, oy
                    + ORIGIN_MARK_SIZE / 2);
        }

        if (showGrid) {
            int oldRenderQuality = -1;
            if (CGVis.IS_OS_MAC) {
                oldRenderQuality = pc.getRenderQuality();
                pc.setRenderQuality(PPaintContext.LOW_QUALITY_RENDERING);
            }

            if (paintXLabels) {
                // g2.setClip((int)boundsRect.getX(), (int)bounds.y,
                // (int)boundsRect.getWidth(), (int)bounds.height);
                double xv = xAxisMarks.getStart();
                final int ty1 = (int) Math
                        .round((boundsRect.getY() - AXE_MARK_SIZE / 2)
                                * pcScale);
                final int by2 = (int) Math.round((boundsRect.getY()
                        + boundsRect.getHeight() + AXE_MARK_SIZE / 2)
                        * pcScale);
                for (int i = 0; i < xNumSteps; i++) {
                    final int x = (int) Math.round(visiblePlot.getX()
                            + (xv - visiblePlotVals.getX()) * pcScale * scaleX);

                    g2.setColor(GRID_COLOR);
                    g2.drawLine(x, ty1, x, by2);

                    xv += xvStep;
                    if (xvStep <= 0)
                        break;
                }
                // g2.setClip(bounds);
            }

            if (paintYLabels) {
                // g2.setClip((int)bounds.x, (int)boundsRect.getY(),
                // (int)bounds.width, (int)boundsRect.getHeight());
                double yv = yAxisMarks.getStart();
                final int lx1 = (int) Math
                        .round((boundsRect.getX() - AXE_MARK_SIZE / 2)
                                * pcScale);
                final int rx2 = (int) Math.round((boundsRect.getX()
                        + boundsRect.getWidth() + AXE_MARK_SIZE / 2)
                        * pcScale);
                for (int i = 0; i < yNumSteps; i++) {
                    final int y = (int) Math.round(visiblePlot.getY()
                            + visiblePlot.getHeight()
                            + (visiblePlotVals.getY() - yv) * pcScale * scaleY);

                    g2.drawLine(lx1, y, rx2, y);

                    yv += yvStep;
                    if (yvStep <= 0)
                        break;
                }
                // g2.setClip(bounds);
            }

            if (CGVis.IS_OS_MAC) {
                pc.setRenderQuality(oldRenderQuality);
            }
        }

    }

}
