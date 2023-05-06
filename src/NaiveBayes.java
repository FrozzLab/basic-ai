import objects.DataSample;

import java.util.*;

public class NaiveBayes {
    static HashMap<String, Double> probabilityMap = new HashMap<>();
    static HashMap<String, Double> attributeCountMap = new HashMap<>();
    static HashMap<String, Double> typeCountMap = new HashMap<>();

    public static void trainProbabilityMap(List<DataSample> samples, int decisionPosition) {
        attributeCountMap = getAttributeCountMap(samples, decisionPosition);

        streamAttributeCountMapBasedOnFilter(e -> !e.getKey().contains("_" + decisionPosition), decisionPosition);

        for (Map.Entry<String, Double> entry : attributeCountMap.entrySet())
            if (entry.getKey().contains("_" + decisionPosition)
                    && !entry.getKey().matches("t_t_.*"))
                typeCountMap.put(entry.getKey(), entry.getValue());

        streamAttributeCountMapBasedOnFilter(e -> typeCountMap.containsKey(e.getKey()), decisionPosition);
    }

    private static void streamAttributeCountMapBasedOnFilter(
            java.util.function.Predicate<? super Map.Entry<String, Double>> predicate,
            int decisionPosition) {
        attributeCountMap.entrySet().stream()
                .filter(predicate)
                .map(e -> {
                    String entryAttribute = e.getKey().split("_")[0];
                    e.setValue(e.getValue() / attributeCountMap.get("t_" + entryAttribute + "_" + decisionPosition));
                    return e;
                })
                .forEach(e -> probabilityMap.put(e.getKey(), e.getValue()));
    }

    private static HashMap<String, Double> getAttributeCountMap(List<DataSample> samples, int decisionPosition) {
        HashMap<String, Double> attributeCountMap = new HashMap<>();
        int totalCount = 0;

        for (DataSample sample : samples) {
            List<String> allAttributes = sample.getInputVector();
            allAttributes.add(0, sample.getCorrectType());

            for (int i = 0; i < allAttributes.size(); i++) {
                String attribute;

                if (i == 0)
                    attribute = "t_" + sample.getCorrectType() + "_" + i;
                else
                    attribute = sample.getCorrectType() + "_" + allAttributes.get(i) + "_" + i;

                if (attributeCountMap.containsKey(attribute))
                    attributeCountMap.put(attribute, attributeCountMap.get(attribute) + 1);
                else
                    attributeCountMap.put(attribute, 1.0);
            }

            totalCount++;
        }

        attributeCountMap.put("t_t_" + decisionPosition, (double)totalCount);

        return attributeCountMap;
    }

    public static double produceAccuracy(List<DataSample> samples, int decisionPosition) {
        List<String> predictionList = new ArrayList<>();
        List<String> expectedList = new ArrayList<>();

        for (DataSample sample : samples) {
            expectedList.add(sample.getCorrectType());
            predictionList.add(produceDecision(sample, decisionPosition));
        }

        double totalCount = 0, correctCount = 0;

        for (int i = 0; i < predictionList.size(); i++) {
            if (expectedList.get(i).equals(predictionList.get(i)))
                    correctCount++;

            totalCount++;
        }

        double accuracy = correctCount / totalCount;

        return accuracy;
    }

    public static String produceDecision(DataSample sample, int decisionPosition) {
        List<String> allAttributes = sample.getInputVector();
        allAttributes.add(0, null);

        HashMap<String, Double> totalProbabilityMap = new HashMap<>();

        for (String type : typeCountMap.keySet())
            totalProbabilityMap.put(type.split("_")[1], 1.0);

        for (Map.Entry<String, Double> entry : totalProbabilityMap.entrySet()) {
            for (int i = 1; i < allAttributes.size(); i++) {
                if (!probabilityMap.containsKey(entry.getKey() + "_" + allAttributes.get(i) + "_" + i)) {
                    double smoothedProbability = getSmoothedProbability(decisionPosition, entry, i);

                    entry.setValue(entry.getValue() * smoothedProbability);
                }
                else
                    entry.setValue(entry.getValue() * probabilityMap.get(entry.getKey() + "_" + allAttributes.get(i) + "_" + i));
            }

            entry.setValue(entry.getValue() * probabilityMap.get("t_" + entry.getKey() + "_" + decisionPosition));
        }

        double maxProbability = Collections.max(totalProbabilityMap.values());

        for (String key : totalProbabilityMap.keySet())
            if (totalProbabilityMap.get(key) == maxProbability) {
                return key;
            }

        return null;
    }

    private static double getSmoothedProbability(int decisionPosition, Map.Entry<String, Double> entry, int i) {
        List<String> probabilityMapKeySetFiltered = probabilityMap.keySet().stream()
                .filter(k -> k.matches(String.format("^%s_[a-zA-Z]+_%o", entry.getKey(), i)))
                .toList();

        return 1.0 / (typeCountMap.get("t_" + entry.getKey() + "_" + decisionPosition)
                + probabilityMapKeySetFiltered.size());
    }
}
