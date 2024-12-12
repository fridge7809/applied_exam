package org.aaa;

import java.util.concurrent.ForkJoinPool;

public class MergeSortParallel<T extends Comparable<T>> {

    private static ForkJoinPool pool;

    private static <T extends Comparable<T>> int sortParallelRecursive(T[] source, T[] buffer, int low, int high,
            int insertionThreshold) {
        System.out.println("Thread in use: " + Thread.currentThread().getName());

        if (high <= low + insertionThreshold) {
            return InsertionSort.sort(source, low, high);
        }

        int mid = low + (high - low) / 2;

        // ForkJoinPool pool = ForkJoinPool.commonPool();
        int leftComparisons = pool.submit(() -> sortParallelRecursive(source, buffer, low, mid, insertionThreshold))
                .join();
        // System.out.println(leftComparisons + " left");
        int rightComparisons = pool
                .submit(() -> sortParallelRecursive(source, buffer, mid + 1, high, insertionThreshold)).join();
        // System.out.println(rightComparisons + " right");

        return leftComparisons + rightComparisons + mergeParallel(source, buffer, low, mid, high);
    }

    private static <T extends Comparable<T>> int mergeParallel(T[] source, T[] buffer, int low, int mid, int high) {
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
                System.out.println("Comparing: " + buffer[j] + " - " + buffer[i]);
                source[k] = buffer[j++];
                comparisons++;
            } else {
                System.out.println("Comparing: " + buffer[j] + " - " + buffer[i]);
                source[k] = buffer[i++];
                comparisons++;
            }
        }

        // System.out.println(comparisons + " merge");
        // Postcondition
        assert Utils.isSorted(source, low, high);
        return comparisons;
    }

    public static <T extends Comparable<T>> int sortParallel(T[] source, T[] buffer, int low, int high,
            int insertionThreshold) {
        pool = new ForkJoinPool(100);
        return sortParallelRecursive(source, buffer, low, high, insertionThreshold);
    }

    public static void main(String[] args) {
        Integer[] arr = new Integer[] { 5, 2, 8, 6, 9, 1, 3, 7 };
        int comp = MergeSortParallel.sortParallel(arr, new Integer[24], 0, arr.length - 1, 0);
        for (Integer i : arr) {
            System.out.println(i);
        }
        System.out.println("Comparisons: " + comp);

        // Integer[] arr2 = new Integer[] { 5, 2, 8, 6, 9, 1, 3, 7, 0, 4 };
        // System.out.println(MergeSort.sort(arr2));

    }
}
