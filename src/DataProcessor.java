import objects.DataSample;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataProcessor {
    static int _decisionAttributePosition;

    public static List<DataSample> parseDataFromCsv(String filePath, int decisionAttributePosition) {
        List<DataSample> parsedSamples = new ArrayList<>();
        _decisionAttributePosition = decisionAttributePosition;

        try {
            parsedSamples = Files.lines(Path.of(filePath))
                    .map(parseLineToSample)
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return parsedSamples;
    }


    private static final Function<String, DataSample> parseLineToSample = (line) -> {
        List<String> parametersAsString = new ArrayList<>(List.of(line.split(",")));

        String decisionParameterAsString = parametersAsString.get(_decisionAttributePosition);
        parametersAsString.remove(_decisionAttributePosition);

        return new DataSample(parametersAsString, decisionParameterAsString);
    };
}
