package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.Cluster;

/**
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    private CPU cpu;

    public CPUService(String name, CPU _cpu) {
        super(name);
        cpu = _cpu;
    }
    @Override
    protected void initialize() {
        Cluster.getInstance().addCpu(this.cpu);
        subscribeBroadcast(TickBroadcast.class, new Callback<TickBroadcast>() {
            @Override
            public void call(TickBroadcast c) {
                if ( !cpu.isProcessing()){
                    if (! cpu.getCluster().getUnProBatches1().isEmpty()){
                        cpu.receiveBatch(cpu.getCluster().getUnProBatches1().poll());
                    }
                    else if (! cpu.getCluster().getUnProBatches2().isEmpty()){
                        cpu.receiveBatch(cpu.getCluster().getUnProBatches2().poll());
                    }
                    else if (! cpu.getCluster().getUnProBatches3().isEmpty()){
                        cpu.receiveBatch(cpu.getCluster().getUnProBatches3().poll());
                    }
                }
                else { // we are processing at the moment
                    cpu.incrementTicksSinceStartedProcessing();
                    cpu.getCluster().incrementNumCpuTimeUsed();
                    if (cpu.getTicksSinceStartedProcessing().get() == cpu.getTicksToProcessBatch()){
                        cpu.finalizeProcess();
                    }
                }
            }
        });

    }
}
