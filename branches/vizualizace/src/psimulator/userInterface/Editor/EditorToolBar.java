package psimulator.userInterface.Editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JToolBar;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.ColorMixerSignleton;
import psimulator.userInterface.Editor.Enums.Tools;
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

        setTextsToComponents();

        // apply background color
        this.setBackground(ColorMixerSignleton.editToolbarColor);
        Component[] comp = this.getComponents();
        for (Component c : comp) {
            c.setBackground(ColorMixerSignleton.editToolbarColor);
        }


        setSelectedButton(jButtonHand);


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
        jButtonHand.addActionListener(listener);
        jButtonPc.addActionListener(listener);
        jButtonMac.addActionListener(listener);
        jButtonCable.addActionListener(listener);
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
        jButtonHand.setToolTipText(dataLayer.getString("HAND"));
        jButtonMac.setToolTipText(dataLayer.getString("MAC"));
        jButtonPc.setToolTipText(dataLayer.getString("PC"));
        jButtonCable.setToolTipText(dataLayer.getString("CABLE"));
        jButtonFitToSize.setToolTipText(dataLayer.getString("FIT_TO_SIZE"));
    }
}
