package objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Perceptron {

    List<Double> weightVector;
    double bias;
    double learningRate;


    public Perceptron(int weightVectorLength, double learningRate) {
        Random random = new Random();
        weightVector = new ArrayList<>();

        for (int i = 0; i < weightVectorLength; i++) {
            weightVector.add(random.nextDouble());
        }

        bias = random.nextDouble();
        this.learningRate = learningRate;
    }


    public double estimateSet(List<DataSample> testSamples, String mode) {
        boolean perceptronIsCorrect = false;
        double correctCount = 0, totalCount = 0;
        double accuracy;

        for (DataSample sample : testSamples) {
            if (Objects.equals(mode, "training"))
                perceptronIsCorrect = estimateSingleTrainSample(sample);
            else if (Objects.equals(mode, "test"))
                perceptronIsCorrect = estimateSingleTestSample(sample);

            if (perceptronIsCorrect)
                correctCount++;

            totalCount++;
        }

        accuracy = correctCount / totalCount;

        return accuracy;
    }


    public boolean estimateSingleTrainSample(DataSample sample) {
        int out = getOutput(sample.inputData);
        int outDesired = sample.correctAnswer;
        double constant = learningRate * (outDesired - out);

        weightVector = modifyWeightVector(sample.inputData, constant);
        bias -= constant;

        return out == outDesired;
    }


    public boolean estimateSingleTestSample(DataSample sample) {
        int out = getOutput(sample.inputData);
        int outDesired = sample.correctAnswer;

        return out == outDesired;
    }


    private List<Double> modifyWeightVector(List<Double> inputVector, double constant) {
        List<Double> newInputVector = new ArrayList<>();
        List<Double> newWeightVector = new ArrayList<>();

        for (int i = 0; i < inputVector.size(); i++) {
            newInputVector.add(inputVector.get(i) * constant);
            newWeightVector.add(weightVector.get(i) + newInputVector.get(i));
        }

        return newWeightVector;
    }


    public int getOutput(List<Double> inputVector) {
        double dotProduct = 0;

        for (int i = 0; i < inputVector.size(); i++) {
            dotProduct += inputVector.get(i) * weightVector.get(i);
        }

        return dotProduct - bias >= 0 ? 1 : 0;
    }
}
