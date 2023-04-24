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
package at.fhjoanneum.cgvis.plots;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Point;
import java.awt.geom.RoundRectangle2D;

import at.fhjoanneum.cgvis.CGVis;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * @author Ilya Boyandin
 */
public class PValueTooltip extends PPath2 {

    private static final long serialVersionUID = 4699401260245122226L;
    private static final Color DEFAULT_PAINT = new Color(0, 213, 213, 220);
    private static final Color DEFAULT_TEXT_PAINT = Color.white;
    private static final Color DEFAULT_STROKE_PAINT = new Color(0, 0, 0, 100);
    private static final BasicStroke DEFAULT_STROKE = new BasicStroke(.5f);
    private static Font HEADER_FONT = new Font("Helvetica", Font.PLAIN, 12);
    private static Font LABELS_FONT = HEADER_FONT;
    private static Font VALUES_FONT = HEADER_FONT;
    private Point padding;
    private int gap;
    private PText headerNode;
    private PText labelsNode;
    private PText valuesNode;

    public PValueTooltip(int archw, int archh) {
        super(new RoundRectangle2D.Double(0, 0, 100, 100, archw, archh));
        padding = new Point(5, 5);
        gap = 3;

        headerNode = new PText();
        labelsNode = new PText();
        valuesNode = new PText();

        headerNode.setFont(HEADER_FONT);
        labelsNode.setFont(LABELS_FONT);
        valuesNode.setFont(VALUES_FONT);

        addChild(headerNode);
        addChild(labelsNode);
        addChild(valuesNode);

        setPaint(DEFAULT_PAINT);
        if (CGVis.IS_OS_MAC) {
            setStroke(null);
        } else {
            setStroke(DEFAULT_STROKE);
            setStrokePaint(DEFAULT_STROKE_PAINT);
        }
        setTextPaint(DEFAULT_TEXT_PAINT);
    }

    public PValueTooltip() {
        this(10, 20);
    }

    public void setTextPaint(Paint textPaint) {
        headerNode.setTextPaint(textPaint);
        labelsNode.setTextPaint(textPaint);
        valuesNode.setTextPaint(textPaint);
    }

    public void setPadding(int px, int py) {
        this.padding.x = px;
        this.padding.y = py;
        updateBounds();
    }

    public Point getPadding() {
        return (Point) padding.clone();
    }

    public int getGap() {
        return gap;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }

    public void setText(String header, String labels, String values) {
        headerNode.setText(header);
        labelsNode.setText(labels);
        valuesNode.setText(values);
        updateBounds();
    }

    public boolean setPosition(double x, double y) {
        final PBounds b = getBoundsReference();
        return setBounds(x, y, b.width, b.height);
    }

    private void updateBounds() {
        if (headerNode != null && labelsNode != null && valuesNode != null) {
            final PBounds hb = headerNode.getBoundsReference();
            final PBounds lb = labelsNode.getBoundsReference();
            final PBounds vb = valuesNode.getBoundsReference();
            setBounds(getX(), getY(), Math.max(hb.width, lb.width + gap
                    + vb.width)
                    + padding.x * 2, hb.height + gap
                    + Math.max(lb.height, vb.height) + padding.y * 2);
            final PBounds b = getBoundsReference();
            headerNode.setBounds(b.x + padding.x, b.y + padding.y, hb.width,
                    hb.height);
            labelsNode.setBounds(b.x + padding.x, hb.height + gap + b.y
                    + padding.y, lb.width, lb.height);
            valuesNode.setBounds(b.x + padding.x + lb.width + gap, hb.height
                    + gap + b.y + padding.y, vb.width, vb.height);
        }
    }

    @Override
    public boolean setBounds(double x, double y, double width, double height) {
        if (super.setBounds(x, y, width, height)) {
            updateBounds();
            return true;
        }
        return false;
    }

    @Override
    protected void paint(PPaintContext pc) {
        final int oldQuality = pc.getRenderQuality();
        if (oldQuality != PPaintContext.HIGH_QUALITY_RENDERING) {
            pc.setRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
        }

        super.paint(pc);

        if (oldQuality != PPaintContext.HIGH_QUALITY_RENDERING) {
            pc.setRenderQuality(oldQuality);
        }
    }

}
