package deque;

public class LinkedListDeque<T> {
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

    public boolean isEmpty() {
        if (sentinel.next == sentinel && sentinel.prev == sentinel){
            return true;
        }
        return false;
    }

    public void addFirst(T item) {
        DequeNode n = new DequeNode(sentinel, item, sentinel.next);
        sentinel.next.prev = n;
        sentinel.next = n;
        size += 1;
    }

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

    public void addLast(T item) {
        DequeNode n = new DequeNode(sentinel.prev, item, sentinel);
        sentinel.prev.next = n;
        sentinel.prev = n;
        size += 1;
    }

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

    public T get(int index) {
        DequeNode indexNode = sentinel;
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
        DequeNode sentHelper = sentinel;
        return getHelper(index, sentHelper);
    }

    private T getHelper(int index, DequeNode sentHelper) {
        if (index == 0) {
            return sentHelper.item;
        } else {
            return getHelper(index - 1, sentHelper.next)
        }
    }

    public void printDeque() {
        DequeNode curr = sentinel;
        for (int i = 0; i < size; i += 1) {
            System.out.print(curr.next.item + " ");
            curr = curr.next;
        }
    }

    public int size() {
        return size;
    }
}
