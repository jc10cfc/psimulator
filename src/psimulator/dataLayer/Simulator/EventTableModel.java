package psimulator.dataLayer.Simulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import psimulator.dataLayer.SimulatorEvents.SimulatorEvent;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class EventTableModel extends AbstractTableModel {
    
    private boolean timeReset = true;
    
    private List<SimulatorEvent> eventList;

    public EventTableModel(){
        eventList = Collections.synchronizedList(new ArrayList<SimulatorEvent>());
    }

    public void addSimulatorEvent(SimulatorEvent simulatorEvent){
        eventList.add(simulatorEvent);
        
        timeReset = false;
        
        this.fireTableRowsInserted(eventList.size()-1, eventList.size()-1);
    }
    
    public void deleteAllSimulatorEvents(){
        timeReset = true;
        
        eventList.clear();
        this.fireTableRowsDeleted(0, 0);
    }
    
    public SimulatorEvent getSimulatorEvent(int i){
        return eventList.get(i);
    }
    
    public boolean hasEvents(){
        return !eventList.isEmpty();
    }

    public boolean isTimeReset() {
        return timeReset;
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

    public List<SimulatorEvent> getEventListCopy() {
        List<SimulatorEvent> copy = new ArrayList<>(eventList);
        return copy;
    }

    public void setEventList(List<SimulatorEvent> eventList) {
        // set event list
        this.eventList = eventList;
        // fire event
        this.fireTableRowsInserted(0, eventList.size());
    }
    
    
}
