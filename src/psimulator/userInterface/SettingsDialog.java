package psimulator.userInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Enums.ToolbarIconSizeEnum;

/**
 *
 * @author Martin
 */
public class SettingsDialog extends JDialog {

    //private Controller controller;
    private DataLayerFacade dataLayer;
    
    /* window componenets */
    private JButton jButtonOk;
    private JButton jButtonCancel;
    private JComboBox languageList;
    private ButtonGroup iconSizeGroup;
    private JLabel iconSizePicture;
    private JRadioButton smallToolbarIconButton;
    private JRadioButton mediumToolbarIconButton;
    private JRadioButton largeToolbarIconButton;
    /* END of window components */
    private ToolbarIconSizeEnum toolbarIconSizeSelected;

    public SettingsDialog(Component mainWindow, DataLayerFacade dataLayer, ActionListener okButtonListener, ActionListener cancelButtonListener) {
        this.dataLayer = dataLayer;
        
        this.setTitle(dataLayer.getString("PREFERENCES"));

        // add TabbedPane
        this.getContentPane().add(createTabbedPane());

        jButtonOk.addActionListener(okButtonListener);
        jButtonCancel.addActionListener(cancelButtonListener);


        this.setPreferencesLikeInModel();


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

    public int getSelectedLanguagePosition() {
        if (languageList != null) {
            return languageList.getSelectedIndex();
        }
        return -1;
    }

    public ToolbarIconSizeEnum getSelectedToolbarIconSize() {
        return toolbarIconSizeSelected;
    }

    private JTabbedPane createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab(dataLayer.getString("GENERAL"), new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/home.png")), createCardGeneral());

        tabbedPane.addTab("Second", new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/exec.png")), createCard2());

        return tabbedPane;
    }

    private JPanel createCardGeneral() {
        JPanel card = new JPanel();

        card.setLayout(new BorderLayout());

        card.add(createApplicationPanel(), BorderLayout.PAGE_START);

        card.add(createOkCancelPanel(), BorderLayout.PAGE_END);

        card.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        return card;
    }

    private JPanel createApplicationPanel() {
        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

        // APPLICATION PANEL
        JPanel applicationPanel = new JPanel();
        applicationPanel.setLayout(new BoxLayout(applicationPanel, BoxLayout.Y_AXIS));
        applicationPanel.setBorder(BorderFactory.createTitledBorder(dataLayer.getString("APPLICATION")));

        // LANGUAGE
        JPanel languagePanel = new JPanel();

        languagePanel.setLayout(new BoxLayout(languagePanel, BoxLayout.X_AXIS));

        JLabel languageLabel = new JLabel(dataLayer.getString("LANGUAGE"));
        Font font = new Font(languageLabel.getFont().getName(), Font.BOLD, languageLabel.getFont().getSize());
        languageLabel.setFont(font);

        languageList = new JComboBox(dataLayer.getAvaiableLanguageNames());
        languageList.setBackground(Color.white);

        languagePanel.add(languageLabel);
        languagePanel.add(Box.createRigidArea(new Dimension(5, 0)));
        languagePanel.add(languageList);

        applicationPanel.add(languagePanel);
        applicationPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        // TOOLBAR ICON SIZE
        JPanel iconSizePanel = new JPanel();
        iconSizePanel.setLayout(new BoxLayout(iconSizePanel, BoxLayout.X_AXIS));

        JLabel iconSizeLabel = new JLabel(dataLayer.getString("TOOLBAR_ICON_SIZE"));
        iconSizeLabel.setFont(font);

        iconSizePanel.add(iconSizeLabel);
        iconSizePanel.add(Box.createRigidArea(new Dimension(5, 0)));

        iconSizePanel.add(createIconSizePanel());


        applicationPanel.add(iconSizePanel);


        // END APPLICATION PANEL
        pane.add(applicationPanel);

        return pane;
    }

    private JPanel createIconSizePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        ButtonGroup buttonGroup = new ButtonGroup();
        ActionListener toolbarIconSizeListener = new IconSizeListener();
        
        smallToolbarIconButton = new JRadioButton(dataLayer.getString("SMALL"));
        smallToolbarIconButton.setActionCommand(ToolbarIconSizeEnum.SMALL.toString());
        smallToolbarIconButton.addActionListener(toolbarIconSizeListener);

        mediumToolbarIconButton = new JRadioButton(dataLayer.getString("MEDIUM"));
        mediumToolbarIconButton.setActionCommand(ToolbarIconSizeEnum.MEDIUM.toString());
        mediumToolbarIconButton.addActionListener(toolbarIconSizeListener);

        largeToolbarIconButton = new JRadioButton(dataLayer.getString("LARGE"));
        largeToolbarIconButton.setActionCommand(ToolbarIconSizeEnum.LARGE.toString());
        largeToolbarIconButton.addActionListener(toolbarIconSizeListener);

        buttonGroup.add(smallToolbarIconButton);
        buttonGroup.add(mediumToolbarIconButton);
        buttonGroup.add(largeToolbarIconButton);

        JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
        buttonPanel.add(smallToolbarIconButton);
        buttonPanel.add(mediumToolbarIconButton);
        buttonPanel.add(largeToolbarIconButton);

        panel.add(buttonPanel);

        iconSizePicture = new JLabel();
        iconSizePicture.setPreferredSize(new Dimension(48, 48));

        panel.add(iconSizePicture);

        return panel;
    }

    private JPanel createOkCancelPanel() {
        JPanel buttonPane = new JPanel();

        jButtonOk = new JButton(dataLayer.getString("OK"));
        jButtonCancel = new JButton(dataLayer.getString("CANCEL"));

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

    private void setPreferencesLikeInModel() {
        // set selected language
        languageList.setSelectedIndex(dataLayer.getCurrentLanguagePosition());
        // set image like in preferences
        setIconSize(dataLayer.getToolbarIconSize());
    }

    private void setIconSize(ToolbarIconSizeEnum iconSize) {
        // set icon size
        switch (iconSize) {
            case SMALL:
                smallToolbarIconButton.setSelected(true);
                iconSizePicture.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/home.png")));
                toolbarIconSizeSelected = ToolbarIconSizeEnum.SMALL;
                break;
            case MEDIUM:
                mediumToolbarIconButton.setSelected(true);
                iconSizePicture.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/home.png")));
                toolbarIconSizeSelected = ToolbarIconSizeEnum.MEDIUM;
                break;
            case LARGE:
                largeToolbarIconButton.setSelected(true);
                iconSizePicture.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/48/home.png")));
                toolbarIconSizeSelected = ToolbarIconSizeEnum.LARGE;
                break;
        }
    }

    /////////////////////-----------------------------------////////////////////
    /**
     * Action Listener for ToolbarIconSize
     */
    class IconSizeListener implements ActionListener {

        /**
         * 
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (ToolbarIconSizeEnum.valueOf(e.getActionCommand())) {
                case SMALL:
                    iconSizePicture.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/home.png")));
                    toolbarIconSizeSelected = ToolbarIconSizeEnum.SMALL;
                    break;
                case MEDIUM:
                    iconSizePicture.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/home.png")));
                    toolbarIconSizeSelected = ToolbarIconSizeEnum.MEDIUM;
                    break;
                case LARGE:
                    iconSizePicture.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/48/home.png")));
                    toolbarIconSizeSelected = ToolbarIconSizeEnum.LARGE;
                    break;
            }
        }
    }
}
