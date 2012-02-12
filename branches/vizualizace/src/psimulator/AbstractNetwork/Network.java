package psimulator.AbstractNetwork;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Martin
 */
public class Network implements Serializable {
    //private int ID;
    //private String name;

    private NetworkCounter counter;
    private List<NetworkCable> cables;
    private List<NetworkDevice> devices;
    // needed for Graph restore from Network - fast lookup
    private LinkedHashMap<Integer, NetworkInterface> interfacesMap;

    public Network(/*
             * int ID, String name
             */) {
        //this.ID = ID;
        //this.name = name;

        this.cables = new ArrayList<NetworkCable>();
        this.devices = new ArrayList<NetworkDevice>();

        this.interfacesMap = new LinkedHashMap<Integer, NetworkInterface>();
    }

    public void save(String fileName) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance("psimulator.AbstractNetwork");

        File file = new File(fileName);

        Marshaller marsh = context.createMarshaller();

        // nastavení formátování
        marsh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        marsh.marshal(this, file);

    }

    public static Network load(String fileName) throws JAXBException {

        File file = new File(fileName);

        Network network = null;

        JAXBContext context = JAXBContext.newInstance("psimulator.AbstractNetwork");

        Unmarshaller unmarsh = context.createUnmarshaller();

        network = (Network) unmarsh.unmarshal(file);

        return network;


    }

    public void addDevice(NetworkDevice device) {
        devices.add(device);
    }

    public void addCable(NetworkCable cable) {
        cables.add(cable);
    }

    public void addNetworkInterface(NetworkInterface networkInterface) {
        interfacesMap.put(networkInterface.getID(), networkInterface);
    }

    public NetworkInterface getNetworkInterface(int id) {
        return interfacesMap.get(id);
    }

    public List<NetworkCable> getCables() {
        return cables;
    }

    public List<NetworkDevice> getDevices() {
        return devices;
    }

    public NetworkCounter getCounter() {
        return counter;
    }

    public void setCounter(NetworkCounter counter) {
        this.counter = counter;
    }

    // ---------------------------------------------------------------
    // Martin Svihlik tyto metody nepotrebuje:
    public void setCables(List<NetworkCable> cables) {
        this.cables = cables;
    }

    public void setDevices(List<NetworkDevice> devices) {
        this.devices = devices;
    }
    /*
     * public int getID() { return ID; }
     *
     * public void setID(int ID) { this.ID = ID; }
     *
     * public String getName() { return name; }
     *
     * public void setName(String name) { this.name = name; }
     */
}
