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
package org.h2gis.h2spatialext.function.spatial.create;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import org.h2gis.h2spatialapi.DeterministicScalarFunction;

/**
 * Creates a rectangular POLYGON formed from the given x and y minima.  The user may specify an SRID; if no SRID is specified the unknown spatial reference system is assumed.
 *
 * @author Erwan Bocher
 */
public class ST_MakeEnvelope extends DeterministicScalarFunction {

    private static final GeometryFactory GF = new GeometryFactory();

    public ST_MakeEnvelope() {
        addProperty(PROP_REMARKS,
                "Creates a rectangular POLYGON formed from the given x and y minima.\n"
                + " The user may specify an SRID; if no SRID is specified the unknown\n"
                + " spatial reference system is assumed.");
    }

    @Override
    public String getJavaStaticMethod() {
        return "makeEnvelope";
    }

    /**
     * Creates a rectangular Polygon formed from the minima and maxima by the
     * given shell.
     *
     * @param xmin X min
     * @param ymin Y min
     * @param xmax X max
     * @param ymax Y max
     * @return Envelope as a POLYGON
     */
    public static Polygon makeEnvelope(double xmin, double ymin, double xmax, double ymax) {
        Coordinate[] coordinates = new Coordinate[]{
            new Coordinate(xmin, ymin),
            new Coordinate(xmax, ymin),
            new Coordinate(xmax, ymax),
            new Coordinate(xmin, ymax),
            new Coordinate(xmin, ymin)
        };
        return GF.createPolygon(GF.createLinearRing(coordinates), null);
    }

    /**
     * Creates a rectangular Polygon formed from the minima and maxima by the
     * given shell.
     * The user can set a srid.
     * @param xmin X min
     * @param ymin Y min
     * @param xmax X max
     * @param ymax Y max
     * @param srid SRID
     * @return Envelope as a POLYGON
     */
    public static Polygon makeEnvelope(double xmin, double ymin, double xmax, double ymax, int srid) {
        Polygon geom = makeEnvelope(xmin, ymin, xmax, ymax);
        geom.setSRID(srid);
        return geom;
    }
}
