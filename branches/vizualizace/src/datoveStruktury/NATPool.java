/*
 * 
 */

package datoveStruktury;

import datoveStruktury.NATAccessList.AccessList;
import datoveStruktury.NATPoolAccess.PoolAccess;
import java.util.ArrayList;
import java.util.List;

/**
 * Datova struktura pro seznam poolu IP adres. <br />
 * Kazdy pool obsahuje jmeno a seznam IpAdres. <br />
 * (cisco prikaz: "ip nat pool 'jmenoPoolu' 'ip_start' 'ip_konec' prefix 'cislo'" )
 * @author Stanislav Řehák
 */
public class NATPool {
    
    public List <Pool> seznam;
    NATtabulka natTabulka;


    public NATPool(NATtabulka tab) {
        this.natTabulka = tab;
        seznam = new ArrayList<Pool>();
    }

    /**
     * Prida pool.
     * @param start
     * @param konec
     * @param prefix
     * @param jmeno, neni null ani ""
     * @return 0 - ok prida pool <br />
     *         1 - kdyz je prvni IP vetsi nez druha IP (%End address less than start address) <br />
     *         2 - pool s timto jmenem je prave pouzivan, tak nic. (%Pool ovrld in use, cannot redefine) <br />
     *         3 - kdyz je spatna maska (% Invalid input detected) <br />
     *         4 - kdyz je start a konec v jine siti (%Start and end addresses on different subnets)
     */
    public int pridejPool(IpAdresa start, IpAdresa konec, int prefix, String jmeno) {
        if (start.dejLongIP() > konec.dejLongIP()) {
            return 1;
        }

        if (prefix > 32 || prefix < 1) {
            return 3;
        }

        start.nastavMasku(prefix);

        if (!konec.jeVRozsahu(start)) {
            return 4;
        }

        // smaznout stejne se jmenujici pool + kontrola jestli vubec ho muzem prespat
        if (smazPool(jmeno) == 2) {
            return 2;
        }

        // tady pridej pool
        Pool novyPool = new Pool();
        novyPool.jmeno = jmeno;

        // pridavam IP adresy do poolu.
        IpAdresa ukazatel = start.vratKopii();
        do {
            novyPool.pool.add(ukazatel);
            ukazatel = IpAdresa.vratOJednaVetsi(ukazatel);
            ukazatel.nastavMasku(prefix);

        } while (ukazatel.dejLongIP() <= konec.dejLongIP() && ukazatel.jeVRozsahu(start));
        novyPool.ukazatel = novyPool.prvni();

        seznam.add(novyPool);
        updateIpNaRozhrani();
        return 0;
    }

    /**
     * Smaze pool podle jmena.
     * Dale maze stare dynamicke zaznamy.
     * @param jmeno
     * @return 0 - ok smazal se takovy pool <br />
     *         1 - pool s takovym jmenem neni. (%Pool jmeno not found) <br />
     *         2 - %Pool ovrld in use, cannot redefine
     */
    public int smazPool(String jmeno) {

        natTabulka.smazStareDynamickeZaznamy();
        if(poolInUse(jmeno)) {
            return 2;
        }

        Pool smaznout = null;
        for (Pool pool : seznam) {
            if (pool.jmeno.equals(jmeno)) {
                smaznout = pool;
            }
        }
        if (smaznout == null) {
            return 1;
        }

        seznam.remove(smaznout);
        updateIpNaRozhrani();
        return 0;
    }

    /**
     * Smaze vsechny pooly.
     */
    public void smazPoolVsechny() {
        seznam.clear();
        updateIpNaRozhrani();
    }

    /**
     * Vrati pro overload prvni IP z poolu, jinak dalsi volnou IP. Pri testovani vrati null,
     * kdyz uz neni volna IP v poolu. To je ale osetreno metodou mamNAtovat(), tak uz pri
     * samotnem natovani by to null nikdy vracet nemelo.
     * @return
     */
    public IpAdresa dejIpZPoolu(Pool pool) {
        if (vratPoolAccess(pool).overload) {
            return pool.prvni();
        } else {
            return pool.dejIp(false);
        }
    }

    /**
     * Vrati vsechny pooly, ktere obsahuji danou adresu.
     * @param adr
     * @return seznam poolu - kdyz to najde alespon 1 pool <br />
     *         null - kdyz to zadny pool nenajde
     */
    public List<Pool> vratPoolProIp(IpAdresa adr) {
        List<Pool> pseznam = new ArrayList<Pool>();
        for (Pool pool : seznam) {
            if (pool.prvni() == null) continue;
            if (adr.jeVRozsahu(pool.prvni())) {
                pseznam.add(pool);
            }
        }
        if (pseznam.size() == 0) return null;
        return pseznam;
    }

    /**
     * Vrati prirazeny poolAccess nebo null, kdyz nic nenajde.
     * @param pool
     * @return
     */
    public PoolAccess vratPoolAccess(Pool pool) {
        for (PoolAccess pa : natTabulka.lPoolAccess.seznam) {
            if (pa.pool.equals(pool.jmeno)) {
                return pa;
            }
        }
        return null;
    }

    /**
     * Vrati prirazeny poolAccess nebo null, kdyz nic nenajde.
     * @param pool
     * @return
     */
    public PoolAccess vratPoolAccessZAccessListu(AccessList acc) {
        for (PoolAccess pa : natTabulka.lPoolAccess.seznam) {
            if (acc.cislo == pa.access) {
                return pa;
            }
        }
        return null;
    }

    /**
     * Vrati pool, ktery je navazan na access-list.
     * @param access_list
     * @return pool - ktery je navazan na access-list <br />
     *         null - kdyz neni PoolAccess s timto cislem a nebo neni Pool s nazvem u nalezeneho PoolAccessu.
     */
    public Pool vratPoolZAccessListu(AccessList access) {
        for (PoolAccess pa : natTabulka.lPoolAccess.seznam) {
            if (pa.access == access.cislo) {
                for (Pool pool : seznam) {
                    if (pool.jmeno.equals(pa.pool)) {
                        return pool;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Nasype vsechny adresy ze vsech poolu na verejne rozhrani.
     * Prida IP z dynamickych i statickych pravidel.
     */
    public void updateIpNaRozhrani() {
        if (natTabulka.verejne == null) {
            return ;
        }
        natTabulka.verejne.smazVsechnyIpKromPrvni();
        for (Pool pool : seznam) {
            for (IpAdresa adr : pool.pool) {
                natTabulka.verejne.seznamAdres.add(adr);
            }
        }

        // jeste staticky
        natTabulka.pridejIpAdresyZeStatickychPravidel(natTabulka.verejne);
    }

    /**
     * Vrati true, pokud se najde nejaky zaznam od toho poolu.
     * @param jmeno
     * @return
     */
    private boolean poolInUse(String jmeno) {
        for (NATtabulka.NATzaznam zaznam : natTabulka.tabulka) {
            if (zaznam.staticke == false) {
                List<Pool> pseznam = vratPoolProIp(zaznam.out);
                if (pseznam == null) continue;
                for (Pool pool : pseznam) {
                    if (pool.jmeno.equals(jmeno)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public class Pool {

        /**
         * Jmeno poolu
         */
        public String jmeno = "";
        /**
         * Prirazeny seznam adres.
         */
        List<IpAdresa> pool;
        /**
         * Ukazuje na dalsi volnou IpAdresu z poolu nebo null, kdyz uz neni volna.
         */
        IpAdresa ukazatel = null;

        public Pool() {
            pool = new ArrayList<IpAdresa>(){

                @Override
                public boolean add(IpAdresa ip) {
                    if (pool.size()==0) {
                        ukazatel = ip;
                    }
                    return super.add(ip);
                }

            };
        }

        /**
         * Vrati prvni IpAdresu z poolu nebo null, kdyz je pool prazdny.
         * @return
         */
        public IpAdresa prvni() {
            if (pool.size() == 0) {
                return null;
            }
            return pool.get(0);
        }

        /**
         * Vrati dalsi IpAdresu z poolu. Kdyz uz jsem na posledni, tak vracim null (DHU).
         * @return
         */
        private IpAdresa dalsi() {
            int n = -1;
            for (IpAdresa ip : pool) {
                n++;
                if (ip.jeStejnaAdresa(ukazatel)) {
                    break; // n = index ukazatele
                }
            }
            if (n + 1 == pool.size()) {
                return null;
            }
            return pool.get(n + 1);
        }

        /**
         * Vrati dalsi IP z poolu nebo null, pokud uz neni dalsi IP.
         * @param testovani true, kdyz se zjistuje, zda je jeste IP, nemenim pak ukazatel na volnou IP
         * @return
         */
        public IpAdresa dejIp(boolean testovani) {
            IpAdresa vrat = ukazatel;
            if (testovani == false) {
                ukazatel = dalsi();
            }
            return vrat;
        }

        /**
         * Vrati posledni Ip
         * @return
         */
        public IpAdresa posledni() {
            if (pool.size() <= 1) {
                return prvni();
            }
            return pool.get(pool.size()-1);
        }
    }
}
