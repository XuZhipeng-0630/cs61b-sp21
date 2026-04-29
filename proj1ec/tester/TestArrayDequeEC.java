package tester;

import static org.junit.Assert.*;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {
    @Test
    public void randomizedTest() {
        ArrayDequeSolution<Integer> L = new ArrayDequeSolution<>();
        StudentArrayDeque<Integer> buggyL = new StudentArrayDeque<>();
        StringBuilder log = new StringBuilder();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                log.append("addLast(").append(randVal).append(")\n");
                L.addLast(randVal);
                buggyL.addLast(randVal);
                assertEquals(L.size(), buggyL.size());
//                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                int randVal = StdRandom.uniform(0, 100);
                log.append("addFirst(").append(randVal).append(")\n");
                L.addFirst(randVal);
                buggyL.addFirst(randVal);
                assertEquals(L.size(), buggyL.size());
//                System.out.println("addFirst(" + randVal + ")");
            } else if (operationNumber == 2) {
                if (!L.isEmpty()) {
                    log.append("removeFirst()\n");
                    Integer num1 = L.removeFirst();
                    Integer num2 = buggyL.removeFirst();
                    assertEquals(log.toString(), num1, num2);
                }
            } else if (operationNumber == 3) {
                if (!L.isEmpty()) {
                    log.append("removeLast()\n");
                    Integer num1 = L.removeLast();
                    Integer num2 = buggyL.removeLast();
                    assertEquals(log.toString(), num1, num2);
                }
            }
        }
    }
}
