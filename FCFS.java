import java.util.Scanner;

public class FCFS {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.print("Enter the number of processes: ");
        int n = in.nextInt();

        int pid[] = new int[n];    // Process IDs
        int at[] = new int[n];     // Arrival Time
        int bt[] = new int[n];     // Burst Time
        int ct[] = new int[n];     // Completion Time
        int tat[] = new int[n];    // Turnaround Time
        int wt[] = new int[n];     // Waiting Time

        for (int i = 0; i < n; i++) {
            System.out.print("\nEnter Arrival Time for Process " + (i + 1) + ": ");
            at[i] = in.nextInt();

            System.out.print("Enter Burst Time for Process " + (i + 1) + ": ");
            bt[i] = in.nextInt();

            pid[i] = i + 1;
        }


        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (at[i] > at[j]) {
                    int temp;

                    temp = at[i]; at[i] = at[j]; at[j] = temp;
                    temp = bt[i]; bt[i] = bt[j]; bt[j] = temp;
                    temp = pid[i]; pid[i] = pid[j]; pid[j] = temp;
                }
            }
        }


        ct[0] = at[0] + bt[0];
        for (int i = 1; i < n; i++) {
            if (at[i] > ct[i - 1])
                ct[i] = at[i] + bt[i]; 
            else
                ct[i] = ct[i - 1] + bt[i];
        }

    
        double totalTAT = 0, totalWT = 0;
        for (int i = 0; i < n; i++) {
            tat[i] = ct[i] - at[i];
            wt[i] = tat[i] - bt[i];
            totalTAT += tat[i];
            totalWT += wt[i];
        }


        System.out.println("\nProcess\tAT\tBT\tCT\tTAT\tWT");
        for (int i = 0; i < n; i++) {
            System.out.println("P" + pid[i] + "\t" + at[i] + "\t" + bt[i] + "\t" +
                    ct[i] + "\t" + tat[i] + "\t" + wt[i]);
        }


        System.out.printf("\nAverage Turnaround Time: %.2f", (totalTAT / n));
        System.out.printf("\nAverage Waiting Time: %.2f", (totalWT / n));


        System.out.print("\n\nGantt Chart: ");
        for (int i = 0; i < n; i++) {
            System.out.print("| P" + pid[i] + " ");
        }
        System.out.println("|");
    }
}