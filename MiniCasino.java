// BACAS, JAMES PHILIP
// DELA CRUZ, CHLOE MARIE

import java.util.Random;
import java.util.Scanner;

public class MiniCasino {
    static Random rand = new Random();
    static final int[] PAYOUTS = {0, 1, 5, 100};
    
    static int playMachine(double[] chances) {
        double r = rand.nextDouble();
        double sum = 0;
        for (int i = 0; i < 4; i++) {
            sum += chances[i];
            if (r < sum) return PAYOUTS[i];
        }
        return 0;
    }

    static double[] getValidChances(Scanner sc, String machineName) {
        while (true) {
            try {
                System.out.println("\nEnter " + machineName + " chances in % (must be 4 numbers summing to 100):");
                double[] chances = new double[4];
                double totalSum = 0;

                for (int i = 0; i < 4; i++) {
                    chances[i] = sc.nextDouble();
                    if (chances[i] < 0) {
                        throw new Exception("Negative value.");
                    }
                    totalSum += chances[i];
                }

                if (Math.abs(totalSum - 100.0) < 0.0001) {
                    for (int i = 0; i < 4; i++) chances[i] /= 100.0;
                    return chances;
                } else {
                    System.out.println("Invalid sum: " + totalSum + "%. Must be 100%.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please try again.");
                sc.nextLine();
            }
        }
    }
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        System.out.print("Starting money (Php): ");
        int money = sc.nextInt();
        System.out.print("Total play time (seconds): ");
        int time = sc.nextInt();
        
        double[] m1 = getValidChances(sc, "Machine 1");
        double[] m2 = getValidChances(sc, "Machine 2");
        
        double ev1 = (m1[1] * 1) + (m1[2] * 5) + (m1[3] * 100);
        double ev2 = (m2[1] * 1) + (m2[2] * 5) + (m2[3] * 100);
        
        double[] bestMachine;
        String chosenName;

        if (ev1 >= ev2) {
            bestMachine = m1;
            chosenName = "Machine 1";
        } else {
            bestMachine = m2;
            chosenName = "Machine 2";
        }

        int initialMoney = money;
        int timeUsed = 0;
        int playCount = 0;
        
        System.out.println("\n--- SIMULATION STARTING ---");
        System.out.println("Agent Strategy: Always play " + chosenName);
        
        while (timeUsed + 10 <= time && money >= 1) {
            playCount++;
            int result = playMachine(bestMachine);
            
            money = (money - 1) + result;
            timeUsed += 10;
            
            System.out.println("Play #" + playCount + " | Result: Php " + result + " | New Balance: Php " + money);
        }
        
        System.out.println("\n--- FINAL RESULTS ---");
        System.out.println("Final Money: Php " + money);
        System.out.println("Total Time Spent: " + timeUsed + "s");
        System.out.println("Net Profit/Loss: Php " + (money - initialMoney));
        
        sc.close();
    }
}