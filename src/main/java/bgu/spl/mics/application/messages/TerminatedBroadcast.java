package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;

public class TerminatedBroadcast implements Broadcast {
    MicroService terminatedMicroservice;
    public TerminatedBroadcast(){
        this.terminatedMicroservice = null;
    }

    public MicroService getService(){
        return terminatedMicroservice;
    }

    /**
     * @apiNote added for other Microservices to check when getting a Broadcast if the microservice that is terminated is timeService
     * @param terminatedMicroservice the microService that is terminated
     */
    public TerminatedBroadcast(MicroService terminatedMicroservice){
        this.terminatedMicroservice = terminatedMicroservice;
    }
}
