package Model.Graph;

import Model.Model;
/**
 * A enum of the various traveltypes
 */
public enum TravelType {
    WALK (Model.getString("walk"), 5),
    CAR(Model.getString("car"), 130),
    BICYCLE(Model.getString("bike"), 20);


    private String name;
    private int maxSpeed;

    /**
     * A constructor for a traveltype
     * @param name the name to be displayed in gui
     * @param maxSpeed the average max speed of that transportation method
     */
    TravelType(String name, int maxSpeed) {
        this.name = name;
        this.maxSpeed = maxSpeed;
    }

    /**
     * A method that updates the name
     */
    public static void updateName(){
        WALK.name = Model.getString("walk");
        CAR.name = Model.getString("car");
        BICYCLE.name = Model.getString("bike");
    }

    /**
     * A method that returns all the names of the traveltypes
     * @return an array of strings
     */
    public static String[] getName() {
        String[] names = new String[TravelType.values().length];
        int i = 0;
        for(TravelType type: TravelType.values()) {
            names[i++] = type.name;
        }
        return names;
    }

    public int getSpeed() {
        return maxSpeed;
    }
    @Override
    public String toString() {
        return name;
    }
    public int getMaxSpeed() {return maxSpeed;}
}
