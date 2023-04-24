package at.fhj.utils.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

public class FancyTable extends JTable {

  private static final long serialVersionUID = 4879123572342148397L;
  
    public Font cellFont = new Font(Font.DIALOG, Font.PLAIN, 11);
//    public Font headerFont = new Font(Font.DIALOG, Font.BOLD, 11);
  public Color bgColorOdd = new Color(237, 243, 254);
  public Color bgColorEven = Color.white;
  public Color fgColor = Color.black;
  public Color selBgColor = new Color(60, 128, 222);
  public Color selFgColor = Color.white;
  public Color borderColor = new Color(217, 217, 217);
  public Color gridColor = new Color(217, 217, 217);

  public FancyTable(TableModel model) {
    super(model);
    FancyCellRenderer fcr = new FancyCellRenderer();
    setDefaultRenderer(Object.class, fcr);
    setDefaultRenderer(Double.class, fcr);
    setDefaultRenderer(Float.class, fcr);
    setDefaultRenderer(Integer.class, fcr);
    setDefaultRenderer(Long.class, fcr);
    setDefaultRenderer(Icon.class, new FancyIconRenderer());
        setIntercellSpacing(new Dimension(0, 0));
        setShowHorizontalLines(false);
        setShowVerticalLines(false);
        setGridColor(gridColor);
        setRowHeight(17);
        setAutoResizeMode(AUTO_RESIZE_OFF);
        JLabel headerRenderer = (JLabel)getTableHeader().getDefaultRenderer();
        headerRenderer.setPreferredSize(new Dimension(0, 20));
  }

  public Dimension getPreferredScrollableViewportSize() {
      Dimension size = super.getPreferredScrollableViewportSize();
      return new Dimension(Math.min(getPreferredSize().width, size.width), size.height);
  }

  public class FancyCellRenderer extends DefaultTableCellRenderer {
      
    private static final long serialVersionUID = 1L;
    
        final Border normalBorder = BorderFactory.createCompoundBorder(
          BorderFactory.createMatteBorder(0, 0, 0, 1, borderColor),
          BorderFactory.createEmptyBorder(0, 5, 0, 5));
      final Border lastRowBorder = BorderFactory.createCompoundBorder(
          BorderFactory.createMatteBorder(0, 0, 1, 1, borderColor),
          BorderFactory.createEmptyBorder(0, 5, 0, 5));
      final Border lastColBorder = BorderFactory.createCompoundBorder(
          BorderFactory.createMatteBorder(0, 0, 0, 0, borderColor),
          BorderFactory.createEmptyBorder(0, 5, 0, 5));
      final Border rightBottomCornerBorder = BorderFactory.createCompoundBorder(
          BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor),
          BorderFactory.createEmptyBorder(0, 5, 0, 5));

      public Component getTableCellRendererComponent(JTable table, Object value,
              boolean isSelected, boolean hasFocus, int row, int column)
      {
          JLabel label = (JLabel)super.getTableCellRendererComponent(table, value,
              isSelected, hasFocus, row, column);
          label.setOpaque(true);
          label.setFont(cellFont);
          label.setHorizontalTextPosition(LEFT);
          
          Class klass = table.getColumnClass(column);  //(value != null ? value.getClass() : Object.class);
          if (Number.class.isAssignableFrom(klass)) {
            label.setHorizontalAlignment(JLabel.RIGHT);
          } else if (klass == Icon.class) {
            label.setHorizontalAlignment(JLabel.CENTER);
          } else {
            label.setHorizontalAlignment(JLabel.LEFT);
          } 
          
          if (row % 2 > 0)
              label.setBackground(bgColorOdd);
          else
              label.setBackground(bgColorEven);
          
      TableModel model = table.getModel();
      final int numRows = model.getRowCount();
      final int numCols = model.getColumnCount();
      if (row == numRows - 1) {
        if (column == numCols - 1) {
          label.setBorder(rightBottomCornerBorder);
        } else {
          label.setBorder(lastRowBorder);
        }
      } else {
        if (column == numCols - 1) {
          label.setBorder(lastColBorder);
        } else {
          label.setBorder(normalBorder);
        }
      }
          
          if (isSelected) {
              label.setBackground(selBgColor);
              label.setForeground(selFgColor);
          } else {
              label.setForeground(fgColor);
          }
          return label;
      }
  }
  
    public class FancyIconRenderer extends FancyCellRenderer {
    private static final long serialVersionUID = 1L;

        public FancyIconRenderer() {
      super();
      setHorizontalAlignment(JLabel.CENTER);
    }

    public void setValue(Object value) {
      setIcon((value instanceof Icon) ? (Icon) value : null);
    }
  }


    /**
   * Scrolls the cell (rowIndex, colIndex) so that it is visible at the center
   * of viewport. Assumes table is contained in a JScrollPane.
   * 
   * @param rowIndex
   * @param colIndex
   * @author javaalmanac.com
   */
  public void scrollToCenter(int rowIndex, int colIndex) {
    if (!(getParent() instanceof JViewport)) {
      return;
    }
    JViewport viewport = (JViewport) getParent();

    // This rectangle is relative to the table where the
    // northwest corner of cell (0,0) is always (0,0).
    Rectangle rect = getCellRect(rowIndex, colIndex, true);

    // The location of the view relative to the table
    Rectangle viewRect = viewport.getViewRect();

    // Translate the cell location so that it is relative
    // to the view, assuming the northwest corner of the
    // view is (0,0).
    rect.setLocation(rect.x - viewRect.x, rect.y - viewRect.y);

    // Calculate location of rect if it were at the center of view
    int centerX = (viewRect.width - rect.width) / 2;
    int centerY = (viewRect.height - rect.height) / 2;

    // Fake the location of the cell so that scrollRectToVisible
    // will move the cell to the center
    if (rect.x < centerX) {
      centerX = -centerX;
    }
    if (rect.y < centerY) {
      centerY = -centerY;
    }
    rect.translate(centerX, centerY);

    // Scroll the area into view.
    viewport.scrollRectToVisible(rect);
  }

    public Font getCellFont() {
        return cellFont;
    }

    public void setCellFont(Font font) {
        this.cellFont = font;
    }

//    public Font getHeaderFont() {
//        return headerFont;
//    }
//    
//    public void setHeaderFont(Font headerFont) {
//        this.headerFont = headerFont;
//    }
    
    public Color getBgColorOdd() {
        return bgColorOdd;
    }

    public void setBgColorOdd(Color bgColorOdd) {
        this.bgColorOdd = bgColorOdd;
    }

    public Color getBgColorEven() {
        return bgColorEven;
    }

    public void setBgColorEven(Color bgColorEven) {
        this.bgColorEven = bgColorEven;
    }

    public Color getFgColor() {
        return fgColor;
    }

    public void setFgColor(Color fgColor) {
        this.fgColor = fgColor;
    }

    public Color getSelBgColor() {
        return selBgColor;
    }

    public void setSelBgColor(Color selBgColor) {
        this.selBgColor = selBgColor;
    }

    public Color getSelFgColor() {
        return selFgColor;
    }

    public void setSelFgColor(Color selFgColor) {
        this.selFgColor = selFgColor;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public Color getGridColor() {
        return gridColor;
    }

    public void setGridColor(Color gridColor) {
        this.gridColor = gridColor;
    }
}
