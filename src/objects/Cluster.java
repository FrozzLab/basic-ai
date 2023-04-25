package objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Cluster {


    List<DataSample> samples;
    List<Double> centroid;
    int datasetSize;


    public Cluster() {
        this.samples = new ArrayList<>();
        this.centroid = new ArrayList<>();
    }


    public void calculateCentroid() {
        List<Double> centroidInputVector = new ArrayList<>();
        double coordinateSum = 0.0;

        if (samples.size() == 0) {
            Random random = new Random();

            for (int i = 0; i < datasetSize; i++) {
                centroidInputVector.add(random.nextDouble());
            }
        }
        else {
            for (int i = 0; i < datasetSize; i++) {
                for (DataSample sample : samples) {
                    coordinateSum += sample.getInputVector().get(i);
                }

                centroidInputVector.add(coordinateSum / samples.size());

                coordinateSum = 0.0;
            }
        }

        centroid = centroidInputVector;
    }


    public List<DataSample> getSamples() {
        return samples;
    }


    public List<Double> getCentroid() {
        return centroid;
    }


    public void setDatasetSize(int datasetSize) {
        this.datasetSize = datasetSize;
    }
}
