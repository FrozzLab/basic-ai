package objects;

public class ResultContainer {

    int maxValue;
    int maxWeight;
    int vectorLength;
    long vector;
    String vectorAsBinary;
    ItemSet itemSet;

    public ResultContainer(int maxValue, int maxWeight, int vectorLength, long vector, ItemSet itemSet) {
        this.maxValue = maxValue;
        this.maxWeight = maxWeight;
        this.vectorLength = vectorLength;
        this.vector = vector;
        this.vectorAsBinary = convertVectorToBinary();
        this.itemSet = itemSet;
    }

    private String convertVectorToBinary() {
        StringBuilder result = new StringBuilder();

        for (int i = vectorLength - 1; i >= 0; i--) {
            if ((vector >> i & 1) == 1) {
                result.append(1);
            }
            else {
                result.append(0);
            }
        }

        return result.toString();
    }

    public String printKnapsackContents() {
        StringBuilder contents = new StringBuilder();

        contents.append(" > List of items:\n");

        for (int i = itemSet.getItemSize() - 1; i >= 0 ; i--) {
            if ((vector >> i & 1) == 1) {
                contents.append("    > Item ").append(itemSet.getItemSize() - i - 1).append(": ")
                        .append(itemSet.getItems().get(itemSet.getItemSize() - 1 - i)).append("\n");
            }
        }

        return contents.toString();
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();

        out.append("""
               Result:
                > Solution value: %d,
                > Solution weight: %d,
                > Characteristic vector (as decimal): %d,
                > Characteristic vector (as binary): %s
               """).append(printKnapsackContents());


        return String.format(out.toString(), maxValue, maxWeight, vector, vectorAsBinary);
    }
}
