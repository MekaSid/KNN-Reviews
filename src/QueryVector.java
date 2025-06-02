import java.util.*;

public class QueryVector extends TextVector {
    private HashMap<String, Double> normalized = new HashMap<>();

    public QueryVector(String input) {
        String[] words = input.split("[^a-zA-Z]+");
        for (String word : words) {
            word = word.toLowerCase();
            if (word.length() >= 2 && !DocumentCollection.noiseWords.contains(word)) {
                this.add(word);
            }
        }
    }

    public void normalize(DocumentCollection dc) {
        int totalDocs = dc.getSize();
        int maxFreq = getHighestRawFrequency();

        for (var entry : rawVector.entrySet()) {
            String word = entry.getKey();
            int tf = entry.getValue();
            int df = dc.getDocumentFrequency(word);
            double idf = df == 0 ? 0 : Math.log((double) totalDocs / df);
            normalized.put(word, (0.5 + 0.5 * ((double) tf / maxFreq)) * idf);
        }
    }

    public Set<Map.Entry<String, Double>> getNormalizedVectorEntrySet() {
        return normalized.entrySet();
    }

    public double getNormalizedFrequency(String word) {
        return normalized.getOrDefault(word, 0.0);
    }
}
