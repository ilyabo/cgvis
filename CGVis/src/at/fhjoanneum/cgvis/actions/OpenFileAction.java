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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

import at.fhj.utils.swing.JMsgPane;
import at.fhjoanneum.cgvis.CGVisMainFrame;
import at.fhjoanneum.cgvis.data.CSVDataSource;
import at.fhjoanneum.cgvis.data.CompoundDataSource;

/**
 * @author Ilya Boyandin
 */
public class OpenFileAction extends AbstractAction {

    private static final long serialVersionUID = -5822859059452128994L;
    private final CGVisMainFrame app;

    public OpenFileAction(CGVisMainFrame app) {
        this.app = app;

        putValue(Action.NAME, "Open File...");
        putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource(
                "res/Open16-2.gif")));
        putValue(Action.SHORT_DESCRIPTION, "Open File");
        putValue(Action.LONG_DESCRIPTION, "Open file");
        putValue(Action.ACTION_COMMAND_KEY, "open-file");
    }

    public void actionPerformed(ActionEvent e) {
        try {
            final JFileChooser fc = new JFileChooser();
            fc.setAcceptAllFileFilterUsed(false);
            fc.setMultiSelectionEnabled(false);
            final String lastVisitedDir = app.getPreferences()
                    .getFileOpenLastVisitedDir();
            final String dir;
            if (lastVisitedDir != null) {
                dir = lastVisitedDir;
            } else {
                dir = System.getProperty("user.dir");
            }
            if (dir != null) {
                fc.setCurrentDirectory(new File(dir));
            }
            final FileFilter currentFilter = getAllSupportedFilesFilter();
            fc.addChoosableFileFilter(currentFilter);
            fc.addChoosableFileFilter(getCGVFileFilter());
            fc.addChoosableFileFilter(getCSVFileFilter());
            fc.setFileFilter(currentFilter);
            fc.setAcceptAllFileFilterUsed(false);
            final JPanel accessory = new JPanel(new BorderLayout(5, 5));
            accessory.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            final JCheckBox normalizeChk = new JCheckBox("Normalize data",
                    true);
            accessory.add(normalizeChk, BorderLayout.NORTH);
            fc.setAccessory(accessory);

            int confirm = fc.showDialog(app, (String) getValue(Action.NAME));
            if (confirm == JFileChooser.APPROVE_OPTION) {
                app.openFile(fc.getSelectedFile().getAbsolutePath(),
                        normalizeChk.isSelected());
                app.getPreferences().setFileOpenLastVisitedDir(
                        fc.getSelectedFile().getParent());
            }
        } catch (Throwable th) {
            JMsgPane.showProblemDialog(app, "File couldn't be loaded: "
                    + th.getMessage());
            Logger.getLogger(getClass().getName()).error("Exception: ", th);
        }
    }

    private FileFilter cgvFileFilter;
    private FileFilter csvFileFilter;
    private FileFilter allSupportedFilesFilter;

    private FileFilter getCGVFileFilter() {
        if (cgvFileFilter == null) {
            cgvFileFilter = new FileFilter() {
                public boolean accept(File f) {
                    return (f.isDirectory() || f.getName().endsWith(
                            CompoundDataSource.FILE_EXTENSION));
                }

                public String getDescription() {
                    return "CGVis files (*" + CompoundDataSource.FILE_EXTENSION
                            + ")";
                }
            };
        }
        return cgvFileFilter;
    }

    private FileFilter getCSVFileFilter() {
        if (csvFileFilter == null) {
            csvFileFilter = new FileFilter() {
                public boolean accept(File f) {
                    return (f.isDirectory() || f.getName().endsWith(
                            CSVDataSource.FILE_EXTENSION));
                }

                public String getDescription() {
                    return "CSV files (*" + CSVDataSource.FILE_EXTENSION + ")";
                }
            };
        }
        return csvFileFilter;
    }

    private FileFilter getAllSupportedFilesFilter() {
        if (allSupportedFilesFilter == null) {
            allSupportedFilesFilter = new FileFilter() {
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    }
                    final String nam = f.getName();
                    return nam.endsWith(CompoundDataSource.FILE_EXTENSION)
                            || nam.endsWith(CSVDataSource.FILE_EXTENSION);
                }

                public String getDescription() {
                    return "All Supported Files (" + "*"
                            + CompoundDataSource.FILE_EXTENSION + ";" + "*"
                            + CSVDataSource.FILE_EXTENSION + ")";
                }
            };
        }
        return allSupportedFilesFilter;
    }

}
