package org.aaa;

import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.UniqueElements;
import net.jqwik.api.constraints.WithNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class TwoSequenceSelectTest {

    @Property
    <T extends Comparable<T>> void shouldFindCandidatesWithinLengthOfOriginalSeq(
            @ForAll() int[] a,
            @ForAll() int[] b) {

        int totalLength = a.length + b.length;

        // random k
        int k = Math.max(0, ((int) (Math.random() * totalLength)-2));
        int[] candidates = TwoSequenceSelect.find(a, b, k);

        // Assert that the found indices are within the bounds of each list
        assertThat(candidates[0]).isBetween(0, a.length);
        assertThat(candidates[1]).isBetween(0, b.length);
    }


    @Example
    void shouldFindCorrectValues(){
        int[] a = {1, 3, 5}; 
        int[] b = {2, 4, 6};  
        int k = 3;

        int[] result = TwoSequenceSelect.find(a, b, k);
        int[] expected = {2, 1};

        assertArrayEquals(expected, result);
    }

    @Example
    void shouldFindCorrectValues2(){
        int[] a = {1, 3, 5, 10, 11, 100, 120, 121, 122}; 
        int[] b = {1, 2, 3, 4, 5, 101, 102, 103, 104};
        int k = 6;

        int[] result = TwoSequenceSelect.find(a, b, k);
        int[] expected = {2, 4};

        assertArrayEquals(expected, result);
    }

    @Example
    void shouldFindCorrectValues3(){
        int[] a = {1, 2}; 
        int[] b = {0};
        int k = 2;

        int[] result = TwoSequenceSelect.find(a, b, k);
        int[] expected = {1, 1};

        assertArrayEquals(expected, result);
    }

    @Example
    void shouldFindCorrectValues4(){
        int[] a = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}; 
        int[] b = {11, 12, 13};
        int k = 11;

        int[] result = TwoSequenceSelect.find(a, b, k);
        int[] expected = {10, 1};

        assertArrayEquals(expected, result);
    }

    @Example
    void shouldFindCorrectValues5(){
        int[] a = {12, 14, 17, 18};
        int[] b = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 15, 16}; 

        int k = 13;

        int[] result = TwoSequenceSelect.find(a, b, k);
        int[] expected = {1, 12};

        assertArrayEquals(expected, result);
    }
}
