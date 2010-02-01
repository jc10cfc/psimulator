/*
 * http://www.benak.net/pocitace/os/linux/ifconfig.php
 * http://books.google.cz/books?id=1x-6XBk8bKoC&pg=PT26&lpg=PT26&dq=ifconfig&source=bl&ots=E5ys4iqBVw&sig=0LU94iuXjoBE3WDxYGnTHChcx9Q&hl=cs&ei=O1lGS6CFHoOCnQOZzP3vAg&sa=X&oi=book_result&ct=result&resnum=8&ved=0CBkQ6AEwBw#v=onepage&q=ifconfig&f=false
 * http://www.starhill.org/man/8_ifconfig.html
 */
package prikazy;

import datoveStruktury.IpAdresa;
import java.util.ArrayList;
import java.util.List;
import pocitac.*;

/**
 *
 * @author neiss
 */
public class Ifconfig extends AbstraktniPrikaz {

    String jmenoRozhrani;
    List <String> seznamIP=new ArrayList<String>();
    String maska;
    String broadcast;
    int pocetBituMasky = -1; //maska zadana formou /24 totiz ma vetsi prioritu nez 255.255.255.0
    String add; //IP adresa, ktera se ma pridat
    List <String> del=new ArrayList<String>();  //ipadresa, ktera se ma odebrat
    boolean minus_a = false;
    boolean minus_v = false;
    boolean minus_s = false;
    /**
     * Do tyhle promenny bude metoda nastavPrikaz zapisovat, jakou chybu nasla:
     * 0: vsechno v poradku
     * 1: spatny prepinac (neznama volba)
     * 2: nejaka chyba v gramatice prikazu (napr: ifconfig wlan0 1.2.3.5 netmask)
     *    potreba provest, co je dobre, a vypsat napovedu --help
     * 3: rozhrani neexistuje
     * 4: zadano vice ipadres, funguje to, ale je to nesmysl
     * 5: neplatna IP adresa
     * 6: pocet bitu masky vetsi nez 32
     * 7: neplatna IP adresa parametru add
     */
    int navratovyKod = 0;
    SitoveRozhrani rozhrani; //rozhrani, se kterym se bude operovat
    int pouzitIp=-1; //cislo seznamIP, ktera IP se ma pouzit


    public Ifconfig(AbstractPocitac pc, Konsole kon, List<String> slova) {
        super(pc, kon, slova);
        nastavPrikaz();
        zkontrolujPrikaz();
        vykonejPrikaz();
    }

    @Override
    protected void nastavPrikaz() {
        String tempRet;
        int ind = 1; //index v seznamu, zacina se jedicko, protoze prvnim slovem je ifconfig
        // prepinace:
        while (ind < slova.size() && slova.get(ind).indexOf("-") == 0) { //kdyz je prvnim znakem slova minus
            if (slova.get(ind).equals("-a")) {
                minus_a = true;
            } else if (slova.get(ind).equals("-v")) {
                minus_v = true;
            } else if (slova.get(ind).equals("-s")) {
                minus_s = true;
            } else {
                errNeznamyPrepinac(slova.get(ind));
                return; //tady ifconfig uz zbytek neprovadi, i kdyby byl dobrej
            }
            ind++;
        }
        //jmenoRozhrani je to prvni za prepinacema:
        if (ind >= slova.size()) {
            return;
        }
        jmenoRozhrani = slova.get(ind);
        ind++;
        if (ind >= slova.size()) {
            return;
        }
        //parametry:
        //Zjistil jsem, ze neznamej parametr se povazuje za adresu nebo za adresu s maskou.
        try { // celej cyklus je v bloku ,protoze by se mohlo stat, ze za nazvem parametru uz nebude jeho hodnota
            while (ind < slova.size()) {
                tempRet = slova.get(ind);
                if (tempRet.equals("netmask")) {//maska
                    ind++;
                    maska = slova.get(ind);
                } else if (tempRet.equals("broadcast")) {//adresa pro broadcast, ta si vubec dela uplne, co se ji zachce
                    ind++;
                    broadcast = slova.get(ind);
                } else if (tempRet.equals("add")) {//adresa pro broadcast, ta si vubec dela uplne, co se ji zachce
                    ind++;
                    add = slova.get(ind);
                } else if (tempRet.equals("del")) {//adresa pro broadcast, ta si vubec dela uplne, co se ji zachce
                    ind++;
                    del.add(slova.get(ind));
                } else { //kdyz to neni nic jinyho, tak to ifconfig povazuje za seznamIP adresu
                    int pos = tempRet.indexOf('/');
                    if (pos != -1) { //zadano i s maskou za lomitkem
                        
                        try {
                            int temp = Integer.parseInt(tempRet.substring(pos + 1, tempRet.length()));
                            if(temp<0){
                                navratovyKod=2;
                            }else{
                                seznamIP.add ( tempRet.substring(0, pos) ) ;
                                pocetBituMasky = temp;
                            }
                        } catch (NumberFormatException ex){
                            navratovyKod=2;
                        }

                    } else {
                        seznamIP.add(tempRet);
                    }
                }
                ind++;
            }
        } catch (IndexOutOfBoundsException ex) { //tuhle vyjimku hazi radky s nactenim hodnoty parametru, kdyz
            navratovyKod = 2;             //tam ta hodnota neni
        }
    }

    /**
     * Tahlecta metoda kontroluje jen hodnoty parametru, na nektery chyby, napr. gramaticky (nespravny
     * prepinace, vice parametru netmask ap.), predpokladam, ze uz se prislo.
     */
    private void zkontrolujPrikaz(){
        if (jmenoRozhrani==null) return; //uzivatel zadal jen ifconfig, mozna nejaky prepinace, ale nic vic
        //-------------------
        for ( SitoveRozhrani rozhr: pc.rozhrani){ //hledani spravneho rozhrani
            if(rozhr.jmeno.equals(jmenoRozhrani)) rozhrani=rozhr;
        }
        if (rozhrani==null){
            navratovyKod=3;
            return;
        }
        //------------------------
        if(seznamIP.size()>1){ //jestli neni moc IP adres
            navratovyKod=4;
            return;
        }
        for (int i=0;i<seznamIP.size();i++){ //kontrola spravnosti IP
            if(IpAdresa.jeSpravnaIP(seznamIP.get(i))){
                pouzitIp=i;
            } else {
                navratovyKod=5; //neplatna IP
            }

        }
        //--------------------
        //string masky se nekontroluje, protoze pro to IpAdresa nema metodu, kontroluje se az pri nastavovani
        if(pocetBituMasky!=-1){ //kontrola pocetBituMasky
            if(pocetBituMasky>32){//mensi totiz bejt nemuze, to se kontroluje driv
                navratovyKod=6;
                pocetBituMasky=pocetBituMasky % 32; //takhle se ifconfig opravdu chova, vyzkousel jsem to
            }
        }
        //---------------------
        if(!IpAdresa.jeSpravnaIP(add)){
            navratovyKod=7;
        }
        
    }

    private void errNeznamyPrepinac(String ret) {
        kon.posliRadek("ifconfig: neznámá volba `" + ret + "'.");
        kon.posliRadek("ifconfig: `--help' vypíše návod k použití.");
        navratovyKod = 1;
    }

    @Override
    protected void vykonejPrikaz() {
        kon.posliRadek(toString());
    }

    @Override
    public String toString() {
        String vratit = "Parametry prikazy ifconfig:\n navratovyKodParseru: " + navratovyKod;
        if (jmenoRozhrani != null) {
            vratit += "\n rozhrani: " + jmenoRozhrani;
        }
        if (seznamIP != null) {
            vratit += "\n ip: " + seznamIP;
        }
        if (pocetBituMasky != -1) {
            vratit += "\n pocetBituMasky: " + pocetBituMasky;
        }
        if (maska != null) {
            vratit += "\n maska: " + maska;
        }
        if (add != null) {
            vratit += "\n add: " + add;
        }
        if (del != null) {
            vratit += "\n del: " + del;
        }

        return vratit;

    }
}
