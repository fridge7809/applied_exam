package org.aaa;

public class TwoSequenceSelect {

    public static int[] find(int[] a, int[] b, int k) {
        int low = Math.max(0, k - b.length); // Minimum elements taken from a
        int high = Math.min(k, a.length); // Maximum elements taken from a

        while (low < high) {
            int mid = (low + high) / 2;
            int ia = mid;
            int ib = k - ia;

            // Validate indices and conditions
            if (ia > 0 && ib < b.length && a[ia - 1] > b[ib]) {
                // a[ia-1] is too large, reduce ia
                high = mid;
            } else if (ib > 0 && ia < a.length && b[ib - 1] > a[ia]) {
                // b[ib-1] is too large, increase ia
                low = mid + 1;
            } else {
                // Found valid partition
                return new int[] { ia, ib };
            }
        }

        // Final partition when low == high
        int ia = low;
        int ib = k - ia;
        return new int[] { ia, ib };
    }

    public static void main(String[] args) {
        int[] a = { 1, 3, 5, 7 };
        int[] b = { 2, 4, 5 };
        int k = 5;

        int[] result = find(a, b, k);
        System.out.println("ia: " + result[0] + ", ib: " + result[1]);
        for (int i : result)
            System.out.println(i);
    }
}
