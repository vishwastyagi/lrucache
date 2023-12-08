package p1.nonthreadsafe.usingnodeclass;

import java.util.HashMap;

/*
Design an LRU (Least Recently Used) cache with the specified eviction policy.

Eviction policy rules order:

Remove data items that have exceeded their TTL.
If no items have expired, remove the least priority item.
If multiple items share the same lowest priority, remove the least recently used item among them.

Using Node class has advantage, it allows for more flexibility and control over the cache. A Node class can be customized to store additional information about the cached items, such as the time they were last accessed. This information can be used to implement the LRU eviction policy, which removes the least recently used items from the cache when it is full.
 The Node class gives you more control over the cache. You can choose how the items are stored and accessed.
 */
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

public class LRUCache {
    private HashMap<Integer, Node> cache;
    private Node head;
    private Node tail;
    private int capacity;

    public LRUCache(int capacity) {
        this.cache = new HashMap<>();
        this.head = new Node(0, 0, 0, 0);
        this.tail = new Node(0, 0, 0, 0);
        this.head.next = tail;
        this.tail.prev = head;
        this.capacity = capacity;
    }

    public void put(int key, int value, long expiryTime, int priority) { // Average O(1) time complexity (amortized), Worst-case time complexity: O(N) when eviction is triggered. O(capacity) space complexity.
        if (cache.containsKey(key)) {
            // Update the existing node
            Node existingNode = cache.get(key);
            existingNode.value = value;
            existingNode.expiryTime = expiryTime;
            existingNode.priority = priority;
            moveToHead(existingNode);
        } else {
            Node newNode = new Node(key, value, expiryTime, priority);
            cache.put(key, newNode);
            addToFront(newNode);
        }
        if (cache.size() > capacity) {
            evict();
        }
    }

    public int get(int key) { //Time complexity: O(1), O(capacity) space complexity.
        if (cache.containsKey(key)) {
            Node node = cache.get(key);
            moveToHead(node);
            return node.value;
        }
        return -1;
    }

    private void evict() { //Time complexity: O(N)
        removeExpiredItems();
        if (cache.size() > capacity) {
            removeLeastPriorityItem();
        }
    }

    private void addToFront(Node node) {
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
        node.prev = head;
    }

    private void moveToHead(Node node) {
        removeNode(node);
        addToFront(node);

    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
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
}
