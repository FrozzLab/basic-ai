import objects.Cluster;
import objects.DataSample;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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


    private static final Function<String, DataSample> parseLineToSample = (line) -> {
        List<String> parametersAsString = new ArrayList<>(List.of(line.split(",")));
        int lastElementIndex = parametersAsString.size() - 1;

        String decisionParameterAsString = parametersAsString.get(lastElementIndex);

        parametersAsString.remove(lastElementIndex);

        List<Double> parameters = parametersAsString.stream()
                .map(Double::valueOf)
                .toList();

        return new DataSample(parameters, decisionParameterAsString);
    };

    public static void assignRandomSamplesToClusters(List<Cluster> clusters, List<DataSample> samples) {
        Random random = new Random();
        List<DataSample> samplesClone = new ArrayList<>(samples);
        int clusterIndexCarousel = 0;
        int nextSampleIndex;

        for (Cluster cluster : clusters)
            cluster.setDatasetSize(samples.get(0).getInputVectorSize());

        while (!samplesClone.isEmpty()) {
            if (clusterIndexCarousel == clusters.size())
                clusterIndexCarousel = 0;

            nextSampleIndex = random.nextInt(0, samplesClone.size());
            DataSample nextSample = samplesClone.get(nextSampleIndex);
            nextSample.setCluster(clusters.get(clusterIndexCarousel));

            clusters.get(clusterIndexCarousel).getSamples().add(samplesClone.get(nextSampleIndex));

            samplesClone.remove(nextSampleIndex);
            clusterIndexCarousel++;
        }

    }
}
