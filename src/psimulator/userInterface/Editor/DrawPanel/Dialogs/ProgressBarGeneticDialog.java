/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package psimulator.userInterface.Editor.DrawPanel.Dialogs;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.MainWindowInnerInterface;

/**
 *
 * @author Martin
 */
public class ProgressBarGeneticDialog extends JDialog implements ActionListener, PropertyChangeListener {

    private DataLayerFacade dataLayer;
    private static final String START_COMMAND = "start";
    private static final String CANCEL_COMMAND = "cancel";
    private static final String ENOUGH_QUALITY_COMMAND = "enough";
    private JPanel mainPanel;
    private JPanel controlPanel;
    private JButton startButton;
    private JButton cancelButton;
    private JButton enoughQuality;
    private JTextArea taskOutput;
    private JProgressBar progressBar;

    public ProgressBarGeneticDialog(MainWindowInnerInterface mainWindow, DataLayerFacade dataLayer) {
        super((JFrame) mainWindow, "OKNO", ModalityType.APPLICATION_MODAL);

        this.dataLayer = dataLayer;

        this.setLocationRelativeTo((JFrame) mainWindow);

        mainPanel = new JPanel(new BorderLayout());

        controlPanel = new JPanel();

        startButton = new JButton("Start");
        startButton.addActionListener((ActionListener) this);
        startButton.setActionCommand(START_COMMAND);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener((ActionListener) this);
        cancelButton.setActionCommand(CANCEL_COMMAND);
        cancelButton.setEnabled(false);

        enoughQuality = new JButton("Enough quality");
        enoughQuality.addActionListener((ActionListener) this);
        enoughQuality.setActionCommand(ENOUGH_QUALITY_COMMAND);
        enoughQuality.setEnabled(false);

        progressBar = new JProgressBar();
        //progressBar.setIndeterminate(true);

        controlPanel.add(startButton);
        controlPanel.add(cancelButton);
        //controlPanel.add(enoughQuality);
        controlPanel.add(progressBar);

        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5, 5, 5, 5));
        taskOutput.setEditable(false);

        mainPanel.add(controlPanel, BorderLayout.PAGE_START);
        mainPanel.add(new JScrollPane(taskOutput), BorderLayout.CENTER);

        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 20, 20));

        this.add(mainPanel);

        this.pack();
        this.setResizable(false);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand() == START_COMMAND) {
            startButton.setEnabled(false);
            cancelButton.setEnabled(true);
            
            //start genetic
            doStartGenetic();
        } else if (ae.getActionCommand() == CANCEL_COMMAND) {
            doCancelAction();
        } else if (ae.getActionCommand() == ENOUGH_QUALITY_COMMAND) {
        }
    }

    private void doCancelAction() {
        // show dialog if really cancel

        int result = showWarningCancelDialog("Cancel computation", "Do you really want to cancel computation?");

        if (result == 0) {
            doCancelGenetic();
                    
            this.setVisible(false);
            this.dispose();
        } else {
            startButton.setEnabled(false);
            cancelButton.setEnabled(true);
        }
    }
    
    private void doStartGenetic(){
        
    }
 
    private void doCancelGenetic(){
        // TODO
      
    
    }

    private int showWarningCancelDialog(String title, String message) {
        Object[] options = {"Yes", "No"};
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

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
