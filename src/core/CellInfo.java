package core;

import java.io.Serializable;

public class CellInfo implements Serializable {

    private static final long serialVersionUID = 458988423983274L;
    private String type = null, name = null;
    private int mark = 0;
    private byte weight = 0;
    private double probability = 1.0, variance = 1.0, lambda = 0;
    private boolean immediate = false, blocked = false;

    public CellInfo(String name) {
        this.type = "container";
        this.name = name;
    }

    public CellInfo(int mark) {
        this.type = "place";
        this.mark = mark;
    }

    public CellInfo(double variance) {
        this.type = "transition";
        this.variance = variance;
        this.immediate = (variance == 0.0);
    }

    public CellInfo(byte weight) {
        this.type = "edge";
        this.weight = weight;
    }

    public CellInfo() {
        this(null);
    }

    public int getMark() {
        return mark;
    }

    public byte getWeight() {
        return weight;
    }

    public String getType() {
        return type;
    }

    public double getVariance() {
        return variance;
    }

    public double getProbability() {
        return probability;
    }

    public double getLambda() {
        return lambda;
    }

    public String getName() {
        return name;
    }

    public boolean isImmediate() {
        return immediate;
    }

    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public void setWeight(byte weight) {
        this.weight = weight;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setVariance(double variance) {
        this.variance = variance;
        this.immediate = (variance != 0) ? false : true;
    }

    public void setLambda(double lambda) {
        this.lambda = lambda;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMarkAsString() {
        StringBuilder sb = new StringBuilder();
//        sb.append("\n\n");
        sb.append(mark);
//        sb.append("\n\n");
        return sb.toString();
    }

    public String getVarianceAsString() {
        StringBuilder sb = new StringBuilder();
//        sb.append("\n\n\n");
        sb.append(variance);
//        sb.append("\n\n\n");
        return sb.toString();
    }

    public String getProbabilityAsString() {
        StringBuilder sb = new StringBuilder();
//        sb.append("\n\n\n");
        sb.append(probability);
//        sb.append("\n\n\n");
        return sb.toString();
    }

    public String getWeightAsString() {
        String string = weight + "";
        return string;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.type.equals("place")) {
            sb.append(getMarkAsString());
//            sb.append(" ");
        }
        if (this.type.equals("transition")) {
            sb.append(getProbabilityAsString());
//            sb.append(" ");
        }
        if (this.type.equals("container")) {
            sb.append(name);
        }

        if (this.type.equals("edge")) {
            sb.append(getWeightAsString());
//            sb.append(" ");
        }
        return sb.toString();
    }

    public boolean isTransition() {
        return (type.equals("transition")) ? true : false;
    }

    public boolean isContainer() {
        return (type.equals("container")) ? true : false;
    }

    public boolean isVertex() {
        return (type.equals("place")) ? true : false;
    }

    public boolean isEdge() {
        return (type.equals("edge")) ? true : false;
    }

    public boolean isBlocked() {
        return blocked;
    }
}
