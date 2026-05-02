package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;

public class MaxArrayDequeTest {

    private static class IntComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer a, Integer b) {
            return a - b;
        }
    }

    private static class ReverseIntComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer a, Integer b) {
            return b - a;
        }
    }

    private static class StringAlphabeticalComparator implements Comparator<String> {
        @Override
        public int compare(String a, String b) {
            return a.compareTo(b);
        }
    }

    private static class StringLengthComparator implements Comparator<String> {
        @Override
        public int compare(String a, String b) {
            return a.length() - b.length();
        }
    }

    @Test
    public void maxReturnsNullOnEmptyDequeTest() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        assertNull(mad.max());
        assertNull(mad.max(new ReverseIntComparator()));
    }

    @Test
    public void maxUsesStoredIntegerComparatorTest() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        mad.addLast(3);
        mad.addLast(10);
        mad.addLast(2);
        mad.addLast(7);

        assertEquals(Integer.valueOf(10), mad.max());
    }

    @Test
    public void maxWithCustomComparatorOverridesStoredComparatorTest() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        mad.addLast(3);
        mad.addLast(10);
        mad.addLast(2);
        mad.addLast(7);

        assertEquals(Integer.valueOf(2), mad.max(new ReverseIntComparator()));
    }

    @Test
    public void maxUsesAlphabeticalComparatorForStringsTest() {
        MaxArrayDeque<String> mad = new MaxArrayDeque<>(new StringAlphabeticalComparator());
        mad.addLast("apple");
        mad.addLast("banana");
        mad.addLast("cherry");
        mad.addLast("apricot");

        assertEquals("cherry", mad.max());
    }

    @Test
    public void maxWithDifferentStringComparatorTest() {
        MaxArrayDeque<String> mad = new MaxArrayDeque<>(new StringAlphabeticalComparator());
        mad.addLast("a");
        mad.addLast("alphabet");
        mad.addLast("cat");
        mad.addLast("dog");

        assertEquals("dog", mad.max());
        assertEquals("alphabet", mad.max(new StringLengthComparator()));
    }
}
