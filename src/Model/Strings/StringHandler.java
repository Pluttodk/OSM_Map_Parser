package Model.Strings;

import Model.AutoFileReader;
import Model.Exceptions.IllegalLanguageException;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Handles all the strings and files parsed
 */
public class StringHandler {

    /**
     * List of all languages
     */
    private static List<Language> languageList;


    /**
     * Returns a list of all Languages
     * @param directoryPath Path to folder with strings
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws IllegalLanguageException
     */
    public static List<Language> getLanguageList(String directoryPath) throws SAXException, IOException, IllegalLanguageException {
        languageList = new ArrayList<>();

        if (StringHandler.class.getProtectionDomain().getCodeSource().getLocation().toString().endsWith(".jar")){
            URL thisJarPath = StringHandler.class.getProtectionDomain().getCodeSource().getLocation();
            ZipInputStream thisJar = new ZipInputStream(thisJarPath.openStream());
            ZipEntry zipEntry;


            while ((zipEntry = thisJar.getNextEntry()) != null) {
                String entryName = zipEntry.getName();
                if (entryName.contains(directoryPath) && entryName.endsWith(".xml")) {
                    try {
                        parseFile(entryName);
                    } catch (Exception ignored) {  }
                }
            }

        }else {
            File directory = new File(directoryPath);
            File[] files = new File[10];
            if (!directory.exists() || !directory.isDirectory()) throw new FileNotFoundException();
            if (directory.listFiles() != null) {
                files = directory.listFiles();
            }
            for (File file : files){
                parseFile(file.getPath());
            }
        }
        return languageList;

    }

    /**
     * Parses all strings and creates a Language when done
     * @param filename Path to xml file to parse
     * @throws IOException
     * @throws SAXException
     * @throws IllegalLanguageException Throws if the Language xml file is wrongly written
     */
    private static void parseFile(String filename) throws IOException, SAXException, IllegalLanguageException {
        AutoFileReader.XMLReader fileReader = new AutoFileReader.XMLReader(filename);
        StringParser reader = new StringParser();
        fileReader.parse(reader);
        HashMap<String, String> strings = reader.getStrings();
        String lang;
        if (reader.getLang() != null){
            lang = reader.getLang();
        }else{
            throw new IllegalLanguageException("Illegal Format");
        }
        Language language = new Language(lang,strings);

        languageList.add(language);
    }

}
