/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prikazy.cisco;

import datoveStruktury.IpAdresa;
import datoveStruktury.Paket;
import java.util.List;
import pocitac.AbstraktniPocitac;
import pocitac.apps.CommandShell.CommandShell;
import prikazy.AbstraktniPrikaz;
import prikazy.AbstraktniTraceroute;
import static Main.Main.*;

/**
 * Prikaz traceroute.
 * Implementovana je pouze zakladni funkcnost 'traceroute IP'.
 * Vsechny pakety, ktery mi zpatky dojdou pocitam za prijaty (prijate++), z toho pak pocitam,
 * jestli neco timeoutovalo.
 * @author Stanislav Řehák
 */
public class CiscoTraceroute extends AbstraktniTraceroute {

    public CiscoTraceroute(AbstraktniPocitac pc, CommandShell kon, List<String> slova) {
        super(pc, kon, slova);

        parsujPrikaz();
        vykonejPrikaz();
    }

    @Override
    protected void parsujPrikaz() {
        String dalsi = dalsiSlovo();
        try {
            adr = new IpAdresa(dalsi);
        } catch (Exception e) {
            kon.posliRadek("Translating \"" + dalsi + "\"...domain server (255.255.255.255)");
            kon.posliRadek("% Unrecognized host or address.");
            kon.posliRadek(jmenoProgramu + ": Preklad hostname na IP neni implementovan.");
            navrKod = 1;
            return;
        }
        navrKod = 0;
    }

    @Override
    protected void vykonejPrikaz() {
        if(navrKod!=0)return;
        
        kon.posliPoRadcich("\n"
                + "Type escape sequence to abort.\n"
                + "Tracing the route to " + adr.vypisAdresu() + "\n\n", 250);

        /*
         * posilani pingu:
         */
        for (int i = 0; i < maxTtl; i++) {
            //nejdriv se kontrolujeminule odeslanej paket:
            if (stavKonani == 1) { //uz dorazil paket z cile
                break;
            }
            if (prijate < odeslane) { //posledni paket nedorazil
                stavKonani = 2;
//                kon.posliRadek("paket timeoutoval");
                for (int k = i-1; k < maxTtl; k++) {
                    kon.posliRadek(zarovnejZLeva((k + 1) + "", 3) + "  *  *  *");
                }
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
            if (i != maxTtl - 1) { //cekani po zadany interval - naposled se neceka
                AbstraktniPrikaz.cekej((int) (interval * 1000));
            }
        }
    }

    @Override
    public void zpracujPaket(Paket p) {
        prijate++;

        kon.posli(zarovnejZLeva(prijate+"", 3)+" "+p.zdroj.vypisAdresu()+ " ");

        if (p.typ == 0) { //icmp reply - jsem v cili
            stavKonani = 1;
            kon.posli(vratCasyPaketu(p));
            
        } else if (p.typ == 3) {
            stavKonani = 3;
            if (p.kod == 0) {
                kon.posli("!N  *  !N");
            } else if (p.kod == 1) {
                kon.posli("!H  *  !H");
            } else if (p.kod == 3) {
                kon.posli("!P  *  !P");
            } else {
                kon.posli("?  *  ?");
            }
        } else if (p.typ == 11) { //timeout - musim pokracovat
            kon.posli(vratCasyPaketu(p));
        }

        kon.posli("\n");
    }

    public long zaokrouhliNaCely(double cislo) {
        return Math.round(cislo);
    }

    /**
     * Vrati casy paketu vhodne pro vypis do konzole.
     * @param p
     * @return
     */
    private String vratCasyPaketu(Paket p) {
        double k1 = (Math.random() / 5) + 0.9; //vraci cisla mezi 0.9 a 1.1
        double k2 = (Math.random() / 5) + 0.0; //vraci cisla mezi 0.9 a 1.1
        return zaokrouhliNaCely(p.cas) + " msec " + zaokrouhliNaCely(p.cas * k1) + " msec " + zaokrouhliNaCely(p.cas * k2) + " msec";
    }
}
