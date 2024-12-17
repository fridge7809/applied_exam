package org.aaa;

public class TwoSequenceSelect {

    public static Integer[] find(Integer[] a, Integer[] b, int k) {
        long low = Math.max(0, k - b.length); // Minimum elements taken from a
        long high = Math.min(k, a.length); // Maximum elements taken from a

        while (low < high) {
            long mid = (low + high) / 2;
            long ia = mid;
            long ib = k - ia;

            assert mid >= 0;

            // Validate indices and conditions
            if (ia > 0 && ib < b.length && a[(int) ia - 1] > b[(int) ib]) {
                // a[ia-1] is too large, reduce ia
                high = mid;
            } else if (ib > 0 && ia < a.length && b[(int) ib - 1] > a[(int) ia]) {
                // b[ib-1] is too large, increase ia
                low = mid + 1;
            } else {
                // Found valid partition
                return new Integer[] { (int) ia, (int) ib };
            }
        }

        // Final partition when low == high
        long ia = low;
        long ib = k - ia;
        return new Integer[] { (int) ia, (int) ib };
    }

    // public static <T extends Comparable<T>> int[] findInPlace(T[] source, int
    // start, int mid, int end, int k) {
    // assert start <= mid && mid < end;
    // int lengthLeft = mid + 1 - start; // Length of the left subarray
    // int lengthRight = end - mid; // Length of the right subarray

    // int low = Math.max(0, k - lengthRight); // Minimum valid value for ia
    // int high = Math.min(k, lengthLeft); // Maximum valid value for ia

    // while (low < high) {
    // int dynamicMid = (low + high) / 2;
    // int ia = mid;
    // int ib = k - ia;

    // // Validate indices and conditions
    // if (ia > 0 && ib < source.length && source[ia - 1].compareTo(source[ib]) > 0)
    // {
    // // a[ia-1] is too large, reduce ia
    // high = dynamicMid;
    // } else if (ib > 0 && ia < source.length && source[ib -
    // 1].compareTo(source[ia]) > 0) {
    // // b[ib-1] is too large, increase ia
    // low = dynamicMid + 1;
    // } else {
    // // Found valid partition
    // return new int[] { ia, ib };
    // }
    // }

    // // Final partition when low == high
    // int ia = low;
    // int ib = k - ia;
    // return new int[] { ia, ib };
    // }

    public static void main(String[] args) {
        // int[] a = { 1, 3, 5, 7 };
        // int[] b = { 2, 4, 5 };
        // int k = 5;

        int k = 2;
        Integer[] source = { 1, 5, 2, 4 };
        Integer[] source2 = { 1, 4, 5, 3, 4 };

        // int[] result = find(source2, 0, 2, 4, k);
        // System.out.println("ia: " + result[0] + ", ib: " + result[1]);
        // for (int i : result)
        // System.out.println(i);
    }
}
