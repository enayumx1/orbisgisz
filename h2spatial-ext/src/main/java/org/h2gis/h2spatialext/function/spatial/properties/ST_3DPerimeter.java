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

package org.h2gis.h2spatialext.function.spatial.properties;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import org.h2gis.h2spatialapi.DeterministicScalarFunction;

/**
 * ST_3DPerimeter returns the 3D perimeter of a polygon or a multipolygon.
 * In the case of a 2D geometry, ST_3DPerimeter returns the same value as ST_Perimeter.
 * 
 * @author Erwan Bocher
 */
public class ST_3DPerimeter extends DeterministicScalarFunction{
   

    public ST_3DPerimeter() {
        addProperty(PROP_REMARKS, "Returns the 3D length measurement of the boundary of a Polygon or a MultiPolygon. \n"
                + "Distance units are those of the geometry spatial reference system.");
    }

    @Override
    public String getJavaStaticMethod() {
        return "st3Dperimeter";
    }
    
    /**
     * Compute the 3D perimeter of a polygon or a multipolygon.
     * @param geometry
     * @return 
     */
    public static Double st3Dperimeter(Geometry geometry){        
        if(geometry==null){
            return null;
        }
        if(geometry.getDimension()<2){
            return 0d;
        }
        return compute3DPerimeter(geometry);
    }
    
    /**
     * Compute the 3D perimeter
     * @param geometry
     * @return 
     */
    private static double compute3DPerimeter(Geometry geometry) {
        double sum = 0;
        for (int i = 0; i < geometry.getNumGeometries(); i++) {
            Geometry subGeom = geometry.getGeometryN(i);
            if (subGeom instanceof Polygon) {
                sum += ST_3DLength.length3D(((Polygon) subGeom).getExteriorRing());
            } 
        }
        return sum;
    }    
}
