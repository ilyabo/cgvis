package at.fhj.utils.test;

import at.fhj.utils.graphics.AxisMarks;
import junit.framework.TestCase;


public class AxisMarksTest extends TestCase {
  
  private static double EPS = 1e-7;
  
  public void testOrdAplha() {
    assertEquals(AxisMarks.ordAlpha(0), 0, EPS);
    assertEquals(AxisMarks.ordAlpha(1), 1, EPS);
    assertEquals(AxisMarks.ordAlpha(100), 100, EPS);
    assertEquals(AxisMarks.ordAlpha(109.92), 100, EPS);
    assertEquals(AxisMarks.ordAlpha(2345), 1000, EPS);
    assertEquals(AxisMarks.ordAlpha(19), 10, EPS);
    assertEquals(AxisMarks.ordAlpha(4.44), 1, EPS);
    assertEquals(AxisMarks.ordAlpha(0.44), 0.1, EPS);
    assertEquals(AxisMarks.ordAlpha(0.033), 0.01, EPS);
    assertEquals(AxisMarks.ordAlpha(0.0033), 0.001, EPS);
    assertEquals(AxisMarks.ordAlpha(-1), -1, EPS);
    assertEquals(AxisMarks.ordAlpha(-10), -10, EPS);
    assertEquals(AxisMarks.ordAlpha(-13.96), -10, EPS);
    assertEquals(AxisMarks.ordAlpha(-130), -100, EPS);
    assertEquals(AxisMarks.ordAlpha(-1234), -1000, EPS);
    assertEquals(AxisMarks.ordAlpha(-0.55), -0.1, EPS);
    assertEquals(AxisMarks.ordAlpha(-0.055), -0.01, EPS);
    assertEquals(AxisMarks.ordAlpha(Double.POSITIVE_INFINITY), Double.POSITIVE_INFINITY, EPS);
    assertEquals(AxisMarks.ordAlpha(Double.NEGATIVE_INFINITY), Double.NEGATIVE_INFINITY, EPS);
    assertTrue(Double.isNaN(AxisMarks.ordAlpha(Double.NaN)));
  }

  public void testAxeMarks() {
    AxisMarks marks = new AxisMarks(0, 100, 20, 90);
    marks.setPlotSize(399, 399);
    marks.calc(0, 100, -100, 100);
    assertEquals(marks.getXStart(), 0, EPS); 
    assertEquals(marks.getXStep(), 10, EPS);
    assertEquals(marks.getYStart(), -100, EPS); 
    assertEquals(marks.getYStep(), 20, EPS);
    
    marks.calc(0, 100, -0.12, 100);
    assertEquals(marks.getXStart(), 0, EPS);
    assertEquals(marks.getXStep(), 10, EPS);
    assertEquals(marks.getYStart(), 0, EPS);
    assertEquals(marks.getYStep(), 10, EPS);

    marks.calc(0, 100, -0.012, 109);
    assertEquals(marks.getYStart(), 0, EPS);
    assertEquals(marks.getYStep(), 10, EPS);

    marks.calc(0, 100, -10.012, 111);
    assertEquals(marks.getYStart(), -10, EPS);
    assertEquals(marks.getYStep(), 10, EPS);

    marks.calc(0, 100, 176.92, 250);
    assertEquals(marks.getYStart(), 180, EPS);
    assertEquals(marks.getYStep(), 10, EPS);

    marks.calc(0, 100, 0, 1000);
    assertEquals(marks.getYStart(), 0, EPS);
    assertEquals(marks.getYStep(), 100, EPS);

    marks.calc(0, 100, -10, 1000);
    assertEquals(marks.getYStart(), 0, EPS);
    assertEquals(marks.getYStep(), 100, EPS);
    
    marks.calc(0, 100, -325.67, 864.23);
    assertEquals(marks.getYStart(), -300, EPS);
    assertEquals(marks.getYStep(), 100, EPS);
    
    marks.calc(0, 100, -32.67, 86.23);
    assertEquals(marks.getYStart(), -30, EPS);
    assertEquals(marks.getYStep(), 10, EPS);
    
    marks.calc(0, 100, -320.67, 4.3);
    assertEquals(marks.getYStart(), -320, EPS);
    assertEquals(marks.getYStep(), 20, EPS);
    
    marks.calc(0, 100, -3200.67, 2100.3);
    assertEquals(marks.getYStart(), -3000, EPS);
    assertEquals(marks.getYStep(), 1000, EPS);
    
    marks.calc(0, 100, -0.0320067, 0.02100);
    assertEquals(marks.getYStart(), -0.03000, EPS);
    assertEquals(marks.getYStep(), 0.01000, EPS);
    
    marks.calc(0, 100, -0.0320067, 0.02100);
    assertEquals(marks.getYStart(), -0.03000, EPS);
    assertEquals(marks.getYStep(), 0.01000, EPS);
    
    marks.calc(0, 100, 1150, 3013);
    assertEquals(marks.getYStart(), 1200, EPS);
    assertEquals(marks.getYStep(), 100, EPS);
    
    marks.calc(0, 100, 0, 1);
    assertEquals(marks.getYStart(), 0, EPS);
    assertEquals(marks.getYStep(), 0.1, EPS);
    
    marks.calc(0, 100, 0, 10);
    assertEquals(marks.getYStart(), 0, EPS);
    assertEquals(marks.getYStep(), 1, EPS);
    
    marks.calc(0, 100, -1, 0);
    assertEquals(marks.getYStart(), -1, EPS);
    assertEquals(marks.getYStep(), 0.1, EPS);
    
    marks.calc(0, 100, 0, 0);
    assertEquals(marks.getYStart(), 0, EPS);
    assertEquals(marks.getYStep(), 0, EPS);
    
    marks.calc(0, 100, 10, 0);  // swap min and max
    assertEquals(marks.getYStart(), 0, EPS);
    assertEquals(marks.getYStep(), 1, EPS);

    marks.calc(0, 100, -32343.78, 0.0423);
    assertEquals(marks.getYStart(), -32000, EPS);
    assertEquals(marks.getYStep(), 2000, EPS);

    marks.calc(0, 100, 1, 1998);
    assertEquals(marks.getYStart(), 200, EPS);
    assertEquals(marks.getYStep(), 200, EPS);
    

    // Test maxDist change
    AxisMarks marks1 = new AxisMarks(0, 100, 20, 90);
    marks1.setPlotSize(399, 399);
    marks1.calc(0, 100, -0.012, 0.033);
    assertEquals(marks1.getYStart(), -0.01, EPS);
    assertEquals(marks1.getYStep(), 0.01, EPS);

    AxisMarks marks2 = new AxisMarks(0, 100, 20, 60);
    marks2.setPlotSize(399, 399);
    marks2.calc(0, 100, -0.012, 0.033);
    assertEquals(marks2.getYStart(), -0.012, EPS);
    assertEquals(marks2.getYStep(), 0.003, EPS);

  
    // Test small plot size
    AxisMarks marks3 = new AxisMarks(0, 100, 20, 90);
    marks3.setPlotSize(399, 10);
    marks3.calc(0, 100, -0.012, 0.033);
    assertEquals(marks3.getYStart(), 0, EPS);
    assertEquals(marks3.getYStep(), 0, EPS);
  
  }
}
