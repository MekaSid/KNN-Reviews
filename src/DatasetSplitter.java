import java.util.*;

public class DatasetSplitter {
    public static Map<String, List<DataPoint>> split(List<DataPoint> data, double trainRatio, double valRatio) {
        Collections.shuffle(data, new Random()); // Shuffle for randomness

        int total = data.size();
        int trainSize = (int)(trainRatio * total);
        int valSize = (int)(valRatio * total);

        List<DataPoint> train = new ArrayList<>(data.subList(0, trainSize));
        List<DataPoint> val = new ArrayList<>(data.subList(trainSize, trainSize + valSize));
        List<DataPoint> test = new ArrayList<>(data.subList(trainSize + valSize, total));

        Map<String, List<DataPoint>> result = new HashMap<>();
        result.put("train", train);
        result.put("val", val);
        result.put("test", test);
        return result;
    }
}
