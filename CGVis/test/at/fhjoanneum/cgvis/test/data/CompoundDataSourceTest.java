package at.fhjoanneum.cgvis.test.data;

import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.PropertyConfigurator;

import at.fhjoanneum.cgvis.data.CompoundDataSource;
import at.fhjoanneum.cgvis.data.IPointSet;

/**
 * @author Erik Koerner
 */
public class CompoundDataSourceTest extends TestCase {
    private CompoundDataSource cds;

    protected void setUp() {
        PropertyConfigurator.configure("log4j.properties");
    }

    /*
     * Test method for 'at.fhjoanneum.cgvis.data.CompoundDataSource()'
     */
    public void testCompoundDataSource() {
        try {
            cds = new CompoundDataSource("data/TME_all.cgv");
            cds.init(null);

            List<IPointSet> pointSets = cds.getPointSets();
            for (int i = 0; i < pointSets.size(); i++) {
                IPointSet ps = pointSets.get(i);
                System.out.println();
                System.out.println("Point Set = '" + ps.getName() + "'");
                System.out.println("Number of Rows ........ "
                        + Integer.toString(ps.getSize()));
                System.out.println("Number of Columns ..... "
                        + Integer.toString(ps.getDimension()));
                for (int j = 0; j < ps.getSize(); j++) {
                    System.out.println("Row " + Integer.toString(j)
                            + " Header .......... " + ps.getElementLabel(j));
                }
                for (int k = 0; k < ps.getDimension(); k++) {
                    System.out.println("Column " + Integer.toString(k)
                            + " Header ...." + ps.getAttributeLabel(k));
                }
                System.out.println("******* Row 0:");
                for (int j = 0; j < ps.getDimension(); j++) {
                    System.out.println(ps.getValue(0, j));
                }
                System.out.println("******* Row 2:");
                for (int j = 0; j < ps.getDimension(); j++) {
                    System.out.println(ps.getValue(2, j));
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        // CompoundDataSourceTest.assertTrue(false);
    }

}
