/*
 * Copyright (C) 2013 IRSTV CNRS-FR-2488
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.h2gis.h2spatialext.function.spatial.mesh;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.vividsolutions.jts.geom.*;
import org.h2gis.h2spatialext.function.spatial.convert.ST_ToMultiLine;
import org.h2gis.utilities.jts_utils.CoordinateSequenceDimensionFilter;
import org.jdelaunay.delaunay.error.DelaunayError;
import org.poly2tri.Poly2Tri;
import org.poly2tri.geometry.polygon.PolygonPoint;
import org.poly2tri.triangulation.Triangulatable;
import org.poly2tri.triangulation.TriangulationAlgorithm;
import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;
import org.poly2tri.triangulation.point.TPoint;
import org.poly2tri.triangulation.sets.ConstrainedPointSet;
import org.poly2tri.triangulation.sets.PointSet;

/**
 * This class is used to collect all data used to compute a mesh based on a
 * Delaunay triangulation
 *
 * @author Erwan Bocher
 * @author Nicolas Fortin
 */
public class DelaunayData {
    public enum MODE {DELAUNAY, CONSTRAINED, TESSELLATION}
    private boolean isInput2D;
    private GeometryFactory gf;
    private boolean isMixedDimension;
    private int dimension;
    private Triangulatable convertedInput = null;
    // Precision
    private MathContext mathContext = MathContext.DECIMAL64;

    /**
     * Create a mesh data structure to collect points and edges that will be
     * used by the Delaunay Triangulation
     */
    public DelaunayData() {
    }


    private double r(double v) {
        return new BigDecimal(v).round(mathContext).doubleValue();
    }

    private org.poly2tri.geometry.polygon.Polygon makePolygon(LineString lineString) {
        PolygonPoint[] points = new PolygonPoint[lineString.getNumPoints() - 1];
        for(int idPoint=0; idPoint < points.length; idPoint++) {
            Coordinate point = lineString.getCoordinateN(idPoint);
            points[idPoint] = new PolygonPoint(r(point.x), r(point.y), Double.isNaN(point.z) ? 0 : r(point.z));
        }
        return new org.poly2tri.geometry.polygon.Polygon(points);
    }

    private org.poly2tri.geometry.polygon.Polygon makePolygon(Polygon polygon) {
        org.poly2tri.geometry.polygon.Polygon poly = makePolygon(polygon.getExteriorRing());
        // Add holes
        for(int idHole = 0; idHole < polygon.getNumInteriorRing(); idHole++) {
            poly.addHole(makePolygon(polygon.getInteriorRingN(idHole)));
        }
        return poly;
    }

    private static Coordinate toJts(boolean is2d, org.poly2tri.geometry.primitives.Point pt) {
        if(is2d) {
            return new Coordinate(pt.getX(), pt.getY());
        } else {
            return new Coordinate(pt.getX(), pt.getY(), pt.getZ());
        }
    }
    private int getMinDimension(GeometryCollection geometries) {
        int dimension = Dimension.FALSE;
        for (int i = 0; i < geometries.getNumGeometries(); i++) {
            dimension = Math.max(dimension, geometries.getGeometryN(i).getDimension());
        }
        return dimension;
    }
    /**
     * Put a geometry into the data array. Set true to populate the list of
     * points and edges, needed for the ContrainedDelaunayTriangulation. Set
     * false to populate only the list of points. Note the z-value is forced to
     * O when it's equal to NaN.
     *
     * @param geom Geometry
     * @param mode Delaunay mode
     * @throws DelaunayError
     */
    public void put(Geometry geom, MODE mode) throws IllegalArgumentException {
        gf = geom.getFactory();
        convertedInput = null;
        // Does not use instanceof here as we must not match for overload of GeometryCollection
        if(geom.getClass().getName().equals(GeometryCollection.class.getName())) {
            dimension = getMinDimension((GeometryCollection)geom);
            isMixedDimension = geom.getDimension() != dimension;
        } else {
            dimension = geom.getDimension();
            isMixedDimension = false;
        }
        // Workaround for issue 105 "Poly2Tri does not make a valid convexHull for points and linestrings delaunay
        // https://code.google.com/p/poly2tri/issues/detail?id=105
        if(mode != MODE.TESSELLATION) {
            Geometry convexHull = new FullConvexHull(geom).getConvexHull();
            if(convexHull instanceof Polygon && convexHull.isValid()) {
                // Does not use instanceof here as we must not match for overload of GeometryCollection
                if(geom.getClass().getName().equals(GeometryCollection.class.getName())) {
                    if(dimension > 0) {
                        // Mixed geometry, try to unify sub-types
                        try {
                            geom = ST_ToMultiLine.createMultiLineString(geom).union();
                        } catch (SQLException ex) {
                            throw new IllegalArgumentException(ex);
                        }
                    } else {
                        geom = geom.union();
                    }
                    if(geom.getClass().getName().equals(GeometryCollection.class.getName())) {
                        throw new IllegalArgumentException("Delaunay does not support mixed geometry type");
                    }
                }
                geom = ((Polygon) convexHull).getExteriorRing().union(geom);
            } else {
                return;
            }
        }
        // end workaround
        CoordinateSequenceDimensionFilter info = CoordinateSequenceDimensionFilter.apply(geom);
        isInput2D = info.is2D();
        convertedInput = null;
        if (mode == MODE.DELAUNAY || geom instanceof Point || geom instanceof MultiPoint) {
            setCoordinates(geom);
        } else {
            if(mode == MODE.TESSELLATION) {
                if(geom instanceof Polygon) {
                    convertedInput = makePolygon((Polygon) geom);
                } else {
                    throw new IllegalArgumentException("Only Polygon are accepted for tessellation");
                }
            } else {
                // Constraint delaunay of segments
                addGeometry(geom);
            }
        }
    }

    public void triangulate() {
        if(convertedInput != null) {
            Poly2Tri.triangulate(TriangulationAlgorithm.DTSweep, convertedInput);
        }
    }

    public MultiPolygon getTriangles() {
        if(convertedInput != null) {
            List<DelaunayTriangle> delaunayTriangle = convertedInput.getTriangles();
            // Convert into multi polygon
            Polygon[] polygons = new Polygon[delaunayTriangle.size()];
            for (int idTriangle = 0; idTriangle < polygons.length; idTriangle++) {
                TriangulationPoint[] pts = delaunayTriangle.get(idTriangle).points;
                polygons[idTriangle] = gf.createPolygon(new Coordinate[]{toJts(isInput2D, pts[0]), toJts(isInput2D, pts[1]), toJts(isInput2D, pts[2]), toJts(isInput2D, pts[0])});
            }
            return gf.createMultiPolygon(polygons);
        } else {
            return gf.createMultiPolygon(new Polygon[0]);
        }
    }

    private void addSegment(Set<LineSegment> segmentHashMap, TriangulationPoint a, TriangulationPoint b) {
        LineSegment lineSegment = new LineSegment(toJts(isInput2D, a), toJts(isInput2D, b));
        lineSegment.normalize();
        segmentHashMap.add(lineSegment);
    }

    /**
     * @return Unique triangles edges
     */
    public MultiLineString getTrianglesSides() {
        List<DelaunayTriangle> delaunayTriangle = convertedInput.getTriangles();
        // Remove duplicates edges thanks to this hash map of normalized line segments
        Set<LineSegment> segmentHashMap = new HashSet<LineSegment>(delaunayTriangle.size());
        for(DelaunayTriangle triangle : delaunayTriangle) {
            TriangulationPoint[] pts = triangle.points;
            addSegment(segmentHashMap, pts[0], pts[1]);
            addSegment(segmentHashMap, pts[1], pts[2]);
            addSegment(segmentHashMap, pts[2], pts[0]);
        }
        LineString[] lineStrings = new LineString[segmentHashMap.size()];
        int i = 0;
        for(LineSegment lineSegment : segmentHashMap) {
            lineStrings[i++] = lineSegment.toGeometry(gf);
        }
        return gf.createMultiLineString(lineStrings);
    }

    /**
     * Add a geometry to the list of points and edges used by the triangulation.
     * @param geom
     * @throws DelaunayError
     */
    private void addGeometry(Geometry geom) throws IllegalArgumentException {
        if(!geom.isValid()) {
            throw new IllegalArgumentException("Provided geometry is not valid !");
        }
        if(geom instanceof GeometryCollection) {
            Map<TriangulationPoint, Integer> pts = new HashMap<TriangulationPoint, Integer>(geom.getNumPoints());
            List<Integer> segments = null;
            AtomicInteger pointsCount = new AtomicInteger(0);
            if(!isMixedDimension && dimension != 0) {
                segments = new ArrayList<Integer>(pts.size());
            }
            PointHandler pointHandler = new PointHandler(this, pts, pointsCount);
            LineStringHandler lineStringHandler = new LineStringHandler(this, pts, pointsCount, segments);
            for(int geomId = 0; geomId < geom.getNumGeometries(); geomId++) {
                addSimpleGeometry(geom.getGeometryN(geomId), pointHandler, lineStringHandler);
            }
            if(segments != null) {
                int[] index = new int[segments.size()];
                for(int i = 0; i < index.length; i++) {
                    index[i] = segments.get(i);
                }
                // Construct final points array by reversing key,value of hash map
                TriangulationPoint[] ptsArray = new TriangulationPoint[pointsCount.get()];
                for(Map.Entry<TriangulationPoint, Integer> entry : pts.entrySet()) {
                    ptsArray[entry.getValue()] = entry.getKey();
                }
                pts.clear();
                convertedInput = new ConstrainedPointSet(Arrays.asList(ptsArray), index);
            } else {
                convertedInput = new PointSet(pts.keySet());
            }
        } else {
            addGeometry(geom.getFactory().createGeometryCollection(new Geometry[]{geom}));
        }
    }

    private void addSimpleGeometry(Geometry geom, PointHandler pointHandler, LineStringHandler lineStringHandler) throws IllegalArgumentException {
        if(geom instanceof Point) {
            geom.apply(pointHandler);
        } else if(geom instanceof LineString) {
            lineStringHandler.reset();
            geom.apply(lineStringHandler);
        } else if(geom instanceof Polygon) {
            Polygon polygon = (Polygon) geom;
            lineStringHandler.reset();
            polygon.getExteriorRing().apply(lineStringHandler);
            for(int idHole = 0; idHole < polygon.getNumInteriorRing(); idHole++) {
                lineStringHandler.reset();
                polygon.getInteriorRingN(idHole).apply(lineStringHandler);
            }
        }
    }
    /**
     * Add all coordinates of the geometry to the list of points
     *
     * @param geom
     * @throws DelaunayError
     */
    private void setCoordinates(Geometry geom) throws IllegalArgumentException {
        Map<TriangulationPoint, Integer> pts = new HashMap<TriangulationPoint, Integer>(geom.getNumPoints() + 4);
        AtomicInteger index = new AtomicInteger(0);
        PointHandler pointHandler = new PointHandler(this, pts, index);
        geom.apply(pointHandler);
        convertedInput = new PointSet(pts.keySet());
    }

    private static class PointHandler implements CoordinateFilter {
        private DelaunayData delaunayData;
        private Map<TriangulationPoint, Integer> pts;
        private AtomicInteger maxIndex;

        public PointHandler(DelaunayData delaunayData, Map<TriangulationPoint, Integer> pts, AtomicInteger maxIndex) {
            this.delaunayData = delaunayData;
            this.pts = pts;
            this.maxIndex = maxIndex;
        }

        protected int addPt(Coordinate coordinate) {
            TPoint pt = new TPoint(delaunayData.r(coordinate.x), delaunayData.r(coordinate.y),
                    Double.isNaN(coordinate.z) ? 0 : delaunayData.r(coordinate.z));
            Integer index = pts.get(pt);
            if(index == null) {
                index = maxIndex.getAndAdd(1);
                pts.put(pt, index);
            }
            return index;
        }

        @Override
        public void filter(Coordinate pt) {
            addPt(pt);
        }
    }

    private static class LineStringHandler extends PointHandler {
        private List<Integer> segments;
        private int firstPtIndex = -1;

        public LineStringHandler(DelaunayData delaunayData, Map<TriangulationPoint, Integer> pts,
                                 AtomicInteger maxIndex, List<Integer> segments) {
            super(delaunayData, pts, maxIndex);
            this.segments = segments;
        }

        /**
         * New line string
         */
        public void reset() {
            firstPtIndex = -1;
        }

        @Override
        public void filter(Coordinate pt) {
            if(firstPtIndex == -1) {
                firstPtIndex = addPt(pt);
            } else {
                segments.add(firstPtIndex);
                firstPtIndex = addPt(pt);
                segments.add(firstPtIndex);
            }
        }
    }
}
