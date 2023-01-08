package bgu.spl.mics.application.objects;
import bgu.spl.mics.application.services.CPUService;


/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    private int id;
    private final int cores;
    private DataBatch dataToProcess;
    private final Cluster cluster;
    boolean is_available;
    int batchesProcessed;
    int cpuTimeUsed;


    public CPU(int _cores,int _id){
        this.id = id;
        this.dataToProcess = null;
        this.cores = _cores;
        this.cluster = Cluster.getInstance();
        this.is_available = true;
        this.batchesProcessed = 0;
        this.cpuTimeUsed = 0;
        cluster.addCPUToVector(this);
    }

    /**
    @PRE : !data.isEmpty()
    @Post: each data is processed && process all from start -> end && become is_available again
    */
    public void process(){
        dataToProcess.isProcessed = true;
        batchesProcessed++ ;
        this.returnProcessedDataToCluster(dataToProcess);
    }

    /**

     @PRE: CPU.isAvailable()
     @PRE: this.data.isEmpty()
     @PRE: !cluster.getInstance().getUnprocessedDataQueue().isEmpty()
     @Post: cluster.getInstance().newData == cluster.getInstance().data && !CPU.isAvailable()
     */
    public void takeDataBatchesFromCluster() {
        if (isAvailable() && !cluster.getUnprocessedDataQueue().isEmpty()) {
            dataToProcess = cluster.getUnprocessedDataQueue().poll();
        }
    }

    /**
     * @param  processedData : list of now processed dataBatches
     @PRE: this.data.isProcessed
     @Post: cluster.getInstance().processedDataDeque.size() == cluster.getInstance().processedDataDeque.size() + 1 && CPU.isAvailable()
     @Post: this.data.isEmpty()
     @Post: this.isAvailable()
     */
    void returnProcessedDataToCluster(DataBatch processedData){
        int gpuID = processedData.gpuID;
        cluster.getGpuQueueMap().get(gpuID).add(processedData);
        dataToProcess = null;
        this.is_available = true;
    }

    /**
     * @PRE : currently not performing process
     * @Inv :
     * @Post: after finishing process, is available again
     *
     * @return
     */
    public boolean isAvailable(){
        return is_available && dataToProcess == null;
    }

    /**
     * @param  type : the type of data batches in this Deque
     */
    public int processDataTime(Data.Type type) {
        int output;
        if (type == Data.Type.Images) {
            output = (32 / cores) * 4;
        }
        else if (type == Data.Type.Text) {
            output = (32 / cores) * 2;
        }
        else { // Type == Tabular
            output = (32 / cores);
        }
        return output;
    }

    public DataBatch getDataToProcess() {
        return dataToProcess;
    }

    public void setAsNotAvailable(){
        this.is_available = false;
    }

    public int getBatchesProcessed() {
        return batchesProcessed;
    }
    public int getCpuTimeUsed() {
        return cpuTimeUsed;
    }

    public void upCpuWorkTime(int capacity) {
        this.cpuTimeUsed += capacity;
    }
}
