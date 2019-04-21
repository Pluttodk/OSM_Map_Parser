package Model.MapDrawStyle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * A marginally modified ArrayList to store instances Model.MapDrawStyle.MapDrawStyle
 */
public class MapDrawStyleList extends ArrayList<MapDrawStyle> implements Serializable {
    /**
     * Retrieve the Model.MapDrawStyle.MapDrawStyle that has a certain value in the field "id"
     * @param id The id we are looking for
     * @return The found Model.MapDrawStyle.MapDrawStyle
     * @throws NoSuchElementException If a Model.MapDrawStyle.MapDrawStyle was not found with that id
     */
    public MapDrawStyle get(String id) throws NoSuchElementException {
        for (MapDrawStyle mds : this) {
            if(mds.getId().equals(id)) return mds;
        }
        throw new NoSuchElementException();
    }
}
