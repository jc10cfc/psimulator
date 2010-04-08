/*
 * Gegründet am Mittwoch 10.3.2010
 */


package datoveStruktury;

import java.util.LinkedList;
import java.util.List;
import pocitac.SitoveRozhrani;
import vyjimky.ChybaKonfigurakuException;

/**
 * Trida, ktera representuje routovaci tabulku pocitace, jak linuxoveho, tak ciscoveho.
 * Pozn:
 * Zaznamy se nakonec budou radit jen podle masky. Pro pridavani novyho zaznamu UG plati podminka, za nove
 * zadavana brana musi jiz bejt dosazitelna priznakem U.
 * @author neiss
 */
public class RoutovaciTabulka {

    /**
     * Representuje jeden radek v routovaci tabulce
     */
    public class Zaznam {
        private IpAdresa adresat;   // ty promenny jsou privatni, nechci, aby se daly zvenci upravovat
        private IpAdresa brana;
        private SitoveRozhrani rozhrani;

        public IpAdresa getAdresat() {  //getry pro vypis
            return adresat;
        }
        public IpAdresa getBrana() {
            return brana;
        }
        public SitoveRozhrani getRozhrani() {
            return rozhrani;
        }

        private Zaznam(IpAdresa adresat, SitoveRozhrani rozhrani){
            this.adresat=adresat;
            this.rozhrani=rozhrani;
        }
        private Zaznam(IpAdresa adresat, IpAdresa brana, SitoveRozhrani rozhrani){
            this.adresat=adresat;
            this.brana=brana;
            this.rozhrani=rozhrani;
        }

        private String vypisSeLinuxove() {
            String v="";
            v+=adresat.vypisAdresu()+"\t";
            if(brana==null){
                v+="0.0.0.0\t"+adresat.vypisMasku()+"\tU\t";
            } else{
                v+=brana.vypisAdresu()+"\t"+adresat.vypisMasku()+"\tUG\t";
            }
            v+="0\t0\t0\t"+rozhrani.jmeno+"\n";
            return v;
        }

        private String vypisSeCiscove() {
            String s = "";
            s += "ip route " + adresat.vypisAdresu() + " " + adresat.vypisMasku() + " " + brana.vypisAdresu() + "\n";
            return s;
        }
    }
    private List<Zaznam>radky; //jednotlive radky routovaci tabulky
//    private AbstractPocitac pc; //odkaz na pocitac, mozna nebude potreba
    private boolean ladiciVypisovani=true;

    /**
     * V konstruktoru se hazi odkaz na pocitac, aby byl prostup k jeho rozhranim.
     * @param pc
     */
    public RoutovaciTabulka(){
        radky=new LinkedList<Zaznam>();
//        this.pc=pc;
    }

    /**
     * Tahleta metoda hleda zaznam v routovaci tabulce, ktery odpovida zadane IP adrese a vrati jeho rozhrani.
     * Slouzi predevsim pro samotne routovani.
     * @param cil IP, na kterou je paket posilan
     * @return null - nenasel se zadnej zaznam, kterej by se pro tuhle adresu hodil
     */
    public SitoveRozhrani najdiSpravnyRozhrani(IpAdresa cil){
        for( Zaznam z:radky){
            if(cil.jeVRozsahu(z.adresat)) return z.rozhrani; //vraci prvni odpovidajici rozhrani
        }
        return null;
    }

    /**
     * Tahleta metoda hleda zaznam v routovaci tabulce, ktery odpovida zadane IP adrese a cely ho vrati.
     * Slouzi predevsim pro samotne routovani, kdyz potrebuju znat cely zaznam, a ne jen rozhrani (napr.
     * na jakou branu to mam poslat, jestli se to odesle.
     * @param cil IP, na kterou je paket posilan
     * @return null - nenasel se zadnej zaznam, kterej by se pro tuhle adresu hodil
     */
    public Zaznam najdiSpravnejZaznam(IpAdresa cil){
        for( Zaznam z:radky){
            if(cil.jeVRozsahu(z.adresat)) return z; //vraci prvni odpovidajici rozhrani
        }
        return null;
    }

    /**
     * Prida novej zaznam, priznaku UG, tzn na branu. V okamziku pridani musi bejt brana dosazitelna s priznakem U,
     * tzn. na rozhrani, ne gw. Lze pridat i zaznam s predvyplnenim rozhranim, i takovej ale musi mit branu uz
     * dosazitelnou na tomhle rozhrani.
     * @param adresat musi bejt vyplnenej
     * @param brana musi bejt vyplnena
     * @param rozhr muze bejt null
     * @return 0: v poradku<br /> 1: existuje stejny zaznam;<br />
     * 2: rozhrani nenalezeno, pro zadaneho adresata neexistuje zaznam U<br />
     */
    public int pridejZaznam(IpAdresa adresat, IpAdresa brana, SitoveRozhrani rozhr) {
        boolean rozhraniNalezeno = false;
        for (Zaznam z : radky) { //hledani spravnyho rozhrani
            if (z.brana == null) { //tohle by moh bejt zaznam potrebnej zaznam priznaku U
                if (!rozhraniNalezeno && brana.jeVRozsahu(z.adresat)) { //nalezl se adresat brane odpovidajici
                    if (rozhr == null) { //rozhrani neni zadano a je potreba ho priradit
                        rozhr = z.rozhrani; //takhle to opravdu funguje, 1. polozka se pocita
                        rozhraniNalezeno = true;
                        break;
                    } else { //rozhrani bylo zadano a je potreba zjistit, jestli je brana v dosahu tohodle rozhrani
                        if (z.rozhrani == rozhr) { //kdyz se rovnaji, je rozhrani nalezeno
                            rozhraniNalezeno = true;
                            break;
                        }
                    }
                }
            }
        }
        if (!rozhraniNalezeno) {
            return 2; // rozhrani nenalezeno, pro zadaneho adresata neexistuje zaznam U
        }
        Zaznam z = new Zaznam(adresat, brana, rozhr);
        if (existujeStejnyZaznam(z)) {
            return 1;
        }
        int i = najdiSpravnouPosici(z);
        radky.add(i, z);
        return 0;
    }

    /**
     * Prida novej zaznam priznaku U.
     * Spolecna metoda pro linux i cisco.
     * @param adresat ocekava IpAdresu, ktera je cislem site
     * @param rozhr predpoklada se, ze rozhrani na pocitaci existuje
     * @return 0: v poradku<br /> 1: existuje stejny zaznam;<br />
     */
    public int pridejZaznam(IpAdresa adresat, SitoveRozhrani rozhr){
        Zaznam z=new Zaznam(adresat, rozhr);
        if(existujeStejnyZaznam(z))return 1;
        int i=najdiSpravnouPosici(z);
        radky.add(i,z);
        return 0;
    }

    public void smazVsechnyZaznamy() {
        radky=new LinkedList<Zaznam>();
    }

    /**
     * Tahleta metoda slouží k přidávání záznamů z konfiguráku. Neprováděj se žádný kontroly, prostě se to tam
     * přidává v pořadí, v jakým to bylo uloženo. Adresat a rozhrani musej bejt vyplneny, brana jen u priznaku
     * UG, tzn. u routy na branu.
     * @param adresat
     * @param brana
     * @param rozhr
     */
    public void pridejZaznamBezKontrol(IpAdresa adresat,IpAdresa brana,SitoveRozhrani rozhr){
        if(!adresat.jeCislemSite()){
            throw new ChybaKonfigurakuException("Chyba v konfiguraku, adresat "+ adresat.vypisAdresuSMaskou() +
                    " v routovaci tabulce neni cislem site. " +
                    "mozna pak by to asi chtelo specialni vyjimku");
        }
        radky.add(new Zaznam(adresat,brana,rozhr)); //pridava se to nakonec, neresi se to
    }

    /**
     * Metoda na mazani zaznamu.
     * @param adresat musi byt zadan
     * @param brana muze byt null
     * @param rozhr muze byt null
     * @return true - zaznam smazan<br /> false - zaznam nenalezen - nic nesmazano
     */
    public boolean smazZaznam(IpAdresa adresat, IpAdresa brana, SitoveRozhrani rozhr){
        Zaznam z;
        for(int i=0;i<radky.size();i++){
            z=radky.get(i);
            if(z.adresat.equals(adresat)){ //adresati se rovnaj -> adept na smazani
                if( ( brana == null ) || ( z.brana!=null && z.brana.jeStejnaAdresa(brana) ) ){//zkracene vyhodnocovani
                            //-> brana nezadana nebo zadana a stejna existuje u zaznamu -> adept na smazani
                    if( rozhr==null || (rozhr!=null && rozhr==z.rozhrani)){  //rozhrani nezadano, nebo zadano a
                        radky.remove(i);                                     //odpovida -> smazat
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Smaze vsechny zaznamy (U i UG) na zadanem rozhrani. Potreba pro mazani rout, kdyz se zmeni adresa nebo maska
     * na rozhrani.
     * @param rozhr
     * @return pocet smazanych rozhrani (spis pro ladeni, jinej efekt to asi nema)
     */
    public int smazVsechnyZaznamyNaRozhrani(SitoveRozhrani rozhr){
        int p = 0; //pocet smazanych zaznamu
        List <Zaznam>smazat = new LinkedList(); //dela se to pres pomocnej seznam, protoze jinak hazela
                                                // java vyjimku ConcurrentModificationException
        for(Zaznam z:radky){
            if (z.rozhrani == rozhr){
                smazat.add(z);
                p++;
            }
        }
        for(Zaznam z:smazat){
            radky.remove(z);
        }
        return p;
    }

    @Deprecated
    public String vypisSeLinuxove(){
        String v="";
        v+="Směrovací tabulka v jádru pro IP\n";
        v+="Adresát         Brána           Maska           Přízn\t Metrik\t Odkaz\t  Užt\t Rozhraní\n";
        for (Zaznam z:radky){
            v+=z.vypisSeLinuxove();
        }
        return v;
    }

    @Deprecated
    public String vypisSeCiscove() {
        String s = "";
        s += "ip classless\n";
        for (Zaznam z:radky){
            s += z.vypisSeCiscove();
        }
        return s;
    }
    
    public int pocetZaznamu(){
        return radky.size();
    }

    /**
     * Vrati zaznam na urceny posici, rozhrani pro vypisovaci metody.
     * @param posice
     * @return
     */
    public Zaznam vratZaznam(int posice){
        return radky.get(posice);
    }
//****************************************************************************************************
//privatni metody
    /**
     * Kontroluje, jestli tabulka uz pridavany radek neobsahuje. Zaznam musi obsahovat adresata a rozhrani 
     * (to je predem zjisteno), brana se kontroluje, jen kdyz neni null.
     * @param zazn
     * @return
     */
    private boolean existujeStejnyZaznam(Zaznam zazn){
        for(Zaznam z:radky){
            if( z.adresat.equals(zazn.adresat) ){   // adresati se rovnaji
                if ( z.brana==null && zazn.brana==null){ //obe brany jsou null
                    if( z.rozhrani==zazn.rozhrani){
                        return true;
                    }
                }
                if(z.brana!=null && zazn.brana!=null){ //obe brany nejsou null a rovnaji se
                    if(z.brana.jeStejnaAdresa(zazn.brana) && z.rozhrani==zazn.rozhrani){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Najde spravnou posici pro pridani novyho zaznamu. Skutecny poradi rout je totalne zmatecny (viz. soubor
     * route.txt v package data), takze to radim jenom podle masky, nakonec ani priznaky nerozhodujou.
     * @param z
     * @return
     */
    private int najdiSpravnouPosici(Zaznam z){
        if(z.adresat.dej32BitMasku()==0)return radky.size();
        int i=0;
        //preskakovani delsich masek:
            //pozor, problemy v implementaci kvuli doplnkovymu kodu
        while( i<radky.size() //neprekrocit meze
                && radky.get(i).adresat.dej32BitMasku() > z.adresat.dej32BitMasku() //dokud je vkladana maska mensi
                && radky.get(i).adresat.dej32BitMasku() !=0 ){ //pozor na nulu
            i++;
        }//zastavi se na stejny nebo vetsi masce, nez ma pridavanej zaznam
        //vic se nakonec uz nic neposouva...
        return i;
    }


    /**
     * Tahleta metoda hleda v routovaci tabulce zaznam s priznakem U, ktery odpovida zadane IP adrese.
     * Pouziva se pro pridavani novych zaznamu do routovaci tabulky.
     * @param vstupni
     * @return cislo radku odpovidajici zadane IP nebo -1, kdyz zadnej radek neodpovida
     */
    private int najdiOdpovidajiciRadek(IpAdresa vstupni){
        for (int i=0;i<radky.size();i++){
            if ( vstupni.jeVRozsahu(radky.get(i).adresat) && radky.get(i).brana ==null ){
                    //kdyz je vstupni v rozsahu adresata a zaroven je priznak U
                return i;
            }
        }
        return -1;
    }

    

}
