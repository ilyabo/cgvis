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
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * @author Ilya Boyandin
 */
public abstract class AbstractFloatingPanelNode extends PNode {

    private static final long serialVersionUID = 1019917786425901175L;

    protected static final Color DEFAULT_BG_COLOR = Color.white;
    protected static final Color TRANSPARENT_COLOR = new Color(255, 255, 255, 0);
    public static final String PROPERTY_SIZE_ADJUSTED_TO_LABELS = "sizeAdjustedToLabels";
    private final PropertyChangeListener cameraListener;
    private List<PNode> disjointNodes;
    private final boolean isHorizontal;
    private int renderQuality = PPaintContext.HIGH_QUALITY_RENDERING;
    private boolean useContentCache = false;
    private boolean autoAdjustBoundsToCamera = true;

    public AbstractFloatingPanelNode(boolean isHorizontal) {
        this.isHorizontal = isHorizontal;
        cameraListener = new CameraListener();
        addInputEventListener(new MouseHandler());
    }

    public boolean getUseContentCache() {
        return useContentCache;
    }

    public void setUseContentCache(boolean useContentCache) {
        this.useContentCache = useContentCache;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public boolean getAutoAdjustBoundsToCamera() {
        return autoAdjustBoundsToCamera;
    }

    public void setAutoAdjustBoundsToCamera(boolean value) {
        this.autoAdjustBoundsToCamera = value;
    }

    private class CameraListener implements PropertyChangeListener {
        private double prevViewScale;

        public void propertyChange(PropertyChangeEvent evt) {
            final String prop = evt.getPropertyName();
            if (prop == PCamera.PROPERTY_VIEW_TRANSFORM) {
                final double viewScale = getCamera().getViewScale();
                if (Math.abs(prevViewScale - viewScale) > viewScale * 10e-4) {
                    // System.out.println("scale: " + prevViewScale + " -> " +
                    // viewScale + " diff: " +
                    // Math.abs(prevViewScale - viewScale) + " " + viewScale *
                    // 10e-4);
                    prevViewScale = viewScale;
                    invalidateCache();
                }
            } else if (prop == PCamera.PROPERTY_BOUNDS) {
                adjustBoundsToCamera();
                invalidateCache();
            }
        }
    }

    private class MouseHandler extends PBasicInputEventHandler {
        @Override
        public void mouseDragged(PInputEvent event) {
            final PDimension delta = event.getCanvasDelta();
            final PCamera cam = getCamera();
            if (isHorizontal()) {
                setY(getY() + delta.height);
                cam.translateView(cam.localToView(
                        new PDimension(delta.width, 0)).getWidth(), 0);
            } else {
                setX(getX() + delta.width);
                cam.translateView(0, cam.localToView(
                        new PDimension(0, delta.height)).getHeight());
            }
            adjustBoundsToCamera();
            event.setHandled(true);
        }

        @Override
        public void mouseExited(PInputEvent event) {
            event.setHandled(true);
        }
    }

    public PCamera getCamera() {
        final PNode parent = getParent();
        if (parent == null) {
            return null;
        } else {
            return (PCamera) parent;
        }
    }

    protected PCanvas getCanvas() {
        final PCamera camera = getCamera();
        if (camera == null) {
            return null;
        }
        return (PCanvas) camera.getComponent();
    }

    @Override
    public void setParent(PNode parent) {
        if (parent != null  &&   !(parent instanceof PCamera)) {
            throw new IllegalArgumentException(
                    "Parent must be an instance of PCamera");
        }
        final PNode oldParent = getParent();
        if (oldParent != null) {
            oldParent.removePropertyChangeListener(cameraListener);
        }
        super.setParent(parent);
        if (parent != null) {
            if (autoAdjustBoundsToCamera) {
                adjustBoundsToCamera();
            }
            parent.addPropertyChangeListener(cameraListener);
        }
    }

    public void adjustBoundsToCamera() {
        final PCamera cam = getCamera();
        final PBounds cb = cam.getBoundsReference();
        final PBounds b = getBoundsReference();
        if (isHorizontal()) {
            double y = b.getY();
            final double h = b.getHeight() > 0 ? b.getHeight() : 50;

            final double minY = cb.getMinY(); /*- getHeight()  * .5 */;
            if (y < minY) {
                y = minY;
            } else {
                final double maxY = cb.getMaxY() - getHeight()/* * 0.5 */;
                if (y > maxY) {
                    y = maxY;
                }
            }

            setBounds(cb.getX(), y, cb.getWidth(), h);
        } else {
            double x = b.getX();
            final double w = b.getWidth() > 0 ? b.getWidth() : 50;

            final double minX = cb.getMinX();
            if (x < minX) {
                x = minX;
            } else {
                final double maxX = cb.getMaxX() - getWidth() /* * 0.5 */;
                if (x > maxX) {
                    x = maxX;
                }
            }
            setBounds(x, cb.getY(), w, cb.getHeight());
        }
    }

    public void addDisjointNode(PNode node) {
        if (disjointNodes == null) {
            disjointNodes = new ArrayList<PNode>();
        }
        disjointNodes.add(node);
    }

    public Area getClip(PPaintContext paintContext) {
        if (disjointNodes == null || disjointNodes.size() == 0) {
            return null;
        }

        Area area = null;
        for (PNode node : disjointNodes) {
            if (node.getVisible()) {
                if (area == null) {
                    final Rectangle2D localClip = paintContext.getLocalClip();
                    final Rectangle2D r = new Rectangle2D.Double();
                    Rectangle2D.intersect(localClip, getBoundsReference(), r);
                    area = new Area(r);
                }
                area.subtract(new Area(node.getBoundsReference()));
            }
        }

        return area;
    }

    public int getRenderQuality() {
        return renderQuality;
    }

    public void setRenderQuality(int renderQuality) {
        this.renderQuality = renderQuality;
    }

    private transient Image buffer;
    private transient Graphics2D bufferGraphics;
    private transient int bufferWidth;
    private transient int bufferHeight;
    private boolean isDirty = true;

    public void invalidateCache() {
        this.isDirty = true;
        this.buffer = null;
    }

    @Override
    protected void paint(PPaintContext paintContext) {
        final int oldRenderQuality = paintContext.getRenderQuality();
        paintContext.setRenderQuality(getRenderQuality());

        final Graphics2D g2 = paintContext.getGraphics();

        final PCamera camera = getCamera();

        final PBounds b = getBoundsReference();

        // Calc buffer size
        final int bw, bh;
        if (isHorizontal()) {
            bw = (int) Math.ceil(getContentWidth());
            bh = (int) Math.ceil(b.height);
        } else {
            bw = (int) Math.ceil(b.width);
            bh = (int) Math.ceil(getContentHeight());
        }

        // Calc location on screen
        int bx, by;
        final PBounds vb = camera.getViewBounds();
        final double scale = camera.getViewScale();
        if (isHorizontal()) {
            bx = (int) Math.floor(b.x - vb.getX()  * scale);
            by = (int) Math.floor(b.y);
        } else {
            bx = (int) Math.floor(b.x);
            by = (int) Math.floor(b.y -vb.getY() * scale );
        }


        // Clip
        final Shape clip = getClip(paintContext);
        final Shape oldClip;
        if (clip != null) {
            oldClip = g2.getClip();
            g2.setClip(clip);
        } else {
            oldClip = null;
        }

        final Paint paint = getPaint();

        // Draw parts of the background non-intersecting with
        if (paint != null) {
            g2.setPaint(paint);
        } else {
            g2.setColor(DEFAULT_BG_COLOR);
        }

        final int fbx = (int) Math.floor(b.x);
        final int fby = (int) Math.floor(b.y);
        final int fbw = (int) Math.floor(b.width);
        final int fbh = (int) Math.floor(b.height);
        if (isHorizontal()) {
            g2.fillRect(fbx, fby, bx - fbx, fbh);
            g2.fillRect(bx + bw, fby, fbw - bw + (fbx - bx), fbh);
        } else {
            g2.fillRect(fbx, fby, fbw, by - fby);
            g2.fillRect(fbx, by + bh, fbw, fbh - bh + (fby - by));
        }


        if (useContentCache) {
            // Create buffer
            if (buffer == null || bufferGraphics == null || bufferWidth < bw
                    || bufferHeight < bh) {
                final PCanvas canvas = getCanvas();
                buffer = canvas.createImage(bw, bh);
                bufferWidth = bw;
                bufferHeight = bh;
                bufferGraphics = (Graphics2D) buffer.getGraphics();
                bufferGraphics.setRenderingHints(g2.getRenderingHints());
                if (paint != null) {
                    bufferGraphics.setPaint(paint);
                } else {
                    bufferGraphics.setPaint(DEFAULT_BG_COLOR);
                }
                bufferGraphics.fillRect(0, 0, bw, bh);
            }

            // Paint into buffer
            if (isDirty) {
                paintContent(bufferGraphics, 0, 0);
                isDirty = false;
            }

            // Copy from buffer to screen
            g2.drawImage(buffer, bx, by, bw, bh, TRANSPARENT_COLOR, null);
        } else {
            if (paint != null) {
                g2.setPaint(paint);
            } else {
                g2.setPaint(DEFAULT_BG_COLOR);
            }
            g2.fillRect(bx, by, bw, bh);
            paintContent(g2, bx, by);
        }

        // Restore render quality
        paintContext.setRenderQuality(oldRenderQuality);

        // Unclip
        if (oldClip != null) {
            g2.setClip(oldClip);
        }
    }

    protected abstract int getContentWidth();

    protected abstract int getContentHeight();

    protected void paintContent(Graphics2D g2, int offsetX, int offsetY) {
    }

}
