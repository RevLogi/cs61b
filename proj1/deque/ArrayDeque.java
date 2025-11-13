package deque;

public class ArrayDeque<T> {
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

    public void addFirst(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextFirst] = item;
        nextFirst -= 1;
        nextFirst = loop(nextFirst);
        size += 1;
    }

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

    public void addLast(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextLast] = item;
        nextLast += 1;
        nextLast = loop(nextLast);
        size += 1;
    }

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

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

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

    public T get(int index) {
        if (index >= size || index < 0) {
            return null;
        }
        index = index + nextFirst + 1;
        index = loop(index);
        return items[index];
    }
}
