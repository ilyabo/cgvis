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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlpull.v1.XmlPullParser;

import at.fhj.utils.misc.ProgressTracker;
import at.fhjoanneum.cgvis.data.DataSourceException;

public class CSVDataProvider extends AbstractDataProvider {

    private enum ElementState {
        UNDEFINED, NAME, STARTKEY, PATH, DECIMALSIGN, SEPARATOR;
    }

    private String name;
    private String configPath;
    private String path;
    private List<String> nanSymbols;
    private String separators;
    private String decimalSign;

    private int lineCount;
    private KeyType keyType;
    private int startKey;
    private String keyColumnHeader;
    private int keyColumnHeaderIndex;
    private String[] sourceColumnLabels;
    private String[] targetColumnLabels;
    private ArrayList<String> keyLabels;

    private ElementState myState;
    private Logger logger;
    private ProgressTracker progressTracker;
    private int progressChunks;

    /**
     * Constructor of the CSVDataProvider class.
     * 
     * @param ps
     *            Class implementing the IPointSetter interface. Needed for
     *            setPoint() callback.
     * @param cfgPath
     *            Expects the absolute path to the XML config file, to get data
     *            files relative to this one.
     * @param keyColHdr
     *            Header of the Key column (row headers are located there)
     */
    public CSVDataProvider(DataProviderParameters dpp) {
        super(dpp.pointSetter);
        name = "Untitled";
        configPath = dpp.configurationPath;
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        myState = ElementState.UNDEFINED;
        path = "";
        nanSymbols = new ArrayList<String>();
        separators = "\t";
        lineCount = 0;
        decimalSign = Character.toString(dfs.getDecimalSeparator());
        keyLabels = new ArrayList<String>();
        keyType = dpp.keyType;
        startKey = 0;
        keyColumnHeader = dpp.keyColumnHeader;
        keyColumnHeaderIndex = -1;
        progressTracker = dpp.progressTracker;
        progressChunks = dpp.progressChunks;

        logger = Logger.getLogger(getClass().getName());
    }

    /**
     * Method parses the XML subtree that refers to a CSV Data file.
     * 
     * @param rdr
     *            XMLPullParser where the data is retrieved from
     */
    public void parseConfiguration(XmlPullParser rdr)
            throws DataSourceException {
        int eventType;
        String s;

        try {
            eventType = rdr.next();
            eventType = rdr.getEventType();
            do {
                switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (rdr.getName().compareToIgnoreCase("name") == 0) {
                        myState = ElementState.NAME;
                        break;
                    }
                    if (rdr.getName().compareToIgnoreCase("path") == 0) {
                        myState = ElementState.PATH;
                        break;
                    }
                    if (rdr.getName().compareToIgnoreCase("separator") == 0) {
                        myState = ElementState.SEPARATOR;
                        break;
                    }
                    if (rdr.getName().compareToIgnoreCase("decimalseparator") == 0) {
                        myState = ElementState.DECIMALSIGN;
                        break;
                    }
                    if (rdr.getName().compareToIgnoreCase("startkey") == 0) {
                        myState = ElementState.STARTKEY;
                        break;
                    }
                    if (rdr.getName().compareToIgnoreCase("nan") == 0) {
                        for (int i = 0; i < rdr.getAttributeCount(); i++) {
                            if (rdr.getAttributeName(i).compareToIgnoreCase(
                                    "symbol") != 0)
                                continue;
                            nanSymbols.add(rdr.getAttributeValue(i).trim());
                        }
                        break;
                    }
                    if (rdr.getName().compareToIgnoreCase("alias") == 0) {
                        String src = "", tgt = "";
                        for (int i = 0; i < rdr.getAttributeCount(); i++) {
                            if (rdr.getAttributeName(i).compareToIgnoreCase(
                                    "sourcecol") == 0)
                                src = rdr.getAttributeValue(i).trim();
                            if (rdr.getAttributeName(i).compareToIgnoreCase(
                                    "targetcol") == 0)
                                tgt = rdr.getAttributeValue(i).trim();
                        }
                        if (src.length() == 0 || tgt.length() == 0)
                            break;
                        if (aliases.containsKey(src) == true)
                            break;
                        aliases.put(src, tgt);
                        System.out.println("Alias von " + src + " = "
                                + aliases.get(src));
                        break;
                    }
                    break;
                case XmlPullParser.TEXT:
                    switch (myState) {
                    case NAME:
                        name = rdr.getText().trim();
                        myState = ElementState.UNDEFINED;
                        System.out.println("Name = " + name);
                        break;
                    case STARTKEY:
                        s = rdr.getText().trim();
                        startKey = Integer.parseInt(s);
                        myState = ElementState.UNDEFINED;
                        System.out.println("Startkey = "
                                + Integer.toString(startKey));
                        break;
                    case PATH:
                        path = rdr.getText().trim();
                        path = path.replace('\\', File.separatorChar);
                        path = path.replace('/', File.separatorChar);
                        myState = ElementState.UNDEFINED;
                        break;
                    case SEPARATOR:
                        separators = rdr.getText();
                        myState = ElementState.UNDEFINED;
                        break;
                    case DECIMALSIGN:
                        decimalSign = rdr.getText().trim();
                        myState = ElementState.UNDEFINED;
                        break;
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (rdr.getName().compareToIgnoreCase("csvdata") == 0) {
                        analyze();
                        return;
                    }
                    break;
                }
                eventType = rdr.next();
            } while (eventType != XmlPullParser.END_DOCUMENT);
            throw new DataSourceException(
                    "Configuration Parser: Unexpected end of file");
        } catch (Exception ex) {
            throw new DataSourceException(ex);
        }
    }

    /**
     * Method analyzes the file and determines the columns and the data row
     * count. Columns include the key column, rows do not include the header
     * row.
     */
    private void analyze() throws DataSourceException {
        if (path == "") {
            logger.error("File Name of Data Source '" + name
                    + "' not specified.");
            throw new DataSourceException("File Name of Data Source '" + name
                    + "' not specified.");
        }

        if (progressTracker != null) {
            progressTracker.startTask("Analyzing CSV Data Source '" + name
                    + "'", 1.0 / progressChunks / 2);
        }

        try {
            logger.info("Analyzing Text Data Source '" + configPath
                    + File.separator + path + "':");
            final BufferedReader in = new BufferedReader(new FileReader(
                    configPath + File.separator + path));
            try {
                String headerLine = in.readLine();
                if (headerLine == null) {
                    logger.error("  No Data in file.");
                    throw new DataSourceException(
                            "Data Source Analysis: No data in file.");
                }

                sourceColumnLabels = headerLine.split(separators);
                targetColumnLabels = new String[sourceColumnLabels.length];

                for (int i = 0; i < sourceColumnLabels.length; i++) {
                    if (aliases.containsKey(sourceColumnLabels[i]) == false)
                        targetColumnLabels[i] = sourceColumnLabels[i];
                    else
                        targetColumnLabels[i] = aliases
                                .get(sourceColumnLabels[i]);
                    if (targetColumnLabels[i].compareTo(keyColumnHeader) == 0)
                        keyColumnHeaderIndex = i;
                }
                if (keyType == KeyType.DATA && keyColumnHeaderIndex == -1) {
                    logger.error("  No key column found.");
                    throw new DataSourceException(
                            "Data Source Analysis: No key column found.");
                }
                logger.info("  " + Integer.toString(targetColumnLabels.length)
                        + " Columns found.");

                String s;
                String[] tok;
                int curKey = startKey;
                while ((s = in.readLine()) != null) {
                    switch (keyType) {
                    case DATA:
                        tok = s.split(separators);
                        if (tok.length <= keyColumnHeaderIndex)
                            throw new DataSourceException(
                                    "Data Source Analysis: Key Element missing in line "
                                            + Integer.toString(lineCount));
                        keyLabels.add(tok[keyColumnHeaderIndex]);
                        break;
                    case AUTO:
                        String x = "00000000" + Integer.toString(curKey);
                        x = x.substring(x.length() - 8, x.length());
                        keyLabels.add(x);
                        curKey++;
                        break;
                    }
                    lineCount++;
                }
                logger.info("  " + Integer.toString(keyLabels.size())
                        + " Rows found.");
                logger.info("Text Data Source analyzed.");
            } finally {
                in.close();
                if (progressTracker != null) {
                    progressTracker.taskCompleted();
                }
            }
        } catch (Exception ex) {
            throw new DataSourceException(ex);
        }
    }

    // ==============================================================================
    // Getters for Rows, Columns and Values:
    // ==============================================================================

    /**
     * Returns an array of Column Headers. Call analyze() method first.
     */
    public String[] getColumnHeaders() {
        return targetColumnLabels;
    }

    /**
     * Returns an array of Row Labels. Call analyze() method first.
     */
    public String[] getRowLabels() {
        return keyLabels.toArray(new String[0]);
    }

    /**
     * Method retrieves Data from Data source and writes it via callback to the
     * PointSet.
     */
    public void retrievePoints() throws DataSourceException {
        String line, s;
        String[] tok;
        int curRow, i;
        double d;

        if (path == "")
            return;

        if (progressTracker != null) {
            progressTracker.startTask("Reading CSV Data Source '" + name + "'",
                    1.0 / progressChunks / 2.0);
        }

        try {
            logger.info("Retrieving Points from Text Data Source '" + name
                    + "' in PointSet '" + pointSetter.getName() + "':");
            final BufferedReader rdr = new BufferedReader(new FileReader(
                    configPath + File.separator + path));
            try {
                line = rdr.readLine(); // Header
                curRow = 0;
                while ((line = rdr.readLine()) != null) {
                    tok = line.split(separators);
                    for (i = 0; i < tok.length; i++) {
                        if (keyType == KeyType.DATA
                                && i == keyColumnHeaderIndex)
                            continue;
                        if (nanSymbols.contains(tok[i]) == true) {
                            pointSetter.setPoint(targetColumnLabels[i],
                                    keyLabels.get(curRow), Double.NaN);
                        } else {
                            s = tok[i].replace(".", decimalSign).replace(",",
                                    decimalSign);
                            try {
                                d = Double.parseDouble(s);
                            } catch (Exception ex) {
                                d = Double.NaN;
                                logger.warn("  Not a Number at Row '"
                                        + keyLabels.get(curRow) + "', Column '"
                                        + targetColumnLabels[i] + "'");
                            }
                            pointSetter.setPoint(targetColumnLabels[i],
                                    keyLabels.get(curRow), d);
                        }
                    }
                    curRow++;
                }
                logger.info("All Text Data Source Points retrieved.");
            } finally {
                rdr.close();
                if (progressTracker != null) {
                    progressTracker.taskCompleted();
                }
            }
        } catch (Exception ex) {
            throw new DataSourceException("Data Point Retrieval failed", ex);
        }
    }
}
