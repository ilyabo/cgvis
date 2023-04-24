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

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * @author Ilya Boyandin
 */
public class ColorScaleNode extends PNode {

    private static final long serialVersionUID = 6080363366114243771L;

    private static Font MARKS_FONT = new Font("Helvetica", Font.PLAIN, 11);
    private static NumberFormat NFORMAT = new DecimalFormat("0.0##########");

    private ColorScale colorScale;
    private int offsetX = 10;
    private int offsetY = 1;
    private int scaleWidth = 100;
    private int scaleHeight = 15;

    public ColorScaleNode(ColorScale colorScale) {
        this.colorScale = colorScale;
        setBounds(0, 0, scaleWidth + offsetX, scaleHeight + offsetY);
    }

    @Override
    protected void paint(PPaintContext pc) {

        final Graphics2D g = pc.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, (int)getWidth(), (int)getHeight());
        
        final FontMetrics fm = g.getFontMetrics();

        final double minV = colorScale.getMinValue();
        final double maxV = colorScale.getMaxValue();
        
        final String sMaxV = NFORMAT.format(maxV);
        final String sMinV = NFORMAT.format(minV);

        final int wMinV = fm.stringWidth(sMinV);
        final int wMaxV = fm.stringWidth(sMaxV);
        
        for (int i = 0; i < scaleWidth; i++) {
            final double value = minV + i * (maxV - minV) / scaleWidth;
            g.setColor(colorScale.getColorForValue(value));
            g.drawLine(offsetX + i, offsetY, offsetX + i, offsetY + scaleHeight);
        }
        
        g.setColor(Color.black);
        g.setFont(MARKS_FONT);
                
        g.drawString(sMinV, offsetX + 1 - wMinV/2, offsetY + 1 + MARKS_FONT.getSize());
        g.drawString(sMaxV, scaleWidth - wMaxV/2, offsetY + 1 + MARKS_FONT.getSize());
    }
    
}
