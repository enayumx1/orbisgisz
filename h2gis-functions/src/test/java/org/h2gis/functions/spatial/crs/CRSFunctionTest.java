/**
 * H2GIS is a library that brings spatial support to the H2 Database Engine
 * <http://www.h2database.com>. H2GIS is developed by CNRS
 * <http://www.cnrs.fr/>.
 *
 * This code is part of the H2GIS project. H2GIS is free software; 
 * you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * H2GIS is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details <http://www.gnu.org/licenses/>.
 *
 *
 * For more information, please consult: <http://www.h2gis.org/>
 * or contact directly: info_at_h2gis.org
 */

package org.h2gis.functions.spatial.crs;

import org.h2.jdbc.JdbcSQLException;
import org.h2gis.functions.factory.H2GISDBFactory;
import org.h2gis.utilities.SFSUtilities;
import org.junit.*;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.h2gis.unitTest.GeometryAsserts.assertGeometryBarelyEquals;
import static org.junit.Assert.*;

/**
 *
 * @author Erwan Bocher
 */
public class CRSFunctionTest {

    private static Connection connection;
    private static Statement st;
    private static final String DB_NAME = "CRSFunctionTest";

    @BeforeClass
    public static void tearUp() throws Exception {
        // Keep a connection alive to not close the DataBase on each unit test
        connection = SFSUtilities.wrapConnection(H2GISDBFactory.createSpatialDataBase(DB_NAME));
    }

    @Before
    public void setUpStatement() throws Exception {
        st = connection.createStatement();
    }

    @After
    public void tearDownStatement() throws Exception {
        st.close();
    }

        @AfterClass
    public static void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void test_ST_Transform27572To4326() throws Exception {
        checkProjectedGeom("POINT(584173.736059813 2594514.82833411)", 27572, 4326,
                "POINT(2.114551398096724 50.34560979151726)",10E-3);
    }

    @Test
    public void testST_Transform4326to2154() throws Exception {
        checkProjectedGeom("POINT(2.114551393 50.345609791)", 4326, 2154,
                "POINT(636890.7403226076 7027895.263553156 0)",10E-3);
    }

    @Test
    public void test_ST_Transform27572to3857() throws Exception {
        checkProjectedGeom("POINT(282331 2273699.7)", 27572, 3857,
                "POINT(-208496.53743537163 6005369.877027287)",10E-3);
    }

    @Test
    public void testST_Transform27572to2154WithoutNadgrid() throws Exception {
        checkProjectedGeom("POINT(282331 2273699.7)", 27572, 2154,
                "POINT(332602.9618934966 6709788.264478932)",10E-3);
    }

    @Test
    public void testST_Transform27572to2154WithNadgrid() throws Exception {
        //This test load a french grid to run the transformation
        final int outProj = 310024140;
        final ResultSet rs = compute("POINT(565767.906 2669005.730)", 320002120, outProj);
        // Java 6: "POINT(619119.4605077105 7102502.97947694)"
        // Java 7: "POINT(619119.4605077105 7102502.979476939)"
        checkWithTolerance(rs, "POINT(619119.4605077105 7102502.9794769)", outProj, 10E-3);
    }

    @Test
    public void testST_TransformAsIdentity() throws Exception {
        checkProjectedGeom("POINT(565767.906 2669005.730)", 2154, 2154,
                "POINT(565767.906 2669005.730)",10E-3);
    }

    @Test
    public void testST_TransformProjectThenProjectBack() throws Exception {
        final String inGeom = "MULTILINESTRING ((0 0, 1 0))";
        final int inOutProj = 4326;
        final ResultSet rs = st.executeQuery("SELECT ST_TRANSFORM(ST_TRANSFORM(" +
                "ST_GeomFromText('" + inGeom + "', " + inOutProj + "), 2154), " + inOutProj + ");");
        // The actual result is "MULTILINESTRING ((0 0 0, 0.9999999999999996 0 0))"
        checkWithTolerance(rs, "MULTILINESTRING ((0 0 0, 1 0 0))", inOutProj, 10E-3);
    }

    @Test
    public void testST_TransformOnMULTILINESTRING() throws Exception {
        checkProjectedGeom("MULTILINESTRING ((0 0, 1 0))", 4326, 4326,
                "MULTILINESTRING ((0 0, 1 0))",10E-3);
        final ResultSet rs = st.executeQuery("SELECT ST_TRANSFORM(" +
                "ST_GeomFromText('MULTILINESTRING ((2.11 50.34, 2.15 51))',  4326 ), 2154);");
        checkWithTolerance(rs, 
                "MULTILINESTRING ((636559.3165826919 7027274.112512174 0, 640202.1706468144 7100786.438815401 0))", 2154,10E-3 );
    }
    
    @Test
    public void testST_TransformOnMULTIPOINT() throws Exception {
        checkProjectedGeom("MULTIPOINT ((0 0), (1 0))", 4326, 4326,
                "MULTIPOINT ((0 0), (1 0))",10E-3);
        final ResultSet rs = st.executeQuery("SELECT ST_TRANSFORM("
                + "ST_GeomFromText('MULTIPOINT ((2.11 50.34), (2.11 50.34))',  4326 ), 2154);");
        checkWithTolerance(rs,
                "MULTIPOINT ((636559.3165826919 7027274.112512174 0), (636559.3165826919 7027274.112512174 0))", 2154, 10E-3);
    }
    
     @Test
    public void testST_TransformOnMULTIPOLYGON() throws Exception {
        final ResultSet rs = st.executeQuery("SELECT ST_TRANSFORM("
                + "ST_GeomFromText('MULTIPOLYGON (((2 40, 3 40, 3 3, 2 3, 2 40)))',  4326 ), 2154);");
        checkWithTolerance(rs,
                "MULTIPOLYGON (((614156.72100231 5877577.312128516 0, 700000 5877033.734723133 0, 700000 1336875.474634381 0, "
                        + "556660.5833028702 1337783.1294808295 0, 614156.72100231 5877577.312128516 0)))", 2154, 10E-3);
    }
    
    @Test
    public void testST_TransformOnNullGeometry() throws Exception {
        try (ResultSet rs = st.executeQuery("SELECT ST_TRANSFORM("
                + "null, 2154);")) {
            rs.next();
            Assert.assertNull(rs.getObject(1));
        }
    }
    
    @Test
    public void testST_TransformOnNulls() throws Exception {
        try (ResultSet rs = st.executeQuery("SELECT ST_TRANSFORM("
                + "null, null);")) {
            rs.next();
            Assert.assertNull(rs.getObject(1));
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testST_TransformOnNullSRID() throws Throwable {
        try {
            st.execute("SELECT ST_TRANSFORM("
                    + "ST_GeomFromText('MULTIPOLYGON (((2 40, 3 40, 3 3, 2 3, 2 40)))',  4326 ), null);");
        } catch (JdbcSQLException e) {
            throw e.getOriginalCause();
        }
    }

    @Test
    public void test_ST_Transform_envelope() throws Exception {
        st.execute("DROP TABLE IF EXISTS L93, L2E;");
        st.execute("CREATE TABLE L93(the_geom MULTIPOLYGON);");
        st.execute("INSERT INTO L93(THE_GEOM) VALUES (ST_MPOLYFROMTEXT('MULTIPOLYGON (((854602 6625825, 853779 6628650, 855453 6627756, 854602 6625825)))'));");
        st.execute("UPDATE L93 SET THE_GEOM = ST_SETSRID(THE_GEOM, 2154);");
        st.execute("CREATE TABLE L2E AS SELECT ST_TRANSFORM(THE_GEOM, 27582) as THE_GEOM FROM L93;");

        ResultSet rsL93 = st.executeQuery("select ST_Extent(THE_GEOM) EXTL93 from L93;");
        Assert.assertTrue(rsL93.next());
        Object resultObjL93 = rsL93.getObject("EXTL93");
        Assert.assertTrue(resultObjL93 instanceof Geometry);
        Envelope resultL93 = ((Geometry) resultObjL93).getEnvelopeInternal();

        ResultSet rsL2e = st.executeQuery("select ST_Extent(THE_GEOM) EXTL2E from L2E;");
        Assert.assertTrue(rsL2e.next());
        Object resultObjL2e = rsL2e.getObject("EXTL2E");
        Assert.assertTrue(resultObjL2e instanceof Geometry);
        Envelope resultL2e = ((Geometry) resultObjL2e).getEnvelopeInternal();

        assertNotEquals("Values should be different : "+resultL93.getMinX()+" and "+resultL2e.getMinX(), resultL93.getMinX(), resultL2e.getMinX(), 1);
        assertNotEquals("Values should be different : "+resultL93.getMaxX()+" and "+resultL2e.getMaxX(), resultL93.getMaxX(), resultL2e.getMaxX(), 1);
        assertNotEquals("Values should be different : "+resultL93.getMinY()+" and "+resultL2e.getMinY(), resultL93.getMinY(), resultL2e.getMinY(), 1);
        assertNotEquals("Values should be different : "+resultL93.getMaxY()+" and "+resultL2e.getMaxY(), resultL93.getMaxY(), resultL2e.getMaxY(), 1);

        st.execute("DROP TABLE IF EXISTS BASE_L93, BASE_L2E, BASE;");
    }
    
        
    private void checkProjectedGeom(String inputGeom, int inProj, int outProj, String expectedGeom,double  epsilon) throws SQLException {
        check(compute(inputGeom, inProj, outProj), expectedGeom, outProj, epsilon);
    }

    private ResultSet compute(String inputGeom, int inProj, int outProj) throws SQLException {
        return st.executeQuery("SELECT ST_TRANSFORM(" +
                "ST_GeomFromText('" + inputGeom + "', " + inProj + "), " + outProj + ");");
    }

    private void check(ResultSet rs, String expectedGeom, int outProj, double  epsilon) throws SQLException {
        try {
            assertTrue(rs.next());
            assertGeometryBarelyEquals(expectedGeom, outProj, rs.getObject(1), epsilon);
            assertFalse(rs.next());
        } finally {
            rs.close();
        }
    }

    private void checkWithTolerance(ResultSet rs, String inGeom, int inOutProj, double tolerance)
            throws SQLException {
        try {
            assertTrue(rs.next());
            assertGeometryBarelyEquals(inGeom, inOutProj, rs.getObject(1), tolerance);
            assertFalse(rs.next());
        } finally {
            rs.close();
        }
    }
}
