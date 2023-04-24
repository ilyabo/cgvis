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
package at.fhjoanneum.cgvis.data.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import at.fhj.utils.misc.ProgressTracker;
import at.fhjoanneum.cgvis.data.DataSourceException;
import at.fhjoanneum.cgvis.data.DataUID;
import at.fhjoanneum.cgvis.data.ExPointSet;
import at.fhjoanneum.cgvis.data.IPointSet;
import at.fhjoanneum.cgvis.data.Normalization;

/**
 * @author Erik Koerner
 */
public class PointSetProvider implements IPointSetter {

    private String configPath;
    private String keyColumnHeader;
    private List<AbstractDataProvider> dataProviders;
    private KeyType keyType;

    private List<String> columnLabels;
    private List<String> rowLabels;
    private List<NormalizationParams> normalization;
    private String name;
    private double[][] values;
    private ExPointSet myPointSet;
    private Logger logger;
    private ProgressTracker progressTracker;
    private int progressChunks;

    private DataUID dataSourceUID;
    private DataUID pointSetUID;

    /**
     * Constructor of the PointSetProvider() class.
     */
    public PointSetProvider(String cfgPath, ProgressTracker progress,
            int chunks, DataUID dataSourceUID) {
        configPath = cfgPath;
        progressTracker = progress;
        progressChunks = chunks;
        keyType = KeyType.DATA;
        keyColumnHeader = "";
        dataProviders = new ArrayList<AbstractDataProvider>();
        columnLabels = new ArrayList<String>();
        rowLabels = new ArrayList<String>();
        name = "Untitled";
        values = new double[0][0];
        myPointSet = null;
        logger = Logger.getLogger(getClass().getName());
        normalization = new ArrayList<NormalizationParams>();
        this.dataSourceUID = dataSourceUID;
        pointSetUID = DataUID.createUID();
    }

    /**
     * @return Returns a boolean value indicating whether the PointSet exists.
     */
    public boolean hasPointSet() {
        return (myPointSet != null);
    }

    /**
     * @return Returns the provider's PointSet.
     */
    public IPointSet getPointSet() {
        return myPointSet;
    }

    /**
     * Method parses the XML subtree that refers to a CSV Data file.
     * 
     * @param rdr
     *            XMLPullParser where the data is retrieved from
     * @throws IOException
     * @throws XmlPullParserException
     */
    public void parseConfiguration(XmlPullParser rdr)
            throws DataSourceException {
        int eventType;
        String s;

        try {
            for (int i = 0; i < rdr.getAttributeCount(); i++) {
                if (rdr.getAttributeName(i).compareToIgnoreCase("key") == 0) {
                    s = rdr.getAttributeValue(i);
                    if (s.compareToIgnoreCase("data") == 0)
                        keyType = KeyType.DATA;
                    if (s.compareToIgnoreCase("auto") == 0)
                        keyType = KeyType.AUTO;
                    logger.debug("Key type = " + keyType.toString());
                }
                if (rdr.getAttributeName(i).compareToIgnoreCase("keycolumn") == 0) {
                    keyColumnHeader = rdr.getAttributeValue(i);
                    logger.debug("  Row Label Column Header = '"
                            + keyColumnHeader + "'");
                }
                if (rdr.getAttributeName(i).compareToIgnoreCase("name") == 0) {
                    name = rdr.getAttributeValue(i);
                    logger.debug("  Name = '" + name + "'");
                }
            }
            eventType = rdr.next();
            eventType = rdr.getEventType();

            DataProviderParameters dpp = new DataProviderParameters();
            dpp.pointSetter = this;
            dpp.configurationPath = configPath;
            dpp.keyColumnHeader = keyColumnHeader;
            dpp.keyType = keyType;
            dpp.progressTracker = this.progressTracker;
            dpp.progressChunks = progressChunks;

            do {
                switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (rdr.getName().compareToIgnoreCase("csvdata") == 0) {
                        CSVDataProvider cdp = new CSVDataProvider(dpp);
                        dataProviders.add(cdp);
                        cdp.parseConfiguration(rdr);
                    }
                    if (rdr.getName().compareToIgnoreCase("dbdata") == 0) {
                        DbDataProvider ddp = new DbDataProvider(dpp);
                        dataProviders.add(ddp);
                        ddp.parseConfiguration(rdr);
                    }
                    // <normalize column="COL1" mode="RANGE"/>
                    if (rdr.getName().compareToIgnoreCase("normalize") == 0) {
                        NormalizationParams np = new NormalizationParams();
                        for (int i = 0; i < rdr.getAttributeCount(); i++) {
                            if (rdr.getAttributeName(i).compareToIgnoreCase(
                                    "column") == 0) {
                                np.column = rdr.getAttributeValue(i);
                            }
                            if (rdr.getAttributeName(i).compareToIgnoreCase(
                                    "mode") == 0) {
                                if (rdr.getAttributeValue(i)
                                        .compareToIgnoreCase("raw") == 0) {
                                    np.normalization = Normalization.RAW;
                                }
                                if (rdr.getAttributeValue(i)
                                        .compareToIgnoreCase("range") == 0) {
                                    np.normalization = Normalization.RANGE;
                                    np.min = 0.0;
                                    np.max = 1.0;
                                }
                                if (rdr.getAttributeValue(i)
                                        .compareToIgnoreCase("vector") == 0) {
                                    np.normalization = Normalization.VECTOR;
                                    np.max = 1.0;
                                }
                            }
                        }
                        for (int i = 0; i < rdr.getAttributeCount(); i++) {
                            if (rdr.getAttributeName(i).compareToIgnoreCase(
                                    "min") == 0) {
                                np.min = Double.parseDouble(rdr
                                        .getAttributeValue(i));
                            }
                            if (rdr.getAttributeName(i).compareToIgnoreCase(
                                    "max") == 0) {
                                np.max = Double.parseDouble(rdr
                                        .getAttributeValue(i));
                            }
                        }
                        normalization.add(np);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (rdr.getName().compareToIgnoreCase("set") == 0) {
                        determinePointSet();
                        return;
                    }
                    break;
                }
                eventType = rdr.next();
            } while (eventType != XmlPullParser.END_DOCUMENT);
            logger.error("Configuration Parser: Unexpected end of file");
            throw new DataSourceException(
                    "Configuration Parser: Unexpected end of file");
        } catch (XmlPullParserException xppe) {
            throw new DataSourceException("Metadata Parsing Error", xppe);
        } catch (IOException ioe) {
            throw new DataSourceException("Metadata IO Error", ioe);
        } catch (NumberFormatException nfe) {
            throw new DataSourceException("Bad Number Format", nfe);
        }
    }

    /**
     * Method retrieves the points of all dada sources and consolidates them to
     * the PointSet.
     */
    public void determinePointSet() throws DataSourceException {
        AbstractDataProvider adp;
        String[] arr;
        int i, j;

        columnLabels.clear();
        rowLabels.clear();
        try {
            logger.info("Determining Column Labels...");
            for (i = 0; i < dataProviders.size(); i++) {
                adp = dataProviders.get(i);
                arr = adp.getColumnHeaders();
                for (j = 0; j < arr.length; j++) {
                    if (columnLabels.contains(arr[j]) == true)
                        continue;
                    if (keyType == KeyType.DATA
                            && arr[j].compareTo(keyColumnHeader) == 0)
                        continue;
                    columnLabels.add(arr[j]);
                }
                arr = adp.getRowLabels();
                for (j = 0; j < arr.length; j++) {
                    if (rowLabels.contains(arr[j]) == true)
                        continue;
                    rowLabels.add(arr[j]);
                }
            }
            values = new double[rowLabels.size()][columnLabels.size()];
            logger.info("Setting PointSet content to NaN...");
            for (i = 0; i < rowLabels.size(); i++) {
                for (j = 0; j < columnLabels.size(); j++) {
                    values[i][j] = Double.NaN;
                }
            }

            logger.info("Retrieving PointSet content...");
            for (i = 0; i < dataProviders.size(); i++) {
                adp = dataProviders.get(i);
                adp.retrievePoints();
            }

            // Ilya Stuff: attrLabels are column labels...

            myPointSet = new ExPointSet(name, values, DataUID
                    .createArrayOfUIDs(rowLabels.size()), rowLabels
                    .toArray(new String[0]), DataUID
                    .createArrayOfUIDs(columnLabels.size()), columnLabels
                    .toArray(new String[0]), dataSourceUID, pointSetUID);

            // Do the Normalization:
            for (i = 0; i < normalization.size(); i++) {
                NormalizationParams np = normalization.get(i);
                switch (np.normalization) {
                case RAW:
                    myPointSet.normalize(np.column, np.normalization);
                    break;
                case RANGE:
                    myPointSet.normalize(np.column, np.normalization, np.min,
                            np.max);
                    break;
                case VECTOR:
                    myPointSet.normalize(np.column, np.normalization, np.max);
                    break;
                }
            }
        } catch (Exception ex) {
            throw new DataSourceException(ex);
        }
    }

    /**
     * This is the callback a DataProvider calls to set an individual point in
     * our value array.
     */
    public void setPoint(String column, String row, double value)
            throws DataSourceException {
        int y = rowLabels.indexOf(row);
        int x = columnLabels.indexOf(column);
        if (x < 0 || y < 0)
            throw new DataSourceException(
                    "Point Setter: Row or Column not found.");
        if (Double.isNaN(values[y][x]) == false && values[y][x] != value)
            logger.warn("Duplicate value in Element '" + row
                    + "' at Attribute '" + column + "'.");
        values[y][x] = value;
    }

    public String getName() {
        return name;
    }

}
