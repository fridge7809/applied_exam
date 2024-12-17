package org.aaa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

public class MergeSortParallel<T extends Comparable<T>> {

    private static ForkJoinPool pool;
    private static int equalPartsOfOutputArray;

    private static <T extends Comparable<T>> int sort(T[] source, T[] buffer, int low, int high,
            int insertionThreshold) {
        // if high and low are equal, we only have 1 element which is per definition
        // sorted, and dont add any comparisons
        if (high == low)
            return 0;

        int mid = low + (high - low) / 2;

        int leftComparisons;
        int rightComparisons;

        if (high <= low + insertionThreshold) {
            leftComparisons = sort(source, buffer, low, mid, insertionThreshold);
            rightComparisons = sort(source, buffer, mid + 1, high, insertionThreshold);
        } else {
            leftComparisons = pool.submit(() -> sort(source, buffer, low, mid, insertionThreshold))
                    .join();

            rightComparisons = pool.submit(() -> sort(source, buffer, mid + 1, high, insertionThreshold))
                    .join();
        }

        return leftComparisons + rightComparisons + merge(source, buffer, low, mid, high);
    }

    private static <T extends Comparable<T>> int merge(T[] source, T[] buffer, int low, int mid, int high) {
        // Precondition, merge operation expects sorted subarrays.
        assert Utils.isSorted(source, low, mid);
        assert Utils.isSorted(source, mid + 1, high);
        int comparisons = 0;

        if (high + 1 - low >= 0) {
            System.arraycopy(source, low, buffer, low, high + 1 - low);
        }

        int i = low;
        int j = mid + 1;
        for (int k = low; k <= high; k++) {
            if (i > mid) {
                source[k] = buffer[j++];
            } else if (j > high) {
                source[k] = buffer[i++];
            } else if (Utils.less(buffer[j], buffer[i])) {
                source[k] = buffer[j++];
                comparisons++;
            } else {
                source[k] = buffer[i++];
                comparisons++;
            }
        }

        assert Utils.isSorted(source, low, high);
        return comparisons;
    }

    private static <T extends Comparable<T>> int sortWithParallelMerge(T[] source, T[] buffer, int low, int high,
            int serialSortThreshold) throws InterruptedException, ExecutionException {
        // if high and low are equal, we only have 1 element which is per definition
        // sorted, and dont add any comparisons
        if (high == low)
            return 0;

        int mid = low + (high - low) / 2;

        int leftComparisons;
        int rightComparisons;

        if (high <= low + serialSortThreshold) {
            leftComparisons = sort(source, buffer, low, mid, serialSortThreshold);
            rightComparisons = sort(source, buffer, mid + 1, high, serialSortThreshold);
        } else {
            leftComparisons = pool.submit(() -> sortWithParallelMerge(source, buffer, low, mid, serialSortThreshold))
                    .join();

            rightComparisons = pool
                    .submit(() -> sortWithParallelMerge(source, buffer, mid + 1, high, serialSortThreshold)).join();
        }

        // int p = 2; // define p value??
        return leftComparisons + rightComparisons + mergeParallel(source, buffer, low, mid, high);
    }



    private static <T extends Comparable<T>> int mergeParallel(T[] source, T[] buffer, int low, int mid, int high) {

        // // serially merging subarrays that are smaller than the equal parts of the
        // // output array.
        // if ((mid + 1 - low) < equalPartsOfOutputArray) { // 4 should be the size of the equal part in the output array.
        //                                                  // 4 could be splitting 16 into 4
        //     return merge(source, buffer, low, mid, high);
        // }

        int n = high - low + 1; // Total size of the range to merge
        int chunkSize = (n + equalPartsOfOutputArray - 1) / equalPartsOfOutputArray; // Divide the range into p parts

        List<Future<Integer>> futures = new ArrayList<>();
        T[] leftSubarray = Arrays.copyOfRange(source, low, mid + 1);
        T[] rightSubarray = Arrays.copyOfRange(source, mid + 1, high + 1);

        for (int task = 0; task < equalPartsOfOutputArray; task++) {
            final int start = task * chunkSize;
            final int end = Math.min(n - 1, start + chunkSize - 1);
            futures.add(pool.submit(() -> {
                int comparisons = 0;
                for (int k = start; k <= end; k++) {
                    Integer[] twoSeq = TwoSequenceSelect.find((Integer[]) leftSubarray, (Integer[]) rightSubarray,
                            k);
                    int ia = twoSeq[0];
                    int ib = twoSeq[1];

                    if (ib >= rightSubarray.length
                            || (ia < leftSubarray.length && leftSubarray[ia].compareTo(rightSubarray[ib]) <= 0)) {
                        buffer[low + k] = leftSubarray[ia];
                    } else {
                        buffer[low + k] = rightSubarray[ib];
                    }
                    comparisons++;
                }
                return comparisons;
            }));
        }

        // Collect results
        int totalComparisons = 0;
        int count = 0;
        for (Future<Integer> future : futures) {
            count++;
            try {
                totalComparisons += future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        System.out.println(count + " Futures count");

        // Copy buffer back to source
        System.arraycopy(buffer, low, source, low, n);
        return totalComparisons;
    }

    public static <T extends Comparable<T>> int sortParallel(T[] source, T[] buffer, int low, int high,
            int insertionThreshold) {
        pool = new ForkJoinPool(6);
        return sort(source, buffer, low, high, insertionThreshold);
    }

    public static <T extends Comparable<T>> int sortParallel(T[] source, int insertionThreshold) {
        pool = new ForkJoinPool(6);
        T[] buffer = (T[]) new Comparable[source.length];
        return sort(source, buffer, 0, source.length - 1, insertionThreshold);
    }

    public static <T extends Comparable<T>> int sortParallelWithParallelMerge(T[] source, int low, int high,
            int insertionThreshold, int numOfAvailableThreads) throws InterruptedException, ExecutionException {
        if(source.length < 2) return 0;
        if (numOfAvailableThreads < 1) numOfAvailableThreads = 1;
        pool = new ForkJoinPool(numOfAvailableThreads);
        equalPartsOfOutputArray = source.length / numOfAvailableThreads;
        if(equalPartsOfOutputArray < 1) equalPartsOfOutputArray = 1;
        System.out.println(equalPartsOfOutputArray + " equalparts size");
		T[] buffer = (T[]) new Comparable[source.length];

        return sortWithParallelMerge(source, buffer, low, high, insertionThreshold);
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Integer[] arr = new Integer[] { 1, 2, 3, 4, 2, 3, 4, 5, 3, 4, 5, 6, 4, 5, 6, 0, 1, 0, 1 };
        int numOfAvailableThreads = 4;
        int comp = MergeSortParallel.sortParallelWithParallelMerge(arr, 0, arr.length - 1, 0,
                numOfAvailableThreads);
        System.out.println(comp);
        for (Integer i : arr)
            System.out.println(i);
    }
}