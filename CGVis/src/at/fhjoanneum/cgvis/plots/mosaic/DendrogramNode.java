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

import at.fhjoanneum.cgvis.plots.AbstractFloatingPanelNode;
import ch.unifr.dmlib.cluster.ClusterNode;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * @author Ilya Boyandin
 */
public class DendrogramNode extends AbstractFloatingPanelNode {

    private static final long serialVersionUID = 6823191433599073267L;
    private static final Color FG_COLOR = new Color(90, 90, 90);
    private static final Color BG_COLOR = new Color(255, 255, 255, 190);

    private static final int STEP_WIDTH = CGVisMosaicPlotNode.SQUARE_WIDTH
            + CGVisMosaicPlotNode.SPACING;
    private static final int STEP_HEIGHT = CGVisMosaicPlotNode.SQUARE_HEIGHT
            + CGVisMosaicPlotNode.SPACING;
    private static final int MIN_JOINT_SIZE = 3;

    private ClusterNode[] rootClusters;
    private final int maxChildNesting;
    private double treeLength;
    private CGVisMosaicPlotNode[] mosaicNodes;

    public DendrogramNode(CGVisMosaicPlotNode[] mosaicNodes,
            ClusterNode[] rootClusters, boolean isHorizontal) {
        super(isHorizontal);
        this.mosaicNodes = mosaicNodes;
        this.rootClusters = rootClusters;
        this.maxChildNesting = calcMaxChildNesting(rootClusters);
        setRenderQuality(PPaintContext.LOW_QUALITY_RENDERING);
        setPaint(BG_COLOR);
        updateTreeLength();
    }

    private int calcMaxChildNesting(ClusterNode[] rootClusters) {
        int mn = 0;
        for (ClusterNode rc : rootClusters) {
            final int nesting = rc.getChildNesting();
            if (nesting > mn) {
                mn = nesting;
            }
        }
        return mn;
    }

    private void updateTreeLength() {
        double maxSize = 0;
        for (int i = 0, levels = maxChildNesting; i < levels; i++) {
            final double size = calcJointSize(i);
            if (maxSize < size) {
                maxSize = size;
            }
        }
        treeLength = maxSize + 1;
        if (isHorizontal()) {
            setHeight(Math.ceil(treeLength));
        } else {
            setWidth(Math.ceil(treeLength));
        }
    }

    private final double calcJointSize(int level) {
        final int l = (rootClusters[0].getChildNesting() - level + 1);
        return MIN_JOINT_SIZE * Math.pow(l, 1.1);
        // return (MIN_JOINT_HEIGHT * Math.log(10 *
        // (rootCluster.getChildNesting() - level) + Math.E));
    }

    @Override
    public boolean setBounds(double x, double y, double width, double height) {
        return super.setBounds(x, y, width, height);
    }

    @Override
    protected int getContentWidth() {
        if (mosaicNodes == null || mosaicNodes.length == 0) {
            return 0;
        }
        if (isHorizontal()) {
            final PBounds last = mosaicNodes[mosaicNodes.length - 1]
                    .getBoundsReference();
            final PBounds first = mosaicNodes[0].getBoundsReference();
            final double viewScale = getCamera().getViewScale();
            return (int) Math.ceil((last.x + last.width - first.x) * viewScale);
        } else {
            return (int) Math.ceil(treeLength);
        }
    }

    @Override
    protected int getContentHeight() {
        if (mosaicNodes == null || mosaicNodes.length == 0) {
            return 0;
        }
        if (isHorizontal()) {
            return (int) Math.ceil(treeLength);
        } else {
            double maxHeight = Double.NaN;
            for (CGVisMosaicPlotNode mosaic : mosaicNodes) {
                final double height = mosaic.getBoundsReference().getHeight();
                if (Double.isNaN(maxHeight) || height > maxHeight) {
                    maxHeight = height;
                }
            }
            final double viewScale = getCamera().getViewScale();
            return (int) Math.ceil(maxHeight * viewScale);
        }
    }

    @Override
    protected void paintContent(Graphics2D g2, int x, int y) {
        g2.setColor(FG_COLOR);

        final PCamera camera = getCamera();

        for (int i = 0, len = rootClusters.length; i < len; i++) {
            final CGVisMosaicPlotNode mosaic = mosaicNodes[i];
            final double scale = camera.getViewScale();
            if (isHorizontal()) {
                offsetX = x + mosaic.getX() * scale;
                offsetY = y;
            } else {
                offsetX = x;
                offsetY = y + mosaic.getY() * scale;
            }
            stepWidth = STEP_WIDTH * scale;
            stepHeight = STEP_HEIGHT * scale;
            if (isHorizontal()) {
                traverseNodesHoriz(rootClusters[i], ChildType.ROOT, 0, g2);
            } else {
                traverseNodesVert(rootClusters[i], ChildType.ROOT, 0, g2);
            }
        }
    }

    private enum ChildType {
        ROOT, LEFT, RIGHT
    };

    private double stepWidth;
    private double stepHeight;
    private double offsetX;
    private double offsetY;

    private double traverseNodesHoriz(ClusterNode node, ChildType type,
            int level, Graphics2D g2) {
        if (node.isLeafNode()) {
            final double height = calcJointSize(level);
            final int y = (int) Math.round(offsetY + getHeight() - height - 1);
            final int x = (int) Math.round(offsetX + stepWidth / 2.0 /* + 1 */);
            final int _y = (int) Math.round(offsetY + getHeight() - 2);
            g2.drawLine(x, y, x, _y); // leaf stems
            offsetX += stepWidth;
            return offsetX;
        } else {
            final double height = calcJointSize(level + 1);
            final int y = (int) Math.round(offsetY + getHeight() - height - 1);

            final double ol = traverseNodesHoriz(node.getLeftChild(),
                    ChildType.LEFT, level + 1, g2);
            final double or = traverseNodesHoriz(node.getRightChild(),
                    ChildType.RIGHT, level + 1, g2);

            final int xl = (int) Math.ceil(ol - stepWidth / 2.0);
            final int xr = (int) Math.floor(or - stepWidth / 2.0);
            g2.drawLine(xl, y, xr, y); // shoulders

            final int xm = (int) Math.floor((ol + or) / 2 - stepWidth / 2.0);

            final double nHeight = calcJointSize(level);
            final int _y = (int) Math.ceil(y - nHeight + height);
            g2.drawLine(xm, _y, xm, y); // stems

            return (ol + or) / 2.0;
        }
    }

    private double traverseNodesVert(ClusterNode node, ChildType type,
            int level, Graphics2D g2) {
        if (node.isLeafNode()) {
            final double width = calcJointSize(level);
            final int x = (int) Math.round(offsetX + getWidth() - width - 1);
            final int y = (int) Math.round(offsetY + stepHeight / 2.0);
            final int _x = (int) Math.floor(offsetX + getWidth() - 1);
            g2.drawLine(x, y, _x, y); // leaf stems
            offsetY += stepHeight;
            return offsetY;
        } else {
            final double width = calcJointSize(level + 1);
            final int x = (int) Math.round(offsetX + getWidth() - width - 1);

            final double ol = traverseNodesVert(node.getLeftChild(),
                    ChildType.LEFT, level + 1, g2);
            final double or = traverseNodesVert(node.getRightChild(),
                    ChildType.RIGHT, level + 1, g2);

            final int yl = (int) Math.ceil(ol - stepHeight / 2.0);
            final int yr = (int) Math.floor(or - stepHeight / 2.0);
            g2.drawLine(x, yl, x, yr); // shoulders

            final int ym = (int) Math.round((ol + or) / 2.0 - stepHeight / 2.0);

            final double nWidth = calcJointSize(level);
            final int _x = (int) Math.ceil(x - nWidth + width);
            g2.drawLine(_x, ym, x, ym); // stems

            return (ol + or) / 2.0;
        }
    }

}
