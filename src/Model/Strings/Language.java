package Model.Strings;

import java.util.HashMap;
import java.util.Observable;

/**
 * Class that contains all information about all languages
 */
public class Language{

    private HashMap<String, String> stringStringHashMap;
    private String lang;

    /**
     * Creates new language
     * @param lang Name of the language
     * @param stringStringHashMap HashMap of keys = global variables value = translation
     */
    Language(String lang,HashMap<String, String> stringStringHashMap){
        this.stringStringHashMap = stringStringHashMap;
        this.lang = lang;
    }

    /**
     * Gets a string from HashMap
     * @param string key value to search after
     * @return Translation of the chosen string
     */
    public String getString(String string){
        return stringStringHashMap.get(string);
    }

    /**
     * Get name of the language
     * @return name of language
     */
    public String getName() {
        return lang;
    }
}
