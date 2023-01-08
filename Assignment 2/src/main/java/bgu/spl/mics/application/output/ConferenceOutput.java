package bgu.spl.mics.application.output;

import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;

import java.util.LinkedList;

public class ConferenceOutput {
    private final String name;
    private final Long date;
    private LinkedList<ModelOutput> publications = new LinkedList<>();

    public ConferenceOutput(ConfrenceInformation confInfo) {
        this.name = confInfo.getName();
        this.date = confInfo.getDate();
        for(Model model : confInfo.getAggregatedModels()){
            publications.add(new ModelOutput(model));
        }
    }
}
