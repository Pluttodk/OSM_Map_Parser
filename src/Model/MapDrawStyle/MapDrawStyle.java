package Model.MapDrawStyle;

import Model.OSM.WayType;

import java.io.Serializable;
import java.util.EnumMap;

/**
 * Defines how the whole map should be drawn
 */
public class MapDrawStyle implements Serializable {
    private EnumMap<WayType, DrawStyle> drawStyles;
    private String id;
    private String displayName;

    /**
     * Initiates the class
     * @param drawStyles An EnumMap of all elements that should be drawn their corresponding Model.MapDrawStyle.DrawStyle (how they should be drawn)
     * @param id The id of this Model.MapDrawStyle.MapDrawStyle (how we find it later in a big pile)
     * @param displayName The name to be displayed via the user interface
     */
    public MapDrawStyle(EnumMap<WayType, DrawStyle> drawStyles, String id, String displayName) {
        this.drawStyles = drawStyles;
        this.id = id;
        this.displayName = displayName;
    }

    public EnumMap<WayType, DrawStyle> getDrawStyles() {
        return drawStyles;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
