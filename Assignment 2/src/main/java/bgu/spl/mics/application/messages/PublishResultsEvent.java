package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;


public class PublishResultsEvent implements Event<Void> {
    Student student;
    Model model;

    public PublishResultsEvent(Model model, Student student){
        this.model = model;
        this.student=student;
    }

    public Model getModel() {
        return model;
    }
}
