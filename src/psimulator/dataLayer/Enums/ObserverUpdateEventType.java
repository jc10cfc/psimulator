package psimulator.dataLayer.Enums;

/**
 *
 * @author Martin
 */
public enum ObserverUpdateEventType {
    LANGUAGE,       // when language changed
    ICON_SIZE,      // when icon size changed
    
    SIMULATOR_PLAYER_LIST_MOVE, // when manually changed current event(NEXT, PREV, FIRST, LAST or DOUBLE CLICK on some event)
    SIMULATOR_PLAYER_PLAY,      // when playing starts
    SIMULATOR_PLAYER_STOP,      // when playing stops (maunal, or hitting end of list = automatic)
    SIMULATOR_PLAYER_NEXT,      // automatic move to next event when playing
    SIMULATOR_SPEED,            // when speed is changed
    SIMULATOR_RECORDER,         // when recorder is turned on / off
    SIMULATOR_CONNECTION,       // when connected / disconnected
    SIMULATOR_REALTIME,         // when realtime enabled / disabled
    SIMULATOR_DETAILS;          
    
}
