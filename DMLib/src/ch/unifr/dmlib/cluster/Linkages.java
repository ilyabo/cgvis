package ch.unifr.dmlib.cluster;

/**
 * @author Ilya Boyandin
 */
public final class Linkages {
    
    private Linkages() {
    }

    // We cannot use an enum for the standard linkages,
    // because Linkage is a parameterized type 
    
    public static <T> Linkage<T> complete() {
        return new Linkage<T>() {
            public double link(
                    ClusterNode<T> mergedNode, ClusterNode<T> node,
                    double distanceToLeft, double distanceToRight,
                    DistanceMatrix<T> distances) {
                return Math.max(distanceToLeft, distanceToRight);
            }
            @Override
            public String toString() {
                return "Complete linkage";
            }
        };
    }
    
    public static <T> Linkage<T> single() {
        return new Linkage<T>() {
            public double link(
                    ClusterNode<T> mergedNode, ClusterNode<T> node,
                    double distanceToLeft, double distanceToRight,
                    DistanceMatrix<T> distances) {
                return Math.min(distanceToLeft, distanceToRight);
            }
            @Override
            public String toString() {
                return "Single linkage";
            }
        };
    }
    
    public static <T> Linkage<T> average() {
        return new Linkage<T>() {
            public double link(
                    ClusterNode<T> mergedNode, ClusterNode<T> node,
                    double distanceToLeft, double distanceToRight,
                    DistanceMatrix<T> distances) {
                double sum = 0;
                int count = 0;
                for (int i : mergedNode.getItemIndices()) {
                    for (int j : node.getItemIndices()) {
                        sum += distances.distance(i, j);
                        count++;
                    }
                }
                return sum / count;
            }
            @Override
            public String toString() {
                return "Average linkage";
            }
        };
    }

}
