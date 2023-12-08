package p1.threadsafe;

/*
To make the provided LRUCache implementation thread-safe, we will use:

ReentrantLock for Explicit Synchronization.
- This lock provides a way to control access to the critical sections of code where concurrent modifications might occur.
- The lock ensures that only one thread can access the synchronized methods at a time, preventing race conditions and ensuring thread safety.
Using ConcurrentHashMap for Concurrent Access.
- Instead of a regular HashMap, a ConcurrentHashMap is used as the underlying data structure for cache storage.
- ConcurrentHashMap provides thread safety for operations like put and get without the need for external synchronization. It allows multiple threads to R/W concurrently without explicit synchronization, ensuring thread safety and high concurrency.
 */

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Node {
    int key;
    int value;
    long expiryTime;
    int priority;
    Node prev;
    Node next;

    public Node(int key, int value, long expiryTime, int priority) {
        this.key = key;
        this.value = value;
        this.expiryTime = expiryTime;
        this.priority = priority;
    }
}
public class LRUCacheThreadSafe {
    private ConcurrentHashMap<Integer, Node> cache;
    private Node head;
    private Node tail;
    private int capacity;
    private final Lock lock = new ReentrantLock();
    public LRUCacheThreadSafe(int capacity) {
        this.cache = new ConcurrentHashMap<>();
        this.head = new Node(0, 0, 0, 0);
        this.tail = new Node(0, 0, 0, 0);
        this.head.next = tail;
        this.tail.prev = head;
        this.capacity = capacity;
    }
    public void put(int key, int value, long expiryTime, int priority) { // Average O(1) time complexity (amortized), Worst-case time complexity: O(N) when eviction is triggered. O(capacity) space complexity.
        lock.lock();
        try {
            if (cache.containsKey(key)) {
                // Update the existing node
                Node existingNode = cache.get(key);
                existingNode.value = value;
                existingNode.expiryTime = expiryTime;
                existingNode.priority = priority;
                // Move the node to the front (most recently used)
                moveToHead(existingNode);
            } else {
                // Create a new node
                Node newNode = new Node(key, value, expiryTime, priority);
                // Add the new node to the cache and the front of the linked list
                cache.put(key, newNode);
                addToFront(newNode);
                // Check if the cache size exceeds the capacity, trigger eviction logic
                if (cache.size() > capacity) {
                    evict();
                }
            }
        } finally {
            lock.unlock();
        }
    }
    public int get(int key) {  //Time complexity: O(1), O(capacity) space complexity.
        lock.lock();
        try {
            if (cache.containsKey(key)) {
                Node node = cache.get(key);
                // Move the accessed node to the front
                moveToHead(node);
                return node.value;
            }
            // If the key is not present in the cache, return -1
            return -1;
        } finally {
            lock.unlock();
        }
    }
    private void moveToHead(Node node) {
        // Remove the node from its current position
        removeNode(node);
        // Add the node to the front of the linked list
        addToFront(node);
    }

    private void addToFront(Node node) {
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
        node.prev = head;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void evict() { //Time complexity: O(N)
        // Remove expired items
        removeExpiredItems();
        // If no items have expired, remove the least priority item
        if (cache.size() > capacity) {
            removeLeastPriorityItem();
        }
    }

    private void removeExpiredItems() {
        long currentTime = System.currentTimeMillis();
        Node current = tail.prev;
        while (current != head) {
            if (current.expiryTime <= currentTime) {
                Node toRemove = current;
                current = current.prev;
                removeNode(toRemove);
                cache.remove(toRemove.key);
            } else {
                // As the nodes are ordered by expiry time, we can stop checking further nodes.
                break;
            }
        }
    }

    private void removeLeastPriorityItem() {
        Node current = tail.prev;
        Node leastPriorityNode = current;
        while (current != head) {
            if (current.priority < leastPriorityNode.priority) {
                leastPriorityNode = current;
            }
            current = current.prev;
        }
        // Remove the least priority node
        removeNode(leastPriorityNode);
        cache.remove(leastPriorityNode.key);
    }

}
