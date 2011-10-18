package psimulator.userInterface.Editor.Dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuListener;
import psimulator.userInterface.Editor.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.Components.EthInterface;
import psimulator.userInterface.MouseActionListeners.ChooseEthInterfaceInterface;

/**
 *
 * @author Martin
 */
public class CableConnectToInterfacePopupMenu extends JPopupMenu {

    // graphical components
    private ButtonGroup interfaceGroup;
    private ItemHandler handler = new ItemHandler();
    private JComponent drawPanel;
    private JMenuItem items[];
    // END graphical components
    
    private AbstractHwComponent component;
    private ChooseEthInterfaceInterface chooseEthInterfaceInterface;
   
    public CableConnectToInterfacePopupMenu(JComponent drawPanel, PopupMenuListener popupMenuListener, ChooseEthInterfaceInterface chooseEthInterfaceInterface) {
        this.drawPanel = drawPanel;
        this.addPopupMenuListener(popupMenuListener);
        this.chooseEthInterfaceInterface = chooseEthInterfaceInterface;
    }

    /**
     * Shows JPopupMenu for AbstractHwComponent component at x and y coordinates. Parent is a drawPanel
     * @param component
     * @param x Coordinate
     * @param y Coordinate
     */
    public void showPopupInterfaceChoose(AbstractHwComponent component, int x, int y) {
        // init data structures
        interfaceGroup = new ButtonGroup();
        this.removeAll();
        this.component = component;
        
        // create new array with length = number of interfaces
        items = new JMenuItem[component.getInterfaces().size()];
        
        int i = 0;
        
        // create menu items
        for(EthInterface ei : component.getInterfaces()){
            // new menu item
            items[i] = new JMenuItem(ei.getName());
            // if EthInterface in use, marked as disabled
            if(ei.hasCable()){
                items[i].setEnabled(false);
            }
            // add menu item to PopupMenu
            this.add(items[ i]);
            // add menu item to interface group
            interfaceGroup.add(items[ i]);
            // add action listener to item
            items[i].addActionListener(handler);
            // increase counter
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
                if (e.getSource() == items[i]) {
                    // set chosen interface in ChooseEthInterfaceInterface
                    chooseEthInterfaceInterface.setChosenInterface(component.getInterfaces().get(i));
                    return;
                }
            }
        }
    }

}
