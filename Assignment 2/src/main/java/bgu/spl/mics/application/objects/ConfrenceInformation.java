package bgu.spl.mics.application.objects;

import java.util.LinkedList;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private final String name;
    private final Long date;
    private int id;
    private LinkedList<Model> aggregatedModels;

    public ConfrenceInformation(String _name, Long _date){
        this.name = _name;
        this.date = _date;
        aggregatedModels = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public Long getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public LinkedList<Model> getAggregatedModels() {
        return aggregatedModels;
    }
    public void addToAggregatedModels(Model model){
        aggregatedModels.add(model);
    }
}
