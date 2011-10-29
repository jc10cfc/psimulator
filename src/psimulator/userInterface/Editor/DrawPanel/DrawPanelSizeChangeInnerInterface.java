package psimulator.userInterface.Editor.DrawPanel;

import java.awt.Dimension;

/**
 *
 * @author Martin
 */
public interface DrawPanelSizeChangeInnerInterface {
    
    /**
     * Updates size of panel according to parameter. If dimension is bigger than actual
     * size of drawPanel, than size is changed.
     * @param dimension of Graph
     */
    
    public void updateSize(Dimension dim);
    
}
