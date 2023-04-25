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


    static List<String> existingTypes = new ArrayList<>();


    private static final Function<String, DataSample> parseLineToSample = (line) -> {
        List<String> parametersAsString = new ArrayList<>(List.of(line.split(",", 2)));

        String decisionParameterAsString = parametersAsString.get(0);
        List<Double> normalizedCharacterOccurrences = getNormalizedOccurrences(parametersAsString.get(1));

        if (!existingTypes.contains(decisionParameterAsString))
            existingTypes.add(decisionParameterAsString);

        return new DataSample(normalizedCharacterOccurrences, decisionParameterAsString);
    };


    public static List<Double> getNormalizedOccurrences(String inputString) {
        inputString = inputString.toLowerCase(Locale.ROOT);

        double[] characterOccurrencesArr = new double[26];

        for(int i = 0, length = inputString.length() ; i < length ; i++)
            if ((int) inputString.charAt(i) >= 97 && (int) inputString.charAt(i) <= 122)
                characterOccurrencesArr[(int) inputString.charAt(i) - 97]++;

        List<Double> characterOccurrences = Arrays.stream(characterOccurrencesArr).boxed().collect(Collectors.toList());

        double maxCount = Collections.max(characterOccurrences);
        double minCount = Collections.min(characterOccurrences);

        List<Double> normalizedCharacterOccurrences = new ArrayList<>();

        for (double occurrenceCount : characterOccurrences) {
            normalizedCharacterOccurrences.add((occurrenceCount - minCount) / (maxCount - minCount));
        }

        return normalizedCharacterOccurrences;
    }


    public static List<String> getExistingTypes() {
        return existingTypes;
    }
}
