package bgu.spl.mics.application.output;

import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Student;
import java.util.LinkedList;

public class Output {
    LinkedList<StudentOutput> students;
    LinkedList<ConferenceOutput> conferences;
    int cpuTimeUsed;
    int gpuTimeUsed;
    int batchesProcessed;

    public Output(LinkedList<Student> _students, LinkedList<ConfrenceInformation> _conferences, int _cpuTimeUsed, int _gpuTimeUsed, int _batchesProcessed){
        this.students = new LinkedList<>();
        for(Student incomingStudent : _students){
            this.students.add(new StudentOutput(incomingStudent));
        }
        this. conferences = new LinkedList<>();
        for(ConfrenceInformation incomingConf : _conferences){
            this.conferences.add(new ConferenceOutput(incomingConf));
        }
        this.cpuTimeUsed = _cpuTimeUsed;
        this.gpuTimeUsed = _gpuTimeUsed;
        this.batchesProcessed = _batchesProcessed;
    }
}
