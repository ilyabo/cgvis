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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import at.fhj.utils.swing.JMsgPane;
import at.fhjoanneum.cgvis.IViewManager;
import at.fhjoanneum.cgvis.data.AttrSelection;
import at.fhjoanneum.cgvis.data.CompoundAttrsPointSet;
import at.fhjoanneum.cgvis.data.IPointSet;
import at.fhjoanneum.cgvis.data.PointAttrSubSet;
import at.fhjoanneum.cgvis.plots.mosaic.MosaicView;
import at.fhjoanneum.cgvis.plots.scatter.ScatterPlotView;

/**
 * @author Ilya Boyandin
 */
public class ShowScatterPlotViewAction extends AbstractAction {

    private static final long serialVersionUID = -4747012099741085617L;
    private final MosaicView view;

    public ShowScatterPlotViewAction(MosaicView view) {
        this.view = view;

        putValue(Action.NAME, "Show Scatter Plot...");
        putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource(
                "res/ShowScatterPlot.gif")));
        putValue(Action.SHORT_DESCRIPTION, "Show Scatter Plot");
        putValue(Action.LONG_DESCRIPTION, "Show scatter plot view");
        putValue(Action.ACTION_COMMAND_KEY, "show-zscatter");
    }

    public void actionPerformed(ActionEvent e) {
        try {
            final AttrSelection[] selection = view.getSelection();

            int selectionSize = 0;
            final PointAttrSubSet[] subsets = new PointAttrSubSet[selection.length];
            for (int i = 0, n = selection.length; i < n; i++) {
                final AttrSelection sel = selection[i];
                subsets[i] = new PointAttrSubSet(sel);
                selectionSize += sel.getSize();
            }

            if (selectionSize < 2) {
                JMsgPane
                        .showInfoDialog(view.getViewComponent(),
                                "You must select at least two attributes to open the scatter plot view.");

            } else {
                final IPointSet ps = /* new PointSet( */new CompoundAttrsPointSet(
                        subsets)/* ) */;
                final IViewManager viewManager = view.getViewManager();
                viewManager.showView(new ScatterPlotView(ps.getName(), ps, view
                        .getViewPreferences(), viewManager));
            }

        } catch (Throwable th) {
            JMsgPane.showErrorDialog(view.getViewComponent(),
                    "Couldn't open scatter plot view: " + th.getMessage());
            Logger.getLogger(getClass().getName()).error("Exception: ", th);
        }
    }

}
