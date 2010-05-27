/*
 * DODELAT:
 *      Parsovani slovicka default. HOTOVO
 *      src ve vypisovani
 */
package prikazy.linux;

import datoveStruktury.IpAdresa;
import datoveStruktury.RoutovaciTabulka;
import java.util.ArrayList;
import java.util.List;
import pocitac.*;
import prikazy.*;
import vyjimky.SpatnaAdresaException;
import vyjimky.SpatneVytvorenaAdresaException;

/**
 * Trida pro podprikaz route linxoveho prikazu ip.
 * @author neiss
 */
public class LinuxIpRoute extends AbstraktniPrikaz {

    public LinuxIpRoute(AbstraktniPocitac pc, Konsole kon, List<String> slova, LinuxIp puv) {
        super(pc, kon, slova);
        this.puv=puv;
        parsujPrikaz();
        zkontrolujPrikaz();
        if(ladeni){
            kon.posliRadek(toString());
        }
        vykonejPrikaz();
        vypisChybovyHlaseni();
    }
    
    private LinuxIp puv;
    private boolean ladeni=false;

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
     * 0. (tzn. 2^0) - nejakej nesmysl (treba i vicekrat zadana adresat). Vsechno se povazuje
     * za adresu, tenhle kod zadava metoda parsujAdresu, kdyz uz jednu zadanou ma a dalsi nemuze.
     * (zadava parser) <br />
     * 1. - spatny adresat (zadava parser) <br />
     * 2. - napsano dev nebo via, ale nic po nem... (zadava parser) <br />
     * 3. - zadano neexistujici rozhrani (kontrola) <br />
     * 4. -  <br />
     * 5. - chybejici parametry u add nebo del (kontrola) <br />
     * 6. - neznama akce (neni to show flush add ani del) (zadava parser) <br />
     * 7. - spatna adresa parametru via (zadava parser) <br />
     * 8. - adresat se nemuze pridat, uz v routovaci tabulce jeden stejnej je (provedeni) <br />
     * 9. - mazany zaznam (u akce del) na rozhrani neni (provedeni) <br />
     * 10. - nevykonava se metoda zkontroluj <br />
     * 11. - nevykonava se metoda vykonejPrikaz (ukladam to sem jen kvuli prehlednosti) <br />
     * 12. - u prikazu get volan zakazany parametr via (zadava parser) <br />
     * 13. - chybejici parametry u flush (kontrola) <br />
     * 14. - chybejici adresa u get (kontrola) <br />
     * 15. - zaznam typu UG (na branu) se nemuze pridat, protoze brana neni dosazitelna U priznakem
     * (zadava provedeni) <br />
     * 16. - nothing to flush (provedeni) <br />
     * 17. - get nenaslo adresu (provedeni) <br />
     * 18. - u get zadanej parametr dev - normalne spravnej, ale ja ho pro zmatenost nepodporuju (kontrola)
     */
    int navrKod=0; //je dobry pouzit funkci md

    //jeste nenastaveny parametry:
    String rozhrRet;
    

    //spravne nastaveny parametry:
    IpAdresa adresat=null; //nastavuje parser
    SitoveRozhrani rozhr; //nastavuje kontrola
    IpAdresa brana; //nastavuje parser
    boolean all=false; //bylo-li zadano slovicko all (vyznam u show a flush)

    //spatny parametry:
    String necoSpatne; //do tohodle stringu se ukladaj spatne nastaveny parametry: adresa, via, nesmysly...


    private void vypisChybovyHlaseni(){

        /*
         * Tahle metoda vypisuje jen chyby parseru a kontroly, provedeni si je vypisuje samo.
         * V tomhle prikaze se po kazdy chybe utece a dalsi se uz nevypisuje
         *
         * Poradi vypisovani:
         * Chyby najity v parserem (nemuze jich nastat vic soucasne):
         * 1. neznámej příkaz (nk 6) - vypsat, utýct
         * 2. špatná adresa adresata (nk 1), spatna adresa via (nk 7) - nemuzou nastat soucasne, protoze
         *      parser se pri prvni takovyhle chybe ukonci.
         * 3. nějakej nesmysl navíc (nk 0) -||-
         * 4. zadáno dev a nic po něm (nk 2)
         * Chyby najity kontrolou:
         * 5. neexistující rozhraní (nk 3)
         * 5. chybejici parametry u add nebo del (nk 5)
         * 6. 
         * 7. 
         * 8. nezadaná adresa, kdyz by mela (nk 4)
         * 9. ostatni (nk 8, 7, 9) - nemuzou nastat soucasne
         *
         * Utika se pres returny.
         */

        if (navrKod == 0) {
            return;
        }
      //parser:
        if ( (navrKod & md(6)) != 0 ) { //neznama akce
            kon.posliRadek("Command \"" + necoSpatne + "\" is unknown, try \"ip addr help\".");
            return;
        }
        if ( ( (navrKod & md(1)) != 0 ) || ( (navrKod & md(12)) != 0) ) { //spatna adresa
            kon.posliRadek("Error: an inet prefix is expected rather than  \"" + necoSpatne + "\"");
            return;
        }
        if ((navrKod & md(0)) != 0) { //nejakej nesmysl
            kon.posliRadek("Error: either \"local\" is duplicate, or \"" + necoSpatne + "\" is a garbage.");
            return;
        }
        if ((navrKod & md(2)) != 0) { // napsano dev a nic po nem
            kon.posliRadek("Command line is not complete. Try option \"help\"");
            return;
        }
      //kontrola:
        if ((navrKod & md(5)) != 0) { // chybejici parametry u add nebo del
            kon.posliRadek("RTNETLINK answers: No such device");
            return;
        }
        if ((navrKod & md(3)) != 0) { // spatne rozhrani
            kon.posliRadek("Cannot find device \""+rozhrRet+"\"");
            return;
        }
        if ((navrKod & md(13)) != 0) { // chybi parametry u flush
            kon.posliRadek("\"ip route flush\" requires arguments.");
            return;
        }
        if ((navrKod & md(18)) != 0) { // u get zadanej nepodporovanej parametr dev
            kon.posliServisne("Parametr dev u akce get je normalne mozny, neni ale simulatorem " +
                    "podporovany, protoze v pripade zadani nespravneho rozhrani vraci tezko zjistitelne " +
                    "nesmysly. Zadejte tedy prosim prikaz get bez tohoto parametru.");
            //tady nedavam return
        }
        if ((navrKod & md(14)) != 0) { // chybi adresa u get
            kon.posliRadek("need at least destination address");
            return;
        }


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

        //kontrola rozhrani, jestlize bylo zadano:
        if (rozhrRet != null) {
            rozhr = pc.najdiRozhrani(rozhrRet);
            if (rozhr == null) { //rozhrani nenalezeno napr. ip a a 1.1.1.1 dev dsa
                navrKod |= md(3);
            }
        }

        //spolecna kontrola pro add a del, kontroluje se hlavne, byla-li zadana cilova adresa:
        if(akce==2 || akce==3){
            //kontrola adresy:
            if(adresat==null){
                adresat=(new IpAdresa("0.0.0.0", 0)); //jestlize nebyl adresat zadan,
                    // hodi se sem implicitni - tzn default (0.0.0.0/0)
            }
        }

        //specialni kontrola pro akci add, kontroluju, bylo-li zadano dev nebo via:
        if(akce==2){
            if(brana==null && rozhrRet==null){ //kontroluju to radsi pres ten string, aby tam nebyly
                        //zbytecne 2 navrKody na neexistujici rozhrani
                navrKod |= md(5);
            }
        }

        //akce show nepotrebuje zadnou zvlastni kontrolu

        //kontrola pro akci flush, musi u ni bejt zadano aspon jedno: adresat, brana, rozhrani, all
        if(akce==4){
            if(adresat==null && rozhr==null && brana==null && !all){
                navrKod |= md(13);
            }
        }

        //kontrola pro akci get, musi u ni bejt zadana adresa:
        if(akce==6){
            if(adresat==null){
                navrKod |= md(14);
            }
            if(rozhr!=null){
                navrKod |= md(18);
            }
        }

    }

    /**
     * Vykonava prikaz jenom v pripade, ze predtim bylo vsechno v poradku. Jinak hned utece a da o tom
     * zpravu do navrKodu.
     * Sama si vypisuje chybovy hlaseni.
     */
    @Override
    protected void vykonejPrikaz() {
        if(navrKod != 0){
            navrKod |= md(11);
            return;
        }

        if(akce==2){//add
            if(pc.routovaciTabulka.existujeZaznamSAdresatem(adresat)!=null){
                navrKod |= md(8);
                kon.posliRadek("RTNETLINK answers: File exists");
            } else { //v poradku, zaznam se muze pridat
                if (brana == null) { //brana nezadana
                    pc.routovaciTabulka.pridejZaznam(adresat, rozhr);
                    // -> tohle uz musi projit, protoze se predtim zkontrolovalo, ze neexistuje
                    //    zaznam se stejnym adresatem
                } else {
                    int nk=0;
                    nk=pc.routovaciTabulka.pridejZaznam(adresat, brana, rozhr);
                    if(nk==2){//zaznam nejde pridat, protoze na brana neni dosazitelna
                        navrKod |= md(15);
                        kon.posliRadek("RTNETLINK answers: Network is unreachable");
                    }
                }
            }
        }
        if (akce == 3) {//del
            if (!pc.routovaciTabulka.smazZaznam(adresat, brana, rozhr)) {
                navrKod |= md(9);
                kon.posliRadek("RTNETLINK answers: No such process");
            }
        }
        if(akce==1){ //show - vypsani
            String v;
            for(int i=0; i<pc.routovaciTabulka.pocetZaznamu();i++){ //vypisuje abulku po radcich
                v="";
                RoutovaciTabulka.Zaznam z = pc.routovaciTabulka.vratZaznam(i);
                //radek se zobrazi jen za nejakejch podminek:
                if( (adresat==null || adresat.equals(z.getAdresat())) //adresat nezadan, nebo souhlasi
                        && (rozhr==null || rozhr==z.getRozhrani()) //rozhrani nezadano, nebo souhlasi
                        && (brana==null || brana.jeStejnaAdresa(z.getBrana())) ) //brana nezadana, nebo dobra
                {
                    v +=z.getAdresat().vypisAdresuSMaskou();
                    if(brana==null && z.getBrana()!=null){
                        // -> brana se vypise, jen kdyz nebyla zadana jako filtr a je zadana
                        v+=" via "+z.getBrana().vypisAdresu();
                    }
                    if(rozhr==null){ //rozhrani se vypise, jen kdyz nebylo zadano jako filtr
                        v+=" dev "+z.getRozhrani().jmeno;
                    }
                }
                if(!v.equals("")){
                    kon.posliRadek(v);
                }
            }
        }
        if (akce == 4) { //flush
            List<RoutovaciTabulka.Zaznam> keSmazani = new ArrayList<RoutovaciTabulka.Zaznam>();
            for (int i = 0; i < pc.routovaciTabulka.pocetZaznamu(); i++) { //vypisuje abulku po radcich
                RoutovaciTabulka.Zaznam zazn = pc.routovaciTabulka.vratZaznam(i);
                //radek se smaze jen za nejakejch podminek:
                if ((adresat == null || adresat.equals(zazn.getAdresat())) //adresat nezadan, nebo souhlasi
                        && (rozhr == null || rozhr == zazn.getRozhrani()) //rozhrani nezadano, nebo souhlasi
                        && (brana == null || brana.jeStejnaAdresa(zazn.getBrana()))) //brana nezadana, nebo dobra
                {
                    keSmazani.add(zazn); //zatim se to jen oznacuje, aby to spravne fungovalo, kdyz
                        //se jede forcyklem...
                }
            }
            if (keSmazani.size() == 0) {
                navrKod |= md(16);
                kon.posliRadek("Nothing to flush.");
            } else {
                for (RoutovaciTabulka.Zaznam z : keSmazani) {
                    pc.routovaciTabulka.smazZaznam(z);
                }
            }
        }
        if (akce==5){
            vypisHelp();
        }
        if(akce==6){ //get
            RoutovaciTabulka.Zaznam zazn=pc.routovaciTabulka.najdiSpravnejZaznam(adresat);
            if(zazn==null){
                kon.posliRadek("RTNETLINK answers: Network is unreachable");
                navrKod |=md(17);
            }else{ //zaznam se podarilo najit, vypise se
                String prvni=adresat.vypisAdresu();
                if(zazn.getBrana()!=null){
                    prvni+= " via "+zazn.getBrana().vypisAdresu();
                }
                prvni+=" dev "+zazn.getRozhrani().jmeno+
                        "  src "+zazn.getRozhrani().vratPrvni().vypisAdresu();
                kon.posliRadek(prvni);
                kon.posliRadek("    cache  mtu 1500 advmss 1460 hoplimit 64");
            }
        }
    }

    /**
     * Parsovani prikazu. <br />
     * Pri volani podrazeny metody ta metoda dostava prvni ji uzitecnu slovo. <br />
     * Vsechny akce (add, del, show, flush, get) se parsujou vicemene stejne, jen u get je zakazanej
     * parametr via. <br />
     * IP se parsujou primo v parseru, existence rozhrani se tady ale nekontroluje. <br />
     * Jakmile parser narazi na jednu chybu, dal uz nepokracuje. <br />
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
            if(slovo.equals("all")){
                all=true;
                slovo = dalsiSlovo();
            }
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
            necoSpatne=slovo;
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
        } else if ( slovo.equals("all") && (akce==1||akce==4 ||akce==6) ) {
            all=true;
            slovo=dalsiSlovo();
            parsujParametry();
        } else { //vsechno ostatni se povazuje za adresu...
            parsujAdresu();
        }
    }

    /**
     * Parsuje adresu.
     * Predpoklada, ze ji nemuze prijit prazdnej String.
     */
    private void parsujAdresu() {
        IpAdresa vytvarena;
        if(zadanaAdresa && (akce==2 || akce == 3)){
            navrKod |=md(0);
            necoSpatne=slovo;
            return;
        }
        zadanaAdresa = true;
        if (slovo.equals("default")) {//kdyz je to de
            vytvarena = new IpAdresa("0.0.0.0", 0);
        } else {
            if (slovo.startsWith("default")) {
            }

            try {
                vytvarena = new IpAdresa(slovo, 32, false);
            } catch (SpatneVytvorenaAdresaException ex) {
                navrKod |= md(1);
                necoSpatne = slovo;
                return;
            }
        }
        adresat = vytvarena;
        slovo = dalsiSlovo();
        parsujParametry();
    }


    private void parsujVia() {
        if(akce==6){ //u akce get neni tenhle parametr povolenej
            navrKod |= md(12); //jen pro poradek, je to ale jinak brany jako adresa
            necoSpatne="via";
            return; //parsovani se na spatnou adresu konci
        }
        if (slovo.equals("")) { //ip a a 1.1.1.1 via
            navrKod|=md(2);
        } else {
            //nastovovani brany:
            try{
                brana=new IpAdresa(slovo);
            }catch(SpatnaAdresaException ex){
                navrKod |= md(7);
                necoSpatne=slovo;
                return; //POZOR!!!!!!!!! Tady se utika a konci parsovani, kdyz je spatna adresa
            }
            //dalsi pokracovani:
            slovo=dalsiSlovo();
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
                ":\r\n\tnavratovy kod po parsovani a kontrole: "
                + rozlozNaLogaritmy2(navrKod);
        vratit += "\r\n\takce: "+akce;
        if(rozhrRet!=null)vratit +=  "\r\n\tzapsane rozhrani: "+rozhrRet;
        if(necoSpatne!=null)vratit +=  "\r\n\tnecoNavic: "+necoSpatne;

        if(adresat!=null)vratit += "\r\n\tnastaveny adresat: "+adresat.vypisAdresuSMaskou();
        if(rozhr!=null)vratit +=  "\r\n\tnastavene rozhr: "+rozhr.jmeno;
        if(brana!=null)vratit +=  "\r\n\tnastavena brana: "+brana.vypisAdresu();
        vratit += "\r\n--------------------------";
        return vratit;
    }

    private void vypisHelp() {
        kon.posliRadek("Usage: ip route { list | flush } SELECTOR");
        kon.posliRadek("       ip route get ADDRESS [ from ADDRESS iif STRING ]");
        kon.posliRadek("                            [ oif STRING ]  [ tos TOS ]");
        kon.posliRadek("       ip route { add | del | change | append | replace | monitor } ROUTE");
        kon.posliRadek("SELECTOR := [ root PREFIX ] [ match PREFIX ] [ exact PREFIX ]");
        kon.posliRadek("            [ table TABLE_ID ] [ proto RTPROTO ]");
        kon.posliRadek("            [ type TYPE ] [ scope SCOPE ]");
        kon.posliRadek("ROUTE := NODE_SPEC [ INFO_SPEC ]");
        kon.posliRadek("NODE_SPEC := [ TYPE ] PREFIX [ tos TOS ]");
        kon.posliRadek("             [ table TABLE_ID ] [ proto RTPROTO ]");
        kon.posliRadek("             [ scope SCOPE ] [ metric METRIC ]");
        kon.posliRadek("INFO_SPEC := NH OPTIONS FLAGS [ nexthop NH ]...");
        kon.posliRadek("NH := [ via ADDRESS ] [ dev STRING ] [ weight NUMBER ] NHFLAGS");
        kon.posliRadek("OPTIONS := FLAGS [ mtu NUMBER ] [ advmss NUMBER ]");
        kon.posliRadek("           [ rtt TIME ] [ rttvar TIME ] [ window NUMBER]");
        kon.posliRadek("           [ cwnd NUMBER ] [ hoplimit NUMBER ] [ initcwnd NUMBER ]");
        kon.posliRadek("           [ ssthresh NUMBER ] [ realms REALM ] [ src ADDRESS ]");
        kon.posliRadek("           [ rto_min TIME ]");
        kon.posliRadek("TYPE := [ unicast | local | broadcast | multicast | throw |");
        kon.posliRadek("          unreachable | prohibit | blackhole | nat ]");
        kon.posliRadek("TABLE_ID := [ local | main | default | all | NUMBER ]");
        kon.posliRadek("SCOPE := [ host | link | global | NUMBER ]");
        kon.posliRadek("FLAGS := [ equalize ]");
        kon.posliRadek("MP_ALGO := { rr | drr | random | wrandom }");
        kon.posliRadek("NHFLAGS := [ onlink | pervasive ]");
        kon.posliRadek("RTPROTO := [ kernel | boot | static | NUMBER ]");
        kon.posliRadek("TIME := NUMBER[s|ms|us|ns|j]");
    }


}
