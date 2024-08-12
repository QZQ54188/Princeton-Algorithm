import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Topological;

import java.util.TreeMap;

public class WordNet {

    /**
     * The function of synsetMap is to quickly find its synonyms given an ID.
     * The function of wordID is that given a noun, you can query which ID it is in,
     * and thus find all the synonym sets of this noun.
     * These two variables interact with each other to facilitate us to find
     * the synonym set of the word.
     */
    private final TreeMap<Integer, String> synsetMap;
    private final TreeMap<String, Bag<Integer>> wordID;
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException("The input file parameters cannot be empty.");
        }
        synsetMap = new TreeMap<>();
        wordID = new TreeMap<>();
        In inSyn = new In(synsets);
        while (!inSyn.isEmpty()) {
            String line = inSyn.readLine();
            String[] field = line.split(",");
            String[] words = field[1].split(" ");
            int id = Integer.parseInt(field[0]);
            for (String word : words) {
                if (wordID.containsKey(word)) {
                    wordID.get(word).add(id);
                }
                else {
                    Bag<Integer> bag = new Bag<>();
                    bag.add(id);
                    wordID.put(word, bag);
                }
            }
            synsetMap.put(id, field[1]);
        }

        Digraph digraph = new Digraph(synsetMap.size());
        In inHyp = new In(hypernyms);
        while (!inHyp.isEmpty()) {
            String line = inHyp.readLine();
            String[] field = line.split(",");
            int id = Integer.parseInt(field[0]);
            for (int i = 1; i < field.length; i++) {
                digraph.addEdge(id, Integer.parseInt(field[i]));
            }
        }

        // check
        int cnt = 0;
        for (int i = 0; i < digraph.V(); i++) {
            if (digraph.outdegree(i) == 0) {
                if (++cnt > 1) {
                    throw new IllegalArgumentException(
                            "There are multiple root nodes in this graph.");
                }
            }
        }

        Topological topological = new Topological(digraph);
        if (!topological.hasOrder()) {
            throw new IllegalArgumentException("There is a cycle in this graph.");
        }

        sap = new SAP(digraph);
    }


    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return wordID.keySet();
    }


    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException("The entered word cannot be null.");
        }
        return wordID.containsKey(word);
    }


    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException("The entered word cannot be null.");
        }
        return sap.length(wordID.get(nounA), wordID.get(nounB));
    }


    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException("The entered word cannot be null.");
        }
        int id = sap.ancestor(wordID.get(nounA), wordID.get(nounB));
        return synsetMap.get(id);
    }


    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
    }
}
