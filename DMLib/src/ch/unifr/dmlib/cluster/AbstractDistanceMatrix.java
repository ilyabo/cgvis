package ch.unifr.dmlib.cluster;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ilya Boyandin
 */
public abstract class AbstractDistanceMatrix<T> implements DistanceMatrix<T> {

    private ClusterNode<T>[] nodes;     // top-level cluster nodes
    private final Linkage<T> linkage;
    private final DistanceMeasure<T> distanceMeasure;
    private final List<T> items;
    private final double maxMergeableDistance;

    public AbstractDistanceMatrix(List<T> items, DistanceMeasure<T> distanceMeasure, Linkage<T> linkage,
            double maxMergeableDistance) {
        this.items = items;
        this.distanceMeasure = distanceMeasure;
        this.linkage = linkage;
        this.maxMergeableDistance = maxMergeableDistance;
        initNodes();
    }

    @SuppressWarnings("unchecked")
    private void initNodes() {
        final int numOfItems = items.size();
        this.nodes = new ClusterNode[numOfItems];
        for (int i = 0; i < numOfItems; i++) {
            this.nodes[i] = createLeafClusterNode(i);
        }
    }

    public DistanceMeasure<T> getDistanceMeasure() {
        return distanceMeasure;
    }

    public List<T> getItems() {
        return items;
    }

    public int getNumOfItems() {
        return items.size();
    }

    protected ClusterNode<T> getNode(int i) {
        return nodes[i];
    }

    public Linkage<T> getLinkage() {
        return linkage;
    }

    /**
     * Returns a list of cluster nodes which are currently on the top level.
     * @see #getMaxMergeableDistance()
     */
    public List<ClusterNode<T>> getTopLevelNodes() {
        List<ClusterNode<T>> nodeList = new ArrayList<ClusterNode<T>>(nodes.length);
        for (ClusterNode<T> node : nodes) {
            if (node != null) {
                nodeList.add(node);
            }
        }
        return nodeList;
    }

    public double getMaxMergeableDistance() {
        return maxMergeableDistance;
    }

    public ClusterNode<T> mergeNearestNeighbours() {
        final int numOfItems = items.size();

        // Find the nearest neighbours
        double minD = Double.NaN;
        int minI = -1, minJ = -1;
        int pairCnt = 0;
        for (int i = 0; i < numOfItems; i++)
            for (int j = 0; j < i; j++) {
                double d = Double.NaN;
                if ((nodes[i] != null) && (nodes[j] != null)) {
                    d = getDistanceBetweenClusterNodes(i, j);
                    if (d <= maxMergeableDistance) {
                        if (Double.isNaN(minD) || d < minD) {
                            minD = d;
                            minI = i;
                            minJ = j;
                        }
                        pairCnt++;
                    }
                }
            }

        // Cluster them
        ClusterNode<T> newNode = null;
        if (pairCnt > 0) {
            final ClusterNode<T> cluster1 = nodes[minI];
            final ClusterNode<T> cluster2 = nodes[minJ];

            newNode = mergeClusterNodes(cluster1, cluster2, minD);

            // now the new node takes place of node minI; node minJ is removed
            nodes[minI] = newNode;
            nodes[minJ] = null;  // this effectively removes the minJ row from the distance matrix

            updateDistances(minI, minJ, newNode);
        }
        return newNode;
    }

    protected abstract void updateDistances(int mergedNode1, int mergedNode2, ClusterNode<T> newNode);

    protected abstract double getDistanceBetweenClusterNodes(int i, int j);

    protected ClusterNode<T> createLeafClusterNode(int itemIndex) {
        return new ClusterNode<T>(items.get(itemIndex), itemIndex);
    }

    /**
     * @param left Left cluster node be merged
     * @param right Right cluster node be merged
     * @param dist Distance between left and right cluster nodes
     */
    protected ClusterNode<T> mergeClusterNodes(ClusterNode<T> left, ClusterNode<T> right, double dist) {
        return new ClusterNode<T>(left, right, dist);
    }

    protected double calcDistanceBetweenItems(int i, int j) {
        return distanceMeasure.distance(items.get(i), items.get(j));
    }

    public double distance(int itemIndex1, int itemIndex2) {
        return calcDistanceBetweenItems(itemIndex1, itemIndex2);
    }

    protected int getClusterNodeIndex(ClusterNode<T> node) {
        for (int i = 0, size = nodes.length; i < size; i++) {
            if (nodes[i] == node) {
                return i;
            }
        }
        return -1;
    }

}
