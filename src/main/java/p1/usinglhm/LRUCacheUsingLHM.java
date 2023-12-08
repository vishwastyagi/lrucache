package p1.usinglhm;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/*
LinkedHashMap maintains a doubly linked list of entries internally which is by default
in insertion order but it has a constructor which allows you to specify that the
list be maintained in access order.
 */
public class LRUCacheUsingLHM {

    private Map<Integer, Integer> cache;
    //private int capacity;

    public LRUCacheUsingLHM(int capacity) {
       // this.capacity = capacity;
        this.cache = new LinkedHashMap<>(capacity,0.75f,true){
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer,Integer> eldest){
                return size()>capacity;
            }
        };

    }

    public int get(int key) {
        return cache.getOrDefault(key,-1);
    }

    public void put(int key, int value) {
        cache.put(key,value);
    }

    @Override
    public String toString() {
        return "LRUCacheUsingLHM{" +
                "cache=" + cache +
                '}';
    }
}
class Test{
    public static void main(String arg[]){

        LRUCacheUsingLHM lruCache = new LRUCacheUsingLHM(2);

        lruCache.put(1, 100);

        lruCache.put(2, 200);

        lruCache.put(3, 300);

        System.out.println(lruCache);

    }
}