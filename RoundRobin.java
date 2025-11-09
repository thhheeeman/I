import java.util.Scanner;

public class RoundRobin {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();

        int[] pid = new int[n];      // Process IDs
        int[] at = new int[n];       // Arrival Time
        int[] bt = new int[n];       // Burst Time
        int[] rt = new int[n];       // Remaining Time
        int[] ct = new int[n];       // Completion Time
        int[] tat = new int[n];      // Turnaround Time
        int[] wt = new int[n];       // Waiting Time

        for (int i = 0; i < n; i++) {
            pid[i] = i + 1;
            System.out.print("\nEnter Arrival Time for P" + pid[i] + ": ");
            at[i] = sc.nextInt();
            System.out.print("Enter Burst Time for P" + pid[i] + ": ");
            bt[i] = sc.nextInt();
            rt[i] = bt[i]; 
        }

        System.out.print("\nEnter Time Quantum: ");
        int tq = sc.nextInt();

        int currentTime = 0, completed = 0;
        boolean allDone;

        while (completed < n) {
            allDone = true;
            for (int i = 0; i < n; i++) {
                if (at[i] <= currentTime && rt[i] > 0) {
                    allDone = false;
                    if (rt[i] > tq) {
                        currentTime += tq;
                        rt[i] -= tq;
                        System.out.print("P" + pid[i] + " | ");
                    } else {
                        currentTime += rt[i];
                        rt[i] = 0;
                        ct[i] = currentTime;
                        tat[i] = ct[i] - at[i];
                        wt[i] = tat[i] - bt[i];
                        completed++;
                        System.out.print("P" + pid[i] + " | ");
                    }
                }
            }

 
            if (allDone) {
                for (int i = 0; i < n; i++) {
                    if (rt[i] > 0) {
                        currentTime = at[i];
                        break;
                    }
                }
            }
        }

    
        System.out.println("\n\nProcess\tAT\tBT\tCT\tTAT\tWT");
        double totalTAT = 0, totalWT = 0;
        for (int i = 0; i < n; i++) {
            totalTAT += tat[i];
            totalWT += wt[i];
            System.out.println("P" + pid[i] + "\t" + at[i] + "\t" + bt[i] + "\t" + ct[i] + "\t" + tat[i] + "\t" + wt[i]);
        }

        System.out.printf("\nAverage Turnaround Time: %.2f", (totalTAT / n));
        System.out.printf("\nAverage Waiting Time: %.2f\n", (totalWT / n));

        sc.close();
    }
}
