/*
 * UDELAT:
 * Prepsat metodu prijmiEthernetove() HOTOVO
 */
package pocitac;

import datoveStruktury.*;
import datoveStruktury.CiscoWrapper;
import java.util.LinkedList;
import java.util.List;
import pocitac.apps.CommandShell.CommandShell;
import pocitac.apps.CommandShell.prikazy.cisco.CiscoParserPrikazu;

/**
 *
 * @author Stanislav Řehák
 */
public class CiscoPocitac extends AbstraktniPocitac {

    private CiscoWrapper wrapper;

    /**
     * Vypis metody prijmiEthernetove().
     */
    private boolean debug = false;

    public CiscoPocitac(String jmeno, int port) {
        super(jmeno, port);
        wrapper = new CiscoWrapper(this);
        
        if(CiscoPocitac.commandList.isEmpty())
           CiscoPocitac.naplnCommandList();
    }

    /**
     * statická proměná s názvy příkazů počítače, využito pro doplnění tabulatorem
     */
    public static LinkedList<String> commandList = new LinkedList<String>();
    /**
     * metoda která naplní commandList, měla by se spustit jen jednou a to při
     * vytvoření první instance pocitace.
     * Později se může upravit na čtení ze souboru, ale to je asi zbytečné,
     * protože když někdo přidá příkaz tak se aplikace stejně znovu kompiluje...
     */
    public static void naplnCommandList(){
        // pro lepsi orientaci nechat abecedne serazene
        commandList.add("accesslist");
        commandList.add("help");
        commandList.add("ipaddress");
        commandList.add("ipnat");
        commandList.add("iproute");
        commandList.add("?");
        commandList.add("ping");
        commandList.add("show");
        commandList.add("traceroute");
        
    }

    /**
     * Vrati CiscoWrapper = ovladac routovaci tabulky.
     * @return
     */
    public CiscoWrapper getWrapper() {
        return wrapper;
    }

    /**
     * Vrati bud rozhrani se zadanym jmenem, nebo null, kdyz zadny rozhrani nenajde.
     * @param jmeno
     * @return null, kdyz nic nenajdes
     */
    @Override
    public SitoveRozhrani najdiRozhrani(String jmeno) {
        if (jmeno == null) {
            return null;
        }
        for (SitoveRozhrani rozhr : rozhrani) {
            if (rozhr.jmeno.equalsIgnoreCase(jmeno)) {
                return rozhr;
            }
        }
        return null;
    }

    /**
     * Ethernetove prijima nebo odmita prichozi pakety.
     * @param p
     * @param rozhr vstupni rozhrani pocitace, kterej ma paket prijmout, tzn. tohodle pocitace
     * @param ocekavana adresa, kterou odesilaci pocitac na tomto rozhrani ocekava
     * @param sousedni adresa, od ktereho mi prisel ARP request. Linuxu je to jedno, ale
     * pro cisco to je jeden z parametru, podle kteryho se rozhoduje, jestli paket prijme
     * @return true, kdyz byl paket prijmut, jinak false
     *
     * Kdyz nejde odpovedet sousedovi na ARP request (nemam na nej routu), tak paket neprijmu. <br />
     * Kdyz mohu odpovedet na ARP && (paket je pro me[1] || vim kam ho poslat dal), tak prijmu. <br />
     * [1] mam IP na rozh.vratPrvni() || ( mam IP na rozh-NAT && muzu ho na neco prelozit )
     *
     * V ostatnich pripadech neprijimam.
     */
    @Override
    public boolean prijmiEthernetove(Paket p, SitoveRozhrani rozhr, IpAdresa ocekavana, IpAdresa sousedni) {

        if (routovaciTabulka.najdiSpravnejZaznam(sousedni) == null) {
            ladici("nemuzu odpovedet na arp dotaz sousedovi => neprijimam");
            return false; // kdyz nemuzu odpovedet na arp dotaz sousedovi, tak smula
        }

        if (rozhr.obsahujeStejnouAdresu(ocekavana)) { //adresa souhlasi == je to pro me
            if (rozhr.vratPrvni() != null && rozhr.vratPrvni().jeStejnaAdresa(ocekavana)) {
                ladici("paket je pro me => prijimam");
                prijmiPaket(p, rozhr);
                return true;
            }

            if (natTabulka.mamZaznamOutProIp(p.cil)) {

                if (rozhr.vratPrvni() != null && p.zdroj.jeStejnaAdresa(rozhr.vratPrvni())) {
                    ladici("ZZZZZZZZZZZZzzzzzzz => neprijimam");
                    return false;
                }

                ladici("paket muzu odnatovat => prijimam");
                prijmiPaket(p, rozhr);
                return true;
            }
            vypis("XXX: "+p.cil.vypisAdresuSPortem());
            vypis("XXX: "+natTabulka.vypisZaznamyCisco());
            ladici("nemam zaznam v NAT tabulce => neprijimam");
            return false;
        }

        if (najdiMeziRozhranima(p.cil) != null) { // kdyz vim, kam to poslat dal
            ladici("vim, kam to poslat dal => prijimam");
            prijmiPaket(p, rozhr);
            return true;
        }

        // jinak zahazuju
        ladici("mohu odpovedet sousedovi na arp dotaz, neni to pro me a ja nevim kam s tim, tak to radsi neprijmu");
        return false;
    }

    /**
     * Vypisuje pouze kdyz je debug=true.
     * @param s
     */
    public void ladici(String s) {
        if (debug) {
            vypis("Ethernet: " + s);
        }
    }

    @Override
    public void configureCommandShell(CommandShell konsole) {
        konsole.setParser(new CiscoParserPrikazu(this, konsole));
        konsole.prompt = this.jmeno + ">";
    }

    @Override
    public List<String> getCommandList() {
        return CiscoPocitac.commandList;
    }
}
