package deque;

import java.util.Iterator;
import java.util.Objects;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {

    private TNode sentinel;
    private int size;

    private class TNode {
        private T item;
        private TNode next;
        private TNode prev;
        public TNode(TNode p, T i, TNode n) {
            this.item = i;
            this.next = n;
            this.prev = p;
        }
    }

    public LinkedListDeque() {
        sentinel = new TNode(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    @Override
    public void  addFirst(T i) {
        size += 1;
        sentinel.next = new TNode(sentinel, i, sentinel.next);
        sentinel.next.next.prev = sentinel.next;
    }

    @Override
    public void  addLast(T i) {
        size += 1;
        sentinel.prev = new TNode(sentinel.prev, i, sentinel);
        sentinel.prev.prev.next = sentinel.prev;
    }


    @Override
    public boolean isEmpty() {
        return size == 0;
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        if (!isEmpty()) {
            TNode current = sentinel.next;
            for (int i = 0; i < size; i++) {
                System.out.print(current.item + " ");
                current = current.next;
            }
        }
    }

    @Override
    public T removeFirst() {
        if (!isEmpty()) {
            T result = sentinel.next.item;
            sentinel.next = sentinel.next.next;
            sentinel.next.prev = sentinel;
            size -= 1;
            return result;
        }
        return null;
    }

    @Override
    public T removeLast() {
        if (!isEmpty()) {
            T result = sentinel.prev.item;
            sentinel.prev = sentinel.prev.prev;
            sentinel.prev.next = sentinel;
            size -= 1;
            return result;
        }
        return null;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        TNode current = sentinel.next;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.item;
    }

    public T getRecursive(int index) {
        return getRecursiveHelper(index, sentinel.next);
    }

    private T getRecursiveHelper(int index, TNode current) {
        if (index < 0 || index >= size) {
            return null;
        }
        if (current == sentinel) {
            return null;
        }
        if (index == 0) {
            return current.item;
        } else {
            current = current.next;
            return getRecursiveHelper(index - 1, current);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private TNode current;
        public LinkedListDequeIterator() {
            current = sentinel.next;
        }
        @Override
        public boolean hasNext() {
            return current != sentinel;
        }
        @Override
        public T next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }
            T result = current.item;
            current = current.next;
            return result;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Deque<?>)) {
            return false;
        }

        Deque<?> other = (Deque<?>) o;

        if (this.size() != other.size()) {
            return false;
        }

        Iterator<T> it1 = this.iterator();
        Iterator<?> it2 = other.iterator();

        while (it1.hasNext() && it2.hasNext()) {
            if (!Objects.equals(it1.next(), it2.next())) {
                return false;
            }
        }

        return !it1.hasNext() && !it2.hasNext();
    }

}


