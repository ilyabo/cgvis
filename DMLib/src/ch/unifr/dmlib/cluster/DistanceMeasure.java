package ch.unifr.dmlib.cluster;

/**
 * @author Ilya Boyandin
 */
public interface DistanceMeasure<T> {

    double distance(T t1, T t2);
    
}
