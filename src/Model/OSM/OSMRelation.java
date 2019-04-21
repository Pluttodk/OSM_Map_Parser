package Model.OSM;

import Model.KDTree.MultiPolygonApprox;
import Model.Model;

import java.awt.geom.Point2D;
import java.util.List;

/**
 * Created by Matt on 09-05-2017.
 */
public class OSMRelation extends MultiPolygonApprox {
    public OSMRelation(List<? extends List<? extends Point2D>> rel) {
        super(rel);
    }
}
