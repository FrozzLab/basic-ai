import models.DecisionDistanceContainer;
import models.Observation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class NearestNeighbor {

    public static double estimateTestSet(List<Observation> testObservations, List<Observation> observations, int numOfNeighbors) {
        double countCorrect = 0.0;

        for (Observation testObservation : testObservations) {
            String decision = estimateSingleObservation(testObservation, observations, numOfNeighbors);

            if (decision.equals(testObservation.getDecisionParameter()))
                countCorrect++;

            System.out.println(testObservation + ", predicted: " + decision);
        }

        return (countCorrect / testObservations.size()) * 100;
    }



    public static String estimateSingleObservation(Observation userObservation, List<Observation> observations, int numOfNeighbors) {
        List<DecisionDistanceContainer> ddContainers = new ArrayList<>();

        for (Observation observation : observations)
            ddContainers.add(new DecisionDistanceContainer(observation.getDecisionParameter(),
                                            calculateEuclidianDistance(userObservation, observation)));

        ddContainers.sort(Comparator.comparing(DecisionDistanceContainer::getDistance));

        List<String> decisionAttributes = new ArrayList<>(numOfNeighbors);

        for (int i = 0; i < numOfNeighbors; i++)
            decisionAttributes.add(ddContainers.get(i).getDecision());

        return getMostFrequentDecisionAttribute(decisionAttributes);
    }



    private static String getMostFrequentDecisionAttribute(List<String> decisionAttributes) {
        HashMap<String,Integer> frequency = new HashMap<>();

        for (String decisionAttribute : decisionAttributes) {
            if (frequency.containsKey(decisionAttribute))
                frequency.put(decisionAttribute, frequency.get(decisionAttribute) + 1);
            else
                frequency.put(decisionAttribute, 1);
        }

        int maxCount = 0;
        String decision = "";

        for (String key : frequency.keySet()) {
            if (frequency.get(key) >= maxCount) {
                maxCount = frequency.get(key);
                decision = key;
            }
        }

        return decision;
    }



    public static double calculateEuclidianDistance (Observation estimatable, Observation observation) {
        List<Double> parametersEst = new ArrayList<>(estimatable.getParameters());
        List<Double> parametersObs = new ArrayList<>(observation.getParameters());
        double preRootSum = 0.0;

        for (int i = 0; i < parametersEst.size(); i++)
            preRootSum += Math.pow((parametersEst.get(i) - parametersObs.get(i)), 2);

        return Math.sqrt(preRootSum);
    }
}
