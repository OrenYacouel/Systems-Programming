package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

public class TestModelEvent implements Event<Model> {
    public Model model;
    public Student student;

    public TestModelEvent(Model model, Student _student) {
        this.model = model;
        this.student = _student;
    }

    public Model getModel() {
        return model;
    }

    public Student getStudent() {
        return student;
    }
}
