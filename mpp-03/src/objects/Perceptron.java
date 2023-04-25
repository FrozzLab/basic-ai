package objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Perceptron {

    List<Double> weightVector;
    String assignedType;
    double bias;
    double learningRate;


    public Perceptron(int weightVectorLength, String assignedType, double learningRate) {
        Random random = new Random();
        weightVector = new ArrayList<>();

        for (int i = 0; i < weightVectorLength; i++) {
            weightVector.add(random.nextDouble());
        }

        bias = random.nextDouble();
        this.assignedType = assignedType;
        this.learningRate = learningRate;
    }


    public double estimateSingleTrainSample(DataSample sample) {
        double out = getOutput(sample.inputVector);
        int outDesired = (assignedType.equals(sample.correctType)) ? 1 : 0;
        double constant = learningRate * (outDesired - out);

        weightVector = modifyWeightVector(sample.inputVector, constant);
        bias -= constant;

        return out;
    }


    public double estimateSingleTestSample(DataSample sample) {
        return getOutput(sample.inputVector);
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


    public double getOutput(List<Double> inputVector) {
        double dotProduct = 0;

        for (int i = 0; i < inputVector.size(); i++) {
            dotProduct += inputVector.get(i) * weightVector.get(i);
        }

        return dotProduct - bias;
    }
}
