package bgu.spl.mics.application.objects;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name; //the name of the conference
    private int publicationDate; // the time of the publication of the conference.
//    private int initDate; //the time we will start to send good models to this conference
    private ConcurrentLinkedQueue<Model> goodModels;

    public ConfrenceInformation(String _name , int _publicationDate/*, int _initDate*/){
        name = _name;
        publicationDate = _publicationDate;
        goodModels = new ConcurrentLinkedQueue<>();
//        initDate = _initDate;
    }

    public int getPublicationDate(){ return publicationDate; }

    public ConcurrentLinkedQueue<Model> getGoodModels(){ return goodModels; }

    public String toString(){
        String output = "";

        output += "\t\"name\": \"" + name + "\",\n";
        output += "\t\t\t\"date\": \"" + publicationDate + "\",\n";
        output += "\t\t\t\"publications\": [\n\t\t\t\t";

        if( goodModels.size() > 0){
            Iterator<Model> itr = goodModels.iterator();

            while(itr.hasNext()){
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
