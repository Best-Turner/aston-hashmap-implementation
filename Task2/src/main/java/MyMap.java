import java.util.Collection;
import java.util.Set;

public interface MyMap<K, V> {

    void put(K key, V value);

    V get(K key);

    boolean remove(K key);

    boolean containsKey(Object key);

    boolean containsValue(Object value);

    boolean isEmpty();

    Set<K> keySet();

    Collection<V> values();

    Set<Entry<K, V>> entrySet();


    void clear();

    int size();

    interface Entry<K, V> {
        K getKey();

        V getValue();

        Entry<K, V> next();
    }
}
