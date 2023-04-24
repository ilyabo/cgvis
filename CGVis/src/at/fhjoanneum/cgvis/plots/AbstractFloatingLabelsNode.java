package at.fhjoanneum.cgvis.plots;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * @author Ilya Boyandin
 */
public abstract class AbstractFloatingLabelsNode<T> extends AbstractFloatingPanelNode {

  private static final Color BG_COLOR = new Color(255, 255, 255, 190);
  private static final int DEFAULT_MARGIN_BEFORE = 5;
  private static final int DEFAULT_MARGIN_AFTER = 2;
  private int marginBefore = DEFAULT_MARGIN_BEFORE;
  private int marginAfter = DEFAULT_MARGIN_AFTER;

  private final LabelIterator<T> labelIterator;
  private int contentLength;
  private boolean drawSpacers = true;

  public AbstractFloatingLabelsNode(boolean isHorizontal, LabelIterator<T> it) {
    super(isHorizontal);
    this.labelIterator = it;

    setPaint(BG_COLOR);
  }

  public interface LabelPositioner<T> {
    void showSpacer(int x, int y);

    void showLabel(T label, int index, int x, int y);

    void hideLabel(T label, int count);
  }

  public interface LabelIterator<T> {
    boolean hasNext();

    T next();

    double getItemPosition();

    double getItemSize();

    void reset();
  }

  @Override
  public void setParent(PNode parent) {
      super.setParent(parent);
      if (parent != null) {
        adjustSizeToLabels();
      }
  }

  public void setDrawSpacers(boolean drawSpacers) {
    this.drawSpacers = drawSpacers;
  }

  public boolean getDrawSpacers() {
    return drawSpacers;
  }

  public void setMarginAfter(int marginAfter) {
    this.marginAfter = marginAfter;
  }

  public int getMarginBefore() {
    return marginBefore;
  }

  public void setMarginBefore(int marginBefore) {
    this.marginBefore = marginBefore;
  }

  public int getMarginAfter() {
    return marginAfter;
  }

  protected abstract double getLabelWidth(int index, T label);

  protected abstract double getLabelHeight(int index, T label);

  private void adjustSizeToLabels() {
      final LabelIterator<T> it = labelIterator;
      it.reset();
      int maxwh = 0;
      double lastPos = 0, firstPos = 0, lastLen = 0, firstLen = 0;
      int count = 0;
      while (it.hasNext()) {
          final T label = it.next();
          final int w = (int)getLabelWidth(count, label);
          if (w > maxwh)
              maxwh = w;

          if (count == 0) {
              firstPos = it.getItemPosition();
              firstLen = it.getItemSize();
          }
          lastPos = it.getItemPosition();
          lastLen = it.getItemSize();
          count++;
      }
      if (isHorizontal()) {
          setHeight(maxwh + marginBefore + marginAfter);
      } else {
          setWidth(maxwh + marginBefore + marginAfter);
      }
      contentLength = (int)Math.ceil(lastPos - firstPos + (lastLen + firstLen) / 2);
  }

  protected void positionLabels(LabelPositioner<T> positioner) {
    final PCamera camera = getCamera();
    final PBounds viewBounds = camera.getViewBounds();
    final PBounds bounds = getBoundsReference();

    final LabelIterator<T> it = labelIterator;
    it.reset();
    if (isHorizontal()) {
        final Point2D.Double pos = new Point2D.Double(0, 0);
        final double scale = bounds.getWidth() / viewBounds.getWidth();

        int count = 0;
        int prevx = 0;
        int skipCount = 0;
        boolean dots = false;

        final int y = (int) (-bounds.getY() - bounds.getHeight());
        final int size = (int) Math.round(it.getItemSize() * scale);

        while (it.hasNext()) {
            final T label = it.next();
            final int fAscent = (int)getLabelHeight(count, label);

            final double posx = it.getItemPosition();
            boolean drawn = false;

            if (posx < viewBounds.getMaxX()) {
              final boolean doDraw = (viewBounds.getMinX() < posx
                      + it.getItemSize() * 2);

              pos.x = posx;
              pos.y = 0;
              camera.viewToLocal(pos);

              final int x = (int) (pos.x + (size + fAscent) / 2) - 1;

              if (count == 0 || x - prevx >= fAscent) {
                  if (drawSpacers && skipCount > 0 && !dots) {
                      final int _x = x - fAscent / 2 + 2;
                      if (doDraw) {
                         positioner.showSpacer(_x, y);
                      }
                      skipCount++;
                      dots = true;
                  } else {
                      if (doDraw) {
                         positioner.showLabel(label, count, x, y);
                         drawn = true;
                      }
                      dots = false;
                  }
                  prevx = x;
              } else {
                  skipCount++;
              }
            }

            if (!drawn) {
              positioner.hideLabel(label, count);
            }
            count++;
        }
    } else {
        final Point2D.Double pos = new Point2D.Double(0, 0);
        final double scale = bounds.getHeight() / viewBounds.getHeight();

        int count = 0;
        int prevy = 0;
        int skipCount = 0;
        boolean dots = false;

        final int x = (int) bounds.getX();
        final int size = (int) Math.round(it.getItemSize() * scale);

        while (it.hasNext()) {
            final T label = it.next();
            final int fAscent = (int)getLabelHeight(count, label);

            final double posy = it.getItemPosition();
            boolean drawn = false;
            if (posy < viewBounds.getMaxY()) {
              final boolean doDraw = (viewBounds.getMinY() < posy
                      + it.getItemSize() * 2);

              pos.x = 0;
              pos.y = posy;
              camera.viewToLocal(pos);

              final int y = (int) (pos.y + (size + fAscent) / 2) - 1;

              if (count == 0 || y - prevy >= fAscent) {
                  if (drawSpacers   &&  skipCount > 0 && !dots) {
                      final int _y = y - fAscent / 2 + 2;
                      if (doDraw) {
                          positioner.showSpacer(x, _y);
                      }
                      skipCount++;
                      dots = true;
                  } else {
                      if (doDraw) {
                          positioner.showLabel(label, count, x, y);
                          drawn = true;
                      }
                      dots = false;
                  }
                  prevy = y;
              } else {
                  skipCount++;
              }
            }
            if (!drawn) {
              positioner.hideLabel(label, count);
            }
            count++;
        }
    }
  }

  @Override
  protected int getContentWidth() {
      if (isHorizontal()) {
          final double viewScale = getCamera().getViewScale();
          return (int) Math.ceil(contentLength * viewScale);
      } else {
          return (int) Math.ceil(getWidth());
      }
  }

  @Override
  protected int getContentHeight() {
      if (isHorizontal()) {
          return (int) Math.ceil(getHeight());
      } else {
          final double viewScale = getCamera().getViewScale();
          return (int) Math.ceil(contentLength * viewScale);
      }
  }


}
