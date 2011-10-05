package datoveStruktury;

import datoveStruktury.NATPool.Pool;
import java.util.ArrayList;
import java.util.List;

/**
 * Datova struktura pro seznam PoolAccess. Jednoznacny identifikator je cislo.
 * (cisco prikaz: "ip nat inside source list 'cisloAccessListu' pool 'jmenoPoolu' overload?" )
 * @author Stanislav Řehák
 */
public class NATPoolAccess {

    NATtabulka natTabulka;
    public List<PoolAccess> seznam;

    public NATPoolAccess(NATtabulka NATtabulka) {
        this.natTabulka = NATtabulka;
        this.seznam = new ArrayList<PoolAccess>();
    }

    /**
     * Prida novy poolAccess na spravnou pozici. Kdyz najde PoolAccess se stejnym jmenem,
     * tak ho bez milosti premazne.
     * @param access
     * @param pool
     */
    public void pridejPoolAccess(int access, String pool, boolean overload) {
        smazPoolAccess(access);

        PoolAccess novy = new PoolAccess(access, pool, overload);
        int index = 0;
        for (PoolAccess pa : seznam) {
            if (novy.access < pa.access) {
                break;
            }
            index++;
        }
        seznam.add(index, novy);
    }

    /**
     * Zkusi smazat PoolAccess.
     * @param access, identifikator PoolAccessu.
     * @return 0 - ok, smazalo to. <br />
     *         1 - takovy zaznam neni (%Dynamic mapping not found)
     */
    public int smazPoolAccess(int access) {
        PoolAccess smazat = null;
        for (PoolAccess pa : seznam) {
            if (pa.access == access) {
                smazat = pa;
            }
        }
        if (smazat == null) {
            return 1;
        }
        
        seznam.remove(smazat);
        return 0;
    }

    /**
     * Smaze vsechny PoolAccessy.
     */
    public void smazPoolAccessVsechny() {
        seznam.clear();
    }

    /**
     * Vrati prirazeny poolAccess nebo null, kdyz nic nenajde.
     * @param pool
     * @return
     */
    public PoolAccess vratPoolAccess(Pool pool) {
        for (PoolAccess pa : seznam) {
            if (pa.pool.equals(pool.jmeno)) {
                return pa;
            }
        }
        return null;
    }

    public class PoolAccess {

        /**
         * Cislo 1-2699.
         */
        public int access;
        /**
         * Unikatni jmeno poolu.
         */
        public String pool;
        public boolean overload;

        public PoolAccess(int access, String pool, boolean overload) {
            this.access = access;
            this.pool = pool;
            this.overload = overload;
        }
    }


}
