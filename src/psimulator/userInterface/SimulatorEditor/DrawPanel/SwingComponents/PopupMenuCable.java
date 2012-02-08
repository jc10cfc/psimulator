package psimulator.userInterface.SimulatorEditor.DrawPanel.SwingComponents;

import javax.swing.ImageIcon;
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
    
    private JMenuItem jItemCableProperties;
    private JMenuItem jItemDeleteCable;
    
    public PopupMenuCable(DrawPanelInnerInterface drawPanel, DataLayerFacade dataLayer, int cables){
        this.dataLayer = dataLayer;
        this.drawPanel = drawPanel;
        
        jItemCableProperties = new JMenuItem(dataLayer.getString("PROPERTIES"));
        jItemDeleteCable = new JMenuItem();
        
        if(cables == 1){
            this.add(jItemCableProperties);
            jItemDeleteCable.setText(dataLayer.getString("DELETE_CABLE"));
        }else{
            jItemDeleteCable.setText(dataLayer.getString("DELETE_CABLES"));
        }
        
        jItemCableProperties.addActionListener(drawPanel.getAbstractAction(DrawPanelAction.PROPERTIES));
        jItemDeleteCable.addActionListener(drawPanel.getAbstractAction(DrawPanelAction.DELETE));
        
        
        // add icons 
        jItemCableProperties.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/configure.png")));
        jItemDeleteCable.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/button_cancel.png")));
        
        // add buttons for operations 
        this.add(jItemDeleteCable);
     
    }
    

    public void show(DrawPanelInnerInterface drawPanel, int x, int y){
        super.show((JComponent)drawPanel, x, y);
    }
}
