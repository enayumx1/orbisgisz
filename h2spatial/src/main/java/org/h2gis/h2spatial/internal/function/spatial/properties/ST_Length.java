/**
 * H2GIS is a library that brings spatial support to the H2 Database Engine
 * <http://www.h2database.com>.
 *
 * H2GIS is distributed under GPL 3 license. It is produced by CNRS
 * <http://www.cnrs.fr/>.
 *
 * H2GIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * H2GIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * H2GIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.h2gis.org/>
 * or contact directly: info_at_h2gis.org
 */
package org.h2gis.h2spatial.internal.function.spatial.properties;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import org.h2gis.h2spatialapi.DeterministicScalarFunction;

/**
 * Compute the geometry length.
 * @author Nicolas Fortin
 */
public class ST_Length extends DeterministicScalarFunction {

    /**
     * Default constructor
     */
    public ST_Length() {
        addProperty(PROP_REMARKS, "Returns the 2D length of the geometry if it is a LineString or MultiLineString.\n"
                + " 0 is returned for other geometries");
    }

    @Override
    public String getJavaStaticMethod() {
        return "getLength";
    }

    /**
     * @param geometry Geometry instance or 0
     * @return Geometry length for LineString or MultiLineString otherwise 0
     */
    public static Double getLength(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        if (geometry instanceof LineString || geometry instanceof MultiLineString) {
            return geometry.getLength();
        }
        return 0.0d;
    }
}
