package objects;

import java.util.List;
import java.util.Objects;

public class PerceptronLayer {

    List<Perceptron> perceptrons;


    public PerceptronLayer(List<Perceptron> perceptrons) {
        this.perceptrons = perceptrons;
    }

    public double estimateSetAsLayer(List<DataSample> testSamples, String mode) {
        double correctCount = 0, totalCount = 0;
        double accuracy;

        for (DataSample sample : testSamples) {
            String layerEstimation = estimateSingleTestSampleAsLayer(sample, mode);

            if (layerEstimation.equals(sample.correctType))
                correctCount++;

            totalCount++;
        }

        accuracy = correctCount / totalCount;

        return accuracy;
    }


    public String estimateSingleTestSampleAsLayer(DataSample sample, String mode) {
        int correctIndex = -1;
        double highestResult = -1, currentResult = -1;

        for (int i = 0; i < perceptrons.size(); i++) {
            if (Objects.equals(mode, "training"))
                currentResult = perceptrons.get(i).estimateSingleTrainSample(sample);
            else if (Objects.equals(mode, "test"))
                currentResult = perceptrons.get(i).estimateSingleTestSample(sample);

            if (currentResult > highestResult) {
                correctIndex = i;
                highestResult = currentResult;
            }
        }

        return perceptrons.get(correctIndex).assignedType;
    }
}
