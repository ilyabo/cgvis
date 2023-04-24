package at.fhj.utils.swing;

import java.awt.Rectangle;
import java.beans.PropertyVetoException;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

/**
 * @author Ilya Boyandin
 */
public class InternalFrameUtils {

    private InternalFrameUtils() {
    }

    public static void tile(JDesktopPane desktopPane) {
        tile(desktopPane.getAllFrames());
    }


    public static void tile(JInternalFrame[] frames) {
        if (frames.length == 0) {
            return;
        }
        JDesktopPane desktopPane = frames[0].getDesktopPane();

        final int numViews = frames.length;
        int numVisViews = 0;
        int iconHeight = 0;
        JInternalFrame oldFocusOwner = null;
        for (JInternalFrame vf : frames) {
            if (!vf.isIcon()) {
                numVisViews++;
            } else {
                if (iconHeight == 0) {
                    iconHeight = (int) vf.getMinimumSize().getHeight();
                }
            }
            if (vf.isFocusOwner()) {
                oldFocusOwner = vf;
            }
        }

        final double sqrt = Math.sqrt(numVisViews);
        final int numRows = (int) Math.round(sqrt);
        final int numCols = (int) Math.ceil(sqrt);

        final Rectangle bounds = desktopPane.getBounds();
        final int width = (int) Math.floor(bounds.getWidth() / numCols);
        final int height = (int) Math.floor((bounds.getHeight() - iconHeight)
                / numRows);

        int n = 0;
        int i = 0, j = 0;
        while (n < numViews) {
            final JInternalFrame vf = frames[n];
            if (!vf.isIcon()) {
                if (vf.isMaximum()) {
                    try {
                        vf.setMaximum(false);
                    } catch (PropertyVetoException e) {
                        // there is nothing we can do about it
                    }
                }
                vf.setBounds(i * width, j * height, width, height);
                i++;
                if (i >= numCols) {
                    j++;
                    i = 0;
                }
            }
            n++;
        }

        if (oldFocusOwner != null) {
            oldFocusOwner.requestFocus();
        }
    }

}
