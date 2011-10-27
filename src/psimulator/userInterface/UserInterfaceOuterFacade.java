package psimulator.userInterface;

import psimulator.logicLayer.ControllerFacade;

/**
 *
 * @author Martin
 */
public interface UserInterfaceOuterFacade {
    // used by controller:
    public void initView(ControllerFacade controller);
}
