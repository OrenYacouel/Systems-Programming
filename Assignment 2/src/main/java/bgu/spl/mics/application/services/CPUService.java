package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.CRMSRunner;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.messages.TerminateBroadcast;

/**
 * This class may not hold references for objects which it is not responsible for.
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class
CPUService extends MicroService {
    CPU myCPU;
    Cluster cluster;
    int workCounter;
    long processTime;
    public boolean shouldCount;

    public CPUService(String name, CPU _cpu) {
        super(name);
        this.myCPU = _cpu;
        this.cluster = Cluster.getInstance();
        this.workCounter = 0;
        this.processTime = 0;
        this.shouldCount = false;
    }

    @Override
    protected void initialize() {
        this.subscribeBroadcast(TickBroadcast.class, (TickBroadcast broadcast) -> {
            workCounter++;
            if (myCPU.getDataToProcess() != null && workCounter == processTime) {
                myCPU.process();
                myCPU.upCpuWorkTime(workCounter);
            }
            if (!cluster.getUnprocessedDataQueue().isEmpty() && myCPU.isAvailable()) {
                myCPU.takeDataBatchesFromCluster();
                try{
                    processTime = myCPU.processDataTime(myCPU.getDataToProcess().type);
                    myCPU.setAsNotAvailable();
                    workCounter = 0;
                }
                catch (NullPointerException ignored){}
            }
        });

        this.subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast) -> {
            terminate();
        });
        CRMSRunner.initializeCountLatch.countDown();
    }
}