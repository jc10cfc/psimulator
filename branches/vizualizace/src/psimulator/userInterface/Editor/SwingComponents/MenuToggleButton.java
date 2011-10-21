package psimulator.userInterface.Editor.SwingComponents;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 *
 * @author Martin
 */
public class MenuToggleButton extends JToggleButton {

    private static final Icon i = new MenuArrowIcon();
    protected JPopupMenu pop;

    public MenuToggleButton(Icon icon, String toolTipText) {
        this(null, icon);
        
        this.setToolTipText(toolTipText);
    }
    
    public MenuToggleButton(JMenuItem[] menuItems, Icon icon) {
        super();

        // if there are tools in menuItems
        if (menuItems != null) {
            // create popup menu
            this.pop = createPopupMenu(menuItems);
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
                    System.out.println("set proper tool");

                    //set proper tool
                }
            }
        };
        a.putValue(Action.SMALL_ICON, icon);
        setAction(a);


        this.setToolTipText("ahoj");
        setFocusable(false);
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    }

    private JPopupMenu createPopupMenu(JMenuItem[] menuItems) {
        JPopupMenu popup = new JPopupMenu();
        popup.setLayout(new BoxLayout(popup, BoxLayout.X_AXIS));

        for (JMenuItem mi : menuItems) {
            popup.add(mi);
        }

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
        });

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