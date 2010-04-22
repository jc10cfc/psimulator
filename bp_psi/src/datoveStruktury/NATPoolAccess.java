/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package datoveStruktury;

import datoveStruktury.NATPool.Pool;
import java.util.ArrayList;
import java.util.List;

/**
 * Datova struktura pro seznam PoolAccess 
 * (cisco prikaz: 'ip nat inside source list 'cisloAccessListu' pool 'jmenoPoolu' overload?' )
 * @author haldyr
 */
public class NATPoolAccess {

    NATtabulka natTabulka;
    public List<PoolAccess> seznamPoolAccess;

    public NATPoolAccess(NATtabulka NATtabulka) {
        this.natTabulka = NATtabulka;
        this.seznamPoolAccess = new ArrayList<PoolAccess>();
    }

    /**
     * Prida novy poolAccess na spravnou pozici.
     * @param access
     * @param pool
     */
    public void pridejPoolAccess(int access, String pool, boolean overload) {
        smazPoolAccess(access);

        PoolAccess novy = new PoolAccess(access, pool, overload);
        int index = 0;
        for (PoolAccess pa : seznamPoolAccess) {
            if (novy.access < pa.access) {
                break;
            }
            index++;
        }
        seznamPoolAccess.add(index, novy);
    }

    /**
     * Zkusi smazat PoolAccess.
     * @param access, identifikator PoolAccessu.
     * @return 0 - ok, smazalo to. <br />
     *         1 - takovy zaznam neni (%Dynamic mapping not found)
     */
    public int smazPoolAccess(int access) {
        PoolAccess smazat = null;
        for (PoolAccess pa : seznamPoolAccess) {
            if (pa.access == access) {
                smazat = pa;
            }
        }
        if (smazat == null) {
            return 1;
        }
        
        seznamPoolAccess.remove(smazat);
        return 0;
    }

    /**
     * Smaze vsechny PoolAccessy.
     */
    public void smazPoolAccessVsechny() {
        seznamPoolAccess.clear();
    }

    /**
     * Vrati prirazeny poolAccess nebo null, kdyz nic nenajde.
     * @param pool
     * @return
     */
    public PoolAccess vratPoolAccess(Pool pool) {
        for (PoolAccess pa : seznamPoolAccess) {
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
