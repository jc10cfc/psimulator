package psimulator.userInterface;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.LayerUI;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.SimulatorEditor.DrawPanel.SwingComponents.ValidationLayerUI;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public abstract class AbstractPropertiesDialog extends JDialog {

    // A single LayerUI for all the fields.
    protected LayerUI<JFormattedTextField> layerUI = new ValidationLayerUI();
    //
    protected DataLayerFacade dataLayer;
    protected JDialog thisDialog;
    protected Component parentComponent;
    /*
     * window componenets
     */
    protected JButton jButtonOk;
    protected JButton jButtonCancel;

    public AbstractPropertiesDialog(Component mainWindow, DataLayerFacade dataLayer) {
        this.dataLayer = dataLayer;
        this.parentComponent = (Component) mainWindow;

        // set of JDialog parameters
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }
    
    protected abstract void copyValuesFromGlobalToLocal();

    protected abstract void copyValuesFromFieldsToLocal();

    protected abstract void copyValuesFromLocalToGlobal();

    protected abstract boolean hasChangesMade();

    protected abstract JPanel createContentPanel();

    protected void addContent(){
        // add Content
        this.getContentPane().add(createMainPanel());
    }
    
    protected void initialize() {

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                closeAction();
            }
        });

        // set OK button as default button
        this.getRootPane().setDefaultButton(jButtonOk);

        //
        this.thisDialog = (JDialog) this;

        this.pack();

        // place in middle of parent window
        int y = parentComponent.getY() + (parentComponent.getHeight() / 2) - (this.getHeight() / 2);
        int x = parentComponent.getX() + (parentComponent.getWidth() / 2) - (this.getWidth() / 2);
        this.setLocation(x, y);

        this.setVisible(true);
    }
    
    /**
     * Add key events reactions to root pane
     *
     * @return
     */
    @Override
    protected JRootPane createRootPane() {
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        JRootPane rootPane = new JRootPane();
        rootPane.registerKeyboardAction(new JButtonCancelListener(), stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        return rootPane;
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel();
        //mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainPanel.add(createContentPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        mainPanel.add(createOkCancelPanel());

        return mainPanel;
    }

    private JPanel createOkCancelPanel() {
        JPanel buttonPane = new JPanel();

        jButtonOk = new JButton(dataLayer.getString("SAVE"));
        jButtonOk.addActionListener(new JButtonOkListener());

        jButtonCancel = new JButton(dataLayer.getString("CANCEL"));
        jButtonCancel.addActionListener(new JButtonCancelListener());

        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(jButtonOk);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(jButtonCancel);
        buttonPane.add(Box.createRigidArea(new Dimension(3, 0)));

        return buttonPane;
    }

    protected void closeAction() {
        boolean close = true;

        copyValuesFromFieldsToLocal();
        if (hasChangesMade()) {
            if (checkUserAndSave() == false) {
                close = false;
            }
        }

        if (close) {
            this.setVisible(false);
            this.dispose();    //closes the window
        }
    }

    /**
     * Checks user if he wants to save changes and saves it.
     */
    private boolean checkUserAndSave() {
        // want to save?
        int i = showWarningSave(dataLayer.getString("WARNING"), dataLayer.getString("DO_YOU_WANT_TO_SAVE_CHANGES"));

        // if YES
        if (i == 0) {
            copyValuesFromLocalToGlobal();
        }

        if (i == -1) {
            return false;
        } else {
            return true;
        }
    }

    private int showWarningSave(String title, String message) {
        Object[] options = {dataLayer.getString("SAVE"), dataLayer.getString("DONT_SAVE")};
        int n = JOptionPane.showOptionDialog(this,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null, //do not use a custom Icon
                options, //the titles of buttons
                options[0]); //default button title

        return n;
    }

    //
    /////////////////////-----------------------------------////////////////////
    /**
     * Action Listener for JComboBoxInterface
     */
    class JButtonOkListener implements ActionListener {

        /**
         *
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            copyValuesFromFieldsToLocal();
            if (hasChangesMade()) {
                copyValuesFromLocalToGlobal();
            }
            thisDialog.setVisible(false);
            thisDialog.dispose();    //closes the window
        }
    }

    /////////////////////-----------------------------------////////////////////
    /**
     * Action Listener for JComboBoxInterface
     */
    class JButtonCancelListener implements ActionListener {

        /**
         *
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            closeAction();
        }
    }
}
