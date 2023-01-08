package bgu.spl.mics.application.objects;


import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class Cluster {
	boolean terminated;
	ConcurrentLinkedQueue<DataBatch> unprocessedDataQueue;
	HashMap<Integer, ConcurrentLinkedQueue<DataBatch>> gpuQueueMap;
	Vector<CPU> cpuVector;


	/**
	 * Retrieves the single instance of this class.
	 */
	private static class ClusterHolder {
		private static final Cluster instance = new Cluster();
	}
	public static Cluster getInstance() {
		return Cluster.ClusterHolder.instance;
	}

	private Cluster(){
		 unprocessedDataQueue = new ConcurrentLinkedQueue<>();
		 gpuQueueMap = new HashMap<>();
		 cpuVector = new Vector<>();
		 terminated = false;
	}

	public void addGPUToList(int gpuID){
		gpuQueueMap.put(gpuID, new ConcurrentLinkedQueue<>());
	}
	public void addCPUToVector(CPU cpu){
		cpuVector.add(cpu);
	}

	public ConcurrentLinkedQueue<DataBatch> getUnprocessedDataQueue() {
		return unprocessedDataQueue;
	}

	public HashMap<Integer, ConcurrentLinkedQueue<DataBatch>> getGpuQueueMap() {
		return gpuQueueMap;
	}
}
