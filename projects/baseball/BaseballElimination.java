import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.TreeMap;

public class BaseballElimination {

    private final int numOfTeams;

    private final int[] wins;
    private final int[] loses;
    private final int[] remains;
    private final int[] games;

    private final TreeMap<Integer, String> indexToTeam;
    private final TreeMap<String, Integer> teamToIndex;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In in = new In(filename);
        numOfTeams = in.readInt();
        indexToTeam = new TreeMap<>();
        teamToIndex = new TreeMap<>();
        wins = new int[numOfTeams];
        loses = new int[numOfTeams];
        remains = new int[numOfTeams];
        games = new int[numOfTeams * numOfTeams];
        for (int i = 0; i < numOfTeams; i++) {
            String team = in.readString();
            indexToTeam.put(i, team);
            teamToIndex.put(team, i);
            wins[i] = in.readInt();
            loses[i] = in.readInt();
            remains[i] = in.readInt();
            for (int j = 0; j < numOfTeams; j++) {
                games[i * numOfTeams + j] = in.readInt();
            }
        }
    }

    // number of teams
    public int numberOfTeams() {
        return numOfTeams;
    }


    // all teams
    public Iterable<String> teams() {
        return teamToIndex.keySet();
    }


    private void validateTeam(String team) {
        if (team == null || !teamToIndex.containsKey(team)) {
            throw new IllegalArgumentException(
                    "The entered team does not meet the specifications.");
        }
    }


    // number of wins for given team
    public int wins(String team) {
        validateTeam(team);
        return wins[teamToIndex.get(team)];
    }


    // number of losses for given team
    public int losses(String team) {
        validateTeam(team);
        return loses[teamToIndex.get(team)];
    }


    // number of remaining games for given team
    public int remaining(String team) {
        validateTeam(team);
        return remains[teamToIndex.get(team)];
    }


    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        validateTeam(team1);
        validateTeam(team2);
        return games[teamToIndex.get(team1) * numOfTeams + teamToIndex.get(team2)];
    }

    private class Helper {
        private ArrayList<String> certificate;
        private boolean isEliminated;

        Helper(String team) {
            certificate = new ArrayList<>();
            isEliminated = false;

            if (numOfTeams == 1) {
                return;
            }
            if (isTrivialEliminated(team)) {
                return;
            }

            // Construct all nodes of the network flow algorithm
            int nV = nCr2(numOfTeams - 1) + numOfTeams + 1;
            FlowNetwork flowNetwork = new FlowNetwork(nV);
            int curQueryIndex = teamToIndex.get(team);
            int fullFlow = 0;
            TreeMap<Integer, Integer> teamIDToGraphID = new TreeMap<>();

            // Add a team node to the graph and connect it to the meeting point
            for (int i = 0, cnt = 0; i < numOfTeams; i++) {
                if (i == curQueryIndex) {
                    continue;
                }
                int gID = cnt++ + nCr2(numOfTeams - 1) + 1;
                teamIDToGraphID.put(i, gID);
                FlowEdge edge = new FlowEdge(gID, nV - 1,
                                             wins[curQueryIndex] + remains[curQueryIndex]
                                                     - wins[i]);
                flowNetwork.addEdge(edge);
            }

            for (int i = 0, index = 1; i < numOfTeams; i++) {
                if (i == curQueryIndex) {
                    continue;
                }
                for (int j = i + 1; j < numOfTeams; j++) {
                    if (j == curQueryIndex) {
                        continue;
                    }
                    FlowEdge edge = new FlowEdge(0, index, games[i * numOfTeams + j]);
                    fullFlow += games[i * numOfTeams + j];
                    FlowEdge edge1 = new FlowEdge(index, teamIDToGraphID.get(i),
                                                  Double.POSITIVE_INFINITY);
                    FlowEdge edge2 = new FlowEdge(index, teamIDToGraphID.get(j),
                                                  Double.POSITIVE_INFINITY);
                    index++;
                    flowNetwork.addEdge(edge);
                    flowNetwork.addEdge(edge1);
                    flowNetwork.addEdge(edge2);
                }
            }

            FordFulkerson ff = new FordFulkerson(flowNetwork, 0, nV - 1);
            for (int i = 0, cnt = 0; i < numOfTeams; i++) {
                if (i == curQueryIndex) {
                    continue;
                }
                int gID = cnt++ + nCr2(numOfTeams - 1) + 1;
                if (ff.inCut(gID)) {
                    certificate.add(indexToTeam.get(i));
                }
            }
            isEliminated = ff.value() < fullFlow;
        }


        // TrivialEliminated case
        private boolean isTrivialEliminated(String team) {
            int index = teamToIndex.get(team);
            int allWins = wins[index] + remains[index];
            for (int i = 0; i < numOfTeams; i++) {
                if (allWins < wins[i]) {
                    certificate.add(indexToTeam.get(i));
                    isEliminated = true;
                    return true;
                }
            }
            return false;
        }

        private int nCr2(int k) {
            return k * (k - 1) / 2;
        }
    }


    // is given team eliminated?
    public boolean isEliminated(String team) {
        validateTeam(team);
        Helper ff = new Helper(team);
        return ff.isEliminated;
    }


    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        validateTeam(team);
        Helper ff = new Helper(team);
        if (ff.certificate.isEmpty()) {
            return null;
        }
        return ff.certificate;
    }


    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
