/**
 * h2spatial is a library that brings spatial support to the H2 Java database.
 *
 * h2spatial is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
 *
 * h2patial is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * h2spatial is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * h2spatial. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */

package org.h2gis.h2spatial.internal.function.spatial.properties;

import com.vividsolutions.jts.geom.Geometry;
import org.h2gis.h2spatialapi.DeterministicScalarFunction;

import java.sql.SQLException;

/**
 * Return Geometry number n from the given GeometryCollection. Use {@link
 * org.h2gis.h2spatial.internal.function.spatial.properties.ST_NumGeometries}
 * to retrieve the total number of Geometries.
 *
 * @author Nicolas Fortin
 * @author Adam Gouge
 */
public class ST_GeometryN extends DeterministicScalarFunction {
    private static final String OUT_OF_BOUNDS_ERR_MESSAGE =
            "Geometry index out of range. Must be between 1 and ST_NumGeometries.";

    /**
     * Default constructor
     */
    public ST_GeometryN() {
        addProperty(PROP_REMARKS, "Returns Geometry number n from a GeometryCollection. " +
                "Use ST_NumGeometries to retrieve the total number of Geometries.");
    }

    @Override
    public String getJavaStaticMethod() {
        return "getGeometryN";
    }

    /**
     * Return Geometry number n from the given GeometryCollection.
     *
     * @param geometry GeometryCollection
     * @param n        Index of Geometry number n in [1-N]
     * @return Geometry number n or Null if parameter is null.
     */
    public static Geometry getGeometryN(Geometry geometry, Integer n) throws SQLException {
        if (geometry == null) {
            return null;
        }
        if (n >= 1 && n <= geometry.getNumGeometries()) {
            return geometry.getGeometryN(n - 1);
        } else {
            throw new SQLException(OUT_OF_BOUNDS_ERR_MESSAGE);
        }
    }
}
