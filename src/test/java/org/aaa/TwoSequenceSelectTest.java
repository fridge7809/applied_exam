package org.aaa;

import net.jqwik.api.*;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TwoSequenceSelectTest {

    @Property
    <T extends Comparable<T>> void shouldFindRanksWithinLengthOfArrays(@ForAll() Integer[] a, @ForAll() Integer[] b) {

        int totalLength = a.length + b.length;

        Arrays.sort(a);
        Arrays.sort(b);

        // random k
        int k = Math.max(0, ((int) (Math.random() * totalLength) - 2));
        int[] ranks = TwoSequenceSelect.find(a, b, k);

        // Assert that the found indices are within the bounds of each list
        assertThat(ranks[0]).isBetween(0, a.length);
        assertThat(ranks[1]).isBetween(0, b.length);
    }

    @Example
    void equalLengthArrays() {
        Integer[] a = { 1, 3, 5 };
        Integer[] b = { 2, 4, 6 };
        int k = 3;

        int[] result = TwoSequenceSelect.find(a, b, k);
        int[] expected = {2, 1};

        assertArrayEquals(expected, result);
    }

    @Example
    void shouldThrowForKOutOfBounds() {
        Integer[] a = {1, 3, 5};
        Integer[] b = {2, 4, 6};
        int k = 7;
        assertThrows(AssertionError.class, () -> TwoSequenceSelect.find(a, b, k));
    }

    @Example
    void shouldThrowForNonSortedSubarrays() {
        Integer[] notSorted = {1, 5, 3};
        Integer[] sorted = {2, 4, 6};
        int k = 0;
        assertThrows(AssertionError.class, () -> TwoSequenceSelect.find(notSorted, sorted, k));
        assertThrows(AssertionError.class, () -> TwoSequenceSelect.find(sorted, notSorted, k));
        assertThrows(AssertionError.class, () -> TwoSequenceSelect.find(notSorted, notSorted, k));
    }

    @Example
    void longerArrays() {
        Integer[] a = { 1, 3, 5, 10, 11, 100, 120, 121, 122 };
        Integer[] b = { 1, 2, 3, 4, 5, 101, 102, 103, 104 };
        int k = 6;

        int[] result = TwoSequenceSelect.find(a, b, k);
        int[] expected = {2, 4};

        assertArrayEquals(expected, result);
    }

    @Example
    void arrayOfLengthOne() {
        Integer[] a = { 1, 2 };
        Integer[] b = { 0 };
        int k = 2;

        int[] result = TwoSequenceSelect.find(a, b, k);
        int[] expected = {1, 1};

        assertArrayEquals(expected, result);
    }

    @Example
    void disjointArrays() {
        Integer[] a = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        Integer[] b = { 11, 12, 13 };
        int k = 11;

        int[] result = TwoSequenceSelect.find(a, b, k);
        int[] expected = {10, 1};

        assertArrayEquals(expected, result);
    }

    @Example
    void disjointArraysReverse() {
        Integer[] a = { 12, 14, 17, 18 };
        Integer[] b = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 15, 16 };

        int k = 13;

        int[] result = TwoSequenceSelect.find(a, b, k);
        int[] expected = {1, 12};

        assertArrayEquals(expected, result);
    }

    @Example
    void emptyArrays() {
        Integer[] a = {}; // empty array
        Integer[] b = { 1, 2, 3 };

        int k = 2;

        int[] result = TwoSequenceSelect.find(a, b, k);
        // should only find elements from b array when a is empty
        int[] expected = {0, 2};

        assertArrayEquals(expected, result);
    }

    @Example
    void shouldHaveBoundraiesOfDataType() {
        Integer[] a = {-3, -1};
        Integer[] b = {Integer.MIN_VALUE, 2, Integer.MAX_VALUE};
        int k = 2;
        int[] actual = TwoSequenceSelect.find(a, b, k);
        assertThat(actual).containsExactly(1, 1);
    }

    @Example
    void shouldNotUnderflowForMidCalculation() {
        long low = Integer.MAX_VALUE - 100;
        long high = Integer.MAX_VALUE - 50;
        long mid = (low + high) / 2;
        assertThat(mid).isPositive();
    }


    @Example
    void valuesCorrespondToCorrectIndicesInMergedArray() {
        Integer[] a = { 12, 14, 17, 18 };
        Integer[] b = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 15, 16 };
        int k = 13;
        int[] twoSeqSelectResult = TwoSequenceSelect.find(a, b, k);

        Integer[] listWithTwoRunsToMerge = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 15, 16, /** run separator :D */ 12, 14, 17, 18 };

        MergeSort.sort(listWithTwoRunsToMerge);

        int valueInA = a[twoSeqSelectResult[0] - 1]; // 12 -> the last element of a to use in the merged list
        int valueInB = b[twoSeqSelectResult[1] - 1]; // 13 -> the last element of b to use in the merged list
        int valueAtKthPositionFromMergedArray = listWithTwoRunsToMerge[k - 1]; // the element at the last position in the subarray of K elements, from the actual merge
        int actualValueAtKthPositionFromTwoSeq = Math.max(valueInA, valueInB); // the "last" element of the k sorted elements

        assertThat(valueAtKthPositionFromMergedArray).isEqualTo(actualValueAtKthPositionFromTwoSeq);
    }

    @Example
    void shouldFindRanksInterchangeably() {
        Pair[] a = new Pair[] {new Pair<>(1, 0), new Pair(3, 1), new Pair(5, 2)};
        Pair[] b = new Pair[] {new Pair<>(2, 0), new Pair(4, 1), new Pair(6, 2)};

        int totalLength = a.length + b.length;
        for (int i = 0; i < totalLength; i++) {
            assertThat(TwoSequenceSelect.find(a, b, i)).containsExactly(find(a, b, i));
        }
    }

    public static int[] find(Pair[] a, Pair[] b, int k) {
        int i = 0, j = 0;

        while (k > 0) {
            if (i <= j) {
                i++;
            } else {
                j++;
            }
            k--;
        }

        return new int[] {i, j};
    }




    @Provide
    Arbitrary<List<Pair<Integer, Integer>>> pairListProvider() {
        Arbitrary<Integer> leftValueProvider = Arbitraries.integers().between(0, 100).injectDuplicates(0.7);
        return Combinators.combine(leftValueProvider, distinctIntegerArrayProvider())
                .as((_, rightArr) ->
                        Arrays.stream(rightArr)
                                .map(right -> new Pair<>(leftValueProvider.sample(), right))
                                .distinct()
                                .toList()
                );
    }

    Arbitrary<Integer[]> distinctIntegerArrayProvider() {
        return Arbitraries.integers()
                .between(0, 100)
                .array(Integer[].class)
                .ofMinSize(1)
                .uniqueElements()
                .map(arr -> {
                    Arrays.sort(arr);
                    return arr;
                });
    }


}
