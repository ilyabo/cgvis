package at.fhj.utils.graphics.plot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import at.fhj.utils.swing.JMsgPane;

/**
 * This class is a simple 2D plot implementation, which
 * supports move, zoom and image export operations.
 * 
 * @author Ilya Boyandin
 *
 * $Revision: 1.1 $
 */
public class JPlotPanel extends JPanel {
  
  private ImageIcon saveIcon = new ImageIcon(
      getClass().getResource("/images/Save16-2.gif"));
  private ImageIcon showPointsIcon = new ImageIcon(
      getClass().getResource("/images/PlotShowPoints16.gif"));
  private ImageIcon fitIcon = new ImageIcon(
      getClass().getResource("/images/PlotFit16.gif"));
  private ImageIcon stretchIcon = new ImageIcon(
      getClass().getResource("/images/PlotStretch16.gif"));
  private ImageIcon shrinkIcon = new ImageIcon(
      getClass().getResource("/images/PlotShrink16.gif"));
  private ImageIcon zoomInIcon = new ImageIcon(
      getClass().getResource("/images/ZoomIn16.gif"));
  private ImageIcon zoomOutIcon = new ImageIcon(
      getClass().getResource("/images/ZoomOut16.gif"));

  private Plot plot;
  private PlotPanel plotPanel;
  
  private JButton saveButton;
  private JToggleButton showPointsButton;
  private JButton fitButton;
  private JButton stretchButton;
  private JButton shrinkButton;
  private JButton zoomInButton;
  private JButton zoomOutButton;

  private String title;
  private String imageExportPath;
  private String imageExportFilename;

  
  public JPlotPanel(PlotData plotData) {
    setOpaque(true);
    setLayout(new BorderLayout());
    
    plot = new Plot(plotData);
    plotPanel = new PlotPanel(plot);
    
    add(plotPanel, BorderLayout.CENTER);

    JToolBar toolBar = new JToolBar();
    toolBar.setFloatable(false);
    
    ActionListener al = new ButtonActionListener(); 

    saveButton = new JButton(saveIcon);
    saveButton.setToolTipText("Save Image");
    saveButton.addActionListener(al);

    showPointsButton = new JToggleButton(showPointsIcon);
    showPointsButton.setToolTipText("Show Points");
    showPointsButton.addActionListener(al);

    fitButton = new JButton(fitIcon);
    fitButton.setToolTipText("Fit in Window");
    fitButton.addActionListener(al);

    stretchButton = new JButton(stretchIcon);
    stretchButton.setToolTipText("Stretch (Shift + Mouse Wheel Back)");
    stretchButton.addActionListener(al);

    shrinkButton = new JButton(shrinkIcon);
    shrinkButton.setToolTipText("Shrink (Shift + Mouse Wheel Fwd)");
    shrinkButton.addActionListener(al);
    
    zoomInButton = new JButton(zoomInIcon);
    zoomInButton.setToolTipText("Zoom In (Mouse Wheel Fwd)");
    zoomInButton.addActionListener(al);

    zoomOutButton = new JButton(zoomOutIcon);
    zoomOutButton.setToolTipText("Zoom Out (Mouse Wheel Back)");
    zoomOutButton.addActionListener(al);

    toolBar.add(fitButton);
    toolBar.add(zoomInButton);
    toolBar.add(zoomOutButton);
    toolBar.addSeparator();
    toolBar.add(stretchButton);
    toolBar.add(shrinkButton);
    toolBar.addSeparator();
    toolBar.add(showPointsButton);
    toolBar.addSeparator();
    toolBar.add(saveButton);
    
    toolBar.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 5));
    
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.add(toolBar, BorderLayout.EAST);

    add(topPanel, BorderLayout.PAGE_START);
  }

  public String getImageExportFilename() {
    return imageExportFilename;
  }

  /**
   * Set the default output file name (without extension) for
   * image export.
   */
  public void setImageExportFilename(String imageExportFilename) {
    this.imageExportFilename = imageExportFilename;
  }

  public String getImageExportPath() {
    return imageExportPath;
  }

  /**
   * Set the default destination directory for image export.
   * @param imageExportPath
   */
  public void setImageExportPath(String imageExportPath) {
    this.imageExportPath = imageExportPath;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  private class ButtonActionListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();
      if (src == saveButton) {
        saveImage();
      } else if (src == showPointsButton) {
        plot.setShowPoints(showPointsButton.getModel().isSelected());  
      } else if (src == fitButton) {
        showPointsButton.setSelected(false);
        plotPanel.setShrinkToFitMode(true);
        plot.reset();
      } else if (src == stretchButton) {
        plot.stretch(0);
        plotPanel.setShrinkToFitMode(false);
      } else if (src == shrinkButton) {
        plot.shrink(0);
        plotPanel.setShrinkToFitMode(false);
      } else if (src == zoomInButton) {
        plot.zoomIn(0, 0);
        plotPanel.setShrinkToFitMode(false);
      } else if (src == zoomOutButton) {
        plot.zoomOut(0, 0);
        plotPanel.setShrinkToFitMode(false);
      }
      plotPanel.setModified(true);
      repaint();
    }
  }

  public double getContinuousnessInterval() {
    return plot.getContinuousnessInterval();
  }

  public void setContinuousnessInterval(double interval) {
    plot.setContinuousnessInterval(interval);
  }
  
  private void saveImage() {
    ImageExporter ie = new ImageExporter();
    File destFile = ie.showFileDialog(getRootPane(),
        getImageExportPath(), getImageExportFilename());
    if (destFile != null) {
      try {
        ie.exportImageToFile(getTitle(), plot, destFile);
      } catch (IOException ex) {
        JMsgPane.showErrorDialog(this, "Couldn't save image " + ex.getMessage());
      }
    }
  }

  
  private static class PlotPanel extends JPanel {
    private static final Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    private static final Cursor moveCursor = new Cursor(Cursor.MOVE_CURSOR);
    private static final Color colorSight = new Color(255, 190, 190);

    private boolean mousePressed = false;
    private int dragStartX, dragStartY;
    private boolean isModified = true;

    private boolean shrinkToFitMode = true;
    private boolean showSight = false;
    private int sightX = 0, sightY = 0;
    private Plot plot;

    public PlotPanel(final Plot plot) {
      this.plot = plot;

      setCursor(normalCursor);
  
      addMouseListener(new MouseAdapter() {
        public void mousePressed(MouseEvent e) {
          dragStartX = e.getX();
          dragStartY = e.getY();
          mousePressed = true;
          PlotPanel.this.setCursor(moveCursor);
        }
        public void mouseReleased(MouseEvent e) {
          mousePressed = false;
          PlotPanel.this.setCursor(normalCursor);
        }
        public void mouseExited(MouseEvent e) {
          //mousePressed = false;
          sightX = sightY = 0;
          showSight = false;
          update(false);
        }
        public void mouseEntered(MouseEvent e) {
          //showSight = true;
        }
      });
      
      addMouseMotionListener(new MouseMotionListener() {
        public void mouseDragged(MouseEvent e) {
          if (mousePressed) {
            plot.shift(e.getX() - dragStartX, e.getY() - dragStartY);
            dragStartX = e.getX();
            dragStartY = e.getY();
            update(true);
          }
        }
        public void mouseMoved(MouseEvent e) {
          final int pWidth = plot.getPlotWidth();
          final int pHeight = plot.getPlotHeight();
  
          final int x = e.getX() - plot.getPlotPosX();
          final int y = e.getY() - plot.getPlotPosY();
  
          if (x >= 0  &&  x <= pWidth  &&  y >= 0  &&  y <= pHeight) { 
  
            
            // (int)Math.round(getXValue(i) * scaleX  - shiftX);
            
            final double xVal = (double)(x + plot.getShiftX())/ plot.getScaleX();
            final double yVal = (double)(- y  + pHeight / 2 + plot.getShiftY())/ plot.getScaleY();
                        
            PlotPanel.this.setToolTipText(
                plot.getPlotData().getXLabel() + ": " + Plot.NFORMAT_SHORT.format(xVal) + ", " +
                plot.getPlotData().getYLabel() + ": " + Plot.NFORMAT_SHORT.format(yVal));
            sightX = x - pWidth / 2;
            sightY = y - pHeight / 2;
            showSight = true;
          } else {
            sightX = sightY = 0;
            PlotPanel.this.setToolTipText(null);
            showSight = false;
          }
          update(false);
        }
      });
      
      addMouseWheelListener(new MouseWheelListener() {
        public void mouseWheelMoved(MouseWheelEvent e) {
          final boolean shiftPressed;
          shiftPressed =((e.getModifiers() & InputEvent.SHIFT_MASK) != 0);
          
          int r = e.getWheelRotation();
          if (r > 0) {
            for (int i = 0; i < r; i++) {
              if (shiftPressed) {
                plot.stretch(sightX);
              } else {
                plot.zoomOut(sightX, sightY);
              }
            }
          } else {
            for (int i = 0; i > r; i--) {
              if (shiftPressed) {
                plot.shrink(sightX);                
              } else {
                plot.zoomIn(sightX, sightY);
              }
            }
          }
          update(true);
        }
      });

      /*addPropertyChangeListener("?size",
        new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          Dimension d = PlotPanel.this.getSize();
          plot.setSize(d.width, d.height);
        }
      });*/
    }
    
    private void update(boolean dataViewModified) {
      if (dataViewModified) {
        setShrinkToFitMode(false);
      }
      setModified(dataViewModified);
      repaint();
    }

    public boolean getShrinkToFitMode() {
      return shrinkToFitMode;
    }

    public void setShrinkToFitMode(boolean shrinkToFitMode) {
      this.shrinkToFitMode = shrinkToFitMode;
    }

    public void setModified(boolean flag) {
      isModified = flag;
    }

    public boolean isModified() {
      return isModified;
    }
    
    private transient Image buffer = null;
    private transient int bufferWidth = -1, bufferHeight = -1;
    
    protected Image getBuffer() {
      final int width = getWidth(), height = getHeight();
      if (bufferWidth != width || bufferHeight != height) {
        buffer = createImage(width, height);
        bufferWidth = width;
        bufferHeight = height;
      }
      return buffer;
    }

    private transient int cachedWidth = 0;
    private transient int cachedHeight = 0;

    public void paintComponent(Graphics g) {
      //super.paintComponent(g);
      
      // TODO: paint to a buffer and use clipping area info for repainting (getClipBounds())

      Dimension size = getSize();
      plot.setSize(size.width, size.height);

      final int pWidth = plot.getPlotWidth(), pHeight = plot.getPlotHeight();

      if (cachedWidth != pWidth  ||  cachedHeight != pHeight) {
        // Shrink only when the size changes
        if (getShrinkToFitMode()) {
          plot.shrinkToFit();
        }
        cachedWidth = pWidth;
        cachedHeight = pHeight;
        setModified(true);
      }

      Image buffer = getBuffer();
      if (isModified()) {
        Graphics bfg = buffer.getGraphics();
        
        // Paint the background
        bfg.setColor(getBackground());
        bfg.fillRect(0, 0, getWidth(), getHeight());

        //plot.paintPlot(bfg, plot.getPlotPosX(), plot.getPlotPosY());
        plot.paintImage(bfg, 0, 0);
        setModified(false);
      }
      
        if (buffer != null) {
            g.drawImage(buffer, 0, 0, null);
        }
      
    
      // Paint the sight
        if (showSight) {
          int pPosX = plot.getPlotPosX(), pPosY = plot.getPlotPosY();
          
        g.setColor(colorSight);
        final int _sightX = pPosX + sightX + pWidth/2;
        final int _sightY = pPosY + sightY + pHeight/2;
        g.drawLine(_sightX, pPosY, _sightX, pPosY + pHeight);
        g.drawLine(pPosX, _sightY, pPosX + pWidth, _sightY);
        }
    }

  }
}
