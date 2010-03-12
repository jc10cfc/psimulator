/*
 * Gegründet am Mittwoch 10.3.2010
 */


package datoveStruktury;

import java.util.AbstractList;
import java.util.LinkedList;
import pocitac.AbstractPocitac;
import pocitac.SitoveRozhrani;

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
    private class Zaznam{
        IpAdresa adresat;
        IpAdresa brana;
        SitoveRozhrani rozhrani;

        public Zaznam(IpAdresa adresat, SitoveRozhrani rozhrani){
            this.adresat=adresat;
            this.rozhrani=rozhrani;
        }
        public Zaznam(IpAdresa adresat, IpAdresa brana, SitoveRozhrani rozhrani){
            this.adresat=adresat;
            this.brana=brana;
            this.rozhrani=rozhrani;
        }
    }
    private AbstractList<Zaznam>radky; //jednotlive radky routovaci tabulky
    private AbstractPocitac pc; //odkaz na pocitac, mozna nebude potreba

    /**
     * V konstruktoru se hazi odkaz na pocitac, aby byl prostup k jeho rozhranim.
     * @param pc
     */
    public RoutovaciTabulka(AbstractPocitac pc){
        radky=new LinkedList<Zaznam>();
        this.pc=pc;
    }

    /**
     * Tahleta metoda hleda zaznam v routovaci tabulce, ktery odpovida zadane IP adrese. Slouzi predevsim pro
     * samotne routovani.
     * @param cil - IP, na kterou je paket posilan
     * @return null - nenasel se zadnej zaznam, kterej by se pro tuhle adresu hodil
     */
    public SitoveRozhrani najdiSpravnyRozhrani(IpAdresa cil){
        for( Zaznam z:radky){
            if(cil.jeVRozsahu(z.adresat)) return z.rozhrani; //vraci prvni odpovidajici rozhrani
        }
        return null;
    }

    /**
     * prida novej zaznam, priznaku UG. V okamziku pridani musi bejt brana dosazitelna s priznakem U,
     * tzn na rozhrani, ne gw
     * @param adresat
     * @param brana
     * @param posice
     */
    public int pridejZaznam(IpAdresa adresat, IpAdresa brana){
        
        return 0;
    }

    /**
     * Prida novej zaznam priznaku U.
     * @param adresat - ocekava IpAdresu, ktera je cislem site
     * @param rozhr - predpoklada se, ze rozhrani na pocitaci existuje
     * @return
     */
    public int pridejZaznam(IpAdresa adresat, SitoveRozhrani rozhr){
        Zaznam z=new Zaznam(adresat, rozhr);
        if(existujeStejnyZaznam(z))return 1;
        int i=najdiSpravnouPosici(z);
        radky.add(i,z);
        return 0;
    }

    private boolean existujeStejnyZaznam(Zaznam zazn){
        for(Zaznam z:radky){
            if( z.adresat.equals(zazn.adresat) && z.brana.jeStejnaAdresa(zazn.brana))
                return true;
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
        int i=0;
        //preskakovani delsich masek:
        while( i<radky.size() && radky.get(i).adresat.dejMasku() > z.adresat.dejMasku() ){
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
