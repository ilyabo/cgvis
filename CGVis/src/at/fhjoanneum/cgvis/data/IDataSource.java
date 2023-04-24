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

import at.fhj.utils.misc.ProgressTracker;

/**
 * Provides access to a dataset. Data can be read, for instance, from a text
 * file, from an XML file, from a database, or obtained from a remote web
 * service.
 * 
 * @author Ilya Boyandin
 */
public interface IDataSource {

    void init(ProgressTracker progress) throws DataSourceException;

    Object query(Query query);

    void storeMetadata(int pointSetIdx, String key, Object data);

    Object getMetadata(int pointSetIdx, String key);

    /**
     * Unload the dataset and free all occupied resources
     */
    void unload();

    DataUID getDataSourceId();

}
