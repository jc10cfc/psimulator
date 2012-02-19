package psimulator.userInterface.SimulatorEditor;

import javax.swing.JScrollPane;
import javax.swing.JViewport;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public interface UserInterfaceMainPanelInnerInterface {
    /**
     * Sets default tool in EditorPanels toolBar
     */
    public void doSetDefaultToolInToolBar();
    
    public JScrollPane getJScrollPane();
    
    public JViewport getJViewport();
    
    public void updateSize();
}
