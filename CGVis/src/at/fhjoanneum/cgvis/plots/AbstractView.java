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

import javax.swing.JInternalFrame;

import at.fhjoanneum.cgvis.IView;
import at.fhjoanneum.cgvis.IViewManager;
import at.fhjoanneum.cgvis.data.DataUID;

/**
 * @author Ilya Boyandin
 */
public abstract class AbstractView implements IView {

	private String title;
	private IViewManager viewManager;
	private JInternalFrame frame;
	
	public AbstractView(String title, IViewManager viewManager) {
		this.title = title;
		this.viewManager = viewManager;
	}
	
	public String getTitle() {
		return title;
	}

	public void fireElementSelectionChanged(DataUID[] selection) {
		viewManager.fireElementSelectionChanged(this, selection);
	}

	public IViewManager getViewManager() {
		return viewManager;
	}
	
	public void setFrame(JInternalFrame frame) {
		this.frame = frame;
	}

	public JInternalFrame getFrame() {
		return frame;
	}
	
}
