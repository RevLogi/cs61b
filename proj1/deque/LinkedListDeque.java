package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T> {
    private class DequeNode {
        public T item;
        public DequeNode prev;
        public DequeNode next;

        public DequeNode(DequeNode p, T i, DequeNode n) {
            prev = p;
            item = i;
            next = n;
        }
    }

    private DequeNode sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel = new DequeNode(null, null,null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        DequeNode n = new DequeNode(sentinel, item, sentinel.next);
        sentinel.next.prev = n;
        sentinel.next = n;
        size += 1;
    }

    @Override
    public T removeFirst() {
        if (size > 0) {
            T returnValue = sentinel.next.item;
            sentinel.next.item = null;
            sentinel.next.next.prev = sentinel;
            sentinel.next = sentinel.next.next;
            size -= 1;
            return returnValue;
        }
        return null;
    }

    @Override
    public void addLast(T item) {
        DequeNode n = new DequeNode(sentinel.prev, item, sentinel);
        sentinel.prev.next = n;
        sentinel.prev = n;
        size += 1;
    }

    @Override
    public T removeLast() {
        if (size > 0) {
            T returnValue = sentinel.prev.item;
            sentinel.prev.item = null;
            sentinel.prev.prev.next = sentinel;
            sentinel.prev = sentinel.prev.prev;
            size -= 1;
            return returnValue;
        }
        return null;
    }

    @Override
    public T get(int index) {
        DequeNode indexNode = sentinel.next;
        if (index >= size) {
            return null;
        }
        for (int i = 0; i < index; i += 1) {
            indexNode = indexNode.next;
        }
        return indexNode.item;
    }

    public T getRecursive(int index) {
        if (index >= size) {
            return null;
        }
        DequeNode sentHelper = sentinel.next;
        return getHelper(index, sentHelper);
    }

    private T getHelper(int index, DequeNode sentHelper) {
        if (index == 0) {
            return sentHelper.item;
        } else {
            return getHelper(index - 1, sentHelper.next);
        }
    }

    @Override
    public void printDeque() {
        DequeNode curr = sentinel;
        for (int i = 0; i < size; i += 1) {
            System.out.print(curr.next.item + " ");
            curr = curr.next;
        }
    }

    @Override
    public int size() {
        return size;
    }

    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
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
        if (o instanceof LinkedListDeque oas) {
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
