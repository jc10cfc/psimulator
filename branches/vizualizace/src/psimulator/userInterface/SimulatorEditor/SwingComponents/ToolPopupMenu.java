package psimulator.userInterface.SimulatorEditor.SwingComponents;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.SimulatorEditor.Tools.AbstractCreationTool;
import psimulator.userInterface.SimulatorEditor.Tools.AbstractTool;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public class ToolPopupMenu extends JPopupMenu{
    
    private List<AbstractTool> tools;
    private List<JMenuItem> jMenuItems;
    private MenuToggleButton toggleButton;
    private ButtonGroup toolsButtonGroup;
    
    public ToolPopupMenu(List<AbstractTool> tools, final MenuToggleButton toggleButton){
        super();
        
        this.toggleButton = toggleButton;
        this.tools = tools;
     
        jMenuItems = new ArrayList<JMenuItem>();
        toolsButtonGroup = new ButtonGroup();
        
        for (final AbstractTool tool : tools) {
            //AddDeviceTool tmp = (AddDeviceTool)tool;
            AbstractCreationTool tmp = (AbstractCreationTool)tool;

            // create menu item for tool
            JMenuItem mi = new JMenuItem(/*tmp.getName() + tmp.getParameterLabel() + tmp.getParameter(), */
                    tmp.getImageIcon(AbstractImageFactory.ICON_SIZE_MENU_BAR_POPUP));
            
            jMenuItems.add(mi);
            
            mi.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    // set current tool to this tool
                    toggleButton.setCurrentTool(tool);
                }
            });
            this.add(mi);
            toolsButtonGroup.add(mi);
        }
  
    }
    
    @Override
    public void show(Component cmpnt, int x , int y){
        // set selected menu item
        //setCurrentToolSelected(toggleButton.getSelectedTool());
        
        // show
        super.show(cmpnt, x, y);
    }

    public void updateToolNames(DataLayerFacade dataLayer){
        for (int i=0;i<jMenuItems.size();i++) {
            jMenuItems.get(i).setText(tools.get(i).getToolTip(dataLayer));
        }
    }
    
}
