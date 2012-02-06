package psimulator.userInterface;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Enums.ToolbarIconSizeEnum;

/**
 *
 * @author Martin
 */
public final class SettingsDialog extends AbstractPropertiesDialog {

    /* window componenets */
    private JComboBox languageList;
    private JLabel iconSizePicture;
    private JRadioButton tinyToolbarIconButton;
    private JRadioButton smallToolbarIconButton;
    private JRadioButton mediumToolbarIconButton;
    private JRadioButton largeToolbarIconButton;
    /* END of window components */
    
    /* variables for local store */
    private ToolbarIconSizeEnum toolbarIconSize;
    private int currentLanguagePosition;
    /* END variables for local store */

    public SettingsDialog(Component mainWindow, DataLayerFacade dataLayer) {
        super(mainWindow, dataLayer);
        
        // copy values to local
        copyValuesFromGlobalToLocal();

        // set title
        this.setTitle(dataLayer.getString("PREFERENCES"));

        // set minimum size
        this.setMinimumSize(new Dimension(150, 150));

        // add content to panel
        addContent();
        
        setElementsAccordingToLocal();
        
        // initialize
        initialize();
    }
    
    @Override
    protected void copyValuesFromGlobalToLocal() {
        currentLanguagePosition = dataLayer.getCurrentLanguagePosition();
        toolbarIconSize = dataLayer.getToolbarIconSize();
    }

    @Override
    protected void copyValuesFromFieldsToLocal() {
        currentLanguagePosition = languageList.getSelectedIndex();
    }

    @Override
    protected void copyValuesFromLocalToGlobal() {
        dataLayer.setCurrentLanguage(currentLanguagePosition);
        dataLayer.setToolbarIconSize(toolbarIconSize);
    }

    @Override
    protected boolean hasChangesMade() {
        if(currentLanguagePosition != dataLayer.getCurrentLanguagePosition()){
            return true;
        }
        
        if(toolbarIconSize!=dataLayer.getToolbarIconSize()){
            return true;
        }
        
        return false;
    }
   

    public int getSelectedLanguagePosition() {
        if (languageList != null) {
            return languageList.getSelectedIndex();
        }
        return -1;
    }

    private void setElementsAccordingToLocal() {
        // set selected language
        languageList.setSelectedIndex(currentLanguagePosition);
        // set image like in preferences
        setIconSize(toolbarIconSize);
    }

    @Override
    protected JPanel createContentPanel()  {
        JPanel mainPanel = new JPanel();
        
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab(dataLayer.getString("GENERAL"), new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/home.png")), createCardGeneral());

        tabbedPane.addTab("Second", new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/exec.png")), createCard2());

        mainPanel.add(tabbedPane);
        
        return mainPanel;
    }

    private JPanel createCardGeneral() {
        JPanel card = new JPanel();

        card.setLayout(new BorderLayout());

        card.add(createApplicationPanel(), BorderLayout.PAGE_START);

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
        
        tinyToolbarIconButton = new JRadioButton(dataLayer.getString("TINY"));
        tinyToolbarIconButton.setActionCommand(ToolbarIconSizeEnum.TINY.toString());
        tinyToolbarIconButton.addActionListener(toolbarIconSizeListener);
        
        smallToolbarIconButton = new JRadioButton(dataLayer.getString("SMALL"));
        smallToolbarIconButton.setActionCommand(ToolbarIconSizeEnum.SMALL.toString());
        smallToolbarIconButton.addActionListener(toolbarIconSizeListener);

        mediumToolbarIconButton = new JRadioButton(dataLayer.getString("MEDIUM"));
        mediumToolbarIconButton.setActionCommand(ToolbarIconSizeEnum.MEDIUM.toString());
        mediumToolbarIconButton.addActionListener(toolbarIconSizeListener);

        largeToolbarIconButton = new JRadioButton(dataLayer.getString("LARGE"));
        largeToolbarIconButton.setActionCommand(ToolbarIconSizeEnum.LARGE.toString());
        largeToolbarIconButton.addActionListener(toolbarIconSizeListener);

        buttonGroup.add(tinyToolbarIconButton);
        buttonGroup.add(smallToolbarIconButton);
        buttonGroup.add(mediumToolbarIconButton);
        buttonGroup.add(largeToolbarIconButton);

        JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
        buttonPanel.add(tinyToolbarIconButton);
        buttonPanel.add(smallToolbarIconButton);
        buttonPanel.add(mediumToolbarIconButton);
        buttonPanel.add(largeToolbarIconButton);

        panel.add(buttonPanel);

        iconSizePicture = new JLabel();
        iconSizePicture.setPreferredSize(new Dimension(48, 48));

        panel.add(iconSizePicture);

        return panel;
    }

    private JPanel createCard2() {
        JPanel card = new JPanel();

        card.add(new JButton("Button 2"));

        return card;
    }

    

    private void setIconSize(ToolbarIconSizeEnum iconSize) {
        // set icon size
        switch (iconSize) {
            case TINY:
                tinyToolbarIconButton.setSelected(true);
                iconSizePicture.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/home.png")));
                toolbarIconSize = ToolbarIconSizeEnum.TINY;
                break;
            case SMALL:
                smallToolbarIconButton.setSelected(true);
                iconSizePicture.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/22/home.png")));
                toolbarIconSize = ToolbarIconSizeEnum.SMALL;
                break;
            case MEDIUM:
                mediumToolbarIconButton.setSelected(true);
                iconSizePicture.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/home.png")));
                toolbarIconSize = ToolbarIconSizeEnum.MEDIUM;
                break;
            case LARGE:
                largeToolbarIconButton.setSelected(true);
                iconSizePicture.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/48/home.png")));
                toolbarIconSize = ToolbarIconSizeEnum.LARGE;
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
                case TINY:
                    iconSizePicture.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/home.png")));
                    toolbarIconSize = ToolbarIconSizeEnum.TINY;
                    break;
                case SMALL:
                    iconSizePicture.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/22/home.png")));
                    toolbarIconSize = ToolbarIconSizeEnum.SMALL;
                    break;
                case MEDIUM:
                    iconSizePicture.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/home.png")));
                    toolbarIconSize = ToolbarIconSizeEnum.MEDIUM;
                    break;
                case LARGE:
                    iconSizePicture.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/48/home.png")));
                    toolbarIconSize = ToolbarIconSizeEnum.LARGE;
                    break;
            }
        }
    }
}
