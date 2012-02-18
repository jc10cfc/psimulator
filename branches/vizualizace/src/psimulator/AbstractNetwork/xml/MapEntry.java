package psimulator.AbstractNetwork.xml;

/**
 *
 * @param <K> 
 * @param <V> 
 * @author Martin Lukáš <lukasma1@fit.cvut.cz>
 */
public class MapEntry<K,V> {

    private V value;
    private K key;

    public MapEntry() {
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    
    
}
