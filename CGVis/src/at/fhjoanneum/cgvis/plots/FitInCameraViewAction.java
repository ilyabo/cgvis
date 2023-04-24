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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import at.fhjoanneum.cgvis.IZoomableView;

public class FitInCameraViewAction extends AbstractAction {

    private static final long serialVersionUID = 2793214836736177079L;
    private IZoomableView view;

    public FitInCameraViewAction(IZoomableView view) {
        this.view = view;
        putValue(Action.NAME, "Fit in View");
        putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource(
                "res/FitInView.gif")));
        putValue(Action.SHORT_DESCRIPTION, "Fit in view");
        putValue(Action.LONG_DESCRIPTION, "Fit in camera view");
        putValue(Action.ACTION_COMMAND_KEY, "fit-in-camera");
    }

    public void actionPerformed(ActionEvent e) {
        view.fitInCameraView(true);
    }
}