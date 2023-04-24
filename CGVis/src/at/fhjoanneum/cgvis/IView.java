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

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;

import at.fhjoanneum.cgvis.data.DataUID;

/**
 * @author Ilya Boyandin
 */
public interface IView {

    String getTitle();

    JComponent getViewComponent();

    JComponent getOptionsComponent();

    Action[] getToolbarActions();

    JComponent getToolbarControls();

    void init();

    void initInFrame();

    IViewManager getViewManager();

    void setFrame(JInternalFrame frame);

    JInternalFrame getFrame();

    void clearSelection();

    void setElementSelection(DataUID[] selection);

    void fireElementSelectionChanged(DataUID[] selection);

}
