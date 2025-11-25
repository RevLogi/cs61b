package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    private static final int INIT_CAPACITY = 4;
    private static double LOAD_FACTOR = 0.75;


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
    private int n; //items size
    private int m; //buckets size

    /** Constructors */
    public MyHashMap() {
        this(INIT_CAPACITY);
    }

    public MyHashMap(int initialSize) {
        this.m = initialSize;
        this.n = 0;
        this.buckets = createTable(this.m);
        for (int i = 0; i < this.m; i++) {
            this.buckets[i] = createBucket();
        }
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this(initialSize);
        LOAD_FACTOR = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return null;
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
        return (Collection<Node>[]) new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below
    @Override
    public void clear() {
        for (int i = 0; i < this.m; i++) {
            this.buckets[i] = createBucket();
        }
        this.n = 0;
    }

    private int hash(K key) {
        return (key.hashCode() & 0x7fffffff) % m;
    }

    private void resize(int tableSize) {
        MyHashMap<K, V> temp = new MyHashMap<>(tableSize);
        for (int i = 0; i < m; i ++) {
            for (Node node : buckets[i]) {
                temp.put(node.key, node.value);
            }
        }
        this.m = temp.m;
        this.n = temp.n;
        this.buckets = temp.buckets;
    }
    @Override
    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        int i = hash(key);
        for (Node node : buckets[i]) {
            if (node.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        int i = hash(key);
        for (Node node : buckets[i]) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return n;
    }

    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        Node node = new Node(key, value);
        if (n >= LOAD_FACTOR*m) {
            resize(2*m);
        }
        int i = hash(key);
        for (Node curr : buckets[i]) {
            if (curr.key.equals(key)) {
                curr.value = value;
                return;
            }
        }
        buckets[i].add(node);
        n += 1;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        for (int i = 0; i < m; i++) {
            for (Node node : buckets[i]) {
                keys.add(node.key);
            }
        }
        return keys;
    }

    @Override
    public V remove(K key) {
        int i = hash(key);
        for (Node node : buckets[i]) {
            if (node.key.equals(key)) {
                V returnValue = node.value;
                buckets[i].remove(node);
                return returnValue;
            }
        }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        int i = hash(key);
        for (Node node : buckets[i]) {
            if (node.key.equals(key) && node.value.equals(value)) {
                buckets[i].remove(node);
                return value;
            }
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return new HashIterator();
    }

    private class HashIterator implements Iterator<K> {
        private final Iterator<K> keys;

        public HashIterator() {
            keys = keySet().iterator();
        }

        @Override
        public boolean hasNext() {
            return keys.hasNext();
        }

        @Override
        public K next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return keys.next();
        }
    }
}
