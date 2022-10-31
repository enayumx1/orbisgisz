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
package org.h2gis.drivers.gpx.model;

import com.vividsolutions.jts.geom.Coordinate;
import org.xml.sax.Attributes;

/**
 * This class is used to convert a waypoint to a coordinate
 * @author Erwan Bocher
 */
public class GPXCoordinate {
    
    /**
     * General method to create a coordinate from a gpx point. 
     *
     * @param attributes Attributes of the point. Here it is latitude and
     * longitude
     * @throws NumberFormatException
     * @return a coordinate
     */
    public static Coordinate createCoordinate(Attributes attributes) throws NumberFormatException {
        // Associate a latitude and a longitude to the point
        double lat;
        double lon;
        try {
            lat = Double.parseDouble(attributes.getValue(GPXTags.LAT));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Cannot parse the latitude value");
        }
        try {
            lon = Double.parseDouble(attributes.getValue(GPXTags.LON));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Cannot parse the longitude value");
        }
        String eleValue = attributes.getValue(GPXTags.ELE);
        double ele = Double.NaN;
        if (eleValue != null) {
            try {
                ele = Double.parseDouble(eleValue);

            } catch (NumberFormatException e) {
                throw new NumberFormatException("Cannot parse the elevation value");
            }
        }        
        return new Coordinate(lon, lat, ele);
    }
}
