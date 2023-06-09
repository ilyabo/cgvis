package at.fhj.utils.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 * <code>ButtonAreaLayout</code> behaves in a similar manner to
 * <code>FlowLayout</code>. It lays out all components from left to right,
 * flushed right. The widths of all components will be set to the largest
 * preferred size width.
 */
public class ButtonAreaLayout implements LayoutManager {

  private int hGap = 5;
  private int topMargin = 17;
  private int leftMargin = 0;
  private int bottomMargin = 0;
  private int rightMargin = 0;
  
  public ButtonAreaLayout() {
  }

  public ButtonAreaLayout(int hGap, int topMargin) {
    this.hGap = hGap;
    this.topMargin = topMargin;
  }

  /*
   * TODO: ButtonAreaLayout rightMargin, bottomMargin, leftMargin
  public ButtonAreaLayout(int hGap, int topMargin, int rightMargin, int bottomMargin, int leftMargin) {
    this.hGap = hGap;
    this.topMargin = topMargin;
    this.rightMargin = rightMargin;
    this.bottomMargin = bottomMargin;
    this.leftMargin = leftMargin;
  }
  */

  public void addLayoutComponent(String string, Component comp) {
  }

  public void layoutContainer(Container container) {
    Component[] children = container.getComponents();

    if (children != null && children.length > 0) {
      int numChildren = children.length;
      Dimension[] sizes = new Dimension[numChildren];
      Insets insets = container.getInsets();
      int yLocation = insets.top + topMargin;
      int maxWidth = 0;

      for (int counter = 0; counter < numChildren; counter++) {
        sizes[counter] = children[counter].getPreferredSize();
        maxWidth = Math.max(maxWidth, sizes[counter].width);
      }
      int xLocation, xOffset;
      if (container.getComponentOrientation().isLeftToRight()) {
        xLocation = leftMargin + container.getSize().width - insets.left - maxWidth;
        xOffset = hGap + maxWidth;
      } else {
        xLocation = leftMargin + insets.left;
        xOffset = -(hGap + maxWidth);
      }
      for (int counter = numChildren - 1; counter >= 0; counter--) {
        children[counter].setBounds(xLocation, yLocation, maxWidth, sizes[counter].height);
        xLocation -= xOffset;
      }
    }
  }

  public Dimension minimumLayoutSize(Container c) {
    if (c != null) {
      Component[] children = c.getComponents();

      if (children != null && children.length > 0) {
        int numChildren = children.length;
        int height = 0;
        Insets cInsets = c.getInsets();
        int extraHeight = topMargin + cInsets.top + cInsets.bottom + bottomMargin;
        int extraWidth = leftMargin + cInsets.left + cInsets.right + rightMargin;
        int maxWidth = 0;

        for (int counter = 0; counter < numChildren; counter++) {
          Dimension aSize = children[counter].getPreferredSize();
          height = Math.max(height, aSize.height);
          maxWidth = Math.max(maxWidth, aSize.width);
        }
        return new Dimension(extraWidth + numChildren * maxWidth + (numChildren - 1) * hGap,
            extraHeight + height);
      }
    }
    return new Dimension(0, 0);
  }

  public Dimension preferredLayoutSize(Container c) {
    return minimumLayoutSize(c);
  }

  public void removeLayoutComponent(Component c) {
  }
}
