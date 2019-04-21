package View.MenuElements;

import Model.AutoFileReader;
import Model.Model;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * A directionsPanel that shows the directions to make
 */
public class DirectionsPanel extends JPanel {
    private int maxTextWidth, maxTextHeight;
    private double routeLength = 0.0;
    private String routeTime = "00:00:00";

    /**
     * A basic constructor for the directionspanel
     */
    public DirectionsPanel() {
        setBackground(new Color(255, 255, 255));
        setLayout(new FlowLayout(FlowLayout.LEFT));
    }

    /**
     * A enum of the various turns that this program supports
     */
    public enum Directions {
        LEFTTURN(Model.getString("direction-turn-left"), "resources/directionImages/direction_continue_left.png"),
        RIGHTTURN(Model.getString("direction-turn-right"), "resources/directionImages/direction_continue_right.png"),
        STRAIGHT(Model.getString("direction-straight"), "resources/directionImages/direction_continue.png"),
        UTURN(Model.getString("direction-uturn"), "resources/directionImages/direction_continue_uturn.png"),
        ARRIVEDLEFTTURN(Model.getString("direction-dest-on-left"), "resources/directionImages/direction_arrived_left.png"),
        ARRIVEDRIGHTTURN(Model.getString("direction-dest-on-right"), "resources/directionImages/direction_arrived_right.png"),
        ARRIVEDSTRAIGHT(Model.getString("direction-dest-in-front"), "resources/directionImages/direction_arrived_straight.png"),
        SHARPLEFT(Model.getString("direction-sharp-left"), "resources/directionImages/direction_continue_sharp_left.png"),
        SHARPRIGHT(Model.getString("direction-sharp-right"), "resources/directionImages/direction_continue_sharp_right.png"),
        SLIGHTLEFT(Model.getString("direction-slight-left"), "resources/directionImages/direction_continue_slight_left.png"),
        SLIGHTRIGHT(Model.getString("direction-slight-right"), "resources/directionImages/direction_continue_slight_right.png"),
        RIGHTATEND(Model.getString("direction-right-at-end"), "resources/directionImages/direction_end_of_road_left.png"),
        LEFTATEND(Model.getString("direction-left-at-end"), "resources/directionImages/direction_end_of_road_right.png"),
        OFFRAMPLEFT(Model.getString("direction-ramp-left"), "resources/directionImages/direction_off_ramp_slight_left.png"),
        OFFRAMPRIGHT(Model.getString("direction-ramp-right"), "resources/directionImages/direction_off_ramp_slight_right.png"),
        ROUNDABOUTSHARPRIGHT(Model.getString("direction-roundabout-sharp-right"),"resources/directionImages/direction_roundabout_sharp_right.png"),
        ROUNDABOUTRIGHT(Model.getString("direction-roundabout-right"),"resources/directionImages/direction_roundabout_right.png"),
        ROUNDABOUTSLIGHTRIGHT(Model.getString("direction-roundabout-light-right"), "resources/directionImages/direction_roundabout_slight_right.png"),
        ROUNDABOUTSTRAIGHT(Model.getString("direction-roundabout-straight"),"resources/directionImages/direction_roundabout_straight.png"),
        ROUNDABOUTSLIGHTLEFT(Model.getString("direction-roundabout-light-left"),"resources/directionImages/direction_roundabout_slight_left.png"),
        ROUNDABOUTLEFT(Model.getString("direction-roundabout-left"),"resources/directionImages/direction_roundabout_left.png"),
        ROUNDABOUTSHARPLEFT(Model.getString("direction-roundabout-sharp-left"), "resources/directionImages/direction_roundabout_sharp_left.png");

        private String directionToGo;
        private String imgUrl;

        /**
         * a constuctor for the enums
         * @param directionToGo a string of which way to go
         * @param imgUrl a path to the image
         */
        Directions(String directionToGo, String imgUrl) {
            this.directionToGo = directionToGo;
            this.imgUrl = imgUrl;
        }

        /**
         * A method that updates the text value (for support of multiple language)
         */
        public static void updateDirections(){
            LEFTTURN.directionToGo = Model.getString("direction-turn-left");
            RIGHTTURN.directionToGo = Model.getString("direction-turn-right");
            STRAIGHT.directionToGo = Model.getString("direction-straight");
            UTURN.directionToGo = Model.getString("direction-uturn");
            ARRIVEDLEFTTURN.directionToGo = Model.getString("direction-dest-on-left");
            ARRIVEDRIGHTTURN.directionToGo = Model.getString("direction-dest-on-right");
            ARRIVEDSTRAIGHT.directionToGo = Model.getString("direction-dest-in-front");
            SHARPLEFT.directionToGo = Model.getString("direction-sharp-left");
            SHARPRIGHT.directionToGo = Model.getString("direction-sharp-right");
            RIGHTATEND.directionToGo = Model.getString("direction-right-at-end");
            LEFTATEND.directionToGo = Model.getString("direction-left-at-end");
            OFFRAMPLEFT.directionToGo = Model.getString("direction-ramp-left");
            OFFRAMPRIGHT.directionToGo = Model.getString("direction-ramp-right");
            ROUNDABOUTSHARPRIGHT.directionToGo = Model.getString("direction-roundabout-sharp-right");
            ROUNDABOUTRIGHT.directionToGo = Model.getString("direction-roundabout-right");
            ROUNDABOUTSLIGHTRIGHT.directionToGo = Model.getString("direction-roundabout-light-right");
            ROUNDABOUTSTRAIGHT.directionToGo = Model.getString("direction-roundabout-straight");
            ROUNDABOUTSLIGHTLEFT.directionToGo = Model.getString("direction-roundabout-light-left");
            ROUNDABOUTLEFT.directionToGo = Model.getString("direction-roundabout-left");
            ROUNDABOUTSHARPLEFT.directionToGo = Model.getString("direction-roundabout-sharp-left");

        }
    }

    /**
     * A method to add a turn to the directions panel
     * @param distance the distance to next turn
     * @param road which road to turn on
     * @param direction which direction to turn
     */
    private void addDirection(float distance, String road, Directions direction) {
        String unit;
        String output;
        if(distance < 1) {
            unit = new DecimalFormat("####.#").format(distance*1000) + " " + Model.getString("meter");
        } else {
            unit = new DecimalFormat("####.#").format(distance) + " " + Model.getString("kilometer");
        }
        road = road == null ? Model.getString("unknown-road") : road;
        output = Model.getString("in")+" " + unit + Model.getString("making-turn")+ " " + direction.directionToGo + Model.getString("of") + road;
        JLabel text  = new JLabel(output);
        text.setFont(new Font("Verdana", Font.ITALIC, 12));
        ImageIcon image = null;
        try {
            image = resizeImage(new AutoFileReader.ImageReader(direction.imgUrl).getImageIcon());
        } catch (IOException e) {
            //Do nothing
        }
        text.setIcon(image);
        checkJLabelSize(text);
        add(text);
    }

    /**
     * Checks if the width of the label is the widest in the directions panel
     * @param label a label to check
     */
    private void checkJLabelSize(JLabel label) {
        if(label.getPreferredSize().getWidth() > maxTextWidth) maxTextWidth = (int) label.getPreferredSize().getWidth();
        maxTextHeight += label.getPreferredSize().height+5;
    }

    int getMaxTextHeight() {
        return maxTextHeight;
    }

    int getMaxTextWidth() {
        return maxTextWidth;
    }

    /**
     * A method that resize an image icon
     * @param imageIcon image icon to be resized
     * @return the resized image
     */
    private ImageIcon resizeImage(ImageIcon imageIcon) {
        Image image = imageIcon.getImage();
        Image newImage = image.getScaledInstance(20, 20,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        return new ImageIcon(newImage);
    }

    public double getRouteLength() {
        return routeLength;
    }

    public String getRouteTime() {
        return routeTime;
    }

    public void setRouteLength(double routeLength) {this.routeLength = routeLength;}

    public void setRouteTime(String routeTime) {this.routeTime = routeTime;}

    /**
     * A method that checks which turn to make based on an angle
     * @param angle of the next turn
     * @param roadLength length until turn
     * @param name of a road
     * @param isRoundabout boolean if it is a roundabout
     */
    public void addDirectionBasedOnAngle(double angle, float roadLength, String name, boolean isRoundabout) {
        if (isRoundabout) {
            if (angle <= 123.75) {
                addDirection(roadLength, name, Directions.ROUNDABOUTSHARPLEFT);
            } else if (angle <= 146.25) {
                addDirection(roadLength, name, Directions.ROUNDABOUTLEFT);
            } else if (angle <= 168.75) {
                addDirection(roadLength, name, Directions.ROUNDABOUTSLIGHTLEFT);
            } else if (angle <= 191.25) {
                addDirection(roadLength, name, Directions.ROUNDABOUTSTRAIGHT);
            } else if (angle <= 213.75) {
                addDirection(roadLength, name, Directions.ROUNDABOUTSLIGHTRIGHT);
            } else if (angle <= 236.25) {
                addDirection(roadLength, name, Directions.ROUNDABOUTRIGHT);
            } else {
                addDirection(roadLength, name, Directions.ROUNDABOUTSHARPRIGHT);
            }
            return;
        }
        if(roadLength > 0.01) {
            if (angle <= 50) {
                addDirection(roadLength, name, Directions.LEFTTURN);
            } else if (angle <= 130) {
                addDirection(roadLength, name, Directions.LEFTTURN);
            } else if (angle <= 170) {
                addDirection(roadLength, name, Directions.SLIGHTLEFT);
            } else if (angle <= 190) {
                addDirection(roadLength, name, Directions.STRAIGHT);
            } else if (angle <= 230) {
                addDirection(roadLength, name, Directions.SLIGHTRIGHT);
            } else if (angle <= 310) {
                addDirection(roadLength, name, Directions.RIGHTTURN);
            } else {
                addDirection(roadLength, name, Directions.SHARPRIGHT);
            }
        }
    }

}