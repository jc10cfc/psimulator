/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prikazy.linux;

import datoveStruktury.IpAdresa;
import java.util.ArrayList;
import java.util.List;
import pocitac.*;
import prikazy.*;
import vyjimky.SpatnaAdresaException;

/**
 *
 * @author neiss
 */
public class LinuxIpRoute extends AbstraktniPrikaz {

    public LinuxIpRoute(AbstraktniPocitac pc, Konsole kon, List<String> slova, LinuxIp puv) {
        super(pc, kon, slova);
        this.puv=puv;
        parsujPrikaz();
        zkontrolujPrikaz();
        vykonejPrikaz();
        vypisChybovyHlaseni();
    }
    
    private LinuxIp puv;
    private boolean ladeni=true;

    //promenny parseru:
    private String slovo;
    boolean zadanaAdresa;

    /**
     * 1 - vypsat <br />
     * 2 - pridat adresu<br />
     * 3 - odebrat adresu<br />
     * 4 - flush <br />
     * 5 - vypsani helpu <br />
     * 6 - get <br />
     */
    private int akce=0;

    /**
     * 0 - v poradku <br />
     * 0. (tzn. 2^0) - nejakej nesmysl (treba i vicekrat zadana seznamAdresatuRet) <br />
     * 1. - spatny adresat <br />
     * 2. - napsano dev nebo via, ale nic po nem... <br />
     * 3. - zadano neexistujici rozhrani <br />
     * 4. - u akce add nebo del nezadan adresat <br />
     * 5. - nezadano rozhrani, kdyz by melo <br />
     * 6. - neznama akce (neni to show flush add ani del)<br />
     * 7. - spatna adresa parametru via<br />
     * 8. - seznamAdresatuRet se nemuze pridat, uz tam jedna stejna je <br />
     * 9. - mazana seznamAdresatuRet neni na rozhrani <br />
     * 10. - nevykonava se metoda zkontroluj <br />
     * 11. - nevykonava se metoda vykonejPrikaz (ukladam to sem jen kvuli prehlednosti) <br />
     */
    int navrKod=0; //je dobry pouzit funkci md

    //jeste nenastaveny parametry:
    String rozhrRet;
    List<String> seznamAdresatuRet=new ArrayList<String>();

    //spravne nastaveny parametry:
    IpAdresa adresat;
    SitoveRozhrani rozhr;
    IpAdresa brana;
    boolean nastavenaMaska=false; //jestli byla nastavena maska za lomitkem. Sice je defaultni, ale
            //nekdy je potreba vedet, jestli byla nebo nebyla nastavena

    //spatny parametry:
    String necoNavic;


    private void vypisChybovyHlaseni(){
        if(ladeni){
            kon.posliRadek(toString());
        }

        /*
         * Poradi vypisovani:
         * 1. neznámej příkaz (nk 6) - vypsat, utýct
         * 2. špatná adresa (nk 1), spatna adresa via (nk 7) - zalezi na poradi jejich zadani
         *      - potreba nejdriv vypsat adresata, po spatny via se stejne na spatnyho adresata uz neprijde
         * 3. nějakej nesmysl navíc (nk 0) -||-
         * 4. zadáno dev a nic po něm (nk 2)
         * 5. chybějící rozhraní (nk 5)
         * 6. nezadaná maska u del (nk 10) - vypíše se hlášení, ale vypisujou se i další hlášení, neutíká se
         * 7. neexistující rozhraní (nk 3)
         * 8. nezadaná adresa (nk 4)
         * 9. ostatni (nk 8, 7, 9) - nemuzou nastat soucasne
         *
         * Utika se pres returny
         */


    }

    /**
     * Kontroluje prikaz jenom v pripade, ze v parseru bylo vsechno v poradku. Jinak hned utece a da o tom
     * zpravu do navrKodu
     */
    private void zkontrolujPrikaz() {
        if(navrKod != 0){
            navrKod |= md(10);
            return;
        }
    }

    /**
     * Vykonava prikaz jenom v pripade, ze predtim bylo vsechno v poradku. Jinak hned utece a da o tom
     * zpravu do navrKodu.
     */
    @Override
    protected void vykonejPrikaz() {
        if(navrKod != 0){
            navrKod |= md(11);
            return;
        }
    }

    /**
     * Parsovani prikazu.
     * Pri volani podrazeny metody ta metoda dostava prvni ji uzitecnu slovo.
     * Vsechny akce (add, del, show, flush, get) se parsujou vicemene stejne, jen u get je zakazanej
     * parametr via.
     */
    private void parsujPrikaz() {
        slovo=dalsiSlovo();
        if(slovo.equals("")){   // nic nezadano - vsecho vypsat
            akce=1;
        } else if ("add".startsWith(slovo)){
            akce=2;
            slovo = dalsiSlovo();
            parsujParametry();
        } else if ("del".startsWith(slovo)){
            akce=3;
            slovo = dalsiSlovo();
            parsujParametry();
        } else if ("show".startsWith(slovo)){
            akce=1;
            slovo = dalsiSlovo();
            parsujParametry();
        } else if ("flush".startsWith(slovo)){
            akce=4;
            slovo = dalsiSlovo();
            parsujParametry();
        } else if ("get".startsWith(slovo)){
            akce=6;
            slovo = dalsiSlovo();
            parsujParametry();
        } else if ("help".startsWith(slovo)){
            akce=5;
            //dal se nepokracuje
        } else{
            necoNavic=slovo;
            navrKod |= md(6);
        }
    }

    /**
     * Parsuje vsechny parametry, tzn slova dev, via a adresu, na ne pak zavola specialni funkce
     */
    private void parsujParametry(){
        if (slovo.equals("")) {
            //konec prikazu, nic se nedeje...
        } else if (slovo.equals("dev")) {
            slovo=dalsiSlovo();
            parsujDev();
        } else if (slovo.equals("via")) {
            slovo=dalsiSlovo();
            parsujVia();
        } else { //vsechno ostatni se povazuje za adresu...
            parsujAdresu();
        }
    }

    /**
     * Parsuje adresu.
     * Predpoklada, ze ji nemuze prijit prazdnej String.
     */
    private void parsujAdresu() {
        if(zadanaAdresa && (akce==2 || akce == 3)){
            navrKod |=md(0);
            necoNavic=slovo;
            return;
        }
        zadanaAdresa = true;
        seznamAdresatuRet.add(slovo);
        slovo = dalsiSlovo();
        parsujParametry();
    }

    private void parsujVia() {
        if (slovo.equals("")) { //ip a a 1.1.1.1 dev
            navrKod|=md(2);
        } else {
            //nastovovani adresata:
            try{
                brana=new IpAdresa(slovo);
            }catch(SpatnaAdresaException ex){
                navrKod |= md(7);
                necoNavic=slovo;
                return; //POZOR!!!!!!!!! Tady se utika a konci parsovani, kdyz je spatna adresa
            }
            //dalsi pokracovani:
            parsujParametry();
        }
    }

    private void parsujDev() {
        if (slovo.equals("")) { //ip a a 1.1.1.1 dev
            navrKod|=md(2);
        } else {
            rozhrRet=slovo;
            //dalsi pokracovani:
            slovo=dalsiSlovo();
            parsujParametry();
        }
    }

    @Override
    public String toString(){
        String vratit = "--------------------------\r\n   Parametry prikazu ip route" +
                ":\r\n\tnavratovyKodParseru: "
                + rozlozNaLogaritmy2(navrKod);
        vratit += "\r\n\takce: "+akce;
        if(seznamAdresatuRet!=null)vratit += "\r\n\tzapsane adresy: "+seznamAdresatuRet;
        if(rozhrRet!=null)vratit +=  "\r\n\tzapsane rozhrani: "+rozhrRet;
        if(necoNavic!=null)vratit +=  "\r\n\tnecoNavic: "+necoNavic;

        if(adresat!=null)vratit += "\r\n\tnastavena adresa: "+adresat.vypisAdresuSMaskou();
        if(rozhr!=null)vratit +=  "\r\n\tnastavene rozhr: "+rozhr.jmeno;
        if(brana!=null)vratit +=  "\r\n\tnastavena brana: "+brana.vypisAdresu();
        vratit += "\r\n--------------------------";
        return vratit;
    }


}
