package mt.fireworks.timecache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.set.primitive.MutableLongSet;
import org.eclipse.collections.impl.factory.primitive.LongLists;

import lombok.AllArgsConstructor;
import mt.fireworks.timecache.index.Index;
import mt.fireworks.timecache.storage.StorageLongKey;

@AllArgsConstructor
public class ByteCacheImpl<T> implements Cache<T, byte[], byte[]>{

//    SerDes<T, byte[]> serdes;
    StorageLongKey storage;
    Index<T>[] indexes;
    SerDes2<T> serdes2;

    @Override
    public boolean add(T val) {
        long tstamp = serdes2.timestampOfT(val);
        byte[] data = serdes2.marshall(val);

        long storageIdx = storage.addEntry(tstamp, data);
        if (storageIdx == 0) {
            return false;
        }

        for (Index<T> i: indexes) {
            i.put(val, storageIdx);
        }

        // TODO provjeri da li već postoji ova vrijednost u kešu
        //			za svaki index dohvati listu s istim timestmapom
        //          dohvati sve vrijednosti s istim tstampom
        //          provrti equalsD

        return true;
    }


    @Override
    public Object[] getArray(T val) {
        Object[] result = new Object[indexes.length];
        MutableLongList keysForRemoval = LongLists.mutable.empty();

        for (int idx = 0; idx < indexes.length; idx++) {
            Index<T> index = indexes[idx];
            MutableLongSet strKeys = index.get(val);
            ArrayList<T> ts = new ArrayList<>(strKeys.size());
            result[idx] = ts;

            /*
            strKeys.forEach(strKey -> {
                byte[] data = storage.getEntry(strKey);
                if (data == null) {
                    keysForRemoval.add(strKey);
                    return;
                }

                Function<byte[], T> unmarshaller = serdes.getUnmarshaller();
                T t = unmarshaller.apply(data);
                ts.add(t);
            });
            */

            strKeys.forEach(strKey -> {
                T res = storage.getEntry2(strKey, serdes2);
                if (res != null) ts.add(res);
            });


            if (keysForRemoval != null && keysForRemoval.size() > 0) {
                strKeys.removeAll(keysForRemoval);
                keysForRemoval.clear();
            }
        }

        return result;
    }

    @Override
    public Map<byte[], Collection<T>> getMap(T val) {
        // TODO Auto-generated method stub
        return null;
    }

}
