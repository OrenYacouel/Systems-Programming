package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
    public enum Result {None, Good, Bad};
    public enum Status {PreTrained, Training, Trained, Tested};
    private Result result;
    private Status status;
    private String name;
    private Data data;
    private Data.Type dataType;
    private int dataSize;

    public Model(String name, Data.Type type, int size) {
        this.name = name;
        this.dataType = type;
        this.dataSize = size;
        this.data = new Data(type, size);
        this.result = Result.None;
        this.status = Status.PreTrained;
    }
    public String getName() {
        return name;
    }

    public Data getData() {
        return data;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
