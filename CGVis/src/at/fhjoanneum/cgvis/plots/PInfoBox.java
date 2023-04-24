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
import java.awt.Paint;
import java.awt.Point;
import java.awt.geom.RoundRectangle2D;

import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * @author Ilya Boyandin
 */
public class PInfoBox extends PPath2 {

    private static final long serialVersionUID = 2160498626862225977L;
    private static final Color DEFAULT_PAINT = new Color(0, 213, 213, 220);
    private static final Color DEFAULT_STROKE_PAINT = new Color(0, 0, 0, 100);
    private static final BasicStroke DEFAULT_STROKE = new BasicStroke(.5f);
    private Point padding;
    private PText textNode;

    public PInfoBox(int archw, int archh) {
        super(new RoundRectangle2D.Double(0, 0, 100, 100, archw, archh));
        setPaint(DEFAULT_PAINT);
        setStroke(DEFAULT_STROKE);
        setStrokePaint(DEFAULT_STROKE_PAINT);
        padding = new Point(5, 5);
        textNode = new PText();
        textNode.setBounds(padding.x, padding.y, 10, 10);
        setTextPaint(Color.white);
        addChild(textNode);
    }

    public PInfoBox() {
        this(10, 20);
    }

    public void setTextPaint(Paint textPaint) {
        textNode.setTextPaint(textPaint);
    }

    public void setPadding(int px, int py) {
        this.padding.x = px;
        this.padding.y = py;
        updateTextBounds();
    }

    public Point getPadding() {
        return (Point) padding.clone();
    }

    public void setText(String text) {
        textNode.setText(text);
        final PBounds b = textNode.getBoundsReference();
        setBounds(getX(), getY(), b.width + padding.x * 2, b.height + padding.y
                * 2);
    }

    public boolean setPosition(double x, double y) {
        final PBounds b = getBoundsReference();
        return setBounds(x, y, b.width, b.height);
    }

    private void updateTextBounds() {
        if (textNode != null) {
            final PBounds b = getBoundsReference();
            textNode.setBounds(b.x + padding.x, b.y + padding.y, b.width
                    - padding.x * 2, b.height - padding.y * 2);
        }
    }

    @Override
    public boolean setBounds(double x, double y, double width, double height) {
        if (super.setBounds(x, y, width, height)) {
            updateTextBounds();
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
