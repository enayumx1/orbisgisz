package org.orbisgis.sputilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @author Nicolas Fortin
 */
public class JDBCUtilities {
    private JDBCUtilities() {}

    private static ResultSet getTablesView(Connection connection, String catalog, String schema, String table) throws SQLException {
        Integer catalogIndex = null;
        Integer schemaIndex = null;
        Integer tableIndex = 1;
        StringBuilder sb = new StringBuilder("SELECT * from INFORMATION_SCHEMA.TABLES where ");
        if(!catalog.isEmpty()) {
            sb.append("UPPER(table_catalog) = ? AND ");
            catalogIndex = 1;
            tableIndex++;
        }
        if(!schema.isEmpty()) {
            sb.append("UPPER(table_schema) = ? AND ");
            schemaIndex = tableIndex;
            tableIndex++;
        }
        sb.append("UPPER(table_name) = ? ");
        PreparedStatement geomStatement = connection.prepareStatement(sb.toString());
        if(catalogIndex!=null) {
            geomStatement.setString(catalogIndex,catalog.toUpperCase());
        }
        if(schemaIndex!=null) {
            geomStatement.setString(schemaIndex,schema.toUpperCase());
        }
        geomStatement.setString(tableIndex,table.toUpperCase());
        return geomStatement.executeQuery();
    }
    private static boolean hasField(ResultSetMetaData resultSetMetaData, String fieldName) throws SQLException {
        for(int columnId = 1; columnId <= resultSetMetaData.getColumnCount(); columnId++) {
            if(fieldName.equalsIgnoreCase(resultSetMetaData.getColumnName(columnId))) {
                return true;
            }
        }
        return false;
    }
    /**
     * Read INFORMATION_SCHEMA.TABLES in order to see if the provided table reference is a temporary table.
     * @param connection Active connection not closed by this method
     * @param location Table reference
     * @return True if the provided table is temporary.
     * @throws SQLException If the table does not exists.
     */
    public static boolean isTemporaryTable(Connection connection, TableLocation location) throws SQLException {
        ResultSet rs = getTablesView(connection, location.getCatalog(), location.getSchema(), location.getTable());
        boolean isTemporary = false;
        try {
            if(rs.next()) {
                String tableType;
                if(hasField(rs.getMetaData(), "STORAGE_TYPE")) {
                    // H2
                    tableType = rs.getString("STORAGE_TYPE");
                } else {
                    // Standard SQL
                    tableType = rs.getString("TABLE_TYPE");
                }
                isTemporary = tableType.contains("TEMPORARY");
            } else {
                throw new SQLException("The table "+location+" does not exists");
            }
        } finally {
            rs.close();
        }
        return isTemporary;
    }
}
