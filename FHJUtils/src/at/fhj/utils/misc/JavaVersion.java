/*
 * $Id: JavaVersion.java,v 1.1 2006/04/04 07:53:41 boyan Exp $
 */
package at.fhj.utils.misc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ilya Boyandin
 *
 * $Revision: 1.1 $
 */
public class JavaVersion implements Comparable {
  
  private final int major, minor, point;
  private final String fullVersion;
  
  public JavaVersion(int major, int minor, int point) {
    this.major = major;
    this.minor = minor;
    this.point = point;
    this.fullVersion = major + "." + minor + "." + point;
  }
  
  /**
   * Parses the java version out of the java.version system property.
   * Can throw NumberFormatException
   */
  public JavaVersion() {
    this(System.getProperty("java.version"));
  }
  
  /**
   * Parses the java version from the string. Can throw IllegalArgumentException
   */
  public JavaVersion(String versionString) {
    final Pattern pattern = Pattern.compile("([0-9]+)\\.([0-9]+)(\\.([0-9]+))?");
      final Matcher m = pattern.matcher(versionString);
      if (m.find()) {
        major = Integer.parseInt(m.group(1));
        minor = Integer.parseInt(m.group(2));
        final String g4 = m.group(4);
      if (g4 != null) {
          point = Integer.parseInt(g4);
        } else {
          point = 0;
        }
        fullVersion = versionString + " (parsed as " + major + "." + minor + "." + point + ")";
      } else {
        throw new IllegalArgumentException("Couldn't parse java version");
      }
  }

  public String getFullVersion() {
    return fullVersion;
  }

  public int getMajor() {
    return major;
  }
  
  public int getMinor() {
    return minor;
  }
  
  public int getPoint() {
    return point;
  }

  public int compareTo(Object o) {
    final JavaVersion v = (JavaVersion)o;
      if (major < v.major) {
      return -1;
    } else if (major == v.major) {
      if (minor < v.minor) {
        return -1;
      } else if (minor == v.minor) {
        if (point < v.point) {
          return -1;
        } else if (point == v.point) {
          return 0;
        }
      }
    }
      return 1;
  }
  
  public String toString() {
    return fullVersion;
  }
}
