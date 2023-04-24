package ch.unifr.dmlib.cluster;


/**
 * Linkage specifies how the distance between a newly added 
 * cluster node (created by merging two nodes) and the existing 
 * nodes should be calculated.
 * 
 * @author Ilya Boyandin
 */
public interface Linkage<T> {

    /**
     * @param mergedNode
     * @param node
     * @param distanceToLeft Distance from node to mergedNode's left child
     * @param distanceToRight Distance from node to mergedNode's right child
     */
    double link(
            ClusterNode<T> mergedNode, ClusterNode<T> node,
            double distanceToLeft, double distanceToRight,
            DistanceMatrix<T> distances);

}
