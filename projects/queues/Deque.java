import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {

    private Item[] array;
    private int size;
    private int front;
    private int back;

    // construct an empty deque
    public Deque() {
        array = (Item[]) new Object[10];
        size = 0;
        front = 0;
        back = 0;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // return the number of items on the deque
    public int size() {
        return size;
    }

    private void resize(int capacity) {
        Item[] temp = (Item[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            temp[i] = array[(front + i) % array.length];
        }
        array = temp;
        front = 0;
        back = size;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("can not add null into the deque.");
        }
        if (size == array.length) {
            resize(array.length * 2);
        }
        front = (front - 1 + array.length) % array.length;
        array[front] = item;
        size++;
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("can not add null into the deque.");
        }
        if (size == array.length) {
            resize(array.length * 2);
        }
        array[back] = item;
        back = (back + 1) % array.length;
        size++;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("Deque is empty.");
        }
        Item item = array[front];
        array[front] = null;
        front = (front + 1) % array.length;
        size--;
        if (size > 0 && size == array.length / 4) {
            resize(array.length / 2);
        }
        return item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("Deque is empty.");
        }
        back = (back - 1 + array.length) % array.length;
        Item item = array[back];
        array[back] = null;
        size--;
        if (size > 0 && size == array.length / 4) {
            resize(array.length / 2);
        }
        return item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new Iterator<Item>() {
            private int cur = front;
            private int count = 0;

            public boolean hasNext() {
                return count < size;
            }

            public Item next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                Item item = array[cur];
                cur = (cur + 1) % array.length;
                count++;
                return item;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException(
                        "this deque implementation doesn't support remove in iterator");
            }
        };
    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<Integer> deque = new Deque<>();
        deque.addFirst(1);
        deque.addLast(2);
        deque.addFirst(0);
        deque.addLast(3);

        System.out.println("Deque size: " + deque.size()); // 输出 4
        System.out.println("Deque is empty: " + deque.isEmpty()); // 输出 false

        System.out.println("Remove first: " + deque.removeFirst()); // 输出 0
        System.out.println("Remove last: " + deque.removeLast()); // 输出 3

        for (int item : deque) {
            System.out.print(item + " "); // 输出 1 2
        }
        System.out.println();
    }

}
