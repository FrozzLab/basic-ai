package objects;

import java.util.ArrayList;
import java.util.List;

public class DataSample {

    List<Double> inputVector;
    String correctType;
    Cluster cluster;


    public DataSample(List<Double> inputVector, String correctType) {
        this.inputVector = inputVector;
        this.correctType = correctType;
    }


    public double calculateEuclidianDistance (List<Double> centroid, DataSample sample) {
        List<Double> centroidVector = new ArrayList<>(centroid);
        List<Double> sampleVector = new ArrayList<>(sample.getInputVector());
        double preRootSum = 0.0;

        for (int i = 0; i < centroidVector.size(); i++)
            preRootSum += Math.pow((centroidVector.get(i) - sampleVector.get(i)), 2);

        return Math.sqrt(preRootSum);
    }


    public String getCorrectType() {
        return correctType;
    }


    public int getInputVectorSize() {
        return inputVector.size();
    }


    public List<Double> getInputVector() {
        return inputVector;
    }


    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }
}
