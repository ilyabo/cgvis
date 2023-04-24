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
package at.fhjoanneum.cgvis.plots.scatter;

import static at.fhjoanneum.cgvis.plots.scatter.ScatterPlotCanvas.ATTR_IDX_ELEMENT_ID;
import static at.fhjoanneum.cgvis.plots.scatter.ScatterPlotCanvas.ATTR_IDX_NONE;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import at.fhjoanneum.cgvis.CGVis;
import at.fhjoanneum.cgvis.IPannableView;
import at.fhjoanneum.cgvis.IViewManager;
import at.fhjoanneum.cgvis.IZoomableView;
import at.fhjoanneum.cgvis.ViewPreferences;
import at.fhjoanneum.cgvis.data.DataUID;
import at.fhjoanneum.cgvis.data.IPointSet;
import at.fhjoanneum.cgvis.plots.AbstractView;
import at.fhjoanneum.cgvis.plots.FitInCameraViewAction;
import at.fhjoanneum.cgvis.plots.PanToShowOriginAction;

/**
 * @author Ilya Boyandin
 */
public class ScatterPlotView extends AbstractView implements IZoomableView,
        IPannableView {

    private ScatterPlotCanvas canvas;

    private transient JComponent toolbarControls;
    private transient Action[] toolbarActions;

    public ScatterPlotView(String title, IPointSet plotData,
            ViewPreferences preferences, IViewManager viewManager) {
        super(title, viewManager);
        final int dim = plotData.getDimension();
        canvas = new ScatterPlotCanvas(this, plotData, 0, 1,
                (dim > 2 ? 2 : -1), -1, preferences);
    }

    public JComponent getViewComponent() {
        return canvas;
    }

    public void init() {
        // do nothing
    }

    public void initInFrame() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                fitInCameraView(false);
            }
        });
    }

    public JComponent getOptionsComponent() {
        return null;
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
            final Insets labelInsets = new Insets(0, 10, 0, 0);
            final Insets inputInsets = new Insets(0, 3, 0, 0);

            c.insets = checkboxInsets;
            final JCheckBox gridChk = new JCheckBox("Grid", canvas
                    .getShowGrid());
            panel.add(gridChk);
            gridChk.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    canvas.setShowGrid(gridChk.isSelected());
                }
            });

            // final JCheckBox snapChk = new JCheckBox("Snap",
            // canvas.getSnap());

            c.insets = checkboxInsets;
            final JCheckBox crosshairChk = new JCheckBox("Crosshair", canvas
                    .getShowCrosshair());
            panel.add(crosshairChk);
            crosshairChk.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    final boolean val = crosshairChk.isSelected();
                    canvas.setShowCrosshair(val);
                    // snapChk.setEnabled(val);
                }
            });

            // c.insets = checkboxInsets;
            // panel.add(snapChk);
            // snapChk.addActionListener(new ActionListener() {
            // public void actionPerformed(ActionEvent e) {
            // canvas.setSnap(snapChk.isSelected());
            // }
            // });
            // snapChk.setEnabled(canvas.getShowCrosshair());

            c.insets = checkboxInsets;
            final JCheckBox autofitChk = new JCheckBox("Autofit", canvas
                    .getAutofit());
            panel.add(autofitChk, c);
            autofitChk.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    canvas.setAutofit(autofitChk.isSelected());
                }
            });

            final IPointSet plotData = canvas.getPlotData();

            final ComboItem[] items = new ComboItem[plotData.getDimension()];
            for (int i = 0, n = plotData.getDimension(); i < n; i++) {
                items[i] = new ComboItem(i, plotData.getAttributeLabel(i));
            }
            Arrays.sort(items);

            final Dimension comboPrefSize = new Dimension(120,
                    CGVis.isAqua() ? 25 : 19);
            c.insets = labelInsets;
            panel.add(new JLabel("X:"), c);
            final JComboBox xCombo = new JComboBox(items);
            c.insets = inputInsets;
            panel.add(xCombo, c);
            xCombo.setSelectedIndex(canvas.getXAttrIndex());
            xCombo.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    canvas
                            .setXAttrIndex(((ComboItem) xCombo
                                    .getSelectedItem()).id);
                }
            });
            xCombo.setPreferredSize(comboPrefSize);

            c.insets = labelInsets;
            panel.add(new JLabel("Y:"), c);
            final JComboBox yCombo = new JComboBox(items);
            c.insets = inputInsets;
            panel.add(yCombo, c);
            yCombo.setSelectedIndex(canvas.getYAttrIndex());
            yCombo.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    canvas
                            .setYAttrIndex(((ComboItem) yCombo
                                    .getSelectedItem()).id);
                }
            });
            yCombo.setPreferredSize(comboPrefSize);

            final boolean useRAttr = canvas.getRAttrIndex() > -1;
            final JComboBox rCombo = new JComboBox(items);
            final JCheckBox sizeChk = new JCheckBox("Size:", useRAttr);
            rCombo.setEnabled(useRAttr);
            c.insets = checkboxInsets;
            panel.add(sizeChk, c);
            sizeChk.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (sizeChk.isSelected()) {
                        rCombo.setEnabled(true);
                        ComboItem selItem = (ComboItem) rCombo
                                .getSelectedItem();
                        if (selItem == null && rCombo.getItemCount() > 0) {
                            selItem = (ComboItem) rCombo.getItemAt(0);
                            rCombo.setSelectedIndex(0);
                        }
                        if (selItem != null) {
                            canvas.setRAttrIndex(selItem.id);
                        }
                    } else {
                        rCombo.setEnabled(false);
                        canvas.setRAttrIndex(ATTR_IDX_NONE);
                    }
                }
            });

            // c.insets = labelInsets;
            // panel.add(new JLabel("Size:"), c);
            rCombo.setSelectedIndex(canvas.getRAttrIndex());
            c.insets = inputInsets;
            panel.add(rCombo, c);
            rCombo.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    canvas
                            .setRAttrIndex(((ComboItem) rCombo
                                    .getSelectedItem()).id);
                }
            });
            rCombo.setPreferredSize(comboPrefSize);

            final ComboItem[] itemsWithElemIdx = new ComboItem[plotData
                    .getDimension() + 1];
            itemsWithElemIdx[0] = new ComboItem(ATTR_IDX_ELEMENT_ID,
                    "<-- ELEMENT# -->");
            System.arraycopy(items, 0, itemsWithElemIdx, 1, items.length);

            final boolean useCAttr = (canvas.getCAttrIndex() != ATTR_IDX_NONE);
            final JComboBox cCombo = new JComboBox(itemsWithElemIdx);
            final JCheckBox colorChk = new JCheckBox("Color:", useCAttr);
            cCombo.setEnabled(useCAttr);
            c.insets = checkboxInsets;
            panel.add(colorChk, c);
            colorChk.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (colorChk.isSelected()) {
                        cCombo.setEnabled(true);
                        canvas.setCAttrIndex(((ComboItem) cCombo
                                .getSelectedItem()).id);
                    } else {
                        cCombo.setEnabled(false);
                        canvas.setCAttrIndex(ScatterPlotCanvas.ATTR_IDX_NONE);
                    }
                }
            });

            for (int i = 0, count = cCombo.getComponentCount(); i < count; i++) {
                if (((ComboItem) cCombo.getItemAt(i)).id == i) {
                    cCombo.setSelectedIndex(i);
                    break;
                }
            }

            c.insets = inputInsets;
            panel.add(cCombo, c);
            cCombo.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    canvas
                            .setCAttrIndex(((ComboItem) cCombo
                                    .getSelectedItem()).id);
                }
            });
            cCombo.setPreferredSize(comboPrefSize);

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

    private static class ComboItem implements Comparable<ComboItem> {
        final int id;
        final String label;

        public ComboItem(int id, String label) {
            this.id = id;
            this.label = label;
        }

        public int compareTo(ComboItem o) {
            return label.compareTo(label);
        }

        public boolean equals(Object obj) {
            return (obj instanceof ComboItem && ((ComboItem) obj).id == this.id);
        }

        public int hashCode() {
            return id;
        }

        public String toString() {
            return label;
        }
    }

    public Action[] getToolbarActions() {
        if (toolbarActions == null) {
            toolbarActions = new Action[] { new FitInCameraViewAction(this),
            // new PanToCenterAction(this),
                    new PanToShowOriginAction(this) };
        }
        return toolbarActions;
    }

    public void fitInCameraView(boolean animate) {
        canvas.fitInCameraView(animate);
    }

    public void centerView(boolean animate) {
        canvas.centerView(animate);
    }

    public void showOrigin(boolean animate) {
        canvas.showOrigin(animate);
    }

    public void setElementSelection(DataUID[] selection) {
        canvas.setElementSelection(selection);
    }

    public void clearSelection() {
        canvas.clearSelection();
    }
}
