import models.Observation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataOperator {

    public static List<Observation> parseData(String fileName) {
        List<Observation> result = new ArrayList<>();

        try {
            result = Files.lines(Path.of(fileName))
                .map(parseLineToObservation)
                .collect(Collectors.toList());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Observation.setIdCount(0);

        return result;
    }



    private static final Function<String, Observation> parseLineToObservation = (line) -> {
        List<String> parametersAsString = new ArrayList<>(List.of(line.split(",")));
        int lastElement = parametersAsString.size() - 1;

        String decisionParameter = parametersAsString.get(lastElement);

        parametersAsString.remove(lastElement);

        List<Double> parameters = parametersAsString.stream()
                .map(Double::valueOf)
                .collect(Collectors.toList());

        return new Observation(parameters, decisionParameter);
    };



    public static List<Observation> normalizeData(List<Observation> observations) {
        List<Double> maxParameters = new ArrayList<>(observations.get(0).getParameters());
        List<Double> minParameters = new ArrayList<>(observations.get(0).getParameters());

        int parameterSize = observations.get(0).getParameters().size();

        for (int i = 1; i < observations.size(); i++) {
            List<Double> parameters = observations.get(i).getParameters();

            for (int j = 0; j < parameterSize; j++) {
                if (maxParameters.get(j) < parameters.get(j))
                    maxParameters.set(j, parameters.get(j));

                if (minParameters.get(j) > parameters.get(j))
                    minParameters.set(j, parameters.get(j));
            }
        }

        List<Observation> result = new ArrayList<>();

        for (Observation observation : observations)
            result.add(new Observation(observation));

        for (Observation observation : result) {
            List<Double> parameters = observation.getParameters();

            for (int i = 0; i < parameterSize; i++) {
                Double parameterNormalized = (parameters.get(i) - minParameters.get(i)) /
                                             (maxParameters.get(i) - minParameters.get(i));
                parameters.set(i, parameterNormalized);
            }
        }

        Observation.setIdCount(0);

        return result;
    }
}
