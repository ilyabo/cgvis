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
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

import com.jgoodies.looks.LookUtils;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.util.PFixedWidthStroke;

/**
 * At the time of writing Mac OS X Java doesn't support custom strokes and
 * produces error messages when trying to use one. This PPath version contains a
 * workaround for the problem. Use it everywhere instead of PPath where you need
 * a stroked PPath.
 * 
 * @author Ilya Boyandin
 */
public class PPath2 extends PPath {

    private static final long serialVersionUID = 8705408181998441587L;

    public PPath2(Shape shape) {
        super(shape);
    }

    @Override
    protected void paint(PPaintContext pc) {
        final Paint p = getPaint();
        final Graphics2D g2 = pc.getGraphics();

        final GeneralPath path = getPathReference();
        if (p != null) {
            g2.setPaint(p);
            g2.fill(path);
        }

        final Stroke stroke = getStroke();
        final Paint strokePaint = getStrokePaint();
        if (stroke != null && strokePaint != null) {
            g2.setPaint(strokePaint);

            if (LookUtils.IS_OS_MAC) {
                // workround: Mac OS X Java doesn't support custom strokes
                final Stroke oldStroke = g2.getStroke();
                if (stroke instanceof BasicStroke) {
                    g2.setStroke(stroke);
                    // this doesn't produce the "custom strokes not supported"
                    // error message as g2.draw(path) does on Mac
                    g2.fill(g2.getStroke().createStrokedShape(path));

                } else if (stroke instanceof PFixedWidthStroke) {
                    // use a scaled BasicStroke instead
                    final PFixedWidthStroke fwStroke = (PFixedWidthStroke) stroke;
                    final float lineWidth = (float) (fwStroke.getLineWidth() / pc
                            .getScale());

                    final Stroke newStroke = new BasicStroke(lineWidth,
                            fwStroke.getEndCap(), fwStroke.getLineJoin(),
                            fwStroke.getMiterLimit(), fwStroke.getDashArray(),
                            fwStroke.getDashPhase());

                    g2.setStroke(newStroke);
                    g2.translate(-lineWidth / 4, +lineWidth / 4); // otherwise
                                                                    // the
                                                                    // stroke is
                                                                    // incorrectly
                                                                    // positioned
                    // only god knows why :)
                    g2.draw(path);
                    g2.translate(+lineWidth / 4, -lineWidth / 4);
                }
                g2.setStroke(oldStroke); // otherwise newStoke would be used
                                            // in
                // subsequent drawLine calls as well

            } else {
                g2.setStroke(stroke);
                g2.draw(path);
            }
        }
    }

}
