package bgu.spl.mics.application.objects;

import bgu.spl.mics.Future;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {

    /**
     * Enum representing the type of the GPU.
     */
    public enum Type {RTX3090, RTX2080, GTX1080}
    boolean isAvailable;
    int id;
    private final Type type;
    Model model;
    Cluster cluster;
    private int trainedDataCounter;
    private int gpuWorkTime;
    private final LinkedList<DataBatch> processedDataBatches;
    private final LinkedList<DataBatch> unprocessedDataBatches;
    private Future<Model> result;


    public GPU(Type _type, int _id) {
        isAvailable = true;
        this.id = _id;
        this.cluster = Cluster.getInstance();
        this.type = _type;
        this.gpuWorkTime = 0;
        trainedDataCounter = 0;
        processedDataBatches = new LinkedList<>();
        unprocessedDataBatches = new LinkedList<>();
        cluster.addGPUToList(id);
    }

    /** Organize data in data batches
     * @PRE model.data.size() > 0
     * @Post: unprocessed_data_counter * 1000 == model.data.size()
     */
    public void prepBatches(Model _model) {
        this.model = _model;
        for(int i = 1; i < model.getData().getSize(); i= i+1000){
            DataBatch dataBatch = new DataBatch(i, i+1000, model.getData().getType(), id);
            unprocessedDataBatches.add(dataBatch);
        }
    }

    /** send data batches to cluster
     * @PRE VRAM capacity >= batches
     * @Post: unprocessed_data_counter == @pre(unprocessed_data_counter - batches)
     */
    public void sendDataBatches(int numOfBatches) {
        int counter = 0;
        while (counter < numOfBatches && !unprocessedDataBatches.isEmpty() ) {
            //make sure i can add
            cluster.getUnprocessedDataQueue().add(unprocessedDataBatches.poll());
            counter++;
        }
    }

    /** receive batches from cluster
     * @PRE VRAM capacity >= processedData.size()
     * @Post: processedData.size() == @pre(processedData.size()+ 1)
     */
    public void takeProcessedDataBatch(int difference) {
        while (difference > 0 && !cluster.getGpuQueueMap().get(this.id).isEmpty()) {
            DataBatch addMe = cluster.getGpuQueueMap().get(this.id).poll();
            this.processedDataBatches.add(addMe);
            difference -- ;
        }
    }

    public int trainTime() {
        int output;
        if (type == Type.RTX3090)
            output = 1;
        else if (type == Type.RTX2080)
            output = 2;
        else
            output = 4;
        return output;
    }

    public int VRAMCapacity(){
        int capacity;
        if(type == Type.RTX3090)
            capacity = 32;
        else if(type == Type.RTX2080)
            capacity = 16;
        else
            capacity = 8;
        return capacity;
    }

    public LinkedList<DataBatch> getUnprocessedDataBatches() {
        return unprocessedDataBatches;
    }

    public LinkedList<DataBatch> getProcessedDataBatches() {
        return processedDataBatches;
    }

    /**
     * first send of data to cluster.  send max unprocessed
     * create future to be resolved when all db's have been processed
     * initialize private future result = new ...
     * return result;
     * @PRE: model.getStatus() == PreTrained
     * @POST: getUnprocessedDataBatches()-- (this.VRAMCapacity)
     * @POST: future was created for training the model process
     */

    public Future<Model> initialClusterTransfer (){
        model.setStatus(Model.Status.Training);
        result = new Future<>();
        this.sendDataBatches(getUnprocessedDataBatches().size());
        return result;
    }

    public void trainData(){
        trainedDataCounter++;
    }

    public Model getModel() {
        return model;
    }

    public int getId() {return id;}

    public int getTrainedDataCounter() {
        return trainedDataCounter;
    }
    /**
     * Called upon completion of data training
     * change status of model to TRAINED, change status of DATA TO PROCESSED
     * Resolves the future of trainModelEvent
     * Returns the trainedDataCounter to 0 for the upcoming trainModelEvent
     * @PRE : all data batches were processed and trained
     * @Post: Model's status updated to Trained
     * @Post: future has been resolved
     * @Post: trainedDataCounter set back to 0 for next Model
     */
    public void completeTraining(){
        model.setStatus(Model.Status.Trained);
        model.getData().setProcessedStatus(true);
        result.resolve(model);
        trainedDataCounter = 0 ;
        isAvailable = true;
    }

    /**
     * test the Model
     * @PRE: model.getStatus() == Trained
     * @Post: result is not null
     * @Post: model.getStatus() == Tested
     */

    public void testModel(Model model, Student.Degree degree){
        double prob = Math.random();
        if((prob < 0.6 & degree == Student.Degree.MSc) | (prob < 0.8 & degree== Student.Degree.PhD) )
            model.setResult(Model.Result.Good);

        else{
            model.setResult(Model.Result.Bad);
        }
        model.setStatus(Model.Status.Tested);
    }

    public int getGpuWorkTime() {
        return gpuWorkTime;
    }

    public void upGpuWorkTime(int capacity) {
        this.gpuWorkTime += capacity;
    }
    public synchronized boolean tryToReserve() {
        if (isAvailable) {
            isAvailable = false;
            return true;
        }
        else{
            return false;
        }
    }
}
