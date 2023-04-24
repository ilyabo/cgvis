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

import java.awt.Color;

import at.fhjoanneum.cgvis.util.ColorUtils;

/**
 * @author Ilya Boyandin
 */
public class GradientColorScale implements ColorScale {
   
    private double minValue, maxValue;

    public GradientColorScale() {
    }
    
    public GradientColorScale(double minV, double maxV) {
        this.minValue = minV;
        this.maxValue = maxV;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public Color getColorForValue(double value) {
        return ColorUtils.getGradientColor(
                (float) ((value - minValue) / (maxValue - minValue)),
                Color.green, Color.yellow, Color.red);
    }

}
