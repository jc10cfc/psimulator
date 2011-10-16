package psimulator.userInterface.Editor.Dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import psimulator.userInterface.Editor.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.Components.EthInterface;

/**
 *
 * @author Martin
 */
public class CableConnectToInterfacePopupMenu extends JPopupMenu {

    private ButtonGroup interfaceGroup;
    private ItemHandler handler = new ItemHandler();
    
    private JComponent drawPanel;
    
    private JMenuItem items[];
    
    public CableConnectToInterfacePopupMenu(JComponent drawPanel) {
        this.drawPanel = drawPanel;
    }

    public void showPopupInterfaceChoose(AbstractHwComponent component, int x, int y) {
        // init data structures
        interfaceGroup = new ButtonGroup();
        this.removeAll();
        
        // create new array with length = number of interfaces
        items = new JMenuItem[component.getInterfaces().size()];
        
        int i = 0;
        
        // create menu items
        for(EthInterface ei : component.getInterfaces()){
            items[i] = new JMenuItem(ei.getName());
            this.add(items[ i]);
            interfaceGroup.add(items[ i]);
            items[i].addActionListener(handler);
            
            i++;
        }
        
        // show draw panel
        this.show(drawPanel, x, y);
    }

    private class ItemHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // determine which menu item was selected
            for (int i = 0; i < items.length; i++) {
                if (e.getSource() == items[ i]) {
                    System.out.println("Vybrana polozka :" + i);
                    return;
                }
            }
        }
    }
}
