package psimulator.userInterface.SimulatorEditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JToolBar;
import psimulator.dataLayer.ColorMixerSignleton;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanelToolChangeOuterInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.MainTool;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.SecondaryTool;
import psimulator.userInterface.SimulatorEditor.SwingComponents.MenuToggleButton;
import psimulator.userInterface.SimulatorEditor.Tools.ToolsFactory;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public class EditorToolBar extends JToolBar implements Observer {

    private DataLayerFacade dataLayer;
    private AbstractImageFactory imageFactory;
    
    private JButton jButtonFitToSize;
    private JButton jButtonAlignToGrid;
    
    private ButtonGroup toolsButtonGroup;
    
    private MenuToggleButton toggleButtonHand;
    private MenuToggleButton toggleButtonRouters;
    private MenuToggleButton toggleButtonSwitches;
    private MenuToggleButton toggleButtonEndDevices;
    private MenuToggleButton toggleButtonRealPC;
    private MenuToggleButton toggleButtonCable;

    public EditorToolBar(DataLayerFacade dataLayer, AbstractImageFactory imageFactory, DrawPanelToolChangeOuterInterface toolChangeInterface) {
        super();
        this.dataLayer = dataLayer;
        this.imageFactory = imageFactory;
        
        // add this ToolBar as observer to languageManager
        dataLayer.addLanguageObserver((Observer)this);

        // tool bar is not possible to move
        this.setFloatable(false);

        // set orientation to vertical
        this.setOrientation(VERTICAL);

        this.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        this.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // BUTTONS
        jButtonFitToSize = new JButton(imageFactory.getImageIconForToolbar(SecondaryTool.FIT_TO_SIZE));
        jButtonFitToSize.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        
        jButtonAlignToGrid = new JButton(imageFactory.getImageIconForToolbar(SecondaryTool.ALIGN_TO_GRID));
        jButtonAlignToGrid.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        
        toolsButtonGroup = new ButtonGroup();
        
        toggleButtonHand = new MenuToggleButton(ToolsFactory.getTools(MainTool.HAND, imageFactory, toolChangeInterface));
        toggleButtonRouters = new MenuToggleButton(ToolsFactory.getTools(MainTool.ADD_ROUTER, imageFactory, toolChangeInterface));
        toggleButtonSwitches = new MenuToggleButton(ToolsFactory.getTools(MainTool.ADD_SWITCH, imageFactory, toolChangeInterface));
        toggleButtonEndDevices = new MenuToggleButton(ToolsFactory.getTools(MainTool.ADD_END_DEVICE, imageFactory, toolChangeInterface));
        toggleButtonRealPC = new MenuToggleButton(ToolsFactory.getTools(MainTool.ADD_REAL_PC, imageFactory, toolChangeInterface));
        toggleButtonCable = new MenuToggleButton(ToolsFactory.getTools(MainTool.ADD_CABLE, imageFactory, toolChangeInterface));
                
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
        this.add(jButtonAlignToGrid);
        
        
        // set texts
        setTextsToComponents();

        // apply background color
        this.setBackground(ColorMixerSignleton.editToolbarColor);
        Component[] comp = this.getComponents();
        for (Component c : comp) {
            c.setBackground(ColorMixerSignleton.editToolbarColor);
            // tool icon cannot be marked (ugly frame)
            c.setFocusable(false);
        }

    }
 
    /**
     * reaction to update from LanguageManager
     */ 
    @Override
    public void update(Observable o, Object o1) {
        setTextsToComponents();
    }

    /**
     * Enables deafult tool of this toolbar
     */
    public void setDefaultTool(){
        toggleButtonHand.setCurrentToolEnabled();
        toggleButtonHand.setSelected(true);
    }
    
    /**
     * adds action listener to jButtonFitToSize
     * @param listener 
     */
    public void addToolActionFitToSizeListener(ActionListener listener) {
        jButtonFitToSize.addActionListener(listener);
    }
    
    /**
     * adds action listener to jButtonAlignToGrid
     * @param listener 
     */
    public void addToolActionAlignToGridListener(ActionListener listener) {
        jButtonAlignToGrid.addActionListener(listener);
    }

    ////////------------ PRIVATE------------///////////
    private void setTextsToComponents() {
        // set text only to Tools that cant be changed
        toggleButtonHand.setToolTipText(dataLayer.getString("HAND"));
        toggleButtonRealPC.setToolTipText(dataLayer.getString("REAL_PC"));
        toggleButtonCable.setToolTipText(dataLayer.getString("CABLE"));
        jButtonFitToSize.setToolTipText(dataLayer.getString("FIT_TO_SIZE"));
        jButtonAlignToGrid.setToolTipText(dataLayer.getString("ALIGN_TO_GRID"));
    }
}
