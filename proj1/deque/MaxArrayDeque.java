package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        comparator = c;
    }

    public T max() {
        if (isEmpty()) {
            return null;
        }
        T result = get(0);
        for (T i: this) {
            if (comparator.compare(i, result) > 0) {
                result = i;
            }
        }
        return result;
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        if (c == null) {
            return max();
        }
        T result = get(0);
        for (T i: this) {
            if (c.compare(i, result) > 0) {
                result = i;
            }
        }
        return result;
    }

}

