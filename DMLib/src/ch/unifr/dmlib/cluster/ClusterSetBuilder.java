package ch.unifr.dmlib.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is intended for building clusters of elements
 * out of a cluster tree. The maxDistance parameter specifies
 * how the clusters must be split.
 *
 * @author Ilya Boyandin
 */
public class ClusterSetBuilder<T> {
    private final double maxDistance;
    private int lastCluster;
    private Map<T, Integer> clusterNums;

    public static <T> List<List<T>> getClusters(ClusterNode<T> root, double maxDistance) {
        ClusterSetBuilder<T> builder = new ClusterSetBuilder<T>(maxDistance);
        builder.traverse(root, 0);

        int numClusters = builder.lastCluster + 1;
        List<List<T>> clusters = new ArrayList<List<T>>(numClusters);
        for (int i = 0; i < numClusters; i++) {
            clusters.add(new ArrayList<T>());
        }
        for (Map.Entry<T, Integer> e : builder.clusterNums.entrySet()) {
            clusters.get(e.getValue()).add(e.getKey());
        }
        return clusters;
    }

    private ClusterSetBuilder(double maxDistance) {
        this.maxDistance = maxDistance;
        reset();
    }

    private void reset() {
        this.lastCluster = 0;
        this.clusterNums = new HashMap<T, Integer>();
    }

    private void traverse(ClusterNode<T> cn, int clusterNum) {
        if (cn.isLeafNode()) {
            clusterNums.put(cn.getItem(), clusterNum);
        } else {
            traverse(cn.getLeftChild(), clusterNum);
            if (cn.getDistance() > maxDistance) {
                clusterNum = ++lastCluster;
            }
            traverse(cn.getRightChild(), clusterNum);
        }
    }
}