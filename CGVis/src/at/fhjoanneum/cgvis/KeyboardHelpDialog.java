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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 * @author Ilya Boyandin
 */
public class KeyboardHelpDialog extends JDialog {

    private static final long serialVersionUID = -3115114082785770328L;

    public KeyboardHelpDialog(Frame owner) {
        super(owner, "Keyboard shortcuts");

        final JPanel panel = new JPanel(new GridBagLayout());
        panel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        final GridBagConstraints c = new GridBagConstraints();

        GridLayout layout = new GridLayout();
        layout.setColumns(10);
        layout.setHgap(5);

        final Insets butInsets = new Insets(5, 5, 5, 5);

        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        c.insets = new Insets(10, 10, 10, 10);

        final JLabel help = new JLabel();
        help
                .setText("<html><body style=\"background-color:white\">"
                        + "<h4 style=\"text-align:center\">Keyboard and Mouse Shortcuts</h4>"
                        + "<table cellpadding=5 border=0>"
                        + "<tr>"
                        + "<td valign=top align=center colspan=2>"
                        + "<table cellpadding=5>"
                        + "<tr>"
                        + "<th colspan=2>Global</th>"
                        + "</tr>"
                        + "<tr><td>&lt;F6&gt; or &lt;Alt + T&gt;</td><td>Tile windows</td></tr>"
                        + "<tr><td>&lt;F5&gt;</td><td>Fit in view</td></tr>"
                        + "<tr><td>&lt;Ctrl + E&gt;</td><td>Select all elements</td></tr>"
                        + "<tr><td>&lt;Esc&gt; or &lt;Ctrl + D&gt;</td><td>Clear selection</td></tr>"
                        + "<tr><td>&lt;MouseWheel&gt; or &lt;Ctrl + Left Mouse Button&gt;</td><td>Zoom</td></tr>"
                        + "</table>"
                        + "<br>"
                        + "</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td valign=top>"
                        + "<table cellpadding=5>"
                        + "<tr>"
                        + "<th colspan=2>HeatMap View</th>"
                        + "</tr>"
                        + "<tr><td>&lt;Shift + MouseClick&gt;</td><td>Select/deselect attribute(s)</td></tr>"
                        + "<tr><td>&lt;Alt + MouseClick&gt;</td><td>Select/deselect element(s)</td></tr>"
                        + "<tr><td>&lt;Ctrl + A&gt;</td><td>Select all attributes</td></tr>"
                        + "</table></td>"
                        + "<td valign=top>"
                        + "<table cellpadding=5>"
                        + "<tr>"
                        + "<th colspan=2>ScatterPlot View</th>"
                        + "</tr>"
                        + "<tr><td>&lt;MouseClick&gt;</td><td>Select/deselect element</td></tr>"
                        + "<tr><td>&lt;Ctrl + MouseClick&gt;</td><td>Add element to selection</td></tr>"
                        + "</table>" + "</td>" + "</tr>" + "</table>"
                        + "</body></html>");
        panel.add(help, c);
        final JButton okButton = new JButton("OK");
        okButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        final Action closeAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        };
        okButton.addActionListener(closeAction);

        // close by escape
        final KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0,
                false);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                escape, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", closeAction);

        c.insets = butInsets;

        c.ipadx = 24;
        c.ipady = 3;
        panel.add(okButton, c);
        setContentPane(panel);
        setResizable(false);
        pack();

        final Dimension size = getSize();
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final int locX = (screen.width - size.width) / 2;
        final int locY = (screen.height - size.height) / 2;
        setLocation(locX, locY);
    }
}
