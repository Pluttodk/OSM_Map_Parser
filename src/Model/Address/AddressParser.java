
package Model.Address;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AddressParser {

    /**
     * Inner static class Builder
     * Used to create an new instance of Address with the related information
     * Uses the Builder Pattern
     */
    public static class Builder {
        private String street = "Unknown", house, floor, side, zipcode, city;
        public Builder street(String street) { this.street = street; return this; }
        public Builder house(String house)   { this.house = house;   return this; }
        public Builder floor(String floor)   { this.floor = floor;   return this; }
        public Builder side(String side)     { this.side = side;     return this; }
        public Builder zipcode(String zipcode) { this.zipcode = zipcode; return this; }
        public Builder city(String city)     { this.city = city;     return this; }
        public Address build() {
            return new Address(this.street, this.house, this.floor, this.side, this.zipcode, this.city);
        }
    }

    private final static String STREET = "(?<street>[A-Za-zæøåÆØÅéÉ. ]*([0-9]+)?[A-Za-zæøåÆØÅéÉ. ]+?)";
    private final static String HOUSE = "([., ](?<house>[0-9]+[A-Za-z]?([.]?[0-9]+)?))";
    private final static String FLOOR= "([., ]*(?<floor>[0-9]+|stuen|stue|st){1})?";
    private final static String SIDE= "([., ]*(?<side>[a-zA-Z]{2}))?";
    private final static String ZIPCODE= "([., ]*(?<zipcode>[0-9]{4}))";
    private final static String ZIPCODE_INCOMPLETE= "([., ]*(?<zipcode>[0-9]{1,4}))";
    private final static String CITY = "([., ]*(?<city>[A-Za-zæøåÆØÅéÉ ]+))";

    /**
     * Used to combine different regular expressions
     * Uses a StringBuilder
     * @param args
     * @return
     */
    private static String combineRegex(String ... args){
        StringBuilder sb = new StringBuilder();
        for (String arg : args){
            sb.append(arg);
        }
        return sb.toString();
    }

    private final static String[] REGEXS = {
            combineRegex(STREET, HOUSE, FLOOR, SIDE, ZIPCODE, CITY),
            combineRegex(STREET, HOUSE, FLOOR,SIDE,CITY),
            combineRegex(STREET,HOUSE,FLOOR,SIDE,ZIPCODE),
            combineRegex(STREET, HOUSE, FLOOR, ZIPCODE, CITY),
            combineRegex(STREET, HOUSE, FLOOR, CITY),
            combineRegex(STREET, HOUSE, FLOOR, ZIPCODE),
            combineRegex(STREET, HOUSE, ZIPCODE, CITY),
            combineRegex(STREET, HOUSE, CITY),
            combineRegex(STREET, HOUSE, ZIPCODE)
    };

    private final static String[] ENDSWITHCITY = {
            combineRegex(STREET, HOUSE, FLOOR, SIDE, ZIPCODE, CITY),
            combineRegex(STREET, HOUSE, FLOOR,SIDE,CITY),
            combineRegex(STREET, HOUSE, FLOOR, ZIPCODE, CITY),
            combineRegex(STREET, HOUSE, FLOOR, CITY),
            combineRegex(STREET, HOUSE, ZIPCODE, CITY),
            combineRegex(STREET, HOUSE, CITY)
    };

    private final static String[] ENDSWITHZIPCODE = {
            combineRegex(STREET,HOUSE,FLOOR,SIDE,ZIPCODE_INCOMPLETE),
            combineRegex(STREET, HOUSE, FLOOR, ZIPCODE_INCOMPLETE),
            combineRegex(STREET, HOUSE, ZIPCODE_INCOMPLETE)
    };

    public static boolean matchesCityRegex(String input){
        for (Pattern pattern : CITYPATTERNS) {
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchesZipCodeRegex(String input){
        for (Pattern pattern : ZIPCODEPATTERNS) {
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }
    private final static Pattern[] ZIPCODEPATTERNS = Arrays.stream(ENDSWITHZIPCODE).map(Pattern::compile).toArray(Pattern[]::new);
    private final static Pattern[] CITYPATTERNS = Arrays.stream(ENDSWITHCITY).map(Pattern::compile).toArray(Pattern[]::new);
    private final static Pattern[] PATTERNS = Arrays.stream(REGEXS).map(Pattern::compile).toArray(Pattern[]::new);

    /**
     * When the group is matched, a consumer accepts a single
     * match by calling its accept method
     * @param consumer
     * @param matcher
     * @param group
     */
    private static void consumeIfMatchGroup(Consumer<String> consumer, Matcher matcher, String group) {
        try {
            String match = matcher.group(group);
            if (match != null) {
                consumer.accept(matcher.group(group));
            }
        } catch (IllegalArgumentException e) {
            // do nothing
        }
    }

    /**
     * Used to parse input.
     * Creates an new instance of Address, when match found.
     * @param input
     * @return
     */

    public static Address parse(String input) {
        Builder builder = new Builder();
        for (Pattern pattern : PATTERNS) {
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches()) {
                consumeIfMatchGroup(builder::street, matcher, "street");
                consumeIfMatchGroup(builder::house, matcher, "house");
                consumeIfMatchGroup(builder::floor, matcher, "floor");
                consumeIfMatchGroup(builder::side, matcher, "side");
                consumeIfMatchGroup(builder::zipcode, matcher, "zipcode");
                consumeIfMatchGroup(builder::city, matcher, "city");
                return builder.build();
            }
        }
        throw new IllegalArgumentException("Cannot parse Address: " + input);
    }
}
