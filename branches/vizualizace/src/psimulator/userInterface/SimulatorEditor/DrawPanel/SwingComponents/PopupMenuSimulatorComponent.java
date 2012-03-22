package psimulator.userInterface.SimulatorEditor.DrawPanel.SwingComponents;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Singletons.ImageFactory.ImageFactorySingleton;
import psimulator.userInterface.MainWindowInnerInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.HwComponentGraphic;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanelInnerInterface;
import shared.telnetConfig.TelnetConfig;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class PopupMenuSimulatorComponent extends JPopupMenu {

    private DataLayerFacade dataLayer;
    private DrawPanelInnerInterface drawPanel;
    private MainWindowInnerInterface mainWindow;
    //
    private HwComponentGraphic hwComponentGraphics;
    //
    private JMenuItem jItemComponentProperties;
    private JMenuItem jItemOpenTelnet;

    public PopupMenuSimulatorComponent(MainWindowInnerInterface mainWindow, DrawPanelInnerInterface drawPanel, DataLayerFacade dataLayer, HwComponentGraphic hwComponentGraphics) {
        this.dataLayer = dataLayer;
        this.drawPanel = drawPanel;
        this.hwComponentGraphics = hwComponentGraphics;
        this.mainWindow = mainWindow;

        initComponents();
    }

    private void initComponents() {
        //jItemComponentProperties = new JMenuItem(dataLayer.getString("PROPERTIES"));
        jItemOpenTelnet = new JMenuItem(dataLayer.getString("OPEN_TELNET"));

        jItemOpenTelnet.addActionListener(new JMenuItemOpenTelnetListener());
        
        jItemOpenTelnet.setIcon(ImageFactorySingleton.getInstance().getImageIcon(ImageFactorySingleton.ICON_TELNET_16_PATH));
        
        this.add(jItemOpenTelnet);
    }
    
    /**
     * Shows popup menu on specified coordiantes
     * @param drawPanel
     * @param x
     * @param y 
     */
    public void show(DrawPanelInnerInterface drawPanel, int x, int y) {
        super.show((JComponent) drawPanel, x, y);
    }

    /**
     * Action Listener for Telnet button
     */
    class JMenuItemOpenTelnetListener implements ActionListener {

        /**
         *
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            // check if telnet window allready opened
            if(mainWindow.hasTelnetWindow(hwComponentGraphics.getId().intValue())){
                //System.out.println("Window allready opened");
                
                // set focus to opened telnet window
                JFrame existingFrame = mainWindow.getTelnetWindow(hwComponentGraphics.getId().intValue());
                if(existingFrame.isShowing()){
                    System.out.println("window showing");
                }
                existingFrame.requestFocus();
                
                return;
            }
            
            
            // Set Language
            String language;
            if(dataLayer.getString("BUNDLE_LANGUAGE_NAME").equals("Čeština")){
                language = "cz";
            }else{
                language = "en";
            }
            

            String [] args;
            
            // set HOST and PORT
            TelnetConfig telnetConfig = dataLayer.getTelnetConfig();
            if(telnetConfig != null && telnetConfig.getConfigRecords().containsKey(hwComponentGraphics.getId().intValue())){
                String host = dataLayer.getConnectionIpAddress();
                String port = ""+telnetConfig.getConfigRecords().get(hwComponentGraphics.getId().intValue()).getPort();
                
                args = new String[4];
                
                args[2] = host;
                args[3] = port;
            }else{
                args = new String[2];
            }
            
            args[0] = "-lang";
            args[1] = language;

            //final JFrame frame = new JFrame();
            final JFrame frame = de.mud.jta.Main.run(args);

            if(frame == null){
                System.out.println("Nastala chyba, okno telnetu se nepodařilo vytvořit");
                return;
            }
            
            frame.addWindowListener(new WindowListener() {
                int id;
                
                @Override
                public void windowOpened(WindowEvent we) {
                    //System.out.println("Window ");
                    id = hwComponentGraphics.getId().intValue();
                    mainWindow.addTelnetWindow(id, frame);
                }

                @Override
                public void windowClosing(WindowEvent we) {
                    //System.out.println("Window closing");
                    mainWindow.removeTelnetWindow(id);
                }

                @Override
                public void windowClosed(WindowEvent we) {
                    mainWindow.removeTelnetWindow(id);
                }
                @Override
                public void windowIconified(WindowEvent we) {
                }
                @Override
                public void windowDeiconified(WindowEvent we) {
                }
                @Override
                public void windowActivated(WindowEvent we) {
                }
                @Override
                public void windowDeactivated(WindowEvent we) {
                }
            });
            
            frame.setLocationRelativeTo(mainWindow.getMainWindowComponent());
            frame.setResizable(false);
            frame.setVisible(true);

        }
    }
    
    
    /**
     * Action Listener for Properties button
     */
    class JMenuItemPropertiesListener implements ActionListener {

        /**
         *
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            // open properties window
            HwComponentProperties hwComponentProperties = new HwComponentProperties(mainWindow.getMainWindowComponent(), dataLayer, drawPanel, hwComponentGraphics);
        }
    }
}
