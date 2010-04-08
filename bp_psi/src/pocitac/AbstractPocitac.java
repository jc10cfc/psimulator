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
     * Posila paket vedlejsimu pocitaci, pricemz si kontroluje, jestli ho ethernetove muze poslat, tzn.,
     * jestli je na druhy strane skutecne ta adresa, na kterou to chci poslat.
     * @param p paket, kterej posilam
     * @param rozhr rozhrani toho sousedniho pocitace, na kterej to posilam
     * @param sousedni IP adresa rozhrani na sousednim pocitaci, na kterej to posilam
     */
    private void posliEthernetove(Paket p, SitoveRozhrani rozhr, IpAdresa sousedni) {
        if (rozhr != null) { //cizi rozhrani by teoreticky mohlo bejt null
            if(rozhr.ip.jeStejnaAdresa(sousedni)){ //adresa souhlasi - muzu to poslat
                rozhr.getPc().prijmiPaket(p);
            }else{//adresa nesouhlasi, zpatky se musi poslat host unreachable
                //posliNovejPaket(p.zdroj, 3, 1, p.cas, p.icmp_seq, p.prikaz); //net unreachable
                posliNovejPaketOdpoved(p,rozhr.pripojenoK.ip, 3, 1); //host unreachable
                            // -> svoji adresu musim dost krkolome zjistovat, ale je to asi nejjednodussi
                            //    a nemusim se bat, ze tam nekde bude null
            }


        }
    }

    /**
     * Slouzi k odeslani odpovedi na icmp request. V odpovednim paketu
     * se pouzije jako zdrojova adresa cilova adresa puvodniho paketu.
     * @param puvodni puvodni paket, na kterej se odpovida
     */
    private void posliNovejPaketOdpoved(Paket puvodni,IpAdresa spec_zdroj, int typ, int kod){
        if(spec_zdroj!=null){
            posliNovejPaket(spec_zdroj, puvodni.zdroj, typ, kod, puvodni.cas, puvodni.icmp_seq,puvodni.prikaz);
        }else{
            posliNovejPaket(puvodni.cil, puvodni.zdroj, typ, kod, puvodni.cas, puvodni.icmp_seq,puvodni.prikaz);
        }
    }

    /**
     * Slouzi k odeslani novyho pingu z tohodle pocitace, musi vytvorit paket a doplnit do nej adresu zdroje.
     * Pouziva metodu dolejc, s tim, ze nespecifikuje specialni zdroj.
     * @param cil
     * @param typ
     * @param kod
     * @param cas
     * @param icmp_seq
     * @param prikaz
     * @return false - ping se nepodarilo odeslat <br />
     *          true - ping byl odeslan
     */
    public boolean posliNovejPaket(IpAdresa cil,int typ,int kod,double cas,int icmp_seq,AbstraktniPing prikaz) {
        return posliNovejPaket(null, cil, typ, kod, cas, icmp_seq, prikaz);
    }

    /**
     * Slouzi k odeslani novyho pingu z tohodle pocitace, musi vytvorit paket a doplnit do nej adresu zdroje.
     * Kdyz chci odeslat paket s natvrdo zadanym zdrojem, zadam ho do spec_zdroj, jinak tam dam null (pouziva
     * se pro icmp reply.
     * @param spec_zdroj IP adresa zdroje, kdyz ji chci natvrdo zadat
     * @param cil
     * @param typ
     * @param kod
     * @param cas
     * @param icmp_seq
     * @param prikaz
     * @return false - ping se nepodarilo odeslat <br />
     *          true - ping byl odeslan
     */
    private boolean posliNovejPaket(IpAdresa spec_zdroj, IpAdresa cil, int typ, int kod,
            double cas, int icmp_seq, AbstraktniPing prikaz) {
        IpAdresa zdroj; //IP, ktera bude jako adresa zdroje v paketu
        SitoveRozhrani mojeRozhr; //rozhrani, pres ktery budu paket posilat
        SitoveRozhrani ciziRozhr=null; //rozhrani, na ktery budu paket posilat
        IpAdresa sousedni=cil;//defaultne (kdyz si to posilam sobe, nebo kdyz to posilam na routu na rozhrani (U))

       //hledani rozhrani, pres ktery se to bude posilat:
        mojeRozhr = najdiMeziRozhranima(cil);//nejdriv se hleda cil mezi mejma adresama
        if (mojeRozhr != null) { //cilova adresa se nasla mezi adresama na mejch rozhranich -> posilam sam sobe
            ciziRozhr = mojeRozhr;
        } else { //kdyz adresa neni moje, zkousim hladat v routovaci tabulce
            RoutovaciTabulka.Zaznam z = routovaciTabulka.najdiSpravnejZaznam(cil);
            if (z != null) { //nejaky zaznam se nasel
                mojeRozhr = z.getRozhrani(); //nesmi existovat zaznam bez rozhrani
                ciziRozhr = mojeRozhr.pripojenoK;
                if (z.getBrana() != null) { //kdyz je zaznam na branu, sousedni adresa musi bejt adresa brany
                    sousedni = z.getBrana();
                } //... jinak je sousedni adresa rovnou cilova adresa toho paketu
            }
        }
        if (mojeRozhr == null) { //kdyz nenajdu spavny rozhrani ani v routovaci tabulce, vratim false
            return false;
        }
        zdroj = mojeRozhr.ip;
        if(spec_zdroj!=null){ //kdyz je specifikovano, s jakym zdrojem se ma paket poslat, tak se tak posle
            zdroj=spec_zdroj;
        }
        Paket paket = new Paket(zdroj, cil, typ, kod, cas, icmp_seq, 64, prikaz);
        if(ladeni)vypis("posilam novej paket na rozhrani "+mojeRozhr.jmeno+" na sousedni adresu "
                    +sousedni.vypisAdresu()+" "+paket.toString());
        posliEthernetove(paket, ciziRozhr, sousedni);
        return true;
    }

    /**
     * Slouzi k preposilani paketu. Neni-li paket kam dorucit, posle se zpatky zprava, ze nelze dorucit.
     * @param paket
     */
    private void preposliPaket(Paket paket) {
        IpAdresa sousedni = paket.cil; //adresa nejblizsiho pocitace, kam se ma paket poslat, nefaultne cil,
                                        //kdyztak se to zmeni na branu z routovaci tabulky
        paket.ttl -=1;
        if (paket.ttl==0){
            return;
        }
        RoutovaciTabulka.Zaznam z = routovaciTabulka.najdiSpravnejZaznam(paket.cil);
        if (z != null) { //zaznam nalezen
            SitoveRozhrani rozhr = z.getRozhrani();
            if(z.getBrana()!=null){
                sousedni=z.getBrana(); //sousedni uzel je brana z routovaci tabulky
            }
            if(ladeni)vypis("preposilam paket na rozhrani "+rozhr.jmeno+" na sousedni adresu "
                    +sousedni.vypisAdresu()+" "+paket.toString());
            posliEthernetove(paket, rozhr.pripojenoK, sousedni);
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
                posliNovejPaketOdpoved(paket,null, 0, 0); //zpatky se posila icmp reply
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
