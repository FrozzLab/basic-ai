import objects.DataSample;

import java.util.*;

public class NaiveBayes {
    static HashMap<String, Double> probabilityMap = new HashMap<>();
    static HashMap<String, Double> attributeCountMap = new HashMap<>();
    static HashMap<String, Double> typeCountMap = new HashMap<>();
    static List<String> typeList;

    public static void trainProbabilityMap(List<DataSample> samples) {
        attributeCountMap = getAttributeCountMap(samples);

        streamAttributeCountMapBasedOnFilter(e -> !e.getKey().contains("_0"));

        for (Map.Entry<String, Double> entry : attributeCountMap.entrySet())
            if (entry.getKey().contains("_0")
                    && !entry.getKey().matches("t_t_.*"))
                typeCountMap.put(entry.getKey(), entry.getValue());

        streamAttributeCountMapBasedOnFilter(e -> typeCountMap.containsKey(e.getKey()));
    }

    private static void streamAttributeCountMapBasedOnFilter(
            java.util.function.Predicate<? super Map.Entry<String, Double>> predicate) {
        attributeCountMap.entrySet().stream()
                .filter(predicate)
                .map(e -> {
                    String entryAttribute = e.getKey().split("_")[0];
                    e.setValue(e.getValue() / attributeCountMap.get("t_" + entryAttribute + "_0"));
                    return e;
                })
                .forEach(e -> probabilityMap.put(e.getKey(), e.getValue()));
    }

    private static HashMap<String, Double> getAttributeCountMap(List<DataSample> samples) {
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

        attributeCountMap.put("t_t_0", (double)totalCount);

        return attributeCountMap;
    }

    public static double[][] produceAccuracyMatrix(List<DataSample> samples) {
        List<String> predictionList = new ArrayList<>();
        List<String> expectedList = new ArrayList<>();

        for (DataSample sample : samples) {
            expectedList.add(sample.getCorrectType());
            predictionList.add(produceDecision(sample));
        }

        typeList = new ArrayList<>(typeCountMap.keySet());
        int[][] confusionMatrix = new int[typeList.size()][typeList.size()];

        typeList.replaceAll(s -> s.split("_")[1]);

        for (int i = 0; i < predictionList.size(); i++)
            confusionMatrix[typeList.indexOf(expectedList.get(i))][typeList.indexOf(predictionList.get(i))] += 1;

        double[][] accuracyMatrix = new double[typeList.size()][4];

        for (int t = 0; t < typeList.size(); t++) {
            int TP = 0, TN = 0, FN = 0, FP = 0;

            for (int i = 0; i < confusionMatrix.length; i++) {
                for (int j = 0; j < confusionMatrix.length; j++) {
                    if (i == t && j == t)
                        TP = confusionMatrix[i][j];
                    else if (i == t)
                        FN += confusionMatrix[i][j];
                    else if (j == t)
                        FP += confusionMatrix[i][j];
                    else
                        TN += confusionMatrix[i][j];
                }
            }

            accuracyMatrix[t][0] = (double) (TP + TN) / (TP + TN + FP + FN) * 100;
            accuracyMatrix[t][1] = (double) TP / (TP + FP) * 100;
            accuracyMatrix[t][2] = (double) TP / (TP + FN) * 100;
            accuracyMatrix[t][3] = (2 * accuracyMatrix[t][1] * accuracyMatrix[t][2])
                    / (accuracyMatrix[t][1] + accuracyMatrix[t][2]);
        }

        return accuracyMatrix;
    }

    public static String produceDecision(DataSample sample) {
        List<String> allAttributes = sample.getInputVector();
        allAttributes.add(0, null);

        HashMap<String, Double> totalProbabilityMap = new HashMap<>();

        for (String type : typeCountMap.keySet())
            totalProbabilityMap.put(type.split("_")[1], 1.0);

        for (Map.Entry<String, Double> entry : totalProbabilityMap.entrySet()) {
            for (int i = 1; i < allAttributes.size(); i++) {
                if (!probabilityMap.containsKey(entry.getKey() + "_" + allAttributes.get(i) + "_" + i)) {
                    double smoothedProbability = getSmoothedProbability(entry, i);

                    entry.setValue(entry.getValue() * smoothedProbability);
                }
                else
                    entry.setValue(entry.getValue() * probabilityMap.get(entry.getKey() + "_" + allAttributes.get(i) + "_" + i));
            }

            entry.setValue(entry.getValue() * probabilityMap.get("t_" + entry.getKey() + "_0"));
        }

        double maxProbability = Collections.max(totalProbabilityMap.values());

        for (String key : totalProbabilityMap.keySet())
            if (totalProbabilityMap.get(key) == maxProbability) {
                return key;
            }

        return null;
    }

    private static double getSmoothedProbability(Map.Entry<String, Double> entry, int i) {
        List<String> probabilityMapKeySetFiltered = probabilityMap.keySet().stream()
                .filter(k -> k.matches(String.format("^%s_[a-zA-Z]+_%o", entry.getKey(), i)))
                .toList();

        return 1.0 / (typeCountMap.get("t_" + entry.getKey() + "_0")
                + probabilityMapKeySetFiltered.size());
    }
}
