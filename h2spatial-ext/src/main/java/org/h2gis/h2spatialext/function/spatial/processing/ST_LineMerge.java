/**
 * h2spatial is a library that brings spatial support to the H2 Java database.
 *
 * h2spatial is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
 * or contact directly: info_at_orbisgis.org
 */
package org.h2gis.h2spatialext.function.spatial.processing;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.operation.linemerge.LineMerger;
import org.h2gis.h2spatialapi.DeterministicScalarFunction;

import java.sql.SQLException;
import java.util.Collection;

/**
 * Merges a collection of linear components to form maximal-length linestrings.
 * @author Nicolas Fortin
 */
public class ST_LineMerge extends DeterministicScalarFunction {
    public ST_LineMerge() {
        addProperty(PROP_REMARKS, "Merges a collection of LineString elements in order to make create a new collection" +
                " of maximal-length linestrings. If you provide something else than (multi)linestrings it returns an" +
                " empty multilinestring");
    }

    @Override
    public String getJavaStaticMethod() {
        return "merge";
    }

    public static Geometry merge(Geometry geometry) throws SQLException {
        if(geometry == null) {
            return null;
        }
        if(geometry.getDimension() != 1) {
            return geometry.getFactory().createMultiLineString(new LineString[0]);
        }
        LineMerger lineMerger = new LineMerger();
        lineMerger.add(geometry);
        Collection coll = lineMerger.getMergedLineStrings();
        return geometry.getFactory().createMultiLineString((LineString[])coll.toArray(new LineString[coll.size()]));
    }
}
