import java.util.*;

class State {
    String location;
    int roomA, roomB;
    int g; // number of actions taken
    int h; // number of dirty rooms
    int f; // g + h; total effort
    String actionTaken;
    State previousState;

    State(String location, int roomA, int roomB, int g, String actionTaken, State previousState) {
        this.location      = location;
        this.roomA         = roomA;
        this.roomB         = roomB;
        this.g             = g;
        this.h             = roomA + roomB;
        this.f             = g + h;
        this.actionTaken   = actionTaken;
        this.previousState = previousState;
    }

    String getKey() { return location + roomA + roomB; }

    @Override
    public String toString() {
        return "[" + actionTaken + " | Loc:" + location
             + " A:" + (roomA == 1 ? "Dirty" : "Clean")
             + " B:" + (roomB == 1 ? "Dirty" : "Clean")
             + " | g:" + g + " h:" + h + " f:" + f + "]";
    }
}

public class VacuumWorld {

    static List<State> getNextMoves(State s) {
        List<State> nextMoves = new ArrayList<>();
        int nextG = s.g + 1;

        if (s.location.equals("A")) {
            if (s.roomA == 1)
                nextMoves.add(new State("A", 0, s.roomB, nextG, "SUCK", s));
            nextMoves.add(new State("B", s.roomA, s.roomB, nextG, "MOVE_RIGHT", s));
        } else {
            if (s.roomB == 1)
                nextMoves.add(new State("B", s.roomA, 0, nextG, "SUCK", s));
            nextMoves.add(new State("A", s.roomA, s.roomB, nextG, "MOVE_LEFT", s));
        }
        return nextMoves;
    }

    static void showSolution(State goal) {
        List<State> path = new ArrayList<>();
        for (State cameFrom = goal; cameFrom != null; cameFrom = cameFrom.previousState)
            path.add(cameFrom);
        Collections.reverse(path);

        System.out.println("\n+==============================+");
        System.out.println("|         SOLUTION PATH        |");
        System.out.println("+==============================+");
        for (int i = 0; i < path.size(); i++)
            System.out.println("  Step " + i + ": " + path.get(i));
        System.out.println("  Total actions: " + (path.size() - 1));
    }

    static void showQueue(Collection<State> toVisit) {
        if (toVisit.isEmpty()) {
            System.out.println("  Queue is now empty.");
            return;
        }
        List<State> queueCopy = new ArrayList<>(toVisit);
        System.out.println("  Queue (" + queueCopy.size() + " waiting):");
        for (int i = 0; i < queueCopy.size(); i++)
            System.out.println("    [" + (i + 1) + "] " + queueCopy.get(i));
    }

    static void bfs(String loc, int a, int b) {
        System.out.println("\n+----------------------------------------+");
        System.out.println("|  STRATEGY: Breadth-First Search (BFS)  |");
        System.out.println("|  Type: UNINFORMED                      |");
        System.out.println("+----------------------------------------+");

        Queue<State> toVisit    = new LinkedList<>();
        Set<String> alreadySeen = new HashSet<>();

        State startState = new State(loc, a, b, 0, "START", null);
        toVisit.add(startState);

        int steps = 0;

        while (!toVisit.isEmpty()) {
            System.out.println("\n  --- Queue before picking ---");
            showQueue(toVisit);

            State thisState = toVisit.poll();

            if (alreadySeen.contains(thisState.getKey())) {
                System.out.println("  >> Skipping (already seen): " + thisState);
                continue;
            }
            alreadySeen.add(thisState.getKey());

            steps++;
            System.out.println("\n  >> Picked from queue: " + thisState);
            System.out.println("  >> Goal check: " + (thisState.h == 0 ? "GOAL!" : "Not yet (dirty rooms = " + thisState.h + ")"));

            if (thisState.h == 0) {
                System.out.println("\n>> BFS SUCCESS: All rooms are clean! ("
                                 + steps + " steps)");
                showSolution(thisState);
                return;
            }

            List<State> nextMoves = getNextMoves(thisState);
            System.out.println("  >> Adding to queue:");
            for (State next : nextMoves) {
                if (!alreadySeen.contains(next.getKey())) {
                    toVisit.add(next);
                    System.out.println("       + " + next);
                }
            }
            System.out.println("  --------------------------------------------------------------------------------");
        }
        System.out.println("\n>> BFS FAILURE: No solution found.");
    }

    static void aStar(String loc, int a, int b) {
        System.out.println("\n+--------------------------------------------------+");
        System.out.println("|  STRATEGY: A* Search                             |");
        System.out.println("|  Type: INFORMED                                  |");
        System.out.println("+--------------------------------------------------+");

        PriorityQueue<State> toVisit = new PriorityQueue<>(Comparator.comparingInt(s -> s.f));
        Set<String> alreadySeen      = new HashSet<>();

        State startState = new State(loc, a, b, 0, "START", null);
        toVisit.add(startState);

        int steps = 0;

        while (!toVisit.isEmpty()) {
            System.out.println("\n  --- Queue before picking (sorted by f) ---");
            showQueue(toVisit);

            State thisState = toVisit.poll();

            if (alreadySeen.contains(thisState.getKey())) {
                System.out.println("  >> Skipping (already seen): " + thisState);
                continue;
            }
            alreadySeen.add(thisState.getKey());

            steps++;
            System.out.println("\n  >> Picked from queue: " + thisState);
            System.out.println("     g=" + thisState.g
                             + " | h=" + thisState.h
                             + " | f=" + thisState.f);
            System.out.println("  >> Goal check: " + (thisState.h == 0 ? "GOAL!" : "Not yet (dirty rooms = " + thisState.h + ")"));

            if (thisState.h == 0) {
                System.out.println("\n>> A* SUCCESS: All rooms are clean! ("
                                 + steps + " steps)");
                showSolution(thisState);
                return;
            }

            List<State> nextMoves = getNextMoves(thisState);
            System.out.println("  >> Adding to queue:");
            for (State next : nextMoves) {
                if (!alreadySeen.contains(next.getKey())) {
                    toVisit.add(next);
                    System.out.println("       + " + next);
                }
            }
            System.out.println("  --------------------------------------------------------------------------------");
        }
        System.out.println("\n>> A* FAILURE: No solution found.");
    }

    public static void main(String[] args) {
        System.out.println("+======================================+");
        System.out.println("|          VACUUM WORLD SEARCH         |");
        System.out.println("+======================================+");

        try (Scanner sc = new Scanner(System.in)) {

            String loc = "";
            while (!loc.equals("A") && !loc.equals("B")) {
                System.out.print("\nEnter initial vacuum location (A or B): ");
                loc = sc.next().toUpperCase();
            }

            int a = -1;
            while (a != 0 && a != 1) {
                System.out.print("Is Room A dirty? (1=Yes, 0=No): ");
                if (sc.hasNextInt()) a = sc.nextInt();
                else sc.next();
            }

            int b = -1;
            while (b != 0 && b != 1) {
                System.out.print("Is Room B dirty? (1=Yes, 0=No): ");
                if (sc.hasNextInt()) b = sc.nextInt();
                else sc.next();
            }

            System.out.println("\n--- Initial State ---");
            System.out.println("Vacuum Location : Room " + loc);
            System.out.println("Room A          : " + (a == 1 ? "Dirty" : "Clean"));
            System.out.println("Room B          : " + (b == 1 ? "Dirty" : "Clean"));

            int choice = 0;
            while (choice < 1 || choice > 3) {
                System.out.println("\nSelect Search Strategy:");
                System.out.println("  [1] BFS  -- Uninformed (Breadth-First Search)");
                System.out.println("  [2] A*   -- Informed (A* Search)");
                System.out.println("  [3] Both -- Run BFS then A* for comparison");
                System.out.print("Enter choice (1/2/3): ");
                if (sc.hasNextInt()) choice = sc.nextInt();
                else sc.next();
            }

            switch (choice) {
                case 1:
                    bfs(loc, a, b);
                    break;
                case 2:
                    aStar(loc, a, b);
                    break;
                case 3:
                    bfs(loc, a, b);
                    aStar(loc, a, b);
                    break;
            }
        }
    }
}