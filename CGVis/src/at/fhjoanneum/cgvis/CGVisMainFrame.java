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
package at.fhjoanneum.cgvis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.apache.log4j.Logger;

import at.fhj.utils.misc.FileUtils;
import at.fhj.utils.misc.ProgressTracker;
import at.fhj.utils.swing.InternalFrameUtils;
import at.fhj.utils.swing.JMemoryIndicator;
import at.fhj.utils.swing.JMsgPane;
import at.fhj.utils.swing.ProgressDialog;
import at.fhj.utils.swing.ProgressWorker;
import at.fhjoanneum.cgvis.actions.ImageExportAction;
import at.fhjoanneum.cgvis.actions.OpenFileAction;
import at.fhjoanneum.cgvis.data.AbstractDataSource;
import at.fhjoanneum.cgvis.data.CSVDataSource;
import at.fhjoanneum.cgvis.data.CompoundDataSource;
import at.fhjoanneum.cgvis.data.DataUID;
import at.fhjoanneum.cgvis.data.IDataSource;
import at.fhjoanneum.cgvis.data.IPointSet;
import at.fhjoanneum.cgvis.data.PointSetNorm;
import at.fhjoanneum.cgvis.data.Query;
import at.fhjoanneum.cgvis.data.PointSetNorm.NormType;
import at.fhjoanneum.cgvis.plots.mosaic.MosaicView;

import com.thoughtworks.xstream.XStream;

/**
 * @author Ilya Boyandin
 */
public class CGVisMainFrame extends JFrame implements IViewManager {

    private static final long serialVersionUID = -650003574986872345L;

    private static final String PREFERENCES_FILE_NAME = ".preferences";

    private final Logger logger = Logger.getLogger(getClass().getName());

    private JCheckBoxMenuItem viewOptionsMenuItem;
    // private List<JInternalFrame> openViews = new ArrayList<JInternalFrame>();
    private final List<IView> openViews = new ArrayList<IView>();
    private Container viewOptionsDialogEmptyPanel;
    private final JMenuBar menubar;
    private final JDesktopPane desktopPane;
    private JDialog viewOptionsDialog;
    private final JToolBar actionsToolBar;
    private final JToolBar controlsToolBar;
    private final JPanel toolBarPanel;

    private XStream xstream;
    private Preferences preferences;

    private String appStartDir;

    private IView activeView;

    private Action openFileAction;
    private Action imageExportAction;

    private Action showHelpAction;
    private Action tileViewsAction;
    private Action fitInViewAction;
    private Action clearSelectionAction;

    private DataUID[] globalElementSelection;

    public CGVisMainFrame() {
        super("CGVis");

        initActions();
        initKeystrokes();

        toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BoxLayout(toolBarPanel, BoxLayout.X_AXIS));
        add(toolBarPanel, BorderLayout.NORTH);

        actionsToolBar = buildActionsToolBar();
        toolBarPanel.add(actionsToolBar);

        controlsToolBar = buildControlsToolBar();
        toolBarPanel.add(controlsToolBar);
        controlsToolBar.setVisible(false);

        menubar = buildMenuBar();
        setJMenuBar(menubar);

        desktopPane = new JDesktopPane();
        desktopPane.setBackground(Color.gray);
        add(desktopPane, BorderLayout.CENTER);

        final JPanel statusPanel = new JPanel(new BorderLayout());
        add(statusPanel, BorderLayout.SOUTH);

        final JMemoryIndicator mi = new JMemoryIndicator(3000);
        statusPanel.add(mi, BorderLayout.EAST);
        mi.startUpdater();

        setPreferredSize(new Dimension(1000, 700));
        pack();

        final Dimension size = getSize();
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final int locX = (screen.width - size.width) / 2;
        final int locY = (screen.height - size.height) / 2;
        setLocation(locX, locY);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
    }

    private void initActions() {
        openFileAction = new OpenFileAction(this);
        imageExportAction = new ImageExportAction(this);
        imageExportAction.setEnabled(false);

        showHelpAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                new KeyboardHelpDialog(CGVisMainFrame.this).setVisible(true);
            }
        };
        tileViewsAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                InternalFrameUtils.tile(desktopPane);
            }
        };
        fitInViewAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                final IView view = findViewByFrame(desktopPane
                        .getSelectedFrame());
                if (view instanceof IZoomableView) {
                    ((IZoomableView) view).fitInCameraView(true);
                }
            }
        };
        clearSelectionAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                final IView view = findViewByFrame(desktopPane
                        .getSelectedFrame());
                if (view != null) {
                    view.clearSelection();
                }
            }
        };
    }

    private void initKeystrokes() {
        final ActionMap am = getRootPane().getActionMap();
        final InputMap im = getRootPane().getInputMap(
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0, false), "SHOW_HELP");
        am.put("SHOW_HELP", showHelpAction);

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.ALT_DOWN_MASK,
                false), "TILE_VIEWS");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0, false), "TILE_VIEWS");
        am.put("TILE_VIEWS", tileViewsAction);

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0, false), "FIT_IN_VIEW");
        am.put("FIT_IN_VIEW", fitInViewAction);

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false),
                "CLEAR_SELECTION");
        im
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_MASK,
                        false), "CLEAR_SELECTION");
        am.put("CLEAR_SELECTION", clearSelectionAction);
    }

    public void init() {
        setAppStartDir(System.getProperty("user.dir"));
        loadPreferences();
    }

    public void shutdown() {
        savePreferences();
        logger.info("Exiting application");
        System.exit(0);
    }

    private JToolBar buildActionsToolBar() {
        JToolBar tb = new JToolBar();
        tb.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
        tb.setFloatable(true);
        tb.add(openFileAction);
        tb.add(imageExportAction);
        tb.addSeparator();
        return tb;
    }

    private JToolBar buildControlsToolBar() {
        JToolBar tb = new JToolBar();
        tb.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
        tb.setFloatable(true);
        return tb;
    }

    private JMenuBar buildMenuBar() {
        final JMenuBar mb = new JMenuBar();
        JMenu menu, subMenu;
        JMenuItem item;

        menu = new JMenu("File");
        mb.add(menu);

        menu.add(openFileAction);

        // menu.addSeparator();
        /*
         * item = new JMenuItem("FACS.csv"); item.addActionListener(new
         * ActionListener() { public void actionPerformed(ActionEvent e) { try {
         * openScatterPlot("data/FACS.csv"); } catch (DataSourceException dse) {
         * dse.printStackTrace(); JMsgPane.showErrorDialog(CGVisMainFrame.this,
         * dse); } } }); menu.add(item);
         */

        // TODO: recent files
        menu.addSeparator();

        item = new JMenuItem("Exit");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });
        menu.add(item);

        menu = new JMenu("Window");
        mb.add(menu);

        subMenu = new JMenu("Arrange");
        menu.add(subMenu);

        item = new JMenuItem("Cascade");
        item.setEnabled(false);
        subMenu.add(item);
        item = new JMenuItem("Tile");
        item.addActionListener(tileViewsAction);
        subMenu.add(item);

        item = new JMenuItem("Tile horizontally");
        item.setEnabled(false);
        subMenu.add(item);
        item = new JMenuItem("Tile vertically");
        item.setEnabled(false);
        subMenu.add(item);
        item = new JMenuItem("Maximize all");
        item.setEnabled(false);
        subMenu.add(item);
        item = new JMenuItem("Minimize all");
        item.setEnabled(false);
        subMenu.add(item);

        menu.addSeparator();

        viewOptionsMenuItem = new JCheckBoxMenuItem("View Options");
        menu.add(viewOptionsMenuItem);
        viewOptionsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (viewOptionsMenuItem.isSelected()) {
                    showViewOptionsDialog();
                } else {
                    hideViewOptionsDialog();
                }
            }
        });

        item = new JCheckBoxMenuItem("Views List", false);
        item.setEnabled(false);
        menu.add(item);

        item = new JCheckBoxMenuItem("Correlations", false);
        item.setEnabled(false);
        menu.add(item);

        menu.addSeparator();

        item = new JMenuItem("Preferences...");
        item.setEnabled(false);
        menu.add(item);

        menu = new JMenu("Help");
        mb.add(menu);

        item = new JMenuItem("Keyboard shortcuts");
        item.addActionListener(showHelpAction);
        menu.add(item);

        item = new JMenuItem("About");
        menu.add(item);

        return mb;
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        // showViewOptionsDialog();
    }

    public JDesktopPane getDesktopPane() {
        return desktopPane;
    }

    public String getAppStartDir() {
        return appStartDir;
    }

    public void setAppStartDir(String appStartDir) {
        this.appStartDir = appStartDir;
    }

    protected void loadPreferences() {
        Preferences prefs = null;
        final String fileName = getPreferencesFileName();
        if (new File(fileName).isFile()) {
            logger.info("Loading preferences from " + fileName);
            try {
                prefs = (Preferences) getXStream().fromXML(
                        new FileReader(fileName));
            } catch (Throwable th) {
                logger.error("Loading preferences failed", th);
            }
        }
        if (prefs == null) {
            prefs = new Preferences();
        }
        this.preferences = prefs;
    }

    protected void savePreferences() {
        final String fileName = getPreferencesFileName();
        logger.info("Saving preferences to " + fileName);
        try {
            getXStream().toXML(preferences, new FileWriter(fileName));
        } catch (Throwable th) {
            logger.error("Loading preferences failed", th);
        }
    }

    protected String getPreferencesFileName() {
        return getAppStartDir() + File.separator + PREFERENCES_FILE_NAME;
    }

    public XStream getXStream() {
        if (xstream == null) {
            xstream = new XStream();
            xstream.alias("preferences", Preferences.class);
            xstream.alias("view-preferences", ViewPreferences.class);
        }
        return xstream;
    }

    private void setActiveView(IView view) {
        if (view != null) {
            setViewOptionsDialogContent(view.getOptionsComponent());

            if (activeView != null) {
                removeToolbarActions(activeView.getToolbarActions());
                removeToolbarControls(view.getToolbarControls());
            }
            addToolbarActions(view.getToolbarActions());
            addToolbarControls(view.getToolbarControls());
            actionsToolBar.repaint();
        } else {
            setViewOptionsDialogContent(null);
            if (activeView != null) {
                removeToolbarActions(activeView.getToolbarActions());
                removeToolbarControls(activeView.getToolbarControls());
                actionsToolBar.repaint();
            }
        }
        this.activeView = view;
    }

    private void addToolbarActions(final Action[] actions) {
        if (actions != null) {
            for (Action a : actions) {
                actionsToolBar.add(a);
            }
        }
    }

    private void removeToolbarActions(Action[] actions) {
        if (actions == null) {
            return;
        }
        for (int i = actionsToolBar.getComponentCount() - 1; i >= 0; i--) {
            final Component item = actionsToolBar.getComponent(i);
            if (item instanceof AbstractButton) {
                final Action action = ((AbstractButton) item).getAction();
                for (Action a : actions) {
                    if (a == action) {
                        actionsToolBar.remove(item);
                        break;
                    }
                }
            }
        }
    }

    private void addToolbarControls(final JComponent toolbarControls) {
        if (toolbarControls != null) {
            if (controlsToolBar.getComponentCount() == 0) {
                controlsToolBar.setVisible(true);
            }
            // controlsToolBar.addSeparator();s
            controlsToolBar.add(toolbarControls);
        }
    }

    private void removeToolbarControls(final JComponent toolbarControls) {
        if (toolbarControls == null) {
            return;
        }
        final int index = controlsToolBar.getComponentIndex(toolbarControls);
        if (index > -1) {
            // final Component separator = controlsToolBar.getComponent(index -
            // 1);
            // if (separator instanceof JSeparator) {
            // controlsToolBar.remove(separator);
            // }
            controlsToolBar.remove(toolbarControls);
            controlsToolBar.updateUI(); // fixes a memory leak (otherwise a
                                        // controlsToolBar reference is
            // kept somewhere in the UI even after removal), probably a bug in
            // JGoodies Windows LF
            if (controlsToolBar.getComponentCount() == 0) {
                controlsToolBar.setVisible(false);
            }
        }
    }

    private void setViewOptionsDialogContent(JComponent contentPane) {
        if (viewOptionsDialog != null) {
            final JRootPane rootPane = viewOptionsDialog.getRootPane();
            if (contentPane == null) {
                rootPane.setContentPane(viewOptionsDialogEmptyPanel);
            } else {
                rootPane.setContentPane(contentPane);
            }
            rootPane.revalidate();
            rootPane.repaint();
        }
    }

    public void showViewOptionsDialog() {
        viewOptionsDialog = new JDialog(this, "View Options");
        viewOptionsDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                viewOptionsMenuItem.setSelected(false);
            }
        });
        viewOptionsDialogEmptyPanel = viewOptionsDialog.getContentPane();

        if (activeView != null) {
            setViewOptionsDialogContent(activeView.getOptionsComponent());
        }

        final Dimension vpsize = new Dimension(250, 170);
        viewOptionsDialog.setSize(vpsize);

        final Point dploc = desktopPane.getLocationOnScreen();
        final Dimension dpsize = desktopPane.getSize();

        viewOptionsDialog.setLocation(dploc.x + dpsize.width - vpsize.width,
                dploc.y);
        viewOptionsDialog.setVisible(true);

        viewOptionsDialog.setVisible(true);
    }

    public void hideViewOptionsDialog() {
        viewOptionsDialog.dispose();
        viewOptionsDialog = null;
        viewOptionsDialogEmptyPanel = null;
    }

    public void showView(final IView view) {
        view.init();

        final JInternalFrame frame = new JInternalFrame(view.getTitle(), true,
                true, true, true);

        final JDesktopPane desktopPane = getDesktopPane();
        desktopPane.add(frame);

        frame.setContentPane(view.getViewComponent());
        view.setFrame(frame);
        if (globalElementSelection != null) {
            view.setElementSelection(globalElementSelection);
        }
        frame.setPreferredSize(new Dimension(800, 600));
        frame.pack();

        final int offset = openViews.size() * 16;
        frame.setLocation(offset, offset);

        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                setActiveView(view);
                updateActions();
            }

            @Override
            public void internalFrameDeactivated(InternalFrameEvent e) {
                setActiveView(null);
                updateActions();
            }

            @Override
            public void internalFrameOpened(InternalFrameEvent e) {
                openViews.add(view);
                updateActions();
            }

            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                openViews.remove(view);
                view.setFrame(null);
                updateActions();
            }
        });

        view.initInFrame();

        frame.setVisible(true);
    }

    public void updateActions() {
        final JInternalFrame sf = desktopPane.getSelectedFrame();
        if (sf == null) {
            imageExportAction.setEnabled(false);
        } else {
            imageExportAction.setEnabled(true);
        }
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public void openFile(String fileName, boolean normalize) {
        final ProgressTracker progress = new ProgressTracker();
        final OpenFileWorker worker = new OpenFileWorker(fileName, normalize,
                this, progress);
        final ProgressDialog progressDlg = new ProgressDialog(this, "Loading "
                + FileUtils.getFilename(fileName), worker, false);
        progress.addProgressListener(progressDlg);
        worker.start();
        progressDlg.setVisible(true);
    }

    private class OpenFileWorker extends ProgressWorker {

        private final String fileName;
        private final JFrame parentFrame;
        private final boolean normalize;

        public OpenFileWorker(String fileName, boolean normalize,
                JFrame parentFrame, ProgressTracker progress) {
            super(progress);
            this.fileName = fileName;
            this.normalize = normalize;
            this.parentFrame = parentFrame;
        }

        @Override
        public Object construct() {
            try {
                logger.info("Opening file " + fileName);

                IDataSource ds = createDatasource(fileName);
                if (ds != null) {
                    ds.init(getProgressTracker());

                    if (!progress.isCancelled()) {
                        final int psetNum = (Integer) ds.query(new Query(
                                AbstractDataSource.GET_POINT_SET_NUM));
                        final IPointSet[] psets = new IPointSet[psetNum];
                        for (int i = 0; i < psetNum; i++) {
                            final IPointSet ps = (IPointSet) ds
                                    .query(new Query(
                                            AbstractDataSource.GET_POINT_SET, i));
                            if (normalize) {
                                psets[i] = new PointSetNorm(ps,
                                        NormType.SCALE_0_1);
                            } else {
                                psets[i] = ps;
                            }
                        }

                        if (!progress.isCancelled()) {
                            showView(new MosaicView(FileUtils
                                    .getFilename(fileName), psets, preferences
                                    .getViewPreferences(), CGVisMainFrame.this));
                            progress.processFinished();
                        }
                    }
                }

            } catch (final Throwable th) {
                JMsgPane.showErrorDialog(parentFrame,
                        "File couldn't be loaded: " + th.getMessage());
                logger.error("File open failed: ", th);
                if (th.getCause() != null) {
                    logger.error("Reason: ", th.getCause());
                }
            }
            return null;
        }
    }

    protected IDataSource createDatasource(String fileName) {
        IDataSource ds = null;
        final String ext = "." + FileUtils.getExtension(fileName);
        if (CompoundDataSource.FILE_EXTENSION.equalsIgnoreCase(ext)) {
            ds = new CompoundDataSource(fileName);
        } else if (CSVDataSource.FILE_EXTENSION.equalsIgnoreCase(ext)) {
            ds = new CSVDataSource(fileName, "\t;" /* '\t' */);
        }
        return ds;
    }

    public boolean confirmExit() {
        if (openViews.size() > 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Close all views and exit application?", "Exit",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public DataUID[] getGlobalElementSelection() {
        return globalElementSelection;
    }

    public void setGlobalElementSelection(DataUID[] selection) {
        this.globalElementSelection = selection;
    }

    public void fireElementSelectionChanged(IView sourceView,
            DataUID[] selection) {
        setGlobalElementSelection(selection);
        for (int i = 0, size = openViews.size(); i < size; i++) {
            final IView view = openViews.get(i);
            if (view != sourceView) {
                view.setElementSelection(selection);
            }
        }
    }

    public IView findViewByFrame(JInternalFrame frame) {
        for (int i = 0, size = openViews.size(); i < size; i++) {
            final IView view = openViews.get(i);
            if (view.getFrame() == frame) {
                return view;
            }
        }
        return null;
    }

    private void exit() {
        if (confirmExit()) {
            dispose();
            shutdown();
        }
    }

}
