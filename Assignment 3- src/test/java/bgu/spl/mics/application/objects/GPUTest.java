//package bgu.spl.mics.application.objects;
//
//
////import jdk.internal.org.jline.reader.UserInterruptException;
//import junit.framework.TestCase;
//import org.junit.Test;
//public class GPUTest extends TestCase {
//    private static GPU gpu;
//    private static Model model;
//    private static Cluster cluster;
//    private static Data image;
//    private static DataBatch proBatch;
//    private static DataBatch unProBatch;
//
//
//    public void setUp() throws Exception {
//       model = new Model("Train", image, new Student("Oren", "Computer Science", Student.Degree.MSc), Model.Status.PRETRAINED, Model.Result.NONE);
//       gpu = new GPU(GPU.Type.RTX3090);
//       cluster = Cluster.getInstance();
//       image = new Data(Data.Type.IMAGES);
//       unProBatch = new DataBatch(image,0);
//       proBatch = new DataBatch(image, 0);
//       proBatch.process();
//
//    }
//
//    public void tearDown() throws Exception {
//    }
//
//
//    @Test
//    public void testBatchMaker() {
//        assertTrue(model.getData().getDataSize() > 0);
//        int batched = gpu.numOfUnProBatchesInDisk();
//        assertTrue(gpu.numOfUnProBatchesInDisk() == batched);
//        gpu.batchMaker(image);
//        assertFalse(gpu.numOfUnProBatchesInDisk() == batched);
//    }
//
//    @Test
//    public void testSendBatch() {
////   try to send a batch of data without batchMake first - don't do anything
//        gpu.sendUnProBatch();
//        assertTrue("sendBatch put batches in disk",gpu.numOfUnProBatchesInDisk() == 0);
//
//        gpu.batchMaker(image);
//        int numOfUnProBatchesInDisk = gpu.numOfUnProBatchesInDisk();
//        gpu.sendUnProBatch();
//        assertFalse("didn't remove batch from disk",gpu.numOfUnProBatchesInDisk() == numOfUnProBatchesInDisk);
//    }
//    @Test
//    public void testReceiveBatch() {
////      try to receive batch from cluster without cluster capable of sending
//
////        Assert.assertThrows(UserInterruptException.class, ()->gpu.receiveBatch(proBatch));
//    }
//
//    @Test
//    public void testTrainBatch() {
//
////      try to train a batch when there is no processed ones - do nothing
//        gpu.finishBatchTraining(gpu.getVRAM().remove(0));
//        assertTrue("VRAM isn't empty as expected",gpu.numOfUntrainedBatches() == 0);
//
////      train a batch and then check correct
//        gpu.receiveBatch(proBatch);//instead of cluster sending proBatch
//        int numOfProBatches = gpu.numOfUntrainedBatches();
//        gpu.finishBatchTraining(gpu.getVRAM().remove(0));
//        assertTrue("VRAM didn't remove proBatch or removed more than one",gpu.numOfUntrainedBatches() == numOfProBatches - 1);
////
//
//    }
//
//    @Test
//    public void testFinishedBatches() {
////        really depends on implementation of fields and communication with cluster- haven't reached this part
//    }
//
//    @Test
//    public void testFinishedModel() {
////        really depends on implementation of fields and communication with cluster- haven't reached this part
//
//    }
//}