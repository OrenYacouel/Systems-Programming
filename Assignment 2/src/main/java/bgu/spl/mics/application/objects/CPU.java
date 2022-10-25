package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.CPUService;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int cores; // number of cores.
    private DataBatch currBatch; //the data the cpu currently processing
    private Cluster cluster; //  a pointer to the signleton compute cluster
    private CPUService service;
    private boolean isProcessing;
    private AtomicInteger ticksSinceStartedProcessing = new AtomicInteger(0);
    private int ticksToProcessBatch;


    public CPU( int _cores ) {
        cores = _cores;
        currBatch = null;
        cluster = Cluster.getInstance();
        isProcessing = false;
    }


    /**
     * This function receives a dataBatch from the cluster
     * @param batch is a DataBatch received from the cluster.
     * @pre {@param batch} != null && {@param batch}.isProcessed == false
     * @post
     */
    public void receiveBatch(DataBatch batch){
        if (batch != null) {
            currBatch = batch;
            setProcessing(true); //maybe already true- we don't care
            if (batch.data.getType() == Data.Type.IMAGES)
                ticksToProcessBatch = (32 / cores) * 4;
            else if (batch.data.getType() == Data.Type.TEXT)
                ticksToProcessBatch = (32 / cores) * 2;
            else if (batch.data.getType() == Data.Type.TABULAR)
                ticksToProcessBatch = (32 / cores) * 1;
            ticksSinceStartedProcessing = new AtomicInteger(0);//resets this ticks counter
        }
    }

    /**
     * This function takes a dataBatch and updated his status to processed and updates statistics.
     * currBatch is a DataBatch with status == unprocessed.
     * @pre {@param batch} != null &&{@param batch}.isProcessed == false
     * @post {@param batch}.isProcessed == true
     */
    public void finalizeProcess(){
        currBatch.process();
        isProcessing = false;
        sendProcessedBatch(currBatch); //sends this batch to the cluster
    }

    /**
     * This function sends a dataBatch from the CPU to the cluster.
     * @param batch is a DataBatch sent from the CPU to the cluster.
     * @pre {@param batch} != null && {@param batch}.isProcessed == true
     * @post cluster.getNumProcessedBatch = @pre(cluster.getNumProcessedBatch) + 1
     */
    public void sendProcessedBatch(DataBatch batch){
        cluster.receiveProBatch(currBatch);
    }

    public void incrementTicksSinceStartedProcessing(){
        ticksSinceStartedProcessing.getAndIncrement();
    }
//    questions
    public boolean isProcessing(){
        return isProcessing;
    }

//    setter

    public void setProcessing(boolean processing) {
        isProcessing = processing;
    }

//    getters
    public Cluster getCluster() {
        return cluster;
    }

    public AtomicInteger getTicksSinceStartedProcessing() {
        return ticksSinceStartedProcessing;
    }

    public int getTicksToProcessBatch() {
        return ticksToProcessBatch;
    }

    public DataBatch getBatch() {
        return currBatch;
    }
}

