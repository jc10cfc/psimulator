/*
 * Mittwoch 21.4.2010 Abend
 */

package prikazy.linux;

import Main.Main;
import datoveStruktury.IpAdresa;
import datoveStruktury.Paket;
import java.util.List;
import pocitac.*;
import prikazy.AbstraktniPrikaz;
import prikazy.AbstraktniTraceroute;
import vyjimky.SpatnaAdresaException;

/**
 * Prikaz traceroute.
 * Kaslu na rozsahlou implementaci parseru, bude to umet jen blbe precist adresu, nic vic.
 * Vsechny pakety, ktery mi zpatky dojdou pocitam za prijaty (prijate++), z toho pak pocitam,
 * jestli neco timeoutovalo.
 * @author neiss
 */
public class LinuxTraceroute extends AbstraktniTraceroute {

    public LinuxTraceroute(AbstraktniPocitac pc, Konsole kon, List<String> slova) {
        super(pc, kon, slova);
        parsujPrikaz();
        vykonejPrikaz();
    }

    @Override
    protected void parsujPrikaz(){
        try{
            adr=new IpAdresa(dalsiSlovo());
        }catch(SpatnaAdresaException ex){
            navrKod=1;
            kon.posliRadek(Main.jmenoProgramu + ": traceroute: Chyba v syntaxi prikazu," +
                    " jedina povolena syntaxe je \"traceroute <adresa>\"");

        }
    }

    @Override
    protected void vykonejPrikaz() {
        if(navrKod!=0)return;

        /*
         * Neodesilani zkusebniho paketu s icmp_seq -1. Je jen zkusebni, metoda odesliNovejPaket(...)
         * ho zablokuje a nikam se NEPOSILA ani nepocita.
         */
        if (pc.posliIcmpRequest(adr, -1, maxTtl, this)) {
            //paket pujde poslat - vypsani prvniho radku:
            kon.posliRadek("traceroute to "+adr.vypisAdresu()+" ("+adr.vypisAdresu()+
                    "), "+maxTtl+" hops max, 40 byte packets");
        } else {
            //paket nepujde poslat, vypise se hlaseni a program se ukonci
            //ve skutecnosti se ale neukoncuje
            kon.posliRadek("traceroute: Warning: findsaddr: netlink error: Network is unreachable");
            return; //dalsi pakety se neposilaj, prvni radek se nevypisuje
        }

        /*
         * posilani dalsich pingu:
         */
        for(int i=0;i<maxTtl;i++) {
           //nejdriv se kontrolujeminule odeslanej paket:
            if (stavKonani == 1) { //uz dorazil paket z cile
                break;
            }
            if (prijate < odeslane) { //posledni paket nedorazil
                stavKonani=2;
//                kon.posliRadek("paket timeoutoval");
                dopisZbylyHvezdicky(i-1);
                break;
            }
            if (stavKonani == 3) { //vratilo se host nebo net unreachable
                break;
            }
           //pak se posila novej paket
            int icmp_seq = (i + 1) % 65536; //zacina to od jednicky a po 65535 se jede znova od nuly
            int ttl = i + 1; //ttl se od jednicky postupne zvysuje
            if (pc.posliIcmpRequest(adr, icmp_seq, ttl, this)) {
                //paket se odeslal
            } else {
                //proste to necham timeoutovat
            }
            odeslane++;
            if (i != maxTtl - 1) //cekani po zadany interval - naposled se neceka
            {
                AbstraktniPrikaz.cekej((int) (interval * 1000));
            }

        }
    }


    @Override
    public void zpracujPaket(Paket p) {
        double k1 = (Math.random()/5)+0.9; //vraci cisla mezi 0.9 a 1.1
        double k2 = (Math.random()/5)+0.0; //vraci cisla mezi 0.9 a 1.1
        prijate++;

        if (p.typ == 0) { //icmp reply - jsem v cili
            stavKonani=1;
            kon.posliRadek(zarovnej(prijate+"", 2)+"  "+p.zdroj.vypisAdresu()+" ("+p.zdroj.vypisAdresu()
                    +")  "+zaokrouhli(p.cas)+" ms  "+zaokrouhli(p.cas*k1)+" ms  "+zaokrouhli(p.cas*k2)+" ms ");
        } else if (p.typ == 3) {
            stavKonani=3;
            if (p.kod == 0) {
                kon.posliRadek(zarovnej(prijate+"", 2)+"  "+p.zdroj.vypisAdresu()+" ("+p.zdroj.vypisAdresu()
                    +")  "+zaokrouhli(p.cas)+" ms !N  "+zaokrouhli(p.cas*k1)+" ms !N  "
                    +zaokrouhli(p.cas*k2)+" ms !N");
            } else if (p.kod == 1) {
                kon.posliRadek(zarovnej(prijate+"", 2)+"  "+p.zdroj.vypisAdresu()+" ("+p.zdroj.vypisAdresu()
                    +")  "+zaokrouhli(p.cas)+" ms !H  "+zaokrouhli(p.cas*k1)+" ms !H  "
                    +zaokrouhli(p.cas*k2)+" ms !H");
            }
        } else if (p.typ == 11) { //timeout - musim pokracovat
            kon.posliRadek(zarovnej(prijate+"", 2)+"  "+p.zdroj.vypisAdresu()+" ("+p.zdroj.vypisAdresu()
                    +")  "+zaokrouhli(p.cas)+" ms  "+zaokrouhli(p.cas*k1)+" ms  "+zaokrouhli(p.cas*k2)+" ms ");
        }
    }

    protected void dopisZbylyHvezdicky(int a) {
        for (int i = a; i < maxTtl; i++) {
            kon.posliRadek(zarovnej((i + 1) + "", 2) + "  * * *");
        }
    }
}

