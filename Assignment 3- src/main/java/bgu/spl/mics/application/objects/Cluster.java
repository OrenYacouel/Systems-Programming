package bgu.spl.mics.application.objects;


import bgu.spl.mics.MessageBusImpl;

import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

	private static class SingletonHolder{
		private static Cluster instance = new Cluster();
	}

	private Queue<DataBatch> unProBatches1; // unprocessed batches from GPUs which their VRAM is empty (at the time of sending)
	private Queue<DataBatch> unProBatches2; // unprocessed batches of almost ready Models
	private Queue<DataBatch> unProBatches3; //unprocessed batches which do not reach the previous conditions

	private ConcurrentHashMap < GPU , Data > dataSources;
	private ConcurrentHashMap < Data , Queue<DataBatch> > proBatches;// A collection of all the processed batches which are in the cluster right now

	private Vector<GPU> GPUs; //A collection of GPU - All the gpus in the system.
	private Vector<CPU> CPUs; //A collection of CPU - All the gpus in the system.

	//Statistics
	private Vector<String> namesModelsTrained; // Names of all the models trained
	private AtomicInteger numProBatches = new AtomicInteger(0); //Total number of data batches processed by the CPUs
	private AtomicInteger cpuTimeUsed = new AtomicInteger(0); // Number of CPU time units used
	private AtomicInteger gpuTimeUsed = new AtomicInteger(0); // Number of GPU time units used

	//Constructor
	private Cluster(){
		unProBatches1 = new LinkedBlockingDeque<DataBatch>() ;
		unProBatches2 = new LinkedBlockingDeque<DataBatch>() ;
		unProBatches3 = new LinkedBlockingDeque<DataBatch>() ;
		dataSources = new ConcurrentHashMap<>();
		proBatches = new ConcurrentHashMap<>();

		GPUs = new Vector<GPU>();
		CPUs = new Vector<CPU>();
		namesModelsTrained = new Vector<String>();
	}

	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance(){
		return Cluster.SingletonHolder.instance;
	}

	public void addGpu(GPU gpu){
		GPUs.add(gpu);
	}

	public void addCpu(CPU cpu){
		CPUs.add(cpu);
	}

//	sends processed batch to gpu if available
	public DataBatch sendProBatch(GPU gpu){
		Data data = dataSources.get(gpu);
		if (!proBatches.get(data).isEmpty()){
			return proBatches.get(data).poll();
		}
		return null;
	}

	public boolean canGiveToGPU (GPU gpu){
		Data data = dataSources.get(gpu);
		if (! proBatches.containsKey(data)) {
			return false;
		}
		return !proBatches.get(data).isEmpty();
	}

	//receives processed batch from the CPU
	public void receiveProBatch(DataBatch batch){
		if (!proBatches.containsKey(batch.getData())){ //if this is the first batch from this data
			proBatches.put(batch.getData() , new ConcurrentLinkedDeque<>());
		}
		proBatches.get(batch.getData()).add(batch);
		numProBatches.getAndIncrement() ;
	}

	//receives unprocessed batch from the GPU
	public void receiveUnProBatchCase1(DataBatch batch){
		if(batch != null)
			unProBatches1.add(batch);
	}

	//receives unprocessed batch from the GPU
	public void receiveUnProBatchCase2(DataBatch batch){
		if(batch != null)
			unProBatches2.add(batch);
	}

	//receives unprocessed batch from the GPU
	public void receiveUnProBatchCase3(DataBatch batch){
		if(batch != null)
			unProBatches3.add(batch);
	}

	//adds a name of trained model
	public void addNameTrainedModel(Model model){
		namesModelsTrained.add(model.getName());
	}

	// updates the number of CPU time units used
	public void incrementNumCpuTimeUsed(){
		cpuTimeUsed.getAndIncrement();
	}

	// updates the number of GPU time units used
	public void incrementNumGpuTimeUsed(){
		gpuTimeUsed.getAndIncrement();
	}

	public boolean unProBatchAvailable(){return (unProBatches1.size()>0 || unProBatches2.size()>0 || unProBatches3.size()>0);}
//	SETTERS


	//GETTERS

	public ConcurrentHashMap<GPU, Data> getDataSources() {
		return dataSources;
	}
	public Vector<String> getNamesModelsTrained(){ return namesModelsTrained; }

	public Queue<DataBatch> getUnProBatches1() {
		return unProBatches1;
	}
	public Queue<DataBatch> getUnProBatches2() {
		return unProBatches2;
	}
	public Queue<DataBatch> getUnProBatches3() {
		return unProBatches3;
	}

	public int getNumProBatches(){
		return Integer.parseInt(numProBatches.toString());
	}

	public int getCpuTimeUsed(){
		return Integer.parseInt(cpuTimeUsed.toString());
	}

	public int getGpuTimeUsed(){
		return Integer.parseInt(gpuTimeUsed.toString());
	}

//	public String toString(){
//		String output = "";
//		output += "\t\"cpuTimeUsed\": \"" + cpuTimeUsed + "\",\n";
//		output += "\t\"gpuTimeUsed\": \"" + gpuTimeUsed + "\",\n";
//		output += "\t\"batchesProcessed\": \"" + numProBatches + "\",\n";
//
//		return output;
//	}
}
