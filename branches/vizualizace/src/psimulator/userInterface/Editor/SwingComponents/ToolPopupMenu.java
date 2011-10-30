package psimulator.userInterface.Editor.SwingComponents;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import psimulator.userInterface.Editor.Tools.AbstractTool;
import psimulator.userInterface.Editor.Tools.AddDeviceTool;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public class ToolPopupMenu extends JPopupMenu{
    
    private List<AbstractTool> tools;
    private MenuToggleButton toggleButton;
    private ButtonGroup toolsButtonGroup;
    
    public ToolPopupMenu(List<AbstractTool> tools, final MenuToggleButton toggleButton){
        super();
        
        this.toggleButton = toggleButton;
        this.tools = tools;
     
        toolsButtonGroup = new ButtonGroup();
        
        for (final AbstractTool tool : tools) {
            AddDeviceTool tmp = (AddDeviceTool)tool;
            // create menu item for tool
            JMenuItem mi = new JMenuItem(tmp.getName()+" - Interfaces: "+tmp.getInterfaces(), 
                    tmp.getImageIcon(AbstractImageFactory.ICON_SIZE_MENU_BAR_POPUP));
            
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

    
}





/*
        popup.addPopupMenuListener(new PopupMenuListener() {
        
        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
        }
        
        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        }
        
        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        //setSelected(false);
        }
        });*/
