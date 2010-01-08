/*
 * http://www.benak.net/pocitace/os/linux/ifconfig.php
 * http://books.google.cz/books?id=1x-6XBk8bKoC&pg=PT26&lpg=PT26&dq=ifconfig&source=bl&ots=E5ys4iqBVw&sig=0LU94iuXjoBE3WDxYGnTHChcx9Q&hl=cs&ei=O1lGS6CFHoOCnQOZzP3vAg&sa=X&oi=book_result&ct=result&resnum=8&ved=0CBkQ6AEwBw#v=onepage&q=ifconfig&f=false
 * http://www.starhill.org/man/8_ifconfig.html
 */
package prikazy;

import java.util.List;
import pocitac.*;

/**
 *
 * @author neiss
 */
public class Ifconfig extends AbstraktniPrikaz {

    String rozhrani;
    String ip;
    String maska;
    String broadcast;
    int pocetBituMasky=-1; //maska zadana formou /24 totiz ma vetsi prioritu nez 255.255.255.0
    String add; //ipadresa, ktera se ma pridat
    String del;  //ipadresa, ktera se ma odebrat
    boolean minus_a = false;
    boolean minus_v = false;
    boolean minus_s = false;
    /**
     * Do tyhle promenny bude metoda nastavPrikaz zapisovat, jakou chybu nasla:
     * 0: vsechno v poradku
     * 1: spatny prepinac (neznama volba)
     * 2: nejaka chyba, potreba provest, co je dobre, a vypsat napovedu --help (napr: ifconfig wlan0 1.2.3.5 netmask)
     */
    int navratovyKodParseru = 0;

    public Ifconfig(AbstractPocitac pc, Konsole kon, List<String> slova) {
        super(pc, kon, slova);
        nastavPrikaz();
        vykonejPrikaz();
    }

    @Override
    protected void nastavPrikaz() {
        String tempRet;
        int ind = 1; //index v seznamu, zacina se jedicko, protoze prvnim slovem je ifconfig
        //volby:
        while ( ind<slova.size() && slova.get(ind).indexOf("-")==0 ) { //kdyz je prvnim znakem slova minus
            if (slova.get(ind).equals("-a")) minus_a = true;
            else if (slova.get(ind).equals("-v"))minus_v = true;
            else if (slova.get(ind).equals("-s"))minus_s = true;
            else {
                errNeznamyPrepinac(slova.get(ind));
                return; //tady ifconfig uz zbytek neprovadi, i kdyby byl dobrej
            }
            ind++;
        }
        //rozhrani je to prvni za prepinacema:
        if(ind>=slova.size())return;
        rozhrani=slova.get(ind);
        ind++;if(ind>=slova.size())return;
        //parametry:
        //Zjistil jsem, ze neznamen parametr se povazuje za adresu nebo za adresu s maskou.
        while(ind<slova.size()){
            tempRet=slova.get(ind);
            if(tempRet.equals("netmask")){//maska
                ind++;
                if(ind>=slova.size()){
                    navratovyKodParseru=2;
                    return;
                }
                maska=slova.get(ind);
            }else if(tempRet.equals("broadcast")){//adresa pro broadcast, ta si vubec dela uplne, co se ji zachce
                ind++;
                if(ind>=slova.size()){
                    navratovyKodParseru=2;
                    return;
                }
                broadcast=slova.get(ind);
            }else{ //kdyz to neni nic jinyho, tak to ifconfig povazuje za ip adresu
                int pos=tempRet.indexOf('/');
                if(pos!=-1){ //zadano i s maskou za lomitkem
                    ip=tempRet.substring(0, pos);
                    pocetBituMasky=Integer.parseInt(tempRet.substring(pos+1,tempRet.length()));
                }else{
                    ip=tempRet;
                }
                kon.posli("jsem tady");
            }
            ind++;
        }

        kon.posli("Parsovani ifconfig probehlo v poradku.");
    }

    private void errNeznamyPrepinac(String ret){
        kon.posli("ifconfig: neznámá volba `"+ret+"'.");
        kon.posli("ifconfig: `--help' vypíše návod k použití.");
        navratovyKodParseru = 1;
    }

    @Override
    protected void vykonejPrikaz() {
        kon.posli(toString());
    }

    @Override
    public String toString(){
        String vratit = "Parametry prikazy ifconfig:\n navratovyKodParseru: "+navratovyKodParseru;
        if(rozhrani!=null) vratit+="\n rozhrani: "+rozhrani;
        if(ip!=null) vratit+="\n ip: "+ip;
        if(pocetBituMasky!=-1) vratit+="\n pocetBituMasky: "+pocetBituMasky;
        if(maska!=null) vratit+="\n maska: "+maska;
        if(add!=null) vratit+="\n add: "+add;
        if(del!=null) vratit+="\n del: "+del;

        return vratit;

    }
}
