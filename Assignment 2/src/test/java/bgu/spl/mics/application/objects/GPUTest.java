package bgu.spl.mics.application.objects;

import bgu.spl.mics.Future;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentLinkedDeque;

import static org.junit.jupiter.api.Assertions.*;

class GPUTest {

    GPU.Type type = GPU.Type.RTX3090;
    private GPU gpu;
    DataBatch dataBatch;
    Model model;
    Data data;
    Cluster cluster;
    Future<Model> result;


    @BeforeEach
    void setUp() {
    gpu = new GPU(type, 0);
    dataBatch = new DataBatch(0, 1000, Data.Type.Images, 0);
    data = new Data(Data.Type.Images, 100000 );
    model = new Model("catovic", Data.Type.Images , 1000);
    cluster = Cluster.getInstance();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testPrepBatches() {
        // PRE
        assertEquals(0, gpu.getTrainedDataCounter());
        assertEquals(gpu.model.getStatus(), Model.Status.PreTrained);
        assertTrue(gpu.getUnprocessedDataBatches().isEmpty());
        // Act
        gpu.prepBatches(gpu.model);
        // POST
        assertEquals(gpu.getUnprocessedDataBatches().size(), model.getData().getSize() / 1000);
    }

    @Test
    void testSendDataBatches() {
        // PRE
        assertFalse(gpu.getUnprocessedDataBatches().isEmpty());
        int gpuPrevUnprocessedSize = gpu.getUnprocessedDataBatches().size();
        int clusterPrevUnprocessedSize = cluster.getUnprocessedDataQueue().size();
        // Act
        gpu.sendDataBatches(1);
        // POST
        assertEquals(gpuPrevUnprocessedSize - 1, gpu.getUnprocessedDataBatches().size());
        assertEquals(cluster.getUnprocessedDataQueue().size() - 1, clusterPrevUnprocessedSize);
    }

    @Test
    void takeProcessedDataBatch() {
        // PRE
        assertTrue(gpu.getProcessedDataBatches().size() < gpu.VRAMCapacity());
        assertFalse(cluster.getGpuQueueMap().get(gpu.id).isEmpty());
        int gpuPrevProcessedSize = gpu.getProcessedDataBatches().size();
        int mineClusterPrevProcessedSize = cluster.getGpuQueueMap().get(gpu.id).size();
        // Act
        gpu.takeProcessedDataBatch(1);
        // POST
        assertEquals(gpu.getProcessedDataBatches().size() - 1 , gpuPrevProcessedSize);
        assertEquals(cluster.getGpuQueueMap().get(gpu.id).size() - 1, mineClusterPrevProcessedSize);
    }

    @Test
    void initialClusterTransfer() {
        // PRE
        assertEquals(gpu.model.getStatus(), Model.Status.PreTrained);
        assertNull(result);
        assertFalse(gpu.getUnprocessedDataBatches().isEmpty());
        int prevUnprocessedBatches = gpu.getUnprocessedDataBatches().size();
        int clusterPrevUnprocessedSize = cluster.getUnprocessedDataQueue().size();
        // Act
        gpu.initialClusterTransfer();
        // POST
        assertEquals(gpu.model.getStatus(), Model.Status.Training);
        assertFalse(result.isDone());
        assertEquals(cluster.getUnprocessedDataQueue().size() - gpu.VRAMCapacity(), clusterPrevUnprocessedSize);
        assertEquals(gpu.getUnprocessedDataBatches().size() + gpu.VRAMCapacity(), prevUnprocessedBatches);
    }

    @Test
    void completeTraining() {
        // PRE
        assertEquals(gpu.getTrainedDataCounter(), model.getData().getSize() / 1000);
        // Act
        gpu.completeTraining();
        // POST
        assertEquals(gpu.model.getStatus(), Model.Status.Trained);
        assertEquals(gpu.getTrainedDataCounter(), 0);
        assertTrue(result.isDone());

    }

    @Test
    void testModel() {
        // PRE
        assertEquals(gpu.model.getStatus(), Model.Status.Trained);
        // Act
        gpu.testModel(model, Student.Degree.MSc);
        // POST
        assertEquals(model.getStatus(), Model.Status.Tested);
        assertNotNull(result);
    }
}
