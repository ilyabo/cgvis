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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlpull.v1.XmlPullParser;

import at.fhjoanneum.cgvis.data.DataSourceException;

/**
 * @author Erik Koerner
 */
public class DbDataProvider extends AbstractDataProvider {

    /**
     * Enumeration for Database sources currently implemented.
     */
    public enum DatabaseType {
        UNDEFINED, ODBC, ORACLE, MYSQL, POSTGRESQL, SQLSERVER, RAW;
    };

    /**
     * State for XML parser to indicate the element currently beinig read.
     */
    private enum ElementState {
        UNDEFINED, NAME, STARTKEY, URL, CLASS, SERVER, PORT, DATABASE, USERNAME, PASSWORD, SELECTION;
    }

    private String name;
    private DatabaseType myDatabase;
    private String className;
    private String url;
    private String server;
    private int port;
    private String database;

    private String userName;
    private String password;
    private String selection;

    private List<String> nanSymbols;
    private KeyType keyType;
    private int startKey;
    private String keyColumnHeader;
    private int keyColumnHeaderIndex;
    private String[] sourceColumnLabels;
    private String[] targetColumnLabels;
    private ArrayList<String> keyLabels;

    private ElementState myState;
    private Logger logger;

    /**
     * Constructor of the DbDataProvider class.
     * 
     * @param ps
     *            Class implementing the IPointSetter interface. Needed for
     *            setPoint() callback.
     * @param keyColHdr
     *            Header of the Key column (row headers are located there)
     */
    public DbDataProvider(DataProviderParameters dpp) {
        super(dpp.pointSetter);

        name = "Untitled";
        myState = ElementState.UNDEFINED;
        nanSymbols = new ArrayList<String>();

        myDatabase = DatabaseType.UNDEFINED;

        className = "";
        url = "";
        server = "localhost";
        port = -1;
        database = "";

        userName = "";
        password = "";
        selection = "";

        keyLabels = new ArrayList<String>();
        keyType = dpp.keyType;
        startKey = 0;
        keyColumnHeader = dpp.keyColumnHeader;
        keyColumnHeaderIndex = -1;

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
            for (int i = 0; i < rdr.getAttributeCount(); i++) {
                if (rdr.getAttributeName(i).compareToIgnoreCase("type") == 0) {
                    s = rdr.getAttributeValue(i);
                    if (s.compareToIgnoreCase("oracle") == 0)
                        myDatabase = DatabaseType.ORACLE;
                    if (s.compareToIgnoreCase("odbc") == 0)
                        myDatabase = DatabaseType.ODBC;
                    if (s.compareToIgnoreCase("mysql") == 0)
                        myDatabase = DatabaseType.MYSQL;
                    if (s.compareToIgnoreCase("postgresql") == 0)
                        myDatabase = DatabaseType.POSTGRESQL;
                    if (s.compareToIgnoreCase("sqlserver") == 0)
                        myDatabase = DatabaseType.SQLSERVER;
                    if (s.compareToIgnoreCase("raw") == 0)
                        myDatabase = DatabaseType.RAW;
                    System.out.println("DatabaseType = "
                            + myDatabase.toString());
                }
            }
            eventType = rdr.next();
            eventType = rdr.getEventType();
            do {
                switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (rdr.getName().compareToIgnoreCase("name") == 0) {
                        myState = ElementState.NAME;
                        break;
                    }
                    if (rdr.getName().compareToIgnoreCase("startkey") == 0) {
                        myState = ElementState.STARTKEY;
                        break;
                    }
                    if (rdr.getName().compareToIgnoreCase("url") == 0) {
                        myState = ElementState.URL;
                        break;
                    }
                    if (rdr.getName().compareToIgnoreCase("class") == 0) {
                        myState = ElementState.CLASS;
                        break;
                    }
                    if (rdr.getName().compareToIgnoreCase("server") == 0) {
                        myState = ElementState.SERVER;
                        break;
                    }
                    if (rdr.getName().compareToIgnoreCase("port") == 0) {
                        myState = ElementState.PORT;
                        break;
                    }
                    if (rdr.getName().compareToIgnoreCase("database") == 0) {
                        myState = ElementState.DATABASE;
                        break;
                    }
                    if (rdr.getName().compareToIgnoreCase("username") == 0) {
                        myState = ElementState.USERNAME;
                        break;
                    }
                    if (rdr.getName().compareToIgnoreCase("password") == 0) {
                        myState = ElementState.PASSWORD;
                        break;
                    }
                    if (rdr.getName().compareToIgnoreCase("selection") == 0) {
                        myState = ElementState.SELECTION;
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
                        System.out.println("Name = " + name);
                        break;
                    case STARTKEY:
                        s = rdr.getText().trim();
                        startKey = Integer.parseInt(s);
                        myState = ElementState.UNDEFINED;
                        System.out.println("Startkey = "
                                + Integer.toString(startKey));
                        break;
                    case URL:
                        url = rdr.getText().trim();
                        System.out.println("Url = " + url);
                        break;
                    case CLASS:
                        className = rdr.getText().trim();
                        System.out.println("Class Name = " + className);
                        break;
                    case SERVER:
                        server = rdr.getText().trim();
                        System.out.println("Server = " + server);
                        break;
                    case PORT:
                        port = Integer.parseInt(rdr.getText());
                        System.out.println("Port = " + port);
                        break;
                    case DATABASE:
                        database = rdr.getText().trim();
                        System.out.println("Database = " + database);
                        break;
                    case USERNAME:
                        userName = rdr.getText().trim();
                        System.out.println("User Name = " + userName);
                        break;
                    case PASSWORD:
                        password = rdr.getText().trim();
                        System.out.println("Password = " + password);
                        break;
                    case SELECTION:
                        selection = rdr.getText().trim();
                        System.out.println("Selection = '" + selection + "'");
                        break;
                    }
                    myState = ElementState.UNDEFINED;
                    break;
                case XmlPullParser.END_TAG:
                    if (rdr.getName().compareToIgnoreCase("dbdata") == 0) {
                        prepareParameters();
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
            throw new DataSourceException(ex.getMessage());
        }
    }

    /**
     * Method prepares the parameters for database configuration
     */
    private void prepareParameters() throws DataSourceException {
        switch (myDatabase) {
        case ORACLE:
            if (port == -1)
                port = 1521;
            className = "oracle.jdbc.driver.OracleDriver";
            url = "jdbc:oracle:thin:@" + server + ":" + Integer.toString(port)
                    + ":" + database;
            logger.debug("Url for DbDataProvider '" + name + "' = '" + url
                    + "'");
            break;
        case ODBC:
            className = "sun.jdbc.odbc.JdbcOdbcDriver";
            url = "jdbc:odbc:" + database;
            logger.debug("Url for DbDataProvider '" + name + "' = '" + url
                    + "'");
            break;
        case MYSQL:
            if (port == -1)
                port = 3306;
            className = "com.mysql.jdbc.Driver";
            url = "jdbc:mysql://" + server + ":" + Integer.toString(port) + "/"
                    + database;
            logger.debug("Url for DbDataProvider '" + name + "' = '" + url
                    + "'");
            break;
        case POSTGRESQL:
            if (port == -1)
                port = 5432;
            className = "org.postgresql.Driver";
            url = "jdbc:postgresql://" + server + ":" + Integer.toString(port)
                    + "/" + database;
            logger.debug("Url for DbDataProvider '" + name + "' = '" + url
                    + "'");
            break;
        case SQLSERVER:
            if (port == -1)
                port = 1433;
            className = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
            url = "jdbc:microsoft:sqlserver://" + server + ":"
                    + Integer.toString(port) + ";databaseName=" + database
                    + ";selectMethod=cursor;";
            logger.debug("Url for DbDataProvider '" + name + "' = '" + url
                    + "'");
            break;
        case RAW:
            break;
        default:
            logger.debug("Database Connection for DbDataProvider '" + name
                    + "' not specified.");
            throw new DataSourceException("Database Connection not specified.");
        }
    }

    /**
     * Method opens the connection.
     */
    private Connection openConnection() throws DataSourceException {
        Connection conn = null;
        try {
            Class.forName(className);
            conn = DriverManager.getConnection(url, userName, password);
            if (conn == null) {
                logger.error("URL '" + url + "' failed to open.");
                throw new DataSourceException("Database Connection '" + url
                        + "' failed to open.");
            }
            return conn;
        } catch (ClassNotFoundException cnf) {
            logger.error("Class '" + className + "' failed to load.");
            DataSourceException dse = new DataSourceException(
                    "Class not loaded.");
            dse.initCause(cnf);
            throw dse;
        } catch (Exception ex) {
            DataSourceException dse = new DataSourceException(
                    "Exception in DbDataProvider.openConnection().");
            dse.initCause(ex);
            throw new DataSourceException(ex.getMessage());
        }
    }

    /**
     * Method analyzed the data structure for PointSet dimensioning.
     * 
     * @throws SQLException
     */
    private void analyze() throws DataSourceException, SQLException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        int i;

        logger.info("Analyzing DB Data Source '" + name + "' in PointSet '"
                + pointSetter.getName() + "':");
        if (selection.compareTo("") == 0) {
            logger.error("DB Data Source: Select Statement is empty.");
            throw new DataSourceException(
                    "Database Data Provider: Empty Select Statement.");
        }
        conn = openConnection();
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(selection);

            ResultSetMetaData rsmd = rs.getMetaData();
            int numberOfColumns = rsmd.getColumnCount();
            keyColumnHeaderIndex = -1;
            sourceColumnLabels = new String[numberOfColumns];
            for (i = 1; i <= numberOfColumns; i++) {
                sourceColumnLabels[i - 1] = rsmd.getColumnName(i);
            }
            targetColumnLabels = new String[numberOfColumns];
            for (i = 0; i < sourceColumnLabels.length; i++) {
                if (aliases.containsKey(sourceColumnLabels[i]) == false)
                    targetColumnLabels[i] = sourceColumnLabels[i];
                else
                    targetColumnLabels[i] = aliases.get(sourceColumnLabels[i]);
                if (targetColumnLabels[i].compareTo(keyColumnHeader) == 0)
                    keyColumnHeaderIndex = i;
            }
            if (keyType == KeyType.DATA && keyColumnHeaderIndex == -1) {
                logger.error("No Key Column found in DB Data Source.");
                throw new DataSourceException("No key column found.");
            }
            logger.info("  " + Integer.toString(targetColumnLabels.length)
                    + " Columns found.");

            switch (keyType) {
            case DATA:
                while (rs.next()) {
                    keyLabels.add(rs.getString(keyColumnHeaderIndex + 1));
                }
                break;
            case AUTO:
                int curKey = startKey;
                while (rs.next()) {
                    String x = "00000000" + Integer.toString(curKey);
                    x = x.substring(x.length() - 8, x.length());
                    keyLabels.add(x);
                    curKey++;
                }
                break;
            }
            logger.info("  " + Integer.toString(keyLabels.size())
                    + " Rows found.");
            logger.info("DB Data Source analyzed.");
        } finally {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
            conn.close();
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
        double d;

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        int x, y;

        try {
            logger.info("Retrieving Points from DB Data Source '" + name
                    + "' in PointSet '" + pointSetter.getName() + "':");
            if (selection.compareTo("") == 0) {
                logger.error(" DB Data Source: Select Statement is empty.");
                throw new DataSourceException(
                        "Point Retrieval: Empty Select Statement.");
            }
            conn = openConnection();
            try {
                stmt = conn.createStatement();
                rs = stmt.executeQuery(selection);
                ResultSetMetaData rsmd = rs.getMetaData();
                int numberOfColumns = rsmd.getColumnCount();

                y = 0;
                while (rs.next() == true) {
                    for (x = 0; x < numberOfColumns; x++) {
                        if (keyType == KeyType.DATA
                                && x == keyColumnHeaderIndex)
                            continue;
                        try {
                            d = rs.getDouble(x + 1);
                        } catch (Exception ex) {
                            d = Double.NaN;
                            logger.warn("  Not a Number at Row '"
                                    + keyLabels.get(y) + "', Column '"
                                    + targetColumnLabels[x] + "'");
                        }
                        pointSetter.setPoint(targetColumnLabels[x], keyLabels
                                .get(y), d);
                    }
                    y++;
                }
                logger.info("All DB Data Source Points retrieved.");
            } finally {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                conn.close();
            }
        } catch (Exception ex) {
            logger.error("DB Data Source retrieval aborted.");
            System.out.println("Exception in retrievePoints(): "
                    + ex.getMessage());
            throw new DataSourceException(ex.getMessage());
        }
    }
}
