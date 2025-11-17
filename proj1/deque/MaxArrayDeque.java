package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> givenComparator;

    public MaxArrayDeque(Comparator<T> c) {
        givenComparator = c;
    }

    public T max() {
        return max(givenComparator);
    }

    public T max(Comparator<T> c) {
        T maxItem = this.get(0);

        for (T x : this) {
            if (c.compare(x, maxItem) > 0) {
                maxItem = x;
            }
        }
        return maxItem;
    }
}
