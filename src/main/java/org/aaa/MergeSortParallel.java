package org.aaa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

public class MergeSortParallel<T extends Comparable<T>> {

    private static ForkJoinPool pool;

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

            rightComparisons = pool
                    .submit(() -> sort(source, buffer, mid + 1, high, insertionThreshold)).join();
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
            int insertionThreshold) throws InterruptedException, ExecutionException {
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
            leftComparisons = pool.submit(() -> sortWithParallelMerge(source, buffer, low, mid, insertionThreshold))
                    .join();

            rightComparisons = pool
                    .submit(() -> sortWithParallelMerge(source, buffer, mid + 1, high, insertionThreshold)).join();
        }

        // int p = 2; // define p value??
        return leftComparisons + rightComparisons + mergeParallel2(source, buffer, low, mid, high);
    }


    /*
     * MergeParallel idea:
     * source of 8 elements
     * Split merging of the two subarrays up into 2 different tasks by using
     * twoseqselect
     * These tasks should not update the source array, but rather put the merged
     * array of 4 elements into an array of arrays at the position of the task id?
     * This problem might already be solved by using buffer in merge??? but im not
     * sure.
     */

    private static <T extends Comparable<T>> int mergeParallel(T[] source, T[] buffer, int low, int mid, int high)
            throws InterruptedException, ExecutionException {
        int p = 4;
        int n = high - low + 1; // Size of the result array part
        int chunkSize = (n + p - 1) / p; // Divide output into p parts
        Future<Integer>[] futures = new Future[p];

        // int k = Math.max(1, (high - low) / p);

        System.out.println(low + " " + high + " ");

        int lengthA = mid + 1 - low;
        int lengthB = high + 1 - low - lengthA;
        Integer[] bufferA = new Integer[lengthA];
        Integer[] bufferB = new Integer[lengthB];

        for (int i = low; i <= mid; i++) {
            System.arraycopy(source, i, bufferA, 0, lengthA);
            System.arraycopy(source, i + lengthA, bufferB, 0, lengthB);
            Integer r = i - low + TwoSequenceSelect.find(bufferA, bufferB, (int) source[i])[1];
            buffer[r] = source[i];
        }

        for (int i = mid + 1; i <= high; i++) {
            System.arraycopy(source, i, bufferA, 0, lengthA);
            System.arraycopy(source, i + lengthA, bufferB, 0, lengthB);
            Integer r = TwoSequenceSelect.find(bufferA, bufferB, (int) source[i])[0] + i - mid - 1;
            buffer[r] = source[i];
        }

        System.out.println(Arrays.toString(buffer) + " lowhigh: " + low + " " + high);

        /*
         * For each iteration; each paralle merge we need to:
         * run twoseqselect for n / p (chunk), to merge in parallel.
         * This gives us 1 elemtn from a and 3 frmo b.
         * Then increment an offset, that specifies that for the next iteration of the
         * for loop, we ignore first 1 element and first 3 elements
         * Then we submit each merge to the pool
         */

        // for (int i = 0; i < p; i++) {
        // int lower = i * chunkSize; // lower bound of sorted output array
        // int higher = (i + 1) * chunkSize; // higher bound of sorted output array
        // (MATH.MIN OF SOMETHING??)

        // // int lengthA = mid + 1 - low;
        // // int lengthB = high + 1 - low - lengthA;
        // Integer[] bufferA = new Integer[higher-lower];
        // Integer[] bufferB = new Integer[higher-lower];

        // System.arraycopy(source, lower, bufferA, 0, higher-lower);
        // System.arraycopy(source, higher, bufferB, 0, higher-lower);

        // Integer[] twoSeqSelect = TwoSequenceSelect.find(bufferA, bufferB, k);

        // System.out.println("twoseq: " + Arrays.toString(twoSeqSelect));

        // /*
        // * int startLeft = low + (i == 0 ? 0 : twoSeqSelect[0]);
        // * int endLeft = low + (i == 0 ? 0 : twoSeqSelect[0] - 1);
        // * int startRight = mid + 1 + (i == 0 ? 0 : twoSeqSelect[1] - 1);
        // * int endRight = mid + 1 + (i == 0 ? 0 : twoSeqSelect[1] - 1);
        // *
        // * futures[i] = pool.submit(() -> mergeByTwoSeqSelect(source, buffer,
        // startLeft,
        // * endLeft,
        // * startRight, endRight));
        // */
        // }

        // // Collect results
        int totalComparisons = 0;
        // for (Future<Integer> future : futures) {
        // totalComparisons += future.get();
        // }

        // pool.shutdown();
        return totalComparisons;
    }

    private static <T extends Comparable<T>> int mergeParallel2(T[] source, T[] buffer, int low, int mid, int high)
            throws InterruptedException, ExecutionException {
        int p = 4; // Number of parallel tasks
        int n = high - low + 1; // Total size of the range to merge
        int chunkSize = (n + p - 1) / p; // Divide the range into p parts

        List<Future<Integer>> futures = new ArrayList<>();
        T[] leftSubarray = Arrays.copyOfRange(source, low, mid + 1);
        T[] rightSubarray = Arrays.copyOfRange(source, mid + 1, high + 1);

        for (int task = 0; task < p; task++) {
            final int start = task * chunkSize;
            final int end = Math.min(n - 1, start + chunkSize - 1);
            futures.add(pool.submit(() -> {
                int comparisons = 0;
                for (int k = start; k <= end; k++) {
                    Integer[] positions = TwoSequenceSelect.find((Integer[]) leftSubarray, (Integer[]) rightSubarray,
                            k);
                    int ia = positions[0];
                    int ib = positions[1];

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
            totalComparisons += future.get();
        }
        System.out.println(count);

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

    public static <T extends Comparable<T>> int sortParallelWithParallelMerge(T[] source, T[] buffer, int low, int high,
            int insertionThreshold) throws InterruptedException, ExecutionException {
        pool = new ForkJoinPool(6);
        return sortWithParallelMerge(source, buffer, low, high, insertionThreshold);
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Integer[] arr = new Integer[] { 1, 2, 3, 4, 2, 3, 4, 5, 3, 4, 5, 6, 4, 5, 6, 7 };
        // int comp = MergeSortParallel.sortParallel(arr, new Integer[8], 0, arr.length
        // - 1, 0);
        // for (Integer i : arr) {
        // System.out.println(i);
        // }
        int comp = MergeSortParallel.sortParallelWithParallelMerge(arr, new Integer[16], 0, arr.length - 1, 0);
        System.out.println(comp);
        for (Integer i : arr)
            System.out.println(i);
    }
}