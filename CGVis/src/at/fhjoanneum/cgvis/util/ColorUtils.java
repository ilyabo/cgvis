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
package at.fhjoanneum.cgvis.util;

import java.awt.Color;

/**
 * @author Ilya Boyandin
 */
public class ColorUtils {

    /**
     * @param value
     *            between 0 and 1
     */
    public static Color getGradientColor(float value, float gradientStartHue,
            float gradientEndHue) {
        assert (value >= 0 && value <= 1);
        final float v;
        if (gradientStartHue <= gradientEndHue) {
            v = gradientEndHue + (gradientEndHue - gradientStartHue) * value;
        } else {
            v = gradientStartHue - (gradientStartHue - gradientEndHue) * value;
        }
        return Color.getHSBColor(v, 1.0f, 1.0f);
    }

    /**
     * @param value
     *            between 0 and 1
     */
    public static Color getGradientColor(float value, Color gradientStart,
            Color gradientEnd) {
        assert (value >= 0 && value <= 1);
        final int r, g, b;

        if (gradientStart.getRed() <= gradientEnd.getRed()) {
            r = (int) Math.floor(gradientStart.getRed()
                    + (gradientEnd.getRed() - gradientStart.getRed()) * value);
        } else {
            r = (int) Math.floor(gradientStart.getRed()
                    - (gradientStart.getRed() - gradientEnd.getRed()) * value);
        }
        if (gradientStart.getGreen() <= gradientEnd.getGreen()) {
            g = (int) Math.floor(gradientStart.getGreen()
                    + (gradientEnd.getGreen() - gradientStart.getGreen())
                    * value);
        } else {
            g = (int) Math.floor(gradientStart.getGreen()
                    - (gradientStart.getGreen() - gradientEnd.getGreen())
                    * value);
        }
        if (gradientStart.getBlue() <= gradientEnd.getBlue()) {
            b = (int) Math
                    .floor(gradientStart.getBlue()
                            + (gradientEnd.getBlue() - gradientStart.getBlue())
                            * value);
        } else {
            b = (int) Math
                    .floor(gradientStart.getBlue()
                            - (gradientStart.getBlue() - gradientEnd.getBlue())
                            * value);
        }
        return new Color(r, g, b);
    }

    public static Color getGradientColor(float value, Color gradientStart,
            Color gradientMiddle, Color gradientEnd, int alpha) {
        assert (value >= 0 && value <= 1);
        assert (alpha >= 0 && alpha <= 255);
        final int r, g, b;
        final Color ca, cb;

        if (value <= .5f) {
            ca = gradientStart;
            cb = gradientMiddle;
            value *= 2.0f;
        } else {
            ca = gradientMiddle;
            cb = gradientEnd;
            value = (value - .5f) * 2.0f;
        }

        if (ca.getRed() <= cb.getRed()) {
            r = (int) Math.floor(ca.getRed() + (cb.getRed() - ca.getRed())
                    * value);
        } else {
            r = (int) Math.floor(ca.getRed() - (ca.getRed() - cb.getRed())
                    * value);
        }
        if (ca.getGreen() <= cb.getGreen()) {
            g = (int) Math.floor(ca.getGreen()
                    + (cb.getGreen() - ca.getGreen()) * value);
        } else {
            g = (int) Math.floor(ca.getGreen()
                    - (ca.getGreen() - cb.getGreen()) * value);
        }
        if (ca.getBlue() <= cb.getBlue()) {
            b = (int) Math.floor(ca.getBlue() + (cb.getBlue() - ca.getBlue())
                    * value);
        } else {
            b = (int) Math.floor(ca.getBlue() - (ca.getBlue() - cb.getBlue())
                    * value);
        }

        return new Color(r, g, b, alpha);
    }

    public static Color getGradientColor(float value, Color gradientStart,
            Color gradientMiddle, Color gradientEnd) {
        return getGradientColor(value, gradientStart, gradientMiddle,
                gradientEnd, 255);
    }

    /**
     * @param value
     *            between 0 and 1
     */
    public static Color getGradientColor(float value, float gradientStartHue,
            float gradientMiddleHue, float gradientEndHue) {
        assert (value >= 0 && value <= 1);
        final float v;
        if (value <= 0.5f) {
            if (gradientStartHue <= gradientMiddleHue) {
                v = gradientMiddleHue + (gradientMiddleHue - gradientStartHue) * value;
            } else {
                v = gradientStartHue - (gradientStartHue - gradientMiddleHue) * value;
            }
        } else {
            if (gradientMiddleHue <= gradientEndHue) {
                v = gradientEndHue + (gradientEndHue - gradientMiddleHue) * value;
            } else {
                v = gradientMiddleHue - (gradientMiddleHue - gradientEndHue) * value;
            }
        }
        return Color.getHSBColor(v, 1.0f, 1.0f);
    }
}
