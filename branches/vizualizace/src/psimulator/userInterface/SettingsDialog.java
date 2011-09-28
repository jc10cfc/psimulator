package psimulator.userInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
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
    private Component parentComponent;
    /* END of window components */

    public SettingsDialog(Component mainWindow, LanguageManager languageManager, ActionListener okButtonListener, ActionListener cancelButtonListener) {
        this.languageManager = languageManager;

        this.parentComponent = mainWindow;
        
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

        pane.setBorder(BorderFactory.createTitledBorder(languageManager.getString("APPLICATION")));

        BoxLayout boxLayout = new BoxLayout(pane, BoxLayout.X_AXIS);
        pane.setLayout(boxLayout);


        JLabel label = new JLabel(languageManager.getString("LANGUAGE"));
        Font font = new Font(label.getFont().getName(), Font.BOLD, label.getFont().getSize());
        label.setFont(font);
        
        languageList = new JComboBox(languageManager.getAvaiableLanguageNames());
        languageList.setBackground(Color.white);
        languageList.setSelectedIndex(languageManager.getCurrentLanguagePosition());
        

        pane.add(label);
        pane.add(Box.createRigidArea(new Dimension(5,0)));
        pane.add(languageList);

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
