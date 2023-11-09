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
 * or contact directly:
 * info_at_ orbisgis.org
 */

package org.h2gis.h2spatial.internal.function.spatial.properties;

import org.h2.util.StringUtils;
import org.h2gis.h2spatialapi.AbstractFunction;
import org.h2gis.h2spatialapi.ScalarFunction;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.TableLocation;
import org.omg.DynamicAny._DynEnumStub;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Get the column SRID from constraints and data.
 * @author Nicolas Fortin
 */
public class ColumnSRID extends AbstractFunction implements ScalarFunction {
    private static final String SRID_FUNC = ST_SRID.class.getSimpleName();
    private static final Pattern SRID_CONSTRAINT_PATTERN = Pattern.compile("ST_SRID\\s*\\(\\s*[\"`]?\\w*[\"`]?\\s*\\)\\s*=\\s*\\d+", Pattern.CASE_INSENSITIVE);
    private static final Pattern COLUMN_PATTERN = Pattern.compile("\\([\"`]?\\w+[\"`]?\\)");
    private static final Pattern SRID_PATTERN = Pattern.compile("\\d+$");
    private static final Pattern COLUMN_NAME_PATTERN = Pattern.compile("\\w+");
    public ColumnSRID() {
        addProperty(PROP_REMARKS, "Get the column SRID from constraints and data.");
    }

    @Override
    public String getJavaStaticMethod() {
        return "getSRID";
    }

    /**
     *
     * @param constraint Constraint expression ex:"ST_SRID(the_geom) = 27572"
     * @return The SRID or 0 if no constraint are found or constraint on other column
     */
    public static int getSRIDFromConstraint(String constraint, String columnName) {
        int srid = 0;
        Matcher matcher = SRID_CONSTRAINT_PATTERN.matcher(constraint);
        while (matcher.find()) {
            if(srid != 0) {
                return 0;
            }
            Matcher matcherColumn = COLUMN_PATTERN.matcher(matcher.group());
            if (matcherColumn.find()) {
                Matcher matcherColumnName = COLUMN_NAME_PATTERN.matcher(matcherColumn.group());
                if (matcherColumnName.find()) {
                    if(!matcherColumnName.group().equalsIgnoreCase(columnName)) {
                        return 0;
                    }
                }
            }
            Matcher matcherSrid = SRID_PATTERN.matcher(matcher.group());
            if (matcherSrid.find()) {
                srid = Integer.valueOf(matcherSrid.group());
            }
        }
        return srid;
    }

    /**
     * @param connection Active connection
     * @param tableName Target table name
     * @param columnName Spatial field name
     * @return Tthe column SRID from constraints and data.
     * @throws SQLException
     */
    public static int getSRID(Connection connection, String catalogName, String schemaName, String tableName, String columnName,String constraint) throws SQLException {
        Statement st = connection.createStatement();
        // Read constraint
        if(constraint != null && constraint.toUpperCase().contains(SRID_FUNC)) {
            // Check constraint
            // Extract column and SRID constraint value
            // constraint = ".. ST_SRID(the_geom) = 27572 .."
            int srid = getSRIDFromConstraint(constraint, columnName);
            if(srid > 0) {
                return srid;
            }
        }
        // Fetch the first geometry to find a stored SRID
        ResultSet rs = st.executeQuery(String.format("select ST_SRID(%s) from %s LIMIT 1;", StringUtils.quoteJavaString(columnName.toUpperCase()),new TableLocation(catalogName, schemaName, tableName)));
        if(rs.next()) {
            int srid = rs.getInt(1);
            if(srid > 0) {
                return srid;
            }
        }
        rs.close();
        // Unable to find a valid SRID
        return 0;

    }
}
