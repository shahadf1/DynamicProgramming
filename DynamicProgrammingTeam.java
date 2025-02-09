import java.io.File;
import java.util.*;

class Employee {
    int id;
    String name;
    int skillLevel;
    int parentId; // ID of the supervisor

    public Employee(int id, String name, int skillLevel, int parentId) {
        this.id = id;
        this.name = name;
        this.skillLevel = skillLevel;
        this.parentId = parentId;
    }
}

public class DynamicProgrammingTeam {
    private static List<Employee> employees = new ArrayList<>();
    private static List<Employee> bestTeam = new ArrayList<>();
    private static int maxSkillLevel = 0;

    public static void main(String[] args) {
        readEmployeesFromFile("example.txt");
        findBestTeam(2); // Maximum team size = 2
        printResult();
    }

    // Read employees from a file
    private static void readEmployeesFromFile(String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(" : ");
                int parentId = Integer.parseInt(parts[0]);
                String name = parts[1];
                int id = Integer.parseInt(parts[2]);
                int skillLevel = Integer.parseInt(parts[3]);
                employees.add(new Employee(id, name, skillLevel, parentId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Find the best team of given size using dynamic programming
    private static void findBestTeam(int maxTeamSize) {
        int n = employees.size();
        int[][] dp = new int[n + 1][maxTeamSize + 1];
        int[][] prev = new int[n + 1][maxTeamSize + 1]; // To track the team

        // Initialize DP table
        for (int i = 0; i <= n; i++) {
            Arrays.fill(dp[i], 0);
            Arrays.fill(prev[i], -1);
        }

        // Fill DP table
        for (int i = 1; i <= n; i++) {
            Employee current = employees.get(i - 1);
            for (int j = 1; j <= maxTeamSize; j++) {
                // Exclude current employee
                dp[i][j] = dp[i - 1][j];
                prev[i][j] = -1;

                // Include current employee if valid
                for (int k = i - 1; k > 0; k--) {
                    Employee other = employees.get(k - 1);
                    if (isValidPair(current, other)) {
                        int skillWithCurrent = dp[k][j - 1] + current.skillLevel + other.skillLevel;
                        if (skillWithCurrent > dp[i][j]) {
                            dp[i][j] = skillWithCurrent;
                            prev[i][j] = k; // Track the previous employee in the team
                        }
                    }
                }
            }
        }

        // Find the maximum skill level
        maxSkillLevel = dp[n][maxTeamSize];

        // Reconstruct the best team
        int i = n, j = maxTeamSize;
        while (i > 0 && j > 0) {
            if (prev[i][j] != -1) {
                bestTeam.add(employees.get(i - 1));
                bestTeam.add(employees.get(prev[i][j] - 1));
                j -= 2;
            }
            i--;
        }
    }

    // Check if two employees form a valid pair
    private static boolean isValidPair(Employee emp1, Employee emp2) {
        if (emp1.parentId == emp2.id || emp2.parentId == emp1.id) return false;
        if (emp1.parentId != 0 && emp1.parentId == emp2.parentId) return false;
        return true;
    }

    // Print the result
    private static void printResult() {
        System.out.println("Optimal Team:");
        for (Employee emp : bestTeam) {
            System.out.print(emp.name + " ");
        }
        System.out.println("\nTotal Skill Level: " + maxSkillLevel);

        // Skill difference
        int skillDifference = Math.abs(bestTeam.get(0).skillLevel - bestTeam.get(1).skillLevel);
        System.out.println("Skill Difference: " + skillDifference);
    }
}