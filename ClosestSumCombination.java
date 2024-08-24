import java.util.*;

public class ClosestSumCombination {

    public static List<String> findClosestCombination(Map<String, Long> dictionary, long target) {
        // 限制最大值为 target + 一个合理的范围，比如 dictionary 中所有值的和
        long sumAllValues = dictionary.values().stream().mapToLong(Long::longValue).sum();
        long maxValue = Math.min(target + sumAllValues, target + 10000);  // 根据具体情况调整最大范围

        long[] dp = new long[(int) (maxValue + 1)];
        Arrays.fill(dp, Long.MAX_VALUE);
        dp[0] = 0;

        List<String>[] combination = new List[(int) (maxValue + 1)];
        combination[0] = new ArrayList<>();

        for (int i = 1; i <= maxValue; i++) {
            combination[i] = new ArrayList<>();

            for (Map.Entry<String, Long> entry : dictionary.entrySet()) {
                String key = entry.getKey();
                long value = entry.getValue();

                if (value <= i && dp[(int) (i - value)] != Long.MAX_VALUE) {
                    long newCount = dp[(int) (i - value)] + 1;

                    if (newCount < dp[i]) {
                        dp[i] = newCount;
                        combination[i] = new ArrayList<>(combination[(int) (i - value)]);
                        combination[i].add(key);
                    }
                }
            }
        }

        // 查找从 target 到 0 的最近组合
        for (int i = (int) target; i >= 0; i--) {
            if (!combination[i].isEmpty()) {
                return combination[i];
            }
        }

        return new ArrayList<>();
    }

    public static void main(String[] args) {
        Map<String, Long> dictionary = new HashMap<>();
        dictionary.put("a", 2000L);
        dictionary.put("b", 4000L);
        dictionary.put("c", 8000L);

        long target = 21000L;
        System.out.println("Closest combination to " + target + " is: " + findClosestCombination(dictionary, target));
    }
}
