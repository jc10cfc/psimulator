/*
 * Erstellt am 27.10.2011.
 */
package networkModule.L2;

import dataStructures.EthernetPacket;
import dataStructures.L3Packet;
import dataStructures.MacAddress;
import java.util.*;
import logging.*;
import networkModule.Layer;
import networkModule.NetMod;
import networkModule.TcpIpNetMod;
import physicalModule.PhysicMod;
import utils.SmartRunnable;
import utils.WorkerThread;

/**
 * Tady bude veskera implementace ethernetu a to jak pro switch, tak i router.
 * @author neiss
 */
public class EthernetLayer extends Layer implements SmartRunnable, Loggable {

	protected final List<EthernetInterface> ifaces = new ArrayList<>();
	protected final WorkerThread worker;
	protected final Map<Integer, SwitchportSettings> switchports = new HashMap<>();
	private final List<SendItem> sendBuffer = Collections.synchronizedList(new LinkedList<SendItem>());
	private final List<ReceiveItem> receiveBuffer = Collections.synchronizedList(new LinkedList<ReceiveItem>());
	public final PhysicMod physicMod;	// zkratka na fysickej modul



// Konstruktory a nastavovani pri startu: -------------------------------------------------------------------------------------------------

	public EthernetLayer(NetMod netMod) {
		super(netMod);
		exploreHardware();
		this.physicMod=netMod.getPhysicMod();
		this.worker = new WorkerThread(this);
	}

	/**
	 * Prida novy interface.
	 * @param name
	 * @param mac
	 * @return to pridany interface - loader s tim potrebuje jeste neco delat
	 */
	public EthernetInterface addInterface(String name, MacAddress mac){
		EthernetInterface iface = new EthernetInterface(name, mac, this);
		this.ifaces.add(iface);
		return iface;
	}

	/**
	 * Explores hardware. Pri startu projde vsechny switchporty fysickyho modulu a nacte si je.
	 */
	private void exploreHardware() {
		for (int i : netMod.getPhysicMod().getNumbersOfPorts()) {
			switchports.put(i, new SwitchportSettings(i));
		}
	}

	/**
	 * Metoda, ktera prida vsechny switchporty na zadany interface. Pouziva ji loader pro jenodussi nastaveni sitovyho
	 * modulu u switche, u normalniho tcpnetmodu by to nemelo smysl.
	 *
	 * @param iface
	 */
	public void addAllSwitchportsToGivenInterface(EthernetInterface iface) {
		for (SwitchportSettings swport : switchports.values()) {
			iface.addSwitchportSettings(swport);
			debug("Pridavam na interface "+iface.name+" switchport c. "+ swport.switchportNumber, null);
		}
	}





// Verejny metody pro sitovou komunikaci: ----------------------------------------------------------------------------------------------------

	public void receivePacket(EthernetPacket packet, int switchportNumber) {
		receiveBuffer.add(new ReceiveItem(packet, switchportNumber));
		worker.wake();
	}

	public void sendPacket(L3Packet packet, EthernetInterface iface, MacAddress target) {
		sendBuffer.add(new SendItem(packet, iface, target));
		worker.wake();
	}


// Ostatni verejny metody: --------------------------------------------------------------------------------------------------------------

	@Override
	public void doMyWork() {
		while ( ! (sendBuffer.isEmpty() && receiveBuffer.isEmpty())) {
			if (!sendBuffer.isEmpty()) {
				SendItem it = sendBuffer.remove(0);
				handleSendPacket(it.packet, it.iface, it.target);
			}
			if (!receiveBuffer.isEmpty()) {
				ReceiveItem it = receiveBuffer.remove(0);
				handleReceivePacket(it.packet, it.switchportNumber);
			}
		}
	}

	@Override
	public String getDescription() {
		return netMod.getDevice().getName()+": EthernetLayer";
	}

	public SwitchportSettings getSwitchport(int i){
		return switchports.get(i);
	}


// Privatni metody resici sitovou komunikaci: ------------------------------------------------------------------------

	/**
	 * Obsluhuje pakety, ktery dala sitova vrstva k odeslani.
	 *
	 * @param packet
	 * @param iface
	 * @param target
	 */
	private void handleSendPacket(L3Packet packet, EthernetInterface iface, MacAddress target) {
		EthernetPacket p = new EthernetPacket(iface.getMac(), target, packet.getType(), packet);
		transmitPacket(iface, p);
	}

	/**
	 *
	 * Obsluhuje pakety, ktery dostal sitovej modul od fysickyho.
	 *
	 * @param packet
	 * @param switchportNumber
	 */
	private void handleReceivePacket(EthernetPacket packet, int switchportNumber) {

		Logger.log(this, Logger.DEBUG, LoggingCategory.LINK, "Prijal jsem paket na switchportu "+ switchportNumber+". ", packet);

		//kontrola existence switchportu:
		SwitchportSettings swport = switchports.get(switchportNumber);
		if (swport == null) {
			Logger.log(getDescription(), Logger.ERROR, LoggingCategory.ETHERNET_LAYER,
					("Prisel paket na switchport, o jehoz existenci nemam tuseni: switchport c.: " + switchportNumber));
		}
		//kontrola, bylo-li nalezeno rozhrani:
		EthernetInterface iface = swport.assignedInterface;
		if (iface == null) {
			Logger.log(getDescription(), Logger.WARNING, LoggingCategory.ETHERNET_LAYER, "Nenalezeno interface ke switchportu, zrejme spatnej konfigurak, prusvih!");
			return;
		}

		//pridani do switchovaci tabulky:
		iface.addSwitchTableItem(packet.src, swport);

		//samotny vyrizovani paketu:
		if (packet.dst.equals(iface.getMac())) {	//pokud je paket pro me
			handlePacketForMe(packet, iface, swport);
		} else if (packet.dst.equals(MacAddress.broadcast())) { //paket je broadcastovej
			handlePacketForMe(packet, iface, swport);
			if (iface.switchingEnabled) {
				iface.transmitPacketOnAllSwitchports(packet);	// interface to odesle na vsechny porty
			}
		} else { //paket neni pro me, musim ho odeslat dal
			if (iface.switchingEnabled) { //odesila se, kdyz je to dovoleny
				transmitPacket(iface, packet);
			} else {
				Logger.log(this, Logger.IMPORTANT, LoggingCategory.ETHERNET_LAYER, "Nemam povoleno switchovat, zahazuju paket.", packet);
			}
		}

	}

	/**
	 * Stara se o samotny odeslani paketu. Najde si switchport a na ten to odesle.
	 *
	 * @param iface
	 * @param packet
	 */
	private void transmitPacket(EthernetInterface iface, EthernetPacket packet) {
		SwitchportSettings swport = iface.getSwitchport(packet.dst);
		if (swport == null) { // switchport nenalezen
			iface.transmitPacketOnAllSwitchports(packet);	// interface to odesle na vsechny porty
		} else if ( packet.dst.equals(MacAddress.broadcast()) ) {	// je to broadcast, odesila se to vsude
			iface.transmitPacketOnAllSwitchports(packet);	// interface to odesle na vsechny porty
		} else {
			netMod.getPhysicMod().sendPacket(packet, swport.switchportNumber); //odeslu to po tom najitym switchportu
		}
	}


	private void handlePacketForMe(EthernetPacket packet, EthernetInterface iface, SwitchportSettings swport) {
		if (netMod.isSwitch()){
			//TODO: Jedina vec, kdy se budou posilat pakety primo switchi je spanning tree protocol - tady bude jeho implementace.
		}else{
			((TcpIpNetMod)netMod).ipLayer.receivePacket(packet.data, iface);
		}
	}

	private void handleReceivedBroadcastPacket(EthernetPacket packet, EthernetInterface iface, SwitchportSettings swport){

	}



// ostatni privatni metody: -----------------------------------------------------------------------------------------------------------

	private void debug(String message,Object obj){
		Logger.log(this, Logger.DEBUG, LoggingCategory.ETHERNET_LAYER, message, obj);
	}

	private void paketInfo(String message,Object obj){
		Logger.log(this, Logger.INFO, LoggingCategory.LINK, message, obj);
	}



// tridy pro polozky v bufferech: ------------------------------------------------------------------------------------------------------------

	private class SendItem {

		L3Packet packet;
		EthernetInterface iface;
		MacAddress target;

		public SendItem(L3Packet packet, EthernetInterface iface, MacAddress target) {
			this.packet = packet;
			this.iface = iface;
			this.target = target;
		}
	}

	private class ReceiveItem {

		EthernetPacket packet;
		int switchportNumber;

		public ReceiveItem(EthernetPacket packet, int switchportNumber) {
			this.packet = packet;
			this.switchportNumber = switchportNumber;
		}
	}

}
