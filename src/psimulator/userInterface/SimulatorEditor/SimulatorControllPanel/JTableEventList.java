package psimulator.userInterface.SimulatorEditor.SimulatorControllPanel;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class JTableEventList extends JTable {

    public JTableEventList(TableModel tableModel) {
        super(tableModel);

        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);  // only single selection
        this.setRowSelectionAllowed(true);                           // row selection enabled
        this.setFocusable(false);                                    // dont display focus on cells
        this.getTableHeader().setReorderingAllowed(false);           // disable reordering columns

        // set custom cell renderer 
        this.getColumnModel().getColumn(4).setCellRenderer(new JTableEventListColorCellRenderer());
        
        
        // init column sizes of table
        initColumnSizes();
    }
    
    
    
    private void initColumnSizes() {
        
        TableColumn column;
        for (int i = 0; i < this.getColumnModel().getColumnCount(); i++) {
            column = this.getColumnModel().getColumn(i);
            switch (i) {
                case 0:
                    column.setPreferredWidth(20);
                    break;
                case 1:
                    column.setPreferredWidth(20);
                    break;
                case 2:
                    column.setPreferredWidth(20);
                    break;
                case 3:
                    column.setPreferredWidth(10);
                    break;
                case 4:
                    column.setPreferredWidth(10);
                    break;
                default:
                    column.setPreferredWidth(10);
                    break;

            }

        }
    }
}
