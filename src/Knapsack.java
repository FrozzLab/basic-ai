import objects.ItemSet;
import objects.ResultContainer;

public class Knapsack {

    public static ResultContainer findOptimalSolutionUsingBruteForce(ItemSet itemSet) {
        int maxValue = 0;
        int maxWeight = 0;
        long result = 0;

        for (long vector = 0; vector < Math.pow(2, itemSet.getItemSize()); vector++) {
            int currentWeight = 0, currentValue = 0;

            for (int i = itemSet.getItemSize() - 1; i >= 0 ; i--) {
                if ((vector >> i & 1) == 1) {
                    currentValue += itemSet.getItems().get(itemSet.getItemSize() - 1 - i).getValue();
                    currentWeight += itemSet.getItems().get(itemSet.getItemSize() - 1 - i).getWeight();
                }
            }

            if (currentWeight <= itemSet.getMaxCapacity()) {
                if (currentValue > maxValue) {
                    maxValue = currentValue;
                    maxWeight = currentWeight;
                    result = vector;
                }
            }
        }

        return new ResultContainer(maxValue, maxWeight, itemSet.getItemSize(), result, itemSet);
    }

}
