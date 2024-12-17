package org.aaa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static org.aaa.Utils.isSorted;
import static org.aaa.Utils.less;

public class MergeSortParallel {

    private static ForkJoinPool pool;
    private static int parts;

    private static <T extends Comparable<T>> int sort(T[] source, T[] buffer, int low, int high, int serialSortThreshold) {
        if (high == low) {
            return 0;
        }

        int mid = low + (high - low) / 2;

        int leftComparisons;
        int rightComparisons;

        if (high <= low + serialSortThreshold) {
            leftComparisons = sort(source, buffer, low, mid, serialSortThreshold);
            rightComparisons = sort(source, buffer, mid + 1, high, serialSortThreshold);
        } else {
            leftComparisons = pool.submit(() -> sort(source, buffer, low, mid, serialSortThreshold)).join();
            rightComparisons = pool.submit(() -> sort(source, buffer, mid + 1, high, serialSortThreshold)).join();
        }

        return leftComparisons + rightComparisons + merge(source, buffer, low, mid, high);
    }

    private static <T extends Comparable<T>> int merge(T[] source, T[] buffer, int low, int mid, int high) {
        assert isSorted(source, low, mid);
        assert isSorted(source, mid + 1, high);
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
            } else if (less(buffer[j], buffer[i])) {
                source[k] = buffer[j++];
                comparisons++;
            } else {
                source[k] = buffer[i++];
                comparisons++;
            }
        }

        assert isSorted(source, low, high);
        return comparisons;
    }

    private static <T extends Comparable<T>> int sortWithParallelMerge(T[] source, T[] buffer, int low, int high, int serialSortThreshold) {
        if (high == low) {
            return 0;
        }

        int mid = low + (high - low) / 2;

        int leftComparisons;
        int rightComparisons;

        if (high <= low + serialSortThreshold) {
            leftComparisons = sort(source, buffer, low, mid, serialSortThreshold);
            rightComparisons = sort(source, buffer, mid + 1, high, serialSortThreshold);
        } else {
            leftComparisons = pool.submit(() -> sortWithParallelMerge(source, buffer, low, mid, serialSortThreshold)).join();
            rightComparisons = pool.submit(() -> sortWithParallelMerge(source, buffer, mid + 1, high, serialSortThreshold)).join();
        }

        // int p = 2; // define p value??
        return leftComparisons + rightComparisons + mergeParallel(source, buffer, low, mid, high);
    }

    private static <T extends Comparable<T>> int mergeParallel(T[] source, T[] buffer, int low, int mid, int high) {
        int n = high - low + 1; // Total size of the range to merge
        int chunkSize = (n + parts - 1) / parts; // Divide the range into p parts

        List<Future<Integer>> futures = new ArrayList<>();
        T[] leftSubarray = Arrays.copyOfRange(source, low, mid + 1);
        T[] rightSubarray = Arrays.copyOfRange(source, mid + 1, high + 1);

        for (int task = 0; task < parts; task++) {
            final int start = task * chunkSize;
            final int end = Math.min(n - 1, start + chunkSize - 1);
            futures.add(pool.submit(() -> {
                int comparisons = 0;
                for (int k = start; k <= end; k++) {
                    int[] ranks = TwoSequenceSelect.find(leftSubarray, rightSubarray, k);
                    int ia = ranks[0];
                    int ib = ranks[1];

                    if (ib >= rightSubarray.length || (ia < leftSubarray.length && leftSubarray[ia].compareTo(rightSubarray[ib]) <= 0)) {
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

        IntStream.range(low, n).parallel().forEach(i -> source[i] = buffer[i]);
        return totalComparisons;
    }

    public static <T extends Comparable<T>> int sortParallel(T[] source, T[] buffer, int low, int high,
            int serialSortThreshold) {
        pool = new ForkJoinPool(6);
        return sort(source, buffer, low, high, serialSortThreshold);
    }

    public static <T extends Comparable<T>> int sortParallel(T[] source, int serialSortThreshold) {
        pool = new ForkJoinPool(6);
        T[] buffer = (T[]) new Comparable[source.length];
        return sort(source, buffer, 0, source.length - 1, serialSortThreshold);
    }

    public static <T extends Comparable<T>> int sortParallelWithParallelMerge(T[] source, int serialSortThreshold, int threadCount) {
        if (source == null || source.length == 0) {
            throw new IllegalArgumentException("Array is null or empty");
        }
        if (source.length < 2) {
            return 0;
        }

        threadCount = Math.max(threadCount, 1);
        pool = new ForkJoinPool(threadCount);
        parts = Math.max(1, source.length / threadCount);

        System.out.println(parts + " equalparts size");

        T[] buffer = (T[]) new Comparable[source.length];

        return sortWithParallelMerge(source, buffer, 0, source.length - 1, serialSortThreshold);
    }

    public static void main(String[] args) {
        Double[] arr = new Double[]{1.1, 1.2, 1.19, 1.09, 1.10};
        int threadCount = 4;
        int comparisons = MergeSortParallel.sortParallelWithParallelMerge(arr, 0, threadCount);
        System.out.println(comparisons);
        for (Double i : arr) {
            System.out.println(i);
        }
    }
}