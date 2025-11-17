package deque;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Comparator;

public class MaxArrayDequeTest {
    private static class alphabetComparator implements Comparator<String> {
        @Override
        public int compare(String a, String b) {
            return a.compareTo(b);
        }
    }

    private static class sizeComparator implements Comparator<String> {
        @Override
        public int compare(String a, String b) {
            if (a.length() > b.length()) {
                return 1;
            } else if (a.length() < b.length()) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    @Test
    /* Create a MaxArrayDeque and use max method to get the max element */
    public void maxTest() {
        Comparator<String> alphabetComparator = new alphabetComparator();
        Comparator<String> sizeComparator = new sizeComparator();

        MaxArrayDeque<String> mad = new MaxArrayDeque<>(alphabetComparator);
        mad.addFirst("front");
        mad.addLast("middle");
        mad.addLast("last");
        Object maxItem1 = mad.max();
        Object maxItem2 = mad.max(sizeComparator);
        assertEquals("middle", maxItem2);
        assertEquals("middle", maxItem1);
        mad.addLast("zero");
        Object maxItem3 = mad.max();
        assertEquals("zero", maxItem3);
    }
}
