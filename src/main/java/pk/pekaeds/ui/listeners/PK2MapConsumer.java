package pk.pekaeds.ui.listeners;

import pk.pekaeds.pk2.map.PK2Map;

/**
 * Classes should implement this interface if they need to keep a reference to the currently loaded map file.
 *<p></p>
 * setMap(PK2Map map) will be called any time the map gets updated through PekaEDSGUI.
 *<p></p>
 * Updating the reference to the current map should only be done through PekaEDSGUI, because it keeps a record of every registered PK2MapConsumer and updates them any time the reference changes.
 */
public interface PK2MapConsumer {
    void setMap(PK2Map map);
}
