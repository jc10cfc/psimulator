/*
 * Gegründet am Montag 15.3.2010.
 * 
 * Jeste dodelat:
 *      i -host by mel umet zpracovat adresu, kdyz mu prijde s maskou - zatím řešený testem na lomítko
 */

package prikazy.linux;

import prikazy.*;
import datoveStruktury.IpAdresa;
import datoveStruktury.RoutovaciTabulka;
import java.util.List;
import pocitac.AbstraktniPocitac;
import pocitac.Konsole;
import pocitac.SitoveRozhrani;

/**
 * Linuxovy prokaz route.
 * @author neiss
 */
public class LinuxRoute extends AbstraktniPrikaz{
    boolean ladiciVypisovani = false; //pro debug

    // pomocny promenny pro parser prikazu:
    private int uk=1; //ukazatel do seznamu slov, prvni slovo je to route
    private String slovo; //drzi si slovo ke zpracovani
    private boolean poDevNepokracovat=false; //kdyz je rozhrani zadano jen eth0, a ne dev eth0, tak uz nemuze
                                              //prijit zadnej dalsi prikaz jako gw nebo netmask

    //nastaveni prikazu:
    boolean minus_n=false;
    boolean minus_v=false;
    boolean minus_e=false;
    private String adr; //adresat
    private String maska;
    private int pocetBituMasky;
    private SitoveRozhrani rozhr=null; // rozhrani
    private boolean nastavovanaBrana=false; // jestli uz byla zadana brana
    private boolean nastavovanoRozhrani=false; // jestli uz bylo zadano rozhrani
    private boolean nastavovanaMaska=false;
    private boolean minusHost = false; //zadano -host
    private boolean minusNet = false; //zadano -net
    private IpAdresa ipAdresa;
    private IpAdresa brana;
    boolean defaultni=false; //jestli neni defaultni routa (0.0.0.0/0)

    /**
     * Je to pole bitu (bity pocitany odzadu od nuly, jako mocnina):<br />
     * nevyplneno (0) - zadna akce, jenom vypsat <br />
     * 0. bit (1) - add <br />
     * 1. bit (2) - del <br />
     * 2. bit (4) - flush <br />
     */
    private int akce = 0;

    /**
     * Je to pole bitu (bity pocitany odzadu od nuly, jako mocnina):<br />
     * 0. bit (1) - nespravnej prepinac <br />
     * 1. bit (2) - malo parametru u akce <br />
     * 2. bit (4) - spatny adresat, spatna maska za lomitkem <br />
     * 3. bit (8) - spatna brana <br />
     * 4. bit (16) - brana zadavana vice nez jednou <br />
     * 5. bit (32) - nezname rozhrani <br />
     * 6. bit (64) - rozhrani zadano bez dev a pak jeste neco pokracovalo, nic se nesmi nastavit <br />
     * 7. bit (128) - nejakej nesmysl navic <br />
     * 8. bit (256) - pri parametru -host byla zadana maska -> nic neprovadet <br />
     * 9. bit (512) - maska zadavana vice nez jednou (jakymkoliv zpusobem) -> nic nenastavovat <br />
     * 10. bit (1024) - maska je nespravna <br />
     * 11. bit (2048) - IP adresata neni cislem site
     */
    int navratovyKod=0;

    /**
     * 1 - stejnej zaznam existuje, resp. neexistuje (u del)
     * 2 - brana neni dosazitelna U priznakem
     * 4 - zaznam ke smazani neexistuje
     */
    int navratovyKodProvedeni=0; 

    
    public LinuxRoute(AbstraktniPocitac pc, Konsole kon, List<String> slova) {
        super(pc, kon,slova);
        parsujPrikaz();
        vykonejPrikaz();
    }



    @Override
    protected void vykonejPrikaz() {
        if (ladiciVypisovani) {
            kon.posliRadek(this.toString());
        }
        if (navratovyKod == 0) { //bez chyby
            if (akce == 0) { //nic nedelat, jenom vypsat
                vypisTabulku();
            }
            if (akce == 1) { //add
                if (brana == null) { //brana nezadana
                    navratovyKodProvedeni=pc.routovaciTabulka.pridejZaznam(ipAdresa, rozhr);
                } else {
                    navratovyKodProvedeni=pc.routovaciTabulka.pridejZaznam(ipAdresa, brana, rozhr);
                }
                if(navratovyKodProvedeni == 1){
                    kon.posliRadek("SIOCADDRT: File exists");
                }else if(navratovyKodProvedeni == 2){
                    kon.posliRadek("SIOCADDRT: No such process");
                }
            }else if (akce==2){
                if ( ! pc.routovaciTabulka.smazZaznam(ipAdresa, brana, rozhr) ){
                    kon.posliRadek("SIOCDELRT: No such process");
                    navratovyKodProvedeni=4;
                }
            }else if(akce==4){
                pc.routovaciTabulka.smazVsechnyZaznamy();
            }
        }
    }
//*******************************************************************************************************
//metody na parsovani prikazu:

    /**
     * Precte prikaz a nastavi mu parametry. Rovnou kontroluje, spravnost parametru.
     */
    private void parsujPrikaz() {
        // prepinace:
        slovo = dalsiSlovo();
        while( slovo.length()>0 && slovo.charAt(0)=='-'){
            if( slovo.equals("-n") || slovo.equals("--numeric") ){
                minus_n=true;
            }else if( slovo.equals("-v") || slovo.equals("--verbose") ){
                minus_v=true;
            }else if( slovo.equals("-e") || slovo.equals("--extend") ){
                minus_e=true;
            }else{
                kon.posliRadek("route: invalid option -- "+slovo); //neznamej prepinac
                vypisDelsiNapovedu();
                navratovyKod=navratovyKod|1;
                return;
            }
            slovo=dalsiSlovo();
        }
        //akce:
        if ( slovo.equals("add") ){
            slovo=dalsiSlovo();
            nastavAdd();
        }
        if ( slovo.equals("del") ){
            slovo=dalsiSlovo();
            nastavDel();
        }
        if ( slovo.equals("flush") ){
            slovo=dalsiSlovo();
            nastavFlush();
        }
    }


    private void nastavAdd() { //i ukazuje na posici prvniho prvku za add
        akce |= 1;
        nastavAddNeboDel();
    }

    private void nastavDel() {
        akce |= 2;
        nastavAddNeboDel();
    }

    /**
     * Protoze add a del maji stejnou syntaxi, spolecnej kod z jejich metod jsem hodil do tyhle metody.
     */
    private void nastavAddNeboDel(){
        if(slovo.equals("")){ //konec
            navratovyKod=navratovyKod|2;
            vypisKratkouNapovedu();
        }else if(slovo.equals("-net")){
            slovo=dalsiSlovo();
            nastavMinus_net();
        }else if(slovo.equals("-host")){
            slovo=dalsiSlovo();
            nastavMinus_host();
        }else { //cokoliv jinyho se povazuje za adresu adresata - hosta
            nastavMinus_host();
        }
    }

    private void nastavFlush() {
        kon.posliRadek("Flush normalne neni podporovano.");
        akce=4;
    }

    private void nastavMinus_net() {
        minusNet=true;
        boolean bezChyby=true;
        //cteni stringu:
        if( slovo.equals("default") || slovo.equals("0.0.0.0") || slovo.equals("0.0.0.0/0") ){ //default
            nastavDefault();
        }else if(slovo.contains("/")){ // slovo obsahuje lomitko -> mohla by to bejt adresa s maskou
            bezChyby=prectiIpSMaskou(slovo);
        }else{ // slovo neobsahuje lomitko -> mohla by to bejt samotna IP adresa
            if(IpAdresa.jeSpravnaIP(slovo, false)){ //samotna IP je spravna
                adr=slovo;
            }else{ //samotna IP neni spravna
                kon.posliRadek(adr+": unknown host");
                navratovyKod |= 4; //spatny adresat
                bezChyby=false;
            }
        }
        //adresa je prectena, kdyz je vsechno v poradku, pokracuje se dal:
        if(bezChyby) {
            slovo=dalsiSlovo();
            if(slovo.equals("gw")){
                slovo=dalsiSlovo();
                nastavGw();
            }else if(slovo.equals("dev")){
                slovo=dalsiSlovo();
                nastavDev();
            }else if(slovo.equals("netmask")){
                slovo=dalsiSlovo();
                nastavNetmask();
            }else if(slovo.equals("") && akce ==2){ //prazdnej retezec u akce del
                //konec prikazu
            }else{ //cokoliv ostatniho, i nic, se povazuje za rozhrani
                poDevNepokracovat=true;
                nastavDev();
            }
            //tedka je jeste nutno zjistit, jestli byla nastavena maska
            if(nastavovanaMaska && navratovyKod==0 && ! defaultni){
                nastavAdresu();
            }else{
                navratovyKod |=4;
                kon.posliRadek("SIOCADDRT: Invalid argument");
            }
        }


    }

    private void nastavMinus_host() { //predpokladam, ze ve slove je ulozena uz ta adresa
        minusHost=true;
        boolean chyba=false;
        if(slovo.equals("default")){
            nastavDefault();
        }else if( ! IpAdresa.jeSpravnaIP(slovo,false)){ //adresa je spatna
            if(slovo.contains("/")){ //kdyz je zadana IP adresa s maskou (zatim na to kaslu a kontroluju jen lomitko)
                                     //vypise se jina hlaska, ney normalne.
                kon.posliRadek("route: síťová maska nedává smysl, když cílem je cesty počítač");
                vypisDelsiNapovedu();
                navratovyKod |= 256;
            }else{
                kon.posliRadek(slovo+": unknown host");
                navratovyKod |= 4;
            }
            chyba=true;
        }else{ //adresa je dobra
            adr=slovo;
            ipAdresa=new IpAdresa(adr,32); // a adresa se rovnou vytvori
        }
        if(!chyba){ //kdyz nenastala chyba, tak se rozhoduje, co bude dal
            slovo=dalsiSlovo();
            if(slovo.equals("gw")){
                slovo=dalsiSlovo();
                nastavGw();
            }else if(slovo.equals("dev")){
                slovo=dalsiSlovo();
                nastavDev();
            }else if(slovo.equals("netmask")){ //on to pozna a hodi chybu
                kon.posliRadek("route: síťová maska nedává smysl, když cílem je cesty počítač");
                navratovyKod |= 128; //nejakej dalsi nesmysl
            }else if(slovo.equals("") && akce ==2){ //prazdnej retezec u akce del
                //konec prikazu
            }else{ //cokoliv ostatniho, i nic, se povazuje za rozhrani
                poDevNepokracovat=true;
                nastavDev();
            }
        }
    }

    private void nastavGw() {//ceka, ze ve slove je uz ta IP adresa
        if(nastavovanaBrana){ //kontroluje se, jestli se to necykli s nastavDev()
            vypisKratkouNapovedu();
            navratovyKod |= 16;
            return;
        }
        nastavovanaBrana=true;
        boolean chyba=false;
        try{
            brana = new IpAdresa(slovo,32);
        }catch (RuntimeException e){
            chyba = true;
            if(ladiciVypisovani){
                pc.vypis("doslo na vyjimku");
                e.printStackTrace();
            }
        }
        if ( chyba ){
            kon.posliRadek(slovo+": unknown host");
            navratovyKod |= 8;
        }else{ //spravna brana
            slovo=dalsiSlovo();
            if(slovo.equals("dev")){
                slovo=dalsiSlovo();
                nastavDev();
            }else if(slovo.equals("netmask")){
                slovo=dalsiSlovo();
                nastavNetmask();
            }else if(slovo.equals("")){ //konec prikazu
            }else{ //vsechno ostatni se povazuje za nazev rozhrani
                poDevNepokracovat=true; //zadano bez dev -> uz se nemuze pokracovat
                nastavDev();
            }
        }
    }

    private void nastavDev() {
        if(nastavovanoRozhrani){ //kontroluje se, jestli se to necykli s nastavGw()
            vypisKratkouNapovedu();
            navratovyKod |= 16;
        }
        nastavovanoRozhrani=true;
        rozhr=pc.najdiRozhrani(slovo);
        if(rozhr==null){ // rozhrani nebylo nalezeno
            if(ladiciVypisovani)rozhr=new SitoveRozhrani(slovo, null, null);
            kon.posliRadek("SIOCADDRT: No such device");
            navratovyKod |= 32;
        }else{ //rozhrani je spravne a je jiz ulozeno v promenne rozhr
            slovo=dalsiSlovo();
            if(slovo.equals("gw")){
                if(poDevNepokracovat){ //rozhrani bylo zadano bez dev -> to uz se pak nesmi pokracovat
                    vypisKratkouNapovedu();
                    navratovyKod |=64;
                }else{
                    slovo=dalsiSlovo();
                    nastavGw();
                }
            }else if(slovo.equals("netmask")){
                if(poDevNepokracovat){ //rozhrani bylo zadano bez dev -> to uz se pak nesmi pokracovat
                    vypisKratkouNapovedu();
                    navratovyKod |=64;
                }else{
                    slovo=dalsiSlovo();
                    nastavNetmask();
                }
            }else if(slovo.equals("")){ //konec prikazu
                //v poradku, konci se
            }else{ //nejakej dalsi nesmysl
                vypisKratkouNapovedu();
                navratovyKod |=128; //nic se neprovede
            }
        }
    }

    private void nastavNetmask(){
        if(minusHost){ // kontrola, jestli to vubec muzu nastavovat
            navratovyKod |= 256;
            kon.posliRadek("route: síťová maska nedává smysl, když cílem je cesty počítač");
            vypisDelsiNapovedu();
            return; //nic se nema nastavovat
        }
        if(nastavovanaMaska){ // kontrola, jestli uz maska nebyla nastavena
            navratovyKod |= 512; //maska nastavovana dvakrat
            vypisKratkouNapovedu();
            return; //nic se nema nastavovat
        }
        nastavovanaMaska=true;
        if ( ! IpAdresa.jeSpravnaIP(slovo, true)){
            kon.posliRadek("route: síťová maska "+slovo+"je nesprávná");
            navratovyKod |= 1024;
        }else{ //spravna brana
            maska=slovo;
            slovo=dalsiSlovo();
            if(slovo.equals("dev")){
                slovo=dalsiSlovo();
                nastavDev();
            }if(slovo.equals("gw")){
                slovo=dalsiSlovo();
                nastavGw();
            }else if(slovo.equals("")){ //konec prikazu
            }else{ //vsechno ostatni se povazuje za nazev rozhrani
                poDevNepokracovat=true; //zadano bez dev -> uz se nemuze pokracovat
                nastavDev();
            }
        }
    }

    private void nastavDefault() { //slouzi k nastavovani deafult
        adr="default";
        pocetBituMasky=0;
        ipAdresa=new IpAdresa("0.0.0.0",0); // a adresa se rovnou vytvori
        defaultni=true;
        nastavovanaMaska=true;
    }


//*********************************************************************************************************
//dalsi funkce:
    
    /**
     * Kdyz obsahuje 
     * lomitko, pred lomitkem precte IP adresu a za lomitkem pocet bitu masky, zada to do tridnich promennejch 
     * adr a pocetBituMasky a vyplni nastavovanaMaska na true. Kdyz je neco spatne, vrati false a nastavi
     * navratovy kod na |= 4. Kdyz vsechno probehne v poradku, tak se nakonec pokusi vytvori IP adresu adresata.
     * @param adrm
     * @return
     */
    private boolean prectiIpSMaskou(String adrm){
        nastavovanaMaska = true;
        int lomitko=adrm.indexOf('/');
        if ( lomitko == -1 ) {// string musi obsahovat lomitko
            throw new RuntimeException("Tohle by nikdy nemelo nastat.");
        }else{
            if ( lomitko < adrm.length()-1 ) { // lomitko nesmi byt poslednim znakem retezce
                String adresa=adrm.substring(0,lomitko);
                String maska=adrm.substring(lomitko+1, adrm.length());
                if ( IpAdresa.jeSpravnaIP(adresa, false) ){ //adresa je spravna
                    boolean chyba = false;
                    try {        // pokus o parsovani masky
                        pocetBituMasky=Integer.parseInt(maska); //parsuje se jako integer
                    } catch (NumberFormatException ex ) { //integer se nepovedlo zparsovat
                        chyba=true; //kdyz to neni integer
                        vypisKratkouNapovedu(); //napr: route add -net 128.0.0.0/3d dev eth0
                    }
                    if ( ! chyba ) { //vsechno v poradku, muze se to nastavit
                        adr=adresa;
                        pocetBituMasky = pocetBituMasky % 32; //opravdu to tak funguje, dokonce i se zapornejma
                                                                //cislama
                        if( pocetBituMasky == 0 ) { //tohle jediny neni povoleny, opravdu
                            kon.posliRadek("SIOCADDRT: Invalid argument");//napr: route add -net 128.0.0.0/64 dev eth0
                        }else{ //konecne vsechno spravne
                            return true; // NAVRAT Z METODY, KDYZ JE VSECHNO SPRAVNE
                        }
                    }
                }else{ //adresa neni spravna
                    kon.posliRadek(adresa+": Unknown host");
                }
            }else{ //kdyz je lomitko poslednim znakem retezce
                kon.posliRadek("SIOCADDRT: Invalid argument"); //napr: route add -net 1.0.0.0/ dev eth0
            }
        }
        // kdyz se vyskytne nejaka chyba:
        navratovyKod |= 4; //spatny adresat
        return false;
    }

    /**
     * Tahlecta metoda vezme String adr a String maska nebo int pocetBituMasky a udela z nich instanci
     * tridy IpAdresa.
     */
    private void nastavAdresu(){
        if (!nastavovanaMaska){
            throw new RuntimeException("K tomuhle by nikdy nemelo dojit. Tahleta metoda se vola, az kdyz" +
                    "je adresa i maska nastavena.");
        }
        if(navratovyKod == 0){ //doted musi bejt vsechno v poradku
            if(maska != null){//maska byla zadana parametrem netmask
                ipAdresa = new IpAdresa(adr, maska);
            }else{//maska byla zadana za lomitkem
                ipAdresa = new IpAdresa( adr, pocetBituMasky);
            }
            if( ! ipAdresa.jeCislemSite() ) { //adresa neni cislem site, to je chyba
                navratovyKod |= 2048; //adresat neni cislem site
                kon.posliRadek("route: síťová maska nevyhovuje adrese cesty");
                vypisDelsiNapovedu();
            }
        }
    }

    private void vypisTabulku() {

        String v; //string na vraceni
        kon.posliRadek("Směrovací tabulka v jádru pro IP");
        kon.posliRadek("Adresát         Brána           Maska           Přízn Metrik Odkaz  Užt Rozhraní");
        int pocet = pc.routovaciTabulka.pocetZaznamu();
        for (int i = 0; i < pocet; i++) {
            v="";
            RoutovaciTabulka.Zaznam z = pc.routovaciTabulka.vratZaznam(i);
            v += zarovnej(z.getAdresat().vypisAdresu(), 16);
            if (z.getBrana() == null) {
                v += zarovnej("0.0.0.0",16) + zarovnej(z.getAdresat().vypisMasku(),16) + "U     ";
            } else {
                v += zarovnej(z.getBrana().vypisAdresu(),16) + zarovnej(z.getAdresat().vypisMasku(),16) + "UG    ";
            }
            v += "0      0        0 " + z.getRozhrani().jmeno;
            kon.posliRadek(v);
        }  
    }

    /**
     * Jen pro ladeni.
     * @return
     */
    @Override
    public String toString(){
        String vratit = "   Parametry prikazu route:\r\n\tnavratovyKodParseru: " 
                + rozlozNaMocniny2(navratovyKod);
        vratit += "\r\n\takce (1=add, 2=del): " + akce;
        vratit+="\r\n\tprepinace: ";
        if(minus_n)vratit+=" -n";if(minus_e)vratit+=" -e";if(minus_v)vratit+=" -v";
        if (adr != null) {
            vratit += "\r\n\tip: " + adr;
            if (ipAdresa != null) {
                vratit += "\r\n\tvypsana ipAdresa, ktera se nastavi: " + ipAdresa.vypisAdresuSMaskou();
            }else{
                vratit += "\r\n\tvypsana ipAdresa, ktera se nastavi: NEPODARILO SE NASTAVIT";
            }
        }
        if (nastavovanaMaska) {
            vratit += "\r\n\tpocetBituMasky: " + pocetBituMasky;
            vratit += "\r\n\tmaska: " + maska;
        }
        if(nastavovanaBrana){
            if(brana != null){
                vratit+="\r\n\tbrana: "+brana.vypisAdresuSMaskou();
            }else{
                vratit+="\r\n\tbrana: null";
            }
        }
        if (nastavovanoRozhrani) {
            if ( (navratovyKod & 32) ==32 ){ //rozhrani neexistuje
                vratit += "\r\n\trozhrani neexistuje: " + rozhr.jmeno;
            }else{
                vratit += "\r\n\trozhrani: " + rozhr.jmeno;
            }
        }

        return vratit;
    }

    /**
     * Tyto metody byly udělány nahrazením v Kate. Znak pro začátek řádku je ^ a pro konec řádku $.
     */
    private void vypisKratkouNapovedu() {
        kon.posliRadek("Použití: inet_route [-vF] del {-host|-net} Cíl[/prefix] [gw Gw] [metrika M] [[dev] If]");
        kon.posliRadek("       inet_route [-vF] add {-host|-net} Cíl[/prefix] [gw Gw] [metrika M]");
        kon.posliRadek("                              [netmask N] [mss Mss] [window W] [irtt I]");
        kon.posliRadek("                              [mod] [dyn] [reinstate] [[dev] If]");
        kon.posliRadek("       inet_route [-vF] add {-host|-net} Cíl/[prefix] [metrika M] reject");
        kon.posliRadek("       inet_route [-FC] flush      NENÍ podporováno");
    }

    private void vypisDelsiNapovedu() {
        kon.posliRadek("Použití: route [-nNvee] [-FC] [<AF>]         Zobrazí směrovací tabulky v jádru");
        kon.posliRadek("       route [-v] [-FC] {add|del|flush} ...  Změní směrovací tabulku pro AF.");
        kon.posliRadek("");
        kon.posliRadek("       route {-h|--help [<AF>]               Nápověda pro použití s AF.");
        kon.posliRadek("       route {-V|--version}                  Vypíše označení verze a autora");
        kon.posliRadek("                                             programu.");
        kon.posliRadek("");
        kon.posliRadek("        -v, --verbose            bude vypisovat podrobné zprávy");
        kon.posliRadek("                                 o činnosti");
        kon.posliRadek("        -n, --numeric nepřekládat číselné adresy na jména");
        kon.posliRadek("        -e, --extend             vypíše podrobnější informace");
        kon.posliRadek("        -F, --fib                zobrazí Forwarding Infomation Base");
        kon.posliRadek("                                  (implicitní)");
        kon.posliRadek("        -C, --cache              místo FIB zobrazí směrovací cache");
        kon.posliRadek("");
        kon.posliRadek("  <AF>=Use '-A <af>' or '--<af>'; default: inet");
        kon.posliRadek("  Seznam možných tříd adres (podporujících směrování):");
        kon.posliRadek("    inet (DARPA Internet) inet6 (IPv6) ax25 (AMPR AX.25)");
        kon.posliRadek("    netrom (AMPR NET/ROM) ipx (Novell IPX) ddp (Appletalk DDP)");
        kon.posliRadek("    x25 (CCITT X.25)");

    }

}
