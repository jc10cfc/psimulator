package psimulator.userInterface.SimulatorEditor.DrawPanel.SwingComponents.InterfacesTable;

import javax.swing.table.AbstractTableModel;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Network.Components.EthInterfaceModel;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.HwComponentGraphic;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class InterfacesTableModel extends AbstractTableModel {

    private HwComponentGraphic abstractHwComponent;
    private DataLayerFacade dataLayer;
    //
    private boolean showAddresses;
    //
    private String[] columnNames;
    private Object[][] data;// = ...//same as before...

    public InterfacesTableModel(HwComponentGraphic abstractHwComponent, DataLayerFacade dataLayer, boolean showAddresses) {
        this.dataLayer = dataLayer;
        this.abstractHwComponent = abstractHwComponent;
        this.showAddresses = showAddresses;
        
        if(showAddresses){
            String[] names = {dataLayer.getString("INTERFACE"), dataLayer.getString("CONNECTED"),
                dataLayer.getString("CONNECTED_TO"), dataLayer.getString("IS_UP"), 
                dataLayer.getString("IP_ADDRESS_MASK"), dataLayer.getString("MAC_ADDRESS")};
            columnNames = names;
        }else{
            String[] names = {dataLayer.getString("INTERFACE"), dataLayer.getString("CONNECTED"),
                dataLayer.getString("CONNECTED_TO")};
            columnNames = names;
        }
        
        //

        int interfacesCount = abstractHwComponent.getInterfaceCount();

        data = new Object[interfacesCount][columnNames.length];

        for (int i = 0; i < interfacesCount; i++) {
            EthInterfaceModel ethInterface = abstractHwComponent.getEthInterfaceAtIndex(i);

            // fill interface names
            data[i][0] = ethInterface.getName();

            // fill connected status
            data[i][1] = new Boolean(ethInterface.hasCable());

            // fill connected to
            if (ethInterface.hasCable()) {
                if (ethInterface.getCable().getComponent1().getId().intValue() != abstractHwComponent.getId().intValue()) {
                    // set name from component1
                    data[i][2] = ethInterface.getCable().getComponent1().getName();
                } else {
                    // set name from component2
                    data[i][2] = ethInterface.getCable().getComponent2().getName();
                }
            } else {
                data[i][2] = "";
            }
            
            
            if(showAddresses){
                //
                // fill IS UP
                data[i][3] = new Boolean(ethInterface.isIsUp());
                
                // fill IP addresses
                data[i][4] = ethInterface.getIpAddress();

                // fill MAC addresses
                data[i][5] = ethInterface.getMacAddress();
            }
        }
        
        
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        if (col < 3) {
            return false;
        } else {
            return true;
        }
    }

    /*
     * Don't need to implement this method unless your table's data can change.
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
    }
    
    public boolean hasChangesMade() {
        if (showAddresses) {
            for(int i=0;i<getRowCount();i++){
                // if IS UP is different
                if(!abstractHwComponent.getInterfaces().get(i).isIsUp() == (boolean)getValueAt(i, 3)){
                    return true;
                }
                
                // if IP address is different
                if(!abstractHwComponent.getInterfaces().get(i).getIpAddress().equals(getValueAt(i, 4))){
                    return true;
                }
                
                // if MAC address is different
                if(!abstractHwComponent.getInterfaces().get(i).getMacAddress().equals(getValueAt(i, 5))){
                    return true;
                }
                
            }
        }
        return false;
    }
    
    public void copyValuesFromLocalToGlobal() {
        if (showAddresses) {
            for(int i=0;i<getRowCount();i++){
                // save IS UP
                abstractHwComponent.getInterfaces().get(i).setIsUp((boolean)getValueAt(i,3));
                
                // save IP
                abstractHwComponent.getInterfaces().get(i).setIpAddress(getValueAt(i,4).toString());
                
                // save MAC
                String mac = getValueAt(i,5).toString();
                mac = mac.replaceAll(":", "-");
                
                abstractHwComponent.getInterfaces().get(i).setMacAddress(mac);
            }
        }
    }
}
