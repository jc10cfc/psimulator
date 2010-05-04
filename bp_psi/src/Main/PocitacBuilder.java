package Main;

import datoveStruktury.IpAdresa;
import java.util.ArrayList;
import java.util.List;
import pocitac.AbstraktniPocitac;
import pocitac.CiscoPocitac;
import pocitac.LinuxPocitac;
import pocitac.SitoveRozhrani;
import vyjimky.ChybaKonfigurakuException;
import static Main.SAXHandler.*;

/**
 * Pomocna trida pro ukladani pocitacu pri nacitani z konfiguraku.
 * @author haldyr
 */
public class PocitacBuilder {

    String jmeno = "";
    String typ = "";
    List<String[]> rozhrani;
    List<String[]> routovaciTabulka;
    boolean ip_forward = false;
    List<String[]> pool;
    List<String[]> accessList;
    List<String[]> poolAccess;
    List<String[]> staticke;
    boolean bezNastaveni;
    boolean debug = false;
    
    public PocitacBuilder(boolean bezNastaveni) {
        rozhrani = new ArrayList<String[]>();
        routovaciTabulka = new ArrayList<String[]>();
        pool = new ArrayList<String[]>();
        accessList = new ArrayList<String[]>();
        poolAccess = new ArrayList<String[]>();
        staticke = new ArrayList<String[]>();
        this.bezNastaveni = bezNastaveni;
    }

    @Override
    public String toString() {
        String ret = "";
        ret += "PC:  " + jmeno + "\n";
        ret += "typ: " + typ + "\n";
        vypisPoleSeJmenovkou(rozhrani, "rozhrani");
        vypisPoleSeJmenovkou(routovaciTabulka, "routovaci tabulka");
        vypisPoleSeJmenovkou(pool, "pool");
        vypisPoleSeJmenovkou(poolAccess, "poolAccess");
        vypisPoleSeJmenovkou(accessList, "access-list");
        vypisPoleSeJmenovkou(staticke, "staticke");

        return ret;
    }

    /**
     * Pomocna metoda pro vypis nactenych veci pro vytvoreni pocitace.
     * @param seznam seznam retezcu urcenych pro vypis
     * @param jmeno, ktere identifikuje, co je to za seznam
     * @return
     */
    private String vypisPoleSeJmenovkou(List<String[]> seznam, String jmeno) {
        String s = "";
        int i;

        s += jmeno + ":\n";
        for (String[] pole : seznam) {
            i = 0;
            for (String nove : pole) {
                i++;
                if (i == 0) {
                    s += nove;
                } else {
                    s += "," + nove;
                }
            }
            s += "\n";
        }
        s += "\n";
        return s;
    }

    /**
     * Vrati vypis pole.
     * @param pole
     * @return
     */
    private String vypisPole(String[] pole) {
        String s = "[";
        for (int i = 0; i < pole.length; i++) {
            if (i == 0) {
                s += pole[i];
            } else {
                s += "," + pole[i];
            }
        }
        s += "]";
        return s;
    }

    public void nactiPooly(AbstraktniPocitac pocitac) {
        if (bezNastaveni) {
            return;
        }

        for (String[] pul : pool) {

            IpAdresa ip_start = null;
            IpAdresa ip_konec = null;

            try {
                ip_start = new IpAdresa(pul[dejIndexVNatPoolu("ip_start")]);
            } catch (Exception e) {
                if (debug) {
                    e.printStackTrace();
                }
                System.err.println("Prvni IP je spatna: " + pul[dejIndexVNatPoolu("ip_start")]);
            }

            try {
                ip_konec = new IpAdresa(pul[dejIndexVNatPoolu("ip_konec")]);
            } catch (Exception e) {
                if (debug) {
                    e.printStackTrace();
                }
                System.err.println("Druha IP je spatna: " + pul[dejIndexVNatPoolu("ip_konec")]);
            }

            String pJmeno = pul[dejIndexVNatPoolu("pJmeno")];
            String cislo = pul[dejIndexVNatPoolu("prefix")];

            int i = -1;
            try {
                i = Integer.parseInt(cislo);
            } catch (NumberFormatException e) {
                if (debug) {
                    e.printStackTrace();
                }
                System.err.println("Neni cislo: " + cislo);
            }

            try {
                int n = pocitac.natTabulka.lPool.pridejPool(ip_start, ip_konec, i, pJmeno);
                if (n != 0) {
                    System.err.println("Pool je spatne zadan: " + vypisPole(pul));
                }
            } catch (Exception e) {
                if (debug) {
                    e.printStackTrace();
                }
                System.err.println("Pool je spatne zadan: " + vypisPole(pul) + ", preskakuji.. ");
            }
        }
    }

    public void nactiAccessListy(AbstraktniPocitac pocitac) {
        if (bezNastaveni) {
            return;
        }

        for (String[] access : accessList) {
            try {
                String jm = access[dejIndexVNatAccessListu("cislo")];
                int cislo = Integer.parseInt(jm);

                IpAdresa ip = new IpAdresa(access[dejIndexVNatAccessListu("ipA")]);
                IpAdresa wccc = new IpAdresa(access[dejIndexVNatAccessListu("ipAWildcard")]);
                String maska = IpAdresa.vratMaskuZWildCard(wccc);
                ip.nastavMasku(maska);

                pocitac.natTabulka.lAccess.pridejAccessList(ip, cislo);
            } catch (Exception e) {
                if (debug) {
                    e.printStackTrace();
                }
                System.err.println("access-list je spatne zadan: " + vypisPole(access) + ", preskakuji.. ");
            }
        }
    }

    public void nactiPoolAccess(AbstraktniPocitac pocitac) {
        if (bezNastaveni) {
            return;
        }

        for (String[] poolAcc : poolAccess) {
            try {
                String acc = poolAcc[dejIndexVNatPrirazeni("accessCislo")];
                int cislo = Integer.parseInt(acc);
                String jm = poolAcc[dejIndexVNatPrirazeni("poolJmeno")];

                String overload = poolAcc[dejIndexVNatPrirazeni("overload")];

                boolean ovrld;
                if (overload.equals("true") || overload.equals("1")) {
                    ovrld = true;
                    pocitac.natTabulka.lPoolAccess.pridejPoolAccess(cislo, jm, ovrld);
                } else if (overload.equals("false") || overload.equals("0")) {
                    ovrld = false;
                    pocitac.natTabulka.lPoolAccess.pridejPoolAccess(cislo, jm, ovrld);
                } else {
                    System.err.println("prirazeni je spatne zadano: " + vypisPole(poolAcc) + ", preskakuji.. ");
                }
            } catch (Exception e) {
                if (debug) {
                    e.printStackTrace();
                }
                System.err.println("prirazeni je spatne zadano: " + vypisPole(poolAcc) + ", preskakuji.. ");
            }
        }
    }

    public void nactiStatickyNat(AbstraktniPocitac pocitac) {
        if (bezNastaveni) {
            return;
        }

        for (String[] stat : staticke) {
            if (!jePolePlne(stat)) {
                System.err.println("Staticky zaznam (in/out) pro NAT neni uplny: "+vypisPole(stat) + ", preskakuji..");
                return;
            }
            IpAdresa in;
            IpAdresa out;
            try {
                in = new IpAdresa(stat[0]);
            } catch (Exception e) {
                System.err.println("Staticky zaznam, element 'in' neni platnou IP adresou: "+stat[0]+ ", preskakuji..");
                return;
            }
            try {
                out = new IpAdresa(stat[1]);
            } catch (Exception e) {
                System.err.println("Staticky zaznam, element 'out' neni platnou IP adresou: "+stat[1]+ ", preskakuji..");
                return;
            }
            int n = pocitac.natTabulka.pridejStatickePravidloCisco(in, out);

            if ( n == 1 ) System.err.println("chyba, in adresa pro staticky zaznam: "+in.vypisAdresu()+" tam uz je, preskakuji..");
            if ( n == 2 ) System.err.println("chyba, out adresa pro staticky zaznam: "+out.vypisAdresu()+" tam uz je, preskakuji..");
        }
    }

    public void nactiRoutovaciTabulku(AbstraktniPocitac pocitac) {
        if (bezNastaveni) {
            return;
        }

        for (String[] mujzaznam : routovaciTabulka) { // tady resim routovaci tabulku

            IpAdresa adresat = new IpAdresa(mujzaznam[dejIndexVZaznamu("adresat")], mujzaznam[dejIndexVZaznamu("maskaAdresata")]);
            SitoveRozhrani iface = null;
            String jm = mujzaznam[dejIndexVZaznamu("rozhraniKam")];
            for (SitoveRozhrani sr : pocitac.rozhrani) {
                if (sr.jmeno.equals(jm)) {
                    iface = sr;
                }
            }
            if (pocitac instanceof LinuxPocitac) {
                if (iface == null) {
                    System.err.println("Nepodarilo se najit rozhrani s nazvem: " + jm
                            + ", Preskakuji zaznam " + adresat.vypisAdresuSMaskou() + " v routovaci tabulce..");
                    continue;
                }
            }

            if (pocitac instanceof CiscoPocitac) {
                if (!adresat.jeCislemSite()) {
                    throw new ChybaKonfigurakuException("Adresa " + adresat.vypisAdresuSMaskou() + " neni cislem site!");
                }
            }

            if (IpAdresa.jeZakazanaIpAdresa(adresat.vypisAdresu())) {
                System.err.println("IpAdresa " + adresat.vypisAdresuSMaskou() + " je ze zakazaneho rozsahu 224.* - 255.*, preskakuji..");
                continue;
            }

            if (mujzaznam[dejIndexVZaznamu("brana")].equals("")
                    || mujzaznam[dejIndexVZaznamu("brana")].equals("null")) { // kdyz to je na rozhrani

                if (pocitac instanceof CiscoPocitac) {
                    if (iface == null) {
                        System.err.println("Nepodarilo se najit rozhrani s nazvem " + jm
                                + ", Preskakuji zaznam " + adresat.vypisAdresuSMaskou() + " v routovaci tabulce..");
                        continue;
                    }
                    ((CiscoPocitac) pocitac).getWrapper().pridejZaznam(adresat, iface);
                } else {
                    pocitac.routovaciTabulka.pridejZaznamBezKontrol(adresat, null, iface);
                }

            } else { // vcetne brany
                IpAdresa brana = new IpAdresa(mujzaznam[dejIndexVZaznamu("brana")]);

                if (pocitac instanceof CiscoPocitac) {
                    if (IpAdresa.jeZakazanaIpAdresa(brana.vypisAdresu())) {
                        System.err.println("IpAdresa " + brana.vypisAdresuSMaskou() + " je ze zakazaneho rozsahu 224.* - 255.*, preskakuji..");
                        continue;
                    }
                    ((CiscoPocitac) pocitac).getWrapper().pridejZaznam(adresat, brana);
                } else {
                    pocitac.routovaciTabulka.pridejZaznamBezKontrol(adresat, brana, iface);
                }
            }
        }
    }

    public void nactiRozhrani(AbstraktniPocitac pocitac) {
        for (String[] iface : rozhrani) { // prochazim a pridavam rozhrani k PC

            String jmenoRozh = iface[dejIndexVRozhrani("jmeno")];
            if (jmenoRozh.length() == 0) {
                throw new ChybaKonfigurakuException("Rozhrani musi mit prirazene jmeno!");
            }

            SitoveRozhrani sr = new SitoveRozhrani(jmenoRozh, pocitac, iface[dejIndexVRozhrani("mac")]);

            // osetreni prazdne IP nebo masky
            // kdyz chybi maska, tak se dopocita v kontruktou IpAdresy, kdyz chybi IP, tak se maska neresi
            String maska = iface[dejIndexVRozhrani("maska")];
            String adresa = iface[dejIndexVRozhrani("ip")];
            String nahozene = iface[dejIndexVRozhrani("nahozene")];
            String nat = iface[dejIndexVRozhrani("nat")];

            if (maska.equals("") && !adresa.equals("")) { // chybi maska, ale IP je, pak se maska dopocita
                IpAdresa ip = new IpAdresa(adresa);
                sr.zmenPrvniAdresu(ip);
            } else if (!adresa.equals("") && !maska.equals("")) { // kdyz je tu oboje
                IpAdresa ip = new IpAdresa(adresa, maska);
                sr.zmenPrvniAdresu(ip);
            } else if (!maska.equals("") && adresa.equals("")) { // vypisem, ze preskakujem
                System.err.println("Preskakuji masku z duvodu nepritomnosti IP adresy.., maska: "+maska);
            }

            if (nahozene.equals("1") || nahozene.equals("true")) {
                sr.nastavRozhrani(true);
            } else if (nahozene.equals("0") || nahozene.equals("false")) {
                sr.nastavRozhrani(false);
            }

            pocitac.pridejRozhrani(sr);

            // nastaveni inside/outside rozhrani
            if (nat.equals("soukrome")) {
                pocitac.natTabulka.pridejRozhraniInside(sr);
            } else if (nat.equals("verejne")) {
                pocitac.natTabulka.nastavRozhraniOutside(sr);
            } else if (nat.equals("")) {
                //ok
            } else {
                System.out.println("Neznama volba " + nat + " byla preskocena. "
                        + "Povolene jsou jen soukrome/verejna");
            }
        }
    }
}
