package psimulator.dataLayer.Enums;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public enum ObserverUpdateEventType {
    GRAPH_SIZE_CHANGED,         // when Graph size is changed
    GRAPH_COMPONENT_CHANGED,    //
    
    VIEW_DETAILS,               // when view details changed
    LANGUAGE,                   // when language changed
    ICON_SIZE,                  // when icon size changed
    ZOOM_CHANGE,                // when zoom changed
    UNDO_REDO,                  // when undo/redo
    PACKET_IMAGE_TYPE_CHANGE,   // when packet icon changed
    
    SIMULATOR_PLAYER_LIST_MOVE, // when manually changed current event(NEXT, PREV, FIRST, LAST or DOUBLE CLICK on some event)
    SIMULATOR_PLAYER_PLAY,      // when playing starts
    SIMULATOR_PLAYER_STOP,      // when playing stops (maunal, or hitting end of list = automatic)
    SIMULATOR_PLAYER_NEXT,      // automatic move to next event when playing
    SIMULATOR_SPEED,            // when speed is changed
    SIMULATOR_RECORDER,         // when recorder is turned on / off
    SIMULATOR_CONNECTION,       // when connected / disconnected
    SIMULATOR_REALTIME,         // when realtime enabled / disabled
    SIMULATOR_NEW_PACKET,       // when new packet recieved
    SIMULATOR_DETAILS;      
    
}
