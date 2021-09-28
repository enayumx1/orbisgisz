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

package org.h2gis.h2spatial;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKBWriter;
import com.vividsolutions.jts.io.WKTWriter;
import org.h2.value.CompareMode;
import org.h2.value.Value;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Storage type of H2 for Geometry field.
 * This class enable the cast to String in the following way "select the_geom::text from mytable;"
 * @author Nicolas Fortin
 */
public class ValueGeometry extends Value implements Serializable {
    private static final long serialVersionUID = 3710022674420076702L;
    private static final WKBWriter WKB_WRITER = new WKBWriter(3,true);
    private static final WKTWriter WKT_WRITER = new WKTWriter();
    /** Keep null until the method getValue is called */
    private Geometry value;

    /**
     * Value constructor
     * @param value Stored value
     */
    public ValueGeometry(Geometry value) {
        this.value = value;
    }

    /**
     * Get geometry value
     * @return
     */
    public Geometry getValue() {
        if(value==null) {
            // Create an empty point
            value = (new GeometryFactory()).createPoint((Coordinate)null);
        }
        return value;
    }

    @Override
    public byte[] getBytesNoCopy() {
        return WKB_WRITER.write(getValue());
    }

    @Override
    public String getSQL() {
        return getString();
    }

    @Override
    public int getType() {
        return Value.JAVA_OBJECT;
    }

    @Override
    public long getPrecision() {
        return getBytes().length;
    }

    @Override
    public int getDisplaySize() {
        return getString().length();
    }

    @Override
    public String getString() {
        return WKT_WRITER.write(getValue());
    }

    @Override
    public Object getObject() {
        return getValue();
    }

    @Override
    public void set(PreparedStatement preparedStatement, int i) throws SQLException {
        preparedStatement.setObject(i,getObject());
    }

    @Override
    protected int compareSecure(Value value, CompareMode compareMode) {
        return getValue().compareTo(value);
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return getValue().equals(o);
    }
}
