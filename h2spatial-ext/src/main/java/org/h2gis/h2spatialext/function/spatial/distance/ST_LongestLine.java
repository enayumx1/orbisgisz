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

package org.h2gis.h2spatialext.function.spatial.distance;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import org.h2gis.h2spatialapi.DeterministicScalarFunction;

/**
 *
 * @author Erwan Bocher
 */
public class ST_LongestLine extends DeterministicScalarFunction{

    public ST_LongestLine(){
        addProperty(PROP_REMARKS, "Returns the 2-dimensional longest line between the points of two geometries."
                + "If the geometry 1 and geometry 2 is the same geometry the function will \n "
                + "return the longest line between the two vertices most far from each other in that geometry.");
    }
    
    @Override
    public String getJavaStaticMethod() {
        return "longestLine";
    }
    
    /**
     * Return the longest line between the points of two geometries.
     * @param geomA
     * @param geomB
     * @return 
     */
    public static Geometry longestLine(Geometry geomA, Geometry geomB) {
        Coordinate[] coords = new MaxDistanceOp(geomA, geomB).getCoordinatesDistance();
        if(coords!=null){
            return geomA.getFactory().createLineString(coords);
        }
        return null;
    }
}
