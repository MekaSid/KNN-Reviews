import java.util.*;

public class DocumentCollection {
    private HashMap<Integer, TextVector> documents = new HashMap<>();
    public static final Set<String> noiseWords = new HashSet<>(Arrays.asList(TextVector.noiseWordArray));

    public DocumentCollection(List<String> reviewTexts) {
        int id = 1;
        for (String text : reviewTexts) {
            DocumentVector vec = new DocumentVector();
            String[] words = text.split("[^a-zA-Z]+");
            for (String word : words) {
                word = word.toLowerCase();
                if (word.length() >= 2 && !noiseWords.contains(word)) {
                    vec.add(word);
                }
            }
            documents.put(id++, vec);
        }
    }

    public void normalize(DocumentCollection dc) {
        for (TextVector tv : documents.values()) {
            tv.normalize(dc);
        }
    }

    public TextVector getDocumentById(int id) {
        return documents.get(id);
    }

    public int getSize() {
        return documents.size();
    }

    public Set<Map.Entry<Integer, TextVector>> getEntrySet() {
        return documents.entrySet();
    }

    public int getDocumentFrequency(String word) {
        int count = 0;
        for (TextVector tv : documents.values()) {
            if (tv.contains(word.toLowerCase())) count++;
        }
        return count;
    }





}
