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

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * @author Ilya Boyandin
 */
public class Constants {

    public final static NumberFormat TOOLTIP_NUMBER_FORMAT = new DecimalFormat(
            "0.0##########");
    public static final Color ELEMENT_SELECTION_COLOR = new Color(255, 0, 225,
            170);

}
