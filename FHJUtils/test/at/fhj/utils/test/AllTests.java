package at.fhj.utils.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

  public static Test suite() {
    TestSuite suite = new TestSuite("Test for at.fhj.utils.test");
    //$JUnit-BEGIN$
    suite.addTestSuite(JavaVersionTest.class);
    suite.addTestSuite(AxisMarksTest.class);
    suite.addTestSuite(StringUtilsTest.class);
    //$JUnit-END$
    return suite;
  }

}
