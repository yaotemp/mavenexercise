import java.util.*;

public class ClosestSumCombination {

    public static List<String> findGreedyCombination(Map<String, Long> dictionary, long target) {
        // Convert dictionary entries to a list and sort by value in descending order
        List<Map.Entry<String, Long>> sortedEntries = new ArrayList<>(dictionary.entrySet());
        sortedEntries.sort((a, b) -> Long.compare(a.getValue(), b.getValue()));  // Sort by value in descending order

        List<String> combination = new ArrayList<>();
        long currentSum = 0;

        // Start adding from the largest possible value less than or equal to the target
        for (int i = sortedEntries.size() - 1; i >= 0; i--) {
            Map.Entry<String, Long> entry = sortedEntries.get(i);
            String key = entry.getKey();
            long value = entry.getValue();

            // Add the value as many times as possible without exceeding the target
            while (currentSum + value <= target) {
                currentSum += value;
                combination.add(key);
            }

            // If we've reached the target, stop
            if (currentSum == target) {
                return combination;
            }
        }

        // Final adjustment step: check if adding one more of the smallest value exceeds the target but is closer
        Map.Entry<String, Long> smallestEntry = sortedEntries.get(0); // The smallest value entry
        String smallestKey = smallestEntry.getKey();
        long smallestValue = smallestEntry.getValue();

        // Check if adding one more smallest value makes the sum closer to the target, even if it exceeds the target
        if (Math.abs(target - (currentSum + smallestValue)) < Math.abs(target - currentSum)) {
            currentSum += smallestValue;
            combination.add(smallestKey);
        }

        return combination;
    }

    public static void main(String[] args) {
        Map<String, Long> dictionary = new HashMap<>();
        dictionary.put("a", 922L);
        dictionary.put("b", 2855L);
        dictionary.put("c", 4928L);
        dictionary.put("d", 8668L);
        dictionary.put("e", 16829L);
        dictionary.put("f", 32244L);
        dictionary.put("g", 221180L);
        dictionary.put("h", 62139609L);

        long target = 24500L; // Change target to any desired value
        System.out.println("Greedy combination to " + target + " is: " + findGreedyCombination(dictionary, target));
    }
}
