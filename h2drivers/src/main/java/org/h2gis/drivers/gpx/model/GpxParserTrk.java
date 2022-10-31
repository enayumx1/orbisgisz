/*
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
package org.h2gis.drivers.gpx.model;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Specific parser for tracks. It will be call each time a <trk> markup is
 * found. It is for the 1.1 version
 *
 * @author Erwan Bocher and Antonin Piasco
 */
public final class GpxParserTrk extends AbstractGpxParserTrk {

    /**
     * Create a new specific parser. It has in memory the default parser, the
     * contentBuffer, the elementNames, the currentLine and the trkID.
     *
     * @param reader The XMLReader used in the default class
     * @param parent The parser used in the default class
     */
    public GpxParserTrk(XMLReader reader, AbstractGpxParserDefault parent) {
        super.initialise(reader, parent);
    }

    /**
     * Fires whenever an XML start markup is encountered. It creates a new
     * trackSegment when a <trkseg> markup is encountered. It creates a new
     * trackPoint when a <trkpt> markup is encountered. It saves informations
     * about <link> in currentPoint or currentLine.
     *
     * @param uri URI of the local element
     * @param localName Name of the local element (without prefix)
     * @param qName qName of the local element (with prefix)
     * @param attributes Attributes of the local element (contained in the
     * markup)
     * @throws SAXException Any SAX exception, possibly wrapping another
     * exception
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (localName.compareToIgnoreCase(GPXTags.LINK) == 0) {
            if (isPoint()) {
                getCurrentPoint().setLink(attributes);
            } else {
                getCurrentLine().setLink(attributes);
            }
        }
    }

    /**
     * Fires whenever an XML end markup is encountered. It catches attributes of
     * trackPoints, trackSegments or routes and saves them in corresponding
     * values[].
     *
     * @param uri URI of the local element
     * @param localName Name of the local element (without prefix)
     * @param qName qName of the local element (with prefix)
     * @throws SAXException Any SAX exception, possibly wrapping another
     * exception
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if ((getCurrentElement().compareToIgnoreCase("text") == 0) && (isPoint())) {
            getCurrentPoint().setLinkText(getContentBuffer());
        } else if (getCurrentElement().compareToIgnoreCase("text") == 0) {
            getCurrentLine().setLinkText(getContentBuffer());
        }
    }
}