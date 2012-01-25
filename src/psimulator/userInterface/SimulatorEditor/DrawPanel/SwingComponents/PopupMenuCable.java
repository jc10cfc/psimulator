package psimulator.userInterface.SimulatorEditor.DrawPanel.SwingComponents;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanelInnerInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.DrawPanelAction;

/**
 *
 * @author Martin
 */
public class PopupMenuCable extends JPopupMenu{
    
    private DataLayerFacade dataLayer;
    private DrawPanelInnerInterface drawPanel;
    
    private JMenuItem jItemDeleteCable;
    
    public PopupMenuCable(DrawPanelInnerInterface drawPanel, DataLayerFacade dataLayer, int cables){
        this.dataLayer = dataLayer;
        this.drawPanel = drawPanel;
        
        jItemDeleteCable = new JMenuItem();
        if(cables == 1){
            jItemDeleteCable.setText(dataLayer.getString("DELETE_CABLE"));
        }else{
            jItemDeleteCable.setText(dataLayer.getString("DELETE_CABLES"));
        }
        
        jItemDeleteCable.addActionListener(drawPanel.getAbstractAction(DrawPanelAction.DELETE));
        
        // add buttons for operations 
        this.add(jItemDeleteCable);
     
    }
    

    public void show(DrawPanelInnerInterface drawPanel, int x, int y){
        super.show((JComponent)drawPanel, x, y);
    }
}
