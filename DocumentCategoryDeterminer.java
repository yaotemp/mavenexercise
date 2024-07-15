public static class DocumentLengthCategories {
    private int smallThreshold;
    private int mediumThreshold;

    public int getSmallThreshold() {
        return smallThreshold;
    }

    public int getMediumThreshold() {
        return mediumThreshold;
    }
}


public class DocumentCategoryDeterminer {
    public static String determineCategory(int docLength, Config config) {
        int smallThreshold = config.getDocumentLengthCategories().getSmallThreshold();
        int mediumThreshold = config.getDocumentLengthCategories().getMediumThreshold();

        if (docLength <= smallThreshold) {
            return "small";
        } else if (docLength <= mediumThreshold) {
            return "medium";
        } else {
            return "large";
        }
    }
}
