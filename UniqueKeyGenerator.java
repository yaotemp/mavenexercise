import java.util.Random;

public class UniqueKeyGenerator {

    public static long generateUniqueKey() {
        Random rand = new Random();

        // Generate a random 17-digit number
        long seventeenDigits = (long) (rand.nextDouble() * 1_000_000_000_000_000_00L);

        // Combine with '9' as the leading digit to form an 18-digit number
        String uniqueKeyStr = "9" + String.format("%017d", seventeenDigits);

        // Convert the resulting string back to a long
        long uniqueKey = Long.parseLong(uniqueKeyStr);

        return uniqueKey;
    }

    public static void main(String[] args) {
        long uniqueKey = generateUniqueKey();
        System.out.println("Generated Key: " + uniqueKey);
    }
}
