package psimulator.userInterface.Editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JToolBar;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.ColorMixerSignleton;
import psimulator.userInterface.Editor.Enums.Tools;
import psimulator.userInterface.Editor.SwingComponents.MenuToggleButton;
import psimulator.userInterface.Editor.Tools.ToolsFactory;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public class EditorToolBar extends JToolBar implements Observer {

    private DataLayerFacade dataLayer;
    private AbstractImageFactory imageFactory;
    
    private JButton jButtonFitToSize;
    
    private ButtonGroup toolsButtonGroup;
    
    private MenuToggleButton toggleButtonHand;
    
    private MenuToggleButton toggleButtonRouters;
    private MenuToggleButton toggleButtonSwitches;
    private MenuToggleButton toggleButtonEndDevices;
    private MenuToggleButton toggleButtonRealPC;
    private MenuToggleButton toggleButtonCable;

    public EditorToolBar(DataLayerFacade dataLayer, AbstractImageFactory imageFactory, ToolChangeInterface toolChangeInterface) {
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

        /// new code
        jButtonFitToSize = new JButton();
        jButtonFitToSize.setText("Fit to size");
        jButtonFitToSize.setActionCommand(Tools.FIT_TO_SIZE.toString());
        
        
        toolsButtonGroup = new ButtonGroup();
        
        toggleButtonHand = new MenuToggleButton(ToolsFactory.getTools(Tools.HAND, imageFactory), toolChangeInterface);
        toggleButtonRouters = new MenuToggleButton(ToolsFactory.getTools(Tools.ADD_ROUTER, imageFactory), toolChangeInterface);
        toggleButtonSwitches = new MenuToggleButton(ToolsFactory.getTools(Tools.ADD_SWITCH, imageFactory), toolChangeInterface);
        toggleButtonEndDevices = new MenuToggleButton(ToolsFactory.getTools(Tools.ADD_END_DEVICE, imageFactory), toolChangeInterface);
        toggleButtonRealPC = new MenuToggleButton(ToolsFactory.getTools(Tools.ADD_REAL_PC, imageFactory), toolChangeInterface);
        toggleButtonCable = new MenuToggleButton(ToolsFactory.getTools(Tools.ADD_CABLE, imageFactory), toolChangeInterface);
        //toggleButtonCable = new MenuToggleButton(null, imageFactory.getImageIconForToolbar(Tools.ADD_CABLE));
        
        
        toolsButtonGroup.add(toggleButtonHand);
        toolsButtonGroup.add(toggleButtonEndDevices);
        toolsButtonGroup.add(toggleButtonRouters);
        toolsButtonGroup.add(toggleButtonSwitches);
        toolsButtonGroup.add(toggleButtonRealPC);
        toolsButtonGroup.add(toggleButtonCable);
 
        
        this.add(toggleButtonHand);
        this.addSeparator();
        this.add(toggleButtonEndDevices);
        this.add(toggleButtonRouters);
        this.add(toggleButtonSwitches);
        this.add(toggleButtonRealPC);
        this.addSeparator();
        this.add(toggleButtonCable);
        this.addSeparator();
        this.add(jButtonFitToSize);
        
        
        
        //end new code

        //toggleButtonHand.setSelected(true);
        
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
        
        toggleButtonHand.setToolTipText(dataLayer.getString("HAND"));
        toggleButtonRealPC.setToolTipText(dataLayer.getString("REAL_PC"));
        toggleButtonCable.setToolTipText(dataLayer.getString("CABLE"));
        jButtonFitToSize.setToolTipText(dataLayer.getString("FIT_TO_SIZE"));
        
        
        //toggleButtonRouters.setToolTipText(dataLayer.getString("ADD_ROUTER"));
        //toggleButtonSwitches.setToolTipText(dataLayer.getString("ADD_SWITCH"));
        //toggleButtonEndDevices.setToolTipText(dataLayer.getString("ADD_END_DEVICE"));
        
        
    }
}
