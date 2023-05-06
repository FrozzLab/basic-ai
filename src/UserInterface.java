import objects.DataSample;

import java.util.*;

public class UserInterface {

    static List<DataSample> trainingDataSamples = new ArrayList<>();
    static List<DataSample> testDataSamples = new ArrayList<>();

    public static void main(String[] args) {
        int decisionAttributePosition;
        String trainingDatasetFileName;
        String testDatasetFileName;

        if (args.length != 3) {
            throw new IllegalArgumentException("""
                    
                    Wrong number of arguments, please provide:
                    > Decision attribute position;
                    > Name of the file with the csv training data;
                    > Name of the file with the csv test data;
                                        
                    --- Note --- The aforementioned files have to be placed in
                                 the "data" directory for the program to work.""");
        }
        else {
            decisionAttributePosition = Integer.parseInt(args[0]);
            trainingDatasetFileName = "data/" + args[1];
            testDatasetFileName = "data/" + args[2];
        }

        trainingDataSamples = DataProcessor.parseDataFromCsv(trainingDatasetFileName);
        testDataSamples = DataProcessor.parseDataFromCsv(testDatasetFileName);

        NaiveBayes.trainProbabilityMap(trainingDataSamples, decisionAttributePosition);

        double accuracy = NaiveBayes.produceAccuracy(testDataSamples, decisionAttributePosition);

        System.out.println(accuracy);
    }
}
