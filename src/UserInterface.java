import objects.Cluster;
import objects.DataSample;

import java.util.*;

public class UserInterface {

    static List<DataSample> dataSamples = new ArrayList<>();
    static List<Double> distanceList = new ArrayList<>();
    static List<Cluster> clusters = new ArrayList<>();

    public static void main(String[] args) {

        int numOfClusters;
        String datasetFileName;

        if (args.length != 2) {
            throw new IllegalArgumentException("""
                    
                    Wrong number of arguments, please provide:
                    > The desired number of clusters;
                    > Name of the file with the csv dataset;
                                        
                    --- Note --- The aforementioned file has to be placed in
                                 the "data" directory for the program to work.""");
        }
        else {
            numOfClusters = Integer.parseInt(args[0]);
            datasetFileName = "data/" + args[1];
        }

        dataSamples = DataProcessor.parseDataFromCsv(datasetFileName);

        if (numOfClusters >= dataSamples.size())
            throw new IllegalArgumentException("The number of clusters cannot be larger than the dataset");

        for (int i = 0; i < numOfClusters; i++) {
            clusters.add(new Cluster());
        }

        DataProcessor.assignRandomSamplesToClusters(clusters, dataSamples);

        distanceList = runClustering(clusters, dataSamples);

        System.out.println("Distance:");

        printClusterData();
    }


    private static void printClusterData() {
        for (int i = 0; i < distanceList.size(); i++)
            System.out.println("Iteration " + i + ": " + distanceList.get(i));

        for (int i = 0; i < clusters.size(); i++) {
            HashMap<String, Integer> typeOccurrenceMap = new HashMap<>();

            for (String correctType : clusters.get(i).getSamples().stream().map(DataSample::getCorrectType).toList()) {
                if (typeOccurrenceMap.containsKey(correctType))
                    typeOccurrenceMap.put(correctType, typeOccurrenceMap.get(correctType) + 1);
                else
                    typeOccurrenceMap.put(correctType, 1);
            }

            double sampleSetSize = clusters.get(i).getSamples().size();
            System.out.println("\nCluster " + i + ":\n");

            for (Map.Entry<String, Integer> typeOccurenceEntry : typeOccurrenceMap.entrySet()) {
                System.out.printf("%s(%d): %.2f%% %n", typeOccurenceEntry.getKey(), typeOccurenceEntry.getValue(),
                        typeOccurenceEntry.getValue() / sampleSetSize * 100);
            }
        }
    }


    private static List<Double> runClustering(List<Cluster> clusters, List<DataSample> dataSamples) {
        List<Double> distanceList = new ArrayList<>();
        boolean repeat;
        int repeatCount = 0;

        while (repeatCount != 2) {
            repeat = true;

            for (Cluster cluster : clusters) {
                cluster.calculateCentroid();
                cluster.getSamples().clear();
            }

            double totalSampleDistance = 0.0;

            for (DataSample sample : dataSamples) {
                List<Double> currDistanceList = new ArrayList<>();

                for (Cluster cluster : clusters) {
                    currDistanceList.add(sample.calculateEuclidianDistance(cluster.getCentroid()));
                }

                double minDistance = Collections.min(currDistanceList);
                totalSampleDistance += minDistance;

                Cluster currentCluster = clusters.get(currDistanceList.indexOf(minDistance));

                if (currentCluster != sample.getCluster()) {
                    repeat = false;
                    sample.setCluster(currentCluster);
                }

                clusters.get(clusters.indexOf(currentCluster)).getSamples().add(sample);
            }

            distanceList.add(totalSampleDistance);

            if (repeat) {
                repeatCount++;
            }
        }

        return distanceList;
    }
}
