package objects;

import java.util.List;

public class DataSample {

    List<Double> inputVector;
    String correctType;


    public DataSample(List<Double> inputVector, String correctType) {
        this.inputVector = inputVector;
        this.correctType = correctType;
    }


    public DataSample(DataSample sample) {
        this.inputVector = sample.inputVector;
        this.correctType = sample.correctType;
    }


    public List<Double> getInputVector() {
        return inputVector;
    }
}
