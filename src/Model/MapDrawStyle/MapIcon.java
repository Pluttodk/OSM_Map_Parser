package Model.MapDrawStyle;

/**
 * The different icons to choose from when drawing a poi element
 */
public enum MapIcon {
    NONE(""),
    STANDARD("res/icons/standard.png"),
    BUS_STOP("res/icons/bus_stop.png"),
    PARKING("res/icons/parking.png"),
    BICYCLE_PARKING("res/icons/bicycle_parking.png"),
    RESTAURANT("res/icons/restaurant.png"),
    FAST_FOOD("res/icons/fast_food.png"),
    TRAIN("res/icons/train.png"),
    GAS("res/icons/gas.png"),
    TOILET("res/icons/toilet.png"),
    AIRPORT("res/icons/airport.png"),
    MOSQUE("res/icons/mosque.png"),
    CHURCH("res/icons/church.png"),
    MONUMENT("res/icons/monument.png"),
    METRO("res/icons/metro.png"),
    SUPERMARKET("res/icons/supermarket.png"),
    CAFE("res/icons/cafe.png"),
    CEMENTARY("res/icons/cementery.png"),
    TAXI("res/icons/taxi.png");

    private final String path;

    MapIcon(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
