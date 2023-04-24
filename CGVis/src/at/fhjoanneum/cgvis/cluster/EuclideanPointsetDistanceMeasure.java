package at.fhjoanneum.cgvis.cluster;

import java.util.List;

import at.fhjoanneum.cgvis.data.IPointSet;
import at.fhjoanneum.cgvis.data.PointSetStats;
import at.fhjoanneum.cgvis.data.PointSetStats.MinMax;
import ch.unifr.dmlib.cluster.DistanceMeasure;

/**
 * @author Ilya Boyandin
 */
public class EuclideanPointsetDistanceMeasure implements DistanceMeasure<List<Double>> {

    private final MinMax stats;

    public EuclideanPointsetDistanceMeasure(IPointSet pointSet) {
        this.stats = PointSetStats.getStats(pointSet);
    }

    public double distance(List<Double> a, List<Double> b) {
        assert(a.size() == b.size());
        int dimension = (a.size());
        double sum = 0;
        for (int k = 0; k < dimension; k++) {
            double v1 = a.get(k);
            double v2 = b.get(k);

            if (Double.isNaN(v1)) {
                if (Double.isNaN(v2)) {
                    v1 = v2 = stats.max;
                } else {
                    v1 = Math.max(v2, stats.max - v2);  // this is probably wrong: it should be the
                                                        // value of the distance, not v1
                }
            } else if (Double.isNaN(v2)) {
                v2 = Math.max(v1, stats.max - v1);
            }
            final double d = (v1 - v2);
            sum += d * d;
        }
        return Math.sqrt(sum);
    }

}
