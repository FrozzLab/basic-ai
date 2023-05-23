import objects.ItemSet;
import objects.ResultContainer;

public class UserInterface {

    static ItemSet itemSet;
    static ResultContainer resultContainer;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        String datasetFileName;

        if (args.length != 1) {
            throw new IllegalArgumentException("""
                    
                    Wrong number of arguments, please provide:
                    > Name of the file with the csv knapsack data;
                                        
                    --- Note --- The aforementioned file has to be placed in
                                 the "data" directory for the program to work.""");
        }
        else {
            datasetFileName = "data/" + args[0];
        }

        itemSet = DataProcessor.processFileIntoItemSet(datasetFileName);
        resultContainer = Knapsack.findOptimalSolutionUsingBruteForce(itemSet);
        long stopTime = System.currentTimeMillis();

        System.out.printf("Compute time: %.2f sec\n", ((stopTime - startTime) / 1000.));
        System.out.print(resultContainer);
    }
}
