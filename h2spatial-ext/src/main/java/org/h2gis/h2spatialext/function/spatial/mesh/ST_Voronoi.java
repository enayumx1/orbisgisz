package org.h2gis.h2spatialext.function.spatial.mesh;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.triangulate.VoronoiDiagramBuilder;
import com.vividsolutions.jts.triangulate.quadedge.QuadEdge;
import com.vividsolutions.jts.triangulate.quadedge.QuadEdgeSubdivision;
import com.vividsolutions.jts.triangulate.quadedge.TriangleVisitor;
import org.h2gis.h2spatialapi.DeterministicScalarFunction;
import org.h2gis.h2spatialext.function.spatial.convert.ST_ToMultiSegments;
import org.h2gis.utilities.jts_utils.Voronoi;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nicolas Fortin
 */
public class ST_Voronoi extends DeterministicScalarFunction {
    private static final int DEFAULT_DIMENSION = 2;

    public ST_Voronoi() {
        addProperty(PROP_REMARKS, "Construct a voronoi diagram from a delaunay triangulation or a set of points.\n" +
                "ST_VORONOI(THE_GEOM MULTIPOLYGON)\n" +
                "ST_VORONOI(THE_GEOM MULTIPOLYGON,OUT_DIMENSION INTEGER)\n" +
                "ST_VORONOI(THE_GEOM MULTIPOLYGON,OUT_DIMENSION INTEGER,ENVELOPE POLYGON)\n" +
                "ST_VORONOI(THE_GEOM MULTIPOINTS)\n" +
                "ST_VORONOI(THE_GEOM MULTIPOINTS,OUT_DIMENSION INTEGER)\n" +
                "ST_VORONOI(THE_GEOM MULTIPOINTS,OUT_DIMENSION INTEGER,ENVELOPE POLYGON)\n" +
                "Ex:\n" +
                "SELECT ST_VORONOI(ST_DELAUNAY('MULTIPOINT(2 2 0,6 3 0,4 7 0,2 8 0,1 6 0,3 5 0)')) the_geom;\n" +
                "SELECT ST_VORONOI(ST_DELAUNAY('MULTIPOINT(2 2 0,6 3 0,4 7 0,2 8 0,1 6 0,3 5 0)'), 1)\n" +
                "SELECT ST_VORONOI(ST_DELAUNAY('MULTIPOINT(2 2 0,6 3 0,4 7 0,2 8 0,1 6 0,3 5 0)'), 1, ST_EXPAND('POINT(3 5)', 10, 10))");
    }

    @Override
    public String getJavaStaticMethod() {
        return "voronoi";
    }

    public static GeometryCollection voronoi(Geometry geomCollection) throws SQLException {
        return voronoi(geomCollection, DEFAULT_DIMENSION);
    }

    public static GeometryCollection voronoi(Geometry geomCollection, int outputDimension) throws SQLException {
        return voronoi(geomCollection, outputDimension, null);
    }

    private static GeometryCollection returnEmptyCollection(int outputDimension) {
        switch (outputDimension) {
            case 2:
                return new GeometryFactory().createMultiPolygon(new Polygon[0]);
            case 1:
                return new GeometryFactory().createMultiLineString(new LineString[0]);
            default:
                return new GeometryFactory().createMultiPoint(new Point[0]);
        }
    }

    public static GeometryCollection voronoi(Geometry geomCollection, int outputDimension, Geometry envelope) throws SQLException {
        if(geomCollection == null) {
            return returnEmptyCollection(outputDimension);
        }
        if(geomCollection instanceof MultiPoint || (geomCollection instanceof GeometryCollection &&
                geomCollection.getNumGeometries() > 0 && geomCollection.getGeometryN(0) instanceof Point) ) {
            // From point set use JTS
            VoronoiDiagramBuilder diagramBuilder = new VoronoiDiagramBuilder();
            diagramBuilder.setSites(geomCollection);
            if(envelope != null) {
                diagramBuilder.setClipEnvelope(envelope.getEnvelopeInternal());
            }
            if(outputDimension == 2) {
                // Output directly the polygons
                return (GeometryCollection) diagramBuilder.getDiagram(geomCollection.getFactory());
            } else if (outputDimension == 1) {
                // Convert into lineStrings.
                //TODO remove duplicate segments
                return ST_ToMultiSegments.createSegments(diagramBuilder.getDiagram(geomCollection.getFactory()));
            } else {
                // Extract triangles Circumcenter
                QuadEdgeSubdivision subdivision = diagramBuilder.getSubdivision();
                List<Coordinate> circumcenter = new ArrayList<Coordinate>(geomCollection.getNumGeometries());
                subdivision.visitTriangles(new TriangleVisitorCircumCenter(circumcenter), false);
                return geomCollection.getFactory().createMultiPoint(circumcenter.toArray(new Coordinate[circumcenter.size()]));
            }
        } else {
            if(Double.compare(geomCollection.getEnvelopeInternal().getArea(), 0d) == 0) {
                return returnEmptyCollection(outputDimension);
            }
            // Triangle input use internal method
            Voronoi voronoi = new Voronoi();
            if (envelope != null) {
                voronoi.setEnvelope(envelope.getEnvelopeInternal());
            }
            voronoi.generateTriangleNeighbors(geomCollection);
            return voronoi.generateVoronoi(outputDimension);
        }
    }

    private static class TriangleVisitorCircumCenter implements TriangleVisitor {
        List<Coordinate> circumCenters;

        public TriangleVisitorCircumCenter(List<Coordinate> circumCenters) {
            this.circumCenters = circumCenters;
        }

        @Override
        public void visit(QuadEdge[] triEdges) {
            Coordinate a = triEdges[0].orig().getCoordinate();
            Coordinate b = triEdges[1].orig().getCoordinate();
            Coordinate c = triEdges[2].orig().getCoordinate();
            circumCenters.add(Triangle.circumcentre(a, b, c));
        }
    }
}