package models;

import java.util.List;

public class Observation {
    static int idCount = 0;
    static int parameterLength;

    int id;
    List<Double> parameters;
    String decisionParameter;

    public Observation(List<Double> parameters, String decisionParameter) {
        this.id = ++idCount;
        this.parameters = parameters;
        parameterLength = parameters.size();
        this.decisionParameter = decisionParameter;
    }

    public Observation(Observation observation) {
        this.id = observation.getId();
        this.parameters = observation.getParameters();
        this.decisionParameter = observation.getDecisionParameter();
    }

    public Observation(List<Double> parameters) {
        this.id = 0;
        this.parameters = parameters;
        this.decisionParameter = "";
    }

    public static void setIdCount(int idCount) {
        Observation.idCount = idCount;
    }

    public static int getParameterLength() {
        return parameterLength;
    }

    public int getId() {
        return id;
    }

    public List<Double> getParameters() {
        return parameters;
    }

    public String getDecisionParameter() {
        return decisionParameter;
    }

    @Override
    public String toString() {
        return parameters + ", " + decisionParameter;
    }
}
