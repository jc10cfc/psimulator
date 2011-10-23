package psimulator.userInterface.Editor.SwingComponents;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import psimulator.dataLayer.ColorMixerSignleton;
import psimulator.userInterface.Editor.Tools.AbstractTool;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public class MenuToggleButton extends JToggleButton {

    /**
     * arrow icon for lower right corner
     */
    private static final Icon i = new MenuArrowIcon();
    /**
     * popup menu for this menu tohhle button
     */
    protected JPopupMenu pop;
    /**
     * current selected tool
     */
    private AbstractTool currentTool;

    public MenuToggleButton(List<AbstractTool> tools) {
        super();
        
        
        if (tools == null || tools.isEmpty()) {
            this.setToolTipText("no tool avaiable");
            this.setEnabled(false);
        } else {
            // if more than one tool, create and add popup menu
            if (tools.size() > 1) {
                // create popup menu
                this.pop = createPopupMenu(tools);

                // add mouse adapter for right click
                addMouseListener(new MouseAdapter() {

                    @Override
                    public void mousePressed(MouseEvent e) {
                        MenuToggleButton b = (MenuToggleButton) e.getSource();

                        if (SwingUtilities.isRightMouseButton(e)) {
                            if (pop != null) {
                                pop.show(b, b.getWidth(), 0);
                            }
                        }
                    }
                });
            }

            // create and add action
            Action a = new AbstractAction() {

                @Override
                public void actionPerformed(ActionEvent ae) {

                    MenuToggleButton b = (MenuToggleButton) ae.getSource();

                    // if tool enabled
                    if (b.isSelected()) {
                        // enable current tool tool
                        currentTool.setEnabled();
                    }
                }
            };
            //a.putValue(Action.SMALL_ICON, icon);
            setAction(a);
            
            // set first tool as current tool
            setCurrentTool(tools.get(0));
        }
     
        setFocusable(false);
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    }
 
    private void setCurrentTool(AbstractTool tool){
        // set current tool to tool
        currentTool = tool;
        // set tool tip text of this MenuToggleButton
        this.setToolTipText(tool.getName());
        // set Image icon of this MenuToggleButton
        this.setIcon(currentTool.getImageIcon());
    }
    
    
    
    private JPopupMenu createPopupMenu(List<AbstractTool> tools) {
        JPopupMenu popup = new JPopupMenu();
        
        for(final AbstractTool tool : tools){
            JMenuItem mi = new JMenuItem(tool.getName(), tool.getImageIcon(AbstractImageFactory.ICON_SIZE_MENU_BAR_POPUP));
            mi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    // set current tool to this tool
                    setCurrentTool(tool);
                }
            });
            
            popup.add(mi);
        }

        /*
        popup.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                //setSelected(false);
            }
        });*/

        return popup;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension dim = getSize();
        Insets ins = getInsets();

        // if there is popup menu, paint triangle in lower right corner
        if (pop != null) {
            int x = dim.width - ins.right - i.getIconWidth();
            int y = dim.width - ins.right - i.getIconHeight();
            i.paintIcon(this, g, x, y);
        }

    }
}

/**
 * Class represents small arrow for menu use
 * @author Martin
 */
class MenuArrowIcon implements Icon {

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(Color.BLACK);
        g2.translate(x, y);

        g2.drawLine(6, 2, 6, 2);
        g2.drawLine(5, 3, 6, 3);
        g2.drawLine(4, 4, 6, 4);
        g2.drawLine(3, 5, 6, 5);
        g2.drawLine(2, 6, 6, 6);

        g2.translate(-x, -y);
    }

    @Override
    public int getIconWidth() {
        return 9;
    }

    @Override
    public int getIconHeight() {
        return 9;
    }
}