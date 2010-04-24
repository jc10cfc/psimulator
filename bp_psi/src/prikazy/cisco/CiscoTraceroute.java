/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prikazy.cisco;

import datoveStruktury.IpAdresa;
import datoveStruktury.Paket;
import java.util.List;
import pocitac.AbstraktniPocitac;
import pocitac.Konsole;
import prikazy.AbstraktniPing;
import prikazy.AbstraktniPrikaz;
import static Main.Main.*;

/**
 *
 * @author haldyr
 */
public class CiscoTraceroute extends AbstraktniPing {

    IpAdresa adr;
    int navrKod = 0;
    int maxTtl = 30; //to je to, co se vypisuje jako hops max
    double interval = 0.1; //interval mezi odesilanim v sekudach
    /**
     * Stav vykonavani prikazu:
     * 0 - ceka se na pakety<br />
     * 1 - vratil se paket od cilovyho pocitace - skoncit<br />
     * 2 - byl timeout - vypisovat hvezdicky a skoncit<br />
     * 3 - vratilo se host unreachable nebo net unreachable<br />
     */
    int stavKonani = 0;

    public CiscoTraceroute(AbstraktniPocitac pc, Konsole kon, List<String> slova) {
        super(pc, kon, slova);

        zpracujRadek();
        if (navrKod == 0) {
            vykonejPrikaz();
        }
    }
    boolean debug = true;

    protected void zpracujRadek() {
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
                dopisZbylyHvezdicky(i - 1);
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
        double k1 = (Math.random() / 5) + 0.9; //vraci cisla mezi 0.9 a 1.1
        double k2 = (Math.random() / 5) + 0.0; //vraci cisla mezi 0.9 a 1.1
        prijate++;

        kon.posli(zarovnejZLeva(prijate+"", 3)+" "+p.zdroj.vypisAdresu()+ " ");

        if (p.typ == 0) { //icmp reply - jsem v cili
            stavKonani = 1;
            kon.posli(zaokrouhli(p.cas) + " msec " + zaokrouhli(p.cas * k1) + " msec " + zaokrouhli(p.cas * k2) + " msec");
            
        } else if (p.typ == 3) {
            stavKonani = 3;
            if (p.kod == 0) {
                kon.posliRadek(zarovnej(prijate + "", 2) + "  " + p.zdroj.vypisAdresu() + " (" + p.zdroj.vypisAdresu()
                        + ")  " + zaokrouhli(p.cas) + " ms !N  " + zaokrouhli(p.cas * k1) + " ms !N  "
                        + zaokrouhli(p.cas * k2) + " ms !N");
            } else if (p.kod == 1) {
                kon.posliRadek(zarovnej(prijate + "", 2) + "  " + p.zdroj.vypisAdresu() + " (" + p.zdroj.vypisAdresu()
                        + ")  " + zaokrouhli(p.cas) + " ms !H  " + zaokrouhli(p.cas * k1) + " ms !H  "
                        + zaokrouhli(p.cas * k2) + " ms !H");
            }
        } else if (p.typ == 11) { //timeout - musim pokracovat
            kon.posliRadek(zarovnej(prijate + "", 2) + "  " + p.zdroj.vypisAdresu() + " (" + p.zdroj.vypisAdresu()
                    + ")  " + zaokrouhli(p.cas) + " ms  " + zaokrouhli(p.cas * k1) + " ms  " + zaokrouhli(p.cas * k2) + " ms ");
        }

        kon.posli("\n");
    }

    public double zaokrouhliNaCely(double cislo) {
//        return cislo;
        return Math.round(cislo);
    }

    protected String zarovnejZLeva(String ret, int dylka) {
        //if (ret.length() >= dylka) return ret;
        int dorovnat = dylka - ret.length();
        String s = "";
        for(int i=0;i<dorovnat;i++){
            s += " ";
        }
        return s+ret;


    }

    private void dopisZbylyHvezdicky(int a) {
        for (int i = a; i < maxTtl; i++) {
            kon.posliRadek(zarovnej((i + 1) + "", 2) + "  * * *");
        }
    }
}
