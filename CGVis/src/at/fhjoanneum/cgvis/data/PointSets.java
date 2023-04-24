package at.fhjoanneum.cgvis.data;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ilya Boyandin
 */
public final class PointSets {
    
    private PointSets() {
    }
    
    public static List<List<Double>> _asListOfElements(IPointSet pointSet) {
        int numOfElements = pointSet.getSize();
        int dim = pointSet.getDimension();
        List<List<Double>> elements = new ArrayList<List<Double>>(numOfElements);
        for (int i = 0; i < numOfElements; i++) {
            List<Double> attrValues = new ArrayList<Double>(dim);
            for (int j = 0; j < dim; j++) {
                attrValues.add(pointSet.getValue(i, j));
            }
            elements.add(attrValues);
        }
        return elements;
    }
    
    public static List<List<Double>> asListOfElements(final IPointSet pointSet) {
        final int numOfElements = pointSet.getSize();
        final int dim = pointSet.getDimension();
        List<List<Double>> elements = new ArrayList<List<Double>>(numOfElements);
        for (int i = 0; i < numOfElements; i++) {
            final int elem = i;
            elements.add(new AbstractList<Double>() {
                @Override
                public Double get(int index) {
                    return pointSet.getValue(elem, index);
                }

                @Override
                public int size() {
                    return dim;
                }
            });
        }
        return elements;
    }

}
