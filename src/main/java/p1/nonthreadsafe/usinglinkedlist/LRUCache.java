package p1.nonthreadsafe.usinglinkedlist;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/*
We are not using Node class here and using built in linked list
cons:-
 Cannot store store additional information about the cached items, such as the time they were last accessed.
 This information can be used to implement the LRU eviction policy, which removes the least recently used items from the cache when it is full.
A Java inbuilt linked list does not provide this level of flexibility.
*/
public class LRUCache {

    private Map<Integer, Integer> cache;
    private LinkedList<Integer> queue;
    private int capacity;

    public LRUCache(int capacity) {
        this.cache = new HashMap<>();
        this.queue = new LinkedList<>();
        this.capacity = capacity;
    }

    public int get(int key) {
        if (!cache.containsKey(key)) {
            return -1;
        }

        // Move the key to the front of the queue
        queue.remove(key);
        queue.addFirst(key);

        return cache.get(key);
    }

    public void put(int key, int value) {
        if (cache.containsKey(key)) {
            // Update the value of the existing key
            cache.put(key, value);

            // Move the key to the front of the queue
            queue.remove(key);
            queue.addFirst(key);
        } else {
            // If the cache is full, remove the least recently used key
            if (queue.size() == capacity) {
                int leastRecentlyUsedKey = queue.removeLast();
                cache.remove(leastRecentlyUsedKey);
            }

            // Add the new key to the cache and the queue
            cache.put(key, value);
            queue.addFirst(key);
        }
    }
}