/*
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.h2gis.drivers.geojson;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import org.h2gis.h2spatialapi.AbstractFunction;
import org.h2gis.h2spatialapi.EmptyProgressVisitor;
import org.h2gis.h2spatialapi.ScalarFunction;

/**
 * SQL function to read a geojson file an creates the corresponding spatial
 * table.
 *
 * @author Erwan Bocher
 */
public class GeojsonRead extends AbstractFunction implements ScalarFunction {

    public GeojsonRead() {
        addProperty(PROP_REMARKS, "Import a geojson 1.0 file.");
    }

    @Override
    public String getJavaStaticMethod() {
        return "readGeoJson";
    }

    /**
     * Read the geojson file.
     * 
     * @param connection
     * @param fileName
     * @param tableReference
     * @throws IOException
     * @throws SQLException 
     */
    public static void readGeoJson(Connection connection, String fileName, String tableReference) throws IOException, SQLException {
        GeoJsonDriverFunction gjdf = new GeoJsonDriverFunction();
        gjdf.importFile(connection, tableReference, new File(fileName), new EmptyProgressVisitor());
    }
}
