package at.fhjoanneum.cgvis.test;

import static org.junit.Assert.*;

import org.junit.Test;

import at.fhjoanneum.cgvis.util.DoubleAxisMarks;

public class DoubleAxisMarksTest {

    private static double EPS = 1e-7;

    @Test
    public void testOrdAplha() {
        assertEquals(DoubleAxisMarks.ordAlpha(0), 0, EPS);
        assertEquals(DoubleAxisMarks.ordAlpha(1), 1, EPS);
        assertEquals(DoubleAxisMarks.ordAlpha(100), 100, EPS);
        assertEquals(DoubleAxisMarks.ordAlpha(109.92), 100, EPS);
        assertEquals(DoubleAxisMarks.ordAlpha(2345), 1000, EPS);
        assertEquals(DoubleAxisMarks.ordAlpha(19), 10, EPS);
        assertEquals(DoubleAxisMarks.ordAlpha(4.44), 1, EPS);
        assertEquals(DoubleAxisMarks.ordAlpha(0.44), 0.1, EPS);
        assertEquals(DoubleAxisMarks.ordAlpha(0.033), 0.01, EPS);
        assertEquals(DoubleAxisMarks.ordAlpha(0.0033), 0.001, EPS);
        assertEquals(DoubleAxisMarks.ordAlpha(-1), -1, EPS);
        assertEquals(DoubleAxisMarks.ordAlpha(-10), -10, EPS);
        assertEquals(DoubleAxisMarks.ordAlpha(-13.96), -10, EPS);
        assertEquals(DoubleAxisMarks.ordAlpha(-130), -100, EPS);
        assertEquals(-1000, DoubleAxisMarks.ordAlpha(-1234), EPS);
        assertEquals(-0.1, DoubleAxisMarks.ordAlpha(-0.55), EPS);
        assertEquals(-0.01, DoubleAxisMarks.ordAlpha(-0.055), EPS);
        assertEquals(Double.POSITIVE_INFINITY, DoubleAxisMarks
                .ordAlpha(Double.POSITIVE_INFINITY), EPS);
        assertEquals(Double.NEGATIVE_INFINITY, DoubleAxisMarks
                .ordAlpha(Double.NEGATIVE_INFINITY), EPS);
        assertTrue(Double.isNaN(DoubleAxisMarks.ordAlpha(Double.NaN)));
        assertEquals(-1E15, DoubleAxisMarks.ordAlpha(-3.359116503111824E15),
                EPS);
        assertEquals(1E17, DoubleAxisMarks.ordAlpha(2.34764920050815296E17),
                EPS);
        assertEquals(1E306, DoubleAxisMarks.ordAlpha(3.14159E306), 1E291);
    }

    @Test
    public void testCalc1() {
        DoubleAxisMarks marks = new DoubleAxisMarks(20, 90);
        marks.calc(0, 100, 399);
        assertEquals(0, marks.getStart(), EPS);
        assertEquals(10, marks.getStep(), EPS);
    }

    @Test
    public void testCalc2() {
        DoubleAxisMarks marks = new DoubleAxisMarks(20, 90);
        marks.calc(-100, 100, 399);
        assertEquals(-100, marks.getStart(), EPS);
        assertEquals(20, marks.getStep(), EPS);
    }

    @Test
    public void testCalc3() {
        DoubleAxisMarks marks = new DoubleAxisMarks(20, 90);
        marks.calc(0, 100, 399);
        assertEquals(0, marks.getStart(), EPS);
        assertEquals(10, marks.getStep(), EPS);
    }

    @Test
    public void testCalc4() {
        DoubleAxisMarks marks = new DoubleAxisMarks(20, 90);
        marks.calc(-0.12, 100, 399);
        assertEquals(0, marks.getStart(), EPS);
        assertEquals(10, marks.getStep(), EPS);
    }

    @Test
    public void testCalc5() {
        DoubleAxisMarks marks = new DoubleAxisMarks(20, 90);
        marks.calc(-0.012, 109, 399);
        assertEquals(0, marks.getStart(), EPS);
        assertEquals(10, marks.getStep(), EPS);
    }

    @Test
    public void testCalc6() {
        DoubleAxisMarks marks = new DoubleAxisMarks(20, 90);
        marks.calc(-10.012, 111, 399);
        assertEquals(-10, marks.getStart(), EPS);
        assertEquals(10, marks.getStep(), EPS);
    }

    @Test
    public void testCalc7() {
        DoubleAxisMarks marks = new DoubleAxisMarks(20, 90);
        marks.calc(176.92, 250, 399);
        assertEquals(180, marks.getStart(), EPS);
        assertEquals(10, marks.getStep(), EPS);
    }

    @Test
    public void testCalc8() {
        DoubleAxisMarks marks = new DoubleAxisMarks(20, 90);
        marks.calc(0, 1000, 399);
        assertEquals(0, marks.getStart(), EPS);
        assertEquals(100, marks.getStep(), EPS);
    }

    @Test
    public void testCalc9() {
        DoubleAxisMarks marks = new DoubleAxisMarks(20, 90);
        marks.calc(-10, 1000, 399);
        assertEquals(0, marks.getStart(), EPS);
        assertEquals(100, marks.getStep(), EPS);
    }

    @Test
    public void testCalc10() {
        DoubleAxisMarks marks = new DoubleAxisMarks(20, 90);
        marks.calc(-325.67, 864.23, 399);
        assertEquals(-300, marks.getStart(), EPS);
        assertEquals(100, marks.getStep(), EPS);
    }

    @Test
    public void testCalc11() {
        DoubleAxisMarks marks = new DoubleAxisMarks(20, 90);
        marks.calc(-32.67, 86.23, 399);
        assertEquals(-30, marks.getStart(), EPS);
        assertEquals(10, marks.getStep(), EPS);
    }

    @Test
    public void testCalc12() {
        DoubleAxisMarks marks = new DoubleAxisMarks(20, 90);
        marks.calc(-320.67, 4.3, 399);
        assertEquals(-320, marks.getStart(), EPS);
        assertEquals(20, marks.getStep(), EPS);
    }

    @Test
    public void testCalc13() {
        DoubleAxisMarks marks = new DoubleAxisMarks(20, 90);
        marks.calc(-3200.67, 2100.3, 399);
        assertEquals(-3000, marks.getStart(), EPS);
        assertEquals(1000, marks.getStep(), EPS);
    }

    @Test
    public void testCalc14() {
        DoubleAxisMarks marks = new DoubleAxisMarks(20, 90);
        marks.calc(-0.0320067, 0.02100, 399);
        assertEquals(-0.03000, marks.getStart(), EPS);
        assertEquals(0.01000, marks.getStep(), EPS);
    }

    @Test
    public void testCalc15() {
        DoubleAxisMarks marks = new DoubleAxisMarks(20, 90);
        marks.calc(-0.0320067, 0.02100, 399);
        assertEquals(-0.03000, marks.getStart(), EPS);
        assertEquals(0.01000, marks.getStep(), EPS);
    }

    @Test
    public void testCalc16() {
        DoubleAxisMarks marks = new DoubleAxisMarks(20, 90);
        marks.calc(1150, 3013, 399);
        assertEquals(1200, marks.getStart(), EPS);
        assertEquals(100, marks.getStep(), EPS);
    }

    @Test
    public void testCalc17() {
        DoubleAxisMarks marks = new DoubleAxisMarks(20, 90);
        marks.calc(0, 1, 399);
        assertEquals(0, marks.getStart(), EPS);
        assertEquals(0.1, marks.getStep(), EPS);
    }

    @Test
    public void testCalc18() {
        DoubleAxisMarks marks = new DoubleAxisMarks(20, 90);
        marks.calc(0, 10, 399);
        assertEquals(0, marks.getStart(), EPS);
        assertEquals(1, marks.getStep(), EPS);
    }

    @Test
    public void testCalc19() {
        DoubleAxisMarks marks = new DoubleAxisMarks(20, 90);
        marks.calc(-1, 0, 399);
        assertEquals(-1, marks.getStart(), EPS);
        assertEquals(0.1, marks.getStep(), EPS);
    }

    @Test
    public void testCalc20() {
        DoubleAxisMarks marks = new DoubleAxisMarks(20, 90);
        marks.calc(0, 0, 399);
        assertEquals(0, marks.getStart(), EPS);
        assertEquals(0, marks.getStep(), EPS);
    }

    @Test
    public void testCalc21() {
        DoubleAxisMarks marks = new DoubleAxisMarks(20, 90);
        marks.calc(10, 0, 399); // swap min and max
        assertEquals(0, marks.getStart(), EPS);
        assertEquals(1, marks.getStep(), EPS);
    }

    @Test
    public void testCalc22() {
        DoubleAxisMarks marks = new DoubleAxisMarks(20, 90);
        marks.calc(-32343.78, 0.0423, 399);
        assertEquals(-32000, marks.getStart(), EPS);
        assertEquals(2000, marks.getStep(), EPS);
    }

    @Test
    public void testCalc23() {
        DoubleAxisMarks marks = new DoubleAxisMarks(20, 90);
        marks.calc(1, 1998, 399);
        assertEquals(200, marks.getStart(), EPS);
        assertEquals(200, marks.getStep(), EPS);
    }

    @Test
    public void testMaxDistChange1() {
        DoubleAxisMarks marks1 = new DoubleAxisMarks(20, 90);

        marks1.calc(-0.012, 0.033, 399);
        assertEquals(-0.01, marks1.getStart(), EPS);
        assertEquals(0.01, marks1.getStep(), EPS);
    }

    @Test
    public void testMaxDistChange2() {
        DoubleAxisMarks marks2 = new DoubleAxisMarks(20, 60);
        marks2.calc(-0.012, 0.033, 399);
        assertEquals(-0.012, marks2.getStart(), EPS);
        assertEquals(0.003, marks2.getStep(), EPS);
    }

    @Test
    public void testSmallPlotSize() {
        DoubleAxisMarks marks3 = new DoubleAxisMarks(20, 90);
        marks3.calc(-0.012, 0.033, 70);
        assertEquals(0, marks3.getStart(), EPS);
        assertEquals(0, marks3.getStep(), EPS);
    }

    // @Test public void testBigDifference() {
    // AxisMarks am = new AxisMarks(40, 80);
    // am.calc(-3.359116503111824, 2.34764920050815296E2, 1.3042107300953777E8);
    //		
    // System.out.println(am.getStart());
    // System.out.println(am.getStep());
    // System.out.println(am.getNumSteps());
    // }

    // @Test public void testHugeValues() {
    // AxisMarks am = new AxisMarks(40, 80);
    //		
    // am.calc(-3.359116503111824E15, 2.34764920050815296E17,
    // 1.3042107300953777E8);
    // // assertEquals(0, am.getStart(), EPS);
    // // assertEquals(0, am.getStep(), EPS);
    // }

    @Test
    public void testHugeValues2() {
        DoubleAxisMarks am = new DoubleAxisMarks(40, 80);

        am.calc(-6.108496899716647E8, 2.080908983991756E10, 40150.338958180495);
        // assertEquals(0, am.getStart(), EPS);
        // assertEquals(0, am.getStep(), EPS);
        // System.out.println(am.getStart());
        // System.out.println(am.getStep());
        // System.out.println(am.getNumSteps());
    }

    @Test
    public void testInfiniteLoop() {
        final DoubleAxisMarks am = new DoubleAxisMarks(80, 71.75);

        final double max = 3.4053901282534738;
        final double min = 3.40539010423356;
        am.calc(min, max, 574.0);

        final double numSteps = (max - am.getStart()) / am.getStep();
        assertTrue(numSteps < (max - min) / am.getStep());
    }
}
