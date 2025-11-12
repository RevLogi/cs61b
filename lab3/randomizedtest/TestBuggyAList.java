package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {

    @Test
    public void testThreeAddThreeRemove(){
        AListNoResizing<Integer> t1 = new AListNoResizing<>();
        BuggyAList<Integer> t2 = new BuggyAList<>();

        for (int i = 4; i < 7; i += 1){
            t1.addLast(i);
            t2.addLast(i);
        }

        assertEquals(t1.size(), t2.size());
        assertEquals(t1.removeLast(), t2.removeLast());
        assertEquals(t1.removeLast(), t2.removeLast());
        assertEquals(t1.removeLast(), t2.removeLast());
    }

    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size1 = L.size();
                int size2 = B.size();
                System.out.println("L size: " + size1);
                System.out.println("B size: " + size2);
                assertEquals(size1, size2);
            } else if (operationNumber == 2){
                // getLast
                if (L.size() > 0){
                    int last1 = L.getLast();
                    int last2 = B.getLast();
                    System.out.println("L last: " + last1);
                    System.out.println("B last: " + last2);
                    assertEquals(last1, last2);
                }
            } else {
                // removeLast
                if (L.size() > 0){
                    int l = L.removeLast();
                    int b = B.removeLast();
                    System.out.println("remove last");
                    assertEquals(l, b);
                }
            }
        }
    }
}
