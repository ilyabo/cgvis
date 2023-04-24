package ch.unifr.dmlib.cluster;

import java.util.List;

import at.fhj.utils.misc.ProgressTracker;

/**
 * @author Ilya Boyandin
 */
public class HierarchicalClusterer<T> {

	private final DistanceMeasure<T> measure;
    private final Linkage<T> linkage;
    private final DistanceMatrix.Factory<T> distanceMatrixFactory;
    private final double maxMergeableDistance;
    private final double distanceMatrixCalcWeight;

    private HierarchicalClusterer(DistanceMeasure<T> measure, Linkage<T> linkage,
	        DistanceMatrix.Factory<T> distanceMatrixFactory, double maxMergeableDistance,
	        double distanceMatrixCalcWeight) {
		this.measure = measure;
		this.linkage = linkage;
		this.distanceMatrixFactory = distanceMatrixFactory;
		this.maxMergeableDistance = maxMergeableDistance;
		this.distanceMatrixCalcWeight = distanceMatrixCalcWeight;
	}

    public static <T> Builder<T> createWith(DistanceMeasure<T> measure, Linkage<T> linkage) {
        return new Builder<T>(measure, linkage);
    }

	public static class Builder<T> {
	    private final DistanceMeasure<T> measure;
        private final Linkage<T> linkage;
        private DistanceMatrix.Factory<T> distanceMatrixFactory = HierarchicalClusterer.<T>createDefaultDistanceMatrixFactory();
        private double maxMergeableDistance = Double.POSITIVE_INFINITY;
        private double distanceMatrixCalcWeight = .4;

        public Builder(DistanceMeasure<T> measure, Linkage<T> linkage) {
	        this.measure = measure;
	        this.linkage = linkage;
	    }

        public Builder<T> withDistanceMatrixFactory(
                DistanceMatrix.Factory<T> distanceMatrixFactory) {
            this.distanceMatrixFactory = distanceMatrixFactory;
            return this;
        }

        public Builder<T> withMaxMergeableDistance(double maxMergeableDistance) {
            this.maxMergeableDistance = maxMergeableDistance;
            return this;
        }

        public Builder<T> withDistanceMatrixCalcWeight(double distanceMatrixCalcWeight) {
            this.distanceMatrixCalcWeight = distanceMatrixCalcWeight;
            return this;
        }

        public HierarchicalClusterer<T> build() {
            return new HierarchicalClusterer<T>(measure, linkage, distanceMatrixFactory,
                    maxMergeableDistance, distanceMatrixCalcWeight);
        }
	}

	/**
	 * This method is public to allow the clients accessing the distance matrix
	 * before the clustering is performed.
	 *
	 * TODO: maybe better remove makeDistanceMatrix() and create a field and a getter for the matrix
	 */
    public DistanceMatrix<T> makeDistanceMatrix(List<T> items, ProgressTracker pt) {
        pt.startSubtask("Calculating distance matrix", distanceMatrixCalcWeight);
        DistanceMatrix<T> distances = distanceMatrixFactory.createFor(items, linkage, measure, maxMergeableDistance);
        distances.calc(pt);
        if (pt.isCancelled()) {
            return null;
        }
        pt.subtaskCompleted();
        return distances;
    }

    public ClusterNode<T> clusterToRoot(List<T> items, ProgressTracker pt) {
        return getRootClusterNode(cluster(items, pt));
    }

    /**
     * @param distances Distance matrix. NOTE: the matrix will be modified during clustering
     * @return The top level cluster nodes (normally only the root cluster node)
     */
    public ClusterNode<T> clusterToRoot(List<T> items, DistanceMatrix<T> distances, ProgressTracker pt) {
        return getRootClusterNode(cluster(items, distances, pt));
    }

    /**
     * @return The top level cluster nodes (if maxMergeableDistance is not set, then only the root cluster node)
     */
	public List<ClusterNode<T>> cluster(List<T> items, ProgressTracker pt) {
	    return cluster(items, makeDistanceMatrix(items, pt), pt);
	}

	/**
	 * @param distances Distance matrix. NOTE: the matrix will be modified during clustering
     * @return The top level cluster nodes (if maxMergeableDistance is not set, then only the root cluster node)
     */
    public List<ClusterNode<T>> cluster(List<T> items, DistanceMatrix<T> distances, ProgressTracker pt) {
        if (distances.getItems() != items) {
            throw new IllegalArgumentException();
        }
        pt.startSubtask("Clustering items", 1.0 - distanceMatrixCalcWeight);
        pt.setSubtaskIncUnit(100.0 / distances.getNumOfItems());
        ClusterNode<T> c = null;
        do {
            c = distances.mergeNearestNeighbours();
            if (pt.isCancelled()) {
                return null;
            }
            pt.incSubtaskProgress();
        } while (c != null);
        pt.subtaskCompleted();

        return distances.getTopLevelNodes();
    }

    private static <T> ClusterNode<T> getRootClusterNode(List<ClusterNode<T>> nodes) {
        if (nodes.size() != 1) {
            throw new IllegalStateException(
                    "Cannot get the root cluster node: There are " + nodes.size() + " top level cluster nodes. " +
                    "Check maxMergeableDistance");
        }
        return nodes.iterator().next();
    }

    private final static <T> DistanceMatrix.Factory<T> createDefaultDistanceMatrixFactory() {
        return new DistanceMatrix.Factory<T>() {
            public DistanceMatrix<T> createFor(List<T> items, Linkage<T> linkage,
                    DistanceMeasure<T> measure, double maxMergeableDistance) {
                return new DefaultDistanceMatrix<T>(items, measure, linkage, maxMergeableDistance);
            }
        };
    }
}
