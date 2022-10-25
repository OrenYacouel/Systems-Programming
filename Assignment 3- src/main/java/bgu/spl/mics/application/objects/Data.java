package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    public enum Type {
        IMAGES, TEXT, TABULAR
    }

    private Type type; // type of data, can be Images, Text, Tabular
    private int numProcessedData; // Number of samples which the GPU has processed for training
    private int dataSize; // number of samples in the data

    public Data(Type _type, int _dataSize){
        this.type = _type;
        dataSize = _dataSize;
    }
    public int getDataSize(){return this.dataSize;}
    public Type getType() {
        return type;
    }

    public void setType(Type _type){ type = _type;}

    public void setDataSize(int _size){
        dataSize = _size;
    }

    public String toString(){
        String output = "";

        output += "\"type\": \"" + type + "\",\n";
        output += "\t\t\t\t\t\t\"size\": \"" + dataSize + "\",\n";

        return output;
    }
}
