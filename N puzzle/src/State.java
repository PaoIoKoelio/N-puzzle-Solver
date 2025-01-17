import java.util.*;

public class State {
    private int n;
    private int[][] puzzle;
    private int g;
    private int h;
    private int f;
    private State parent;
    private int zeroRow;
    private int zeroCol;
    private Direction howDidWeGetHere;
    private int zeroIndex;

    public State(int[][] puzzle, int g, State parent, Direction howDidWeGetHere, int zeroIndex) {
        this.puzzle = puzzle;
        this.n = puzzle.length;
        this.g = g;
        this.parent = parent;
        locateZero();
        this.howDidWeGetHere = howDidWeGetHere;
        if (zeroIndex < 0) {
            this.zeroIndex = n * n - 1;
        } else {
            this.zeroIndex = zeroIndex;
        }
        this.h = calculateManhattanSum();
        this.f = this.h + this.g;
    }

    public int calculateManhattanSum() {
        int distance = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int value = puzzle[i][j];
                if (value != 0) {
                    int targetIndex = (value - 1);

                    if (targetIndex >= zeroIndex) {
                        targetIndex++;
                    }

                    int targetRow = targetIndex / n;
                    int targetCol = targetIndex % n;

                    distance += Math.abs(i - targetRow) + Math.abs(j - targetCol);
                }
            }
        }
        return distance;
    }

    private int countInversions(int[] flattened) {
        int invCounter = 0;
        for (int i = 0; i < n * n - 1; i++) {
            for (int j = i + 1; j < n * n; j++) {
                if (flattened[j] != 0 && flattened[i] != 0
                        && flattened[i] > flattened[j])
                    invCounter++;
            }
        }
        return invCounter;
    }

    public boolean isSolvable() {
        int counter = 0;
        int[] flattened = new int[n * n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                flattened[counter++] = puzzle[i][j];
            }
        }

        int invCount = countInversions(flattened);

        if (n % 2 == 1)
            return invCount % 2 == 0;
        else {
            int pos = zeroRow;
            if (pos % 2 == 1)
                return invCount % 2 == 0;
            else
                return invCount % 2 == 1;
        }
    }

    private void locateZero() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (puzzle[i][j] == 0) {
                    this.zeroRow = i;
                    this.zeroCol = j;
                    return;
                }
            }
        }
    }

    private boolean isValidPos(int row, int col) {
        return row < n && row >= 0 && col < n && col >= 0;
    }

    private int[][] copyPuzzle() {
        int[][] newPuzzle = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                newPuzzle[i][j] = puzzle[i][j];
            }
        }
        return newPuzzle;
    }

    private boolean isGoingBack(int n){
        Direction oppositeOfNewDirection = switch (n){
            case 0->Direction.UP;
            case 1->Direction.DOWN;
            case 2->Direction.LEFT;
            case 3->Direction.RIGHT;
            default -> null;
        };
        return oppositeOfNewDirection.equals(howDidWeGetHere);
    }

    public List<State> getKids() {
        List<State> kids = new ArrayList<>();
        int[] rowOffsets = {-1, 1, 0, 0};
        int[] colOffsets = {0, 0, -1, 1};
        for (int i = 0; i < 4; i++) {
            int newRow = zeroRow + rowOffsets[i];
            int newCol = zeroCol + colOffsets[i];

            if (isValidPos(newRow, newCol)&&!isGoingBack(i)) {
                int[][] newPuzzle = copyPuzzle();
                newPuzzle[zeroRow][zeroCol] = newPuzzle[newRow][newCol];
                newPuzzle[newRow][newCol] = 0;

                kids.add(new State(newPuzzle, g + 1, this, getDirection(i), zeroIndex));
            }
        }
        kids.sort(Comparator.comparingInt(State::getF));
        return kids;
    }


    public Direction getDirection(int n) {
        return switch (n) {
            case 0 -> Direction.DOWN;
            case 1 -> Direction.UP;
            case 2 -> Direction.RIGHT;
            case 3 -> Direction.LEFT;
            default -> null;
        };
    }

    public boolean isGoalState() {
        return h == 0;
    }

    public void printPuzzle() {
        for (int[] row : puzzle) {
            for (int tile : row) {
                System.out.print(tile + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof State)) return false;
        State other = (State) obj;
        return Arrays.deepEquals(this.puzzle, other.puzzle);
    }


    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.puzzle);
    }

    public Direction getHowDidWeGetHere() {
        return howDidWeGetHere;
    }

    public State getParent() {
        return parent;
    }

    public int getF() {
        return f;
    }
}
