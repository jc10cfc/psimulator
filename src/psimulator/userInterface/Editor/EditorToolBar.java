package psimulator.userInterface.Editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.ColorMixerSignleton;
import psimulator.userInterface.Editor.Enums.Tools;
import psimulator.userInterface.Editor.SwingComponents.MenuToggleButton;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public class EditorToolBar extends JToolBar implements Observer {

    private DataLayerFacade dataLayer;
    private AbstractImageFactory imageFactory;
    private JButton jButtonPc;
    private JButton jButtonMac;
    private JButton jButtonHand;
    private JButton jButtonCable;
    private JButton jButtonFitToSize;
    private JButton selectedButton = null;

    
    private ButtonGroup toolsButtonGroup;
    
    private MenuToggleButton toggleButtonHand;
    
    private MenuToggleButton toggleButtonRouters;
    private MenuToggleButton toggleButtonSwitches;
    private MenuToggleButton toggleButtonEndDevices;
    private MenuToggleButton toggleButtonRealPC;
    private MenuToggleButton toggleButtonCable;

    public EditorToolBar(DataLayerFacade dataLayer, AbstractImageFactory imageFactory) {
        super();
        this.dataLayer = dataLayer;
        this.imageFactory = imageFactory;

        // add this ToolBar as observer to languageManager
        dataLayer.addLanguageObserver(this);

        // tool bar is not possible to move
        this.setFloatable(false);

        // set orientation to vertical
        this.setOrientation(VERTICAL);

        this.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        this.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        /*
        jButtonHand = new JButton();
        jButtonHand.setIcon(imageFactory.getImageIconForToolbar(Tools.HAND));
        jButtonHand.setActionCommand(Tools.HAND.toString());

        jButtonPc = new JButton();
        jButtonPc.setIcon(imageFactory.getImageIconForToolbar(Tools.PC));
        jButtonPc.setActionCommand(Tools.PC.toString());

        jButtonMac = new JButton();
        jButtonMac.setIcon(imageFactory.getImageIconForToolbar(Tools.MAC));
        jButtonMac.setActionCommand(Tools.MAC.toString());

        jButtonCable = new JButton();
        jButtonCable.setIcon(imageFactory.getImageIconForToolbar(Tools.CABLE));
        jButtonCable.setActionCommand(Tools.CABLE.toString());

        jButtonFitToSize = new JButton();
        jButtonFitToSize.setText("Fit to size");
        jButtonFitToSize.setActionCommand(Tools.FIT_TO_SIZE.toString());
        
        
        this.add(jButtonHand);
        this.add(jButtonPc);
        this.add(jButtonMac);
        this.addSeparator();
        this.add(jButtonCable);
        this.addSeparator();
        this.add(jButtonFitToSize);
        this.addSeparator();
        */

        /// new code
        jButtonFitToSize = new JButton();
        jButtonFitToSize.setText("Fit to size");
        jButtonFitToSize.setActionCommand(Tools.FIT_TO_SIZE.toString());
        
        
        toolsButtonGroup = new ButtonGroup();
        
        toggleButtonHand = new MenuToggleButton(imageFactory.getImageIconForToolbar(Tools.HAND), "hand");
        toggleButtonRouters = new MenuToggleButton(null, imageFactory.getImageIconForToolbar(Tools.HAND));
        toggleButtonSwitches = new MenuToggleButton(null,  imageFactory.getImageIconForToolbar(Tools.HAND));
        toggleButtonEndDevices = new MenuToggleButton(null,  imageFactory.getImageIconForToolbar(Tools.PC));
        toggleButtonRealPC = new MenuToggleButton(imageFactory.getImageIconForToolbar(Tools.REAL_PC), "real PC");
        toggleButtonCable = new MenuToggleButton(imageFactory.getImageIconForToolbar(Tools.CABLE), "cable");
        
        toolsButtonGroup.add(toggleButtonHand);
        toolsButtonGroup.add(toggleButtonRouters);
        toolsButtonGroup.add(toggleButtonSwitches);
        toolsButtonGroup.add(toggleButtonEndDevices);
        toolsButtonGroup.add(toggleButtonRealPC);
        toolsButtonGroup.add(toggleButtonCable);
 
        
        this.add(toggleButtonHand);
        this.addSeparator();
        this.add(toggleButtonRouters);
        this.add(toggleButtonSwitches);
        this.add(toggleButtonEndDevices);
        this.add(toggleButtonRealPC);
        this.addSeparator();
        this.add(toggleButtonCable);
        this.addSeparator();
        this.add(jButtonFitToSize);
        
        
        
        /*
        JMenuItem item1 = new JMenuItem(imageFactory.getImageIconForToolbar(Tools.HAND));
        JMenuItem item2 = new JMenuItem(imageFactory.getImageIconForToolbar(Tools.MAC));
        
        JMenuItem [] items = new JMenuItem[2];
        items[0] = item1;
        items[1] = item2;
        
        MenuToggleButton toggleButton = new MenuToggleButton(items, "routers",imageFactory.getImageIconForToolbar(Tools.MAC));
        
        this.add(toggleButton);
        
        
        MenuToggleButton toggleButton2 = new MenuToggleButton(null, "routers",imageFactory.getImageIconForToolbar(Tools.PC));
        this.add(toggleButton2);
        
        ButtonGroup bg = new ButtonGroup();
        bg.add(toggleButton);
        bg.add(toggleButton2);
        */
        //end new code

        setTextsToComponents();

        // apply background color
        this.setBackground(ColorMixerSignleton.editToolbarColor);
        Component[] comp = this.getComponents();
        for (Component c : comp) {
            c.setBackground(ColorMixerSignleton.editToolbarColor);
        }

    }

    // reaction to update from LanguageManager
    @Override
    public void update(Observable o, Object o1) {
        setTextsToComponents();
    }

    /**
     * Set JButton in parameter as the only one selected in ToolBar
     * @param button 
     */
    public final void setSelectedButton(JButton button) {
        // if there is selected button
        if (selectedButton != null) {
            // set selected to false
            selectedButton.setSelected(false);
        }
        selectedButton = button;
        button.setSelected(true);
    }

    /**
     * Adds action listener to all buttons in tool bar
     * @param listener Action listener
     */
    public void addToolActionListener(ActionListener listener) {
        /*
        jButtonHand.addActionListener(listener);
        jButtonPc.addActionListener(listener);
        jButtonMac.addActionListener(listener);
        jButtonCable.addActionListener(listener);
        */
    }

    /**
     * adds action listener to jButtonFitToSize
     * @param listener 
     */
    public void addToolActionFitToSizeListener(ActionListener listener) {
        jButtonFitToSize.addActionListener(listener);
    }

    ////////------------ PRIVATE------------///////////
    private void setTextsToComponents() {
        /*
        jButtonHand.setToolTipText(dataLayer.getString("HAND"));
        jButtonMac.setToolTipText(dataLayer.getString("MAC"));
        jButtonPc.setToolTipText(dataLayer.getString("PC"));
        jButtonCable.setToolTipText(dataLayer.getString("CABLE"));
        jButtonFitToSize.setToolTipText(dataLayer.getString("FIT_TO_SIZE"));
        */
    }
}
