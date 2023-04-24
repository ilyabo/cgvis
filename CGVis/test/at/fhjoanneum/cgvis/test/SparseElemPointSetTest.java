package at.fhjoanneum.cgvis.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import at.fhjoanneum.cgvis.data.DataUID;
import at.fhjoanneum.cgvis.data.IPointSet;
import at.fhjoanneum.cgvis.data.PointSet;
import at.fhjoanneum.cgvis.data.SparseElemPointSet;

public class SparseElemPointSetTest {

    @Test
    public void testJoinElementsByLabel() {
        IPointSet[] ps = new IPointSet[] {
                new PointSet("ps1", new double[][] { { 5, 5 }, { 6, 6 },
                        { 1, 1 }, { 2, 2 } }, DataUID.createArrayOfUIDs(4),
                        new String[] { "e", "f", "a", "b" }, DataUID
                                .createArrayOfUIDs(2),
                        new String[] { "x", "y" }),
                new PointSet("ps2", new double[][] { { 3, 3 }, { 6, 6 } },
                        DataUID.createArrayOfUIDs(2),
                        new String[] { "c", "f" },
                        DataUID.createArrayOfUIDs(2), new String[] { "x", "y" }),
                new PointSet("ps3", new double[][] { { 1, 1 }, { 3, 3 },
                        { 5, 5 }, { 6, 6 }, { 4, 4 } }, DataUID
                        .createArrayOfUIDs(5), new String[] { "a", "c", "e",
                        "f", "d" }, DataUID.createArrayOfUIDs(2), new String[] {
                        "x", "y" }) };

        ps = SparseElemPointSet.joinElementsByLabel(ps);

        assertEquals(3, ps.length);
        assertEquals("abcdef 12..56", pointSetToString(ps[0]));
        assertEquals("abcdef ..3..6", pointSetToString(ps[1]));
        assertEquals("abcdef 1.3456", pointSetToString(ps[2]));

        // for (int i = 0; i < 3; i++) assertEquals(6, ps[i].getSize());
        // for (int i = 0; i < 3; i++) assertEquals(6, ps[i].getSize());
    }

    private String pointSetToString(IPointSet ps) {
        StringBuffer sb = new StringBuffer();
        for (int k = 0, size = ps.getSize(); k < size; k++) {
            final String lb = ps.getElementLabel(k);
            sb.append(lb != null ? lb : ".");
        }
        sb.append(' ');
        for (int k = 0, size = ps.getSize(); k < size; k++) {
            final double v = ps.getValue(k, 0);
            sb.append(!Double.isNaN(v) ? ((char) ((int) v + '0')) : '.');
        }
        return sb.toString();
    }

}
