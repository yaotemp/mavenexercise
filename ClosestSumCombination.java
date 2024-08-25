import java.util.*;

public class ClosestSumCombination {

    public static List<String> findClosestCombination(Map<String, Long> dictionary, long target) {
        long sumAllValues = dictionary.values().stream().mapToLong(Long::longValue).sum();
        long maxValue = Math.min(target + sumAllValues, target + 10000);  // Adjust the range as needed

        Map<Long, Long> dp = new HashMap<>();
        dp.put(0L, 0L);

        Map<Long, List<String>> combination = new HashMap<>();
        combination.put(0L, new ArrayList<>());

        for (long i = 1; i <= maxValue; i++) {
            List<String> bestCombination = null;
            long bestCount = Long.MAX_VALUE;

            for (Map.Entry<String, Long> entry : dictionary.entrySet()) {
                String key = entry.getKey();
                long value = entry.getValue();

                if (value <= i && dp.containsKey(i - value)) {
                    long newCount = dp.get(i - value) + 1;

                    if (newCount < bestCount) {
                        bestCount = newCount;
                        bestCombination = new ArrayList<>(combination.get(i - value));
                        bestCombination.add(key);
                    }
                }
            }

            if (bestCombination != null) {
                dp.put(i, bestCount);
                combination.put(i, bestCombination);

                // If we found an exact match, we can stop early
                if (i == target) {
                    return bestCombination;
                }
            }
        }

        // Finding the closest combination to the target upwards
        for (long i = target; i <= maxValue; i++) {
            if (combination.containsKey(i)) {
                return combination.get(i);
            }
        }

        return new ArrayList<>();
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

        long target = 21000L;
        System.out.println("Closest combination to " + target + " is: " + findClosestCombination(dictionary, target));
    }
}
