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

import at.fhjoanneum.cgvis.IPannableView;

/**
 * @author Ilya Boyandin
 */
public class PanToShowOriginAction extends AbstractAction {

    private static final long serialVersionUID = 4083716078964872096L;
    private IPannableView view;

    public PanToShowOriginAction(IPannableView view) {
        this.view = view;
        putValue(Action.NAME, "Show Origin");
        putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource(
                "res/Origin.gif")));
        putValue(Action.SHORT_DESCRIPTION, "Show origin");
        putValue(Action.LONG_DESCRIPTION, "Show origin");
        putValue(Action.ACTION_COMMAND_KEY, "show-origin");
    }

    public void actionPerformed(ActionEvent e) {
        view.showOrigin(true);
    }

}
