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

import javax.swing.SwingUtilities;

/**
 * @author Ilya Boyandin
 */
public class PaintedFloatingLabelsNode extends AbstractFloatingLabelsNode<String> {

    private static final long serialVersionUID = -7841682728936489973L;
    private static final Color TEXT_COLOR = Color.black;
    private static Font DEFAULT_LABELS_FONT = new Font("Helvetica", Font.PLAIN, 9);
    private Font font = DEFAULT_LABELS_FONT;
    private boolean anchorLabelsToEnd = false;
    private boolean rotateHorizLabels = false;


    public PaintedFloatingLabelsNode(boolean isHorizontal, LabelIterator<String> it) {
        super(isHorizontal, it);
    }

    public void setFont(Font font) {
      this.font = font;
    }

    public void setAnchorLabelsToEnd(boolean anchorLabelsToEnd) {
      this.anchorLabelsToEnd = anchorLabelsToEnd;
    }

    public void setRotateHorizLabels(boolean rotateHorizLabels) {
      this.rotateHorizLabels = rotateHorizLabels;
    }

    @Override
    protected double getLabelWidth(int index, String label) {
      FontMetrics fm = getCanvas().getFontMetrics(font);
      return fm.stringWidth(label);
    }

    @Override
    protected double getLabelHeight(int index, String label) {
      FontMetrics fm = getCanvas().getFontMetrics(font);
      return fm.getAscent();
    }

    @Override
    protected void paintContent(final Graphics2D g2, int offsetX, int offsetY) {
      g2.setFont(font);

      g2.setColor(TEXT_COLOR);

      if (isHorizontal()) {
        g2.rotate(-Math.PI / 2, 0, 0);
        paintLabels(g2);
        g2.rotate(Math.PI / 2, 0, 0);
      } else {
        paintLabels(g2);
      }
    }

    private void paintLabels(final Graphics2D g2) {
      final FontMetrics fm = g2.getFontMetrics();

      positionLabels(new LabelPositioner<String>() {

        @Override
        public void showSpacer(int x, int y) {
          drawLabel("...", x, y, g2, fm);
        }

        @Override
        public void showLabel(String label, int index, int x, int y) {
          drawLabel(label, x, y, g2, fm);
        }

        @Override
        public void hideLabel(String label, int count) {
          // do nothing
        }

      });
    }

    private void drawLabel(String label, int x, int y, Graphics2D g2, FontMetrics fm) {
      int w = SwingUtilities.computeStringWidth(fm, label);
      if (isHorizontal()) {
          int tmp = x; x = y; y = tmp;  // swap x and y
      }
      if (anchorLabelsToEnd) {
          int wh;
          if (isHorizontal()) {
              wh = (int)getHeight();
          } else {
              wh = (int)getWidth();
          }
          x = x + wh - w + getMarginBefore() - getMarginAfter() - 1;
      } else {
          x = x + getMarginBefore() - getMarginAfter() + 1;
      }

      if (isHorizontal()  &&   rotateHorizLabels) {
          double theta = Math.PI  * .65 / 2;
          g2.rotate(theta, x, y);
//          g2.translate(0, -getMarginBefore() * 2);
          g2.drawString(label, x, y);
//          g2.translate(0, +getMarginBefore() * 2);
          g2.rotate(-theta, x, y);
      } else {
          g2.drawString(label, x, y);
      }
    }
}
