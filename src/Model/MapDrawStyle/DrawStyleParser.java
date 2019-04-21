package Model.MapDrawStyle;

import Model.*;
import Model.OSM.WayType;
import org.xml.sax.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * The Model.MapDrawStyle.DrawStyle parser can convert XML into one or more instances of Model.MapDrawStyle.MapDrawStyle
 */
public class DrawStyleParser {
    private MapDrawStyleList mapDrawStyles;
    private static MapDrawStyle tempMapDrawStyle;

    /**
     * Loads all XML files in a given directory an tries to parse the files to Model.MapDrawStyle.MapDrawStyle instances in a Model.MapDrawStyle.MapDrawStyleList
     * @param directoryPath The path to the directory
     * @return A collection of MapDrawStyles
     * @throws IOException If the directory is not found
     * @throws SAXException If there was an error while parsing
     */
    public MapDrawStyleList autoloadAllStyles(String directoryPath) throws IOException, SAXException {
        mapDrawStyles = new MapDrawStyleList();
        if (DrawStyleParser.class.getProtectionDomain().getCodeSource().getLocation().toString().endsWith(".jar")) {
            //If loaded from JAR-file
            URL thisJarPath = DrawStyleParser.class.getProtectionDomain().getCodeSource().getLocation();
            ZipInputStream thisJar = new ZipInputStream(thisJarPath.openStream());
            ZipEntry zipEntry;

            while ((zipEntry = thisJar.getNextEntry()) != null) {
                String entryName = zipEntry.getName();
                if (entryName.contains(directoryPath) && entryName.endsWith(".xml")) {
                    try {
                        mapDrawStyles.add(parseFile(entryName));
                    } catch (Exception e) {  }
                }
            }
        } else {
            //If loaded from IDE
            File directory = new File(directoryPath);
            if (!directory.exists() || !directory.isDirectory()) throw new FileNotFoundException();

            for (File file : directory.listFiles()) {
                mapDrawStyles.add(parseFile(file.getPath()));
            }
        }
        return mapDrawStyles;
    }

    /**
     * Parse a single file from XML to a Model.MapDrawStyle.MapDrawStyle
     * @param filename The path to the file
     * @return An instance of Model.MapDrawStyle.MapDrawStyle
     * @throws IOException If the file is not found
     * @throws SAXException If there was an error while parsing
     */
    public MapDrawStyle parseFile(String filename) throws IOException, SAXException {
        AutoFileReader.XMLReader reader = new AutoFileReader.XMLReader(filename);
        reader.parse(new DrawStyleHandler());
        return tempMapDrawStyle;
    }

    /**
     * The handler for parsing XML to Model.MapDrawStyle.MapDrawStyle. Methods are called automatically and should not be called manually.
     */
    private static class DrawStyleHandler implements ContentHandler {
        boolean isWellFormatted = true;

        //Model.MapDrawStyle.MapDrawStyle info
        String name;
        String id;

        //Actual field values
        //--element
        boolean isDualColor, isArea;
        WayType type;
        ColorHex bodyHex, borderHex;
        LineType lineType;
        int lineWeight, zoomLevel;
        MapIcon mapIcon;


        //Standard values
        //--element
        final String STANDARD_IS_AREA = "yes";
        final ColorHex STANDARD_BODY_HEX = new ColorHex("000000");
        final ColorHex STANDARD_BORDER_HEX = new ColorHex("000000");
        final LineType STANDARD_LINE_TYPE = LineType.DEFAULT;
        final int STANDARD_LINE_WEIGHT = 1;
        final int STANDARD_ZOOM_LEVEL = 1;
        final MapIcon STANDARD_MAP_ICON = MapIcon.NONE;

        EnumMap<WayType, DrawStyle> drawStyles;

        @Override
        public void setDocumentLocator(Locator locator) {

        }

        @Override
        public void startDocument() throws SAXException {

        }

        @Override
        public void endDocument() throws SAXException {

        }

        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {

        }

        @Override
        public void endPrefixMapping(String prefix) throws SAXException {

        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            switch (qName) {
                case "mapDrawStyle":
                    drawStyles = new EnumMap<>(WayType.class);
                    isWellFormatted = true;
                    id = atts.getValue("displayName");
                    if (id == null) {
                        isWellFormatted = false;
                        break;
                    }
                    name = atts.getValue("displayName");
                    break;
                case "element":
                    if (isWellFormatted) {
                        try {
                            type = WayType.valueOf(atts.getValue("type").toUpperCase());
                        } catch (Exception e) {
                            break;
                        }

                        isArea = atts.getValue("isArea")!= null && atts.getValue("isArea").equals(STANDARD_IS_AREA);
                        isDualColor = atts.getValue("bodyHex") != null && atts.getValue("borderHex") != null;

                        try {
                            bodyHex = new ColorHex(atts.getValue("bodyHex"));
                        } catch (Exception e) {
                            bodyHex = STANDARD_BODY_HEX;
                        }

                        try {
                            borderHex = new ColorHex(atts.getValue("borderHex"));
                        } catch (Exception e) {
                            borderHex = STANDARD_BORDER_HEX;
                        }

                        try {
                            zoomLevel = Integer.parseInt(atts.getValue("zoomLevel"));
                        }catch (Exception e){
                            zoomLevel = STANDARD_ZOOM_LEVEL;
                        }

                        try {
                            lineWeight = Integer.parseInt(atts.getValue("lineWeight"));
                        } catch (Exception e) {
                            lineWeight = STANDARD_LINE_WEIGHT;
                        }

                        try {
                            lineType = LineType.valueOf(atts.getValue("lineType").toUpperCase());

                        } catch (Exception e) {
                            lineType = STANDARD_LINE_TYPE;
                        }
                        try {
                            mapIcon = MapIcon.valueOf(atts.getValue("icon").toUpperCase());
                        } catch (Exception e) {
                            mapIcon = STANDARD_MAP_ICON;
                        }
                        drawStyles.put(type, new DrawStyle(type, bodyHex, borderHex, zoomLevel, lineType, isArea, isDualColor, lineWeight, mapIcon));
                    }
                    break;

            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            switch (qName) {
                case "mapDrawStyle":
                    if (isWellFormatted) tempMapDrawStyle = new MapDrawStyle(drawStyles, id, name);
                    break;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {

        }

        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {

        }

        @Override
        public void processingInstruction(String target, String data) throws SAXException {

        }

        @Override
        public void skippedEntity(String name) throws SAXException {

        }
    }
}
