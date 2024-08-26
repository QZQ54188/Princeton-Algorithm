import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {

    private static final int T = 256;

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        char[] charToIndex = new char[T];
        for (char i = 0; i < T; i++) {
            charToIndex[i] = i;
        }

        while (!BinaryStdIn.isEmpty()) {
            char ch = BinaryStdIn.readChar(8);
            int index = 0;
            for (; index < T; index++) {
                if (charToIndex[index] == ch) {
                    break;
                }
            }
            for (int i = index; i > 0; i--) {
                charToIndex[i] = charToIndex[i - 1];
            }
            charToIndex[0] = ch;
            BinaryStdOut.write(index, 8);
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        char[] indexToChar = new char[T];
        for (char i = 0; i < T; i++) {
            indexToChar[i] = i;
        }

        while (!BinaryStdIn.isEmpty()) {
            int index = BinaryStdIn.readChar();
            char ch = indexToChar[index];
            for (int i = index; i > 0; i--) {
                indexToChar[i] = indexToChar[i - 1];
            }
            indexToChar[0] = ch;
            BinaryStdOut.write(ch, 8);
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            encode();
        }
        else if (args[0].equals("+")) {
            decode();
        }
    }
}
