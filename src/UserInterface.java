import objects.ItemSet;

public class UserInterface {

    static ItemSet itemSet;

    public static void main(String[] args) {
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

        System.out.println(itemSet);
    }
}
