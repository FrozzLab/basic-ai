package objects;

import java.util.List;

public class DataSample {

    List<String> inputVector;
    String correctType;


    public DataSample(List<String> inputVector, String correctType) {
        this.inputVector = inputVector;
        this.correctType = correctType;
    }


    public String getCorrectType() {
        return correctType;
    }


    public int getInputVectorSize() {
        return inputVector.size();
    }


    public List<String> getInputVector() {
        return inputVector;
    }
}
