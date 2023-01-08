package bgu.spl.mics.application.output;

import bgu.spl.mics.application.objects.Data;
import bgu.spl.mics.application.objects.Model;

public class ModelOutput {
    private String name;
    private Data data;
    private Model.Status status;
    private Model.Result result;

    public ModelOutput(Model _model){
        result = _model.getResult();
        status = _model.getStatus();
        name = _model.getName();
        data = _model.getData();
    }
}