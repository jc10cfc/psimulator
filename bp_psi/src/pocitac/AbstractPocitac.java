/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pocitac;

import datoveStruktury.*;
import datoveStruktury.RoutovaciTabulka;
import java.util.ArrayList;
import java.util.List;

/**
 * Virtualni pocitac, predek Linuxu a Cisca
 * @author neiss
 */
public abstract class AbstractPocitac {

    public Komunikace komunikace;
    public List<SitoveRozhrani> rozhrani; //kvuli vypisum to musi bejt verejny
    public String jmeno; //jmeno pocitace
    public RoutovaciTabulka routovaciTabulka;

    @Deprecated
    public AbstractPocitac(String jmeno) {
        vypis("Pouziva se deprecated metoda AbstractPocitac(String jmeno)");
        komunikace = new Komunikace(3567, this);
        rozhrani = new ArrayList<SitoveRozhrani>();
        this.jmeno = jmeno;
    }

    public AbstractPocitac(String jmeno, int port) {
        this.jmeno = jmeno;
        rozhrani = new ArrayList<SitoveRozhrani>();
        komunikace = new Komunikace(port, this);
        routovaciTabulka = new RoutovaciTabulka();
    }

    @Deprecated
    public AbstractPocitac(int port) {
        vypis("Pouziva se deprecated metoda AbstractPocitac(int port)");
        komunikace = new Komunikace(port, this);
        rozhrani = new ArrayList<SitoveRozhrani>();
    }

    /**
     * Prida rozhrani iface do seznamu rozhrani.
     * @param iface
     */
    public void pridejRozhrani(SitoveRozhrani iface) {
        rozhrani.add(iface);
    }

    @Deprecated
    public void nastavJmeno(String jm) {
        vypis("Pouziva se deprecated metoda nastavJmeno(String jm)");
        this.jmeno = jm;
    }

    public SitoveRozhrani najdiRozhrani(String jmeno) {
        if (jmeno == null) {
            return null;
        }
        for (SitoveRozhrani rozhr : rozhrani) {
            if (rozhr.jmeno.equals(jmeno)) {
                return rozhr;
            }
        }
        return null;
    }

    /**
     * Tahle metoda vypisuje na standartni vystup. Pouzivat pro vypisy v Komunikaci, Konsoli i Parseru atd.
     * pro snadnejsi debugovani, aby se vedelo, co kterej pocitac dela.
     * @param ret
     */
    public void vypis(String ret) {
        System.out.println("(" + jmeno + ":) " + ret);
    }


    // zatim pomocna metoda, pak se muze smazat
    public void vypisRozhrani() {

        for (SitoveRozhrani iface : rozhrani) {
            System.out.println("(" + jmeno + ":) " + iface.jmeno);
            if (iface.ip != null) {
                System.out.println("(" + jmeno + ":) " + iface.ip.vypisAdresu());
                System.out.println("(" + jmeno + ":) " + iface.ip.vypisMasku());
            }
            System.out.println("(" + jmeno + ":) " + iface.macAdresa);
            if (iface.pripojenoK != null) {
                System.out.println("(" + jmeno + ":) " + iface.pripojenoK.jmeno);
            }
            System.out.println("(" + jmeno + ":)");
        }
    }


//****************************************************************************************************
//tady zacinaj metody pro posilani pingu:

    //tahle metoda hleda, jestli mezi myma rozhranima neni nejaky se zadanou adresou
    private SitoveRozhrani najdiMeziRozhranima(IpAdresa cil) {
        for (SitoveRozhrani rozhr : rozhrani) {
            if (rozhr.ip.jeStejnaAdresa(cil)) {
                return rozhr;
            }
        }
        return null;
    }

    /**
     * Slouzi k odeslani novyho pingu z tohodle pocitace, musi vytvorit paket a doplnit do nej adresu zdroje.
     * @param cil
     * @param typ
     * @return false - ping se nepodarilo odeslat <br />
     *          true - ping byl odeslan
     */
    public boolean posliPing(IpAdresa cil, int typ, int kod){
        IpAdresa zdroj; //IP, ktera bude jako adresa zdroje v paketu
        SitoveRozhrani rozhr; //rozhrani, kterym budu paket posilat
        //hledani rozhrani, pres ktery se to bude posilat:
        rozhr=najdiMeziRozhranima(cil);//nejdriv se hleda cil mezi mejma adresama
        if(rozhr==null){ //kdyz adresa neni moje, zkousim hladat v routovaci tabulce
            rozhr=routovaciTabulka.najdiSpravnyRozhrani(cil);
        }
        if(rozhr==null){ //kdyz nenajdu spavny rozhrani ani v routovaci tabulce, vratim false
            return false;
        }
        zdroj=rozhr.ip;
        Paket paket = new Paket(zdroj, cil, typ, kod, 64, 0);
        return true;
    }

    /**
     * Slouzi k preposilani paketu.
     * @param paket
     */
    public void posliPing(Paket paket) {
        SitoveRozhrani rozhr = routovaciTabulka.najdiSpravnyRozhrani(paket.cil);
        if (rozhr != null) { //rozhrani nalezeno
            rozhr.pripojenoK.getPc().prijmiPing(paket);
        } else {//rozhrani nenalezeno - paket neni kam poslat
            posliPing(paket.zdroj, 3, 0); //net unreachable
        }
    }

    public void prijmiPing(Paket paket) {
        SitoveRozhrani rozhr = najdiMeziRozhranima(paket.cil);
        if (rozhr != null) { //paket je u me v cili
            if(paket.typ==8){ //icmp request
                posliPing(paket.zdroj, 0, 0); //zpatke se posila icmp reply
            }else  if(paket.typ==0){ //icmp reply

            }else if (paket.typ==3){ // paket nelze dorucit

            }
            
        } else { // paket se musi poslat dal
            posliPing(paket);
        }
    }















    @Deprecated
    private boolean jsemVCili(IpAdresa cil) {
        for (SitoveRozhrani rozhr : rozhrani) {
            if (rozhr.ip.jeStejnaAdresa(cil)) {
                return true;
            }
        }
        return false;
    }

    // bud pole bitu (pokud bude potreba vic nez 1 informace), jinak klasicky int
    @Deprecated
    public int posliPingStarej(IpAdresa cil) {
        int ret = -1;

        if (jsemVCili(cil)) {
            // ping paket dorazil do cile
            // konec
            return 0;
        }

        SitoveRozhrani sr = routovaciTabulka.najdiSpravnyRozhrani(cil);
        if (sr == null) {
            // neni pro to pravidlo zaznam v routovaci tabulce
            // konec
            return 1;
        }
        if (sr.pripojenoK == null) {
            // neni fyzicky pripojeno nikam
            // konec
            return 2;
        }

        ret = sr.pripojenoK.getPc().prijmiPingStarej(cil);
        return ret;
    }

    @Deprecated
    public int prijmiPingStarej(IpAdresa cil) {
        int ret = -1;
        if (jsemVCili(cil)) {
            // ping paket dorazil do cile
            // konec
            return 0;
        }

        ret = posliPingStarej(cil);
        return ret;
    }

    
}
