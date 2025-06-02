import java.util.*;

public class DocumentVector extends TextVector {
    private HashMap<String, Double> normalized = new HashMap<>();

    public void normalize(DocumentCollection dc) {
        int maxFreq = getHighestRawFrequency();
        int totalDocs = dc.getSize();

        for (var entry : rawVector.entrySet()) {
            String word = entry.getKey();
            int tf = entry.getValue();
            int df = dc.getDocumentFrequency(word);
            double idf = df == 0 ? 0 : Math.log((double) totalDocs / df);
            normalized.put(word, (double) tf / maxFreq * idf);
        }
    }

    public Set<Map.Entry<String, Double>> getNormalizedVectorEntrySet() {
        return normalized.entrySet();
    }

    public double getNormalizedFrequency(String word) {
        return normalized.getOrDefault(word, 0.0);
    }


}
