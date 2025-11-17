package deque;

import java.util.Iterator;

public class ArrayDeque<T>  implements Deque<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        nextFirst = 1;
        nextLast = 2;
        size = 0;
    }

    private int loop(int curr) {
        if (curr >= items.length) {
            curr = curr - items.length;
        }
        if (curr < 0) {
            curr = items.length + curr;
        }
        return curr;
    }

    private void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];
        for (int i = 0; i < size; i += 1) {
            int c = loop(nextFirst + 1 + i);
            a[i] = items[c];
        }
        nextFirst = capacity - 1;
        nextLast = size;
        items = a;
    }

    @Override
    public void addFirst(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextFirst] = item;
        nextFirst -= 1;
        nextFirst = loop(nextFirst);
        size += 1;
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        nextFirst = loop(nextFirst + 1);
        T returnValue = items[nextFirst];
        items[nextFirst] = null;
        size -= 1;
        if (items.length >= 16 && size * 4 < items.length) {
            resize (items.length / 2);
        }
        return returnValue;
    }

    @Override
    public void addLast(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextLast] = item;
        nextLast += 1;
        nextLast = loop(nextLast);
        size += 1;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        nextLast = loop(nextLast - 1);
        T returnValue = items[nextLast];
        items[nextLast] = null;
        size -= 1;
        if (items.length >= 16 && size * 4 < items.length) {
            resize (items.length / 2);
        }
        return returnValue;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        int curr = nextFirst + 1;
        for (int i = 0; i < size; i += 1) {
            curr = loop(curr);
            if (items[curr] == null) {
                break;
            }
            System.out.print(items[curr]);
            curr += 1;
        }
    }

    @Override
    public T get(int index) {
        if (index >= size || index < 0) {
            return null;
        }
        index = index + nextFirst + 1;
        index = loop(index);
        return items[index];
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int nextPos = 0;

        @Override
        public boolean hasNext() {
            return nextPos < size;
        }

        @Override
        public T next() {
            T returnItem = get(nextPos);
            nextPos += 1;
            return returnItem;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ArrayDeque oas) {
            if (oas.size() != this.size()) {
                return false;
            }
            for (int i = 0; i < this.size(); i += 1) {
                if (this.get(i) != oas.get(i)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
