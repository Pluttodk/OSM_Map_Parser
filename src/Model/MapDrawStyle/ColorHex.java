package Model.MapDrawStyle;

import Model.Exceptions.IllegalStringFormatException;
import Model.Model;

import java.awt.*;
import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * A class to store a color hex-value
 */
public class ColorHex implements Serializable {
    private String hex;
    private static final Pattern HEX_PATTERN = Pattern.compile("(#)?([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})");

    /**
     * Create a hex-value
     * @param hex A string of 6 (7 if starting with #) with chars 0-9 and A-F/a-f
     */
    public ColorHex(String hex) {
        if (hex==null || !isWellFormatted(hex)) throw new IllegalStringFormatException();
        if (hex.startsWith("#")) hex = hex.substring(1);
        this.hex = hex;
    }

    @Override
    public String toString() {
        return hex;
    }

    /**
     * Returning hex value
     * @return Hex value as a string without #
     */
    public String getHex() {
        return hex;
    }

    /**
     * Change the stored hex value
     * @param hex A string of 6 (7 if starting with #) with chars 0-9 and A-F/a-f
     */
    public void setHex(String hex) {
        if (!isWellFormatted(hex)) throw new IllegalStringFormatException();
        if (hex.startsWith("#")) hex = hex.substring(1);
        this.hex = hex;
    }

    private boolean isWellFormatted(String hex) {
        return HEX_PATTERN.matcher(hex).matches();
    }

    /**
     * Convert the hex value to an awt.Color instance
     * @return The corresponding awt.Color instance
     */
    public Color getColor() {
        return Color.decode("#"+hex);
    }
}
