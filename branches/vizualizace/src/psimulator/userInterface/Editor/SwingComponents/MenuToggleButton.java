package psimulator.userInterface.Editor.SwingComponents;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import psimulator.userInterface.Editor.Tools.AbstractTool;

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
                this.pop = new ToolPopupMenu(tools, this);

                // add mouse adapter for right click
                addMouseListener(new MouseAdapter() {

                    @Override
                    public void mousePressed(MouseEvent e) {
                        MenuToggleButton b = (MenuToggleButton) e.getSource();

                        if (SwingUtilities.isRightMouseButton(e)) {
                            if (pop != null) {
                                // show popup menu
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
                        // enable current tool
                        setCurrentToolEnabled();
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

    /**
     * sets current tool in MenuTogglebutton  to tool in parameter
     * @param tool Chosen tool
     */
    public final void setCurrentTool(AbstractTool tool) {
        // set current tool to tool
        currentTool = tool;
        // set tool tip text of this MenuToggleButton
        this.setToolTipText(tool.getName());
        // set Image icon of this MenuToggleButton
        this.setIcon(currentTool.getImageIcon());
        // enable current tool in 
        setCurrentToolEnabled();
        // set toggle button selected
        this.setSelected(true);
    }

    /**
     * enables current tool in this Menu ToggleButton 
     */
    public void setCurrentToolEnabled() {
        currentTool.setEnabled();
    }
    
    public AbstractTool getSelectedTool(){
       return currentTool;
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

        int[] xPoints = {6, 2, 6};
        int[] yPoints = {2, 6, 6};
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.drawPolygon(xPoints, yPoints, 3);
        g2.fillPolygon(xPoints, yPoints, 3);

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