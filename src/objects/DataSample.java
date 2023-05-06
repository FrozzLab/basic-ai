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


    public double calculateEuclidianDistance (List<Double> centroid) {
        double preRootSum = 0.0;

        for (int i = 0; i < centroid.size(); i++)
            preRootSum += Math.pow((centroid.get(i) - inputVector.get(i)), 2);

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
