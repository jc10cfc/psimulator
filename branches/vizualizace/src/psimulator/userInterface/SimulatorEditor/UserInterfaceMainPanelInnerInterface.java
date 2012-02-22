package psimulator.userInterface.SimulatorEditor;

import javax.swing.JScrollPane;
import javax.swing.JViewport;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.MainTool;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public interface UserInterfaceMainPanelInnerInterface {

    public void doSetToolInToolBar(MainTool mainTool);
    
    public JScrollPane getJScrollPane();
    
    public JViewport getJViewport();
}
