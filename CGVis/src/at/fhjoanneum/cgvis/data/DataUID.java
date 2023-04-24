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

import java.util.UUID;

/**
 * A unique id for data elements/attributes/datasources.
 * 
 * @author Ilya Boyandin
 */
public class DataUID {

    private UUID id;

    private DataUID() {
        this.id = UUID.randomUUID();
    }

    private DataUID(UUID id) {
        this.id = id;
    }

    public static DataUID createUID() {
        return new DataUID();
    }

    public static DataUID[] createArrayOfUIDs(final int num) {
        final DataUID[] uids = new DataUID[num];
        for (int i = 0; i < num; i++) {
            uids[i] = new DataUID();
        }
        return uids;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DataUID))
            return false;
        if (obj == this)
            return true;
        return id.equals(((DataUID) obj).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id.toString();
    }

    public static DataUID fromString(String str) {
        return new DataUID(UUID.fromString(str));
    }
}
