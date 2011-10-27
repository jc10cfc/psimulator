package psimulator.userInterface.Editor.DrawPanel.SwingComponents;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.Editor.DrawPanel.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.DrawPanel.DrawPanelInnerInterface;
import psimulator.userInterface.Editor.DrawPanel.Enums.ComponentAction;

/**
 *
 * @author Martin
 */
public class PopupMenuAbstractHwComponent extends JPopupMenu{
    
    private DataLayerFacade dataLayer;
    private DrawPanelInnerInterface drawPanel;
    
    private AbstractHwComponent hwComponent;
    
    private JMenuItem jItemAlignToGrid;
    private JMenuItem jItemComponentProperties;
    private JMenuItem jItemDeleteComponent;
    
    public PopupMenuAbstractHwComponent(AbstractHwComponent hwComponent, DrawPanelInnerInterface drawPanel, DataLayerFacade dataLayer){
        this.dataLayer = dataLayer;
        this.drawPanel = drawPanel;
        this.hwComponent = hwComponent;
        
        jItemAlignToGrid = new JMenuItem(dataLayer.getString("ALIGN_TO_GRID"));
        jItemComponentProperties = new JMenuItem(dataLayer.getString("PROPERTIES"));
        jItemDeleteComponent = new JMenuItem(dataLayer.getString("DELETE"));
        
        jItemDeleteComponent.addActionListener(drawPanel.getAbstractAction(ComponentAction.DELETE));
        
        
        this.add(jItemComponentProperties);
        this.addSeparator();
        this.add(jItemAlignToGrid);
        this.add(jItemDeleteComponent);
       
    }
    

    public void show(DrawPanelInnerInterface drawPanel, int x, int y){
        super.show((JComponent)drawPanel, x, y);
    }
}
