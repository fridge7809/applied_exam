package org.aaa;

public class TwoSequenceSelect {

    public static <T extends Comparable<T>> int[] find(T[] a, T[] b, int k) {
        assert k <= a.length + b.length;
        assert k >= 0;
        assert isSorted(a);
        assert isSorted(b);

        long low = Math.max(0, k - b.length); // Minimum elements taken from a
        long high = Math.min(k, a.length); // Maximum elements taken from a

        while (low <= high) {
            long mid = (low + high) / 2;
            long ia = mid;
            long ib = k - ia;

            assert mid >= 0;

            // Validate indices and conditions
            boolean iaIsTooLarge = ia > 0 && ib < b.length && (a[(int) ia - 1].compareTo(b[(int) ib])) > 0;
            boolean ibIsTooLarge = ib > 0 && ia < a.length && b[(int) ib - 1].compareTo(a[(int) ia]) > 0;

            if (iaIsTooLarge) {
                high = mid;
            } else if (ibIsTooLarge) {
                low = mid + 1;
            } else {
                assert ia + ib == k;
                return new int[]{(int) ia, (int) ib};
            }
        }

        long ia = low;
        long ib = k - ia;

        assert ia + ib == k;
        return new int[]{(int) ia, (int) ib};
    }

    private static <T extends Comparable<T>> boolean isSorted(T[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i].compareTo(array[i - 1]) < 0) {
                return false;
            }
        }
        return true;
    }

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
