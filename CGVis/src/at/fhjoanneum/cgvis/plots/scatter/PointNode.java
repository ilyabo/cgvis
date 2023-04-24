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
import java.awt.geom.Ellipse2D;

import at.fhjoanneum.cgvis.Constants;
import at.fhjoanneum.cgvis.plots.PPath2;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.util.PFixedWidthStroke;

/**
 * @author Ilya Boyandin
 */
public class PointNode extends PPath2 {

    private static final long serialVersionUID = 4310255468833955365L;
    private static final PFixedWidthStroke STROKE = new PFixedWidthStroke(1f);
    private static final PFixedWidthStroke SELECTION_STROKE = new PFixedWidthStroke(
            1.5f);
    private static final Color HIGHLIGHT_COLOR = Color.black;
    private static final Color SELECTION_COLOR = Constants.ELEMENT_SELECTION_COLOR;

    private int elementIndex;
    private PActivity lastActivity;
    private boolean isHighlighted;
    private boolean isSelected;
    private PNode selectionMark;

    public PointNode(int elementIndex) {
        super(new Ellipse2D.Double(0, 0, 1, 1));
        // super(new Rectangle2D.Double(0, 0, 1, 1));
        this.elementIndex = elementIndex;
        // setPickable(true);
        // setStroke(FIXED_STROKE);
        setStroke(null);
        setStrokePaint(HIGHLIGHT_COLOR);
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean value) {
        if (value != this.isSelected) {
            final PNode sm = getSelectionMark();
            if (value) {
                final PBounds b = getBoundsReference();
                adjustSelectionMarkBounds(sm, b.x, b.y, b.width, b.height);
                addChild(sm);
            } else {
                removeChild(sm);
            }
            this.isSelected = value;
        }
    }

    private static final Ellipse2D.Float TEMP_ELLIPSE = new Ellipse2D.Float();

    private PNode getSelectionMark() {
        if (selectionMark == null) {
            // final PPath sm = PPath.createEllipse(0, 0, 1, 1);

            TEMP_ELLIPSE.setFrame(0, 0, 1, 1);
            final PPath sm = new PPath2(TEMP_ELLIPSE);

            // final PPath sm = PPath.createEllipse(
            // (float)(b.x - b.width * .1), (float)(b.y - b.height * .1),
            // (float)(b.width * 1.2f), (float)(b.height * 1.2f));
            // final PPath sm = PPath.createEllipse(
            // (float)(b.x + b.width * .1), (float)(b.y + b.height * .1),
            // (float)(b.width * .8f), (float)(b.height * .8f));
            sm.setStroke(SELECTION_STROKE);
            sm.setStrokePaint(SELECTION_COLOR);
            sm.setPaint(null);
            sm.setPickable(false);

            selectionMark = sm;
        }
        return selectionMark;
    }

    @Override
    public boolean setBounds(double x, double y, double width, double height) {
        if (super.setBounds(x, y, width, height)) {
            if (isSelected()) {
                return adjustSelectionMarkBounds(getSelectionMark(), x, y,
                        width, height);
            } else {
                return true;
            }
        }
        return false;
    }

    private boolean adjustSelectionMarkBounds(PNode sm, double x, double y,
            double width, double height) {
        final double d = getParent().getBoundsReference().width * .01;
        final float _x = (float) (x - d - (width < height ? (height - width) / 2
                : 0));
        final float _y = (float) (y - d - (width > height ? (width - height) / 2
                : 0));
        final double w = Math.max(width, height);
        final float _w = (float) (w + 2 * d);
        final float _h = _w;
        return sm.setBounds(_x, _y, _w, _h);
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public void setHighlighted(boolean value) {
        if (value != this.isHighlighted) {
            this.isHighlighted = value;
            if (value) {
                setStroke(STROKE);
            } else {
                setStroke(null);
            }
            repaint();
        }
    }

    public int getElementIndex() {
        return elementIndex;
    }

    public void setElementIndex(int elementIndex) {
        this.elementIndex = elementIndex;
    }

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

    /*
     * @Override public void setPaint(Paint newPaint) {
     * super.setPaint(newPaint); // if (newPaint instanceof Color) { // final
     * Color c = (Color)newPaint; // setStrokePaint(new Color(255 - c.getRed(),
     * 255 - c.getGreen(), 255 - c.getBlue())); // } else {
     * setStrokePaint(Color.black); // } }
     */

    /*
     * @Override protected void paint(PPaintContext pc) { super.paint(pc); / if
     * (isHighlighted) { final Graphics2D g2 = paintContext.getGraphics();
     * g2.setColor(HIGHLIGHT_COLOR);
     * 
     * final PBounds b = getBoundsReference();
     * g2.drawOval((int)Math.round(b.getX()), (int)Math.round(b.getY()),
     * (int)Math.round(b.getWidth()), (int)Math.round(b.getHeight())); }/
     *  / final PBounds b = getBounds(); final Graphics2D g2 =
     * paintContext.getGraphics(); final double scale = paintContext.getScale();
     * final int x = (int)Math.round(b.getCenterX() - 1.5 / scale); final int y =
     * (int)Math.round(b.getCenterY() - 1.5 / scale);
     *  // final Rectangle2D pb = getPathBoundsWithStroke(); //
     * g2.setColor((Color)getStrokePaint()); // System.out.println(scale); //
     * final double w = 3 / scale; // final int iw = (int)Math.round(w); //
     * g2.fillOval((int)Math.round(pb.getCenterX() - w),
     * (int)Math.round(pb.getCenterY() - w), iw, iw);
     *  // g2.fillOval(x, y, w, w); // g2.drawOval( //
     * (int)Math.round(b.getX()), (int)Math.round(b.getY()), //
     * (int)Math.round(b.getWidth()), (int)Math.round(b.getHeight())); / }
     */
}
