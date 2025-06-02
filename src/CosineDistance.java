import java.util.Map;

public class CosineDistance implements DocumentDistance {
    public double findDistance(TextVector query, TextVector doc) {
        double dot = 0.0;
        for (Map.Entry<String, Double> entry : query.getNormalizedVectorEntrySet()) {
            dot += entry.getValue() * doc.getNormalizedFrequency(entry.getKey());
        }

        double normA = query.getL2Norm();
        double normB = doc.getL2Norm();

        if (normA == 0 || normB == 0) return 0.0;
        return dot / (normA * normB);
    }
}
