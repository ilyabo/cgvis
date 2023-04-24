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
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import at.fhjoanneum.cgvis.Constants;
import at.fhjoanneum.cgvis.data.AttrSelection;
import at.fhjoanneum.cgvis.data.IColorForValue;
import at.fhjoanneum.cgvis.data.IPointSet;
import at.fhjoanneum.cgvis.plots.GradientColorScale;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * @author Ilya Boyandin
 */
public class CGVisMosaicPlotNode extends MosaicPlotNode {

  private static final Color ATTR_SELECTION_COLOR = new Color(0, 255, 255, 170);
  private static final Color ELEM_SELECTION_COLOR = Constants.ELEMENT_SELECTION_COLOR;

  static final int SQUARE_WIDTH = 1;
  static final int SQUARE_HEIGHT = 1;
  static final int SPACING = 0;

  private final AttrSelection attrSelection;

  public CGVisMosaicPlotNode(IPointSet pointSet, MosaicCanvas canvas) {
    super(pointSet, canvas, createColorForValue(pointSet));

    attrSelection = new AttrSelection(pointSet);

    setCellWidth(SQUARE_WIDTH);
    setCellHeight(SQUARE_HEIGHT);
    setCellSpacing(SPACING);
  }

  private static IColorForValue createColorForValue(final IPointSet pointSet) {
    final int dim = pointSet.getDimension();
    final int size = pointSet.getSize();

    double minV = Double.NaN;
    double maxV = Double.NaN;

    for (int c = 0; c < dim; c++) {
      for (int i = 0; i < size; i++) {
        final double  v = pointSet.getValue(i, c);
        if (!Double.isNaN(v)) {
          if (Double.isNaN(minV)  ||   v < minV)
            minV = v;
          if (Double.isNaN(maxV)  ||   v > maxV)
            maxV = v;
        }
      }
    }

    return new GradientColorScale(minV, maxV);
  }

  @Override
  public GradientColorScale getColorForValue() {
    return (GradientColorScale)super.getColorForValue();
  }

  public double getMaxValue() {
    return getColorForValue().getMaxValue();
  }

  public double getMinValue() {
    return getColorForValue().getMinValue();
  }

  public IPointSet getPointSet() {
    return (IPointSet)getDataValues();
  }

  public MosaicCanvas getMosaicCanvas() {
    return (MosaicCanvas)getCanvas();
  }

  @Override
  public void setPointSet(IPointSet pointSet) {
    super.setPointSet(pointSet);
    this.attrSelection.setPointSet(pointSet);
    // TODO: keep selection after clustering (it needs to be permuted)
  }

  public AttrSelection getAttrSelection() {
    return attrSelection;
  }

  public boolean invertAttrSelection(int index) {
    final boolean selected = attrSelection.invert(index);
    repaint();
    return selected;
  }

  public void clearSelection() {
    attrSelection.clearAll();
    repaint();
  }

  @Override
  protected void paint(PPaintContext paintContext) {
    super.paint(paintContext);

    Graphics2D g2 = paintContext.getGraphics();

    paintAttrSelection(g2);
    paintElemSelection(g2);
  }

  private void paintAttrSelection(Graphics2D g2) {
    final Rectangle2D bounds = getFullBoundsReference();

    final int offsetX = (int) bounds.getX();
    final int offsetY = (int) bounds.getY();
    final int height = (int) bounds.getHeight();

    g2.setColor(ATTR_SELECTION_COLOR);
    final Iterator<Integer> it = attrSelection.iterator();
    while (it.hasNext()) {
      final Integer c = it.next();
      final int x = (SPACING + (SQUARE_WIDTH + SPACING) * c);
      g2.fillRect(offsetX + x, offsetY, SQUARE_WIDTH, height);
    }

  }

  private void paintElemSelection(Graphics2D g2) {
    final Rectangle2D bounds = getFullBoundsReference();

    final int offsetX = (int) bounds.getX();
    final int offsetY = (int) bounds.getY();
    final int width = (int) bounds.getWidth();

    g2.setColor(ELEM_SELECTION_COLOR);
    final Iterator<Integer> it = getMosaicCanvas().getElemSelection().iterator();
    while (it.hasNext()) {
      final Integer r = it.next();
      final int y = (SPACING + (SQUARE_HEIGHT + SPACING) * r);
      g2.fillRect(offsetX, offsetY + y, width, SQUARE_HEIGHT);
    }

  }

  public void selectAttrRange(int start, int end) {
    attrSelection.selectRange(start, end);
  }

}
