import objects.DataSample;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataProcessor {

    public static List<DataSample> parseDataFromCsv(String filePath) {
        List<DataSample> parsedSamples = new ArrayList<>();

        try {
            parsedSamples = Files.lines(Path.of(filePath))
                    .map(parseLineToSample)
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return parsedSamples;
    }


    static HashMap<String, Integer> existingTypes = new HashMap<>();
    static int mapCount = 0;


    private static final Function<String, DataSample> parseLineToSample = (line) -> {
        List<String> parametersAsString = new ArrayList<>(List.of(line.split(",")));
        int lastElement = parametersAsString.size() - 1;

        String decisionParameterAsString = parametersAsString.get(lastElement);

        parametersAsString.remove(lastElement);

        List<Double> parameters = parametersAsString.stream()
                .map(Double::valueOf)
                .collect(Collectors.toList());

        int decisionParameter;

        if (!existingTypes.containsKey(decisionParameterAsString))
            existingTypes.put(decisionParameterAsString, mapCount++);

        if (mapCount > 2)
            throw new IllegalArgumentException("More than two types in dataset.");

        decisionParameter = existingTypes.get(decisionParameterAsString);

        return new DataSample(parameters, decisionParameter);
    };


    public static List<DataSample> normalizeData(List<DataSample> samples) {
        List<Double> maxParameters = new ArrayList<>(samples.get(0).getInputData());
        List<Double> minParameters = new ArrayList<>(samples.get(0).getInputData());

        int parameterSize = maxParameters.size();

        for (int i = 1; i < samples.size(); i++) {
            List<Double> parameters = samples.get(i).getInputData();

            for (int j = 0; j < parameterSize; j++) {
                if (maxParameters.get(j) < parameters.get(j))
                    maxParameters.set(j, parameters.get(j));

                if (minParameters.get(j) > parameters.get(j))
                    minParameters.set(j, parameters.get(j));
            }
        }

        List<DataSample> normalizedSamples = new ArrayList<>();

        for (DataSample sample : samples)
            normalizedSamples.add(new DataSample(sample));

        for (DataSample sample : normalizedSamples) {
            List<Double> parameters = sample.getInputData();

            for (int i = 0; i < parameterSize; i++) {
                Double parameterNormalized = (parameters.get(i) - minParameters.get(i)) /
                        (maxParameters.get(i) - minParameters.get(i));
                parameters.set(i, parameterNormalized);
            }
        }

        return normalizedSamples;
    }


    public static int getParameterSize(List<DataSample> samples) {
        return samples.get(0).getInputData().size();
    }


    public static String getTypeFromInt(int intOfType) {
        for (Map.Entry<String, Integer> entry : existingTypes.entrySet()) {
            if (Objects.equals(intOfType, entry.getValue())) {
                return entry.getKey();
            }
        }

        return null;
    }
}
