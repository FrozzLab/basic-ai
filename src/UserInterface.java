import models.Observation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class UserInterface {

    static List<Observation> observations = new ArrayList<>();
    static List<Observation> observationsNorm = new ArrayList<>();
    static List<Observation> testObservations = new ArrayList<>();
    static List<Observation> testObservationsNorm = new ArrayList<>();

    public static void main(String[] args) {

        int hyperParam;
        String trainingFileName, testFileName;

        if (args.length != 3) {
            throw new IllegalArgumentException("""
                    
                    Wrong number of arguments, please provide:
                    > The k-NN hyperparameter;
                    > Name of the file with the csv training set;
                    > Name of the file with the csv test set.
                                        
                    --- Note --- The aforementioned files have to be placed in
                                 the "data" directory for the program to work.""");
        }
        else {
            hyperParam = Integer.parseInt(args[0]);
            trainingFileName = "data/" + args[1];
            testFileName = "data/" + args[2];
        }

        getObservations(trainingFileName, testFileName);

        String options = ("""
                                
                Select desired operation:
                    > "hyperParam" to change the k-NN hyperparameter;
                    > "testSet" or "testSetNorm" to produce the accuracy of the program
                        for the original and normalized test sets respectively;
                    > "single" or "singleNorm" to predict the output for a single observation
                        using the original and normalized training sets respectively;
                    > "createAccuracyData" to process every possible hyperparameter and
                        store it in a file;
                    > "quit" to end the program.
                """);

        Scanner scanner = new Scanner(System.in);
        String input = "";

        while (!input.equals("quit")) {
            System.out.println(options);
            input = scanner.nextLine();

            switch (input) {
                case "hyperParam" -> {
                    System.out.println("Input new hyperparameter:");
                    hyperParam = Integer.parseInt(scanner.nextLine());

                    if (hyperParam <= observations.size() && hyperParam > 0)
                        System.out.println("Hyperparameter successfully changed.");
                    else
                        System.out.println("Invalid hyperparameter");
                }
                case "testSet" -> {
                    double accuracy = NearestNeighbor.estimateTestSet(testObservations, observations, hyperParam);

                    System.out.printf("Accuracy: %.2f", accuracy);
                    System.out.print("%\n");
                }
                case "testSetNorm" -> {
                    double accuracy = NearestNeighbor.estimateTestSet(testObservationsNorm, observationsNorm, hyperParam);

                    System.out.printf("Accuracy: %.2f", accuracy);
                    System.out.print("%\n");
                }
                case "single" -> {
                    System.out.println("Input the parameter value separated by spaces:");

                    String response = scanner.nextLine();

                    System.out.println(processUserObservation(hyperParam, response, observations));
                }
                case "singleNorm" -> {
                    System.out.println("Input the parameter value separated by spaces:");

                    String response = scanner.nextLine();

                    System.out.println(processUserObservation(hyperParam, response, observationsNorm));
                }
                case "createAccuracyData" ->
                    processCreateAccuracyDataCase();
                case "quit" ->
                    System.out.println("\nQuitting program.");
                default ->
                    System.out.println("\nIncorrect argument input by user, try again.");
            }
        }
    }



    private static void processCreateAccuracyDataCase() {
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

            printObservations(testObservations, observations, fileChannel);

            fileChannel.write(ByteBuffer.wrap("\nNormalized\n".getBytes()));

            printObservations(testObservationsNorm, observationsNorm, fileChannel);

            fileOutputStream.close();
            fileChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private static void printObservations(List<Observation> testObservations, List<Observation> observations, FileChannel fileChannel) {
        ByteBuffer buffer;

        for (int i = 1; i <= observations.size(); i++) {
            String accuracy = NearestNeighbor.estimateTestSet(testObservations, observations, i) + "\n";

            try {
                buffer = ByteBuffer.wrap(accuracy.getBytes(StandardCharsets.UTF_8));

                fileChannel.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    private static void getObservations(String trainingFileName, String testFileName) {
        observations = DataOperator.parseData(trainingFileName);
        observationsNorm = DataOperator.normalizeData(DataOperator.parseData(trainingFileName));
        testObservations = DataOperator.parseData(testFileName);
        testObservationsNorm = DataOperator.normalizeData(DataOperator.parseData(testFileName));
    }



    private static String processUserObservation(int hyperParam, String userInput, List<Observation> observationsNorm) {
        List<Double> parsedUserInput =
                Arrays.stream(userInput.split(" ")).map(Double::parseDouble).collect(Collectors.toList());

        String decision;

        if (parsedUserInput.size() == Observation.getParameterLength()) {
             decision = NearestNeighbor.estimateSingleObservation(
                    new Observation(parsedUserInput), observationsNorm, hyperParam);
        }
        else
            decision = "Wrong input, please try again.";

        return decision;
    }
}
