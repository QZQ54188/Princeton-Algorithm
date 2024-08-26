import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class CircularSuffixArray {

    private int size;
    private int[] index;
    private String text;


    // This class is a helper class for sorting loop suffix arrays
    private class CircularSuffix implements Comparable<CircularSuffix> {
        // Indicates that the cyclic suffix is at the beginning of the string
        private int offset;

        CircularSuffix(int i) {
            offset = i;
        }

        @Override
        public int compareTo(CircularSuffix o) {
            if (o == this) {
                return 0;
            }
            for (int i = 0; i < size; i++) {
                if (this.charAt(i) > o.charAt(i)) {
                    return 1;
                }
                else if (this.charAt(i) < o.charAt(i)) {
                    return -1;
                }
            }
            return 0;
        }

        private char charAt(int i) {
            return text.charAt(offset + i);
        }
    }

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        size = s.length();
        text = s + s;
        index = new int[size];
        for (int i = 0; i < size; i++) {
            index[i] = i;
        }

        CircularSuffix[] circularSuffixes = new CircularSuffix[size];
        // Record the index of the cyclic suffix string in the initial string
        for (int i = 0; i < size; i++) {
            circularSuffixes[i] = new CircularSuffix(i);
        }
        Arrays.sort(circularSuffixes);
        for (int i = 0; i < size; i++) {
            index[i] = circularSuffixes[i].offset;
        }
    }


    // length of s
    public int length() {
        return size;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= size) {
            throw new IllegalArgumentException();
        }
        return index[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        while (!StdIn.isEmpty()) {
            String s = StdIn.readString();
            CircularSuffixArray circularSuffixArray = new CircularSuffixArray(s);
            for (int i = 0; i < circularSuffixArray.length(); i++)
                StdOut.println(circularSuffixArray.index(i));
        }
    }

}
