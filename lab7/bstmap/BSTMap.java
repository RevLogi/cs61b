package bstmap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>,V> implements Map61B<K,V> {
    private BSTNode root;
    private V removedValue = null;

    private class BSTNode{
        private K key;
        private V value;
        private BSTNode left, right;
        private int size;

        public BSTNode(K key, V value, int size) {
            this.key = key;
            this.value = value;
            this.size = size;
        }
    }

    public BSTMap() {
    }

    @Override
    public void clear() {
        root = null;
    }

    @Override
    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        return find(root, key) != null;
    }

    @Override
    public V get(K key) {
        return get(root, key);
    }

    private BSTNode find(BSTNode n, K key) {
        if (n == null) {
            return null;
        }
        if (key == null) {
            throw new IllegalArgumentException();
        }
        int cmp = key.compareTo(n.key);
        if (cmp < 0) {
            return find(n.left, key);
        } else if (cmp == 0) {
            return n;
        } else {
            return find(n.right, key);
        }
    }

    private V get(BSTNode n, K key) {
        BSTNode rNode = find(n, key);
        if (rNode == null) {
            return null;
        }
        return rNode.value;
    }



    @Override
    public int size() {
        return size(root);
    }

    private int size(BSTNode n) {
        if (n == null) {
            return 0;
        } else {
            return n.size;
        }
    }

    @Override
    public void put(K key, V value) {
        root = put(root, key, value);
    }

    private BSTNode put(BSTNode n, K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (n == null) {
            return new BSTNode(key, value, 1);
        }
        int cmp = key.compareTo(n.key);
        if (cmp < 0) {
            n.left = put(n.left, key, value);
        } else if (cmp == 0) {
            n.value = value;
        } else {
            n.right = put(n.right, key, value);
        }
        n.size = 1 + size(n.left) + size(n.right);
        return n;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<K>();
        keySet(root, keys);
        return keys;
    }

    private void keySet(BSTNode n, Set<K> keys) {
        if (n == null) {
            return;
        }
        keySet(n.left, keys);
        keys.add(n.key);
        keySet(n.right, keys);
    }

    @Override
    public V remove(K key) {
        removedValue = null;
        root = removeHelper(root, key, true);
        return removedValue;
    }

    private BSTNode removeHelper(BSTNode n, K key, boolean set) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (n == null) {
            return n;
        }
        int cmp = key.compareTo(n.key);
        if (cmp < 0) {
            n.left = removeHelper(n.left, key, set);
        } else if (cmp == 0) {
            if (set) {
                removedValue = n.value;
            }
            if (n.left == null) {
                return n.right;
            }
            if (n.right == null) {
                return n.left;
            }
            BSTNode newN = rightmost(n.left);
            n.value = newN.value;
            n.key = newN.key;
            n.left = removeHelper(n.left, newN.key, false);
        } else {
            n.right = removeHelper(n.right, key, set);
        }
        n.size = 1 + size(n.left) + size(n.right);
        return n;
    }

    private BSTNode rightmost(BSTNode n) {
        if (n.right == null) {
            return n;
        }
        return rightmost(n.right);
    }

    @Override
    public V remove(K key, V value) {
        if (value != get(key)) {
            return null;
        }
        return remove(key);
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    public void printInOrder() {
        printInOrder(root);
    }

    private void printInOrder(BSTNode n) {
        if (n == null) {
            System.out.print("");
            return;
        }
        printInOrder(n.left);
        System.out.println(n.value);
        printInOrder(n.right);
    }
}
