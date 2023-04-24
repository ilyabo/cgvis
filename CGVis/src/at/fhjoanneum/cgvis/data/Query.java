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

/**
 * @author Ilya Boyandin
 */
public class Query {

    public static class QueryDef {
        private ParamDef[] paramTypes;
        private Class<?> returnType;
        private String name;

        public QueryDef(String name, Class<?> returnType) {
            // query has no parameters
            this(name, null, returnType);
        }

        public QueryDef(String name, ParamDef[] params, Class<?> returnType) {
            this.name = name.intern();
            this.paramTypes = params;
            this.returnType = returnType;
        }

        public String getName() {
            return name;
        }

        public ParamDef[] getParamDefs() {
            return paramTypes;
        }

        public Class<?> getReturnType() {
            return returnType;
        }

        public boolean equals(Object obj) {
            return name.equals(((QueryDef) obj).getName());
        }

        public int hashCode() {
            return name.hashCode();
        }
    };

    private QueryDef def;
    private Object[] parameters;

    public Query(QueryDef def, Object... parameters) {
        checkParameters(def, parameters);
        this.def = def;
        this.parameters = parameters;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public QueryDef getDef() {
        return def;
    }

    private void checkParameters(QueryDef def, Object[] params) {
        final ParamDef[] defs = def.getParamDefs();
        if (defs == null || defs.length == 0) {
            if (params != null && params.length > 0) {
                throw new IllegalArgumentException(
                        "Incorrect number of parameters");
            }
        } else {
            if (params == null || params.length != defs.length) {
                throw new IllegalArgumentException(
                        "Incorrect number of parameters");
            }
            for (int i = 0, n = params.length; i < n; i++) {
                if (defs[i].getType() != params[i].getClass()) {
                    throw new IllegalArgumentException("Parameter \""
                            + defs[i].getName() + "\" must be "
                            + defs[i].getType());
                }
            }
        }
    }

    public static class ParamDef {
        private final String name;
        private final Class<?> type;

        public ParamDef(final String name, final Class<?> type) {
            this.name = name;
            this.type = type;
        }

        public Class<?> getType() {
            return type;
        }

        public String getName() {
            return name;
        }
    }
}
