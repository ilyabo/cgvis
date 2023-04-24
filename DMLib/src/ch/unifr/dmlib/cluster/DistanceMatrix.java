package ch.unifr.dmlib.cluster;

import java.util.List;

import at.fhj.utils.misc.ProgressTracker;

/**
 * Distances between nodes at the beginning and between clusters
 * of nodes during clustering.
 */
public interface DistanceMatrix<T> {

    int getNumOfItems();

    List<T> getItems();

    List<ClusterNode<T>> getTopLevelNodes();

    double distance(int itemIndex1, int itemIndex2);

    void calc(ProgressTracker progress);

    ClusterNode<T> mergeNearestNeighbours();

    public interface Factory<T> {

        DistanceMatrix<T> createFor(
                List<T> items, Linkage<T> linkage,
                DistanceMeasure<T> measure, double maxMergeableDistance);

    }

}
