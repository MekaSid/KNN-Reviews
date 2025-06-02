import java.util.Map;

public class EuclideanDistance implements DocumentDistance {
    public double findDistance(TextVector query, TextVector doc) {
        double sumOfSquares = 0.0;

        // Iterate over each term in the query vector
        for (Map.Entry<String, Double> entry : query.getNormalizedVectorEntrySet()) {
            String term = entry.getKey();
            double queryValue = entry.getValue();
            double docValue = doc.getNormalizedFrequency(term);

            // Calculate the squared difference for each term
            double difference = queryValue - docValue;
            sumOfSquares += difference * difference;
        }

        // Return the square root of the sum of squared differences
        return Math.sqrt(sumOfSquares);
    }
}