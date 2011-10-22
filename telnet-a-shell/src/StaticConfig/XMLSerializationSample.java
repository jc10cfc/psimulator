/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package StaticConfig;

import StaticConfig.Beans.Computer;
import StaticConfig.Beans.Network;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import StaticConfig.Beans.NetworkInterface;
import java.util.LinkedList;

/**
 *
 * @author zaltair
 */
public class XMLSerializationSample {

    public static void main(String[] args) throws Exception {

        File file = new File("network.xml");

        Network encode = createSampleNetwork();
        encode.getComputers().get(0).getName().setDescription("muhehehe");

        encodeObject(encode, file);
        

        Network decoded = (Network) decodeObject(file);

        System.out.println(decoded.getComputers().get(0).getName().getDescription());

    }

    public static Network createSampleNetwork() {

        Network nt = new Network();
        nt.setNetworkName(nt.getNetworkName().setValue("New network"));

        LinkedList<Computer> computers = new LinkedList<Computer>();

        Computer pc1 = new Computer();
        pc1.setName(pc1.getName().setValue("PC1"));
        pc1.setIpForwarding(pc1.getIpForwarding().setValue(Boolean.TRUE));
        pc1.setNetwork(nt);
        LinkedList<NetworkInterface> iface = new LinkedList<NetworkInterface>();
        NetworkInterface niface = new NetworkInterface();
        niface.setIpAddress(niface.getIpAddress().setValue("192.168.10.15"));
        iface.add(niface);
        pc1.setInterfaces(iface);

        Computer pc2 = new Computer();
        pc2.setName(pc2.getName().setValue("PC2"));
        pc2.setIpForwarding(pc2.getIpForwarding().setValue(Boolean.TRUE));
        pc2.setNetwork(nt);
        LinkedList<NetworkInterface> iiface = new LinkedList<NetworkInterface>();
        NetworkInterface niiface = new NetworkInterface();
        niiface.setIpAddress(niiface.getIpAddress().setValue("192.168.10.16"));
        iiface.add(niface);
        pc2.setInterfaces(iiface);

        computers.add(pc1);
        computers.add(pc2);

        nt.setComputers(computers);

        return nt;


    }

   static void encodeObject(Object o, File xmlFile) throws IOException {
        XMLEncoder encoder = new XMLEncoder(new FileOutputStream(xmlFile));
        encoder.writeObject(o);
        encoder.close();
    }

    static Object decodeObject(File xmlFile) throws IOException {
        XMLDecoder decoder = new XMLDecoder(new FileInputStream(xmlFile));
        Object o = decoder.readObject();
        decoder.close();
        return o;
    }
}
