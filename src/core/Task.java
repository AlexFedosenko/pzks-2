package core;

/**
 * User: Alex
 * Date: 14.05.13
 * Time: 21:48
 */
public class Task {
    private boolean ready = true;
    private int weight = 0;
    public boolean executed = false;
    private int number = 0;
    public int startTime = 0;
    public int endTime = 0;
    public Processor processor;
    public boolean processing = false;

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Task clone() {
        Task result = new Task();
        result.setReady(ready);
        result.setWeight(weight);
        result.executed = executed;
        result.setNumber(number);
        result.startTime = startTime;
        result.endTime = endTime;
        result.processing = processing;
        result.processor = processor;
        return result;
    }
}
