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

/**
 * @author Ilya Boyandin
 */
public class ViewPreferences {

    private long animationsDuration = 1000;
    private long shortAnimationsDuration = 500;
    private boolean useAnimation = true;

    public long getShortAnimationsDuration() {
        return shortAnimationsDuration;
    }

    public void setShortAnimationsDuration(long duration) {
        this.shortAnimationsDuration = duration;
    }

    public long getAnimationsDuration() {
        return animationsDuration;
    }

    public void setAnimationsDuration(long duration) {
        this.animationsDuration = duration;
    }

    public boolean useAnimation() {
        return useAnimation;
    }

    public void setUseAnimation(boolean useAnimation) {
        this.useAnimation = useAnimation;
    }

}
