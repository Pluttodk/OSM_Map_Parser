package Model;

import lib.TST;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Class to automatically suggest words
 */
public class AutoCompleter {
    private TST<String> words;

    /**
     * For no data input
     */
    public AutoCompleter() {
        words = new TST<>();
    }

    /**
     * For a already ordered input
     * @param words the words ordered in a ternary search trie
     */
    public AutoCompleter(TST<String> words) {
        this.words = words;
    }

    /**
     * For a input from a file
     * @param filename the relative path to the file
     */
    public AutoCompleter(String filename) throws IOException {
        words = new TST<>();

        AutoFileReader.LineReader reader = new AutoFileReader.LineReader(filename);

        reader.nextLine();
        while (!reader.isEmpty()) {
            addWord(reader.getLine());
            reader.nextLine();
        }
    }

    /**
     * Adds a word to the word list
     * @param word The word to be added
     */
    public void addWord(String word) {
        words.put(word, word);
    }

    /**
     * Returns the first suggested word
     * @param inputWords the words that should be considered for the suggestion
     * @return the suggested word
     */
    public SuggestedWord getSuggestedWord(String[] inputWords) {
        return getSuggestedWord(inputWords, 0);
    }

    /**
     * Returns the nth suggested word
     * @param inputWords the words that should be considered for the suggestion
     * @param offset the offset from where the word should be taken (n)
     * @return the suggested word
     */
    public SuggestedWord getSuggestedWord(String[] inputWords, int offset) {
        for (int i = 0; i < inputWords.length; i++) {
            String searchString = "";
            for (int j = i; j < inputWords.length; j++) {
                searchString += inputWords[j].substring(0,1).toUpperCase() + inputWords[j].substring(1);
                if (j != inputWords.length-1) searchString += " ";
            }
            for(String word : words.keysWithPrefix(searchString)) {
               if (offset > 0) {
                  offset--;
               } else {
                  return new SuggestedWord(searchString, word, word.substring(searchString.length(), word.length()));
               }
           }
        }
        return null;
    }

    /**
     * Returns a list of suggested words
     * @param inputWords the words to be considered for the suggestions
     * @param maxCount the maximal number of suggestions to be returned
     * @return the list of suggested words
     */
    public SuggestedWord[] getSuggestedWords(String[] inputWords, int maxCount) {
        ArrayList<SuggestedWord> suggestedWords = new ArrayList<>();
        for (int i = 0; i < maxCount; i++) {
            SuggestedWord suggestedWord = getSuggestedWord(inputWords, i);
            if (suggestedWord != null) {
                suggestedWords.add(suggestedWord);
            }
        }
        SuggestedWord[] suggestedWordsArray = new SuggestedWord[suggestedWords.size()];
        return suggestedWords.toArray(suggestedWordsArray);
    }

    /**
     * A class to represent internally the suggested word
     */
    public class SuggestedWord {
        private String inputWord;
        private String suggestedWord;
        private String suggestedCompletion;

        private SuggestedWord(String inputWord, String suggestedWord, String suggestedCompletion){
            this.inputWord = inputWord;
            this.suggestedWord = suggestedWord;
            this.suggestedCompletion = suggestedCompletion;
        }

        /**
         * Returns the string that the suggestion was based upon
         * @return the inputted word
         */
        public String getInputWord() {
            return inputWord;
        }

        /**
         * Returns the string that is suggested to be inserted in the input
         * @return the suggested insertion
         */
        public String getSuggestedCompletion() {
            return suggestedCompletion;
        }

        /**
         * Returns the input word combined with the completion, so basically the whole suggested word
         * @return the whole suggested word
         */
        public String getSuggestedWord() {
            return suggestedWord;
        }

        @Override
        public String toString() {
            return suggestedWord;
        }
    }
}