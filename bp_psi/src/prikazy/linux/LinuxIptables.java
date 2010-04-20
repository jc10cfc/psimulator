/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package prikazy.linux;

import prikazy.*;
import Main.Main;
import datoveStruktury.IpAdresa;
import java.util.List;
import pocitac.*;
import vyjimky.SpatnaAdresaException;

/**
 *
 *
 * @author neiss
 */
public class LinuxIptables extends AbstraktniPrikaz{
    
    boolean ladeni = true;
    
    /**
     * Navratovy kod parseru a kontroloru.<br />
     * Funguje klasicky po bitech jako v ifconfigu ap.<br />
     * 0 - vsechno v poradku<br />
     * 1 - nejakej nesmysl v gramatice prikazu, je uveden v promenny nesmysl<br />
     * 2 - tabulka nezadana<br />
     * 4 - zadano -t, ale nic po tom<br />
     * 8 - spatna tabulka<br />
     * 16 - vicekrat zadano -o<br />
     * 32 - zadano jen -o a nic za tim<br />
     * 64 - vicekrat zadano -i<br />
     * 128 - zadano jen -i a nic za tim<br />
     * 256 - vicekrat zadano -j<br />
     * 512 - zadano jen -j a pak nic<br />
     * 1024 - neznama akce<br />
     * 2048 - zadano vice -d<br />
     * 4096 - zadano jen -d a nic za tim<br />
     * 8192 - nespravna adresa<br />
     * 16384 - zadano vic retezu (vic parametru -A, -I, -D)<br />
     */
    int navrKod = 0;
    /**
     * Navratovy kod pro semantiku
     * 0 - v poradku
     * 1 - navKod neni nula, dalsi kontrola se neprovadi
     * 2 - zadan prepinac -o, -j, -i, -d, kterej pro prikaz nema smysl
     */
    int semKod = 0;

    // ostatni promenny parseru:
    String slovo; //aktualni slovo
    String nesmysl; //k navrKodu 1.
    String tabulka;
    String vstupniRozhr;
    String vystupniRozhr;
    String akce; //
    String cilAdr;
    boolean zadanoMinus_o=false;
    boolean zadanoMinus_i=false;
    boolean zadanoMinus_j=false;
    boolean zadanoMinus_d=false;
    boolean zadanRetez=false;
    String retez;
    /**
     * 1 - append
     * 2 - insert
     * 3 - delete
     * 4 - list (vypsani)
     */
    int provest=0;
    int cisloPravidla=-1; //cislo pravidla pro smazani nebo pridani
    
    //nastaveny promenny:
    boolean minus_n=true;
    IpAdresa cilovaAdr;

    public LinuxIptables(AbstraktniPocitac pc, Konsole kon, List<String> slova) {
        super(pc, kon, slova);
        parsujPrikaz();
        zkontrolujGramatikuPrikazu();
        zkontrolujSemantiku();
        vypisChybovyHlaseni();
        vykonejPrikaz();
    }


    private void parsujPrikaz() {
        slovo=dalsiSlovo();
        while( ! slovo.equals("") ){
            zpracujBeznyPrepinace();
            slovo=dalsiSlovo();
        }
    }

    /**
     * Zpracovava bezny prepinace jako -n, -t, -o, -i, -j, -A, -I, -D, -L, -d.
     * Pro ty, co maj pak nejakou hodnotu vetsinou vola specialni funkci.
     * Na zacatku tyhle metody by v promenny slovo melo bejt ulozeny prvni
     * slovo prepinace (napr. -t), na konci posledni slovo prepinace (napr.
     * nat).
     */
    private void zpracujBeznyPrepinace() {
        if (slovo.equals("-t")) {
            tabulka = dalsiSlovo();
        } else if (slovo.equals("-o")) {
            zpracujMinus_o();
        } else if (slovo.equals("-i")) {
            zpracujMinus_i();
        } else if (slovo.equals("-j")) {
            zpracujMinus_j();
        } else if (slovo.equals("-d")) {
            zpracujMinus_d();
        } else if ((slovo.equals("-A")) || (slovo.equals("-I")) || (slovo.equals("-D"))) {
            zpracujRetez();
        } else if (slovo.equals("-L")) {
            if (zadanoMinus_j) {
                navrKod |= 16384;
            } else {
                provest = 4;
                zadanRetez = true;
            }
        } else if (slovo.equals("-n")) {
            minus_n = true;
            //na tenhleten parametr kaslu, stejne u me nema smyslu
            //spravne by nemelo bejt povoleny -n -n...
        } else if (slovo.equals("")) { //zadny dlasi slovo uz neni
            //nic dalsiho se nedela
        } else {
            navrKod |= 1;
            nesmysl = slovo;
        }
    }


    private void zpracujMinus_o() {
        if(zadanoMinus_o){
            navrKod |= 16;
        }else{
            zadanoMinus_o=true;
        }
        vystupniRozhr=dalsiSlovo();
    }

    private void zpracujMinus_i() {
        if(zadanoMinus_i){
            navrKod |= 64;
        }else{
            zadanoMinus_i=true;
        }
        vstupniRozhr=dalsiSlovo();
    }

    private void zpracujMinus_j() {
        if(zadanoMinus_j){
            navrKod |= 256;
        }else{
            zadanoMinus_j=true;
        }
        akce=dalsiSlovo();
    }

    private void zpracujMinus_d() {
        if(zadanoMinus_d){
            navrKod |= 2048;
        }else{
            zadanoMinus_d=true;
        }
        cilAdr=dalsiSlovo();
    }

    /**
     * Zpracovava -A, -I, -D
     */
    private void zpracujRetez() {
        if (zadanoMinus_j) {
            navrKod |= 16384;
        } else {
            provest = 1;
            zadanRetez = true;
            if(slovo.equals("-A")) provest = 1;
            if(slovo.equals("-I")) provest = 2;
            if(slovo.equals("-D")) provest = 3;
            retez=dalsiSlovo();
            if(provest==2){
                
            }
        }
    }

    /**
     * Kontroluje gramatiku prikazu, pripadne prirazuje nektery hodnoty.
     */
    private void zkontrolujGramatikuPrikazu() {

        //kontrola spravnosti tabulky:
        if (tabulka == null) {
            navrKod |= 2; //tabulka nezadana
        } else {
            if (!tabulka.equals("nat")) {
                if (tabulka.equals("")) {
                    navrKod |= 4; //zadano jen -t
                }else{
                    navrKod |= 8;
                }
            }
        }

        //kontrola spravnosti -o:
        if (zadanoMinus_o) {
            if (vystupniRozhr.equals("")) {
                navrKod |= 32;
            }
        }

        //kontrola spravnosti -i:
        if (zadanoMinus_i) {
            if (vstupniRozhr.equals("")) {
                navrKod |= 128;
            }
        }

        //kontrola spravnosti -j:
        if (zadanoMinus_j) {
            if (akce.equals("")) {
                navrKod |= 512;
            } else {
                if (!(akce.equals("MASQUERADE") || akce.equals("DNAT"))) {
                    navrKod |= 1024;
                }
            }
        }

        //kontrola spravnosti -d:
        if (zadanoMinus_d) {
            if (cilAdr.equals("")) {
                navrKod |= 4096;
            } else {
                try {
                    cilovaAdr = new IpAdresa(cilAdr);
                } catch (SpatnaAdresaException ex) {
                    navrKod |= 8192;
                }
            }
        }


    }

    /**
     * Kontroluje, jestli byly zadany spravny parametry
     */
    private void zkontrolujSemantiku(){
        if(navrKod!=0){
            semKod=1;
            return;
        }
        if(provest==4){ //-L
            if(zadanoMinus_d){
                kon.posliRadek("iptables v1.4.1.1: Illegal option `-d' with this command");
                semKod |= 2;
            }
            if(zadanoMinus_o){
                kon.posliRadek("iptables v1.4.1.1: Illegal option `-o' with this command");
                semKod |= 2;
            }
            if(zadanoMinus_i){
                kon.posliRadek("iptables v1.4.1.1: Illegal option `-i' with this command");
                semKod |= 2;
            }
            if(zadanoMinus_j){
                kon.posliRadek("iptables v1.4.1.1: Illegal option `-j' with this command");
                semKod |= 2;
            }
        }
    }

    /**
     * Vypisuje chybovy hlaseni.
     * Budou pak chtit seradit podle priority.
     */
    private void vypisChybovyHlaseni() {
        if(ladeni) {
            kon.posliRadek(toString());
            kon.posliRadek("----------------------------");
        }
        if ( (navrKod&1) != 0){ //nesmysl v gramatice
            kon.posliRadek("Bad argument `"+nesmysl+"'");
            kon.posliRadek("Try `iptables -h' or 'iptables --help' for more information.");
            return; //asi by tadyu melo bejt...
        }

        if ( (navrKod&2) != 0 ){ //nezadano jmeno tabulky
            kon.posliRadek(Main.jmenoProgramu+": Normalne by se pouzila tabulka filter, " +
                    "ta ale v tomto programu neni. Podporujeme zatim jen tabulku nat.");
        }
        if ( (navrKod&4) != 0 ){ //zadano jen minus_t
            kon.posliRadek("iptables v1.4.1.1: Unknown arg `-t'");
            kon.posliRadek("Try `iptables -h' or 'iptables --help' for more information.");
        }
        if ( (navrKod&8) != 0 ){ //zadana spatna tabulka
            kon.posliRadek("iptables v1.4.1.1: can't initialize iptables table `"+tabulka+"': " +
                    "Table does not exist (do you need to insmod?)");
            kon.posliRadek("Perhaps iptables or your kernel needs to be upgraded.");
        }

        if ( (navrKod&16) != 0 ){ //vic -o
            kon.posliRadek("iptables v1.4.1.1: multiple -o flags not allowed");
            kon.posliRadek("Try `iptables -h' or 'iptables --help' for more information.");
        }
        if ( (navrKod&32) != 0 ){ //zadano jen minus_o
            kon.posliRadek("iptables v1.4.1.1: Unknown arg `-o'");
            kon.posliRadek("Try `iptables -h' or 'iptables --help' for more information.");
        }
        if ( (navrKod&64) != 0 ){ //vic -i
            kon.posliRadek("iptables v1.4.1.1: multiple -o flags not allowed");
            kon.posliRadek("Try `iptables -h' or 'iptables --help' for more information.");
        }
        if ( (navrKod&128) != 0 ){ //zadano jen minus_i
            kon.posliRadek("iptables v1.4.1.1: Unknown arg `-i'");
            kon.posliRadek("Try `iptables -h' or 'iptables --help' for more information.");
        }
        if ( (navrKod&256) != 0 ){ //vic -j
            kon.posliRadek("iptables v1.4.1.1: multiple -j flags not allowed");
            kon.posliRadek("Try `iptables -h' or 'iptables --help' for more information.");
        }
        if ( (navrKod&512) != 0 ){ //zadano jen minus_j
            kon.posliRadek("iptables v1.4.1.1: Unknown arg `-j'");
            kon.posliRadek("Try `iptables -h' or 'iptables --help' for more information.");
        }
        if ( (navrKod&1024) != 0 ){ //neznama akce
            kon.posliRadek("iptables v1.4.1.1: Couldn't load target `"+akce+"':/lib/xtables/libipt_"+
                    akce+".so: cannot open shared object file: No such file or directory");
            kon.posliRadek("Try `iptables -h' or 'iptables --help' for more information.");
        }
        if ( (navrKod&2048) != 0 ){ //vic -d
            kon.posliRadek("iptables v1.4.1.1: multiple -d flags not allowed");
            kon.posliRadek("Try `iptables -h' or 'iptables --help' for more information.");
        }
        if ( (navrKod&4096) != 0 ){ //zadano jen minus_d
            kon.posliRadek("iptables v1.4.1.1: Unknown arg `-d'");
            kon.posliRadek("Try `iptables -h' or 'iptables --help' for more information.");
        }
        if ( (navrKod&8192) != 0 ){ //neznama adresa -d
            kon.posliRadek("iptables v1.4.1.1: host/network `"+cilAdr+"' not found");
            kon.posliRadek("Try `iptables -h' or 'iptables --help' for more information.");
        }
    }

    @Override
    protected void vykonejPrikaz() {
    }

    @Override
    public String toString(){
        String vratit = "  Parametry prikazu iptables:\n\r\tnavratovyKodParseru: " + navrKod;
        if (tabulka != null) {
            vratit += "\n\r\ttabulka: " + tabulka;
        }
        if (retez != null) {
            vratit += "\n\r\tretez: " + retez;
        }
        vratit+="\n\r\tprovest: "+provest;
        vratit+="\n\r\tcisloPravidla: "+cisloPravidla;
        if (zadanoMinus_o) {
            vratit += "\n\r\tvystupniRozhr: " + vystupniRozhr;
        }
        if (zadanoMinus_i) {
            vratit += "\n\r\tvstupniRozhr: " +vstupniRozhr ;
        }
        if (zadanoMinus_j) {
            vratit += "\n\r\takce: " + akce;
        }
        if (zadanoMinus_d) {
            vratit += "\n\r\tcilAdr: " + cilAdr;
            if(cilovaAdr!=null){
                vratit += "\n\r\tcilovaAdr: " + cilovaAdr.vypisAdresu();
            }
        }
        vratit+="\n\r\tnavratovy kod semantiky: "+semKod;



        return vratit;
    }
   

}
