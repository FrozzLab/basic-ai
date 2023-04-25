package models;

public class DecisionDistanceContainer {

    String decision;
    Double distance;

    public DecisionDistanceContainer(String decision, Double distance) {
        this.decision = decision;
        this.distance = distance;
    }

    public Double getDistance() {
        return distance;
    }

    public String getDecision() {
        return decision;
    }

    @Override
    public String toString() {
        return "{decision='" + decision + '\'' +
                ", distance=" + distance +
                '}';
    }
}
