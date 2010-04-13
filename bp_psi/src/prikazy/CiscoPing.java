/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prikazy;

import datoveStruktury.IpAdresa;
import datoveStruktury.Paket;
import java.util.List;
import pocitac.AbstractPocitac;
import pocitac.Konsole;

/**
 *
 * @author haldyr
 */
public class CiscoPing extends AbstraktniPing {

    IpAdresa cil;

    public CiscoPing(AbstractPocitac pc, Konsole kon, List<String> slova) {
        super(pc, kon, slova);
        boolean popkracuj = zpracujRadek();
        if (popkracuj) {
            vykonejPrikaz();
        }
    }

    private boolean zpracujRadek() {
        if (slova.size() < 2) {
            return false;
        }
        try {
            cil = new IpAdresa(slova.get(1));
        } catch (Exception e) {
            kon.posliRadek("Translating \"" + slova.get(1) + "\"" + "...domain server (255.255.255.255)");
            cekej(1000);
            kon.posliRadek("% Unrecognized host or address, or protocol not running.");
            return false;
        }
        return true;
    }

    

    @Override
    protected void vykonejPrikaz() {
        if (pc.posliIcmpRequest(cil, 0, 64, this)) {
            for (int i = 1; i < 1; i++) {
                cekej(1000);
                pc.posliIcmpRequest(cil, i, 64, this);
            }
        }
    }

    @Override
    //TODO: dodelat ping pro cisco
    public void zpracujPaket(Paket p) {
        if (p.typ == 0) {
            kon.posliRadek("64 bytes from " + p.zdroj.vypisAdresu() + ": icmp_seq=" +
                    p.icmp_seq + " ttl=" + p.ttl + " time=" + ((double) Math.round(p.cas * 1000)) / 1000 + " ms");
        } else if (p.typ == 3) {
            if (p.kod == 0) {
                kon.posliRadek("From " + p.zdroj.vypisAdresu() + ": icmp_seq=" +
                        p.icmp_seq + " Destination Net Unreachable");
            } else if (p.kod == 1) {
                kon.posliRadek("From " + p.zdroj.vypisAdresu() + ": icmp_seq=" +
                        p.icmp_seq + " Destination Host Unreachable");
            }
        } else if (p.typ == 11) {
            kon.posliRadek("From " + p.zdroj.vypisAdresu() + " icmp_seq=" + p.icmp_seq + "Time to live exceeded");
        }
    }
}
