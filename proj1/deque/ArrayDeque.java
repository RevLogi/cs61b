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

    private void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];
        if (capacity > size) {
            System.arraycopy(items, 0, a, 0, nextLast);
            System.arraycopy(items, nextLast, a, capacity - size + nextLast, size - nextLast);
        }
        if (capacity == size) {
            for (int i = 0; items[i] != null; i +=1) {
                a[i] = items[i];
            }
            for (int i = items.length - 1; items[i] != null; i -= 1) {
                a[i] = items[i];
            }
        }
        items = a;
    }

    public void addFirst(T item) {
        if (nextFirst + 1 == nextLast) {
            resize(size * 2);
        }
        items[nextFirst] = item;
        nextFirst -= 1;
        size += 1;
    }

    public T removeFirst() {
        nextFirst += 1;
        T returnValue = items[nextFirst];
        items[nextFirst] = null;
        if (size * 4 < items.length) {
            resize (size);
        }
        return returnValue;
    }

    public void addNext(T item) {
        if (nextFirst + 1 == nextLast) {
            resize(size * 2);
        }
        items[nextLast] = item;
        nextLast += 1;
        size += 1;
    }

    public T removeLast() {
        nextLast -= 1;
        T returnValue = items[nextLast];
        items[nextLast] = null;
        if (size * 4 < items.length) {
            resize (size);
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
            if (curr >= size) {
                curr = curr - size;
            }
            if (items[curr] == null) {
                break;
            }
            System.out.print(items[curr]);
            curr += 1;
        }
    }

    public T get(int index) {
        if (index >= size) {
            index = index - size;
        }
        return items[index];
    }
}
