package prikazy;

import Main.SAXHandler.*;
import datoveStruktury.CiscoWrapper.CiscoZaznam;
import datoveStruktury.NATAccessList.AccessList;
import datoveStruktury.NATPool.Pool;
import datoveStruktury.NATPoolAccess.PoolAccess;
import datoveStruktury.NATtabulka.NATzaznam;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import pocitac.AbstraktniPocitac;
import pocitac.CiscoPocitac;
import pocitac.Konsole;
import pocitac.LinuxPocitac;
import pocitac.SitoveRozhrani;
import vyjimky.NeznamyTypPcException;
import static Main.Main.*;

/**
 * Prikaz ukladani do XML souboru vsechny pocitace dle aktualnich nastaveni.
 * @author Stanislav Řehák
 */
public class Uloz extends AbstraktniPrikaz {

    List<AbstraktniPocitac> pocitace;
    BufferedWriter out;
    String tabs = "";
    String soubor = konfigurak;
    List<PCJmeno> pripojenoKnove;

    public Uloz(AbstraktniPocitac pc, Konsole kon, List<String> slova) {
        super(pc, kon, slova);
        pocitace = (List<AbstraktniPocitac>) Main.Main.vsechno;
        pripojenoKnove = new ArrayList();
        if (slova.size() >= 2) {
            soubor = slova.get(1);
        }
        vykonejPrikaz();
    }

    private class PCJmeno {

        private PCJmeno(String pc_jmeno, String rozh_jmeno) {
            this.pc_jmeno = pc_jmeno;
            this.rozh_jmeno = rozh_jmeno;
        }
        String pc_jmeno;
        String rozh_jmeno;
    }

    /**
     * Zapise do souboru dany pocitac v XML formatu.
     * @param pocitac pocitac, ktery chceme zapsat
     * @throws IOException
     */
    private synchronized void ulozPC(AbstraktniPocitac pocitac) throws IOException {
        out.write("<pocitac jmeno=\"" + pocitac.jmeno + "\" ");

        if (pocitac instanceof CiscoPocitac) {
            out.write("typ=\"cisco\">\n");
        } else if (pocitac instanceof LinuxPocitac) {
            out.write("typ=\"linux\">\n");
        } else {
            throw new NeznamyTypPcException();
        }

        tabs += "\t";

        for (SitoveRozhrani iface : pocitac.rozhrani) {
            ulozRozhrani(iface);
        }

        ulozRoutovaciTabulku(pocitac);

        if (pocitac instanceof LinuxPocitac) {
            if (pocitac.ip_forward) {
                zapisElement("ip_forward", "1");
            } else {
                zapisElement("ip_forward", "0");
            }
        }

        ulozNATtabulku(pocitac);
        
        zapisUkoncovaci("pocitac\n");
    }

    /**
     * Vytvori element a zapise ho do souboru.
     * @param jmeno jmeno elementu
     * @param obsah obsah elementu, kdyz je null, tak tam bude prazdnej ""
     * @throws IOException
     */
    private void zapisElement(String jmeno, String obsah) throws IOException {
        zapis(vratElement(jmeno, obsah));
    }

    /**
     * Zapise start element, zvysi pocet tabelatoru.
     * @param jmeno
     * @throws IOException
     */
    private void zapisStartovaci(String jmeno) throws IOException {
        zapis("<"+jmeno+">\n");
        tabs += "\t";
    }

    /**
     * Zapise konec element, snizi pocet tabelatoru.
     * @param jmeno
     * @throws IOException
     */
    private void zapisUkoncovaci(String jmeno) throws IOException {
        tabs = tabs.substring(1);
        zapis("</"+jmeno+">\n");
    }

    /**
     * Vrati element, ktery vyrobi na zaklade parametru.
     * @param jmeno jmeno elementu
     * @param obsah obsah elementu, kdyz je null, tak tam bude prazdnej ""
     * @return
     */
    private String vratElement(String jmeno, String obsah) {
        if (obsah == null) {
            obsah = "";
        }
        return "<" + jmeno + ">" + obsah + "</" + jmeno + ">\n";
    }

    /**
     * Zapise do souboru dane rozhrani v XML formatu.
     * @param rozhrani rozhrani, ktere chceme zapsat
     * @throws IOException
     */
    private void ulozRozhrani(SitoveRozhrani rozhrani) throws IOException {
        zapisStartovaci("rozhrani");
        
        zapisElement("jmeno", rozhrani.jmeno);
        if (rozhrani.vratPrvni() == null) {
            zapisElement("ip", "");
            zapisElement("maska", "");
        } else {
            zapisElement("ip", rozhrani.vratPrvni().vypisAdresu());
            zapisElement("maska", rozhrani.vratPrvni().vypisMasku());
        }
        zapisElement("mac", rozhrani.macAdresa);
        zapisElement("nahozene", rozhrani.jeNahozene() ? "true" : "false");

        if (rozhrani.getPc().natTabulka.vratInside().contains(rozhrani)) {
            zapisElement("nat", "soukrome");
        }
        if (rozhrani.getPc().natTabulka.vratVerejne() != null) {
            if (rozhrani.getPc().natTabulka.vratVerejne().jmeno.equals(rozhrani.jmeno)) {
                zapisElement("nat", "verejne");
            }
        }

        zapisUkoncovaci("rozhrani");
    }

    /**
     * Zapise do souboru routovaci tabulku pro dany pocitac v XML formatu.
     * @param pc pocitac u ktereho chceme zapsat routovaci tabulku
     * @throws IOException
     */
    private void ulozRoutovaciTabulku(AbstraktniPocitac pc) throws IOException {

        if (pc.routovaciTabulka.pocetZaznamu() == 0) {
            return;
        }

        zapisStartovaci("routy");

        if (pc instanceof LinuxPocitac) {

            for (int i = 0; i < pc.routovaciTabulka.pocetZaznamu(); i++) {

                zapisStartovaci("zaznam");

                zapisElement("adresat", pc.routovaciTabulka.vratZaznam(i).getAdresat().vypisAdresu());
                zapisElement("maskaAdresata", pc.routovaciTabulka.vratZaznam(i).getAdresat().vypisMasku());

                if (pc.routovaciTabulka.vratZaznam(i).getBrana() != null) {
                    zapisElement("brana", pc.routovaciTabulka.vratZaznam(i).getBrana().vypisAdresu());
                } else {
//                    zapisElement("brana", "null")); // nakonec to tam nechci
                }
                zapisElement("rozhraniKam", pc.routovaciTabulka.vratZaznam(i).getRozhrani().jmeno);
                
                zapisUkoncovaci("zaznam");
            }
        }

        if (pc instanceof CiscoPocitac) {
            // u cisca ukladam jen zadane prikazy, protoze se routy z rozhrani generuji automaticky

            CiscoPocitac poc = (CiscoPocitac) pc;
            for (int i = 0; i < poc.getWrapper().size(); i++) {
                CiscoZaznam zaznam = poc.getWrapper().vratZaznam(i);

                zapisStartovaci("zaznam");

                zapisElement("adresat", zaznam.getAdresat().vypisAdresu());
                zapisElement("maskaAdresata", zaznam.getAdresat().vypisMasku());

                if (zaznam.getBrana() != null) {
                    zapisElement("brana", zaznam.getBrana().vypisAdresu());
                }

                if (zaznam.getRozhrani() != null) {
                    zapisElement("rozhraniKam", zaznam.getRozhrani().jmeno);
                }
                
                zapisUkoncovaci("zaznam");
            }
        }

        zapisUkoncovaci("routy");
    }

    /**
     * Zapise do souboru vsechno ohledne nastaveni DNATu.
     * @throws IOException
     */
    private void ulozNATtabulku(AbstraktniPocitac pocitac) throws IOException {

        zapisStartovaci("natovani");

        ulozNATPooly(pocitac);
        ulozNATPoolAccess(pocitac);
        ulozNATAccessList(pocitac);
        ulozNATStaticky(pocitac);
        
        zapisUkoncovaci("natovani");

    }

    /**
     * Pomocna metoda pro ulozeni NAT poolu.
     * @param pocitac
     * @throws IOException
     */
    private void ulozNATPooly(AbstraktniPocitac pocitac) throws IOException {
        zapisStartovaci("pooly");

        for (Pool pool : pocitac.natTabulka.lPool.seznam) {
            zapisStartovaci("pool");
            zapisElement("pJmeno", pool.jmeno);
            zapisElement("ip_start", pool.prvni().vypisAdresu());
            zapisElement("ip_konec", pool.posledni().vypisAdresu());
            zapisElement("prefix", "" + pool.prvni().pocetBituMasky());
            zapisUkoncovaci("pool");
        }
        
        zapisUkoncovaci("pooly");
    }

    /**
     * Pomocna metoda pro ulozeni NAT PoolAcessu.
     * @param pocitac
     * @throws IOException
     */
    private void ulozNATPoolAccess(AbstraktniPocitac pocitac) throws IOException {
        zapisStartovaci("prirazeniVice");

        for (PoolAccess pa : pocitac.natTabulka.lPoolAccess.seznam) {
            zapisStartovaci("prirazeni");

            zapisElement("accessCislo", "" + pa.access);
            zapisElement("poolJmeno", "" + pa.pool);
            zapisElement("overload", pa.overload ? "true" : "false");
           
            zapisUkoncovaci("prirazeni");
        }
        
        zapisUkoncovaci("prirazeniVice");
    }

    /**
     * Pomocna metoda pro ulozeni NAT access-listu
     * @param pocitac
     * @throws IOException
     */
    private void ulozNATAccessList(AbstraktniPocitac pocitac) throws IOException {
        zapisStartovaci("access-listy");

        for (AccessList access : pocitac.natTabulka.lAccess.seznam) {
            zapisStartovaci("access-list");

            zapisElement("cislo", "" + access.cislo);
            zapisElement("ipA", access.ip.vypisAdresu());
            zapisElement("ipAWildcard", access.ip.vypisWildcard());
            
            zapisUkoncovaci("access-list");
        }
        
        zapisUkoncovaci("access-listy");
    }

    /**
     * Ulozi staticka pravidla do konfiguraku.
     * @param pocitac
     * @throws IOException
     */
    private void ulozNATStaticky(AbstraktniPocitac pocitac) throws IOException {

        for (NATzaznam zaznam : pocitac.natTabulka.vratTabulku()) {
            if (zaznam.jeStaticke()) {
                zapisStartovaci("staticke");

                zapisElement("in", zaznam.vratIn().vypisAdresu());
                zapisElement("out", zaznam.vratOut().vypisAdresu());
                
                zapisUkoncovaci("staticke");
            }
        }
    }

    /**
     * Ulozi propojeni pocitacu pomoci kabelu.
     * @throws IOException
     */
    private void ulozPripojeni() throws IOException {

        zapisStartovaci("kabelaz");

        for (AbstraktniPocitac pocitac : pocitace) {
            for (SitoveRozhrani iface : pocitac.rozhrani) {
                if (jeRozhraniPripojenoAUlozeno(pocitac.jmeno, iface.jmeno) || iface.pripojenoK == null) {
                    continue;
                }

                zapisStartovaci("kabel");

                zapisElement("prvni", pocitac.jmeno+":"+iface.jmeno);
                zapisElement("druhy", iface.pripojenoK.getPc().jmeno+":"+iface.pripojenoK.jmeno);
                
                zapisUkoncovaci("kabel");
                pripojenoKnove.add(new PCJmeno(pocitac.jmeno, iface.jmeno));
                pripojenoKnove.add(new PCJmeno(iface.pripojenoK.getPc().jmeno, iface.pripojenoK.jmeno));
            }
        }
        
        zapisUkoncovaci("kabelaz");
    }

    /**
     * Vrati true, pokud je uz takove rozhrani zapsane do konfiguraku nebo pokud k rozhrani nevede kabel.
     * @param pc jmeno pocitace
     * @param jm jmeno rozhrani
     * @return
     */
    private boolean jeRozhraniPripojenoAUlozeno(String pc, String jm) {
        for (PCJmeno zaznam : pripojenoKnove) {
            if (zaznam.pc_jmeno.equals(pc) && zaznam.rozh_jmeno.equals(jm)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Pomocna metoda na zapis do souboru. Pridava odsazeni, ktere je udrzovano pomoci jinych metod (a podle umisteni v souboru).
     * @param s text, ktery chceme zapsat.
     * @throws IOException
     */
    private synchronized void zapis(String s) throws IOException {
        out.write(tabs + s);
    }

    @Override
    public synchronized void vykonejPrikaz() {

        kon.posliRadek("Ukladam do " + soubor + "..");

        try {
            out = new BufferedWriter(new FileWriter(soubor));
            zapis("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            zapis("<!-- DTD tady musi byt!!! -->\n"
                    + "<!DOCTYPE konfigurak SYSTEM \"psi.dtd\">\n\n"
                    + "<konfigurak>\n");

            for (AbstraktniPocitac pocitac : pocitace) {
                ulozPC(pocitac);
            }

            ulozPripojeni();

            out.write("</konfigurak>\n");
            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Chyba, z nejakeho duvodu se nepodarilo zapsat do souboru. info: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
