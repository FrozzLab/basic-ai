import objects.DataSample;
import objects.Perceptron;

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
    static List<DataSample> trainingSamplesNorm = new ArrayList<>();
    static List<DataSample> testSamples = new ArrayList<>();
    static List<DataSample> testSamplesNorm = new ArrayList<>();

    static List<Double> accuracyList = new ArrayList<>();
    static List<Double> accuracyListNorm = new ArrayList<>();

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

        Perceptron perceptron = new Perceptron(DataProcessor.getParameterSize(testSamples), learningRate);
        Perceptron perceptronNorm = new Perceptron(DataProcessor.getParameterSize(testSamples), learningRate);

        System.out.println("Original:");
        accuracyList = teachPerceptron(epochCount, perceptron, trainingSamples, testSamples);
        System.out.println("Normalized:");
        accuracyListNorm = teachPerceptron(epochCount, perceptronNorm, trainingSamplesNorm, testSamplesNorm);

        String options = ("""
                                
                Select desired operation:
                    > "single" or "singleNorm" to predict the output for a single observation
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
                    String type = caseEstimateSingleSampleFromUser(perceptron, scanner);

                    System.out.println(type);
                }
                case "singleNorm" -> {
                    String type = caseEstimateSingleSampleFromUser(perceptronNorm, scanner);

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

    private static List<Double> teachPerceptron(int epochCount, Perceptron perceptron,
                                                List<DataSample> trainingSamples, List<DataSample> testSamples) {
        int count = 0;
        double accuracy;
        List<Double> accuracyList = new ArrayList<>();


        for (int i = 0; i < epochCount; i++) {
            perceptron.estimateSet(trainingSamples, "training");

            accuracy = perceptron.estimateSet(testSamples, "test") * 100;
            accuracyList.add(accuracy);

            System.out.println("Epoch number " + ++count + ": " + String.format("%.2f", accuracy) + "%");

            Collections.shuffle(trainingSamples);
            Collections.shuffle(testSamples);
        }

        return accuracyList;
    }


    private static String caseEstimateSingleSampleFromUser(Perceptron perceptron, Scanner scanner) {
        System.out.println("Input the parameter value separated by commas:");

        List<String> responseAsString = List.of(scanner.nextLine().split(","));
        List<Double> response = responseAsString.stream()
                .map(Double::valueOf)
                .collect(Collectors.toList());

        return DataProcessor.getTypeFromInt(perceptron.getOutput(response));
    }


    private static void getSamples(String trainingFileName, String testFileName) {
        trainingSamples = DataProcessor.parseDataFromCsv(trainingFileName);
        trainingSamplesNorm = DataProcessor.normalizeData(DataProcessor.parseDataFromCsv(trainingFileName));
        testSamples = DataProcessor.parseDataFromCsv(testFileName);
        testSamplesNorm = DataProcessor.normalizeData(DataProcessor.parseDataFromCsv(testFileName));
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

            fileChannel.write(ByteBuffer.wrap("Original\n".getBytes()));

            for (Double accuracy : accuracyList)
                fileChannel.write(ByteBuffer.wrap((accuracy + "\n").getBytes()));

            fileChannel.write(ByteBuffer.wrap("\nNormalized\n".getBytes()));

            for (Double accuracy : accuracyListNorm)
                fileChannel.write(ByteBuffer.wrap((accuracy + "\n").getBytes()));

            fileOutputStream.close();
            fileChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
