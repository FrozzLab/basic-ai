package objects;

import java.util.List;

public class DataSample {

    List<Double> inputData;
    int correctAnswer;


    public DataSample(List<Double> inputData, int correctAnswer) {
        this.inputData = inputData;
        this.correctAnswer = correctAnswer;
    }


    public DataSample(DataSample sample) {
        this.inputData = sample.inputData;
        this.correctAnswer = sample.correctAnswer;
    }


    public List<Double> getInputData() {
        return inputData;
    }
}
