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
package at.fhjoanneum.cgvis.actions;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;

import org.apache.log4j.Logger;

import at.fhj.utils.graphics.plot.ImageExporter;
import at.fhj.utils.graphics.plot.ImagePainter;
import at.fhj.utils.misc.StringUtils;
import at.fhj.utils.swing.JMsgPane;
import at.fhjoanneum.cgvis.CGVisMainFrame;
import at.fhjoanneum.cgvis.IView;
import at.fhjoanneum.cgvis.Preferences;

/**
 * @author Ilya Boyandin
 */
public class ImageExportAction extends AbstractAction {

    private static final long serialVersionUID = -6704637296724502607L;

    private Logger logger = Logger.getLogger(getClass().getName());

    private final CGVisMainFrame app;

    public ImageExportAction(CGVisMainFrame app) {
        this.app = app;

        putValue(Action.NAME, "Export to image...");
        putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource(
                "res/ImageExport.gif")));
        putValue(Action.SHORT_DESCRIPTION, "Export to image");
        putValue(Action.LONG_DESCRIPTION, "Export to image");
        putValue(Action.ACTION_COMMAND_KEY, "image-export");
    }

    public void actionPerformed(ActionEvent e) {
        final IView view = app.findViewByFrame(app.getDesktopPane()
                .getSelectedFrame());
        if (view != null) {
            exportImageToFile(view);
        }
    }

    public void exportImageToFile(IView view) {
        final JInternalFrame frame = view.getFrame();
        final ImageExporter ie = new ImageExporter();
        final File destFile = ie.showFileDialog(frame.getRootPane(),
                Preferences.getImageExportLastVisitedDir(), StringUtils
                        .replaceAll(view.getTitle(), ".", "_"));
        if (destFile != null) {
            logger.info("Exporting view " + view.getTitle() + " to image");
            final ImagePainter painter = new ImagePainter() {
                public Dimension getSize() {
                    return frame.getContentPane().getSize();
                }

                public void paintImage(Graphics g, int x, int y) {
                    g.translate(x, y);
                    frame.getContentPane().paint(g);
                    g.translate(-x, -y);
                }
            };
            try {
                ie.exportImageToFile(view.getTitle(), painter, destFile);
                Preferences.setImageExportLastVisitedDir(destFile.getParent());
            } catch (IOException ex) {
                JMsgPane.showErrorDialog(frame, "Couldn't save image "
                        + ex.getMessage());
                logger.error("Image export failed", ex);
            }
        }
    }

}
