import objects.DataSample;
import objects.Perceptron;
import objects.PerceptronLayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class UserInterface {

    static List<DataSample> trainingSamples = new ArrayList<>();
    static List<DataSample> testSamples = new ArrayList<>();
    static List<Double> accuracyList = new ArrayList<>();

    public static void main(String[] args) {

        int epochCount;
        double learningRate;
        String trainingFileName, testFileName;

        if (args.length != 4) {
            throw new IllegalArgumentException("""
                    
                    Wrong number of arguments, please provide:
                    > The desired epoch count;
                    > The perceptron learning rate;
                    > Name of the file with the csv training set;
                    > Name of the file with the csv test set.
                                        
                    --- Note --- The aforementioned files have to be placed in
                                 the "data" directory for the program to work.""");
        }
        else {
            epochCount = Integer.parseInt(args[0]);
            learningRate = Double.parseDouble(args[1]);
            trainingFileName = "data/" + args[2];
            testFileName = "data/" + args[3];
        }

        getSamples(trainingFileName, testFileName);
        List<String> existingTypes = DataProcessor.getExistingTypes();
        List<Perceptron> perceptrons = new ArrayList<>();

        for (String type : existingTypes)
            perceptrons.add(new Perceptron(26, type, learningRate));

        PerceptronLayer perceptronLayer = new PerceptronLayer(perceptrons);

        System.out.println("Accuracy:");
        accuracyList = teachPerceptronLayer(epochCount, perceptronLayer, trainingSamples, testSamples);

        String options = ("""
                                
                Select desired operation:
                    > "single" to predict the output for a single observation
                        using the original and normalized training sets respectively;
                    > "accuracyData" to process every possible hyperparameter and
                        store it in a file;
                    > "quit" to end the program.
                """);

        Scanner scanner = new Scanner(System.in);
        String input = "";

        while (!input.equals("quit")) {
            System.out.println(options);
            input = scanner.nextLine();

            switch (input) {
                case "single" -> {
                    String type = caseEstimateSingleSampleFromUser(perceptronLayer, scanner);

                    System.out.println(type);
                }
                case "accuracyData" ->
                        printTrainingAccuracyData();
                case "quit" ->
                        System.out.println("\nQuitting program.");
                default ->
                        System.out.println("\nIncorrect argument input by user, try again.");
            }
        }
    }

    private static List<Double> teachPerceptronLayer(int epochCount, PerceptronLayer perceptronLayer,
                                                     List<DataSample> trainingSamples, List<DataSample> testSamples) {
        int count = 0;
        double accuracy;
        List<Double> accuracyList = new ArrayList<>();


        for (int i = 0; i < epochCount; i++) {
            perceptronLayer.estimateSetAsLayer(trainingSamples, "training");

            accuracy = perceptronLayer.estimateSetAsLayer(testSamples, "test") * 100;
            accuracyList.add(accuracy);

            System.out.println("Epoch number " + ++count + ": " + String.format("%.2f", accuracy) + "%");

            Collections.shuffle(trainingSamples);
            Collections.shuffle(testSamples);
        }

        return accuracyList;
    }


    private static String caseEstimateSingleSampleFromUser(PerceptronLayer perceptronLayer, Scanner scanner) {
        System.out.println("Input the sentence:");

        String responseAsString = scanner.nextLine();
        List<Double> normalizedOccurrences = DataProcessor.getNormalizedOccurrences(responseAsString);
        DataSample sample = new DataSample(normalizedOccurrences, null);

        return perceptronLayer.estimateSingleTestSampleAsLayer(sample, "test");
    }


    private static void getSamples(String trainingFileName, String testFileName) {
        trainingSamples = DataProcessor.parseDataFromCsv(trainingFileName);
        testSamples = DataProcessor.parseDataFromCsv(testFileName);
    }


    private static void printTrainingAccuracyData() {
        File outputFile = new File("data/output.data");

        if (outputFile.length() != 0) {
            try {
                RandomAccessFile randomAccessFile = new RandomAccessFile(outputFile, "rw");
                randomAccessFile.setLength(0);
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile, true);
            FileChannel fileChannel = fileOutputStream.getChannel();

            fileChannel.write(ByteBuffer.wrap("Accuracy:\n".getBytes()));

            for (Double accuracy : accuracyList)
                fileChannel.write(ByteBuffer.wrap((accuracy + "\n").getBytes()));

            fileOutputStream.close();
            fileChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
