package at.fhj.utils.graphics.plot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import at.fhj.utils.graphics.AxisMarks;

/**
 * 
 * @author Ilya Boyandin
 *
 * $Revision: 1.2 $
 */
public class Plot implements ImagePainter {

  public static final DecimalFormat NFORMAT =
    new DecimalFormat("0.0###", new DecimalFormatSymbols(Locale.US));

  public static final DecimalFormat NFORMAT_SHORT =
    new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));

  private PlotData plotData;

  private static final Color colorPlotBg = Color.white;
  private static final Color colorBox = Color.lightGray;
  private static final Color colorMarkers = Color.black;
  private static final Color colorAxes = Color.lightGray;
  private static final Color colorPlot = new Color(0, 0, 255);
  private static final Color colorPoint = Color.gray;

  private static final Font markersFont = new Font("Dialog", Font.PLAIN, 10);

  private final static double ZOOM_FACTOR = 1.5;
  private final static int MAX_ZOOM = 25;
  private final static int MIN_ZOOM = 0;

  private int vMarksWidth = 55;
  private int hMarksHeight = 25;

  private double scaleX = 1.0;
  private double scaleY = 1.0;
  private double shiftX = 0;
  private double shiftY = 0;
  private int zoomX = 0;
  private int zoomY = 0;
  private double continuousnessInterval = 0;
  private boolean showPoints = false;

  private AxisMarks axisMarks = new AxisMarks(45, 90, 30, 90);

  private Dimension size;

  public Plot(PlotData plotData) {
    this.plotData = plotData;
    this.size = new Dimension();
  }

  public PlotData getPlotData() {
    return plotData;
  }

  public void setSize(int width, int height) {
    size.setSize(width, height);
  }

  public Dimension getSize() {
    return size;
  }
  
  public void reset() {
    shiftX = shiftY = 0;
    zoomX = zoomY = 0;
    minXV = minYV = Double.POSITIVE_INFINITY;
    maxXV = maxYV = Double.NEGATIVE_INFINITY;
    showPoints = false;
    shrinkToFit();
  }

  protected int getPlotWidth() {
    return size.width - 10 - vMarksWidth;
  }
  
  protected int getPlotHeight() {
    return size.height - 12 - hMarksHeight;
  }
  
  public double getScaleX() {
    return scaleX;
  }

  public double getScaleY() {
    return scaleY;
  }

  public double getShiftX() {
    return shiftX;
  }

  public double getShiftY() {
    return shiftY;
  }

  public boolean getShowPoints() {
    return showPoints;
  }

  public void setShowPoints(boolean showPoints) {
    this.showPoints = showPoints;
  }

  public double getContinuousnessInterval() {
    return continuousnessInterval;
  }

  public void setContinuousnessInterval(double interval) {
    this.continuousnessInterval = interval;
  }

  private transient double minXV = Double.POSITIVE_INFINITY;
  private transient double maxXV = Double.NEGATIVE_INFINITY;
  private transient double minYV = Double.POSITIVE_INFINITY;
  private transient double maxYV = Double.NEGATIVE_INFINITY;

  protected void shrinkToFit() {
    final int dataSize = plotData.getSize();
    if (minXV > maxXV  ||  minYV > maxYV) {
      for (int i = 0; i < dataSize; i++) {
        final double xval = plotData.getXValue(i);
        if (xval > maxXV)
          maxXV = xval;
        if (xval < minXV)
          minXV = xval;

        final double yval = plotData.getYValue(i);
        if (yval > maxYV)
          maxYV = yval;
        else if (yval < minYV)
          minYV = yval;
      }
    }

    final double fitRateX = 0.995, fitRateY = 0.95;
    
    scaleX = fitRateX * (double)getPlotWidth() / (maxXV - minXV);
    shiftX = minXV * scaleX  - (1 - fitRateX) * (maxXV - minXV) * scaleX / 2 + .5;
    
    scaleY = fitRateY * (double)getPlotHeight() / (maxYV - minYV);
    shiftY = maxYV * scaleY  -  fitRateY * getPlotHeight()/2 ; // + (1 - fitRateY) * (maxYV - minYV) * scaleY;
    
    zoomX = zoomY = 0;
  }

  public void stretch(int centerX) {
    if (zoomX < MAX_ZOOM) {
      final int w = getPlotWidth();
      zoomX++;
      shiftX = (int)Math.round(((shiftX + centerX) + (w - w / ZOOM_FACTOR) / 2) * ZOOM_FACTOR) - centerX;
      scaleX *= ZOOM_FACTOR;
    }
  }
  
  public void shrink(int centerX) {
    if (zoomX > MIN_ZOOM) {
      zoomX--;
      final int w = getPlotWidth();
      shiftX = (int)Math.round((shiftX + centerX) / ZOOM_FACTOR - (w - w / ZOOM_FACTOR) / 2)  - centerX;
      scaleX /= ZOOM_FACTOR;
    }
  }
  
  public void zoomIn(int centerX, int centerY) {
    if (Math.max(zoomX, zoomY) < MAX_ZOOM) {
      final int w = getPlotWidth();
      zoomX++; zoomY++;
      shiftX = (int)Math.round(((shiftX + centerX) + (w - w / ZOOM_FACTOR) / 2) * ZOOM_FACTOR) - centerX;
      shiftY = (shiftY - centerY) * ZOOM_FACTOR  + centerY;
      scaleX *= ZOOM_FACTOR;
      scaleY *= ZOOM_FACTOR;
    }
  }
  
  public void zoomOut(int centerX, int centerY) {
    if (Math.max(zoomX, zoomY) > MIN_ZOOM) {
      final int w = getPlotWidth();
      zoomX--; zoomY--;
      shiftX = (int)Math.round((shiftX + centerX) / ZOOM_FACTOR - (w - w / ZOOM_FACTOR) / 2)  - centerX;
      shiftY = (shiftY - centerY )/ ZOOM_FACTOR + centerY;
      scaleX /= ZOOM_FACTOR;
      scaleY /= ZOOM_FACTOR;
    }
  }
  
  public void shift(int dx, int dy) {
    shiftX -= dx;
    shiftY += dy;    
  }

  
  private void paintPlot(Graphics g, int posX, int posY) {
    Graphics2D g2 = (Graphics2D)g;

    final int pWidth = getPlotWidth(), pHeight = getPlotHeight();
    
    g.setColor(colorPlotBg);
    g.fillRect(posX, posY, pWidth, pHeight);

    // Paint the box
    g.setColor(colorBox);
    g.drawRect(posX - 1, posY - 1, pWidth + 1, pHeight + 1);

    
    
    if (plotData.hasData()) {
      final int dataSize = plotData.getSize();
      
      // Paint markers
      // FontMetrics fm = SwingUtilities2.getFontMetrics(this, g);
      FontMetrics fm = g.getFontMetrics(markersFont);
      g.setFont(markersFont);

      final double minVisX = shiftX / scaleX;
      final double maxVisX = (shiftX  + pWidth) / scaleX;

      final double minVisYVal = (shiftY - pHeight / 2) / scaleY;
      final double maxVisYVal = (shiftY + pHeight / 2) / scaleY;
      
      axisMarks.setPlotSize(pWidth, pHeight);
      axisMarks.calc(minVisX, maxVisX, minVisYVal, maxVisYVal);

      final double yStart = axisMarks.getYStart();
      final double yStep = axisMarks.getYStep();

      for (double mark = yStart; mark <= maxVisYVal ; mark += yStep) {
        
        final int markY = (int)Math.round(- mark * scaleY + shiftY) + pHeight / 2;

        final String smark = NFORMAT.format(mark);
        int smWidth = fm.stringWidth(smark);

        g.setColor(colorMarkers);
        g.drawString(smark, posX - 2 - smWidth, posY + markY + markersFont.getSize()/2 - 1);
        
        g.setColor(colorAxes);
        drawHAxe(g, posX, posY + markY, pWidth);

        if (Math.abs(yStep) <= 1e-07) {
          break;
        }
      }


      final double xStart = axisMarks.getXStart();
      final double xStep = axisMarks.getXStep();

      for (double mark = xStart; mark <= maxVisX /* * xIndToXVal*/ ; mark += xStep) {

        final int markX = (int)Math.round(mark * scaleX /* / xIndToXVal */ - shiftX);

        final String smark = NFORMAT.format(mark);
        int smWidth = fm.stringWidth(smark);
        /*
         TODO: adjust vMarksWidth dynamically
        if (smWidth + markersFont.getSize() * 2 > vMarksWidth) {
          vMarksWidth = smWidth + markersFont.getSize() * 2;
        }
        */
  
        g.setColor(colorMarkers);
        g.drawString(smark, posX + markX - smWidth / 2, posY + pHeight + markersFont.getSize() + 2);
        
        g.setColor(colorAxes);
        drawVAxe(g, posX + markX, posY, pHeight);

        if (Math.abs(xStep) <= 1e-07) {
          break;
        }
      }
      
      // Paint the axe labels 
      g.setColor(colorMarkers);
      final String xLabel = plotData.getXLabel();
      final int xLabelW = fm.stringWidth(xLabel);
      g.drawString(xLabel, posX + (pWidth - xLabelW) / 2, posY + pHeight + markersFont.getSize() * 2 + 8);
      
      final String yLabel = plotData.getYLabel();
      final int yLabelW = fm.stringWidth(yLabel);
      AffineTransform saveTransform = g2.getTransform();
      g2.transform(rotate270);
      g.drawString(yLabel, - posY - (pHeight + yLabelW) / 2, 12);
      g2.setTransform(saveTransform);

      
      // Paint the data
      g.setColor(colorPlot);
      Rectangle r = g.getClipBounds();
      g.setClip(posX, posY, pWidth, pHeight);
      int px = 0, py = 0, px_ = 0, py_ = 0;
      int miny = Integer.MAX_VALUE;
      int maxy = Integer.MIN_VALUE;
      double prevXVal = Double.NaN;
      final double contInt = continuousnessInterval;
      for (int i = 0; i < dataSize; i++) {
        
        final double xVal = plotData.getXValue(i);
        final int x = (int)Math.round(xVal * scaleX  - shiftX);

        if (px > pWidth  &&  x > pWidth) {
          break;
        }
        if (x < 0) {
          prevXVal = xVal;
          px = x;
          continue;
        }
        
        final int y = (int)((pHeight / 2) - Math.round(plotData.getYValue(i) * scaleY - shiftY));
        if (px < 0) {
          // we've lost this value as we were skipping every x < 0, so calc it now
          py = (int)((pHeight / 2) - Math.round(plotData.getYValue(i - 1) * scaleY - shiftY));
        }
        
        // TODO: ? optimize (if makes sense): detect the intersection of the line and the rect

        final int x_ = (int)(posX + x);
        final int y_ = (int)(posY + y);
        
        px_ = (int)(posX + px);
        py_ = (int)(posY + py);

        if (i > 0) {
          if (x_ > px_  ||  (xVal - prevXVal > contInt)) {
            if (maxy > miny) {
              g.drawLine(px_, miny, px_, maxy);
              if (showPoints) {   // TODO: ensure that none of the "points" are painted twice
                g.setColor(colorPoint);
                g.drawRect(px_ - 1, miny - 1, 2, 2);                  
                g.drawRect(px_ - 1, maxy - 1, 2, 2);                  
                g.setColor(colorPlot);
              }
            }
            //if (Math.abs(prevXVal - 4634.96)< 1) {
            if (/*contInt <= 0  ||*/  xVal - prevXVal <= contInt) { 
              g.drawLine(px_, py_, x_, y_);
              if (showPoints) {
                g.setColor(colorPoint);
                g.drawRect(px_ - 1, py_ - 1, 2, 2);                  
                g.drawRect(x_ - 1, y_ - 1, 2, 2);                  
                g.setColor(colorPlot);
              }
            } else {
              g.drawRect(px_, py_, 0, 0);
              if (showPoints) {
                g.setColor(colorPoint);
                g.drawRect(px_ - 1, py_ - 1, 2, 2);
                g.setColor(colorPlot);
              }
            }
            miny = maxy = y_;
          } else {
            if (y_ < miny) miny = y_;
            if (y_ > maxy) maxy = y_;
          }
          
            //g.drawLine(px_, py_, x_, y_);
          
          /*
          // pixel graphic:
          if (x_ != px_ || y_ != py_)
            g.drawRect(x_, y_, 0, 0);
          */
        } else {
          if (y_ < miny) miny = y_;
          if (y_ > maxy) maxy = y_;
        }
        prevXVal = xVal;
        px = x; py = y;
      }
      if (maxy > miny) {
        g.drawLine(px_, miny, px_, maxy);
        if (showPoints) {
          g.setColor(colorPoint);
          g.drawRect(px_ - 1, maxy - 1, 2, 2);
        }
      }
      g.setClip(r); // remove clipping
    }
  }
  
  private final double theta = 3 * Math.PI / 2;
  private final AffineTransform rotate270 = new AffineTransform(
      Math.cos(theta), Math.sin(theta), 
      -Math.sin(theta), Math.cos(theta),
      0.0, 0.0
  );

  private void drawHAxe(Graphics g, int px, int py, int len) {
    final int lw = 4, sw = 5; 
    int p = -(int)Math.round(shiftX) % (sw + lw);
    while (p < len) {
      int x1 = p;
      int x2 = x1 + (p + lw < len ? lw : len - p) - 1;
      if (x2 >= 0) {
        if (x1 < 0) x1 = 0;
        g.drawRect(px + x1, py, x2 - x1, 0);
      }
      p += lw + sw;
    }
  }

  private void drawVAxe(Graphics g, int px, int py, int len) {
    final int lw = 4, sw = 5; 
    int p = - lw + (int)Math.round(shiftY) % (sw + lw);
    while (p < len) {
      int y1 = p;
      int y2 = y1 + (p + lw < len ? lw : len - p) - 1;
      if (y2 >= 0) {
        if (y1 < 0) y1 = 0;
        g.drawRect(px, py + y1, 0, y2 - y1);
      }
      p += lw + sw;
    }
  }

  /**
   * This method is invoked by ImageExporter
   */
  public void paintImage(Graphics g, int x, int y) {
    paintPlot(g, x + getPlotPosX(), y + getPlotPosY());
  }
  
  public int getPlotPosX() {
    return 3 + vMarksWidth;
  }

  public int getPlotPosY() {
    return 5;
  }

}