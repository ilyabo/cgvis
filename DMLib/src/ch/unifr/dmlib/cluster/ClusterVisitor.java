package ch.unifr.dmlib.cluster;

/**
 * @author Ilya Boyandin
 */
public interface ClusterVisitor<T> {

    void beforeChildren(ClusterNode<T> cn);

    void betweenChildren(ClusterNode<T> cn);

    void afterChildren(ClusterNode<T> cn);

    public class Adapter<T> implements ClusterVisitor<T> {

        public void afterChildren(ClusterNode<T> cn) {
        }

        public void betweenChildren(ClusterNode<T> cn) {
        }

        public void beforeChildren(ClusterNode<T> cn) {
        }

    }

}
