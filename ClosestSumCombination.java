import java.util.*;

public class ClosestSumCombination {

    public static List<String> findClosestCombination(Map<String, Integer> dictionary, int target) {
        int maxValue = target + Collections.max(dictionary.values());

        // dp[i]表示到达和i所需的最少字典项数
        int[] dp = new int[maxValue + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;

        List<String>[] combination = new List[maxValue + 1];
        combination[0] = new ArrayList<>();

        // 遍历所有可能的和从 1 到最大值
        for (int i = 1; i <= maxValue; i++) {
            combination[i] = new ArrayList<>();

            // 检查每个字典项
            for (Map.Entry<String, Integer> entry : dictionary.entrySet()) {
                String key = entry.getKey();
                int value = entry.getValue();

                if (value <= i && dp[i - value] != Integer.MAX_VALUE) {
                    int newCount = dp[i - value] + 1;

                    // 更新 dp[i] 和 combination[i] 为更优的情况
                    if (newCount < dp[i]) {
                        dp[i] = newCount;
                        combination[i] = new ArrayList<>(combination[i - value]);
                        combination[i].add(key);
                    }
                }
            }
        }

        // 从目标值开始，往下找到最接近目标值且使用字典项最少的组合
        for (int i = target; i >= 0; i--) {
            if (!combination[i].isEmpty()) {
                return combination[i];
            }
        }

        return new ArrayList<>();  // 如果找不到任何组合返回空列表
    }

    public static void main(String[] args) {
        Map<String, Integer> dictionary = new HashMap<>();
        dictionary.put("a", 2000);
        dictionary.put("b", 4000);
        dictionary.put("c", 8000);

        int target = 50000;
        System.out.println("最接近 " + target + " 的组合是: " + findClosestCombination(dictionary, target));
    }
}
