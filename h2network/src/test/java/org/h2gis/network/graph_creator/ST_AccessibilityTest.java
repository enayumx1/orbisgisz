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

package org.h2gis.network.graph_creator;

import org.h2gis.h2spatial.CreateSpatialExtension;
import org.h2gis.h2spatial.ut.SpatialH2UT;
import org.h2gis.utilities.GraphConstants;
import org.junit.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.h2gis.utilities.GraphConstants.CLOSEST_DEST;
import static org.h2gis.utilities.GraphConstants.SOURCE;
import static org.junit.Assert.assertEquals;

/**
 * Created by adam on 4/22/14.
 */
public class ST_AccessibilityTest {

    private static Connection connection;
    private Statement st;
    private static final double TOLERANCE = 0.0;
    private static final String DO = "'directed - edge_orientation'";
    private static final String RO = "'reversed - edge_orientation'";
    private static final String U = "'undirected'";
    private static final String W = "'weight'";
    private static final String DEST_TABLE = "'dest_table'";

    @BeforeClass
    public static void setUp() throws Exception {
        // Keep a connection alive to not close the DataBase on each unit test
        connection = SpatialH2UT.createSpatialDataBase("ST_AccessibilityTest", true);
        CreateSpatialExtension.registerFunction(connection.createStatement(), new ST_Accessibility(), "");
        GraphCreatorTest.registerCormenGraph(connection);
    }

    @Before
    public void setUpStatement() throws Exception {
        st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }

    @After
    public void tearDownStatement() throws Exception {
        st.close();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        connection.close();
    }

    // ************************** All-to-Several ****************************************

    @Test
    public void allToSeveralDO() throws Exception {
        // SELECT * FROM ST_Accessibility('cormen_edges_all',
        //     'directed - edge_orientation', '1, 5')
        check(allToSeveral(DO, "'1, 5'"), new int[]{1, 5, 5, 5, 5}, new double[]{0.0, 2.0, 1.0, 1.0, 0.0});
        // SELECT * FROM ST_Accessibility('cormen_edges_all',
        //     'directed - edge_orientation', '2, 3, 4')
        final ResultSet rs234 = allToSeveral(DO, "'2, 3, 4'");
        final double[] dist234 = new double[]{1.0, 0.0, 0.0, 0.0, 1.0};
        // d(1,2)=d(1,3)=1.0.
        try {
            check(rs234, new int[]{2, 2, 3, 4, 4}, dist234);
        } catch (AssertionError e) {
            rs234.beforeFirst();
            check(rs234, new int[]{3, 2, 3, 4, 4}, dist234);
        }
    }

    @Test
    public void allToSeveralWDO() throws Exception {
        // SELECT * FROM ST_Accessibility('cormen_edges_all',
        //     'directed - edge_orientation', 'weight', '1, 5')
        check(allToSeveral(DO, W, "'1, 5'"), new int[]{1, 5, 5, 5, 5}, new double[]{0.0, 4.0, 2.0, 4.0, 0.0});
        // SELECT * FROM ST_Accessibility('cormen_edges_all',
        //     'directed - edge_orientation', 'weight', '2, 3, 4')
        check(allToSeveral(DO, W, "'2, 3, 4'"), new int[]{3, 2, 3, 4, 4}, new double[]{5.0, 0.0, 0.0, 0.0, 6.0});
    }

    @Test
    public void allToSeveralRO() throws Exception {
        // SELECT * FROM ST_Accessibility('cormen_edges_all',
        //     'reversed - edge_orientation', '1, 5')
        check(allToSeveral(RO, "'1, 5'"), new int[]{1, 1, 1, 5, 5}, new double[]{0.0, 1.0, 1.0, 1.0, 0.0});
        // SELECT * FROM ST_Accessibility('cormen_edges_all',
        //     'reversed - edge_orientation', '2, 3, 4')
        final ResultSet rs234 = allToSeveral(RO, "'2, 3, 4'");
        final double[] dist234 = new double[]{2.0, 0.0, 0.0, 0.0, 1.0};
        // d(1,3)=d(1,4)=2.0, d(5,3)=d(5,4)=1.0.
        try {
            check(rs234, new int[]{3, 2, 3, 4, 3}, dist234);
        } catch (AssertionError e) {
            rs234.beforeFirst();
            try {
                check(rs234, new int[]{3, 2, 3, 4, 4}, dist234);
            } catch (AssertionError e1) {
                rs234.beforeFirst();
                try {
                    check(rs234, new int[]{4, 2, 3, 4, 3}, dist234);
                } catch (AssertionError e2) {
                    rs234.beforeFirst();
                    check(rs234, new int[]{4, 2, 3, 4, 4}, dist234);
                }
            }
        }
    }

    @Test
    public void allToSeveralWRO() throws Exception {
        // SELECT * FROM ST_Accessibility('cormen_edges_all',
        //     'reversed - edge_orientation', 'weight', '1, 5')
        check(allToSeveral(RO, W, "'1, 5'"), new int[]{1, 5, 1, 5, 5}, new double[]{0.0, 7.0, 5.0, 6.0, 0.0});
        // SELECT * FROM ST_Accessibility('cormen_edges_all',
        //     'reversed - edge_orientation', 'weight', '2, 3, 4')
        check(allToSeveral(RO, W, "'2, 3, 4'"), new int[]{3, 2, 3, 4, 3}, new double[]{9.0, 0.0, 0.0, 0.0, 2.0});
    }

    @Test
    public void allToSeveralU() throws Exception {
        // SELECT * FROM ST_Accessibility('cormen_edges_all',
        //     'undirected', '1, 5')
        final ResultSet rs15 = allToSeveral(U, "'1, 5'");
        final double[] dist15 = new double[]{0.0, 1.0, 1.0, 1.0, 0.0};
        // d(3,1)=d(3,5)=1.0.
        try {
            check(rs15, new int[]{1, 1, 1, 5, 5}, dist15);
        } catch (AssertionError e) {
            rs15.beforeFirst();
            check(rs15, new int[]{1, 1, 5, 5, 5}, dist15);
        }
        // SELECT * FROM ST_Accessibility('cormen_edges_all',
        //     'undirected', '2, 3, 4')
        final ResultSet rs234 = allToSeveral(U, "'2, 3, 4'");
        final double[] dist234 = new double[]{1.0, 0.0, 0.0, 0.0, 1.0};
        // d(1,2)=d(1,3)=1.0, d(5,3)=d(5,4)=1.0.
        try {
            check(rs234, new int[]{2, 2, 3, 4, 3}, dist234);
        } catch (AssertionError e) {
            rs234.beforeFirst();
            try {
                check(rs234, new int[]{2, 2, 3, 4, 4}, dist234);
            } catch (AssertionError e1) {
                rs234.beforeFirst();
                try {
                    check(rs234, new int[]{3, 2, 3, 4, 3}, dist234);
                } catch (AssertionError e2) {
                    rs234.beforeFirst();
                    check(rs234, new int[]{3, 2, 3, 4, 4}, dist234);
                }
            }
        }
    }

    @Test
    public void allToSeveralWU() throws Exception {
        // SELECT * FROM ST_Accessibility('cormen_edges_all',
        //     'undirected', 'weight', '1, 5')
        check(allToSeveral(U, W, "'1, 5'"), new int[]{1, 5, 5, 5, 5}, new double[]{0.0, 4.0, 2.0, 4.0, 0.0});
        // SELECT * FROM ST_Accessibility('cormen_edges_all',
        //     'undirected', 'weight', '2, 3, 4')
        check(allToSeveral(U, W, "'2, 3, 4'"), new int[]{3, 2, 3, 4, 3}, new double[]{5.0, 0.0, 0.0, 0.0, 2.0});
    }

    private ResultSet allToSeveral(String orientation, String weight, String destinationString) throws SQLException {
        return st.executeQuery(
                "SELECT * FROM ST_Accessibility('cormen_edges_all', "
                        + orientation + ((weight != null) ? ", " + weight : "")
                        + ", " + destinationString + ")");
    }

    private ResultSet allToSeveral(String orientation, String destinationString) throws SQLException {
        return allToSeveral(orientation, null, destinationString);
    }

    private void check(ResultSet rs, int[] closestDests, double[] distances) throws SQLException {
        int count = 0;
        while (rs.next()) {
            final int returnedSource = rs.getInt(SOURCE);
            final int closestDestination = rs.getInt(CLOSEST_DEST);
            assertEquals(closestDests[returnedSource - 1], closestDestination);
            final double distance = rs.getDouble(GraphConstants.DISTANCE);
            assertEquals(distances[returnedSource - 1], distance, TOLERANCE);
            count++;
        }
        assertEquals(distances.length, count);
        rs.close();
    }
}
