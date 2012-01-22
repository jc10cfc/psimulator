package psimulator.dataLayer.Simulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Martin
 */
public class EventTableModel extends AbstractTableModel {
    
    private List<SimulatorEvent> eventList;
    // names of columns are set in SimulatorControlPanel
    //private String[] columnNames = {"Time", "From", "To", "Type", "Info"};
    
    public EventTableModel(){
        eventList = Collections.synchronizedList(new ArrayList<SimulatorEvent>());
    }
    
    
    public void addSimulatorEvent(SimulatorEvent simulatorEvent){
        eventList.add(simulatorEvent);
        this.fireTableRowsInserted(eventList.size()-1, eventList.size()-1);
    }
    
    public void deleteAllSimulatorEvents(){
        eventList.clear();
        this.fireTableRowsDeleted(0, 0);
    }
    
    public SimulatorEvent getSimulatorEvent(int i){
        return eventList.get(i);
    }
    
    
    @Override
    public int getRowCount() {
        return eventList.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }
    
    @Override
    public Object getValueAt(int i, int i1) {
        return eventList.get(i).getValueAt(i1);
    }
    
    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
   
}
