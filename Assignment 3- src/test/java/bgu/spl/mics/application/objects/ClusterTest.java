//package bgu.spl.mics.application.objects;
//
//import junit.framework.TestCase;
//import org.junit.Test;
//import org.junit.jupiter.api.BeforeEach;
//
//import java.util.Vector;
//
//public class ClusterTest extends TestCase {
//    Cluster cluster;
//    GPU gpu;
//    CPU cpu;
//    Data data;
//    int startIdx;
//    private Vector<DataBatch> proBatches;
//    private Vector<DataBatch> unProBatches;
//    private DataBatch batch;
//
//    @BeforeEach
//    public void setUp() throws Exception {
//        super.setUp();
//        cluster = Cluster.getInstance();
//        gpu = new GPU(GPU.Type.RTX3090);
//        cpu = new CPU(5);
//        data = new Data(Data.Type.TABULAR);
//        startIdx = 0;
//        proBatches = new Vector<DataBatch>();
//        unProBatches = new Vector<DataBatch>();
//        DataBatch batch = new DataBatch(data, startIdx);
//    }
//
//    public void tearDown() throws Exception {
//    }
//
//    @Test
//    public void testReceiveProBatch() {
//        assertTrue("The batch is not processed" ,batch.isProcessed());
//        assertEquals("The processed batches collection is not empty",0, proBatches.size());
//        assertFalse("There is an available processed Batch" ,cluster.proBatchAvailable());
//
//        cluster.receiveProBatch(batch);
//        assertEquals("The batch has not been received", 1, proBatches.size());
//        assertTrue("The batch has not been received",cluster.proBatchAvailable());
//    }
//
//    @Test
//    public void testReceiveUnProBatch() {
//        assertFalse("The batch is processed" ,batch.isProcessed());
//        assertEquals("The unprocessed batches collection is not empty",0, unProBatches.size());
//        assertFalse("There is an available unprocessed Batch" ,cluster.unProBatchAvailable());
//
//        cluster.receiveProBatch(batch);
//        assertEquals("The batch has not been received", 1, unProBatches.size());
//        assertTrue("The batch has not been received",cluster.unProBatchAvailable());
//    }
//
//    @Test
//    public void testSendProBatch() {
//        assertTrue("The batch is unProcessed",batch.isProcessed());
//        cluster.receiveProBatch(batch);
//        assertTrue(cluster.proBatchAvailable());
//        assertEquals("The batch has not been received",1,proBatches.size());
//
//        cluster.sendProBatch();
//        assertEquals("The batch has not been sent", 0, proBatches.size());
//        assertFalse("there's an available Processed batch",cluster.proBatchAvailable());
//    }
//
//    @Test
//    public void testSendUnProBatch() {
//        assertFalse("The batch is Processed",batch.isProcessed());
//        cluster.receiveUnProBatchCase1(batch);
//        assertTrue(cluster.unProBatchAvailable());
//        assertEquals("The batch has not been received",1,unProBatches.size());
//
//        cluster.sendUnProBatch();
//        assertEquals("The batch has not been sent", 0, unProBatches.size());
//        assertFalse("there's an available Processed batch",cluster.unProBatchAvailable());
//    }
//
//    @Test
//    public void testProBatchAvailable() {
//        assertFalse(cluster.proBatchAvailable());
//
//        cluster.receiveProBatch(batch);
//        assertTrue("Theres is no available processed batch",cluster.proBatchAvailable());
//    }
//
//    @Test
//    public void testUnProBatchAvailable() {
//        assertFalse(cluster.unProBatchAvailable());
//
//        cluster.receiveUnProBatch(batch);
//        assertTrue("Theres is no available unprocessed batch",cluster.unProBatchAvailable());
//    }
//
//    @Test
//    public void testAddNameTrainedModel() {
//        Model model = new Model("Train", data, new Student("Oren", "Computer Science", Student.Degree.MSc), Model.Status.PRETRAINED, Model.Result.NONE);
//
//        assertEquals(0, cluster.getNamesModelsTrained().size());
//        cluster.addNameTrainedModel(model);
//        assertEquals("list of the models names has not been updated",1, cluster.getNamesModelsTrained().size());
//    }
//
//    @Test
//    public void testUpdateNumCpuTimeUsed() {
//    }
//
//    @Test
//    public void testUpdateNumGpuTimeUsed() {
//    }
//}