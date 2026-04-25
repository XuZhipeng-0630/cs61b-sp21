package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> buggyL = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                buggyL.addLast(randVal);
                // System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size1 = L.size();
                int size2 = buggyL.size();
                assertEquals(size1, size2);
                // System.out.println("size: " + size1);
            } else if (operationNumber == 2) {
                if (L.size() > 0 && L.size() == buggyL.size()) {
                    int num1 = L.getLast();
                    int num2 = buggyL.getLast();
                    assertEquals(num1, num2);
                }
            } else if (operationNumber == 3) {
                if (L.size() > 0 && L.size() == buggyL.size()) {
                    int num1 = L.removeLast();
                    int num2 = buggyL.removeLast();
                    assertEquals(num1, num2);
                    assertEquals(L.size(), buggyL.size());
                }
            }
        }
    }
}
