package at.fhjoanneum.cgvis.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import at.fhj.utils.misc.ProgressTracker;
import at.fhjoanneum.cgvis.cluster.PointSetHierarchicalClusterer;
import at.fhjoanneum.cgvis.data.DataUID;
import at.fhjoanneum.cgvis.data.PointSet;
import ch.unifr.dmlib.cluster.ClusterNode;

public class PointSetHierarchicalClustererTest {

    @Test
    public void test2DClustering() {
        PointSet ps = new PointSet("testps", new double[][] { { 3, 6 },
                { 1, 2 }, { 1, 1 }, { 4, 2 }, { 4, 1 }, { 5, 1.5 } }, DataUID
                .createArrayOfUIDs(6), new String[] { "a", "b", "c", "d", "e",
                "f" }, DataUID.createArrayOfUIDs(2), new String[] { "x", "y" });
        PointSetHierarchicalClusterer hc = new PointSetHierarchicalClusterer();
        hc.cluster(ps, new ProgressTracker());
        ClusterNode c = hc.getRootCluster();

        final String dump = c.dumpToString();
//        assertEquals("{{{'f'{'e''d'}}{'c''b'}}'a'}", dump);
//        assertEquals("{{{'5'{'4''3'}}{'2''1'}}'0'}", dump);
        assertEquals("{{{'[5.0, 1.5]'{'[4.0, 1.0]''[4.0, 2.0]'}}{'[1.0, 1.0]''[1.0, 2.0]'}}'[3.0, 6.0]'}", dump);

        final int[] elements = c.getItemIndices();
        assertArrayEquals(new int[] { 0, 1, 2, 3, 4, 5 }, elements, true);

        ClusterNode leftChild = c.getLeftChild();
        ClusterNode rightChild = c.getRightChild();

        ClusterNode big, small;
        if (leftChild.getItemIndices().length > rightChild.getItemIndices().length) {
            big = leftChild;
            small = rightChild;
        } else {
            big = rightChild;
            small = leftChild;
        }
        assertArrayEquals(new int[] { 1, 2, 3, 4, 5 }, big.getItemIndices(), true);
        assertArrayEquals(new int[] { 0 }, small.getItemIndices(), true);

        leftChild = big.getLeftChild();
        rightChild = big.getRightChild();
        if (leftChild.getItemIndices().length > rightChild.getItemIndices().length) {
            big = leftChild;
            small = rightChild;
        } else {
            big = rightChild;
            small = leftChild;
        }
        assertArrayEquals(new int[] { 1, 2 }, small.getItemIndices(), true);
        assertArrayEquals(new int[] { 3, 4, 5 }, big.getItemIndices(), true);

        leftChild = big.getLeftChild();
        rightChild = big.getRightChild();
        if (leftChild.getItemIndices().length > rightChild.getItemIndices().length) {
            big = leftChild;
            small = rightChild;
        } else {
            big = rightChild;
            small = leftChild;
        }
        assertArrayEquals(new int[] { 5 }, small.getItemIndices(), true);
        assertArrayEquals(new int[] { 3, 4 }, big.getItemIndices(), true);
    }

    @Test
    public void test2DClustering2() {
        PointSet ps = new PointSet("testps",
                new double[][] { { 1, 1 }, { 5, 7 }, { 8, 4 }, { 1, 9 },
                        { 3, 2 }, { 1, 8 }, { 4, 8 }, { 9, 3 }, { 10, 4 },
                        { 2, 2 }, { 5, 1 }, { 6, 10 }, { 10, 2 } }, DataUID
                        .createArrayOfUIDs(13), new String[] { "a", "b", "c",
                        "d", "e", "f", "g", "h", "i", "j", "k", "l", "m" },
                DataUID.createArrayOfUIDs(2), new String[] { "x", "y" });
        PointSetHierarchicalClusterer hc = new PointSetHierarchicalClusterer();
        hc.cluster(ps, new ProgressTracker());
        ClusterNode c = hc.getRootCluster();

        final String dump = c.dumpToString();
        assertEquals(
//                "{{{'m'{'i'{'h''c'}}}{'k'{{'j''e'}'a'}}}{{'l'{'g''b'}}{'f''d'}}}",
//                "{{{'12'{'8'{'7''2'}}}{'10'{{'9''4'}'0'}}}{{'11'{'6''1'}}{'5''3'}}}",
                "{{{'[10.0, 2.0]'{'[10.0, 4.0]'{'[9.0, 3.0]''[8.0, 4.0]'}}}{'[5.0, 1.0]'{{'[2.0, 2.0]''[3.0, 2.0]'}'[1.0, 1.0]'}}}{{'[6.0, 10.0]'{'[4.0, 8.0]''[5.0, 7.0]'}}{'[1.0, 8.0]''[1.0, 9.0]'}}}",
                dump);

        final int[] elements = c.getItemIndices();
        assertArrayEquals(
                new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 },
                elements, true);
    }

    void assertArrayEquals(int[] a, int[] b, boolean orderInsensitive) {
        assertTrue(a.length == b.length);
        if (orderInsensitive) {
            int[] a2 = new int[a.length];
            System.arraycopy(a, 0, a2, 0, a.length);
            int[] b2 = new int[b.length];
            System.arraycopy(b, 0, b2, 0, b.length);
            Arrays.sort(a2);
            Arrays.sort(b2);
            a = a2;
            b = b2;
        }
        for (int i = 0; i < a.length; i++) {
            assertEquals(a[i], b[i]);
        }
    }
}
