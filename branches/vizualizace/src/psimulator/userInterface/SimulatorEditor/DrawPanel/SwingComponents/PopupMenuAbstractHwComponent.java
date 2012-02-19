package psimulator.userInterface.SimulatorEditor.DrawPanel.SwingComponents;

import java.util.List;
import javax.swing.*;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.AbstractHwComponent;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanelInnerInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.DrawPanelAction;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class PopupMenuAbstractHwComponent extends JPopupMenu {

    private DataLayerFacade dataLayer;
    private DrawPanelInnerInterface drawPanel;
    private JMenuItem jItemAlignToGrid;
    private JMenuItem jItemComponentProperties;
    private JMenuItem jItemSelectAll;
    private JMenuItem jItemDeleteComponent;
    private JMenuItem jItemFitToSize;
    
    private JMenuItem jItemAutomaticLayout;

    public PopupMenuAbstractHwComponent(DrawPanelInnerInterface drawPanel, DataLayerFacade dataLayer, int components) {
        this.dataLayer = dataLayer;
        this.drawPanel = drawPanel;

        jItemComponentProperties = new JMenuItem(dataLayer.getString("PROPERTIES"));
        jItemSelectAll = new JMenuItem(dataLayer.getString("SELECT_ALL"));
        jItemFitToSize = new JMenuItem(dataLayer.getString("FIT_TO_SIZE"));
        jItemAutomaticLayout = new JMenuItem(dataLayer.getString("AUTOMATIC_LAYOUT"));
        
        jItemAlignToGrid = new JMenuItem();
        jItemDeleteComponent = new JMenuItem();
        
        jItemComponentProperties.addActionListener(drawPanel.getAbstractAction(DrawPanelAction.PROPERTIES));
        jItemAlignToGrid.addActionListener(drawPanel.getAbstractAction(DrawPanelAction.ALIGN_COMPONENTS_TO_GRID));
        jItemDeleteComponent.addActionListener(drawPanel.getAbstractAction(DrawPanelAction.DELETE));
        jItemSelectAll.addActionListener(drawPanel.getAbstractAction(DrawPanelAction.SELECT_ALL));
        jItemFitToSize.addActionListener(drawPanel.getAbstractAction(DrawPanelAction.FIT_TO_SIZE));
        jItemAutomaticLayout.addActionListener(drawPanel.getAbstractAction(DrawPanelAction.AUTOMATIC_LAYOUT));
        
        // add images
        jItemComponentProperties.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/configure.png")));
        jItemAlignToGrid.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/grid.png")));
        jItemDeleteComponent.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/button_cancel.png")));
        jItemSelectAll.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/select_all.png")));
        jItemFitToSize.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/fit_to_size.png")));
        jItemAutomaticLayout.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/stock_alignment.png")));

        if (components == 1) {
            createOneComponentMenu();
        } else if (components == 0) {
            createAllComponentsMenu();
        } else {
            createMoreComponentsMenu();
        }

    }

    private void createOneComponentMenu() {
        jItemAlignToGrid.setText(dataLayer.getString("ALIGN_TO_GRID_ONE"));
        jItemDeleteComponent.setText(dataLayer.getString("DELETE_ONE"));
        //
        this.add(jItemComponentProperties);
        this.addSeparator();
        this.add(jItemAlignToGrid);
        this.add(jItemDeleteComponent);
    }

    private void createMoreComponentsMenu() {
        jItemAlignToGrid.setText(dataLayer.getString("ALIGN_TO_GRID_SELECTED"));
        jItemDeleteComponent.setText(dataLayer.getString("DELETE_SELECTED"));
        //
        this.add(jItemAlignToGrid);
        this.add(jItemDeleteComponent);
    }

    private void createAllComponentsMenu() {
        jItemAlignToGrid.setText(dataLayer.getString("ALIGN_TO_GRID_ALL"));
        jItemDeleteComponent.setText(dataLayer.getString("DELETE_ALL"));
        //
        this.add(jItemAutomaticLayout);
        this.add(jItemAlignToGrid);
        this.addSeparator();
        this.add(jItemSelectAll);
        this.add(jItemFitToSize);
        
    }

    public void show(DrawPanelInnerInterface drawPanel, int x, int y) {
        super.show((JComponent) drawPanel, x, y);
    }
}
