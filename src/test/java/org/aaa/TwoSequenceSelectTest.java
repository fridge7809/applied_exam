package org.aaa;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.Arrays;

public class TwoSequenceSelectTest {

    @Property
    <T extends Comparable<T>> void shouldFindCandidatesWithinLengthOfOriginalSeq(
            @ForAll() Integer[] a,
            @ForAll() Integer[] b) {

        int totalLength = a.length + b.length;

        Arrays.sort(a);
        Arrays.sort(b);

        // random k
        int k = Math.max(0, ((int) (Math.random() * totalLength) - 2));
        Integer[] candidates = TwoSequenceSelect.find(a, b, k);

        // Assert that the found indices are within the bounds of each list
        assertThat(candidates[0]).isBetween(0, a.length);
        assertThat(candidates[1]).isBetween(0, b.length);
    }

    @Example
    void shouldFindCorrectValues() {
        Integer[] a = { 1, 3, 5 };
        Integer[] b = { 2, 4, 6 };
        int k = 3;

        Integer[] result = TwoSequenceSelect.find(a, b, k);
        Integer[] expected = { 2, 1 };

        assertArrayEquals(expected, result);
    }

    @Example
    void shouldFindCorrectValues2() {
        Integer[] a = { 1, 3, 5, 10, 11, 100, 120, 121, 122 };
        Integer[] b = { 1, 2, 3, 4, 5, 101, 102, 103, 104 };
        int k = 6;

        Integer[] result = TwoSequenceSelect.find(a, b, k);
        Integer[] expected = { 2, 4 };

        assertArrayEquals(expected, result);
    }

    @Example
    void shouldFindCorrectValues3() {
        Integer[] a = { 1, 2 };
        Integer[] b = { 0 };
        int k = 2;

        Integer[] result = TwoSequenceSelect.find(a, b, k);
        Integer[] expected = { 1, 1 };

        assertArrayEquals(expected, result);
    }

    @Example
    void shouldFindCorrectValues4() {
        Integer[] a = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        Integer[] b = { 11, 12, 13 };
        int k = 11;

        Integer[] result = TwoSequenceSelect.find(a, b, k);
        Integer[] expected = { 10, 1 };

        assertArrayEquals(expected, result);
    }

    @Example
    void shouldFindCorrectValues5() {
        Integer[] a = { 12, 14, 17, 18 };
        Integer[] b = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 15, 16 };

        int k = 13;

        Integer[] result = TwoSequenceSelect.find(a, b, k);
        Integer[] expected = { 1, 12 };

        assertArrayEquals(expected, result);
    }

    @Example
    void shouldFindCorrectValues6() {
        Integer[] a = {}; // empty array
        Integer[] b = { 1, 2, 3 };

        int k = 2;

        Integer[] result = TwoSequenceSelect.find(a, b, k);
        // should only find elements from b array when a is empty
        Integer[] expected = { 0, 2 };

        assertArrayEquals(expected, result);
    }


    @Example
    void valuesCorrespondToCorrectIndicesInMergedArray() {
        Integer[] a = { 12, 14, 17, 18 };
        Integer[] b = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 15, 16 };
        int k = 13;
        Integer[] twoSeqSelectResult = TwoSequenceSelect.find(a, b, k);

        Integer[] listWithTwoRunsToMerge = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 15, 16, /** run separator :D */ 12, 14, 17, 18 };

        MergeSort.sort(listWithTwoRunsToMerge);

        int valueInA = a[twoSeqSelectResult[0] - 1]; // 12 -> the last element of a to use in the merged list
        int valueInB = b[twoSeqSelectResult[1] - 1]; // 13 -> the last element of b to use in the merged list
        int valueAtKthPositionFromMergedArray = listWithTwoRunsToMerge[k - 1]; // the element at the last position in the subarray of K elements, from the actual merge
        int actualValueAtKthPositionFromTwoSeq = Math.max(valueInA, valueInB); // the "last" element of the k sorted elements

        assertThat(valueAtKthPositionFromMergedArray).isEqualTo(actualValueAtKthPositionFromTwoSeq);
    }

    
}
