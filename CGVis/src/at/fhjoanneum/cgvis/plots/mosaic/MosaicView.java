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
package at.fhjoanneum.cgvis.plots.mosaic;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import at.fhjoanneum.cgvis.IViewManager;
import at.fhjoanneum.cgvis.IZoomableView;
import at.fhjoanneum.cgvis.ViewPreferences;
import at.fhjoanneum.cgvis.actions.ShowScatterPlotViewAction;
import at.fhjoanneum.cgvis.data.AttrSelection;
import at.fhjoanneum.cgvis.data.DataUID;
import at.fhjoanneum.cgvis.data.IPointSet;
import at.fhjoanneum.cgvis.plots.AbstractView;
import at.fhjoanneum.cgvis.plots.FitInCameraViewAction;

/**
 * @author Ilya Boyandin
 */
public class MosaicView extends AbstractView implements IZoomableView {

    private MosaicCanvas canvas;
    private ViewPreferences preferences;

    public MosaicView(String title, IPointSet[] pointSets,
            ViewPreferences preferences, IViewManager viewManager) {
        super(title, viewManager);
        this.preferences = preferences;
        this.canvas = new MosaicCanvas(this, pointSets, preferences);
    }

    public void init() {
        canvas.init();
    }

    public void initInFrame() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                fitInCameraView(false);
            }
        });
    }

    public ViewPreferences getViewPreferences() {
        return preferences;
    }

    public void fitInCameraView(boolean animate) {
        canvas.fitInCameraView(animate);
    }

    public JComponent getViewComponent() {
        return canvas;
    }

    public JPanel getOptionsComponent() {
        return null;
    }

    private transient Action[] toolbarActions;
    private JPanel toolbarControls;
    private JCheckBox attrsDendrogramChk;
    private JCheckBox elemsDendrogramChk;

    public Action[] getToolbarActions() {
        if (toolbarActions == null) {
            toolbarActions = new Action[] { new FitInCameraViewAction(this),
                    new ClusterAction(this),
                    new ShowScatterPlotViewAction(this) };
        }
        return toolbarActions;
    }

    public void startClustering() {
        canvas.startClustering();
    }

    public AttrSelection[] getSelection() {
        return canvas.getAttrSelection();
    }

    public JComponent getToolbarControls() {
        if (toolbarControls == null) {
            final JPanel panel = new JPanel(new GridBagLayout());
            panel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
            final GridBagConstraints c = new GridBagConstraints();

            GridLayout layout = new GridLayout();
            layout.setColumns(10);
            layout.setHgap(5);

            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.NONE;
            c.gridx = GridBagConstraints.RELATIVE;
            c.gridy = 0;

            final Insets checkboxInsets = new Insets(0, 5, 0, 0);
            // final Insets labelInsets = new Insets(0, 10, 0, 0);
            // final Insets inputInsets = new Insets(0, 3, 0, 0);

            c.insets = checkboxInsets;
            final JCheckBox attrsChk = new JCheckBox("Attributes", canvas
                    .getShowAttributeLabels());
            panel.add(attrsChk);
            attrsChk.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    canvas.setShowAttributeLabels(attrsChk.isSelected());
                }
            });

            c.insets = checkboxInsets;
            final JCheckBox elemsChk = new JCheckBox("Elements", canvas
                    .getShowElementLabels());
            panel.add(elemsChk);
            elemsChk.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    canvas.setShowElementLabels(elemsChk.isSelected());
                }
            });

            c.insets = checkboxInsets;
            attrsDendrogramChk = new JCheckBox("Attrs cluster tree", canvas
                    .getShowAttrsDendrogram());
            panel.add(attrsDendrogramChk);
            attrsDendrogramChk.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    canvas.setShowAttrsDendrogram(attrsDendrogramChk
                            .isSelected());
                }
            });
            attrsDendrogramChk.setEnabled(false);

            c.insets = checkboxInsets;
            elemsDendrogramChk = new JCheckBox("Elems cluster tree", canvas
                    .getShowElemsDendrogram());
            panel.add(elemsDendrogramChk);
            elemsDendrogramChk.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    canvas.setShowElementsDendrogram(elemsDendrogramChk
                            .isSelected());
                }
            });
            elemsDendrogramChk.setEnabled(false);

            /*
             * final JButton deselectAllBut = new JButton("Deselect all");
             * panel.add(deselectAllBut); deselectAllBut.addActionListener(new
             * ActionListener() { public void actionPerformed(ActionEvent e) {
             * canvas.clearSelection(); } });
             */

            // filler
            c.weightx = 1;
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.EAST;
            panel.add(new JLabel(), c);

            toolbarControls = panel;
        }
        return toolbarControls;
    }

    protected void elementsDendrogramAdded() {
        elemsDendrogramChk.setEnabled(true);
    }

    protected void attrsDendrogramAdded() {
        attrsDendrogramChk.setEnabled(true);
    }

    public void setElementSelection(DataUID[] selection) {
        canvas.setElementSelection(selection);
    }

    public void clearSelection() {
        canvas.clearSelection();
    }
}
