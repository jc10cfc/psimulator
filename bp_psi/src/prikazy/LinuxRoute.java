/*
 * Gegründet am Montag 15.3.2010.
 */

package prikazy;

import datoveStruktury.IpAdresa;
import java.util.List;
import pocitac.AbstractPocitac;
import pocitac.Konsole;
import pocitac.SitoveRozhrani;

/**
 *
 * @author neiss
 */
public class LinuxRoute extends AbstraktniPrikaz{
    // pomocny promenny pro parser prikazu:
    private int uk=1; //ukazatel do seznamu slov, prvni slovo je to route
    private String slovo; //drzi si slovo ke zpracovani
    private boolean poDevNepokracovat=false; //kdyz je rozhrani zadano jen eth0, a ne dev eth0, tak uz nemuze
                                              //prijit zadnej dalsi prikaz jako gw nebo netmask

    //nastaveni prikazu:
    boolean minus_n=false;

    /**
     * Je to pole bitu (bity pocitany odzadu od nuly, jako mocnina):<br />
     * 0. bit (1) - nespravnej prepinac <br />
     * 1. bit (2) - malo parametru u akce <br />
     * 2. bit (4) - spatny adresat <br />
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
    
    private String adr; //adresat
    private String brana;
    private String maska;
    private SitoveRozhrani rozhr; // rozhrani
    private boolean nastavenaBrana=false; // jestli uz byla zadana brana
    private boolean nastavenoRozhrani=false; // jestli uz bylo zadano rozhrani
    private boolean nastavenaMaska=false;
    private boolean minusHost = false; //zadano -host
    private boolean minusNet = false; //zadano -net
    

    LinuxRoute(AbstractPocitac pc, Konsole kon, List<String> slova) {
        super(pc, kon,slova);
        nastavPrikaz();
        vykonejPrikaz();
    }



    @Override
    protected void vykonejPrikaz() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
//*******************************************************************************************************
//metody na parsovani prikazu:

    /**
     * Precte prikaz a nastavi mu parametry.
     */
    private void nastavPrikaz() {
        // prepinace:
        slovo = dalsiSlovo();
        while( slovo!=null && slovo.charAt(0)=='-'){
            if( slovo.equals("-n") || slovo.equals("--numeric") ){
                minus_n=true;
            }else if( slovo.equals("-v") || slovo.equals("--verbose") ){
                //nepodporuju
            }else if( slovo.equals("-e") || slovo.equals("--extend") ){
                //zatim nepodporuju
            }else{
                kon.posliRadek("route: invalid option -- 'a'"); //neznamej prepinac
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
            nastavDel();
        }
        if ( slovo.equals("flush") ){
            nastavFlush();
        }
        
    }


    private void nastavAdd() { //i ukazuje na posici prvniho prvku za add

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

    private void nastavDel() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void nastavFlush() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void nastavMinus_net() {
        throw new UnsupportedOperationException("Not yet implemented");
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




}
