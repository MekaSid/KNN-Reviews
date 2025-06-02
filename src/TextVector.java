import java.util.*;

public abstract class TextVector {
    public HashMap<String, Integer> rawVector = new HashMap<>();

    public static final String[] noiseWordArray = {
            "a", "about", "above", "all", "along", "also", "although", "am", "an", "and", "any", "are", "aren't", "as", "at",
            "be", "because", "been", "but", "by", "can", "cannot", "could", "couldn't", "did", "didn't", "do", "does", "doesn't",
            "e.g.", "either", "etc", "etc.", "even", "ever", "enough", "for", "from", "further", "get", "gets", "got", "had",
            "have", "hardly", "has", "hasn't", "having", "he", "hence", "her", "here", "hereby", "herein", "hereof", "hereon",
            "hereto", "herewith", "him", "his", "how", "however", "i", "i.e.", "if", "in", "into", "it", "it's", "its", "me",
            "more", "most", "mr", "my", "near", "nor", "now", "no", "not", "or", "on", "of", "onto", "other", "our", "out",
            "over", "really", "said", "same", "she", "should", "shouldn't", "since", "so", "some", "such", "than", "that",
            "the", "their", "them", "then", "there", "thereby", "therefore", "therefrom", "therein", "thereof", "thereon",
            "thereto", "therewith", "these", "they", "this", "those", "through", "thus", "to", "too", "under", "until", "unto",
            "upon", "us", "very", "was", "wasn't", "we", "were", "what", "when", "where", "whereby", "wherein", "whether",
            "which", "while", "who", "whom", "whose", "why", "with", "without", "would", "you", "your", "yours", "yes"
    };

    public void add(String word) {
        rawVector.put(word, rawVector.getOrDefault(word, 0) + 1);
    }

    public boolean contains(String word) {
        return rawVector.containsKey(word);
    }

    public int getRawFrequency(String word) {
        return rawVector.getOrDefault(word, 0);
    }

    public int getHighestRawFrequency() {
        int max = 0;
        for (int freq : rawVector.values()) {
            if (freq > max) max = freq;
        }
        return max;
    }

    public abstract void normalize(DocumentCollection dc);

    public abstract Set<Map.Entry<String, Double>> getNormalizedVectorEntrySet();

    public abstract double getNormalizedFrequency(String word);

    public double getL2Norm() {
        double sum = 0.0;
        for (Map.Entry<String, Double> entry : getNormalizedVectorEntrySet()) {
            sum += Math.pow(entry.getValue(), 2);
        }
        return Math.sqrt(sum);
    }

    public ArrayList<Integer> findClosestDocuments(DocumentCollection docs, DocumentDistance alg, int k) {
        ArrayList<Map.Entry<Integer, Double>> scored = new ArrayList<>();

        for (Map.Entry<Integer, TextVector> entry : docs.getEntrySet()) {
            if (entry.getValue().getNormalizedVectorEntrySet().isEmpty()) continue;

            double sim = alg.findDistance(this, entry.getValue());
            scored.add(Map.entry(entry.getKey(), sim));
        }

        // Sort by cosine similarity (descending)
        scored.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        // Return top 5 (or fewer) doc IDs
        ArrayList<Integer> topDocs = new ArrayList<>();
        for (int i = 0; i < Math.min(k, scored.size()); i++) {
            topDocs.add(scored.get(i).getKey());
        }

        return topDocs;
    }

}
