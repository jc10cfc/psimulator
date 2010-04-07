/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pocitac;

import datoveStruktury.*;
import datoveStruktury.RoutovaciTabulka;
import java.util.ArrayList;
import java.util.List;
import prikazy.AbstraktniPing;

/**
 * Virtualni pocitac, predek Linuxu a Cisca
 * @author neiss
 */
public abstract class AbstractPocitac {

    public Komunikace komunikace;
    public List<SitoveRozhrani> rozhrani; //kvuli vypisum to musi bejt verejny
    public String jmeno; //jmeno pocitace
    public RoutovaciTabulka routovaciTabulka;

    private boolean ladeni=true;

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
     * @param kod
     * @param cas
     * @param icmp_seq
     * @param prikaz
     * @return false - ping se nepodarilo odeslat <br />
     *          true - ping byl odeslan
     */
    public boolean posliNovejPaket(IpAdresa cil, int typ, int kod, double cas, int icmp_seq, AbstraktniPing prikaz) {
        IpAdresa zdroj; //IP, ktera bude jako adresa zdroje v paketu
        SitoveRozhrani mojeRozhr; //rozhrani, pres ktery budu paket posilat
        SitoveRozhrani ciziRozhr=null; //rozhrani, na ktery budu paket posilat
        //hledani rozhrani, pres ktery se to bude posilat:
        mojeRozhr = najdiMeziRozhranima(cil);//nejdriv se hleda cil mezi mejma adresama
        if (mojeRozhr == null) { //kdyz adresa neni moje, zkousim hladat v routovaci tabulce
            mojeRozhr = routovaciTabulka.najdiSpravnyRozhrani(cil);
            if (mojeRozhr != null) { //nejaky se naslo
                ciziRozhr = mojeRozhr.pripojenoK; //pridava se to, ktery se mo pouzitreturn true;
                if (ciziRozhr == null) {
                    return true;
                }
            }
        } else {
            ciziRozhr = mojeRozhr;
        }
        if (mojeRozhr == null) { //kdyz nenajdu spavny rozhrani ani v routovaci tabulce, vratim false
            return false;
        }
        zdroj = mojeRozhr.ip;
        Paket paket = new Paket(zdroj, cil, typ, kod, cas, icmp_seq, 64, prikaz);
        if (ladeni) {
            vypis("posilam novej paket: " + paket.toString());
        }
        ciziRozhr.getPc().prijmiPaket(paket);
        return true;
    }

    /**
     * Slouzi k preposilani paketu. Neni-li paket kam dorucit, posle se zpatky zprava, ze nelze dorucit.
     * @param paket
     */
    public void preposliPaket(Paket paket) {
        paket.ttl -=1;
        if (paket.ttl==0){
            return;
        }
        SitoveRozhrani rozhr = routovaciTabulka.najdiSpravnyRozhrani(paket.cil);
        if (rozhr != null) { //rozhrani nalezeno
            if(ladeni)vypis("preposilam paket na rozhrani "+rozhr.jmeno+"paket: "+paket.toString());
            rozhr.pripojenoK.getPc().prijmiPaket(paket);
        } else {//rozhrani nenalezeno - paket neni kam poslat
            posliNovejPaket(paket.zdroj, 3, 0,paket.cas, paket.icmp_seq, paket.prikaz); //net unreachable
        }
    }

    /**
     * Prijima ping. Je-li urcen pro mne, udela patricnou akci (odesle odpoved nebo vypise vypis). Neni-li
     * urcen pro me, posle paket dal.
     * @param paket
     */
    public void prijmiPaket(Paket paket) {
        if(ladeni)vypis("prijal jsem paket "+paket.toString());
        paket.cas += Math.random()*0.03 + 0.07; //nejnizsi hodnota asi 0.07 ms, nejvyssi 0.1 ms
        SitoveRozhrani rozhr = najdiMeziRozhranima(paket.cil);
        if (rozhr != null) { //paket je u me v cili
            if(paket.typ==8){ //icmp request
                posliNovejPaket(paket.zdroj, 0, 0, paket.cas, paket.icmp_seq,paket.prikaz); //zpatky se
                                                                                    //posila icmp reply
            }else { //paket je urcen pro me
                paket.prikaz.zpracujPaket(paket);
            }
        } else { // paket se musi poslat dal
            preposliPaket(paket);
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
