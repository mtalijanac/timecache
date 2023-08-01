package mt.fireworks.timecache;

import java.util.Collection;
import java.util.Map;

/**
 *
 * @param <T> keširan objekt
 * @param <K> ključ, npr byte array
 * @param <S> serijalizirani tip, npr. String ili byte array
 */
public interface Cache<T, K, S> {

    boolean add(T val);

    /** @return [key, ArrayList<T>, key, ArrayList<T>] */
    Object[] getArray(T val);


    Map<K, Collection<T>> getMap(T val);


    void tick();

}
