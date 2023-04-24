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
package at.fhjoanneum.cgvis.data;

import at.fhj.utils.misc.ProgressTracker;

/**
 * @author Ilya Boyandin
 */
public class RandomDataSource extends AbstractDataSource {

    private DataUID id = DataUID.createUID();

    public void init(ProgressTracker progress) throws DataSourceException {
        final int N = 500, D = 25;
        final double[][] data = new double[N][D];
        final String[] eLabels = new String[N];
        final String[] cLabels = new String[D];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < D; j++) {
                data[i][j] = Math.random() * 100000;
            }
            eLabels[i] = "Element " + i;
        }
        for (int j = 0; j < D; j++) {
            cLabels[j] = "Attr " + j;
        }
        addPointSet(new PointSet("Random", data, DataUID.createArrayOfUIDs(N),
                eLabels, DataUID.createArrayOfUIDs(D), cLabels));
    }

    public DataUID getDataSourceId() {
        return id;
    }

}
