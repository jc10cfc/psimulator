/*
 * DODELAT akutne:
 *      nastavovani druheho typu natu
 * DODELAT mozna nekdy, rozhodne ne akutne:
 *      Seradit chybovy hlaseni podle priorit a pripadne zaridit, aby se nevypisovaly vsechny.
 *      Udelat seznam zadanejch pravidel, chtelo by nejakej wrapper, zatim nejde mit vic pravidel nez jedno.
 *
 */
package prikazy.linux;

import prikazy.*;
import Main.Main;
import datoveStruktury.IpAdresa;
import java.util.ArrayList;
import java.util.List;
import pocitac.*;
import vyjimky.SpatnaAdresaException;

/**
 *
 *
 * @author neiss
 */
public class LinuxIptables extends AbstraktniPrikaz {

    boolean ladeni = false;
    /**
     * Navratovy kod parseru a kontroloru.<br />
     * Funguje klasicky po bitech jako v ifconfigu ap.<br />
     * 0 - vsechno v poradku<br />
     * 1 - nejakej nesmysl v gramatice prikazu, je uveden v promenny nesmysl<br />
     * 2 - tabulka nezadana<br />
     * 4 - prepinac nedokoncen (poslednim slovem je napr -t), uklada se v nedokoncenejPrepinac<br />
     * 8 - spatna tabulka<br />
     * 16 - vicekrat zadany prepinac(e), uklada se v dvojityPrepinace<br />
     * 32 - nespravna adresa -d<br />
     * 64 - spatna adresa --to<br />
     * 128 - nezadan zadny komand -L, -A, -I, -D
     * 256 - zadano vic retezu (vic parametru -A, -I, -D)<br />
     * 512 - spatny cislo pravidla <br />
     * 1024 - neznama akceJump<br />
     * 2048 - vzhledem k akci nebo k pravidlu zakazanej prepinac, uklada se do zakazanyPrepinace <br />
     * 4096 - nepodporovany nebo zakazany prepinac nebo akceJump<br />
     * 8192 - vystupni rozhrani neexistuje<br />
     * 16384 - pro danou moznost chybi nejakej prepinac<br />
     * 32768 - neznamy jmeno retezu<br />
     * 65536 - moc velky cislo k deletovani<br />
     * 131072 - <br />
     */
    int navrKod = 0;
    // ostatni promenny parseru:
    String slovo; //aktualni slovo
    String nesmysl; //k navrKodu 1.
    String tabulka;
    String vstupniRozhr;
    String vystupniRozhr;
    String akceJump; //akce u -j
    String cilAdr;
    String preklAdr;
    List<String> dvojityPrepinace = new ArrayList<String>();//prepinace, ktery byly zadany vic nez jednou
    List<String> nepovolenyPrepinace = new ArrayList<String>(); //prepinace, ktery jsou v zadany kombinaci
           //nepovoleny, a to budto simulatorem nebo samotnym iptables
    List<String> chybejiciPrepinace = new ArrayList<String>();//prepinace, ktery pro moje prepinace chybej
    String nedokoncenejPrepinac;
    boolean minus_h=false;
    boolean zadanoMinus_o = false;
    boolean zadanoMinus_i = false;
    boolean zadanoMinus_j = false;
    boolean zadanoMinus_d = false;
    boolean zadanoToDestination = false;
    boolean zadanRetez = false;
    String cisloPr; //cislo pravidla jako String
    String retez;
    /**
     * 0 - nic<br />
     * 1 - append<br />
     * 2 - insert<br />
     * 3 - delete<br />
     * 4 - list (vypsani)<br />
     */
    int provest = 0;
    int cisloPravidla = -1; //cislo pravidla pro smazani nebo pridani
    List<String> zakazanyPrepinace = new ArrayList<String>();

    //nastaveny promenny:
    boolean minus_n = true;
    IpAdresa cilovaAdr;
    IpAdresa prekladanaAdr;//ip adresa, na kterou se ma prekladat
    SitoveRozhrani vystupni;

    public LinuxIptables(AbstraktniPocitac pc, Konsole kon, List<String> slova) {
        super(pc, kon, slova);
        parsujPrikaz();
        zkontrolujPrikaz();
        vypisChybovyHlaseni();
        vykonejPrikaz();
    }

    /**
     * Parsuje prikaz, k cemuz vola i metody dole. Kontroluje gramatiku prikazu.
     *
     */
    private void parsujPrikaz() {
        slovo = dalsiSlovo();
        while (!slovo.equals("")) {
            zpracujBeznyPrepinace();
            if(minus_h)break; // po -h se uz nic dalsiho neparsuje.
            slovo = dalsiSlovo();
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
        if (slovo.equals("-h")) {
            minus_h=true;
        }else if (slovo.equals("-t")) {
            zpracujMinus_t();
        } else if (slovo.equals("-o")) {
            zpracujMinus_o();
        } else if (slovo.equals("-i")) {
            zpracujMinus_i();
        } else if (slovo.equals("-j")) {
            zpracujMinus_j();
        } else if (slovo.equals("-d")) {
            zpracujMinus_d();
        } else if (akceJump != null && akceJump.equals("DNAT") && (slovo.equals("--to") || slovo.equals("--to-destination"))) {
            // -> dokud neni zadan DNAT, neni --to povoleny
            zpracujToDestination();
        } else if ((slovo.equals("-A")) || (slovo.equals("-I")) || (slovo.equals("-D"))) {
            zpracujRetez();
        } else if (slovo.equals("-L")) {
            if (zadanoMinus_j) {
                navrKod |= 256;
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

    private void zpracujMinus_t() {
        tabulka = dalsiSlovo();
        if (!tabulka.equals("nat")) {
            if (tabulka.equals("")) {
                navrKod |= 4; //zadano jen -t
                nedokoncenejPrepinac = "-t";
            } else {
                navrKod |= 8;
            }
        }
    }

    private void zpracujMinus_o() {
        if (zadanoMinus_o) {
            navrKod |= 16;
            dvojityPrepinace.add("-o");
        } else {
            zadanoMinus_o = true;
        }
        vystupniRozhr = dalsiSlovo();
        if (vystupniRozhr.equals("")) {
            navrKod |= 4;
            nedokoncenejPrepinac = "-o";
        }
    }

    private void zpracujMinus_i() {
        if (zadanoMinus_i) {
            navrKod |= 16;
            dvojityPrepinace.add("-i");
        } else {
            zadanoMinus_i = true;
        }
        vstupniRozhr = dalsiSlovo();
        if (vstupniRozhr.equals("")) {
            navrKod |= 4;
            nedokoncenejPrepinac = "-i";
        }
    }

    private void zpracujMinus_j() {
        if (zadanoMinus_j) {
            navrKod |= 16;
            dvojityPrepinace.add("-j");
        } else {
            zadanoMinus_j = true;
        }
        akceJump = dalsiSlovo();
        if (akceJump.equals("")) {
            navrKod |= 4;
            nedokoncenejPrepinac = "-j";
        } else {
            if (!(akceJump.equals("MASQUERADE") || akceJump.equals("DNAT"))) {
                navrKod |= 1024;
            }
        }
    }

    private void zpracujMinus_d() {
        if (zadanoMinus_d) {
            navrKod |= 16;
            dvojityPrepinace.add("-d");
        } else {
            zadanoMinus_d = true;
        }
        cilAdr = dalsiSlovo();
        if (cilAdr.equals("")) {
            navrKod |= 4;
            nedokoncenejPrepinac = "-d";
        } else {
            try {
                cilovaAdr = new IpAdresa(cilAdr);
            } catch (SpatnaAdresaException ex) {
                navrKod |= 32;
            }
        }
    }

    private void zpracujToDestination() {
        if (zadanoToDestination) {
            navrKod |= 16;
            dvojityPrepinace.add("--to-destination");
        } else {
            zadanoToDestination = true;
        }
        preklAdr = dalsiSlovo();
        if (preklAdr.equals("")) {
            navrKod |= 4;
            nedokoncenejPrepinac = "--to-destination";
        } else {
            try {
                cilovaAdr = new IpAdresa(cilAdr);
            } catch (SpatnaAdresaException ex) {
                navrKod |= 64;
            }
        }
    }

    /**
     * Zpracovava -A, -I, -D
     */
    private void zpracujRetez() {
        if (zadanRetez) {
            navrKod |= 256;
        } else {
            provest = 1;
            zadanRetez = true;
            if (slovo.equals("-A")) {
                provest = 1;
            }
            if (slovo.equals("-I")) {
                provest = 2;
            }
            if (slovo.equals("-D")) {
                provest = 3;
            }
            retez = dalsiSlovo();
            if (provest == 2) {//insert
                cisloPr = dalsiSlovoAleNezvetsujCitac();
                try {
                    cisloPravidla = Integer.parseInt(cisloPr);
                    dalsiSlovo(); //zvetsovani citace, kdyz se to povedlo
                } catch (NumberFormatException ex) {
                    cisloPravidla = 1;
                }
            }
            if (provest == 3) { //delete
                cisloPr = dalsiSlovo();
                try {
                    cisloPravidla = Integer.parseInt(cisloPr);
                } catch (NumberFormatException ex) {
                    navrKod |= 512;
                }
            }
            if (cisloPravidla != -1 && cisloPravidla < 1) {
                navrKod |= 512;
            }
            if(!retez.equals("PREROUTING") && !retez.equals("POSTROUTING") ){
                navrKod|=32768;
            }

        }
    }

    /**
     * Kontroluje, jestli byly zadany spravny parametry
     */
    private void zkontrolujPrikaz() {

        if(minus_h)return; //nic se nekontroluje...

        //kontrola spravnosti tabulky - pozor, vyplnuje se jeste navrKod:
        if (tabulka == null) {
            navrKod |= 2; //tabulka nezadana
        }

        if(provest==0){
            navrKod |= 128;
        }

        if (provest == 4 || provest ==3) { //-L - vypisovani, -D - mazani
          // -> poustim to zaroven i pri -D, abych si usetril kopirovani
            if (zadanoMinus_d) {
                zakazanyPrepinace.add("-d");
                navrKod |= 2048;
            }
            if (zadanoMinus_o) {
                zakazanyPrepinace.add("-o");
                navrKod |= 2048;
            }
            if (zadanoMinus_i) {
                zakazanyPrepinace.add("-i");
                navrKod |= 2048;
            }
            if (zadanoMinus_j) {
                zakazanyPrepinace.add("-j");
                navrKod |= 2048;
            }
        }

        if(provest==1 ||provest==2){ //append nebo insert
            if (zadanoMinus_i) { //-i neni u me povoleny ani v jednom
                nepovolenyPrepinace.add("-i");
                navrKod |= 4096;
            }
            if (retez.equals("POSTROUTING")) { //klasickej preklad adres na jednom verejnym rozhrani
                if (zadanoMinus_d) {
                    nepovolenyPrepinace.add("-d");
                    navrKod |= 4096;
                }
                if(zadanoMinus_o){
                    vystupni=pc.najdiRozhrani(vystupniRozhr);
                    if(vystupni==null){
                        navrKod |= 8192;
                    }
                }else{
                    chybejiciPrepinace.add("-o");
                    navrKod |= 16384;
                }
                if(zadanoMinus_j){
                    if( ! akceJump.equals("MASQUERADE")){
                        navrKod |= 4096;
                        nepovolenyPrepinace.add("-j "+akceJump); //dam ho tam, i kdyz neni prepinac
                    }
                }else{
                    chybejiciPrepinace.add("-j");
                    navrKod |= 16384;
                }
            }
            if (retez.equals("PREROUTING")){
                if( ! zadanoMinus_d  ){
                    chybejiciPrepinace.add("-d");
                    navrKod |= 16384;
                }
                if( ! zadanoMinus_j  ){
                    chybejiciPrepinace.add("-j");
                    navrKod |= 16384;
                }else{
                    if( ! akceJump.equals("DNAT")){
                        navrKod |= 4096;
                        nepovolenyPrepinace.add("-j "+akceJump); //dam ho tam, i kdyz neni prepinac
                    }
                }
                if( ! zadanoToDestination  ){
                    chybejiciPrepinace.add("--to-destination");
                    navrKod |= 16384;
                }
                if( zadanoMinus_o  ){
                    zakazanyPrepinace.add("-o"); //ten je tady zakazanej
                    navrKod |= 2048;
                }
            }
        }

        if(provest==3){ //delete
            //prebytecny prepinace se zkoumaly uz s vypisem
            if(cisloPravidla!=1){
                navrKod |= 65536;
            }
        }
    }

    /**
     * Vypisuje chybovy hlaseni.
     * Budou pak chtit seradit podle priority.
     */
    private void vypisChybovyHlaseni() {
        if (ladeni) {
            kon.posliRadek(toString());
            kon.posliRadek("----------------------------");
        }
        if ((navrKod & 1) != 0) { //nesmysl v gramatice
            kon.posliRadek("Bad argument `" + nesmysl + "'");
            kon.posliRadek("Try `iptables -h' or 'iptables --help' for more information.");
            return; //asi by tadyu melo bejt...
        }

        if ((navrKod & 2) != 0) { //nezadano jmeno tabulky
            kon.posliRadek(Main.jmenoProgramu + ": Normalne by se pouzila tabulka filter, " +
                    "ta ale v tomto programu neni. Podporujeme zatim jen tabulku nat.");
        }
        if ((navrKod & 4) != 0) { //prepinac nedokoncen
            kon.posliRadek("iptables v1.4.1.1: Unknown arg `" + nedokoncenejPrepinac + "'");
            kon.posliRadek("Try `iptables -h' or 'iptables --help' for more information.");
        }
        if ((navrKod & 8) != 0) { //zadana spatna tabulka
            kon.posliRadek("iptables v1.4.1.1: can't initialize iptables table `" + tabulka + "': " +
                    "Table does not exist (do you need to insmod?)");
            kon.posliRadek("Perhaps iptables or your kernel needs to be upgraded.");
        }

        if ((navrKod & 32768) != 0) { //zadanej spatnej retez
            kon.posliRadek("iptables: No chain/target/match by that name");
        }

        if ((navrKod & 16) != 0) { //nejakej prepinac zadanej dvakrat
            kon.posliRadek("iptables v1.4.1.1: multiple " + dvojityPrepinace.get(0) + " flags not allowed");
            kon.posliRadek("Try `iptables -h' or 'iptables --help' for more information.");
        }

        if ((navrKod & 32) != 0) { //spatna adresa -d
            kon.posliRadek("iptables v1.4.1.1: host/network `" + cilAdr + "' not found");
            kon.posliRadek("Try `iptables -h' or 'iptables --help' for more information.");
        }

        if ((navrKod & 64) != 0) { //spatna adresa --to-destination
            kon.posliRadek("iptables v1.4.1.1: Bad IP address `" + preklAdr + "'");
            kon.posliRadek("Try `iptables -h' or 'iptables --help' for more information.");
        }

        if ((navrKod & 128) != 0) { //nezadan zadny prikaz
            kon.posliRadek("iptables v1.4.1.1: no command specified");
            kon.posliRadek("Try `iptables -h' or 'iptables --help' for more information.");
        }

        if ((navrKod & 256) != 0) { //vic retezu
            kon.posliRadek("Parametry -A, -I, -D nemuzete zadavat vicektrat.");
            // -> normalne to pise: "iptables v1.4.1.1: Can't use -A with -I"  - to se mi nechtelo pamatovat
            kon.posliRadek("Try `iptables -h' or 'iptables --help' for more information.");
        }

        if ((navrKod & 512) != 0) { //spatny cislo
            kon.posliRadek("iptables v1.4.1.1: Invalid rule number `" + cisloPr + "'");
            kon.posliRadek("Try `iptables -h' or 'iptables --help' for more information.");
        }

        if ((navrKod & 1024) != 0) { //neznama akce
            kon.posliRadek("iptables v1.4.1.1: Couldn't load target `" + akceJump + "':/lib/xtables/libipt_" +
                    akceJump + ".so: cannot open shared object file: No such file or directory");
            kon.posliRadek("Try `iptables -h' or 'iptables --help' for more information.");
        }

        if ((navrKod & 2048) != 0) { //zakazanyPrepinace
            kon.posliRadek("iptables v1.4.1.1: Illegal option `" + vypisSeznam(zakazanyPrepinace)
                    + "' with this command");
            kon.posliRadek("Try `iptables -h' or 'iptables --help' for more information.");
        }

        if ((navrKod & 4096) != 0) { //pro akci zatim nepodporovany prepinac
            kon.posliRadek(Main.jmenoProgramu+": Takova moznost by mozna normalne byla mozna, simulator ji vsak "
                    +"zatim nepodporuje. Zkuste odstranit prepinac: "+ vypisSeznam(nepovolenyPrepinace));
        }

        if ((navrKod & 8192) != 0) { //zadany vystupni rozhrani neexistuje kontroluje se jen na POSTROUTING)
            kon.posliRadek(Main.jmenoProgramu+": Zadany rozhrani "+vystupniRozhr+" neexistuje.");
        }
        if ((navrKod & 16384) != 0) { //zadany vystupni rozhrani neexistuje kontroluje se jen na POSTROUTING)
            kon.posliRadek(Main.jmenoProgramu+": Pro danou moznost chybeji tyto prepinace: "
                    +vypisSeznam(chybejiciPrepinace));
        }


    }

    @Override
    protected void vykonejPrikaz() {
        if(navrKod!=0){
            return; // provadi se, jen kdyz je vsechno dobre
        }
        if(minus_h){
            vypisHelp();
            return;
        }
        if(provest==4){
            vypis();
        }
        if(provest==1 || provest==2){ //-A nebo -I
            pc.natTabulka.nastavLinuxMaskaradu(vystupni);
        }
        if(provest==3){ //mazani
            pc.natTabulka.zrusLinuxMaskaradu();
        }
    }


    private void vypis() {
        kon.posliRadek("Chain PREROUTING (policy ACCEPT)");
        kon.posliRadek("target     prot opt source               destination");
        kon.posliRadek("");
        kon.posliRadek("Chain POSTROUTING (policy ACCEPT)");
        kon.posliRadek("target     prot opt source               destination");
        if(pc.natTabulka.jeNastavenaLinuxovaMaskarada())
            kon.posliRadek("MASQUERADE  all  --  0.0.0.0/0            0.0.0.0/0");
        kon.posliRadek("");
        kon.posliRadek("Chain OUTPUT (policy ACCEPT)");
        kon.posliRadek("target     prot opt source               destination");
    }

    private String vypisSeznam(List<String> l){
        String vr="";
        for(int i=0;i<l.size();i++){
            if(l.get(i)!=null){
                if(i!=0)vr+=", ";
                vr+=l.get(i);
            }
        }
        return vr;
    }

    @Override
    public String toString() {
        String vratit = "  Parametry prikazu iptables:\n\r\tnavratovyKodParseru: " + rozlozNaMocniny2(navrKod);
        if (tabulka != null) {
            vratit += "\n\r\ttabulka: " + tabulka;
        }
        if (retez != null) {
            vratit += "\n\r\tretez: " + retez;
        }
        vratit += "\n\r\tprovest: " + provest;
        vratit += "\n\r\tcisloPravidla: " + cisloPravidla;
        if (minus_h) {
            vratit += "\n\r\tPOZOR, zadan prepinac -h, tzn., nic dalsiho se neparsuje, nic se nekontro" +
                    "luje, nic se nevypisuje, jen napoveda se vypise.";
        }
        if (zadanoMinus_o) {
            vratit += "\n\r\tvystupniRozhr: " + vystupniRozhr;
        }
        if (zadanoMinus_i) {
            vratit += "\n\r\tvstupniRozhr: " + vstupniRozhr;
        }
        if (zadanoMinus_j) {
            vratit += "\n\r\takceJump: " + akceJump;
        }
        if (zadanoMinus_d) {
            vratit += "\n\r\tcilAdr: " + cilAdr;
            if (cilovaAdr != null) {
                vratit += "\n\r\tcilovaAdr: " + cilovaAdr.vypisAdresu();
            }
        }



        return vratit;
    }

    private void vypisHelp() {
        kon.posliRadek("iptables v1.4.1.1    ");
        kon.posliRadek("");
        kon.posliRadek("Usage: iptables -[AD] chain rule-specification [options]");
        kon.posliRadek("       iptables -[RI] chain rulenum rule-specification [options]");
        kon.posliRadek("       iptables -D chain rulenum [options]                      ");
        kon.posliRadek("       iptables -[LS] [chain [rulenum]] [options]               ");
        kon.posliRadek("       iptables -[FZ] [chain] [options]                         ");
        kon.posliRadek("       iptables -[NX] chain                                     ");
        kon.posliRadek("       iptables -E old-chain-name new-chain-name                ");
        kon.posliRadek("       iptables -P chain target [options]                       ");
        kon.posliRadek("       iptables -h (print this help information)                ");
        kon.posliRadek("");
        kon.posliRadek("Commands:");
        kon.posliRadek("Either long or short options are allowed.");
        kon.posliRadek("  --append  -A chain            Append to chain");
        kon.posliRadek("  --delete  -D chain            Delete matching rule from chain");
        kon.posliRadek("  --delete  -D chain rulenum                                   ");
        kon.posliRadek("                                Delete rule rulenum (1 = first) from chain");
        kon.posliRadek("  --insert  -I chain [rulenum]                                            ");
        kon.posliRadek("                                Insert in chain as rulenum (default 1=first)");
        kon.posliRadek("  --replace -R chain rulenum");
        kon.posliRadek("                                Replace rule rulenum (1 = first) in chain");
        kon.posliRadek("  --list    -L [chain [rulenum]]");
        kon.posliRadek("                                List the rules in a chain or all chains");
        kon.posliRadek("  --list-rules -S [chain [rulenum]]");
        kon.posliRadek("                                Print the rules in a chain or all chains");
        kon.posliRadek("  --flush   -F [chain]          Delete all rules in  chain or all chains");
        kon.posliRadek("  --zero    -Z [chain]          Zero counters in chain or all chains");
        kon.posliRadek("  --new     -N chain            Create a new user-defined chain");
        kon.posliRadek("  --delete-chain");
        kon.posliRadek("            -X [chain]          Delete a user-defined chain");
        kon.posliRadek("  --policy  -P chain target");
        kon.posliRadek("                                Change policy on chain to target");
        kon.posliRadek("  --rename-chain");
        kon.posliRadek("            -E old-chain new-chain");
        kon.posliRadek("                                Change chain name, (moving any references)");
        kon.posliRadek("Options:");
        kon.posliRadek("  --proto       -p [!] proto    protocol: by number or name, eg. `tcp'");
        kon.posliRadek("  --source      -s [!] address[/mask]");
        kon.posliRadek("                                source specification");
        kon.posliRadek("  --destination -d [!] address[/mask]");
        kon.posliRadek("                                destination specification");
        kon.posliRadek("  --in-interface -i [!] input name[+]");
        kon.posliRadek("                                network interface name ([+] for wildcard)");
        kon.posliRadek("  --jump        -j target");
        kon.posliRadek("                                target for rule (may load target extension)");
        kon.posliRadek("  --goto      -g chain");
        kon.posliRadek("                              jump to chain with no return");
        kon.posliRadek("  --match       -m match");
        kon.posliRadek("                                extended match (may load extension)");
        kon.posliRadek("  --numeric     -n              numeric output of addresses and ports");
        kon.posliRadek("  --out-interface -o [!] output name[+]");
        kon.posliRadek("                                network interface name ([+] for wildcard)");
        kon.posliRadek("  --table       -t table        table to manipulate (default: `filter')");
        kon.posliRadek("  --verbose     -v              verbose mode");
        kon.posliRadek("  --line-numbers                print line numbers when listing");
        kon.posliRadek("  --exact       -x              expand numbers (display exact values)");
        kon.posliRadek("[!] --fragment  -f              match second or further fragments only");
        kon.posliRadek("  --modprobe=<command>          try to insert modules using this command");
        kon.posliRadek("  --set-counters PKTS BYTES     set the counter during insert/append");
        kon.posliRadek("[!] --version   -V              print package version.");
    }

}
