package psimulator.userInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import psimulator.dataLayer.language.LanguageManager;

/**
 *
 * @author Martin
 */
public class SettingsDialog extends JDialog {

    private LanguageManager languageManager;

    /* window componenets */
    private JButton jButtonOk;
    private JButton jButtonCancel;
    private JComboBox languageList;
    /* END of window components */

    public SettingsDialog(Component mainWindow, LanguageManager languageManager, ActionListener okButtonListener, ActionListener cancelButtonListener) {
        this.languageManager = languageManager;

        this.setTitle(languageManager.getString("PREFERENCES"));

        // add TabbedPane
        this.getContentPane().add(createTabbedPane());
        
        jButtonOk.addActionListener(okButtonListener);
        jButtonCancel.addActionListener(cancelButtonListener);
        
        
        // set of JDialog parameters
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setResizable(false);

        this.pack();
        this.setSize(300, 300);

        Component parentComponent = (Component) mainWindow;

        // place in middle of parent window
        int y = parentComponent.getY() + (parentComponent.getHeight() / 2) - (this.getHeight() / 2);
        int x = parentComponent.getX() + (parentComponent.getWidth() / 2) - (this.getWidth() / 2);
        this.setLocation(x, y);
        // end of set of JDialog parameters
    }
    
    public int getSelectedLanguagePosition(){
        if(languageList != null){
            return languageList.getSelectedIndex();
        }
        return -1;
    }

    private JTabbedPane createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab(languageManager.getString("GENERAL"), new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/home.png")), createCardGeneral());

        tabbedPane.addTab("Second", new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/exec.png")), createCard2());

        return tabbedPane;
    }

    private JPanel createCardGeneral() {
        JPanel card = new JPanel();

        card.setLayout(new BorderLayout());

        card.add(createApplicationPanel(), BorderLayout.PAGE_START);

        card.add(createOkCancelPanel(), BorderLayout.PAGE_END);
        
        card.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        
        return card;
    }

    private JPanel createApplicationPanel() {
        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        
        // APPLICATION PANEL
        JPanel applicationPanel = new JPanel();
        applicationPanel.setLayout(new BoxLayout(applicationPanel, BoxLayout.Y_AXIS));
        applicationPanel.setBorder(BorderFactory.createTitledBorder(languageManager.getString("APPLICATION")));
        
        // LANGUAGE
        JPanel languagePanel = new JPanel();
        
        languagePanel.setLayout(new BoxLayout(languagePanel, BoxLayout.X_AXIS));
        
        JLabel languageLabel = new JLabel(languageManager.getString("LANGUAGE"));
        Font font = new Font(languageLabel.getFont().getName(), Font.BOLD, languageLabel.getFont().getSize());
        languageLabel.setFont(font);
        
        languageList = new JComboBox(languageManager.getAvaiableLanguageNames());
        languageList.setBackground(Color.white);
        languageList.setSelectedIndex(languageManager.getCurrentLanguagePosition());
  
        languagePanel.add(languageLabel);
        languagePanel.add(Box.createRigidArea(new Dimension(5,0)));
        languagePanel.add(languageList);

        applicationPanel.add(languagePanel);
        applicationPanel.add(Box.createRigidArea(new Dimension(0,8)));
        
        // TOOLBAR ICON SIZE
        JPanel iconSizePanel = new JPanel();
        iconSizePanel.setLayout(new BoxLayout(iconSizePanel, BoxLayout.X_AXIS));
        
        JLabel iconSizeLabel = new JLabel(languageManager.getString("LANGUAGE"));
        iconSizeLabel.setFont(font);
        
        iconSizePanel.add(iconSizeLabel);
        iconSizePanel.add(Box.createRigidArea(new Dimension(5,0)));

        applicationPanel.add(iconSizePanel);
        
        
        // END APPLICATION PANEL
        pane.add(applicationPanel);
        
        return pane;
    }
    
    
    private JPanel createOkCancelPanel(){
        JPanel buttonPane = new JPanel();
   
        jButtonOk = new JButton(languageManager.getString("OK"));
        jButtonCancel = new JButton(languageManager.getString("CANCEL"));
        
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(jButtonOk);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(jButtonCancel);

        return buttonPane;
    }

    private JPanel createCard2() {
        JPanel card = new JPanel();

        card.add(new JButton("Button 2"));

        return card;
    }
}
