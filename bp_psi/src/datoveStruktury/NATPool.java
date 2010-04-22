/*
 * TODO: zjistit, kdy to posila %Pool ovrld in use, cannot redefine
 */

package datoveStruktury;

import datoveStruktury.NATPoolAccess.PoolAccess;
import java.util.ArrayList;
import java.util.List;

/**
 * Datova struktura pro seznam poolu IP adres. <br />
 * Kazdy pool obsahuje jmeno a seznam IpAdres.
 * @author haldyr
 */
public class NATPool {
    
    public List <Pool> seznamPoolu;
    NATtabulka NATtabulka;
    NATPoolAccess seznamPoolAccess;


    public NATPool(NATtabulka tab) {
        this.NATtabulka = tab;
        seznamPoolu = new ArrayList<Pool>();
        seznamPoolAccess = tab.NATseznamPoolAccess;
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

        //TODO: zjistit, kdy to posila %Pool ovrld in use, cannot redefine
//        if (aktivniPool.jmeno.equals(jmeno)) {
//            return 2;
//        }

//        for (Pool pool : seznamPoolu) {
//            if (pool.jmeno.)
//        }

        if (prefix > 32 || prefix < 1) {
            return 3;
        }

        start.nastavMasku(prefix);

        if (!konec.jeVRozsahu(start)) {
            return 4;
        }

        // smaznout stejne se jmenujici pool
        smazPool(jmeno);

        // tady pridej pool
        Pool novyPool = new Pool();
        novyPool.jmeno = jmeno;

        // pridavam IP adresy do poolu.
        IpAdresa ukazatel = start;
        do {
            novyPool.pool.add(ukazatel);
            ukazatel = IpAdresa.vratOJednaVetsi(ukazatel);
            ukazatel.nastavMasku(prefix);

        } while (ukazatel.dejLongIP() < konec.dejLongIP() && ukazatel.jeVRozsahu(start));
        novyPool.ukazatel = novyPool.prvni();

        seznamPoolu.add(novyPool);
        return 0;
    }

    /**
     * Smaze pool podle jmena.
     * @param jmeno
     * @return 0 - ok smazal se takovy pool <br />
     *         1 - pool s takovym jmenem neni. (%Pool jmeno not found)
     */
    public int smazPool(String jmeno) {
        Pool smaznout = null;
        for (Pool pool : seznamPoolu) {
            if (pool.jmeno.equals(jmeno)) {
                smaznout = pool;
            }
        }
        if (smaznout == null) {
            return 1;
        }
        seznamPoolu.remove(smaznout);
        return 0;
    }

    /**
     * Smaze vsechny pooly.
     */
    public void smazPoolVsechny() {
        seznamPoolu.clear();
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
     * Vrati prirazeny poolAccess nebo null, kdyz nic nenajde.
     * @param pool
     * @return
     */
    public PoolAccess vratPoolAccess(Pool pool) {
        for (PoolAccess pa : seznamPoolAccess.seznamPoolAccess) {
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
    public PoolAccess vratPoolAccessZAccessListu(NATAccessList.AccessList acc) {
        for (PoolAccess pa : seznamPoolAccess.seznamPoolAccess) {
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
    public Pool vratPoolZAccessListu(NATAccessList.AccessList access) {
        for (PoolAccess pa : seznamPoolAccess.seznamPoolAccess) {
            if (pa.access == access.cislo) {
                for (Pool pool : seznamPoolu) {
                    if (pool.jmeno.equals(pa.pool)) {
                        return pool;
                    }
                }
            }
        }
        return null;
    }    

    public class Pool {

        public String jmeno = "";
        List<IpAdresa> pool;
        /**
         * Ukazuje na volnou IpAdresu z poolu.
         */
        IpAdresa ukazatel = null;

        public Pool() {
            pool = new ArrayList<IpAdresa>();
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
            if (pool.size() == 1) {
                return prvni();
            }
            return pool.get(pool.size()-1);
        }
    }

    /**
     * Zjistuje, zda dosli IP v poolu. Pri overloadu to nenastane nikdy (vrati false)
     * @param ip
     * @return true - IP v poolu jsou vycerpany | neni prirazen access-list | neni prirazen PoolAccess <br />
     *         false - jeste tam jsou volne IP
     */
    @Deprecated
    public boolean doslyIpPoolu(IpAdresa ip) {

        NATAccessList.AccessList acc = NATtabulka.NATseznamAccess.vratAccessListIP(ip);
        if (acc == null) {
            return true;
        }

        PoolAccess pa2 = vratPoolAccessZAccessListu(acc);
        if (pa2.overload) {
            return false;
        }

        Pool pool = vratPoolZAccessListu(acc);
        if (pool == null) {
            return true;
        }

        if (pool.dejIp(true) == null) {
            return true;
        }

        return false;
    }

}