package at.fhjoanneum.cgvis.test.data;

import org.apache.log4j.PropertyConfigurator;

import at.fhjoanneum.cgvis.data.DataUID;
import at.fhjoanneum.cgvis.data.ExPointSet;
import at.fhjoanneum.cgvis.data.Normalization;

import junit.framework.TestCase;

public class ExPointSetTest extends TestCase {

    double[][] vals;
    String[] colLabels;
    String[] rowLabels;
    ExPointSet pointSet;
    double myMin, myMax;
    DataUID[] colUIDs;
    DataUID[] rowUIDs;
    DataUID dataSourceUID;
    DataUID myUID;

    protected void setUp() {
        final int rows = 5;
        final int cols = 3;
        int x, y;
        myMin = Double.MAX_VALUE;
        myMax = Double.MIN_VALUE;
        PropertyConfigurator.configure("log4j.properties");
        vals = new double[rows][cols];
        for (y = 0; y < rows; y++) {
            for (x = 0; x < cols; x++) {
                vals[y][x] = 10 * x + y;
                vals[0][0] = Double.NaN;
                if (Double.compare(vals[y][x], Double.NaN) == 0)
                    continue;
                myMin = Math.min(myMin, vals[y][x]);
                myMax = Math.max(myMax, vals[y][x]);
            }
        }

        colLabels = new String[cols];
        for (x = 0; x < cols; x++) {
            colLabels[x] = "Column" + Integer.toString(x);
        }
        rowLabels = new String[rows];
        for (y = 0; y < rows; y++) {
            rowLabels[y] = "Row" + Integer.toString(y);
        }
        rowUIDs = DataUID.createArrayOfUIDs(rowLabels.length);
        colUIDs = DataUID.createArrayOfUIDs(colLabels.length);
        dataSourceUID = DataUID.createUID();
        myUID = DataUID.createUID();
        pointSet = new ExPointSet("Erik", vals, rowUIDs, rowLabels, colUIDs,
                colLabels, dataSourceUID, myUID);
    }

    public void testExPointSet() {
        boolean b;
        int x, y;

        System.out.println("Global Minimum ... "
                + Double.toString(pointSet.getMinValue()));
        System.out.println("Global Maximum ... "
                + Double.toString(pointSet.getMaxValue()));
        System.out.println("Allocation ....... "
                + Double.toString(pointSet.getAllocation()));
        assertEquals(myMin, pointSet.getMinValue());
        assertEquals(myMax, pointSet.getMaxValue());

        b = pointSet.existsAttribute("Column2");
        System.out.println("Column2 exists ... " + Boolean.toString(b));
        b = pointSet.existsAttribute("Column9");
        System.out.println("Column9 exists ... " + Boolean.toString(b));

        x = pointSet.getElementById(rowUIDs[1]);
        assertEquals(x, 1);
        y = pointSet.getAttrById(colUIDs[2]);
        assertEquals(y, 2);
        x = pointSet.getElementById(DataUID.createUID());
        assertEquals(x, -1);
        y = pointSet.getAttrById(DataUID.createUID());
        assertEquals(y, -1);

        try {
            x = pointSet.getAttributeIndex("Column2");
            System.out.println("CoordinateIndex of 'Column2' ... "
                    + Integer.toString(x));

            pointSet.normalize("Column2", Normalization.RANGE);

            for (y = 0; y < pointSet.getSize(); y++) {
                System.out.print(Double.toString(pointSet.getRawValue(y, x))
                        + " ");
            }
            System.out.println();
            for (y = 0; y < pointSet.getSize(); y++) {
                System.out
                        .print(Double.toString(pointSet.getValue(y, x)) + " ");
            }
            System.out.println();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
