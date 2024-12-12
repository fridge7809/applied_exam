package org.aaa;

import java.util.concurrent.ForkJoinPool;

public class MergeSortParallel<T extends Comparable<T>> {

    private static ForkJoinPool pool;

    private static <T extends Comparable<T>> int sort(T[] source, T[] buffer, int low, int high,
            int insertionThreshold) {

        // if high and low are equal, we only have 1 element which is per definition sorted, and dont add any comparisons
        if(high == low) return 0;

        int mid = low + (high - low) / 2;

        int leftComparisons;
        int rightComparisons;

        if (high <= low + insertionThreshold) {
            System.out.println("Sorting sequentially");
            leftComparisons = sort(source, buffer, low, mid, insertionThreshold);
            rightComparisons = sort(source, buffer, mid + 1, high, insertionThreshold);
        } else {
            System.out.println("Sorting in parallel");

            leftComparisons = pool.submit(() -> sort(source, buffer, low, mid, insertionThreshold))
                    .join();

            rightComparisons = pool
                    .submit(() -> sort(source, buffer, mid + 1, high, insertionThreshold)).join();
        }

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
        return sort(source, buffer, low, high, insertionThreshold);
    }

    public static void main(String[] args) {
        Integer[] arr = new Integer[] { 5, 2, 8, 6, 9, 1, 3, 7 };
        int comp = MergeSortParallel.sortParallel(arr, new Integer[8], 0, arr.length - 1, 0);
        for (Integer i : arr) {
            System.out.println(i);
        }
        System.out.println("Comparisons: " + comp);
    }
}
