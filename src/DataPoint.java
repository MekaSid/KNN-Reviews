public class DataPoint {
    public int label;         // Integer rating (0, 1)
    public String rawText;    // Original review text

    public DataPoint(double[] features, int label, String rawText) {
        this.label = label;
        this.rawText = rawText;
    }
}
