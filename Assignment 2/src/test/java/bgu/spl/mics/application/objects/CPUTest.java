package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class CPUTest {
    private CPU cpu;
    private Cluster cluster;
    private DataBatch dbToProcess;


    @BeforeEach
    void setUp() {
        cpu = new CPU(32, 0);
        dbToProcess = new DataBatch(1000, 1, Data.Type.Images, 0);
        cluster = Cluster.getInstance();
    }

    @AfterEach
    void tearDown() {
        dbToProcess = null;
        cpu.is_available = true;
    }

    @Test
    void process() {
        //PRE
        assertNotEquals(dbToProcess, null);
        assertFalse(dbToProcess.isProcessed);
        int prevProcessedCounter = cpu.batchesProcessed;
        //act
        cpu.process();
        //POST
        assertEquals(cpu.batchesProcessed - 1, prevProcessedCounter);
    }

    @Test
    void takeDataBatchesFromCluster() {
        //PRE
        assertTrue(cpu.is_available);
        assertNull(dbToProcess);
        assertFalse(cluster.getUnprocessedDataQueue().isEmpty());
        int prevClusterDataListSize = cluster.getUnprocessedDataQueue().size();
        //act
        cpu.takeDataBatchesFromCluster();
        //POST
        assertFalse(cpu.isAvailable());
        assertNotEquals(dbToProcess, null);
        assertEquals(cluster.getUnprocessedDataQueue().size() - 1, prevClusterDataListSize);
    }

    @Test
    void returnProcessedDataToCluster() {
        //PRE
        assertTrue(dbToProcess.isProcessed);
        //act
        int prevGpuDataListSize = cluster.getGpuQueueMap().get(dbToProcess.gpuID).size();
        cpu.returnProcessedDataToCluster(dbToProcess);
        //POST
        assertNull(dbToProcess);
        assertTrue(cpu.isAvailable());
        assertEquals(cluster.getGpuQueueMap().get(dbToProcess.gpuID).size() - 1, prevGpuDataListSize);
    }


    @Test
    void getCpuTimeUsed() {
    }

    @Test
    void upCpuWorkTime() {
        //PRE
        int prevWorkTime = cpu.getCpuTimeUsed();
        //act
        cpu.upCpuWorkTime(1);
        //POST
        assertEquals(cpu.getCpuTimeUsed() - 1, prevWorkTime);
    }
}
