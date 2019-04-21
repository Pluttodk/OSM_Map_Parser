package Model.Strings;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.util.HashMap;

/**
 * Parser for languages
 */
public class StringParser implements ContentHandler {

    private HashMap<String, String> strings;
    String id, string;
    String lang;

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
        switch (qName){
            case "strings":
                strings = new HashMap<>();
                lang = atts.getValue("lang");
                break;

            case "string":
                id = atts.getValue("id");
                string = atts.getValue("string");

                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName){
            case "string":
                strings.put(id, string);
                break;
        }
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

    public HashMap<String, String> getStrings() {
        return strings;
    }

    String getLang(){
        return lang;
    }

}
