import java.util.Iterator;
import edu.princeton.cs.algs4.StdOut;

public class Deque<Item> implements Iterable<Item> {

    private Node first = null;
    private Node last  = null;
    private int m_size = 0;
    
    private class Node
    {
        Item item;
        Node next;      // two references needed because we can push from any side
        Node previous;  // and then it's not known from which side will be pop-ed
    }
    // null (<--next) first (previous->>) -- x -- x -- (<--next) last (previous-->) null

    // unit testing (required)
    public static void main(String[] args) {
        Deque<Integer> deque = new Deque<Integer>();
        StdOut.printf("size \t = %d\n", deque.size() );
        StdOut.printf("is empty \t = %b\n", deque.isEmpty() );
        
        for (int i = 1; i <= 3; ++i)   deque.addLast(-i);
        for (int i = 1; i <= 3; ++i)   deque.addFirst(i);

        StdOut.printf("removed elements: ");
        for (int i = 0; i < 4; ++i)    StdOut.printf("%d ", deque.removeLast());
        for (int i = 0; i < 2; ++i)    StdOut.printf("%d ", deque.removeFirst());
        StdOut.printf("\n");

        for (int i = 1; i <= 4; ++i)   deque.addLast(10*i);
        for (int i = 0; i < 2; ++i)    deque.removeFirst();
        deque.removeLast();
        for (int i = 1; i <= 3; ++i)   deque.addFirst(100*i);
        for (int i = 1; i <= 4; ++i)   deque.addLast(10*i);
        for (int i = 0; i < 7; ++i)    deque.removeLast();


        StdOut.printf("size \t = %d\n", deque.size() );
        StdOut.printf("is empty \t = %b\n", deque.isEmpty() );
        System.out.print( "\nelements are:\n[ " );
        for (int x : deque)
            StdOut.printf("%d ", x );
        System.out.println( "]\n" );
    }

    // construct an empty deque
    public Deque() {  }

    // is the deque empty?
    public boolean isEmpty() {
        if (first == null ^ last == null)
            throw new java.util.NoSuchElementException("Sanity check: Deque appears empty and not-empty at the same time");
        return (first == null || last == null);
    }


    // return the number of items on the deque
    public int size() { return m_size; }

    // add the item to the front
    public void addFirst(Item item)
    {
        if (item == null) throw new IllegalArgumentException("addFirst: null argument");

        Node oldfirst = first;
        first = new Node();
        first.item = item;
        first.next = null;
        first.previous = oldfirst;
        ++m_size;
        if (last == null)    last = first;
        else                 oldfirst.next = first;
    }

    // add the item to the back
    public void addLast(Item item)
    {
        if (item == null) throw new IllegalArgumentException("addLast: null argument");

        Node oldlast = last;
        last = new Node();
        last.item = item;
        last.next = oldlast;
        last.previous = null;
        ++m_size;
        if (first == null)    first = last;
        else                  oldlast.previous = last;
    }

    // remove and return the item from the front
    public Item removeFirst()
    {
        if (isEmpty()) throw new java.util.NoSuchElementException("Can't remove first element. The deque is empty!");

        Item item = first.item;
        first = first.previous;
        if (first == null)  last = first;           // needed to figure these 2 lines out
        else                first.next = null;
        m_size--;
        return item;
    }

    // remove and return the item from the back
    public Item removeLast()
    {
        // How to use method (iterator()) on 'this' object ? Is it even possible ?
        /* System.out.print( "t1: " );
        for (int x : this) StdOut.printf("%d ", x );

        ListIterator i = this.iterator();
        while (i.hasNext())
        {
            int s = i.next();
            StdOut.printf("%d ", i );
        } */

        if (isEmpty()) throw new java.util.NoSuchElementException("Can't remove last element. The deque is empty!");

        Item item = last.item;
        last = last.next;
        // if (last != null) last.previous = null;
        // else              first = last; // = null
        if (last == null)  first = last;            // --
        else               last.previous = null;
        m_size--;
        return item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() { return new ListIterator(); }

    private class ListIterator implements Iterator<Item>
    {
        private Node current = first;
        public boolean hasNext() { return current != null; } // here, 'current' is one element behind the last one
        public void remove() { throw new UnsupportedOperationException("(remove) We don't do that here"); }
        public Item next() // name collision (in this context next==>previous)
        {
            if (!hasNext()) {throw new java.util.NoSuchElementException("ListIterator out of bounds"); }
            Item item = current.item;
            current = current.previous;
            return item;
        }
    }
}
