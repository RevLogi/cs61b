package bstmap;

import static org.junit.Assert.*;
import org.junit.Test;

/** Tests by Brendan Hu, Spring 2015, revised for 2016 by Josh Hug */
public class TestBSTMap {

  	@Test
    public void sanityGenericsTest() {
    	try {
    		BSTMap<String, String> a = new BSTMap<String, String>();
	    	BSTMap<String, Integer> b = new BSTMap<String, Integer>();
	    	BSTMap<Integer, String> c = new BSTMap<Integer, String>();
	    	BSTMap<Boolean, Integer> e = new BSTMap<Boolean, Integer>();
	    } catch (Exception e) {
	    	fail();
	    }
    }

    //assumes put/size/containsKey/get work
    @Test
    public void sanityClearTest() {
    	BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        for (int i = 0; i < 455; i++) {
            b.put("hi" + i, 1+i);
            //make sure put is working via containsKey and get
            assertTrue( null != b.get("hi" + i) && (b.get("hi"+i).equals(1+i))
                        && b.containsKey("hi" + i));
        }
        assertEquals(455, b.size());
        b.clear();
        assertEquals(0, b.size());
        for (int i = 0; i < 455; i++) {
            assertTrue(null == b.get("hi" + i) && !b.containsKey("hi" + i));
        }
    }

    // assumes put works
    @Test
    public void sanityContainsKeyTest() {
    	BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        assertFalse(b.containsKey("waterYouDoingHere"));
        b.put("waterYouDoingHere", 0);
        assertTrue(b.containsKey("waterYouDoingHere"));
    }

    // assumes put works
    @Test
    public void sanityGetTest() {
    	BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        assertEquals(null,b.get("starChild"));
        assertEquals(0, b.size());
        b.put("starChild", 5);
        assertTrue(((Integer) b.get("starChild")).equals(5));
        b.put("KISS", 5);
        assertTrue(((Integer) b.get("KISS")).equals(5));
        assertNotEquals(null,b.get("starChild"));
        assertEquals(2, b.size());
    }

    // assumes put works
    @Test
    public void sanitySizeTest() {
    	BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        assertEquals(0, b.size());
        b.put("hi", 1);
        assertEquals(1, b.size());
        for (int i = 0; i < 455; i++)
            b.put("hi" + i, 1);
        assertEquals(456, b.size());
    }

    //assumes get/containskey work
    @Test
    public void sanityPutTest() {
    	BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        b.put("hi", 1);
        assertTrue(b.containsKey("hi") && b.get("hi") != null);
    }

    //assumes put works
    @Test
    public void containsKeyNullTest() {
        BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        b.put("hi", null);
        assertTrue(b.containsKey("hi"));
    }

    private BSTMap<String, Integer> setupMap() {
        BSTMap<String, Integer> b = new BSTMap<>();
        b.put("dog", 10);
        b.put("bag", 20);
        b.put("flat", 30);
        b.put("alf", 40);
        b.put("cat", 50);
        b.put("elf", 60);
        b.put("emf", 70);
        b.put("glut", 80);
        return b;
    }

    @Test
    public void comprehensiveRemoveTest() {
        BSTMap<String, Integer> b = setupMap();
        assertEquals(8, b.size());

        // ----------------------------------------------------
        // Case 1: Remove Leaf Node (0 children) - "cat"
        // ----------------------------------------------------
        assertEquals(50, (int) b.remove("cat"));
        assertFalse(b.containsKey("cat"));
        assertEquals(7, b.size());

        // ----------------------------------------------------
        // Case 2: Remove Node with 1 Child (Right) - "elf"
        // "emf" (70) should move up to replace "elf" (60).
        // ----------------------------------------------------
        assertEquals(60, (int) b.remove("elf"));
        assertFalse(b.containsKey("elf"));
        assertTrue(b.containsKey("emf")); // Child remains
        assertEquals(6, b.size());

        // ----------------------------------------------------
        // Case 3: Remove Node with 2 Children (Internal) - "bag"
        // Should be replaced by its predecessor "alf" (or successor).
        // ----------------------------------------------------
        assertEquals(20, (int) b.remove("bag"));
        assertFalse(b.containsKey("bag"));
        assertTrue(b.containsKey("alf")); // Predecessor is still in the tree (but moved)
        assertEquals(5, b.size());

        // ----------------------------------------------------
        // Case 4: Remove Root Node with 2 Children - "dog"
        // ----------------------------------------------------
        assertEquals(10, (int) b.remove("dog"));
        assertFalse(b.containsKey("dog"));
        assertEquals(4, b.size());

        // ----------------------------------------------------
        // Case 5: Remove Node with 1 Child (Left) - "flat"
        // "emf" is now the root of the right subtree. "flat" has 1 child "glut".
        // ----------------------------------------------------
        assertEquals(30, (int) b.remove("flat"));
        assertFalse(b.containsKey("flat"));
        assertEquals(3, b.size());

        // ----------------------------------------------------
        // Case 6: Remove Non-Existent Key
        // Assuming V is Integer, remove returns null if key is not found.
        // ----------------------------------------------------
        assertNull(b.remove("zzz"));
        assertEquals(3, b.size());

        // ----------------------------------------------------
        // Case 7: Remaining Leaf Removals
        // ----------------------------------------------------
        assertEquals(80, (int) b.remove("glut"));
        assertEquals(2, b.size());

        assertEquals(40, (int) b.remove("alf"));
        assertEquals(1, b.size());

        assertEquals(70, (int) b.remove("emf"));
        assertEquals(0, b.size());
        assertFalse(b.containsKey("emf"));

        // ----------------------------------------------------
        // Case 8: Remove from Empty Map
        // ----------------------------------------------------
        assertNull(b.remove("dog"));
        assertEquals(0, b.size());
    }
}
