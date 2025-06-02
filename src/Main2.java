import java.util.*;
import java.io.*;

public class Main2 {
    public static void main(String[] args) {
        try {
            // Step 1: Load and balance dataset
            List<DataPoint> rawData = DataLoader.loadCSV("./amazon_reviews.csv");
            System.out.println("Total balanced data points: " + rawData.size());

            // Step 2: Split into train/val/test (on DataPoints to keep labels)
            Map<String, List<DataPoint>> split = DatasetSplitter.split(rawData, 0.8, 0.1);
            List<DataPoint> trainSet = split.get("train");
            List<DataPoint> testSet = split.get("test");
            List<DataPoint> validSet = split.get("val");
//            System.out.printf("Test Set Size: %d\n", testSet.size());

            List<String> texts = new ArrayList<>();
            List<Integer> labels = new ArrayList<>();
            for (DataPoint dp : trainSet) {
                texts.add(dp.rawText);
                labels.add(dp.label);
            }

            // Step 3: TF-IDF using DocumentCollection
            DocumentCollection docs = new DocumentCollection(texts);
            docs.normalize(docs);

            // Step 4: Split into train/val/test (on DataPoints to keep labels)
//            System.out.printf("Test Set Size: %d\n", testSet.size());


            int bestK = 1;
            double bestValAcc = 0;
            for (int k = 1; k <= 9; k += 2) {
                int correct = 0;
                for (DataPoint dp : validSet) {
                    QueryVector qv = new QueryVector(dp.rawText);
                    qv.normalize(docs);
                    List<Integer> topDocs = qv.findClosestDocuments(docs, new EuclideanDistance(), k);
                    int pred = majorityLabel(topDocs, labels);
                    if (pred == dp.label) correct++;
                }
                double acc = (double) correct / validSet.size();
                System.out.printf("K=%d → Val Accuracy: %.2f%%\n", k, acc * 100);
                if (acc > bestValAcc) {
                    bestValAcc = acc;
                    bestK = k;
                }
            }

            System.out.printf("Best K: %d\n", bestK);

            // Step 5: Evaluation on test set
            int TP = 0, FP = 0, FN = 0, TN = 0;
            int testCorrect = 0;


            System.out.println("\nPredictions on Test Set:");
            for (int i = 0; i < testSet.size(); i++) {

                DataPoint dp = testSet.get(i);
                QueryVector qv = new QueryVector(dp.rawText);
                qv.normalize(docs);

                ArrayList<Integer> topDocs = qv.findClosestDocuments(docs, new EuclideanDistance(), bestK);
                int pred = majorityLabel(topDocs, labels);
                int actual = dp.label;

                // Confusion matrix components
                if (pred == 1 && actual == 1) TP++;
                else if (pred == 1 && actual == 0) FP++;
                else if (pred == 0 && actual == 1) FN++;
                else if (pred == 0 && actual == 0) TN++;

                System.out.printf("Review %d:\n", i + 1);
                System.out.println("Text: " + dp.rawText);
                System.out.println("Predicted Label: " + (pred == 1 ? "GOOD" : "BAD"));
                System.out.println("Actual Label: " + (actual == 1 ? "GOOD" : "BAD"));
                System.out.println("-------------------------------------------");

                if (pred == actual) testCorrect++;
            }

            // Step 6: Metrics
            double accuracy = (double) testCorrect / testSet.size();
            double precision = TP + FP == 0 ? 0 : (double) TP / (TP + FP);
            double recall = TP + FN == 0 ? 0 : (double) TP / (TP + FN);
            double f1 = (precision + recall) == 0 ? 0 : 2 * (precision * recall) / (precision + recall);

            System.out.printf("\nFinal Test Accuracy: %.2f%%\n", accuracy * 100);
            System.out.printf("Precision: %.2f%%\n", precision * 100);
            System.out.printf("Recall: %.2f%%\n", recall * 100);
            System.out.printf("F1 Score: %.2f%%\n", f1 * 100);

            // Step 7: Summary of label counts
            int goodCount = 0, badCount = 0;
            for (DataPoint dp : testSet) {
                if (dp.label == 1) goodCount++;
                else badCount++;
            }
            System.out.println("\n📊 Test Set Distribution:");
            System.out.println("GOOD reviews: " + goodCount);
            System.out.println("BAD reviews: " + badCount);

            // Step 8: Live user input
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("\nType a review to classify as good (4/5) or bad (1/2), or type 'exit' to quit: ");
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("exit")) break;

                QueryVector q = new QueryVector(input);
                q.normalize(docs);
                ArrayList<Integer> top = q.findClosestDocuments(docs, new EuclideanDistance(), bestK);
                int pred = majorityLabel(top, labels);

                System.out.println(pred == 1 ? "This is a GOOD review!" : "This is a BAD review!");
            }

        } catch (IOException e) {
            System.err.println("File error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int majorityLabel(List<Integer> docIds, List<Integer> labels) {
        int good = 0, bad = 0;
        for (int id : docIds) {
            int label = labels.get(id - 1); // 1-based ID from DocumentCollection
            if (label == 1) good++;
            else bad++;
        }
        return good >= bad ? 1 : 0;
    }
}
