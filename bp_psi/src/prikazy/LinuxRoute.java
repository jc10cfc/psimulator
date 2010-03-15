/*
 * Gegründet am Montag 15.3.2010.
 */

package prikazy;

import datoveStruktury.IpAdresa;
import java.util.List;
import pocitac.AbstractPocitac;
import pocitac.Konsole;

/**
 *
 * @author neiss
 */
public class LinuxRoute extends AbstraktniPrikaz{
    // pomocny promenny pro parser prikazu:
    private int uk=1; //ukazatel do seznamu slov, prvni slovo je to route
    private String slovo; //drzi si slovo ke zpracovani

    //nastaveni prikazu:
    boolean minus_n=false;
    int navratovyKod=0;
    /* Je to pole bitu (bity pocitany odzadu od nuly, jako mocnina):
     * 0. bit (1) - nespravnej prepinac
     * 1. bit (2) - malo parametru u akce
     * 2. bit (4) - spatny adresat
     * 3. bit (8) - spatna brana
     * 4. bit (16) - brana zadavana vice nez jednou
      */
    private String adr;
    private String brana;
    private boolean nastavenaBrana=false;

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
                nastavDev();
            }
        }
    }

    private void nastavGw() {
        if(nastavenaBrana){ //kontroluje se, jestli se to necykli s nastavDev()
            vypisKratkouNapovedu();
            navratovyKod |= 16;
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
            }else if(slovo.equals("")){ //konec prikazu
            }else{ //vsechno ostatni se povazuje za nazev rozhrani
                nastavDev();
            }
        }
    }

    private void nastavDev() {
        throw new UnsupportedOperationException("Not yet implemented");
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
