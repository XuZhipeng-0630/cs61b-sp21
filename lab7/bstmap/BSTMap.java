package bstmap;

import java.util.*;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private BSTnode root;

    private class BSTnode {
        private K key;
        private V value;
        private BSTnode left;
        private BSTnode right;
        private int size;

        public BSTnode(K key, V value, int size) {
            this.key = key;
            this.value = value;
            this.size = size;
        }

    }

    public BSTMap() {}

    @Override
    public void clear() {
        root = null;
    }

    @Override
    public boolean containsKey(K key) {
        if (key == null) throw new IllegalArgumentException("argument to containsKey() is null");
        return containsKey(root, key);
    }

    private boolean containsKey(BSTnode node, K key) {
        if (node == null) return false;
        int cmp = key.compareTo(node.key);
        if (cmp == 0) return true;
        else if (cmp < 0) return containsKey(node.left, key);
        else return containsKey(node.right, key);
    }

    @Override
    public V get(K key) {
        return get(root, key);
    }

    private V get(BSTnode node, K key) {
        if (key == null) throw new IllegalArgumentException("calls get() with a null key");
        if (node == null) return null;
        int cmp = key.compareTo(node.key);
        if (cmp < 0) return get(node.left, key);
        else if (cmp > 0) return get(node.right, key);
        else return node.value;
    }

    @Override
    public int size() {
        return size(root);
    }

    private int size(BSTnode node) {
        if (node == null) return 0;
        else return node.size;
    }

    @Override
    public void put(K key, V value) {
        if (key == null) throw new IllegalArgumentException("calls put() with a null key");
        else {
            root = put(root, key, value);
        }
    }

    private BSTnode put(BSTnode node, K key, V value) {
        if (node == null) {
            return new BSTnode(key, value, 1);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) node.left = put(node.left, key, value);
        else if (cmp > 0) node.right = put(node.right, key, value);
        else node.value = value;
        node.size = size(node.left) + size(node.right) + 1;
        return node;
    }

    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        for (K key : this) {
            set.add(key);
        }
        return set;
    }

    @Override
    public V remove(K key) {
        if (key == null) throw new IllegalArgumentException("calls remove() with a null key");
        V returnValue = get(key);
        if (returnValue != null || containsKey(key)) {
            root = remove(root, key);
        }
        return returnValue;
    }

    private BSTnode remove(BSTnode node, K key) {
        if (node == null) return null;
        int cmp = key.compareTo(node.key);
        if (cmp < 0) node.left = remove(node.left, key);
        else if (cmp > 0) node.right = remove(node.right, key);
        else {
            if (node.left == null) return node.right;
            else if (node.right == null) return node.left;
            else {
                BSTnode successorNode = min(node.right);
                node.key = successorNode.key;
                node.value = successorNode.value;
                node.right = deleteMin(node.right);
            }
        }
        node.size = size(node.left) + size(node.right) + 1;
        return node;
    }

    private BSTnode min(BSTnode node) {
        if (node.left != null) return min(node.left);
        else return node;
    }

    private BSTnode deleteMin(BSTnode node) {
        if (node.left == null) return node.right;
        node.left = deleteMin(node.left);
        node.size = size(node.left) + size(node.right) + 1;
        return node;
    }

    @Override
    public V remove(K key, V value) {
        if (key == null) throw new IllegalArgumentException("calls remove() with a null key");
        if (Objects.equals(get(key), value) && containsKey(key)) return remove(key);
        else return null;
    }

    @Override
    public Iterator<K> iterator() {
        return keys().iterator();
    }

    private Iterable<K> keys() {
        Deque<K> deque = new LinkedList<>();
        keys(root, deque);
        return deque;
    }

    private void keys(BSTnode node, Deque<K> deque) {
        if (node == null) return;
        keys(node.left, deque);
        deque.addLast(node.key);
        keys(node.right, deque);
    }

    public void printInOrder() {
        for (K key : this) {
            System.out.print(key + " ");
        }
    }

}
