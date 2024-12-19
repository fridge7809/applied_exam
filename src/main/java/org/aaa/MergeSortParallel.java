package org.aaa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

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

        return leftComparisons + rightComparisons + mergeParallel(source, buffer, low, mid, high);
    }

    private static <T extends Comparable<T>> int mergeParallel(T[] source, T[] buffer, int low, int mid, int high) {
        // extracted these to package private methods for testing to ensure absense of off by ones (╯°□°)╯︵ ┻━┻

        int n = getN(low, high);
        int chunkSize = getChunkSize(n, parts);


        List<Callable<Integer>> tasks = new ArrayList<>();
        T[] leftSubarray = Arrays.copyOfRange(source, low, mid + 1);
        T[] rightSubarray = Arrays.copyOfRange(source, mid + 1, high + 1);

        if (n == 1) { // one element doesnt need merging
            buffer[0] = source[0];
            return 0;
        }

        if (n < 4) { // parallel merge for merging for 2 elements is overkill
            return merge(source, buffer, low, mid, high);
        }

        for (int task = 0; task < parts; task++) {
            final int start = task * chunkSize;
            final int end = Math.min(n - 1, start + chunkSize - 1);
            final int middle = (end + start) / 2;
            tasks.add(() -> {
                int comparisons = 0;
                for (int i = start; i < middle; i++) {
                    int ib = TwoSequenceSelect.find(leftSubarray, rightSubarray, i)[1];
                    int rank = i + ib;
                    buffer[rank] = source[i];
                    comparisons++;
                }
                return comparisons;
            });

            tasks.add(() -> {
                int comparisons = 0;
                for (int i = middle; i < end; i++) {
                    int ia = TwoSequenceSelect.find(leftSubarray, rightSubarray, i)[0];
                    int rank = i + ia;
                    buffer[rank] = source[i];
                    comparisons++;
                }
                return comparisons;
            });
        }


        // Collect results
        int totalComparisons = 0;
        int count = 0;
            try {
                List<Future<Integer>> results = pool.invokeAll(tasks);
                for (Future<Integer> future : results) {
                    count++;
                    totalComparisons += future.get();
                }
            } catch (InterruptedException | ExecutionException | AssertionError a) {
                a.printStackTrace();
            }
        System.out.println(count + " Futures count");

        System.arraycopy(buffer, low, source, low, n);
        return totalComparisons;
    }

    static int getChunkSize(int elements, int parts) {
        return (elements + parts - 1) / parts;
    }

    static int getN(int low, int high) {
        return high - low + 1;
    }

    public static <T extends Comparable<T>> int sortParallel(T[] source, T[] buffer, int low, int high,
            int serialSortThreshold) {
        pool = new ForkJoinPool(6);
        return sort(source, buffer, low, high, serialSortThreshold);
    }

    public static <T extends Comparable<T>> int sortParallel(T[] source, int serialSortThreshold) {
        pool = new ForkJoinPool();
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
        parts = getParts(source, threadCount);

        System.out.println(parts + " equalparts size");

        T[] buffer = (T[]) new Comparable[source.length];

        return sortWithParallelMerge(source, buffer, 0, source.length - 1, serialSortThreshold);
    }

    static <T extends Comparable<T>> int getParts(T[] source, int threadCount) {
        return Math.max(1, source.length / threadCount);
    }

    public static void main(String[] args) {
        Integer[] arr = new Integer[] {1,2,3,4,5,6,7,8, 9, 10, 11, 12, 13, 14, 15, 16};
        int threadCount = 4;
        int comparisons = MergeSortParallel.sortParallelWithParallelMerge(arr, 0, threadCount);
        System.out.println(Arrays.toString(arr));
    }
}