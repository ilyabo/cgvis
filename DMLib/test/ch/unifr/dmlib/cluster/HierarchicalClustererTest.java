package ch.unifr.dmlib.cluster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import at.fhj.utils.misc.ProgressTracker;

public class HierarchicalClustererTest {

    @Test
    public void test2DClustering() {
        ClusterNode<Point2D> c =
            HierarchicalClusterer
                .createWith(PointDistance.EUCLIDEAN, Linkages.<Point2D>single())
                .build()
                .clusterToRoot(
                    Arrays.asList(point( 3, 6 ), point( 1, 2 ), point( 1, 1 ), point( 4, 2 ), point( 4, 1 ), point( 5, 1.5 )),
                    new ProgressTracker()
                );

        String dump = shorten(c.dumpToString());
        assertEquals("{{{'[5.0, 1.5]'{'[4.0, 1.0]''[4.0, 2.0]'}}{'[1.0, 1.0]''[1.0, 2.0]'}}'[3.0, 6.0]'}", dump);


        final int[] elements = c.getItemIndices();
        assertArrayEquals(new int[] { 0, 1, 2, 3, 4, 5 }, elements, true);

        ClusterNode<Point2D> l = c.getLeftChild();
        ClusterNode<Point2D> r = c.getRightChild();

        ClusterNode<Point2D> big, small;
        if (l.getItemIndices().length > r.getItemIndices().length) {
            big = l;
            small = r;
        } else {
            big = r;
            small = l;
        }
        assertArrayEquals(new int[] { 1, 2, 3, 4, 5 }, big.getItemIndices(), true);
        assertArrayEquals(new int[] { 0 }, small.getItemIndices(), true);

        l = big.getLeftChild();
        r = big.getRightChild();
        if (l.getItemIndices().length > r.getItemIndices().length) {
            big = l;
            small = r;
        } else {
            big = r;
            small = l;
        }
        assertArrayEquals(new int[] { 1, 2 }, small.getItemIndices(), true);
        assertArrayEquals(new int[] { 3, 4, 5 }, big.getItemIndices(), true);

        l = big.getLeftChild();
        r = big.getRightChild();
        if (l.getItemIndices().length > r.getItemIndices().length) {
            big = l;
            small = r;
        } else {
            big = r;
            small = l;
        }
        assertArrayEquals(new int[] { 5 }, small.getItemIndices(), true);
        assertArrayEquals(new int[] { 3, 4 }, big.getItemIndices(), true);
    }

    @Test
    public void test2DClustering2() {
        ClusterNode<Point2D> c =
            HierarchicalClusterer
                .createWith(PointDistance.EUCLIDEAN, Linkages.<Point2D>single())
                .build()
                .clusterToRoot(
                    Arrays.asList(
                            point( 1, 1 ), point( 5, 7 ), point( 8, 4 ), point( 1, 9 ),
                            point( 3, 2 ), point( 1, 8 ), point( 4, 8 ), point( 9, 3 ), point( 10, 4 ),
                            point( 2, 2 ), point( 5, 1 ), point( 6, 10 ), point( 10, 2 )
                      ),
                    new ProgressTracker()
                );

        String dump = shorten(c.dumpToString());
        assertEquals(
                "{{{'[10.0, 2.0]'{'[10.0, 4.0]'{'[9.0, 3.0]''[8.0, 4.0]'}}}{'[5.0, 1.0]'{{'[2.0, 2.0]''[3.0, 2.0]'}'[1.0, 1.0]'}}}" +
                "{{'[6.0, 10.0]'{'[4.0, 8.0]''[5.0, 7.0]'}}{'[1.0, 8.0]''[1.0, 9.0]'}}}",
                dump
        );

        int[] elements = c.getItemIndices();
        assertArrayEquals(
                new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 },
                elements, true);

    }


    @Test
    public void testMaxMergeableDistance() {
        List<Point2D> points = Arrays.asList(point( 3, 6 ), point( 1, 2 ), point( 1, 1 ), point( 4, 2 ), point( 4, 1 ), point( 5, 1.5 ));

        List<ClusterNode<Point2D>> nodes =
            HierarchicalClusterer
                .createWith(PointDistance.EUCLIDEAN, Linkages.<Point2D>single())
                .withMaxMergeableDistance(1.0)
                .build()
                .cluster(points, new ProgressTracker());

        assertEquals(
                " <'[3.0, 6.0]'>  <{'[1.0, 1.0]''[1.0, 2.0]'}>  <{'[4.0, 1.0]''[4.0, 2.0]'}>  <'[5.0, 1.5]'> ",
                shorten(dumpClusterNodeList(nodes))
        );


        nodes =
            HierarchicalClusterer
                .createWith(PointDistance.EUCLIDEAN, Linkages.<Point2D>single())
                .withMaxMergeableDistance(1.5)
                .build()
                .cluster(points, new ProgressTracker());
        assertEquals(
                " <'[3.0, 6.0]'>  <{'[1.0, 1.0]''[1.0, 2.0]'}>  <{'[5.0, 1.5]'{'[4.0, 1.0]''[4.0, 2.0]'}}> ",
                shorten(dumpClusterNodeList(nodes))
        );

        nodes =
            HierarchicalClusterer
                .createWith(PointDistance.EUCLIDEAN, Linkages.<Point2D>single())
                .withMaxMergeableDistance(0)
                .build()
                .cluster(points, new ProgressTracker());
        assertEquals(
                " <'[3.0, 6.0]'>  <'[1.0, 2.0]'>  <'[1.0, 1.0]'>  <'[4.0, 2.0]'>  <'[4.0, 1.0]'>  <'[5.0, 1.5]'> ",
                shorten(dumpClusterNodeList(nodes))
        );

    }

    private static String shorten(String dump) {
        return dump.replaceAll("Point2D.Double", "");
    }

    private static <T> String dumpClusterNodeList(List<ClusterNode<T>> nodeList) {
        StringBuilder sb = new StringBuilder();
        for (ClusterNode<T> node : nodeList) {
            sb.append(" <");
            sb.append(node.dumpToString());
            sb.append("> ");
        }
        return sb.toString();
    }

    static enum PointDistance implements DistanceMeasure<Point2D> {
        EUCLIDEAN {
            public double distance(Point2D a, Point2D b) {
                return a.distance(b);
            }
        };
    }

    static Point2D point(double x, double y) {
        return new Point2D.Double(x, y);
    }

    void assertArrayEquals(int[] a, int[] b, boolean sortFirst) {
        assertTrue(a.length == b.length);
        if (sortFirst) {
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
