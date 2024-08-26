import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {

    private static final int T = 256;

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String input = BinaryStdIn.readString();
        CircularSuffixArray circularSuffixArray = new CircularSuffixArray(input);
        int size = circularSuffixArray.length();
        char[] t = new char[size];
        int first = 0;

        for (int i = 0; i < size; i++) {
            if (circularSuffixArray.index(i) == 0) {
                first = i;
            }
            t[i] = input.charAt((circularSuffixArray.index(i) - 1 + size) % size);
        }

        BinaryStdOut.write(first);
        for (char ch : t) {
            BinaryStdOut.write(ch, 8);
        }
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        char[] t = BinaryStdIn.readString().toCharArray();
        int[] next = computeNext(t);
        int size = t.length;

        while (size-- > 0) {
            BinaryStdOut.write(t[next[first]]);
            first = next[first];
        }
        BinaryStdOut.close();
    }


    private static int[] computeNext(char[] t) {
        int size = t.length;
        int[] next = new int[size];
        int[] count = new int[T + 1];
        for (int i = 0; i < size; i++) {
            count[t[i] + 1]++;
        }

        for (int i = 0; i < T; i++) {
            count[i + 1] += count[i];
        }

        for (int i = 0; i < size; i++) {
            next[count[t[i]]++] = i;
        }
        return next;
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            transform();
        }
        else if (args[0].equals("+")) {
            inverseTransform();
        }
    }
}
