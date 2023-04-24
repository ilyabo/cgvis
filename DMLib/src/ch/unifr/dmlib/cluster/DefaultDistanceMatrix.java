package ch.unifr.dmlib.cluster;

import java.util.List;

import at.fhj.utils.misc.ProgressTracker;

/**
 * @author Ilya Boyandin
 */
public class DefaultDistanceMatrix<T> extends AbstractDistanceMatrix<T> {

	private double itemDistances[][];	// doesn't change after it's initialized
	// TODO: keep the distances in a SortedSet to improve performance
    private double nodeDistances[][];  	// is modified during clustering

    public DefaultDistanceMatrix(List<T> items, DistanceMeasure<T> distanceMeasure, Linkage<T> linkage) {
        this(items, distanceMeasure, linkage, Double.POSITIVE_INFINITY);
    }

    /**
     * @param maxMergeableDistance Limits the distance between items/nodes which can be merged.
     *        Default value is {@code POSITIVE_INFINITY}. See also {@link #getTopLevelNodes()}
     */
    public DefaultDistanceMatrix(List<T> items, DistanceMeasure<T> distanceMeasure, Linkage<T> linkage,
            double maxMergeableDistance) {
        super(items, distanceMeasure, linkage, maxMergeableDistance);
    }

    @Override
    protected double getDistanceBetweenClusterNodes(int nodeIndex1, int nodeIndex2) {
    	return getDistance(nodeIndex1, nodeIndex2, nodeDistances);
    }

    /**
     * This method is added to allow subclasses update distances
     * of cluster nodes which are somehow related to the nodes
     * which are merged while clustering.
     */
    protected void setDistanceBetweenClusterNodes(ClusterNode<T> node1, ClusterNode<T> node2, double distance) {
        int i = getClusterNodeIndex(node1);
        int j = getClusterNodeIndex(node2);
        setDistanceBetweenClusterNodes(i, j, distance);
    }

    private void setDistanceBetweenClusterNodes(int i, int j, double distance) {
        if (i == j) {
            throw new IllegalArgumentException();
        } else if (j < i) {
            nodeDistances[i][j] = distance;
        } else {
            nodeDistances[j][i] = distance;
        }
    }

    /**
     * Returns distance between items, not cluster nodes.
     */
    @Override
    public double distance(int itemIndex1, int itemIndex2) {
        return getDistance(itemIndex1, itemIndex2, itemDistances);
    }

    private final double getDistance(int i, int j, double[][] distances) {
        final double d;
        if (i == j) {
            d = 0;
        } else if (j < i) {
            d = distances[i][j];
        } else {
            d = distances[j][i];
        }
        return d;
    }

    public void calc(ProgressTracker progress) {
        List<T> items = getItems();

        final int numOfItems = items.size();
        progress.setSubtaskIncUnit(100.0 / numOfItems);

        // initialize the arrays
        itemDistances = new double[numOfItems][];
        nodeDistances = new double[numOfItems][];

        // precalculate the distances
        for (int i = 0; i < numOfItems; i++) {
            itemDistances[i] = new double[i];
            for (int j = 0; j < i; j++) {
            	itemDistances[i][j] = calcDistanceBetweenItems(i, j);
            }
            if (progress.isCancelled()) {
                return;
            }

            nodeDistances[i] = new double[i];
            System.arraycopy(itemDistances[i], 0, nodeDistances[i], 0, i);
            progress.incSubtaskProgress();
        }

    }

    @Override
    protected void updateDistances(int minI, int minJ, ClusterNode<T> newNode) {
        List<T> items = getItems();

        // update distances
        for (int i = 0, numOfItems = items.size(); i < numOfItems; i++) {
            if (i == minI) {
                continue;
            }
            ClusterNode<T> node = getNode(i);
            if (node != null) {
                setDistanceBetweenClusterNodes(
                        minI, minJ,
                        getLinkage().link(
                            newNode, node,
                            getDistanceBetweenClusterNodes(minJ, i),
                            getDistanceBetweenClusterNodes(minI, i),
                            this
                        )
                );
            }
        }
    }

}
