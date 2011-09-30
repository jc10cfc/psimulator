package psimulator.userInterface;

import psimulator.logicLayer.ControllerFacade;

/**
 *
 * @author Martin
 */
public interface UserInterfaceFacade {
    // used by controller:
    public void initView(ControllerFacade controller);
}
