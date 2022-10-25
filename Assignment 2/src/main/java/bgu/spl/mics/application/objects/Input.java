package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

public class Input {
    private LinkedList<Student> Students;
    private LinkedList<GPU> GPUs;
    private LinkedList<CPU>CPUs;
    private LinkedList<ConfrenceInformation> Conferences;
    private int TickTime;
    private int Duration;

    public LinkedList<Student> getStudents(){
        return Students;
    }

    public LinkedList<GPU> getGPUs(){
        return GPUs;
    }

    public LinkedList<CPU> getCPUs(){
        return CPUs;
    }

    public LinkedList<ConfrenceInformation> getConferences(){
        return Conferences;
    }

    public int getTickTime(){
        return TickTime;
    }

    public int getDuration(){
        return Duration;
    }
}
