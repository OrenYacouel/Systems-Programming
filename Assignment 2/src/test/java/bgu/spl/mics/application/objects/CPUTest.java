//package bgu.spl.mics.application.objects;
//
//import junit.framework.TestCase;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.LinkedList;
//import java.util.Queue;
//
//public class CPUTest extends TestCase {
//    private static CPU cpu;
//    private static Cluster cluster;
//
//    @Before
//    public void setUp() throws Exception {
//        super.setUp();
//        cpu = new CPU(5);
//    }
//
//    @After
//    public void tearDown() throws Exception {
//    }
//
//    @Test
//    public void testTick() {
//        //        really depends on implementation of fields and communication with CPU service- haven't reached this part
//    }
//
//    @Test
//    public void testRequestNewUnprocessedBatch() {
////        Queue<DataBatch> queue = new LinkedList<DataBatch>();
////        DataBatch batch = null;
////
////        //No batch to send to the CPU yet
////        assertEquals( cpu.requestNewUnprocessedBatch(), null );
////
////        batch = new DataBatch( new Data(Data.Type.TABULAR), 7);
////        cluster.receiveUnProBatch(batch);
////        assertEquals(cpu.requestNewUnprocessedBatch(), batch); //the CPU should receive the batch
//    }
//
//    @Test
//    public void testProcessBatch() {
//        int numOfProcessing = cpu.getNumOfProcessing();
//        DataBatch batch = new DataBatch(new Data(Data.Type.TABULAR), 7);
//        assertFalse( numOfProcessing == numOfProcessing + 1 ); //we haven't processed the batch yet
//
//        cpu.processBatch( batch );
//        assertTrue( numOfProcessing == numOfProcessing + 1 ); //we have processed the batch.
//    }
//
//    @Test
//    public void testFinalizeProcess() {
//        DataBatch batch = new DataBatch( new Data(Data.Type.TABULAR), 7);
//        assertFalse( batch.isProcessed() ); ///we haven't finalized the batch yet
//
//        cpu.finalizeProcess( batch );
//        assertFalse( batch.isProcessed() ); //we have finalized the batch
//    }
//
//    @Test
//    public void testSendProcessedBatch() {
//        DataBatch batch = new DataBatch( new Data(Data.Type.TABULAR), 7);
//        Cluster cluster = null;
//
//        assertFalse(cluster.getNumOfProcessedBatch() == cluster.getNumOfProcessedBatch() + 1); // we have not sent the processed Batch yet
//
//        cpu.sendProcessedBatch(batch);
//        assertTrue(cluster.getNumOfProcessedBatch() == cluster.getNumOfProcessedBatch() + 1); // we have sent the processed Batch
//    }
//}