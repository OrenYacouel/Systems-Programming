package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.GPUService;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

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

    private Type type;
    private Model currModel; // the model the GPU is currently working on.  (null for none)
    private Cluster cluster; // a pointer to the singleton compute cluster
    private int numOfBatchesLeft;
    private GPUService service;
    private final int ticksToTrainData;
    private AtomicInteger floatingBatches = new AtomicInteger(0);
    private AtomicInteger processingTicks = new AtomicInteger(0);
    private AtomicInteger ticksSinceTrainingBatchStarted = new AtomicInteger(0);
    private ConcurrentLinkedQueue<Model> unProModels; //im not sure if we need this
    private ConcurrentLinkedQueue <DataBatch> disk;
    private ConcurrentLinkedQueue <DataBatch> VRAM; //processed batches
    private int VRAMCap;


//    constructor
    public GPU(Type _type){
        type = _type;
        currModel = null;
        cluster = Cluster.getInstance();
        unProModels = new ConcurrentLinkedQueue<Model>();
        disk = new ConcurrentLinkedQueue <DataBatch>();
        VRAM = new ConcurrentLinkedQueue <>();
        numOfBatchesLeft = 0;

//      init VRAM capacity according to type
        if (type == Type.RTX3090) {
            VRAMCap = 32;
            ticksToTrainData = 1;
        }
        else if (type == Type.RTX2080) {
            VRAMCap = 16;
            ticksToTrainData = 2;
        }
        else // if (type == Type.GTX1080)
        {
            VRAMCap = 18;
            ticksToTrainData = 4;
        }
    }

    public boolean areThereBatchesToProcess(){
        return !disk.isEmpty() ;
    }
    public float getNumOfBatchesLeft(){
        return (float)numOfBatchesLeft;
    }

    public void tickAction(){
        if ( currStatus() == Model.Status.TRAINING ){
            cluster.incrementNumGpuTimeUsed();
            processingTicks.getAndIncrement();
            ticksSinceTrainingBatchStarted.getAndIncrement();
            float ModelDataSizeInBatches = this.currModel.getData().getDataSize()/1000;
            //if the model is at 90% trained we will do case 2
            if (getNumOfBatchesLeft() / ModelDataSizeInBatches <= 0.1) {
                if (shouldAviramSendBatch() && disk.size() > 0) {
                    for (int i = 0; i < 2; i++) {
                        cluster.receiveUnProBatchCase2(sendUnProBatch());
                    }
                }
            }
            //else, we will just add the batches to unProBatches3 .
            else {
                if (shouldAviramSendBatch() && disk.size() > 0) {
                    for (int i = 0; i < 2; i++) {
                        cluster.receiveUnProBatchCase3(sendUnProBatch());
                    }
                }
            }
            if (ticksSinceTrainingBatchStarted.get() == ticksToTrainData && !VRAM.isEmpty()){
                finishBatchTraining();
            }
            while ((VRAMCap - VRAM.size() > 0) && cluster.canGiveToGPU(this)) {
                receiveBatch(cluster.sendProBatch(this));
            }
            if( VRAM.isEmpty() && !areThereBatchesToProcess() && floatingBatches.get() == 0 ) {
                currModel.setStatus(Model.Status.TRAINED);
                System.out.println("finished model training" + currModel.getName());
                cluster.getNamesModelsTrained().add(currModel.getName());
            }
        }
    }



    /**
     * this func takes the data of the model, and divides it into batches and keeps it in the disk
     */
    public void batchMaker(){
        Data data = currModel.getData();
        int numOfSamples = data.getDataSize();
        int i = 0;
        while( i < numOfSamples) {
            DataBatch batch = new DataBatch(data, i);
            disk.add(batch);
            numOfBatchesLeft++;
            i +=1000;
        }
    }
    public void prepareAndSendFirstBatches(){
        if ( !unProModels.isEmpty() || currModel.getStatus() == Model.Status.PRETRAINED ){
            if ( currModel.getStatus() == Model.Status.TRAINED ) {
                setCurrModel(getUnProModels().poll());
            }
            floatingBatches = new AtomicInteger(0);;//we reset the sent Batches for each model
            float ModelDataSizeInBatches = this.currModel.getData().getDataSize()/1000;
            this.currModel.setStatus(Model.Status.TRAINING);
            this.cluster.getDataSources().put(this,currModel.getData()); // adds the data-gpu to the cluster's map
            batchMaker(); //adds the batches to the GPU's disk
            for (int i = 0; i < 2; i++){ //sends 2 batches
                this.cluster.receiveUnProBatchCase1(sendUnProBatch());
            }
        }
    }

    public boolean doesAviramHaveFreeSpace(){
        return ( VRAMCap - VRAM.size() > 0 );
    }
    public boolean shouldAviramSendBatch(){ return VRAMCap - floatingBatches.get() - VRAM.size() > 0;}
    /**
     * sends a batch of unprocessed data to the cluster
     * @pre disk.size() != 0
     * @return disk.dequeue()
     */
    public DataBatch sendUnProBatches(Model model) {
        if( !disk.isEmpty() )
            return disk.poll();
        else return null;
    }
    /**
     * @pre vramCap > 0
     * @param batch - get processed batch from the cluster
     *  place it in VRAM (if it has capacity) and update stats
     */
    public void receiveBatch(DataBatch batch){
        if(doesAviramHaveFreeSpace()) {
            VRAM.add(batch);
            floatingBatches.getAndDecrement();
        }
    }
    /**
     *
     * @after the batch will be trained
     */
    public void finishBatchTraining(){
        VRAM.remove();
        ticksSinceTrainingBatchStarted = new AtomicInteger(0);
        numOfBatchesLeft--;
    }

    public DataBatch sendUnProBatch() {
            floatingBatches.getAndIncrement();
            return disk.poll();
    }

    public int numOfUntrainedBatches() {return this.VRAM.size();}
    public Model.Status currStatus (){
        if(currModel == null)
            return null;
        return currModel.getStatus();
    }

//  setters
    public void setCurrModel(Model currModel) {
        this.currModel = currModel;
    }

//  getters
    public Cluster getCluster(){return this.cluster;}
    public ConcurrentLinkedQueue <DataBatch> getVRAM(){ return VRAM;}
    public ConcurrentLinkedQueue<Model> getUnProModels() {
        return unProModels;
    }
    public Model getCurrModel() {
        if (currModel == null)
            return null;
        return currModel;
    }
}
