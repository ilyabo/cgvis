/*
 * This file is part of CGVis.
 *
 * Copyright 2008 Ilya Boyandin, Erik Koerner
 * 
 * CGVis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CGVis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with CGVis.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.fhjoanneum.cgvis.data;

import java.util.ArrayList;
import java.util.List;

import at.fhjoanneum.cgvis.data.Query.ParamDef;
import at.fhjoanneum.cgvis.data.Query.QueryDef;
import ch.unifr.dmlib.cluster.ClusterNode;

/**
 * @author Ilya Boyandin
 */
public abstract class AbstractDataSource implements IDataSource {

    /** Number of point sets available. */
    public final static QueryDef GET_POINT_SET_NUM = new QueryDef(
            "pointSetNum", Integer.class);

    public final static QueryDef GET_POINT_SET_NAME = new QueryDef(
            "pointSetNum", new ParamDef[] { new ParamDef("pointSetIdx",
                    Integer.class) }, String.class);

    /** Obtain the full point set. */
    public final static QueryDef GET_POINT_SET = new QueryDef("pointSet",
            new ParamDef[] { new ParamDef("pointSetIdx", Integer.class) },
            IPointSet.class);

    /** Filter query. */
    public final static QueryDef GET_POINT_SUBSET = new QueryDef("pointSubset",
            new ParamDef[] { new ParamDef("pointSetIdx", Integer.class),
                    new ParamDef("elementIndices", Integer[].class) },
            IPointSet.class);

    /** Hierarchical clustering */
    public final static QueryDef GET_HCL_RESULTS = new QueryDef("hierCluster",
            new ParamDef[] { new ParamDef("pointSetIdx", Integer.class),
            // ...
            }, ClusterNode.class);

    /** Value frequencies. Used for a histogram (for an attribute). */
    public final static QueryDef GET_VALUE_FREQUENCIES = new QueryDef(
            "valueFreqs", new ParamDef[] { new ParamDef("pointSetIdx",
                    Integer.class),
            // ...
            }, ClusterNode.class);

    /** Correlations between attributes */
    public final static QueryDef GET_ATTR_CORRELATIONS = new QueryDef(
            "attrCorrel", new ParamDef[] { new ParamDef("pointSetIdx",
                    Integer.class),
            // ...
            }, ClusterNode.class);

    private List<IPointSet> pointSets;

    public AbstractDataSource() {
        pointSets = new ArrayList<IPointSet>();
    }

    public List<IPointSet> getPointSets() {
        return pointSets;
    }

    public void addPointSet(IPointSet pointSet) {
        pointSets.add(pointSet);
    }

    protected void checkResultType(Query query, Object result) {
        if (!(query.getDef().getReturnType()
                .isAssignableFrom(result.getClass()))) {
            throw new IllegalArgumentException("Invalid query result type");
        }
    }

    /**
     * Calls <code>executeQuery(Query)</code>, checks the returned result
     * type, and if it's ok, returns the result. Subclasses should override
     * <code>executeQuery(Query)</code> instead of this method.
     */
    public final Object query(Query query) {
        final Object result = executeQuery(query);
        checkResultType(query, result);
        return result;
    }

    protected Object executeQuery(Query query) {
        final Object[] params = query.getParameters();
        final String name = query.getDef().getName();

        final Object result;

        if (GET_POINT_SET_NUM.getName() == name) {
            result = pointSets.size();

        } else if (GET_POINT_SET_NAME.getName() == name) {
            result = pointSets.get((Integer) params[0]).getName();

        } else if (GET_POINT_SET.getName() == name) {
            result = pointSets.get((Integer) params[0]);

        } else if (GET_POINT_SUBSET.getName() == name) {
            result = executePointSubsetQuery(params);

        } else if (GET_HCL_RESULTS.getName() == name) {
            result = executeHCLQuery(params);

        } else if (GET_VALUE_FREQUENCIES.getName() == name) {
            result = executeValueFreqsQuery(params);

        } else if (GET_ATTR_CORRELATIONS.getName() == name) {
            result = executeAttrCorrelationsQuery(params);

        } else {
            throw new UnsupportedOperationException();
        }

        return result;
    }

    private Object executePointSubsetQuery(Object[] params) {
        // TODO: executePointSubsetQuery
        return null;
    }

    private Object executeHCLQuery(Object[] params) {
        // TODO: executeHCLQuery
        return null;
    }

    private Object executeValueFreqsQuery(Object[] params) {
        // TODO: executeValueFreqsQuery
        return null;
    }

    private Object executeAttrCorrelationsQuery(Object[] params) {
        // TODO: executeAttrCorrelationsQuery
        return null;
    }

    public void storeMetadata(int pointSetIndex, String key, Object data) {
        // TODO: store pointset metadata using XStream
    }

    public Object getMetadata(int pointSetIndex, String key) {
        // TODO: get metadata
        return null;
    }

    public void unload() {
        pointSets = null;
    }
}
