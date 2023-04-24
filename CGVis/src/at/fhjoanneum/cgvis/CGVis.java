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

import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import at.fhj.utils.misc.JavaVersion;

import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;

/**
 * @author Ilya Boyandin
 */
public class CGVis {

    public static final String RELEASE_NUMBER = "1.0";
    public static final JavaVersion MIN_JAVA_VERSION = new JavaVersion(1, 5, 0);

    private static Logger logger = Logger.getLogger(CGVis.class.getName());

    public static void main(String[] args) throws IOException {
        // Init Log4j
        PropertyConfigurator.configure("log4j.properties");

        logger.info(">>> Starting CGVis Version " + RELEASE_NUMBER);
        logger.info("Max available memory: "
                + (Runtime.getRuntime().maxMemory() / 1024) + "Kb");

        // Check the Java version
        try {
            JavaVersion version = new JavaVersion();
            logger.info("Java version: " + version);
            if (version.compareTo(MIN_JAVA_VERSION) < 0) {
                logger.info("The java version used is too old: " + version
                        + ". " + "The oldest supported version is "
                        + MIN_JAVA_VERSION);
                JOptionPane
                        .showMessageDialog(
                                null,
                                "Java version used: "
                                        + version
                                        + ".\n"
                                        + "This java version is too old. The oldest supported\n"
                                        + "version is " + MIN_JAVA_VERSION
                                        + ". You can download the newest\n"
                                        + "version at: http://www.java.sun.com");
                logger.info("Exiting application");
                System.exit(1);
            }
        } catch (Throwable th) {
            logger.error("Java version check failed", th);
        }
        logger.info("Java vendor: " + System.getProperty("java.vendor"));

        configureUI();

        final CGVisMainFrame app = new CGVisMainFrame();
        app.init();
        app.setVisible(true);
    }

    private static void configureUI() {
        UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
        Options.setDefaultIconSize(new Dimension(18, 18));

        PlasticLookAndFeel.setPlasticTheme(new ExperienceBlue());
        final String lafName;
        if (LookUtils.IS_OS_MAC) {
            lafName = "ch.randelshofer.quaqua.QuaquaLookAndFeel";
            // lafName = Options.getSystemLookAndFeelClassName();
        } else if (LookUtils.IS_OS_WINDOWS) {
            lafName = "com.jgoodies.looks.windows.WindowsLookAndFeel";
        } else {
            lafName = Options.getSystemLookAndFeelClassName();
        }

        // "com.jgoodies.looks.plastic.PlasticXPLookAndFeel"
        // "com.jgoodies.looks.plastic.Plastic3DLookAndFeel"

        try {
            UIManager.setLookAndFeel(lafName);
        } catch (Exception e) {
            System.err.println("Can't set look & feel:" + e);
        }
    }

    public static final String OS_NAME = System.getProperty("os.name");

    public static boolean IS_OS_MAC = getOSMatches("Mac");

    private static boolean getOSMatches(String osNamePrefix) {
        if (OS_NAME == null) {
            return false;
        }
        return OS_NAME.startsWith(osNamePrefix);
    }

    public static final boolean isAqua() {
        final String lf = UIManager.getLookAndFeel().getName();
        return (lf.indexOf("Aqua") > -1) || (lf.indexOf("Quaqua") > -1);
    }

}
