package Model;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * A class to automatically handle whether the file is loaded in jar-runtime or IDE-runtime.
 */
public class AutoFileReader {
    private BufferedInputStream inputStream;

    /**
     * Initiates the filereader. Looks inside jar-file automatically (if launched by jar)
     * @param filename The relative path to the file to be loaded (relative to the root)
     * @throws IOException Thrown if the file does not exist
     */
    public AutoFileReader(String filename) throws IOException {
        this(filename, true);
    }

    /**
     * Initiates the filereader with the option to look outside the jar file.
     * If you look outside the jar file, the path should be absolute or relative to the directory that the jar is placed in.
     * @param filename The relative path to the file to be loaded (relative to the root)
     * @param allowJarSearch False if the file should be found outside the jar, true if the file should be found inside the jar (requires that the program is executed from a jar.)
     * @throws IOException If file is not found, or another I/O error
     */
    public AutoFileReader(String filename, boolean allowJarSearch) throws IOException {
        if (allowJarSearch && getClass().getProtectionDomain().getCodeSource().getLocation().toString().endsWith(".jar")) {
            inputStream = new BufferedInputStream(AutoFileReader.class.getResourceAsStream("/"+filename)); //JAR runtime
        } else {
            inputStream = new BufferedInputStream(new FileInputStream(filename)); //IDE runtime
        }
    }

    /**
     * Constructor to use the functionality from the AutoFileReader, but the file is not loaded from the harddrive
     * @param is The InputStream to be read
     */
    public AutoFileReader(InputStream is) {
        inputStream = new BufferedInputStream(is);
    }

    /**
     * Get the InputStreamReader from your file
     * @return The InputStreamReader
     */
    public InputStreamReader getInputStreamReader() {
        try {
            return new InputStreamReader(inputStream, "UTF8");
        } catch (UnsupportedEncodingException e) {
            //Will never be thrown
            throw new RuntimeException(e);
        }
    }

    /**
     * Extract the corresponding InputStream
     * @return The InputStream
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Extract the corresponding InputSource
     * @return The InputSource
     */
    public InputSource getInputSource() {
        return new InputSource(inputStream);
    }

    /**
     * An inner class, if you want to read the file line by line
     */
    public static class LineReader extends AutoFileReader {
        private BufferedReader br;
        private String line;

        /**
         * Initiates the filereader
         * @param filename The relative path to the file to be loaded (relative to the root)
         * @throws IOException If file is not found, or another I/O error
         */
        public LineReader(String filename) throws IOException {
            super(filename);
            br = new BufferedReader(getInputStreamReader());
        }

        /**
         * Get the last read line
         * @return The line as a string
         */
        public String getLine() {
            return line;
        }

        /**
         * Read the next line
         * @throws IOException Thrown if an I/O error occurs
         */
        public void nextLine() throws IOException {
            line = br.readLine();
        }

        /**
         * Check there is no more lines
         * @return True if line is null, false if not
         */
        public boolean isEmpty() {
            return line == null;
        }
    }

    /**
     * Inner class to handle XML files parse them
     */
    public static class XMLReader extends AutoFileReader {
        org.xml.sax.XMLReader xmlReader;

        /**
         * Initiates the reader. Looks inside jar-file automatically (if launched by jar)
         * @param filename The relative path to the file to be loaded (relative to the root)
         * @throws IOException If file is not found, or another I/O error
         * @throws SAXException If an error in SAX occurs
         */
        public XMLReader(String filename) throws IOException, SAXException {
            this(filename, true);
        }

        /**
         * Construction of the xml reader
         * @param filename The relative path to the file to be loaded (relative to the root)
         * @param allowJarSearch Whether to look inside potential jar or not
         * @throws IOException If an I/O error occurs
         */
        public XMLReader(String filename, boolean allowJarSearch) throws IOException, SAXException {
            super(filename, allowJarSearch);
            xmlReader = XMLReaderFactory.createXMLReader();
        }

        /**
         * Constructor to use the functionality from the AutoFileReader.XMLReader, but the file is not loaded from the harddrive
         * @param is The InputStream to be read
         * @throws SAXException If an error in SAX occurs
         */
        public XMLReader(InputStream is) throws SAXException {
            super(is);
            xmlReader = XMLReaderFactory.createXMLReader();
        }

        /**
         * Method that automatically parses the XML file
         * @param handler The class that should handle the parsing
         * @throws IOException If file is not found, or another I/O error
         * @throws SAXException If an error in SAX occurs
         */
        public void parse(ContentHandler handler) throws IOException, SAXException {
            xmlReader.setContentHandler(handler);
            xmlReader.parse(getInputSource());
        }
    }

    /**
     * An inner class that is able to read Zip files
     */
    public static class ZipReader extends AutoFileReader {
        private ZipInputStream zipInputStream;
        private ZipEntry zipEntry;

        /**
         * Initiates the filereader. Looks inside jar-file automatically (if launched by jar)
         * @param filename The relative path to the file to be loaded (relative to the root)
         * @throws IOException If file is not found, or another I/O error
         */
        public ZipReader(String filename) throws IOException {
            this(filename, true);
        }

        /**
         * Construction of the zip reader
         * @param filename The relative path to the file to be loaded (relative to the root)
         * @param allowJarSearch Whether to look inside potential jar or not
         * @throws IOException If an I/O error occurs
         */
        public ZipReader(String filename, boolean allowJarSearch) throws IOException {
            super(filename, allowJarSearch);
            zipInputStream = new ZipInputStream(getInputStream());
        }

        /**
         * Get the first entry in the zip file as an InputStream
         * @return The InputStream
         * @throws IOException If file is not found, or another I/O error
         */
        public ZipInputStream getFirstInputStream() throws IOException {
            zipInputStream = new ZipInputStream(getInputStream());
            zipEntry = zipInputStream.getNextEntry();
            return zipInputStream;
        }

        /**
         * Get the current zip entry as a ZipEntry
         * @return The ZipEntry
         */
        public ZipEntry getCurrentZipEntry() {
            return zipEntry;
        }

        /**
         * Get the InputStream as a ZipInputStream
         * @return The ZipInputStream
         * @throws IOException If file is not found, or another I/O error
         */
        public ZipInputStream getZipInputStream() throws IOException {
            ZipInputStream zipInputStream = new ZipInputStream(getInputStream());
            return zipInputStream;
        }
    }

    /**
     * An inner class to handle bin-objects
     * @param <O> The class the object should be casted to
     */
    public static class ObjectReader<O> extends AutoFileReader {
        private ObjectInputStream objectInputStream;

        /**
         * Initiates the filereader. Looks inside jar-file automatically (if launched by jar)
         * @param filename The relative path to the file to be loaded (relative to the root)
         * @throws IOException If file is not found, or another I/O error
         */
        public ObjectReader(String filename) throws IOException {
            this(filename, true);
        }

        /**
         * Construction of the object reader
         * @param filename The relative path to the file to be loaded (relative to the root)
         * @param allowJarSearch Whether to look inside potential jar or not
         * @throws IOException If an I/O error occurs
         */
        public ObjectReader(String filename, boolean allowJarSearch) throws IOException {
            super(filename, allowJarSearch);
            objectInputStream = new ObjectInputStream(getInputStream());
        }

        /**
         * Constructor to use the functionality from the AutoFileReader, but the file is not loaded from the harddrive
         * @param is The InputStream to be read
         */
        public ObjectReader(InputStream is) throws IOException {
            super(is);
            objectInputStream = new ObjectInputStream(getInputStream());
        }

        /**
         * Convert and get the loaded bin to an object of the parameter class, O
         * @return The object of the parameter class, O, that was loaded from the file
         * @throws IOException If an I/O error occurs
         * @throws ClassNotFoundException If an illegal parameter class was provided
         */
        public O getObject() throws IOException, ClassNotFoundException {
            return (O) objectInputStream.readObject();
        }
    }

    /**
     * Inner class to handle images
     */
    public static class ImageReader {
        private ImageIcon imageIcon;
        private Image image;

        /**
         * Construction of the image reader
         * @param filename The relative path to the file to be loaded (relative to the root)
         * @param allowJarSearch Whether to look inside potential jar or not
         * @throws IOException If an I/O error occurs
         */
        public ImageReader(String filename, boolean allowJarSearch) throws IOException {
            if (allowJarSearch && getClass().getProtectionDomain().getCodeSource().getLocation().toString().endsWith(".jar")) {
                imageIcon = new ImageIcon(getClass().getResource("/"+filename));
                image = ImageIO.read(getClass().getResource("/"+filename));
            } else {
                imageIcon = new ImageIcon(filename);
                image = ImageIO.read(new File(filename));
            }
        }

        /**
         * Construction of the image reader. Looks inside jar-file automatically (if launched by jar)
         * @param filename The relative path to the file to be loaded (relative to the root)
         * @throws IOException If an I/O error occurs
         */
        public ImageReader(String filename) throws IOException {
            this(filename, true);
        }

        /**
         * Get loaded image as an ImageIcon
         * @return the ImageIcon
         */
        public ImageIcon getImageIcon() {
            return imageIcon;
        }

        /**
         * Get loaded image as an Image instance
         * @return the Image
         */
        public Image getImage() {
            return image;
        }
    }
}