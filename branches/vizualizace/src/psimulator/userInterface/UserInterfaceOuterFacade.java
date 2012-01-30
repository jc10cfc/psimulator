package psimulator.userInterface;

import psimulator.logicLayer.ControllerFacade;

/**
 *
 * @author Martin
 */
public interface UserInterfaceOuterFacade {
    /**
     * inits view and calls setVisible(true)
     * @param controller 
     */
    public void initView(ControllerFacade controller);
    
        
    
    public GlassPanelPainter getGlassPanelPainter();

}
