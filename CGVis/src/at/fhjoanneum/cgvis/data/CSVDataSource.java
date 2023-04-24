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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import at.fhj.utils.misc.FileUtils;
import at.fhj.utils.misc.ProgressTracker;

/**
 * @author Ilya Boyandin
 */
public class CSVDataSource extends AbstractDataSource {

    public static final String FILE_EXTENSION = ".csv";
    private String[] filePaths;
    private String separators;
    private DataUID dataSourceId = DataUID.createUID();

    public CSVDataSource(String filePath, String separators) {
        this.filePaths = new String[] { filePath };
        this.separators = separators;
        // this.separator = Character.toString(separator);
    }

    public CSVDataSource(String[] filePaths, String separators) {
        this.filePaths = filePaths;
        this.separators = separators;
        // this.separators = Character.toString(separator);
    }

    public void init(ProgressTracker progress) throws DataSourceException {
        for (int i = 0, length = filePaths.length; i < length; i++) {
            progress.startTask(
                    "Loading " + FileUtils.getFilename(filePaths[i]),
                    1.0 / length);
            loadPointSet(filePaths[i], progress);
            progress.taskCompleted();
        }
    }

    private void loadPointSet(String filePath, ProgressTracker progress)
            throws DataSourceException {
        // load the pointset
        PointSet pointSet = null;
        BufferedReader in = null;
        try {
            final File file = new File(filePath);
            final long fileSize = file.length();

            in = new BufferedReader(new FileReader(file));
            StringTokenizer st;
            String line;
            int lineCnt = 1;

            line = in.readLine();
            lineCnt++;

            st = new StringTokenizer(line, separators);

            if (line == null || !st.hasMoreTokens()) {
                throw new IOException("No data in file");
            }
            st.nextToken(); // skip the element labels column title

            final List<String> attrLabels = new ArrayList<String>();
            while (st.hasMoreTokens()) {
                attrLabels.add(st.nextToken());
            }

            final int dimension = attrLabels.size();
            final List<String> elemLabels = new ArrayList<String>();
            final List<double[]> values = new ArrayList<double[]>();

            while ((line = in.readLine()) != null) {
                st = new StringTokenizer(line, separators);
                if (!st.hasMoreTokens()) {
                    throw new IOException("No data in line " + lineCnt);
                }
                elemLabels.add(st.nextToken());
                final double[] element = new double[dimension];
                int attrCnt = 0;

                while (st.hasMoreTokens()) {
                    final String token = st.nextToken();
                    if (attrCnt < dimension) {
                        try {
                            element[attrCnt] = Double.parseDouble(token);
                        } catch (NumberFormatException nfe) {
                            throw new IOException(
                                    "Couldn't parse value '" + token + "' in line " + lineCnt + ", column " + attrCnt);
                        }
                    }
                    attrCnt++;
                }
                if (attrCnt != dimension) {
                    throw new IOException("Wrong number of values in line " + lineCnt +
                            " (expected: " + dimension + ", actual: " + attrCnt + ")"
                    );
                }
                values.add(element);
                lineCnt++;

                progress.incTaskProgress((double) fileSize / line.length());
            }

            final double[][] _values = new double[values.size()][dimension];
            final String[] _elemLabels = new String[elemLabels.size()];
            final String[] _attrLabels = new String[attrLabels.size()];
            pointSet = new PointSet(FileUtils.getFilenameOnly(filePath), values
                    .toArray(_values), DataUID
                    .createArrayOfUIDs(_elemLabels.length), // TODO
                    elemLabels.toArray(_elemLabels), DataUID
                            .createArrayOfUIDs(_attrLabels.length), // TODO
                    attrLabels.toArray(_attrLabels));
            addPointSet(pointSet);

        } catch (IOException ioe) {
            throw new DataSourceException(ioe);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe) {
                    // nothing we can do
                }
            }
        }
    }

    public Object executeQuery(Query query) {
        // add custom processing here

        return super.executeQuery(query);
    }

    public DataUID getDataSourceId() {
        return dataSourceId;
    }

}
