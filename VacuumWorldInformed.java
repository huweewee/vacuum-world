import java.util.Scanner;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;

class State {
    String location;
    int roomA;
    int roomB;
    int h;
    String actionTaken;

    State(String location, int roomA, int roomB, String actionTaken) {
        this.location = location;
        this.roomA = roomA;
        this.roomB = roomB;
        this.h = roomA + roomB;
        this.actionTaken = actionTaken;
    }

    @Override
    public String toString() {
        return "[" + actionTaken + " -> Loc: " + location + ", A: " + roomA + ", B: " + roomB + ", h: " + h + "]";
    }
}

public class VacuumWorldInformed {

    public static void main(String[] args) {
        System.out.println("=== Vacuum World: Informed Search (Greedy Best-First) ===");

        try (Scanner input = new Scanner(System.in)) {
            String startLoc = "";
            while (!startLoc.equals("A") && !startLoc.equals("B")) {
                System.out.print("Enter Initial Location of Vacuum (A or B): ");
                startLoc = input.next().toUpperCase();
            }

            int startA = -1;
            while (startA != 0 && startA != 1) {
                System.out.print("Is Room A dirty? (1 for Yes, 0 for No): ");
                if (input.hasNextInt()) {
                    startA = input.nextInt();
                } else {
                    input.next();
                }
            }

            int startB = -1;
            while (startB != 0 && startB != 1) {
                System.out.print("Is Room B dirty? (1 for Yes, 0 for No): ");
                if (input.hasNextInt()) {
                    startB = input.nextInt();
                } else {
                    input.next();
                }
            }

            PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingInt(s -> s.h));
            List<String> visited = new ArrayList<>();

            State initialState = new State(startLoc, startA, startB, "START");
            queue.add(initialState);

            System.out.println("\n--- Starting Search Narrative ---");
            int steps = 0;

            while (!queue.isEmpty()) {
                System.out.println("Current Queue (Frontier): " + queue);
                
                State current = queue.poll();
                String stateKey = current.location + current.roomA + current.roomB;

                if (visited.contains(stateKey)) continue;
                visited.add(stateKey);

                System.out.println("Step " + steps + ": Agent chooses " + current);

                if (current.h == 0) {
                    System.out.println("\n>> SUCCESS: Goal reached. All rooms are clean!");
                    break;
                }

                if (current.location.equals("A")) {
                    if (current.roomA == 1) {
                        queue.add(new State("A", 0, current.roomB, "SUCK"));
                    }
                    queue.add(new State("B", current.roomA, current.roomB, "MOVE_RIGHT"));
                } else {
                    if (current.roomB == 1) {
                        queue.add(new State("B", current.roomA, 0, "SUCK"));
                    }
                    queue.add(new State("A", current.roomA, current.roomB, "MOVE_LEFT"));
                }

                steps++;
                System.out.println("----------------------------------------------");

                if (steps > 20) {
                    System.out.println("Search terminated: Too many steps.");
                    break;
                }
            }
        }
    }
}