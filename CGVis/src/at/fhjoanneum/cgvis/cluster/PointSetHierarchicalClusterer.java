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
package at.fhjoanneum.cgvis.cluster;

import java.util.List;

import at.fhj.utils.misc.ProgressTracker;
import at.fhjoanneum.cgvis.data.IPointSet;
import at.fhjoanneum.cgvis.data.PointSetElemPerm;
import at.fhjoanneum.cgvis.data.PointSets;
import ch.unifr.dmlib.cluster.ClusterNode;
import ch.unifr.dmlib.cluster.ClusterVisitor;
import ch.unifr.dmlib.cluster.HierarchicalClusterer;
import ch.unifr.dmlib.cluster.Linkages;

/**
 * @author Ilya Boyandin
 */
public class PointSetHierarchicalClusterer {

    private IPointSet pointSet;
    private ClusterNode<List<Double>> rootCluster;

    public void cluster(IPointSet pointSet, ProgressTracker pt) {
        this.pointSet = pointSet;
        this.rootCluster = null;

        List<List<Double>> items = PointSets.asListOfElements(pointSet);

        this.rootCluster = HierarchicalClusterer
            .createWith(new EuclideanPointsetDistanceMeasure(pointSet), Linkages.<List<Double>>single())
            .build()
            .clusterToRoot(items, pt);
    }

    public ClusterNode<List<Double>> getRootCluster() {
        return rootCluster;
    }

    public int[] getElementPermutation() {
        final int[] perm = new int[pointSet.getSize()];
        ClusterNode.<List<Double>>traverseClusters(rootCluster, new ClusterVisitor.Adapter<List<Double>>() {
            int cnt = 0;

            @Override
            public void beforeChildren(ClusterNode<List<Double>> cn) {
                if (cn.isLeafNode()) {
                    perm[cnt++] = cn.getItemIndex();
                }
            }
        });
        return perm;
    }

    public IPointSet makeClusteredPointset() {
        return new PointSetElemPerm(pointSet, getElementPermutation());
    }

}
