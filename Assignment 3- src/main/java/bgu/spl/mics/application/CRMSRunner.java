package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;

import java.io.*;
import java.sql.Time;
import java.util.ArrayList;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;


/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */

    public class CRMSRunner {

        public static CountDownLatch threadCount;

    public static void main(String[] args) {

        ArrayList<Thread> StudentThreads = new ArrayList<Thread>();
        ArrayList<Thread> GPUThreads = new ArrayList<Thread>();
        ArrayList<Thread> CPUThreads = new ArrayList<Thread>();
        ArrayList<Thread> conferencesThreads = new ArrayList<Thread>();

        LinkedList<Student> students = new LinkedList<>();
        LinkedList<StudentService> studentServices = new LinkedList<>();

        LinkedList<GPU> gpus = new LinkedList<>();
        LinkedList<GPUService> gpuServices = new LinkedList<>();

        LinkedList<CPU> cpus = new LinkedList<>();
        LinkedList<CPUService> cpuServices = new LinkedList<>();

        LinkedList<ConfrenceInformation> confrenceInformations = new LinkedList<>();
        LinkedList<ConferenceService> conferenceServices = new LinkedList<>();

        int tickTime;
        int duration;

        Gson gson = new Gson();

        try {
            Reader reader = new FileReader("example_input.json");
            JsonElement fileElement = JsonParser.parseReader(reader);
            JsonObject fileObject = fileElement.getAsJsonObject();
            JsonArray studentsArray = fileObject.get("Students").getAsJsonArray();
            for (JsonElement studentElement : studentsArray) {
                JsonObject studentObject = studentElement.getAsJsonObject();
                Student my_student;
                String name = studentObject.get("name").getAsString();
                String department = studentObject.get("department").getAsString();
                String status = studentObject.get("status").getAsString();
                Student.Degree degree;
                if (status == "MSc") {
                    degree = Student.Degree.MSc;
                } else {
                    degree = Student.Degree.PhD;
                }
                JsonArray modelsArray = studentObject.get("models").getAsJsonArray();
                ConcurrentLinkedQueue<Model> models = new ConcurrentLinkedQueue<>();
                for (JsonElement my_model : modelsArray) {
                    JsonObject modelObject = my_model.getAsJsonObject();
                    String modelName = modelObject.get("name").getAsString();
                    String modelType = modelObject.get("type").getAsString();
                    int modelSize = modelObject.get("size").getAsInt();
                    Data.Type my_type = stringToDataType(modelType);
                    Data my_data = new Data(my_type, modelSize);
                    models.add(new Model(modelName, my_data, null, Model.Status.PRETRAINED, Model.Result.NONE));
                }
                my_student = new Student(name, department, degree, models);
                StudentService myStudentService = new StudentService(name, my_student);
                for (Model mod : models) {
                    mod.setStudent(my_student);
                }
                students.add(my_student);
                studentServices.add(myStudentService);
            }

            JsonArray GPUsArray = fileObject.get("GPUS").getAsJsonArray();

            int a = 1;
            for (JsonElement type : GPUsArray) {
                String my_type = type.getAsString();
                GPU.Type converted = stringToGPUType(my_type);
                GPU my_gpu = new GPU(converted);
                GPUService myGPUService = new GPUService("GPUService " + a, my_gpu);
                gpus.add(my_gpu);
                gpuServices.add(myGPUService);
                a++;
            }

            int b = 1;
            JsonArray CPUsArray = fileObject.get("CPUS").getAsJsonArray();
            for (JsonElement type : CPUsArray) {
                int my_cores = type.getAsInt();
                CPU my_cpu = new CPU(my_cores);
                CPUService myCPUService = new CPUService("CPUService " + b, my_cpu);
                cpus.add(my_cpu);
                cpuServices.add(myCPUService);
                b++;
            }

            int c = 1;
            JsonArray conferencesArray = fileObject.get("Conferences").getAsJsonArray();
            for (JsonElement conf : conferencesArray) {
                JsonObject confObject = conf.getAsJsonObject();
                String confName = confObject.get("name").getAsString();
                int confDate = confObject.get("date").getAsInt();
                ConfrenceInformation my_confi = new ConfrenceInformation(confName, confDate);
                ConferenceService myConfService = new ConferenceService("ConferenceService " + c, my_confi);
                confrenceInformations.add(my_confi);
                conferenceServices.add(myConfService);
                c++; //MERAGESH CPP
            }

            tickTime = fileObject.get("TickTime").getAsInt();
            duration = fileObject.get("Duration").getAsInt();
            TimeService timeService = new TimeService(tickTime, duration);
            for(StudentService s: studentServices)
                StudentThreads.add(new Thread(s));

            for(GPUService g: gpuServices){
                GPUThreads.add(new Thread(g));
            }

            for(CPUService cp: cpuServices){
                CPUThreads.add(new Thread(cp));
            }

            for(ConferenceService conf: conferenceServices){
                conferencesThreads.add(new Thread(conf));
            }

            threadCount = new CountDownLatch(confrenceInformations.size() + gpuServices.size());

            Thread timeThread = new Thread(timeService);

            for(Thread t: GPUThreads)
                t.start();

            for(Thread t: CPUThreads)
                t.start();

            for(Thread t: conferencesThreads)
                t.start();

        try{
            threadCount.await();
        }catch (InterruptedException interrupt){}

            for (Thread t : StudentThreads) {
                t.start();
            }

            timeThread.start();
            timeThread.join();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        Cluster cluster = Cluster.getInstance();

        //Output File

//        File output = new File("/users/studs/bsc/2022/orkados/Documents/jasonWorksAsMyBitch/assignment2PostMistake/output_try.txt");
        File output = new File("C:\\Users\\orkados\\Desktop\\assignment2PostMistake\\output_try.txt");
        //File output = new File(args[1]);
        FileWriter writer = null;
        try{
            writer = new FileWriter(output);

            //writing the students into the output file

            writer.write("{\n\t\"students\": [");
            for(int i=0; i<students.size();i++){
                writer.write("\n\t\t{\n\t\t");
                writer.write(students.get(i).toString());
                writer.write("\n\t\t}");
                if( i< students.size() -1)
                    writer.write(",");
            }
            writer.write("\n\t],\n");

            //writing the conferences into the output file
            writer.write("\t\"conferences\": [\n");
            for(int i=0; i<confrenceInformations.size(); i++){
                writer.write("\t\t{\n\t\t");
                writer.write(confrenceInformations.get(i).toString());
                writer.write("\n\t\t}");
                if( i < confrenceInformations.size() -1)
                    writer.write(",");
                writer.write("\n");
            }
            writer.write("\t], \n");

            //writing the entire data
            writer.write("\t\"cpuTimeUsed\": ");
            writer.write(Integer.toString(cluster.getCpuTimeUsed()));
            writer.write(",\n");

            writer.write("\t\"gpuTimeUsed\": ");
            writer.write(Integer.toString(cluster.getGpuTimeUsed()));
            writer.write(",\n");

            writer.write("\t\"batchesProcessed\": ");
            writer.write(Integer.toString(cluster.getNumProBatches()));
            writer.write(",\n");

            writer.write("}");

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Data.Type stringToDataType(String type){
        if(type == "Images")
            return Data.Type.IMAGES;
        else if(type == "Text")
            return Data.Type.TEXT;
        else
            return Data.Type.TABULAR;
    }

    public static GPU.Type stringToGPUType(String type){
        if(type == "RTX3090")
            return GPU.Type.RTX3090;
        if(type == "RTX2080")
            return GPU.Type.RTX2080;
        else return GPU.Type.GTX1080;
    }

  }