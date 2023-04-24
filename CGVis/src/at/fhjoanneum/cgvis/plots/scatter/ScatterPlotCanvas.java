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
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import at.fhjoanneum.cgvis.Constants;
import at.fhjoanneum.cgvis.ViewPreferences;
import at.fhjoanneum.cgvis.data.DataUID;
import at.fhjoanneum.cgvis.data.IPointSet;
import at.fhjoanneum.cgvis.plots.PInfoBox;
import at.fhjoanneum.cgvis.plots.PValueTooltip;
import at.fhjoanneum.cgvis.plots.PanHandler;
import at.fhjoanneum.cgvis.plots.ZoomHandler;
import at.fhjoanneum.cgvis.util.ColorUtils;
import at.fhjoanneum.cgvis.util.PiccoloUtils;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * 
 * @author Ilya Boyandin
 */
public class ScatterPlotCanvas extends PCanvas {

    private static final long serialVersionUID = 5804812866756087116L;

    public static final int ATTR_IDX_NONE = -1;
    public static final int ATTR_IDX_ELEMENT_ID = -2;

    private static final Color AXIS_TITLES_BGCOLOR = new Color(240, 240, 200,
            170);
    private static final Color AXIS_TITLES_FGCOLOR = Color.black;
    private static final int POINT_NODE_COLOR_ALPHA = 200;

    private static final Color DEFAULT_NODE_COLOR = new Color(69, 115, 190, 200);
    // private static final Color DEFAULT_NODE_COLOR = new Color(194, 194, 232,
    // 200);
    private static final Color NAN_NODE_COLOR = Color.darkGray;

    private IPointSet plotData;
    private PlotAreaNode plotAreaNode;
    private int xAttrIndex;
    private int yAttrIndex;
    private int rAttrIndex;
    private int cAttrIndex;
    private PlotAreaParams plotAreaParams;
    private boolean useAutofit = true;
    private AxisRectNode axisRectNode;
    private ViewPreferences preferences;
    private ScatterCrosshairNode crosshair;
    private PValueTooltip tooltipBox;
    private ZoomHandler zoomHandler;
    private PInfoBox xAxisTitle;
    private PInfoBox yAxisTitle;
    private boolean showCrosshair = false;
    private boolean snap = true;
    private boolean useRAttrAsSquare = false;
    private List<PointNode> nodeSelection;
    private ScatterPlotView view;
    private PointNode[] pointNodes;

    private static final AffineTransform yReflect;
    static {
        yReflect = new AffineTransform();
        yReflect.setToScale(1.0, -1.0);
    }

    public ScatterPlotCanvas(ScatterPlotView view, IPointSet plotData,
            int xAttrIndex, int yAttrIndex, int rAttrIndex, int cAttrIndex,
            ViewPreferences preferences) {
        this.view = view;
        this.plotData = plotData;
        this.xAttrIndex = xAttrIndex;
        this.yAttrIndex = yAttrIndex;
        this.rAttrIndex = rAttrIndex;
        this.cAttrIndex = cAttrIndex;
        this.preferences = preferences;

        nodeSelection = new ArrayList<PointNode>();

        final PCamera camera = getCamera();

        setBackground(new Color(212, 210, 199));
        camera.setViewTransform(yReflect);
        setDefaultRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
        setAnimatingRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
        setInteractingRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);

        final PLayer layer = getLayer();

        plotAreaNode = new PlotAreaNode(); // createPlotArea();
        layer.addChild(plotAreaNode);

        final PlotAreaParams pap = calcPlotAreaParams();
        setPlotAreaParams(pap);
        plotAreaNode.setBounds(pap.getBounds());

        final int size = plotData.getSize();
        pointNodes = new PointNode[size];
        for (int i = 0; i < size; i++) {
            // if (Double.isNaN(plotData.getValue(i, 2))) continue;
            final PointNode pnode = new PointNode(i);
            pointNodes[i] = pnode;
            plotAreaNode.addChild(pnode);
        }
        updateAllPointNodes();

        camera.setViewBounds(pap.outerBounds);

        // Axis rect
        axisRectNode = new AxisRectNode();
        axisRectNode.setShowGrid(true);
        camera.addChild(axisRectNode);

        // Axis captions
        xAxisTitle = createAxisTitleNode();
        camera.addChild(xAxisTitle);

        final AffineTransform rotateLeft = new AffineTransform();
        rotateLeft.setToRotation(-Math.PI / 2);
        yAxisTitle = createAxisTitleNode();
        yAxisTitle.setTransform(rotateLeft);
        camera.addChild(yAxisTitle);

        // Tooltip
        tooltipBox = new PValueTooltip();
        tooltipBox.setVisible(false);
        tooltipBox.setPickable(false);
        camera.addChild(tooltipBox);

        // Crosshair
        crosshair = new ScatterCrosshairNode();
        crosshair.setVisible(false);
        camera.addChild(crosshair);

        updateAxis();

        // Listeners
        plotAreaNode.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (PlotAreaNode.PROPERTY_FULL_BOUNDS == evt.getPropertyName()) {
                    updateAxis();
                    updateAxisTitles();
                }
            }
        });

        camera.addInputEventListener(new CameraMouseHandler());
        camera.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (PCamera.PROPERTY_VIEW_TRANSFORM == evt.getPropertyName()) {
                    updateAxis();
                    updateCrosshair(null);
                    hideTooltip();
                } else if (PCamera.PROPERTY_BOUNDS == evt.getPropertyName()) {
                    crosshair.setBounds(getCamera().getBoundsReference());
                }
            }
        });

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                updateAxis();
                updateAxisTitles();
                if (useAutofit) {
                    fitInCameraView(false);
                }
                hideCrosshair();
                hideTooltip();
            }
        });

        setZoomEventHandler(null);
        zoomHandler = new ZoomHandler();
        addInputEventListener(zoomHandler);
        final PanHandler panHandler = new PanHandler();
        setPanEventHandler(panHandler);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                case KeyEvent.VK_ESCAPE:
                    clearSelection();
                    break;
                case 'e':
                case 'E':
                    if (e.isControlDown()) {
                        selectAll();
                    }
                    break;
                }
            }
        });
    }

    private PInfoBox createAxisTitleNode() {
        final PInfoBox axisTitle = new PInfoBox(10, 50);
        axisTitle.setPadding(3, 1);
        axisTitle.setPaint(AXIS_TITLES_BGCOLOR);
        axisTitle.setTextPaint(AXIS_TITLES_FGCOLOR);
        return axisTitle;
    }

    private void updateAxisTitles() {
        xAxisTitle.setText("X: " + plotData.getAttributeLabel(xAttrIndex));
        final PBounds vb = getCamera().getBoundsReference();
        final PBounds bx = xAxisTitle.getBoundsReference();
        xAxisTitle.setPosition(vb.getMaxX() - axisRectNode.getPadding()
                - bx.getWidth() - 1, vb.getMaxY() - axisRectNode.getPadding()
                - bx.getHeight() - 1);

        final PBounds by = yAxisTitle.getBoundsReference();
        yAxisTitle.setText("Y: " + plotData.getAttributeLabel(yAttrIndex));
        yAxisTitle.setPosition(-vb.getMinY() - axisRectNode.getPadding()
                - by.getWidth() - 1, vb.getMinX() + axisRectNode.getPadding()
                + 1);
    }

    public IPointSet getPlotData() {
        return plotData;
    }

    public void setPlotData(IPointSet plotData) {
        this.plotData = plotData;
    }

    public int getRAttrIndex() {
        return rAttrIndex;
    }

    public void setRAttrIndex(int attrIndex) {
        rAttrIndex = attrIndex;
        updateAttrs();
    }

    public int getXAttrIndex() {
        return xAttrIndex;
    }

    public void setXAttrIndex(int attrIndex) {
        xAttrIndex = attrIndex;
        updateAttrs();
        updateAxis();
        updateAxisTitles();
    }

    public int getYAttrIndex() {
        return yAttrIndex;
    }

    public void setYAttrIndex(int attrIndex) {
        yAttrIndex = attrIndex;
        updateAttrs();
        updateAxis();
        updateAxisTitles();
    }

    public int getCAttrIndex() {
        return cAttrIndex;
    }

    private void updateAttrs() {
        final PlotAreaParams newPas = calcPlotAreaParams();
        animateAttrChange(newPas);
        if (useAutofit) {
            final PCamera camera = getCamera();
            final Rectangle2D newBounds = newPas.outerBounds;
            if (!camera.getBounds().equals(newBounds)) {
                // camera.animateViewToCenterBounds(newBounds, true,
                // preferences.getAnimationsDuration());
                final int pad = axisRectNode.getPadding() + 2;
                final Insets padding = new Insets(-pad, pad, -pad, pad);
                PiccoloUtils.animateViewToPaddedBounds(getCamera(), newBounds,
                        padding, preferences.getAnimationsDuration());
            }
        }
        setPlotAreaParams(newPas);
    }

    private void setPlotAreaParams(PlotAreaParams plotAreaParams) {
        this.plotAreaParams = plotAreaParams;
    }

    public void setCAttrIndex(int attrIndex) {
        if (attrIndex != this.cAttrIndex) {
            this.cAttrIndex = attrIndex;
            final PlotAreaParams newPas = calcPlotAreaParams();
            animateColorChange(newPas);
            setPlotAreaParams(newPas);
            updateAxis();
        }
    }

    public void setAutofit(boolean b) {
        this.useAutofit = b;
    }

    public boolean getAutofit() {
        return useAutofit;
    }

    /*
     * public void setUseElementIndexAsCAttr(boolean value) { if (value !=
     * this.useElementIndexAsCAttr) { this.useElementIndexAsCAttr = value; //
     * this.cAttrIndex = ATTR_IDX_ELEMENT_NONE; final PlotAreaParams newPas =
     * calcPlotAreaParams(); animateColorChange(newPas);
     * setPlotAreaParams(newPas); } }
     * 
     * public boolean getUseElementIndexAsCAttr() { return
     * useElementIndexAsCAttr; }
     */

    private void animateColorChange(PlotAreaParams pap) {
        for (int i = 0, n = plotAreaNode.getChildrenCount(); i < n; i++) {
            final PointNode pnode = (PointNode) plotAreaNode.getChild(i);
            final Color newColor = calcPointNodeColor(pnode, pap);
            if (preferences.useAnimation()) {
                final PActivity activity = pnode.animateToColor(newColor,
                        preferences.getShortAnimationsDuration());
                pnode.addActivity(activity);
            } else {
                pnode.setPaint(newColor);
            }
            // updatePointNode(pnode);
        }
    }

    private void animateAttrChange(PlotAreaParams pap) {
        hideCrosshair();
        hideTooltip();
        final Rectangle2D pb = pap.getBounds();
        if (preferences.useAnimation()) {
            plotAreaNode.addActivity(plotAreaNode.animateToBounds(pb.getX(), pb
                    .getY(), pb.getWidth(), pb.getHeight(), preferences
                    .getAnimationsDuration()));
        } else {
            plotAreaNode.setBounds(pb);
        }

        for (PointNode pnode : (List<PointNode>) plotAreaNode
                .getChildrenReference()) {
            final Rectangle2D.Double b = calcPointNodeBounds(pnode, pap);
            if (preferences.useAnimation()) {
                final PActivity activity = pnode.animateToBounds(b.x, b.y,
                        b.width, b.height, preferences.getAnimationsDuration());
                pnode.addActivity(activity);
            } else {
                pnode.setBounds(b);
            }
        }
    }

    private Color calcPointNodeColor(PointNode pointNode, PlotAreaParams pap) {
        Color c = null;
        final int eltIndex = pointNode.getElementIndex();

        switch (cAttrIndex) {

        case ATTR_IDX_NONE:
            c = DEFAULT_NODE_COLOR;
            break;

        case ATTR_IDX_ELEMENT_ID:
            c = ColorUtils.getGradientColor((float) eltIndex
                    / plotData.getSize(), 0, 1);
            if (POINT_NODE_COLOR_ALPHA < 255) {
                c = new Color(c.getRed(), c.getGreen(), c.getBlue(),
                        POINT_NODE_COLOR_ALPHA);
            }
            break;

        default:
            final double vc = plotData.getValue(eltIndex, cAttrIndex);

            if (Double.isNaN(vc)) {
                c = NAN_NODE_COLOR; // TODO: NaN color -> change form
            } else {
                if (pap.maxC > pap.minC) {
                    c = ColorUtils.getGradientColor(
                            (float) ((vc - pap.minC) / (pap.maxC - pap.minC)),
                            Color.green, Color.yellow, Color.red,
                            POINT_NODE_COLOR_ALPHA);
                } else {
                    c = DEFAULT_NODE_COLOR;
                }
            }
        }

        return c;
    }

    private static final double MIN_R = .5;

    private Rectangle2D.Double calcPointNodeBounds(PointNode pointNode,
            PlotAreaParams pap) {
        final int eltIndex = pointNode.getElementIndex();

        final double vx = plotData.getValue(eltIndex, xAttrIndex);
        final double vy = plotData.getValue(eltIndex, yAttrIndex);

        double r;

        if (rAttrIndex >= 0) {
            final double vr = getElementRadius(eltIndex);
            if (Double.isNaN(vr)) {
                // r = .5 * pap.scaleR; // TODO: NaN radius -> change node form
                r = MIN_R;
            } else {
                r = MIN_R + (vr + pap.minR) * pap.scaleR;
                // TODO: handle negative and 0 radius
                // r = (1.0 + vr - pap.minR) * pap.scaleR;
            }
        } else {
            r = pap.defaultR;
        }

        double x = vx * pap.scaleX - r;
        double y = vy * pap.scaleY - r;

        final Rectangle2D b = pap.getBounds();
        if (Double.isNaN(vx)) {
            x = b.getX() - pap.maxRplus - r;
        }
        if (Double.isNaN(vy)) {
            y = b.getY() + b.getHeight() + pap.maxRplus - r;
        }

        return new Rectangle2D.Double(x, y, r * 2, r * 2);
    }

    private double getElementRadius(final int eltIndex) {
        final double vr;
        if (useRAttrAsSquare) {
            vr = Math.sqrt(plotData.getValue(eltIndex, rAttrIndex) / Math.PI);
        } else {
            vr = plotData.getValue(eltIndex, rAttrIndex);
        }
        return vr;
    }

    private void updateAllPointNodes() {
        for (PointNode pnode : (List<PointNode>) plotAreaNode
                .getChildrenReference()) {
            pnode.setBounds(calcPointNodeBounds(pnode, plotAreaParams));
            pnode.setPaint(calcPointNodeColor(pnode, plotAreaParams));
        }
    }

    private PlotAreaParams calcPlotAreaParams() {
        final PlotAreaParams pap = new PlotAreaParams();

        final int size = plotData.getSize();

        pap.minX = Double.NaN;
        pap.maxX = Double.NaN;
        pap.minY = Double.NaN;
        pap.maxY = Double.NaN;

        if (rAttrIndex >= 0) {
            pap.minR = Double.NaN;
            pap.maxR = Double.NaN;
        } else {
            pap.minR = -1.0;
            pap.maxR = 1.0;
        }
        if (cAttrIndex == ATTR_IDX_ELEMENT_ID) {
            pap.minC = 0;
            pap.maxC = size - 1;
        } else {
            if (cAttrIndex >= 0) {
                pap.minC = Double.NaN;
                pap.maxC = Double.NaN;
            } else {
                pap.minC = pap.maxC = 0;
            }
        }

        for (int i = 0; i < size; i++) {
            final double x = plotData.getValue(i, xAttrIndex);
            if (Double.isNaN(pap.minX) || x < pap.minX)
                pap.minX = x;
            if (Double.isNaN(pap.maxX) || x > pap.maxX)
                pap.maxX = x;

            final double y = plotData.getValue(i, yAttrIndex);
            if (Double.isNaN(pap.minY) || y < pap.minY)
                pap.minY = y;
            if (Double.isNaN(pap.maxY) || y > pap.maxY)
                pap.maxY = y;

            if (rAttrIndex >= 0) {
                final double r = getElementRadius(i);
                if (!Double.isNaN(r)) {
                    if (Double.isNaN(pap.minR) || r < pap.minR) {
                        pap.minR = r;
                    }
                    if (Double.isNaN(pap.maxR) || r > pap.maxR) {
                        pap.maxR = r;
                    }
                }
            }

            if (cAttrIndex >= 0) {
                final double c = plotData.getValue(i, cAttrIndex);
                if (Double.isNaN(pap.minC) || c < pap.minC)
                    pap.minC = c;
                if (Double.isNaN(pap.maxC) || c > pap.maxC)
                    pap.maxC = c;
            }
        }

        if (Double.isNaN(pap.minR) || Double.isNaN(pap.maxR)) {
            // all the R values are NaNs
            rAttrIndex = ATTR_IDX_NONE;
        }
        if (Double.isNaN(pap.minC) || Double.isNaN(pap.maxC)) {
            // all the C values are NaNs
            cAttrIndex = ATTR_IDX_NONE;
        }

        if (Double.isNaN(pap.minX) || Double.isNaN(pap.maxX)) {
            pap.minX = 0.0;
            pap.maxX = 1.0;
        }
        if (Double.isNaN(pap.minY) || Double.isNaN(pap.maxY)) {
            pap.minY = 0.0;
            pap.maxY = 1.0;
        }

        final boolean doAutoScale = true;
        double bx, by, bw, bh;

        final int numNodesToFit;
        if (rAttrIndex == ATTR_IDX_NONE) {
            numNodesToFit = 100;
        } else {
            numNodesToFit = 25;
        }

        if (doAutoScale) {
            pap.scaleR = 1.0;
            if (rAttrIndex == ATTR_IDX_NONE) {
                pap.maxRplus = MIN_R;
            } else {
                pap.maxRplus = MIN_R + (pap.maxR + pap.minR) * pap.scaleR;
            }
            pap.scaleX = numNodesToFit * pap.maxRplus / (pap.maxX - pap.minX);
            pap.scaleY = numNodesToFit * pap.maxRplus / (pap.maxY - pap.minY);

            bw = (numNodesToFit + 4) * pap.maxRplus * pap.scaleR; // 4 for the
                                                                    // marin
            bh = (numNodesToFit + 4) * pap.maxRplus * pap.scaleR;

            bx = pap.minX * pap.scaleX - pap.maxRplus * pap.scaleR * 2;
            by = pap.minY * pap.scaleY - pap.maxRplus * pap.scaleR * 2;

            /*
             * } else {
             * 
             * pap.scaleX = 1.0; pap.scaleY = 1.0; // if (rAttrIndex ==
             * ATTR_IDX_ELEMENT_NONE) { // pap.maxRplus = MIN_R; // } else { //
             * pap.maxRplus = MIN_R + (pap.maxR + pap.minR); // }
             * 
             * pap.scaleR = 1.0 / (pap.maxR - pap.minR);
             * 
             * bx = pap.minX * pap.scaleX - pap.maxRplus * pap.scaleR * 2 - 1;
             * by = pap.minY * pap.scaleY - pap.maxRplus * pap.scaleR * 2 - 1;
             * bw = pap.maxX - pap.minX + 2; bh = pap.maxY - pap.minY + 2;
             */
        }

        if (rAttrIndex == ATTR_IDX_NONE) {
            // pap.defaultR = pap.maxRplus;
            pap.defaultR = .5;
        }

        pap.setBounds(new Rectangle2D.Double(bx, by, bw, bh));
        pap.outerBounds = new Rectangle2D.Double(bx - pap.maxRplus * pap.scaleR
                * 2, by - pap.maxRplus * pap.scaleR * 2, bw + pap.maxRplus
                * pap.scaleR * 4, bh + pap.maxRplus * pap.scaleR * 4);

        return pap;
    }

    private Rectangle2D calcOriginMarkBounds(PlotAreaParams pap) {
        final Rectangle2D plotBounds = pap.getBounds();
        final double os = Math.min(plotBounds.getWidth(), plotBounds
                .getHeight()) * .1;
        return new Rectangle2D.Double(-os / 4, -os / 4, os / 2, os / 2);
    }

    private Rectangle2D plotAreaToValues(Rectangle2D src, PlotAreaParams pap) {
        return new Rectangle2D.Double(src.getX() / pap.scaleX, src.getY()
                / pap.scaleY, src.getWidth() / pap.scaleX, src.getHeight()
                / pap.scaleY);
    }

    private PointNode findNearestPointNode(Point2D point) {
        PointNode nearestNode = null;
        PointNode inNearestNode = null;
        double minDist = Double.MAX_VALUE;
        double inMinDist = Double.MAX_VALUE;

        final PCamera camera = getCamera();
        point = camera.localToView(new Point2D.Double(point.getX(), point
                .getY()));
        final PBounds viewBounds = camera.getViewBounds();

        final List<PNode> children = plotAreaNode.getChildrenReference();
        for (PNode child : children) {
            if (child instanceof PointNode && child.getVisible()
                    && child.intersects(viewBounds)) {
                final PointNode pnode = (PointNode) child;

                final PBounds b = pnode.getBoundsReference();
                final double x = b.getCenterX();
                final double y = b.getCenterY();

                final double px = (x - point.getX());
                final double py = (y - point.getY());
                final double r = b.getWidth() / 2;
                // final double dist = Math.sqrt(px * px + py * py) - r;
                final double dist = Math.sqrt(px * px + py * py);

                if (dist < minDist) {
                    minDist = dist;
                    nearestNode = pnode;
                }

                if (dist < r) {
                    if (dist < inMinDist) {
                        // works in case of a big circle containing smaller ones
                        inMinDist = dist;
                        inNearestNode = pnode;
                    }
                }
            }
        }

        if (nearestNode != null) {
            if (minDist >= nearestNode.getBoundsReference().getWidth() / 2
                    && minDist >= Math.min(viewBounds.getWidth(), viewBounds
                            .getHeight()) / 10.0) {
                if (inNearestNode != null) {
                    nearestNode = inNearestNode;
                } else {
                    nearestNode = null;
                }
            }
        }

        return nearestNode;
    }

    public void updateAxis() {
        final PCamera camera = getCamera();
        final PBounds cb = camera.getBounds();
        new Rectangle2D.Double();
        final Rectangle2D plotArea = (Rectangle2D) plotAreaParams.getBounds()
                .clone();
        camera.viewToLocal(plotArea);
        final Rectangle2D insec = cb.createIntersection(plotArea);
        // insec = (Rectangle2D)plotArea.clone();
        Rectangle2D vals = plotAreaToValues(camera
                .localToView((Rectangle2D) insec.clone()), plotAreaParams);
        axisRectNode.update(cb, insec, vals);
    }

    private transient PointNode nearestNode = null;

    private void updateCrosshair(PInputEvent event) {
        if (!showCrosshair) {
            return;
        }

        if (snap) {
            final PointNode pnode;
            // if (event != null) {
            // pnode = findNearestPointNode(event.getCanvasPosition());
            // } else {
            // pnode = nearestNode;
            // }
            pnode = nearestNode;

            if (pnode != null) {
                final PBounds b = pnode.getBounds();
                zoomHandler.setViewZoomPoint(new Point2D.Double(b.getCenterX(),
                        b.getCenterY()));

                final PCamera camera = getCamera();
                final Point2D pos = new Point2D.Double(b.getCenterX(), b
                        .getCenterY());
                camera.viewToLocal(pos);
                crosshair.setCrosshairPosition(pos);

                // tooltipBox.setText(getToolTipString(pnode));
                final PBounds vb = camera.getBoundsReference();
                final PBounds tb = tooltipBox.getBoundsReference();
                double x = pos.getX() + 8;
                double y = pos.getY() + 8;
                if (x + tb.getWidth() > vb.getWidth()) {
                    final double _x = pos.getX() - tb.getWidth() - 8;
                    if (vb.getX() - _x < x + tb.getWidth() - vb.getMaxX()) {
                        x = _x;
                    }
                }
                if (y + tb.getHeight() > vb.getHeight()) {
                    final double _y = pos.getY() - tb.getHeight() - 8;
                    if (vb.getY() - _y < y + tb.getHeight() - vb.getMaxY()) {
                        y = _y;
                    }
                }
                // tooltipBox.setPosition(x, y);

                // nearestNode = pnode;
                crosshair.setVisible(true);
                // tooltipBox.setVisible(true);
            } else {
                crosshair.setCrosshairPosition(null);
                crosshair.setVisible(false);
                // tooltipBox.setVisible(false);
                // nearestNode = null;
            }
        } else {
            if (event != null) {
                final Point2D pos = event.getPosition();
                final PCamera camera = getCamera();
                camera.viewToLocal(pos);
                crosshair.setCrosshairPosition(pos);
                crosshair.setVisible(true);
            }
        }
    }

    private void updateNearestNode(PInputEvent event) {
        final PointNode newNearestNode = findNearestPointNode(event
                .getCanvasPosition());
        if (newNearestNode != nearestNode) {
            if (nearestNode != null) {
                nearestNode.setHighlighted(false);
            }
            nearestNode = newNearestNode;
            if (nearestNode != null) {
                nearestNode.setHighlighted(true);
                plotAreaNode.bringToFront(nearestNode);
            }
        }
    }

    private void updateTooltip(PInputEvent event) {
        if (zoomHandler.isDragging() || nearestNode == null) {
            tooltipBox.setVisible(false);
            return;
        }
        final PBounds b = nearestNode.getBounds();
        zoomHandler.setViewZoomPoint(new Point2D.Double(b.getCenterX(), b
                .getCenterY()));

        final PCamera camera = getCamera();
        final Point2D pos = new Point2D.Double(b.getCenterX(), b.getCenterY());
        camera.viewToLocal(pos);
        crosshair.setCrosshairPosition(pos);

        updateToolTipString(nearestNode);

        final PBounds vb = camera.getBoundsReference();
        final PBounds tb = tooltipBox.getBoundsReference();
        double x = pos.getX() + 8;
        double y = pos.getY() + 8;
        if (x + tb.getWidth() > vb.getWidth()) {
            final double _x = pos.getX() - tb.getWidth() - 8;
            if (vb.getX() - _x < x + tb.getWidth() - vb.getMaxX()) {
                x = _x;
            }
        }
        if (y + tb.getHeight() > vb.getHeight()) {
            final double _y = pos.getY() - tb.getHeight() - 8;
            if (vb.getY() - _y < y + tb.getHeight() - vb.getMaxY()) {
                y = _y;
            }
        }
        tooltipBox.setPosition(x, y);
        tooltipBox.setVisible(true);
    }

    private void updateToolTipString(PointNode pointNode) {
        final int eltIndex = pointNode.getElementIndex();
        final NumberFormat fmt = Constants.TOOLTIP_NUMBER_FORMAT;

        final StringBuffer headerText = new StringBuffer();
        headerText.append(plotData.getElementLabel(eltIndex));

        final StringBuffer labelText = new StringBuffer();
        final StringBuffer valueText = new StringBuffer();

        labelText.append(plotData.getAttributeLabel(xAttrIndex));
        labelText.append(": ");
        labelText.append('\n');
        final double vx = plotData.getValue(eltIndex, xAttrIndex);
        valueText.append(Double.isNaN(vx) ? "NaN" : fmt.format(vx));
        valueText.append('\n');

        labelText.append(plotData.getAttributeLabel(yAttrIndex));
        labelText.append(": ");
        labelText.append('\n');
        final double vy = plotData.getValue(eltIndex, yAttrIndex);
        valueText.append(Double.isNaN(vy) ? "NaN" : fmt.format(vy));
        valueText.append('\n');

        if (rAttrIndex >= 0) {
            labelText.append(plotData.getAttributeLabel(rAttrIndex));
            labelText.append(": ");
            labelText.append('\n');
            final double vr = plotData.getValue(eltIndex, rAttrIndex);
            valueText.append(Double.isNaN(vr) ? "NaN" : fmt.format(vr));
            valueText.append('\n');
        }
        if (cAttrIndex >= 0) {
            labelText.append(plotData.getAttributeLabel(cAttrIndex));
            labelText.append(": ");
            labelText.append('\n');
            final double vc = plotData.getValue(eltIndex, cAttrIndex);
            valueText.append(Double.isNaN(vc) ? "NaN" : fmt.format(vc));
            valueText.append('\n');
        } else if (cAttrIndex == ATTR_IDX_ELEMENT_ID) {
            labelText.append("Element#: ");
            labelText.append('\n');
            valueText.append(eltIndex);
            valueText.append('\n');
        }

        tooltipBox.setText(headerText.toString(), labelText.toString(),
                valueText.toString());
    }

    private void hideCrosshair() {
        crosshair.setVisible(false);
        // tooltipBox.setVisible(false);
        // nearestNode = null;
    }

    private void hideTooltip() {
        tooltipBox.setVisible(false);
    }

    class CameraMouseHandler extends PBasicInputEventHandler {

        @Override
        public void mouseEntered(PInputEvent event) {
            updateNearestNode(event);
            updateCrosshair(event);
            updateTooltip(event);
        }

        @Override
        public void mouseMoved(PInputEvent event) {
            updateNearestNode(event);
            updateCrosshair(event);
            updateTooltip(event);
        }

        @Override
        public void mouseDragged(PInputEvent event) {
            updateNearestNode(event);
            updateCrosshair(event);
            updateTooltip(event);
        }

        @Override
        public void mouseExited(PInputEvent event) {
            hideCrosshair();
            hideTooltip();
        }

        @Override
        public void mouseClicked(PInputEvent event) {
            zoomHandler.setViewZoomPoint(event.getPosition());
            if (nearestNode != null) {
                if (nearestNode.isSelected()) {
                    final int size = getSelection().size();
                    clearSelection();
                    if (size > 1) {
                        selectNode(nearestNode);
                    }
                } else {
                    if (!event.isControlDown()) {
                        for (PointNode node : nodeSelection) {
                            node.setSelected(false);
                        }
                        nodeSelection.clear();
                    }
                    selectNode(nearestNode);
                }
            }
        }
    }

    public List<PointNode> getSelection() {
        return nodeSelection;
    }

    public void clearSelection() {
        for (PointNode pnode : nodeSelection) {
            pnode.setSelected(false);
        }
        nodeSelection.clear();
        fireElementSelectionChanged();
    }

    public void selectAll() {
        for (int i = 0, size = pointNodes.length; i < size; i++) {
            final PointNode pn = pointNodes[i];
            if (!nodeSelection.contains(pn)) {
                pn.setSelected(true);
                nodeSelection.add(pn);
            }
        }
        fireElementSelectionChanged();
    }

    /*
     * private void deselectNode(PointNode pnode) { nodeSelection.remove(pnode);
     * pnode.setSelected(false); fireElementSelectionChanged(); }
     */

    private void selectNode(PointNode pnode) {
        if (!nodeSelection.contains(pnode)) {
            pnode.setSelected(true);
            nodeSelection.add(pnode);
            plotAreaNode.bringToFront(pnode);
            fireElementSelectionChanged();
        }
    }

    public void setElementSelection(DataUID[] selection) {
        // clear the old selection
        for (int i = 0, size = nodeSelection.size(); i < size; i++) {
            nodeSelection.get(i).setSelected(false);
        }
        nodeSelection.clear();

        // set the new selection
        for (int i = 0, size = selection.length; i < size; i++) {
            final int elemIndex = plotData.getElementById(selection[i]);
            if (elemIndex != -1) {
                final PointNode node = pointNodes[elemIndex];
                node.setSelected(true);
                nodeSelection.add(node);
            }
        }
        plotAreaNode.bringToFront(nodeSelection);
    }

    public DataUID[] getSelectionUIDs() {
        final int size = nodeSelection.size();
        final DataUID[] sel = new DataUID[size];
        for (int i = 0; i < size; i++) {
            sel[i] = plotData.getElementId(nodeSelection.get(i)
                    .getElementIndex());
        }
        return sel;
    }

    private void fireElementSelectionChanged() {
        view.fireElementSelectionChanged(getSelectionUIDs());
    }

    public void fitInCameraView(boolean animate) {
        final long duration;
        if (animate && preferences.useAnimation()) {
            duration = preferences.getShortAnimationsDuration();
        } else {
            duration = 0;
        }
        final int pad = axisRectNode.getPadding() + 2;
        final Insets padding = new Insets(-pad, pad, -pad, pad);
        PiccoloUtils.animateViewToPaddedBounds(getCamera(),
                plotAreaParams.outerBounds, padding, duration);
    }

    public void centerView(boolean animate) {
        final Rectangle2D newBounds = plotAreaParams.getBounds();
        final PCamera camera = getCamera();
        if (animate && preferences.useAnimation()) {
            camera.animateViewToCenterBounds(newBounds, false, preferences
                    .getShortAnimationsDuration());
        } else {
            final PBounds vb = camera.getViewBounds();
            camera.setViewBounds(new Rectangle2D.Double(newBounds.getCenterX()
                    - vb.getWidth() / 2, newBounds.getCenterY()
                    - vb.getHeight() / 2, vb.getWidth(), vb.getHeight()));
        }
    }

    public void showOrigin(boolean animate) {
        final Rectangle2D ob = plotAreaParams.outerBounds;

        final double ox = ob.getX();
        final double oy = ob.getY();
        final double ow = ob.getWidth();
        final double oh = ob.getHeight();

        final double x, y, w, h;

        if (!plotAreaParams.getBounds().contains(0, 0)) {
            final Rectangle2D omb = calcOriginMarkBounds(plotAreaParams);
            x = Math.min(ox, 0) - omb.getWidth() * 2;
            y = Math.min(oy, 0) - omb.getHeight() * 2;
            w = Math.max(ox + ow, 0) - x + omb.getWidth() * 4;
            h = Math.max(oy + oh, 0) - y + omb.getHeight() * 4;
        } else {
            x = ox;
            y = oy;
            w = ow;
            h = oh;
        }

        final long duration;
        if (animate && preferences.useAnimation()) {
            duration = preferences.getShortAnimationsDuration();
        } else {
            duration = 0;
        }
        final int pad = axisRectNode.getPadding() + 2;
        final Insets padding = new Insets(-pad, pad, -pad, pad);
        PiccoloUtils.animateViewToPaddedBounds(getCamera(),
                new Rectangle2D.Double(x, y, w, h), padding, duration);
    }

    public void setShowCrosshair(boolean value) {
        if (value != showCrosshair) {
            if (value) {
                updateCrosshair(null);
            } else {
                hideCrosshair();
            }
            showCrosshair = value;
            repaint();
        }
    }

    public boolean getShowCrosshair() {
        return showCrosshair;
    }

    public boolean getSnap() {
        return snap;
    }

    public void setSnap(boolean snap) {
        if (snap != this.snap) {
            this.snap = snap;
            updateCrosshair(null);
        }
    }

    public void setShowGrid(boolean value) {
        axisRectNode.setShowGrid(value);
    }

    public boolean getShowGrid() {
        return axisRectNode.getShowGrid();
    }

}
