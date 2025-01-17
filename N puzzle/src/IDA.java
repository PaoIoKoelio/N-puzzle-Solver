import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class IDA {
    public static List<Direction> moves;
    public static List<State> path;
    public static State goalState;

    public static void solve(State intialState) {
        int threshold = intialState.getF();

        while (true) {
            int result = search(intialState, threshold);
            if (result == -1) {
                reconstructPath(goalState);
                return;
            }
            if (result == Integer.MAX_VALUE) {
                return;
            }
            threshold = result;
        }
    }

    private static int search(State state, int threshold) {
        if (state.getF() > threshold) {
            return state.getF();
        }
        if (state.isGoalState()) {
            goalState = state;
            return -1;
        }

        int minThreshold = Integer.MAX_VALUE;

        for (State neighbor : state.getKids()) {
            int result = search(neighbor, threshold);
            if (result == -1) {
                return -1;
            }
            minThreshold = Math.min(minThreshold, result);
        }
        return minThreshold;

    }

    private static void reconstructPath(State goal) {
        moves = new ArrayList<>();
        List<State> path = new ArrayList<>();
        int counter = 0;
        for (State state = goal; state.getParent() != null; state = state.getParent()) {
            path.add(state);
            moves.add(state.getHowDidWeGetHere());
            counter++;
        }
        System.out.println(counter);
        Collections.reverse(path);
        Collections.reverse(moves);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int n = scanner.nextInt();
        int zIndex = scanner.nextInt();

        int[][] initialPuzzle = new int[(int) Math.sqrt(n + 1)][(int) Math.sqrt(n + 1)];


        for (int i = 0; i < (int) Math.sqrt(n + 1); i++) {
            for (int j = 0; j < (int) Math.sqrt(n + 1); j++) {
                initialPuzzle[i][j] = scanner.nextInt();
            }
        }

        scanner.close();

        State state = new State(initialPuzzle, 0, null, null, zIndex);
        if (state.isSolvable()) {
            long startTime = System.nanoTime();

            IDA.solve(state);

            long endTime = System.nanoTime();

            long durationInMilliseconds = (endTime - startTime) / 1_000_000;
             System.out.println("Execution time: " + durationInMilliseconds + " ms");
            for (Direction direction : moves) {
                System.out.println(direction.toString().toLowerCase());
            }
        } else {
            System.out.println(-1);
        }
    }
}
