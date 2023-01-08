package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;
import java.util.LinkedList;

public class PublishConferenceBroadcast implements Broadcast{
    LinkedList<Model> modelsToPublish;


    public PublishConferenceBroadcast(LinkedList<Model> _models) {
        modelsToPublish = _models ;
    }

    public LinkedList<Model> getModelsToPublish() {
        return modelsToPublish;
    }

    public LinkedList<String> modelListByName(){
        LinkedList<String> modelsByName = new LinkedList<>();
        for(Model model : getModelsToPublish()){
            modelsByName.add(model.getName());
        }
        return modelsByName;

    }


}
