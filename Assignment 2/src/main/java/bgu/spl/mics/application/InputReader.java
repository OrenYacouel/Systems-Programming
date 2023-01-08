package bgu.spl.mics.application;
import bgu.spl.mics.application.objects.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import bgu.spl.mics.application.objects.Data;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;


public class InputReader {
    public LinkedList<Student> studentList = new LinkedList<>();
    public LinkedList<GPU> gpuList = new LinkedList<>();
    public LinkedList<CPU> cpuList = new LinkedList<>();
    public LinkedList<ConfrenceInformation> conferenceList= new LinkedList<>();
    public long tickTime;
    public long duration;

    public InputReader(){}

    public void parse(String path) throws FileNotFoundException {
        try {
            InputReader inputReader = new InputReader();
            JsonReader jReader = new JsonReader(new FileReader(path));
            JsonObject jObject = JsonParser.parseReader(jReader).getAsJsonObject();
            JsonArray JStudents = jObject.get("Students").getAsJsonArray();
            JsonArray JGPUS = jObject.get("GPUS").getAsJsonArray();
            JsonArray JCPUS = jObject.get("CPUS").getAsJsonArray();
            JsonArray JConferences = jObject.get("Conferences").getAsJsonArray();

            //Students
            for (JsonElement student : JStudents) {
                JsonObject studenJObject = student.getAsJsonObject();
                String name = studenJObject.get("name").getAsString();
                String department = studenJObject.get("department").getAsString();
                String status = studenJObject.get("status").getAsString();
                JsonArray models = studenJObject.get("models").getAsJsonArray();
                LinkedList<Model> listOfModels = new LinkedList<>();
                for (JsonElement model : models) {
                    JsonObject modelJObject = model.getAsJsonObject();
                    String mName = modelJObject.get("name").getAsString();
                    String mType = modelJObject.get("type").getAsString();
                    int mSize = modelJObject.get("size").getAsInt();
                    Data.Type type = stringToDataType(mType);
                    listOfModels.addLast(new Model(mName, type, mSize));
                }
                Student.Degree dStatus = stringToDegreeType(status);
                studentList.addLast(new Student(name, department, dStatus, listOfModels));
            }
            //GPU
            for (int i = 0; i < JGPUS.size(); i++) {
                String jType = JGPUS.get(i).getAsString();
                GPU.Type gType = stringToGPUType(jType);
                gpuList.addLast(new GPU(gType, i));
            }
            //CPU
            for (int i = 0; i < JCPUS.size(); i++) {
                int cores = JCPUS.get(i).getAsInt();
                cpuList.addLast(new CPU(cores, i));
            }
            //Conferences
            for (JsonElement conference : JConferences) {
                JsonObject jConf = conference.getAsJsonObject();
                String name = jConf.get("name").getAsString();
                Long date = jConf.get("date").getAsLong();
                conferenceList.addLast(new ConfrenceInformation(name, date));
                }

            //Times
            this.duration = jObject.get("Duration").getAsLong();
            this.tickTime = jObject.get("TickTime").getAsLong();
        } catch (FileNotFoundException e) {
            throw e;
        }
    }

    private static Data.Type stringToDataType(String type){
        Data.Type dataType;
        if(type.equals("images") | type.equals("Images") )
            dataType = Data.Type.Images;
        else if (type.equals("Text") | type.equals("text"))
            dataType = Data.Type.Text;
        else{
            dataType = Data.Type.Tabular;
        }
        return dataType;
    }

    private static Student.Degree stringToDegreeType (String type){
        Student.Degree degree;
        if(type.equals( "MSc"))
            degree = Student.Degree.MSc;
        else{
            degree = Student.Degree.PhD;
        }
        return degree;
    }

    private static GPU.Type stringToGPUType (String type){
        GPU.Type dataType;
        if(type.equals("RTX3090"))
            dataType = GPU.Type.RTX3090;
        else if (type.equals("RTX2080"))
            dataType = GPU.Type.RTX2080;
        else{
            dataType = GPU.Type.GTX1080;
        }
        return dataType;
    }
}
