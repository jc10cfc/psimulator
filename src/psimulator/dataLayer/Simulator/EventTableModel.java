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

    private List<SimulatorEvent> eventList;
    private volatile boolean timeReset = true;
    private volatile int currentPositionInList;
    private volatile boolean isInTheList;
    private final Object lock = new Object();

    public EventTableModel() {
        eventList = Collections.synchronizedList(new ArrayList<SimulatorEvent>());

        isInTheList = false;
        currentPositionInList = 0;
    }
    
    public int getCurrentPositionInList(){
        return currentPositionInList;
    }

    public boolean isInTheList() {
        synchronized (lock) {
            return isInTheList;
        }
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
        synchronized (lock) {
            return eventList.get(i).getValueAt(i1);
        }
    }

    @Override
    public Class getColumnClass(int c) {
        synchronized (lock) {
            return getValueAt(0, c).getClass();
        }
    }

    public void setCurrentPositionInList(int position) {
        synchronized (lock) {
            if (position < 0) {
                currentPositionInList = 0;
                isInTheList = false;
            } else if(position <= eventList.size() - 1){
                currentPositionInList = position;
                isInTheList = true;
            }
        }
    }
    
    public boolean canMoveToNextEvent(){
         synchronized (lock) {
             if(currentPositionInList < eventList.size() - 1){
                 return true;
             }else{
                 return false;
             }
         }
    }

    public void moveToNextEvent() {
        synchronized (lock) {
            // next when not in the list (start playing)
            if(isInTheList == false && currentPositionInList == 0 && eventList.size() > 0){
                currentPositionInList = 0;
                isInTheList = true;
                return;
            }
            // classical next 
            if (currentPositionInList < eventList.size() - 1) {
                currentPositionInList++;
                isInTheList = true;
            }
        }
    }

    public void moveToPreviousEvent() {
        synchronized (lock) {
            if (currentPositionInList > 0) {
                currentPositionInList--;
                isInTheList = true;
            }
        }
    }

    public void moveToFirstEvent() {
        synchronized (lock) {
            if (eventList.size() > 0) {
                currentPositionInList = 0;
                isInTheList = true;
            }
        }
    }

    public void moveToLastEvent() {
        synchronized (lock) {
            if (eventList.size() > 0) {
                currentPositionInList = eventList.size() - 1;
                isInTheList = true;
            }
        }
    }
    
    public SimulatorEvent moveToLastEventAndReturn(){
        synchronized (lock) {
            if (eventList.size() > 0) {
                currentPositionInList = eventList.size() - 1;
                isInTheList = true;
                return eventList.get(currentPositionInList);
            }
            return null;
        }
    }

    public void addSimulatorEvent(SimulatorEvent simulatorEvent) {
        synchronized (lock) {
            eventList.add(simulatorEvent);
            timeReset = false;

            this.fireTableRowsInserted(eventList.size() - 1, eventList.size() - 1);
        }
    }

    public void deleteAllSimulatorEvents() {
        synchronized (lock) {
            timeReset = true;

            int listSize = eventList.size();
            eventList.clear();

            currentPositionInList = 0;
            isInTheList = false;

            this.fireTableRowsDeleted(0, listSize);
        }
    }

    public SimulatorEvent getSimulatorEvent(int i) {
        synchronized (lock) {
            return eventList.get(i);
        }
    }

    public boolean hasEvents() {
        synchronized (lock) {
            return !eventList.isEmpty();
        }
    }

    public boolean isTimeReset() {
        synchronized (lock) {
            return timeReset;
        }
    }

    public List<SimulatorEvent> getEventListCopy() {
        synchronized (lock) {
            List<SimulatorEvent> copy = new ArrayList<>(eventList);
            return copy;
        }
    }

    public void setEventList(List<SimulatorEvent> eventList) {
        synchronized (lock) {
            // set event list
            this.eventList = eventList;
            // fire event
            this.fireTableRowsInserted(0, eventList.size());
        }
    }
}
