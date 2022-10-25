package bgu.spl.mics.application.objects;


/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {




    public enum Status {
        PRETRAINED, TRAINING, TRAINED, TESTED
    }

    public enum Result{
        NONE, GOOD, BAD
    }

    private String type;
    private int dataSize;

    private String name;
    private Data data;
    private Student student;
    private Status status;
    private Result result;

    public Model(String _name, Data _data, Student _student, Status _status, Result _result){
        name = _name; // name of the model.
        data = _data; // the data the model should train on
        student = _student; //The student which created the mode
        status = _status; // can be “PreTrained”, “Training”, “Trained”, “Tested”.
        result = _result; // can be “None” (for a model not in status tested), “Good” or“Bad”.
    }
    public Data getData(){return this.data; }

    public String getName(){ return name; }

    public String getStudentName(){
        return student.getName();
    }

    public Student getStudent() {
        return student;
    }

    public Status getStatus() {
        return status;
    }

    public Result getResult(){ return result; }

    public void setResult(Result result) {
        this.result = result;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getType(){ return type; }

    public int getDataSize(){ return dataSize; }

    public void setData(Data myData) {
        data = myData;
    }

    public void setStudent(Student s) {
        student = s;
    }

    public String toString(){
        String output = "";

        output += "\"name\": \"" + name + "\",\n";
        output += "\t\t\t\t\t\"data\": {\n" + data.toString() + "\t\t\t\t\t},\n";
        output += "\t\t\t\t\t\"status\": \"" + status + "\",\n";
        output += "\t\t\t\t\t\"results\": \"" + result + "\"";

        return output;
    }

}
