package at.fhj.utils.test;

import at.fhj.utils.misc.JavaVersion;
import junit.framework.TestCase;

public class JavaVersionTest extends TestCase {

  public void testJavaVersion1() {
    JavaVersion jv = new JavaVersion("1.2.3");
    assertEquals(1, jv.getMajor());
    assertEquals(2, jv.getMinor());
    assertEquals(3, jv.getPoint());
  }
    
  public void testJavaVersion2() {
    JavaVersion jv = new JavaVersion("122.3232.0334");
    assertEquals(122, jv.getMajor());
    assertEquals(3232, jv.getMinor());
    assertEquals(334, jv.getPoint());
  }
  
  public void testJavaVersion3() {
    JavaVersion jv = new JavaVersion("122.3232");
    assertEquals(122, jv.getMajor());
    assertEquals(3232, jv.getMinor());
    assertEquals(0, jv.getPoint());
  }
  
  public void testJavaVersion4() {
    JavaVersion jv = new JavaVersion("1.6.0-rc");
    assertEquals(1, jv.getMajor());
    assertEquals(6, jv.getMinor());
    assertEquals(0, jv.getPoint());
  }
  
  public void testJavaVersion5() {
    JavaVersion jv = new JavaVersion("xyz01.06.010zyx");
    assertEquals(1, jv.getMajor());
    assertEquals(6, jv.getMinor());
    assertEquals(10, jv.getPoint());
  }
  
  public void testJavaVersionFailure1() {
    try {
      new JavaVersion("xyz01.");
      fail("Should throw an IllegalArgumentException");
    } catch (IllegalArgumentException ex) {
      // ok
    }
  }
  
  public void testJavaVersionFailure2() {
    try {
      new JavaVersion(".1");
      fail("Should throw an IllegalArgumentException");
    } catch (IllegalArgumentException ex) {
      // ok
    }
  }
  
  public void testJavaVersionNull() {
    try {
      new JavaVersion(null);
      fail("Should throw an NPE");
    } catch (NullPointerException ex) {
      // ok
    }
  }
  
  public void testJavaVersionEmpty() {
    try {
      new JavaVersion("");
      fail("Should throw an IllegalArgumentException");
    } catch (IllegalArgumentException ex) {
      // ok
    }
  }
  
  public void testCompareTo() {
    assertEquals(-1, new JavaVersion("1.4.1").compareTo(new JavaVersion("1.4.2")));
    assertEquals(+1, new JavaVersion("1.4.2").compareTo(new JavaVersion("1.4.1")));
    assertEquals(+0, new JavaVersion("1.5.1").compareTo(new JavaVersion("1.5.1")));
    assertEquals(+1, new JavaVersion("1.5.1").compareTo(new JavaVersion("1.4.2")));
    assertEquals(+1, new JavaVersion("2.5.1").compareTo(new JavaVersion("1.6.2")));
    assertEquals(-1, new JavaVersion("1.5.2").compareTo(new JavaVersion("2.4.1")));
    assertEquals(-1, new JavaVersion("1.4.9").compareTo(new JavaVersion("1.5.0")));
    assertEquals(-1, new JavaVersion("1.9.9").compareTo(new JavaVersion("2.0.0")));
    assertEquals(+1, new JavaVersion("2.9.9").compareTo(new JavaVersion("2.0.0")));
  }

}
