package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    public boolean isProcessed;
    public final int index_start;
    public final int index_end;
    public Data.Type type;
    public int gpuID;


    public DataBatch(int index_start, int index_end, Data.Type _type, int _gpuID){
        this.index_start = index_start;
        this.index_end = index_end;
        this.type = _type;
        this.gpuID = _gpuID;
    }

}