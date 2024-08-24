import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;

public class BoggleSolver {

    private static class TrieNode {
        private boolean isWord = false;
        private TrieNode[] children = new TrieNode[26];
    }

    private final TrieNode root;
    private static final int[][] dirs = {
            { -1, -1 }, { -1, 0 }, { -1, 1 },
            { 0, 1 }, { 0, -1 },
            { 1, 1 }, { 1, 0 }, { 1, -1 }
    };

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        root = new TrieNode();
        for (String s : dictionary) {
            put(root, s);
        }
    }

    private void put(TrieNode root, String s) {
        for (char ch : s.toCharArray()) {
            if (root.children[ch - 'A'] == null) {
                root.children[ch - 'A'] = new TrieNode();
            }
            root = root.children[ch - 'A'];
        }
        root.isWord = true;
    }


    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        HashSet<String> hashSet = new HashSet<>();
        boolean[][] isVisited = new boolean[board.rows()][board.cols()];
        StringBuilder stringBuilder = new StringBuilder();

        // Continue a depth-first search for each position in the table
        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                dfs(root, board, i, j, stringBuilder, isVisited, hashSet);
            }
        }
        return hashSet;
    }


    /**
     * When performing a depth-first search on the Boggle game board,
     * use a prefix tree to quickly determine whether the current path
     * is likely to form a word. If the path is a valid prefix, continue searching,
     * otherwise prune.
     */
    private void dfs(TrieNode root, BoggleBoard board, int row, int col, StringBuilder stringBuilder
            , boolean[][] isVisited, HashSet<String> hashSet) {
        char ch = board.getLetter(row, col);
        TrieNode child = root.children[ch - 'A'];
        if (child == null) {
            return;
        }

        if (ch == 'Q') {
            child = child.children['U' - 'A'];
            if (child == null) {
                return;
            }
        }

        stringBuilder.append(ch);
        if (ch == 'Q') {
            stringBuilder.append('U');
        }
        isVisited[row][col] = true;

        if (stringBuilder.length() >= 3 && child.isWord) {
            hashSet.add(stringBuilder.toString());
        }

        for (int[] dir : dirs) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (validateBoard(board, newRow, newCol) && !isVisited[newRow][newCol]) {
                dfs(child, board, newRow, newCol, stringBuilder, isVisited, hashSet);
            }
        }

        isVisited[row][col] = false;
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        if (ch == 'Q') {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
    }


    private boolean validateBoard(BoggleBoard board, int row, int col) {
        return col >= 0 && col < board.cols() && row >= 0 && row < board.rows();
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (!get(root, word)) {
            return 0;
        }
        int length = word.length();
        if (length < 3) return 0;
        else if (length <= 4) return 1;
        else if (length <= 6) return length - 3;
        else if (length == 7) return 5;
        else return 11;
    }

    private boolean get(TrieNode root, String word) {
        for (char ch : word.toCharArray()) {
            root = root.children[ch - 'A'];
            if (root == null) {
                return false;
            }
        }
        return root.isWord;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }

}
