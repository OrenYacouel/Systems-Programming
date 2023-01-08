package bgu.spl.mics.application.output;

import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.LinkedList;

public class StudentOutput {
    private String name;
    private String department;
    private Student.Degree status;
    private int publications;
    private int papersRead;
    private LinkedList<Model> trainedModels = new LinkedList<>();

    public StudentOutput(Student _student){
        name = _student.getName();
        department = _student.getDepartment();
        status=_student.getStatus();
        publications = _student.getPublications();
        papersRead = _student.getPapersRead();
        for(Model model : _student.getModels()){
            if(model.getStatus() == Model.Status.Trained | model.getStatus() == Model.Status.Tested )
                trainedModels.add(model);
        }
    }
}
