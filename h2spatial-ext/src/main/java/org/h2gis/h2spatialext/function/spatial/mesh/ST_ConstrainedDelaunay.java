/**
 * h2spatial is a library that brings spatial support to the H2 Java database.
 *
 * h2spatial is distributed under GPL 3 license. It is produced by the "Atelier
 * SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
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
 * or contact directly: info_at_ orbisgis.org
 */
package org.h2gis.h2spatialext.function.spatial.mesh;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import java.sql.SQLException;
import java.util.ArrayList;
import org.h2gis.h2spatialapi.DeterministicScalarFunction;
import static org.h2gis.h2spatialapi.Function.PROP_REMARKS;
import org.jdelaunay.delaunay.ConstrainedMesh;
import org.jdelaunay.delaunay.error.DelaunayError;
import org.jdelaunay.delaunay.evaluator.TriangleQuality;
import org.jdelaunay.delaunay.geometries.DEdge;

/**
 * Returns polygons or lines that represent a Delaunay triangulation constructed
 * from a geometry. Note that the triangulation computes
 * the intersections between lines.
 *
 * @author Erwan Bocher
 */
public class ST_ConstrainedDelaunay extends DeterministicScalarFunction {

 
    public ST_ConstrainedDelaunay() {
        addProperty(PROP_REMARKS, "Returns polygons that represent a Constrained Delaunay Triangulation from a geometry.\n"
                + "Output is a COLLECTION of polygons, for flag=0 (default flag) or a MULTILINESTRING for flag=1.\n"
                + "If the input geometry does not contain any lines, a delaunay triangulation will be computed."
                + "The last argument can be set to improve the quality of the triangulation. The value must be comprised"
                + " between 0 and 1.\n "
                + "If value > 0.6 the triangle is of acceptable quality.\n");
    }

    @Override
    public String getJavaStaticMethod() {
        return "createCDT";
    }

    /**
     * Build a constrained delaunay triangulation based on a geometry
     * (point, line, polygon)
     *
     * @param geometry
     * @return a set of polygons (triangles)
     * @throws SQLException,DelaunayError
     * @throws org.jdelaunay.delaunay.error.DelaunayError
     */
    public static GeometryCollection createCDT(Geometry geometry) throws SQLException, DelaunayError {
        return createCDT(geometry, 0, -1);
    }
    
    /**
     * Build a constrained delaunay triangulation based on a geometry
     * (point, line, polygon)
     *
     * @param geometry
     * @param flag
     * @return a set of polygons (triangles)
     * @throws SQLException, DelaunayError
     * @throws org.jdelaunay.delaunay.error.DelaunayError
     */
    public static GeometryCollection createCDT(Geometry geometry, int flag) throws SQLException, DelaunayError {
        return createCDT(geometry,  flag, -1);
    }

    /**
     * Build a delaunay constrained delaunay triangulation based on a
     * geometry (point, line, polygon)
     *
     * @param geometry
     * @param flag
     * @param qualityRefinement
     * @return Output is a COLLECTION of polygons (for flag=0) or a MULTILINESTRING (for flag=1)
     * @throws SQLException, DelaunayError
     * @throws org.jdelaunay.delaunay.error.DelaunayError
     */
    public static GeometryCollection createCDT(Geometry geometry, int flag, double qualityRefinement) throws SQLException, DelaunayError {
        if (geometry != null) {
            if (flag == 0) {
                return DelaunayTools.toMultiPolygon(buildDelaunay(geometry, qualityRefinement).getTriangleList());
            } else if (flag == 1) {
                return DelaunayTools.toMultiLineString(buildDelaunay(geometry, qualityRefinement).getEdges());
            } else {
                throw new SQLException("Only flag 0 or 1 is supported.");
            }
        }
        return null;
    }

    /**
     * Compute a constrained delaunay triangulation
     * @param geometry
     * @return
     * @throws DelaunayError 
     */
    private static ConstrainedMesh buildDelaunay(Geometry geometry, double qualityRefinement) throws DelaunayError, SQLException {
        ConstrainedMesh mesh = new ConstrainedMesh();
        mesh.setVerbose(true);
        DelaunayData delaunayData = new DelaunayData();
        delaunayData.put(geometry, true);
        //We actually fill the mesh
        ArrayList<DEdge> edges = delaunayData.getDelaunayEdges();
        if (!edges.isEmpty()) {
            //We have filled the input of our mesh. We can close our source.
            mesh.setConstraintEdges(edges);
            //If needed, we use the intersection algorithm
            mesh.forceConstraintIntegrity();
        } else {
            mesh.setPoints(delaunayData.getDelaunayPoints());
        }
        //we process delaunay
        mesh.processDelaunay();
        if(qualityRefinement!=-1){
            if(qualityRefinement>=0 && qualityRefinement<1){
            mesh.refineMesh(qualityRefinement,new TriangleQuality());
            }
            else{
                throw new SQLException("The quality value must be comprised between 0 and 1");
            }
        }
        return mesh;
    }
}
