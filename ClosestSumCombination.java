import java.util.*;

public class ClosestSumCombination {

    public static List<String> findGreedyCombination(Map<String, Long> dictionary, long targetLength) {
        // Convert dictionary entries to a list and sort by value in descending order
        List<Map.Entry<String, Long>> sortedEntries = new ArrayList<>(dictionary.entrySet());
        sortedEntries.sort((a, b) -> Long.compare(a.getValue(), b.getValue()));

        List<String> combination = new ArrayList<>();
        long currentSum = 0;

        // Start adding from the largest possible value less than or equal to the target
        for (int i = sortedEntries.size() - 1; i >= 0; i--) {
            Map.Entry<String, Long> entry = sortedEntries.get(i);
            String key = entry.getKey();
            long value = entry.getValue();

            // Add the value as many times as possible without exceeding the target
            while (currentSum + value <= targetLength) {
                currentSum += value;
                combination.add(key);
            }

            // If we've reached the target, stop
            if (currentSum == targetLength) {
                return combination;
            }
        }

       

        return combination;
    }

    public static List<String> findCombination(Map<String, Long> dictionary, long att_length, int att_num) {
        // Calculate average attachment length
        long average_att_length = att_length / att_num;

        // Find greedy combination for the average attachment length
        List<String> averageCombination = findGreedyCombination(dictionary, average_att_length);

        // Calculate the remaining length after (att_num - 1) combinations
        long remainingLength = att_length - (calculateCombinationLength(dictionary, averageCombination) * (att_num - 1));

        // Find greedy combination for the remaining length
        List<String> remainingCombination = findGreedyCombination(dictionary, remainingLength);

        // Combine the results
        List<String> finalCombination = new ArrayList<>();

        // Add the average combination (att_num - 1) times
        for (int i = 0; i < att_num - 1; i++) {
            finalCombination.addAll(averageCombination);
        }

        // Add the remaining combination
        finalCombination.addAll(remainingCombination);

        return finalCombination;
    }

    // Helper method to calculate the total length of a combination
    private static long calculateCombinationLength(Map<String, Long> dictionary, List<String> combination) {
        long totalLength = 0;
        for (String key : combination) {
            totalLength += dictionary.get(key);
        }
        return totalLength;
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

        long target = 62139609L; // Change target to any desired value
        int att_num = 1; // Change the number of attachments to any desired value

        List<String> result = findCombination(dictionary, target, att_num);
        System.out.println("Combination to achieve target " + target + " with " + att_num + " attachments is: " + result);
    }
}
