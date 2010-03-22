/*
 * Gegründet am Montag 15.3.2010.
 */

package prikazy;

import datoveStruktury.IpAdresa;
import datoveStruktury.RoutovaciTabulka;
import java.util.List;
import pocitac.AbstractPocitac;
import pocitac.Konsole;
import pocitac.SitoveRozhrani;

/**
 *
 * @author neiss
 */
public class LinuxRoute extends AbstraktniPrikaz{
    boolean ladiciVypisovani = true; //pro debug

    // pomocny promenny pro parser prikazu:
    private int uk=1; //ukazatel do seznamu slov, prvni slovo je to route
    private String slovo; //drzi si slovo ke zpracovani
    private boolean poDevNepokracovat=false; //kdyz je rozhrani zadano jen eth0, a ne dev eth0, tak uz nemuze
                                              //prijit zadnej dalsi prikaz jako gw nebo netmask

    //nastaveni prikazu:
    boolean minus_n=false;
    boolean minus_v=false;
    boolean minus_e=false;
    private boolean add=false;
    private boolean del=false;
    private String adr; //adresat
    private String brana;
    private String maska;
    private int pocetBituMasky;
    private SitoveRozhrani rozhr; // rozhrani
    private boolean nastavenaBrana=false; // jestli uz byla zadana brana
    private boolean nastavenoRozhrani=false; // jestli uz bylo zadano rozhrani
    private boolean nastavenaMaska=false;
    private boolean minusHost = false; //zadano -host
    private boolean minusNet = false; //zadano -net

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
      */
    int navratovyKod=0;
    

    LinuxRoute(AbstractPocitac pc, Konsole kon, List<String> slova) {
        super(pc, kon,slova);
        nastavPrikaz();
        vykonejPrikaz();
    }



    @Override
    protected void vykonejPrikaz() {
        if(!add && !del){
            kon.posliRadek(vypisTabulku());
            return;
        }
        if(ladiciVypisovani){
            kon.posliRadek(this.toString());
        }
    }
//*******************************************************************************************************
//metody na parsovani prikazu:

    /**
     * Precte prikaz a nastavi mu parametry.
     */
    private void nastavPrikaz() {
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
        add = true;
        nastavAddNeboDel();
    }

    private void nastavDel() {
        del=true;
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
        kon.posliRadek("flush neni podporovano");
    }

    private void nastavMinus_net() {
        minusNet=true;
        boolean bezChyby=true;
        //cteni stringu:
        if(slovo.contains("/")){ // slovo obsahuje lomitko -> mohla by to bejt adresa s maskou
            bezChyby=prectiIpSMaskou(slovo);
        }else{ // slovo neobsahuje lomitko -> mohla by to bejt samotna IP adresa
            if(IpAdresa.jeSpravnaIP(slovo, false)){ //samotna IP je spravna
                adr=slovo;
            }else{ //samotna IP neni spravna
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
            }else{ //cokoliv ostatniho, i nic, se povazuje za rozhrani
                poDevNepokracovat=true;
                nastavDev();
            }
        }


    }

    private void nastavMinus_host() {
        minusHost=true;
        if( ! IpAdresa.jeSpravnaIP(slovo,false)){
            kon.posliRadek(slovo+": unknown host");
            navratovyKod |= 4;
        }else{
            adr=slovo;
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
            }else{ //cokoliv ostatniho, i nic, se povazuje za rozhrani
                poDevNepokracovat=true;
                nastavDev();
            }
        }
    }

    private void nastavGw() {//ceka, ze ve slove je uz ta IP adresa
        if(nastavenaBrana){ //kontroluje se, jestli se to necykli s nastavDev()
            vypisKratkouNapovedu();
            navratovyKod |= 16;
            return;
        }
        nastavenaBrana=true;
        if ( ! IpAdresa.jeSpravnaIP(slovo, false)){
            kon.posliRadek(slovo+": unknown host");
            navratovyKod |= 8;
        }else{ //spravna brana
            brana=slovo;
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
        if(nastavenoRozhrani){ //kontroluje se, jestli se to necykli s nastavGw()
            vypisKratkouNapovedu();
            navratovyKod |= 16;
        }
        nastavenoRozhrani=true;
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
        if(nastavenaMaska){ // kontrola, jestli uz maska nebyla nastavena
            navratovyKod |= 512; //maska nastavovana dvakrat
            vypisKratkouNapovedu();
            return; //nic se nema nastavovat
        }
        nastavenaMaska=true;
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

    /**
     * Tahle metoda postupne vraci slova, podle vnitrni promenny uk. POcita s tim, ze prazdny
     * retezec ji nemuze prijit.
     * @return prazdny retezec, kdyz je na konci seznamu
     */
    private String dalsiSlovo(){
        String vratit;
        if( uk < slova.size() ){
            vratit = slova.get(uk);
            uk++;
        }else{
            vratit="";
        }
        return vratit;
    }

//*********************************************************************************************************
//dalsi funkce:

    private void vypisKratkouNapovedu() {
        kon.posliRadek("Tohle je kratka napoveda.");
    }
    private void vypisDelsiNapovedu() {
        kon.posliRadek("Tohle je delsi napoveda.");
    }
    
    /**
     * Kdyz obsahuje 
     * lomitko, pred lomitkem precte IP adresu a za lomitkem pocet bitu masky, zada to do tridnich promennejch 
     * adr a pocetBituMasky a vyplni nastavenaMaska na true. Kdyz je neco spatne, vrati false a nastavi
     * navratovy kod na |= 4;
     * @param adrm
     * @return
     */
    private boolean prectiIpSMaskou(String adrm){
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
                            nastavenaMaska = true;
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

    private String vypisTabulku() {

        String v = ""; //string na vraceni
        v += "Směrovací tabulka v jádru pro IP\n";
        v += "Adresát         Brána           Maska           Přízn Metrik Odkaz  Užt Rozhraní\n";
        int pocet = pc.routovaciTabulka.pocetZaznamu();
        for (int i = 0; i < pocet; i++) {
            RoutovaciTabulka.Zaznam z = pc.routovaciTabulka.vratZaznam(i);
            v += zarovnej(z.getAdresat().vypisIP(), 16);
            if (z.getBrana() == null) {
                v += zarovnej("0.0.0.0",16) + zarovnej(z.getAdresat().vypisMasku(),16) + "U     ";
            } else {
                v += zarovnej(z.getBrana().vypisIP(),16) + zarovnej(z.getAdresat().vypisMasku(),16) + "UG    ";
            }
            v += "0      0        0" + z.getRozhrani().jmeno + "\n";
        }
        return v;
        
        
    }

    @Override
    public String toString(){
        String vratit = "Parametry prikazy route:\n navratovyKodParseru: " + navratovyKod;
        vratit+="\n prepinace: ";
        if(minus_n)vratit+=" -n";if(minus_e)vratit+=" -e";if(minus_v)vratit+=" -v";
        if (adr != null) {
            vratit += "\n ip: " + adr;
        }
        if (nastavenaMaska) {
            vratit += "\n pocetBituMasky: " + pocetBituMasky;
            vratit += "\n maska: " + maska;
        }
        if(nastavenaBrana){
            vratit+="\n brana: "+brana;
        }
        if (nastavenoRozhrani) {
            if ( (navratovyKod & 32) ==32 ){ //rozhrani neexistuje
                vratit += "\n rozhrani neexistuje: " + rozhr.jmeno;
            }else{
                vratit += "\n rozhrani: " + rozhr.jmeno;
            }
        }

        return vratit;
    }




}
