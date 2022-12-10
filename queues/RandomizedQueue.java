import java.util.Iterator;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class RandomizedQueue<Item> implements Iterable<Item> {

    private Node first = null;
    private Node last  = null;
    private int m_size = 0;
    
    private class Node
    {
        Item item;
        Node next;
    }
    
    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<Integer> queue = new RandomizedQueue<Integer>();

        StdOut.printf("is empty \t = %b\n", queue.isEmpty() );
        for (int i = 0; i < 10; ++i)   queue.enqueue(i);
        StdOut.printf("size \t = %d\n", queue.size() );
        StdOut.printf("is empty \t = %b\n", queue.isEmpty() );

        for (int x : queue)
            StdOut.printf("%d ", x );
            
        StdOut.printf("\nrandom samples: [ ");
        for (int i = 0; i < 5; ++i)
            StdOut.printf("%d ", queue.sample() );
        StdOut.printf("]\n");
        
        StdOut.printf("removed elements: [ ");
        for (int i = 0; i < 9; ++i)   StdOut.printf("%d ", queue.dequeue() ); // to debug (sometimes it crushes...)
        StdOut.printf("]\nRemaining elemements: ");

        for (int x : queue)
            StdOut.printf("%d ", x );
        StdOut.printf("\n");
        StdOut.printf("size \t = %d\n", queue.size() );
    }
    
    // construct an empty randomized queue
    public RandomizedQueue() { }

    // add the item
    public void enqueue(Item item) {
        if (item == null) throw new IllegalArgumentException("enqueue: null argument");

        Node oldlast = last;
        last = new Node();
        last.item = item;
        last.next = null;
        if (first == null) first = last;
        else oldlast.next = last;
        ++m_size;
    }

    private Item dequeueFirst() {
        Item item = first.item;
        first = first.next;
        if (first == null) last = null;
        --m_size;
        return item;
    }

    // remove and return a random item (pick node at random and increment it)
    public Item dequeue() {
        if (isEmpty()) throw new java.util.NoSuchElementException("Can't remove element. The queue is empty!");

        Node hitNode = sampleNode();
        if (hitNode == last) return dequeueFirst(); // we hit last node -> advance in a circular way
        
        Node targetNode = hitNode.next;
        Item item = targetNode.item;
        if (targetNode == last)  {
            hitNode.next = null;
            last = hitNode;
        }
        else
            hitNode.next = targetNode.next;
        targetNode = null; // explicitly delete removed object (I don't know how to do this in dequeFirst() )
        /* case if is empty is covered by dequeueFirst() */
        --m_size;
        return item;
    }

    // return a random node (but do not remove it)
    private Node sampleNode() {
        if (isEmpty()) throw new java.util.NoSuchElementException("Can't remove element. The queue is empty!");

        Node tmpNode = this.first;
        int index = StdRandom.uniformInt(0, m_size);
        for (int i = 0; i < index; ++i) tmpNode = tmpNode.next;
        return tmpNode;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        Node tmpNode = sampleNode();
        return tmpNode.item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() { return new ListIterator(); }

    private class ListIterator implements Iterator<Item>
    {
        // private Node current = first; to go
        private int[] order;
        private int cnt = 0;
        public ListIterator() { this.order = generateOrder(m_size); }
        public boolean hasNext() { return cnt < m_size; } // here, 'current' is one element behind the last one
        public void remove() { throw new UnsupportedOperationException("(remove) We don't do that here"); }
        public Item next()
        {
            if (!hasNext()) {throw new java.util.NoSuchElementException("ListIterator out of bounds"); }
            int index = order[cnt];
            ++cnt;
            Node tmpNode = first;

            for (int i = 0; i < index; ++i)
                tmpNode = tmpNode.next;
            return tmpNode.item;
        }
    }

    private int[] generateOrder(int N) {
        int tab[] = new int[N];
        for (int i = 0; i < N; ++i) tab[i] = i;
        StdRandom.shuffle(tab);
        return tab;
    }

    // is the deque empty?
    public boolean isEmpty() {
        if (first == null ^ last == null)
            throw new java.util.NoSuchElementException("Sanity check: Deque appears empty and not-empty at the same time");
        return (first == null || last == null);
    }

    // return the number of items on the randomized queue
    public int size() { return m_size; }
}