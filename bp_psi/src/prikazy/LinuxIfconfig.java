/*
 * Materiály:
 *      http://www.benak.net/pocitace/os/linux/ifconfig.php
 *      http://books.google.cz/books?id=1x-6XBk8bKoC&pg=PT26&lpg=PT26&dq=ifconfig&source=bl&ots=E5ys4iqBVw&sig=0LU94iuXjoBE3WDxYGnTHChcx9Q&hl=cs&ei=O1lGS6CFHoOCnQOZzP3vAg&sa=X&oi=book_result&ct=result&resnum=8&ved=0CBkQ6AEwBw#v=onepage&q=ifconfig&f=false
 *      http://www.starhill.org/man/8_ifconfig.html
 * Dodělat:
 *      ifconfig wlan0 0.0.0.0 by mělo odebrat adresu z rozhraní.
 *      dodelat chyby u zakazanejch adres 255.1.1.1 ap.
 */
package prikazy;

import datoveStruktury.IpAdresa;
import java.util.ArrayList;
import java.util.List;
import pocitac.*;
import vyjimky.SpatnaMaskaException;

/**
 *
 * @author neiss
 */
public class LinuxIfconfig extends AbstraktniPrikaz {

    boolean ladiciVypisovani=false; //jestli se maj vypisovat informace pro ladeni
    boolean ladeni=false;

    String jmenoRozhrani;
    /**
     * Do tyhle promenny se uklada jenom IP adresa bez masky za lomitkem.
     */
    List <String> seznamIP=new ArrayList<String>();
    /**
     * Maska jako String (napr. 255.255.255.0);
     */
    String maska;
    String broadcast;
    int pocetBituMasky = -1; //m zadana formou /24 totiz ma vetsi prioritu nez 255.255.255.0
    String add; //IP adresa, ktera se ma pridat
    List <String> del=new ArrayList<String>();  //ipadresa, ktera se ma odebrat
    boolean minus_a = false;
    boolean minus_v = false;
    boolean minus_s = false;
    /**
     * Do tyhle promenny bude metoda parsujPrikaz zapisovat, jakou chybu nasla:
     * 0: vsechno v poradku
     * 1: spatny prepinac (neznama volba)
     * 2: nejaka chyba v gramatice prikazu (napr: ifconfig wlan0 1.2.3.5 netmask)
     *    potreba provest, co je dobre, a vypsat napovedu --help
     * 3: rozhrani neexistuje
     * 4: zadano vice ipadres, funguje to, ale je to nesmysl
     * 5: neplatna IP adresa
     * 6: pocet bitu masky vetsi nez 32
     * 7: neplatna IP adresa parametru add
     * 8: neplatna IP adresaparametru del
     */
    int navratovyKod = 0;
    SitoveRozhrani rozhrani; //rozhrani, se kterym se bude operovat
    int pouzitIp = -1; //cislo seznamIP, ktera IP se ma pouzit


    public LinuxIfconfig(AbstractPocitac pc, Konsole kon, List<String> slova) {
        super(pc, kon, slova);
        parsujPrikaz();
        zkontrolujPrikaz();
        vykonejPrikaz();
    }

    protected void parsujPrikaz() {
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
                if (tempRet.equals("netmask")) {//m
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
     * prepinace, vice parametru netmask ap.), predpokladam, ze uz se prislo. Posila klientovi hlaseni
     * o chybach.
     */
    private void zkontrolujPrikaz(){
        if (jmenoRozhrani==null) return; //uzivatel zadal jen ifconfig, mozna nejaky prepinace, ale nic vic
        //-------------------
        //kontrola existence rozhrani
        rozhrani=pc.najdiRozhrani(jmenoRozhrani);
        if (rozhrani==null){
            //tady se nic nevypisuje, protoze ostatni se v ifconfigu asi vyhodnocuje driv (kdyz je spatne
            //rozhrani i ipadresa, tak se jako spatna ukaze IP adresa
            navratovyKod=3;
        }
        //------------------------
        //kontrola IP
        if(seznamIP.size()>1){ //jestli neni moc IP adres
            navratovyKod=4;
        }
        for (int i=0;i<seznamIP.size();i++){ //kontrola spravnosti IP
            if(IpAdresa.jeSpravnaIP(seznamIP.get(i), false)){
                pouzitIp=i;
            } else {
                kon.posliRadek(seznamIP.get(i)+": unknown host");
                kon.posliRadek("ifconfig: `--help' vypíše návod k použití.");
                navratovyKod=5; //neplatna IP
            }

        }
        //--------------------
        //kontrola masky
        //string masky se nekontroluje, protoze pro to IpAdresa nema metodu, kontroluje se az pri nastavovani
        if(pocetBituMasky!=-1){ //kontrola pocetBituMasky
            if(pocetBituMasky>32){//mensi totiz bejt nemuze, to se kontroluje driv
                navratovyKod=6;
                pocetBituMasky=pocetBituMasky % 32; //takhle se ifconfig opravdu chova, vyzkousel jsem to
            }
        }
        //---------------------
        //kontrola IP adresy add (pridavani nove IP)
        if(add!=null){
            if(!IpAdresa.jeSpravnaIP(add, false)){
                navratovyKod=7;
                kon.posliRadek(add+": unknown host");
            }
        }
        //---------------------
        //kontrola IP adres del (odebirani existujici IP)
        for(int i=0;i<del.size();i++){
            if(!IpAdresa.jeSpravnaIP(del.get(i), false)){
                navratovyKod=8;
                kon.posliRadek(del.get(i)+": unknown host"); //musim to posilat uz tady, protoze to hendka smazu
                del.remove(i); //ta spatna IP adresa se odebere
            }
        }
        
    }

    @Override
    protected void vykonejPrikaz() {
        if(ladeni){
            kon.posliRadek(toString());
        }
        switch (navratovyKod){
            case 0:{
                proved();
                break;
            }
            case 1:{break;} //spatnej prepinac, to se nic neprovadi
            case 2:{ //nejaka chyba v gramatice
                proved();
                vypisHelp();
                if(ladiciVypisovani) kon.posliRadek("blok pro navratovy kod 2, navratovy kod:"+navratovyKod);
                break;
            }
            case 3:{ //rozhrani neexistuje
                kon.posliRadek(jmenoRozhrani+": chyba při získávání informací o rozhraní Zařízení nebylo nalezeno");
                break;
            }
            case 4:{ //zadano vice ip adres
                proved();
                break;
            }
            case 5:{ //neplatna ip adresa, v tomto pripade se musi provist vsechno, co je pred tou spatnou IP
                proved(); //takze tohle je spatne, protoze nezalezi na poradi!!!!!
                //ten chybovej vypis uz provede metoda zkontrolujPrikaz
                break;
            }
            case 6:{ //pocetBituMasky byl vetsi nez 32, nicmene metoda zkontrolujPrikaz to uz opravila
                proved();
                break;
            }
            case 7:{ //neplatna adresa add
                proved();
                break;
            }
            case 8:{//neplatna adresa del
                proved(); //muzu to v klidu provist, protoze se ta spatna adresa uz stejne smazala
                break;
            }
        }
        if(ladiciVypisovani){
            kon.posliRadek("");
            kon.posliRadek("navratovy kod:"+navratovyKod);
        }
    }

    private void proved() { //nastavuje
        if(ladiciVypisovani){ //jen ladeni
            kon.posliRadek("Spoustim metodu proved(): navratovy kod:"+navratovyKod);
        }
        if (rozhrani == null) { //vypsat vsechno
            for (SitoveRozhrani rozhr : pc.rozhrani) {
                vypisRozhrani(rozhr);
            }
        } else { //rozhrani bylo zadano
            if (seznamIP.size() == 0 && add == null && del.size() == 0 && maska == null && broadcast == null) {
                        //jenom vypis rozhrani
                if(navratovyKod==0) vypisRozhrani(rozhrani); //vypisuje se jen kdyz to bylo spravne zadano
            } else { //nastavovani
                nastavAdresuAMasku(rozhrani);
                //nastavovani broadcastu zatim nepodporuju
                if(navratovyKod!=7 && add !=null){ //parametr add byl zadan a je spravnej ale zatim
                    // ho nepodporuju
                    //POZOR pri implementaci:
                    //Je to jediny nastaveni, u kteryho se rozhoduje podle navratovyho kodu, na ten se ale neda
                    //spolehat, protoze se muze prepsat
                }
                //nastavovani parametru del zatim nepodporuju
            }
        }
    }

    private void vypisRozhrani(SitoveRozhrani r){
        //je to jen provizorni vypis
        kon.posliRadek(r.jmeno+"\tLink encap:Ethernet  HWadr "+r.macAdresa);
        if (r.ip != null) {
            kon.posliRadek("\tinet adr:" + r.ip.vypisAdresu() + "  Všesměr:" + r.ip.vypisBroadcast() +
                    "  Maska:" + r.ip.vypisMasku());
        }
        kon.posliRadek("\tAKTIVOVÁNO VŠESMĚROVÉ_VYSÍLÁNÍ BĚŽÍ MULTICAST  MTU:1500  Metrika:1"); //asi ne cesky
        kon.posliRadek("\tRX packets:169765 errors:2 dropped:14 overruns:2 frame:0"); //ty cisla by chtely generovat
        kon.posliRadek("\tTX packets:157184 errors:0 dropped:0 overruns:0 carrier:0");
        kon.posliRadek("\tkolizí:0 délka odchozí fronty:1000");
        kon.posliRadek("\tPřijato bajtů: 155979699 (155.9 MB) Odesláno bajtů: 26830180 (26.8 MB)");
        kon.posliRadek("\tPřerušení:21 Vstupně/Výstupní port:0xa000");
        kon.posliRadek("");
    }

    /**
     * Pokusi se nejprve nastavit adresu (pokud je zadana), pak masku ze Stringu (je-li zadana)
     * a nakonec i masku z pocetBituMasky (je-li zadana), protoze ta ma vetsi prioritu. Pokud se
     * provedla nejaka zmena vyridi nakonec routovaci tabulku. Sama nic nenastavuje, ale pouziva
     * k tomu privatni metody. Je-li zadana maska obema zpusoby, zmeni se dvakrat (tim padem i
     * routovaci tabulka, i kdyz je vysledek stejnej jako predchozi hodnoty, napr:
     * ifconfig eth0 1.1.1.1/24 netmask 255.255.0.0 se zmeni nejprv na tu ze stringu, pak na
     * tu za lomitkem)
     * @param r
     */
    private void nastavAdresuAMasku(SitoveRozhrani r) { //nastavuje ip
        boolean zmena=false; // jestli se vykonala nejaka zmena, nebo jestli zadany hodnoty byly stejny 
                                // jako puvodni -> kvuli zmenam routovaci tabulky

        //nastavovani adresy:
        if (pouzitIp != -1){ //adresa byla zadana, musi se nastavit
            String nastavit = seznamIP.get(pouzitIp);
            if (r.ip!=null && nastavit.equals(r.ip.vypisAdresu())) {
                //ip existuje a je stejna, nic se nemeni
            } else { //IP adresa neni stejna, bude se menit
                r.ip = vytvorAdresu(nastavit);
                zmena=true;
            }
        }

        //nastavovani masky ze Stringu m
        if (maska != null) { //zadana adresa s maskou za lomitkem
            if(r.ip!=null && r.ip.vypisMasku().equals(maska)){
                //ip adresa existuje a ma stejnou masku, nic se nemeni
            }else{//zadana hodnota je jina nez puvodni, musi se menit
                priradMasku(r.ip, maska);
                zmena=true;
            }
        }

        //nastavovani masky za lomitkem
        if (pocetBituMasky != -1) { //zadana adresa s maskou za lomitkem
            if(r.ip!=null && r.ip.pocetBituMasky()==pocetBituMasky){
                //ip adresa existuje a ma stejnou masku, nic se nemeni
            }else{//zadana hodnota je jina nez puvodni, musi se menit
                priradMasku(r.ip, pocetBituMasky);
                zmena=true;
            }
        }

        //kdyz se provedla nejaka zmena, musi se to projevit v routovaci tabulce:
        if(zmena)vyridRoutovani(r);

    }

    /**
     * Vytvori novou adresu, nenastavuje masku, ale hlida, jestli IpAdresu lze pouzit,
     * nebo jestli na ni neni nejaka specialni akce.
     * @param ip
     * @return null pro 0.0.0.0
     */
    private IpAdresa vytvorAdresu(String adr){
        if(adr.equals("0.0.0.0")){ //mazani adresy z rozhrani
            return null;
        }else{
            return new IpAdresa(adr);
        }
    }

    /**
     * Zadane IP adrese nastavi masku podle zadaneho poctuBitu (masky). pocetBitu musi bejt spravny cislo.
     * Kdyz je adresa null, posle chybovy hlaseni a skonci.
     * @param ip
     * @param pocetBitu
     */
    private void priradMasku(IpAdresa ip, int pocetBitu){
        if(ip==null){
            kon.posliRadek("SIOCSIFNETMASK: Cannot assign requested address");
            return;
        }else{
            ip.nastavMasku(pocetBitu);
        }
    }

    /**
     * Pokusi se nastavit masku podle parametru m, ktery musi bejt spravnym stringem.
     * Je-li zadana IP null, vypise chybovy hlaseni a ukonci se.
     * @param ip adresa, ktera se ma zmenit
     * @param m string masky; nesmi bejt null
     */
    private void priradMasku(IpAdresa ip, String m){//pokusi se nastavit masku
        if(ip==null){ //neni nastavena IP adresa, vypise se chybovy hlaseni a skonci se
            kon.posliRadek("SIOCSIFNETMASK: Cannot assign requested address");
            return;
        }
        try{//je potreba zkontrolovat spravnost masky!!! //proto vyjimka
            ip.nastavMasku(m);
        }catch(SpatnaMaskaException ex){
            kon.posliRadek("SIOCSIFNETMASK: Invalid argument");
            return;
        }
    }

    private void vyridRoutovani(SitoveRozhrani r){
        pc.routovaciTabulka.smazVsechnyZaznamyNaRozhrani(r); //mazani rout
        if(r.ip!=null){
            pc.routovaciTabulka.pridejZaznam(r.ip.vratCisloSite(), r);
        }
    }

    private void vypisHelp(){ // funkce na ladiciVypisovani napovedy --help
        kon.posliRadek("Použití: (...)");
    }

    @Deprecated //zjistil jsem, ze tahle metoda vlastne neni vubec potreba
    private void unknownHost(String vypsat){
        kon.posliRadek(vypsat+": Unknown host");
        kon.posliRadek("ifconfig: `--help' vypíše návod k použití.)");
    }

    @Override
    public String toString() {
        String vratit = "  Parametry prikazy ifconfig:\n\r\tnavratovyKodParseru: " + navratovyKod;
        if (jmenoRozhrani != null) {
            vratit += "\n\r\trozhrani: " + jmenoRozhrani;
        }
        if (seznamIP != null) {
            vratit += "\n\r\tip: " + seznamIP;
        }
        vratit+="\n\r\tpouzitIp: "+pouzitIp;
        if (pocetBituMasky != -1) {
            vratit += "\n\r\tpocetBituMasky: " + pocetBituMasky;
        }
        if (maska != null) {
            vratit += "\n\r\tmaska: " + maska;
        }
        if (add != null) {
            vratit += "\n\r\tadd: " + add;
        }
        if (del != null) {
            vratit += "\n\r\tdel: " + del;
        }

        return vratit;

    }

    private void errNeznamyPrepinac(String ret) {
        kon.posliRadek("ifconfig: neznámá volba `" + ret + "'.");
        kon.posliRadek("ifconfig: `--help' vypíše návod k použití.");
        navratovyKod = 1;
    }
}
