package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author xuzhipeng
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int n;
    private int m;
    private double maxLoadFactor;
    private static final int DEFAULT_CAPACITY = 16;
    private static final double DEFAULT_MAX_LOAD = 0.75;
    private Set<K> keys;
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        this(DEFAULT_CAPACITY, DEFAULT_MAX_LOAD);
    }

    public MyHashMap(int initialSize) {
       this(initialSize, DEFAULT_MAX_LOAD);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        n = 0;
        m = initialSize;
        maxLoadFactor = maxLoad;
        keys = new HashSet<>();
        buckets = createTable(initialSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] buckets = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            buckets[i] = createBucket();
        }
        return buckets;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    @Override
    public void clear() {
        n = 0;
        keys.clear();
        buckets = createTable(m);
    }

    private boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(K key) {
        if (key == null) throw new IllegalArgumentException("calls containsKey with null key");
        int i = getBucketIndex(key);
        for (Node node : buckets[i]) {
            if (node.key.equals(key)) return true;
        }
        return false;
    }

    @Override
    public V get(K key) {
        if (key == null) throw new IllegalArgumentException("calls get with null key");
        int i = getBucketIndex(key);
        for (Node n : buckets[i]) {
            if (n.key.equals(key)) {
                return n.value;
            }
        }
        return null;
    }

    private int getBucketIndex(K key) {
        return getBucketIndex(key, m);
    }

    private int getBucketIndex(K key, int capacity) {
        return (key.hashCode()  & 0x7FFFFFFF) % capacity;
    }

    @Override
    public void put(K key, V value) {
        if (key == null) throw new IllegalArgumentException("calls put with null key");
        if (value == null) remove(key);
        else {
            if (isOverLoad()) resize(2 * m);
            int i = getBucketIndex(key);
            if (!containsKey(key)) {
                n++;
                buckets[i].add(createNode(key, value));
                keys.add(key);
            }
            else {
                for (Node n : buckets[i]) {
                    if (n.key.equals(key)) {
                        n.value = value;
                        break;
                    }
                }
            }
        }
    }

    private boolean isOverLoad() {
        return (double) (n + 1) / m > maxLoadFactor;
    }

    private void resize(int newCapacity) {
        Collection<Node>[] newBuckets = createTable(newCapacity);
        for (Collection<Node> bucket : buckets) {
            for (Node node : bucket) {
                int i = getBucketIndex(node.key, newCapacity);
                newBuckets[i].add(node);
            }
        }
        buckets = newBuckets;
        m = newCapacity;
    }

    @Override
    public int size() {
        return n;
    }

    @Override
    public Set<K> keySet() {
        return keys;
    }

    @Override
    public V remove(K key) {
        if (key == null) throw new IllegalArgumentException("calls remove with null key");
        return remove(key, get(key));
    }

    @Override
    public V remove(K key, V value) {
        if (key == null) throw new IllegalArgumentException("calls remove with null key");
        int i = getBucketIndex(key);
        Iterator<Node> it = buckets[i].iterator();
        while (it.hasNext()) {
            Node node = it.next();
            if (node.key.equals(key) && Objects.equals(node.value, value)) {
                it.remove();
                keys.remove(key);
                n--;
                return node.value;
            }
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }

}
