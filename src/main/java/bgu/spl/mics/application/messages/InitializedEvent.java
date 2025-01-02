package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;

public class InitializedEvent implements Event<Boolean> {
    private MicroService initializedMicroservice;
    private Future<Boolean> ready;

    public InitializedEvent(MicroService initializedMicroservice){
        this.initializedMicroservice = initializedMicroservice;
        ready = new Future<>();
    }

    public MicroService getService(){
        return initializedMicroservice;
    }

    @Override
    public Future<Boolean> getFuture() {
        return ready;
    }
}
