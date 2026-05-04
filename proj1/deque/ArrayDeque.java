package deque;

import java.util.Iterator;
import java.util.Objects;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private int size;
    private T[] items;
    private int nextFirst;
    private int nextLast;
    private int boxsize;

    public ArrayDeque() {
        size = 0;
        boxsize = 8;
        items = (T[]) new Object[boxsize];
        nextFirst = 4;
        nextLast = 5;
    }

    private int minusOneIndex(int index) {
        return (index - 1 + boxsize) % boxsize;
    }

    private int plusOneIndex(int index) {
        return (index + 1) % boxsize;
    }

    private int firstIndex() {
        return plusOneIndex(nextFirst);
    }

    private int lastIndex() {
        return minusOneIndex(nextLast);
    }

    private void addOneResize() {
        if (size == boxsize) {
            T[] temp = (T[]) new Object[boxsize * 2];
            for (int i = 0; i < size; i++) {
                temp[i] = get(i);
            }
            items = temp;
            boxsize *= 2;
            nextFirst = boxsize - 1;
            nextLast = size;
        }
    }

    private void minusOneResize() {
        if ((boxsize > 8) && (size <= boxsize / 4)) {
            T[] temp = (T[]) new Object[boxsize / 2];
            for (int i = 0; i < size; i++) {
                temp[i] = get(i);
            }
            items = temp;
            boxsize /= 2;
            nextFirst = boxsize - 1;
            nextLast = size;
        }
    }

    @Override
    public void addFirst(T item) {
        addOneResize();
        items[nextFirst] = item;
        nextFirst = minusOneIndex(nextFirst);
        size++;
    }

    @Override
    public void addLast(T item) {
        addOneResize();
        items[nextLast] = item;
        nextLast = plusOneIndex(nextLast);
        size++;
    }


//    @Override
//    public boolean isEmpty() {
//        return size == 0;
//    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(get(i) + " ");
        }
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        int first = firstIndex();
        T result = items[first];
        items[first] = null;
        nextFirst = first;
        size--;
        minusOneResize();
        return result;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        int last = lastIndex();
        T result = items[last];
        items[last] = null;
        nextLast = last;
        size--;
        minusOneResize();
        return result;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return items[(firstIndex() + index) % boxsize];
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int index;
        public ArrayDequeIterator() {
            index = 0;
        }
        @Override
        public boolean hasNext() {
            return index < size;
        }
        @Override
        public T next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }
            T result = get(index);
            index++;
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

        for (int i = 0; i < size; i++) {
            if (!Objects.equals(this.get(i), other.get(i))) {
                return false;
            }
        }

        return true;
    }

}
