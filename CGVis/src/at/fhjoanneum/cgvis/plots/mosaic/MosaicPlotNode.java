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
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import at.fhjoanneum.cgvis.data.IColorForValue;
import at.fhjoanneum.cgvis.data.IDataValues;
import at.fhjoanneum.cgvis.data.IPointSet;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * @author Ilya Boyandin
 */
public class MosaicPlotNode extends PNode {

  private static final Color DEFAULT_MISSING_VALUE_COLOR1 = new Color(162, 160, 149);
  private static final Color DEFAULT_MISSING_VALUE_COLOR2 = new Color(152, 150, 139);
  private static final Color SPACING_COLOR = new Color(0, 0, 0);

  private static final int DEFAULT_SQUARE_WIDTH = 1;
  private static final int DEFAULT_SQUARE_HEIGHT = 1;
  private static final int DEFAULT_SPACING = 0;

  private int cellWidth = DEFAULT_SQUARE_WIDTH;
  private int cellHeight = DEFAULT_SQUARE_HEIGHT;
  private int cellSpacing = DEFAULT_SPACING;

  private Color missingValueColor1 = DEFAULT_MISSING_VALUE_COLOR1;
  private Color missingValueColor2 = DEFAULT_MISSING_VALUE_COLOR2;

  // private static Font MARKS_FONT = new Font("Helvetica", Font.PLAIN, 11);


  // private static final float GRADIENT_START_HUE = Color.RGBtoHSB(0, 255, 0,
  // null)[0];
  // private static final float GRADIENT_MIDDLE_HUE = Color.RGBtoHSB(0, 0,
  // 255, null)[0];
  // private static final float GRADIENT_END_HUE = Color.RGBtoHSB(255, 0, 0,
  // null)[0];

  private final PCanvas canvas;
  private IDataValues dataValues;
  private final IColorForValue colorForValue;

  //    private final ColorScale colorScale;

  public MosaicPlotNode(IDataValues dataValues, PCanvas canvas, IColorForValue colorForValue) {
    this.dataValues = dataValues;
    this.colorForValue = colorForValue;
    // super(new Rectangle2D.Float(0, 0, 1, 1));
    // setStrokePaint(Color.black);
    this.canvas = canvas;

    //        this.colorScale = colorScale;

    // setPaint(BG_COLOR);
    setPaint(null);

    setBounds(0, 0, getPlotWidth(), getPlotHeight());
  }

  public IColorForValue getColorForValue() {
    return colorForValue;
  }

  public int getCellWidth() {
    return cellWidth;
  }

  public void setCellWidth(int cellWidth) {
    this.cellWidth = cellWidth;
  }

  public int getCellHeight() {
    return cellHeight;
  }

  public void setCellHeight(int cellHeight) {
    this.cellHeight = cellHeight;
  }

  public int getCellSpacing() {
    return cellSpacing;
  }

  public void setCellSpacing(int cellSpacing) {
    this.cellSpacing = cellSpacing;
  }

  public PCanvas getCanvas() {
    return canvas;
  }

  public void setMissingValueColor1(Color color) {
    this.missingValueColor1 = color;
  }

  public void setMissingValueColor2(Color color) {
    this.missingValueColor2 = color;
  }

  public IDataValues getDataValues() {
    return dataValues;
  }

  public void setPointSet(IPointSet pointSet) {
    // TODO: keep selection after clustering (it needs to be permuted)
    this.dataValues = pointSet;
  }

  protected int getPlotHeight() {
    return dataValues.getSize() * (cellHeight + cellSpacing) + cellSpacing;
  }

  protected int getPlotWidth() {
    return dataValues.getDimension() * (cellWidth + cellSpacing) + cellSpacing;
  }

  /**
   * @param pos
   *            Global position in the view
   * @return Cell attrs: Point(element, attr)
   */
  public Point pointToCell(Point2D pos) {
    final PBounds b = getFullBoundsReference();
    final int e = (int) Math.ceil((pos.getY() - cellSpacing - b.y)
        / (cellHeight + cellSpacing)) - 1;
    final int c = (int) Math.ceil((pos.getX() - cellSpacing - b.x)
        / (cellWidth + cellSpacing)) - 1;
    if (e >= 0 && e < dataValues.getSize() && c >= 0
        && c < dataValues.getDimension()) {
      return new Point(c, e);
    } else {
      return null;
    }
  }

  public Rectangle cellToRect(Point cell) {
    if (cell == null) {
      return null;
    }
    final PBounds b = getFullBoundsReference();
    final int x = ((int) b.x) + cellSpacing
    + (cell.x * (cellWidth + cellSpacing));
    final int y = ((int) b.y) + cellSpacing
    + (cell.y * (cellHeight + cellSpacing));
    return new Rectangle(x, y, cellWidth, cellHeight);
  }

  private transient Image imageCache;
  private transient boolean isDirty = true;

  public boolean isDirty() {
    return isDirty;
  }

  public void setDirty(boolean isDirty) {
    this.isDirty = isDirty;
  }

  @Override
  protected void paint(PPaintContext paintContext) {
    super.paint(paintContext);

    final Rectangle2D bounds = getFullBoundsReference();

    if (isDirty) {
      final int plotWidth = getPlotWidth();
      final int plotHeight = getPlotHeight();
      if (imageCache == null) {
        imageCache = canvas.createImage(plotWidth, plotHeight);
      }

      final Graphics2D g = (Graphics2D) imageCache.getGraphics();

      final int dim = dataValues.getDimension();
      final int size = dataValues.getSize();
      if (cellSpacing > 0) {
        g.setColor(SPACING_COLOR);
        g.fillRect(0, 0, plotWidth, plotHeight);
      }

      for (int c = 0; c < dim; c++) {
        final int x = (cellSpacing + (cellWidth + cellSpacing) * c);
        for (int e = 0; e < size; e++) {
          final double v = dataValues.getValue(e, c);
          final int y = (cellSpacing + (cellHeight + cellSpacing)
              * e);
          if (!Double.isNaN(v)) {
            final Color color = colorForValue.getColorForValue(v);
            g.setColor(color);
          } else {
            g.setColor((e + c) % 2 == 0 ? missingValueColor1 : missingValueColor2);
          }
          g.fillRect(x, y, cellWidth, cellHeight);
        }
      }
      isDirty = false;
    }

    final Graphics2D g2 = paintContext.getGraphics();
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    final int offsetX = (int) bounds.getX();
    final int offsetY = (int) bounds.getY();

    g2.drawImage(imageCache, offsetX, offsetY, null);
  }

}
