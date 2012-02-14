package psimulator.userInterface.SimulatorEditor.DrawPanel.SwingComponents.InterfacesTable;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import psimulator.dataLayer.DataLayerFacade;

/**
 *
 * @author Martin
 */
public class InterfacesTableCellRenderer extends JLabel implements TableCellRenderer {

    private DataLayerFacade dataLayer;

    public InterfacesTableCellRenderer(DataLayerFacade dataLayer) {
        this.dataLayer = dataLayer;
    }

    @Override
    public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        // Configure the component with the specified value
        setText(value.toString());

        if (column == 0 || column == 2) {
            setFocusable(false);
        }

        // IP address column
        if (column == 3) {
            setToolTipText(dataLayer.getString("REQUIRED_FORMAT_IS") + " 192.168.1.1/24 (IP/mask)");
        }

        // MAC address column
        if (column == 4) {
            setToolTipText(dataLayer.getString("REQUIRED_FORMAT_IS") + " HH-HH-HH-HH-HH-HH (H = hexadecimal n.)");
        }

        return this;
    }
}
