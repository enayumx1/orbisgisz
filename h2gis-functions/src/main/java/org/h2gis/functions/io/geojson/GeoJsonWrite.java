/**
 * H2GIS is a library that brings spatial support to the H2 Database Engine
 * <http://www.h2database.com>. H2GIS is developed by CNRS
 * <http://www.cnrs.fr/>.
 *
 * This code is part of the H2GIS project. H2GIS is free software; you can
 * redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; version 3.0 of
 * the License.
 *
 * H2GIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details <http://www.gnu.org/licenses/>.
 *
 *
 * For more information, please consult: <http://www.h2gis.org/>
 * or contact directly: info_at_h2gis.org
 */
package org.h2gis.functions.io.geojson;

import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;
import org.h2gis.api.AbstractFunction;
import org.h2gis.api.EmptyProgressVisitor;
import org.h2gis.api.ScalarFunction;
import org.h2gis.utilities.URIUtilities;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * SQL function to write a spatial table to a GeoJSON file.
 *
 * @author Erwan Bocher
 */
public class GeoJsonWrite extends AbstractFunction implements ScalarFunction {

    public GeoJsonWrite() {
        addProperty(PROP_REMARKS, "Export a spatial table to a GeoJSON 1.0 file.\n "
                + "\nGeoJsonWrite(..."
                + "\n Supported arguments :"
                + "\n path of the file, table name"
                + "\n path of the file, table name, true to delete the file if exists"
                + "\n path of the file, table name, encoding chartset"
                + "\n path of the file, table name, encoding chartset, true to delete the file if exists");
    }

    @Override
    public String getJavaStaticMethod() {
        return "exportTable";
    }

    /**
     * Read a table and write it into a GEOJSON file.
     *
     * @param connection Active connection
     * @param fileName Shape file name or URI
     * @param tableReference Table name or select query Note : The select query
     * must be enclosed in parenthesis
     * @param encoding charset encoding
     * @param deleteFile true to delete output file
     * @throws IOException
     * @throws SQLException
     */
    public static void exportTable(Connection connection, String fileName, String tableReference, String encoding, boolean deleteFile) throws IOException, SQLException {
        GeoJsonDriverFunction geoJsonDriver = new GeoJsonDriverFunction();
        geoJsonDriver.exportTable(connection, tableReference, URIUtilities.fileFromString(fileName), encoding, deleteFile, new EmptyProgressVisitor());
    }

    /**
     * Write the GeoJSON file.
     *
     * @param connection
     * @param fileName
     * @param tableReference
     * @throws IOException
     * @throws SQLException
     */
    public static void exportTable(Connection connection, String fileName, String tableReference) throws IOException, SQLException {
        exportTable(connection, fileName, tableReference, null, false);
    }

    /**
     * Read a table and write it into a geojson file.
     *
     * @param connection Active connection
     * @param fileName Shape file name or URI
     * @param tableReference Table name or select query Note : The select query
     * must be enclosed in parenthesis
     * @param option Could be string file encoding charset or boolean value to
     * delete the existing file
     * @throws IOException
     * @throws SQLException
     */
    public static void exportTable(Connection connection, String fileName, String tableReference, Value option) throws IOException, SQLException {
        String encoding = null;
        boolean deleteFiles = false;
        if (option instanceof ValueBoolean) {
            deleteFiles = option.getBoolean();
        } else if (option instanceof ValueVarchar) {
            encoding = option.getString();
        } else if (!(option instanceof ValueNull)) {
            throw new SQLException("Supported optional parameter is boolean or varchar");
        }
        exportTable(connection, fileName, tableReference, encoding, deleteFiles);
    }
}
