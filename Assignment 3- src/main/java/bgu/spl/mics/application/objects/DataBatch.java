package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {

    enum Status{ PROCESSED, UNPROCESSED}

    Data data;  // the Data the batch belongs to
    int startIndex; //The index of the first sample in the batch.
    Status status;

    public DataBatch(Data data, int startIndex) {
        this.data = data;
        this.startIndex = startIndex;
        status = Status.UNPROCESSED;
    }

    protected boolean isProcessed(){
        return (status == Status.PROCESSED);
    }
    public void process(){ status = Status.PROCESSED;}

    public Data getData() {
        return data;
    }
}
