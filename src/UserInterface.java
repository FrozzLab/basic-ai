import objects.DataSample;

import java.util.*;

public class UserInterface {

    static List<DataSample> trainingDataSamples = new ArrayList<>();
    static List<DataSample> testDataSamples = new ArrayList<>();

    public static void main(String[] args) {
        String trainingDatasetFileName;
        String testDatasetFileName;
        int decisionAttributePosition;

        if (args.length != 3) {
            throw new IllegalArgumentException("""
                    
                    Wrong number of arguments, please provide:
                    > Name of the file with the csv training data;
                    > Name of the file with the csv test data;
                    > Position of the decision attribute;
                                        
                    --- Note --- The aforementioned files have to be placed in
                                 the "data" directory for the program to work.""");
        }
        else {
            trainingDatasetFileName = "data/" + args[0];
            testDatasetFileName = "data/" + args[1];
            decisionAttributePosition = Integer.parseInt(args[2]);
        }

        trainingDataSamples = DataProcessor.parseDataFromCsv(trainingDatasetFileName, decisionAttributePosition);
        testDataSamples = DataProcessor.parseDataFromCsv(testDatasetFileName, decisionAttributePosition);

        NaiveBayes.trainProbabilityMap(trainingDataSamples);

        double[][] accuracyMatrix = NaiveBayes.produceAccuracyMatrix(testDataSamples);

        for (int i = 0; i < NaiveBayes.typeList.size(); i++) {
            System.out.printf("""
                    Class %s:
                    Accuracy: %.2f%%
                    Precision: %.2f%%
                    Recall: %.2f%%
                    F-Measure: %.2f%%
                    
                    """, NaiveBayes.typeList.get(i), accuracyMatrix[i][0], accuracyMatrix[i][1],
                    accuracyMatrix[i][2], accuracyMatrix[i][3]);
        }
    }
}
