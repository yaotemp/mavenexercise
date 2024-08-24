import java.util.*;

public class ClosestSumCombination {

    public static List<String> findClosestCombination(Map<String, Integer> dictionary, int target) {
        int maxValue = target + Collections.max(dictionary.values());

        // dp[i] represents the minimum number of dictionary items needed to reach sum i
        int[] dp = new int[maxValue + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;

        List<String>[] combination = new List[maxValue + 1];
        combination[0] = new ArrayList<>();

        // Iterate over all possible sums from 1 to maxValue
        for (int i = 1; i <= maxValue; i++) {
            combination[i] = new ArrayList<>();

            // Check each dictionary item
            for (Map.Entry<String, Integer> entry : dictionary.entrySet()) {
                String key = entry.getKey();
                int value = entry.getValue();

                if (value <= i && dp[i - value] != Integer.MAX_VALUE) {
                    int newCount = dp[i - value] + 1;

                    // If the new combination has fewer items, update dp[i] and combination[i]
                    if (newCount < dp[i]) {
                        dp[i] = newCount;
                        combination[i] = new ArrayList<>(combination[i - value]);
                        combination[i].add(key);
                    }
                }
            }
        }

        // Start from the target value and find the closest combination with the fewest items
        for (int i = target; i >= 0; i--) {
            if (!combination[i].isEmpty()) {
                return combination[i];
            }
        }

        return new ArrayList<>();  // Return an empty list if no combination is found
    }

    public static void main(String[] args) {
        Map<String, Integer> dictionary = new HashMap<>();
        dictionary.put("a", 2000);
        dictionary.put("b", 4000);
        dictionary.put("c", 8000);

        int target = 28500;
        System.out.println("Closest combination to " + target + " is: " + findClosestCombination(dictionary, target));
    }
}
