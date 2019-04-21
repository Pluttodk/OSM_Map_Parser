package Model;

import java.awt.geom.Point2D;

/**
 * Created by Matt on 03-05-2017.
 */
public class EuclideanDistance {
    private static int R = 6371; //km, earths radius

    public static double dist(Model model, Point2D p, Point2D q) {
        double lon1 = p.getX();
        double lat1 = p.getY();
        double lon2 = q.getX();
        double lat2 = q.getY();

        //haven't test these two lines' effect on the code
        lon1 /= model.getLonfactor();
        lon2 /= model.getLonfactor();

        //source: http://www.movable-type.co.uk/scripts/latlong.html
        double φ1 = Math.toRadians(lat1);
        double φ2 = Math.toRadians(lat2);
        double Δφ = Math.toRadians(lat2-lat1);
        double Δλ = Math.toRadians(lon2-lon1);

        double a = Math.sin(Δφ/2) * Math.sin(Δφ/2) +
                Math.cos(φ1) * Math.cos(φ2) *
                        Math.sin(Δλ/2) * Math.sin(Δλ/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return R * c;
    }
}
