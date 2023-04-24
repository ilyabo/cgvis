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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import at.fhj.utils.misc.ProgressTracker;
import at.fhjoanneum.cgvis.data.provider.PointSetProvider;

/**
 * @author Erik Koerner
 */
public class CompoundDataSource extends AbstractDataSource {

    public final static String FILE_EXTENSION = ".cgv";

    private List<PointSetProvider> pointSetProviders;
    private String configPath;
    private String xmlFileName;
    private ProgressTracker progressTracker;
    private int progressChunks;
    private DataUID myUID;

    private Logger logger;

    /**
     * Constructor of the CompoundDataSource class.
     * 
     * @param confFileName
     *            Configuration File Name
     */
    public CompoundDataSource(String confFileName) {
        logger = Logger.getLogger(getClass().getName());
        myUID = DataUID.createUID();
        try {
            pointSetProviders = new ArrayList<PointSetProvider>();
            xmlFileName = confFileName;
            logger.debug("Metadata File = '" + xmlFileName + "'");
            File fx = new File(xmlFileName);
            configPath = fx.getParentFile().getAbsolutePath();
            logger.debug("Metadata Path = '" + configPath + "'");
        } catch (Exception ex) {
            logger.error("Error constructing CompoundDataSource: " + ex);
            configPath = "";
        }
    }

    /**
     * Ilya wanted me to implement the Data Processing in the init() method ;-)
     */
    public void init(ProgressTracker progress) throws DataSourceException {
        logger.info("Starting to parse " + xmlFileName + "...");
        progressTracker = progress;
        analyzeProgress(xmlFileName);
        parseFile(xmlFileName);
        for (int i = 0; i < pointSetProviders.size(); i++) {
            PointSetProvider psp = pointSetProviders.get(i);
            if (psp.hasPointSet() == false)
                continue;
            this.addPointSet(psp.getPointSet());
        }
        logger.info(xmlFileName + " parsed.");
    }

    private void analyzeProgress(String xmlFileName) throws DataSourceException {
        XmlPullParser rdr;
        int eventType;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance(
                    System.getProperty(XmlPullParserFactory.PROPERTY_NAME),
                    null);
            factory.setNamespaceAware(true);
            rdr = factory.newPullParser();
            rdr.setInput(new FileReader(xmlFileName));
            eventType = rdr.getEventType();
            progressChunks = 0;
            do {
                switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (rdr.getName().compareToIgnoreCase("csvdata") == 0) {
                        progressChunks++;
                        break;
                    }
                    if (rdr.getName().compareToIgnoreCase("dbdata") == 0) {
                        progressChunks++;
                        break;
                    }
                    break;
                }
                eventType = rdr.next();
            } while (eventType != XmlPullParser.END_DOCUMENT);
            logger.debug("Number of Subtasks ... "
                    + Integer.toString(progressChunks));
        } catch (XmlPullParserException ex) {
            throw new DataSourceException(new DataSourceException()
                    .initCause(ex));
        } catch (FileNotFoundException ex) {
            throw new DataSourceException(new DataSourceException()
                    .initCause(ex));
        } catch (IOException ex) {
            throw new DataSourceException(new DataSourceException()
                    .initCause(ex));
        }
    }

    /**
     * Method to parse the configuration file.
     * 
     * @param xmlFileName
     *            Configuration file path (absolute or relative to App).
     * @throws DataSourceException
     */
    private void parseFile(String xmlFileName) throws DataSourceException {
        XmlPullParser rdr;
        int eventType;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance(
                    System.getProperty(XmlPullParserFactory.PROPERTY_NAME),
                    null);
            factory.setNamespaceAware(true);
            rdr = factory.newPullParser();
            rdr.setInput(new FileReader(xmlFileName));
            eventType = rdr.getEventType();
            do {
                switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.END_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if (rdr.getName().compareToIgnoreCase("cgvisdata") == 0) {
                        parseCGVisData(rdr);
                        break;
                    }
                    throw new DataSourceException(
                            "No cgvisdata document element in metadata file.");
                }
                eventType = rdr.next();
            } while (eventType != XmlPullParser.END_DOCUMENT);
            logger.info("Metadata file parsing completed.");
        } catch (XmlPullParserException ex) {
            throw new DataSourceException(new DataSourceException()
                    .initCause(ex));
        } catch (FileNotFoundException ex) {
            throw new DataSourceException(new DataSourceException()
                    .initCause(ex));
        } catch (IOException ex) {
            throw new DataSourceException(new DataSourceException()
                    .initCause(ex));
        }
    }

    /**
     * Parses a full CGVisData XML Element
     * 
     * @param rdr
     *            XmlPullParser from which to read
     * @throws XmlPullParserException
     * @throws DataSourceException
     * @throws IOException
     */
    private void parseCGVisData(XmlPullParser rdr)
            throws XmlPullParserException, DataSourceException, IOException {
        int eventType;

        eventType = rdr.getEventType();
        do {
            switch (eventType) {
            case XmlPullParser.START_DOCUMENT:
                break;
            case XmlPullParser.END_DOCUMENT:
                break;
            case XmlPullParser.START_TAG:
                if (rdr.getName().compareToIgnoreCase("set") == 0) {
                    logger.info("Providing Point Set:");
                    PointSetProvider psp = new PointSetProvider(configPath,
                            progressTracker, progressChunks, myUID);
                    pointSetProviders.add(psp);
                    psp.parseConfiguration(rdr);
                    logger.info("Point Set Parsed.");
                    break;
                }
                break;
            case XmlPullParser.END_TAG:
                if (rdr.getName().compareToIgnoreCase("cgvisdata") != 0)
                    throw new DataSourceException(
                            "Configuration Parsing: Unexpected end tag '"
                                    + rdr.getName() + "'");
                return;
            }
            eventType = rdr.next();
        } while (eventType != XmlPullParser.END_DOCUMENT);
        throw new DataSourceException(
                "Configuration Parsing: Unexpected end of file");
    }

    public DataUID getDataSourceId() {
        return myUID;
    }
}
