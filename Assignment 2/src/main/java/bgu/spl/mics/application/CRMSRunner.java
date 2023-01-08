package bgu.spl.mics.application;

import bgu.spl.mics.application.output.Output;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static CountDownLatch initializeCountLatch;
    public static InputReader input;

    public static void main(String[] args) {

        File path = new File(args[0]);
	//String path = args[0];
        input = new InputReader();
        try {
            input.parse(args[0]);
            //input.parse(args[0]); //for debugging
            initializeCountLatch = new CountDownLatch(countThreads(input));
            runProgram(input);
            output(args[1]);
        } catch (FileNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void runProgram(InputReader inputReader) throws InterruptedException {
        Thread clock = new Thread(new TimeService(inputReader.tickTime, inputReader.duration));
        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < inputReader.conferenceList.size(); i++) {
            String count = String.valueOf(i);
            threadList.add(new Thread(new ConferenceService("ConferenceService" + count, inputReader.conferenceList.get(i))));
        }
        for (int i = 0; i < inputReader.cpuList.size(); i++) {
            String count = String.valueOf(i);
            threadList.add(new Thread(new CPUService("CPUService" + count, inputReader.cpuList.get(i))));
        }
        for (int i = 0; i < inputReader.gpuList.size(); i++) {
            String count = String.valueOf(i);
            threadList.add(new Thread(new GPUService("GPUService" + count, inputReader.gpuList.get(i))));
        }
        for (int i = 0; i < inputReader.studentList.size(); i++) {
            String count = String.valueOf(i);
            threadList.add(new Thread(new StudentService("StudentService" + count, inputReader.studentList.get(i), inputReader.gpuList)));
        }
        for (Thread thread : threadList) {
            thread.start();
        }

        initializeCountLatch.await();
        clock.start();
        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException ignored) {}
        }
        clock.interrupt();
    }

    public static int countThreads(InputReader inputReader) {
        return (inputReader.cpuList.size() + inputReader.gpuList.size() + inputReader.studentList.size() + inputReader.conferenceList.size());
    }

    public static void output(String path) {
        Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
        Output output = new Output(input.studentList, input.conferenceList, totalCPUTime(input), totalGPUTime(input), totalBatchesProcessed(input));
        try {
            FileWriter fw = new FileWriter("output.json");
            gsonBuilder.toJson(output, fw);
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
	    System.exit(-1);
        }
    }

    public static int totalBatchesProcessed(InputReader inputReader) {
        int counter = 0;
        for (CPU cpu : inputReader.cpuList) {
            counter += cpu.getBatchesProcessed();
        }
        return counter;
    }

    public static int totalCPUTime(InputReader inputReader) {
        int counter = 0;
        for (CPU cpu : inputReader.cpuList) {
            counter += cpu.getCpuTimeUsed();
        }
        return counter;
    }

    public static int totalGPUTime(InputReader inputReader) {
        int counter = 0;
        for (GPU gpu : inputReader.gpuList) {
            counter += gpu.getGpuWorkTime();
        }
        return counter;
    }
}
