package bgu.spl.mics.application.objects;

//import com.sun.org.apache.xpath.internal.operations.Mod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree {
        MSc, PhD
    }

    private String name; // changed int to string- not sure if correct
    private String department;
    private Degree status;
    private int publications;
    private int papersRead;
    private ConcurrentLinkedQueue<Model> models; //all models here should be with STATUS == PRETRAINED
    private ConcurrentLinkedQueue<Model> trainedModels; //all models here should be with STATUS == TRAINED
    private ConcurrentLinkedQueue<Model> publishedModels; //all models here should be with STATUS == TESTED


    public Student(String _name, String _department , Degree _status, ConcurrentLinkedQueue<Model> _models){//, List<Model> _modelList){
        name = _name;
        department = _department;
        status= _status;
        publications = 0; //maybe more
        papersRead = 0;
        models = _models;
        trainedModels = new ConcurrentLinkedQueue<>();
        publishedModels = new ConcurrentLinkedQueue<>();
    }

    public String getName() {
        return name;
    }

    public ConcurrentLinkedQueue<Model> getTrainedModels() {
        return trainedModels;
    }

    public ConcurrentLinkedQueue<Model> getPublishedModels() {
        return publishedModels;
    }

    public ConcurrentLinkedQueue<Model> getModels() {
        return models;
    }

    public Degree getStatus() {
        return status;
    }

    public String getDepartment(){ return department;}

    public void incrementPublication(){ publications ++;}

    public void incrementPapersRead(){ papersRead ++;}

    public String toString(){
        String output = "";

        output += "\t\"name\": \"" + name + "\",\n";
        output += "\t\t\t\"department\": \"" + department + "\",\n";
        output += "\t\t\t\"status\": \"" +status + "\",\n";
        output += "\t\t\t\"publications\": " +Integer.toString(publications) + ",\n";
        output += "\t\t\t\"papersRead\": " + Integer.toString(papersRead) + ",\n";
        output += "\t\t\t\"trainedModels\": [\n\t\t\t\t";

        if(trainedModels != null && trainedModels.size() > 0){
            Iterator<Model> itr = trainedModels.iterator();

            while (itr.hasNext()){
                Model m = itr.next();
                output += "{\n\t\t\t";
                output += "\t\t" + m.toString() + "\n\t\t\t\t}";
                output += ",\n\t\t\t\t";
            }
            output = output.substring(0, output.length() -6);
            output += "\n";
        }
        output += "\t\t\t]";

        return output;
    }
}
