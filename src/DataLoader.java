import java.io.*;
import java.util.*;

public class DataLoader {

    public static List<DataPoint> loadCSV(String filePath) throws IOException {
        List<DataPoint> goodReviews = new ArrayList<>();
        List<DataPoint> badReviews = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        String line = reader.readLine(); // Read header
        if (line == null) throw new IOException("Empty CSV file.");

        String[] headers = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

        int overallIdx = -1;
        int reviewIdx = -1;
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i].trim().replaceAll("\"", "").toLowerCase();
            if (header.equals("overall")) overallIdx = i;
            if (header.equals("reviewtext")) reviewIdx = i;
        }

        if (overallIdx == -1 || reviewIdx == -1)
            throw new IOException("Required columns 'overall' and 'reviewText' not found.");

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            if (parts.length <= Math.max(overallIdx, reviewIdx)) continue;

            try {
                double ratingDouble = Double.parseDouble(parts[overallIdx].trim());
                int rating = (int) ratingDouble;

                String reviewText = parts[reviewIdx].trim();
                if (reviewText.startsWith("\"") && reviewText.endsWith("\"")) {
                    reviewText = reviewText.substring(1, reviewText.length() - 1);
                }

                if (reviewText.isEmpty()) continue;

                if (rating == 4 || rating == 5) {
                    goodReviews.add(new DataPoint(null, 1, reviewText));
                } else if (rating == 1 || rating == 2) {
                    badReviews.add(new DataPoint(null, 0, reviewText));
                }
                // Ignore rating == 3
            } catch (NumberFormatException e) {
                System.err.println("Skipping invalid line: " + line);
            }
        }

        reader.close();

        // Balance dataset
        int size = Math.min(goodReviews.size(), badReviews.size());
        Collections.shuffle(goodReviews, new Random());
        Collections.shuffle(badReviews, new Random());

        List<DataPoint> balanced = new ArrayList<>();
        balanced.addAll(goodReviews.subList(0, size));
        balanced.addAll(badReviews.subList(0, size));
        Collections.shuffle(balanced, new Random());

        System.out.println("📊 Binary Sentiment Distribution:");
        System.out.println("Good Reviews (label 1): " + size);
        System.out.println("Bad Reviews (label 0): " + size);
        return balanced;
    }
}
