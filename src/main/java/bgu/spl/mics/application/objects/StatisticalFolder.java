package bgu.spl.mics.application.objects;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {
    private Integer systemRunTime;
    private Integer numDetectedObjects;
    private Integer numTrackedObjects;
    private Integer numLandmarks;
    public final Object lockSRT = new Object();
    public final Object lockNDO = new Object();
    public final Object lockNTO = new Object();
    public final Object lockNL = new Object();

    public StatisticalFolder(){
        systemRunTime = 0;
        numDetectedObjects = 0;
        numTrackedObjects = 0;
        numLandmarks = 0;
    }

    public void setSystemRunTime(Integer systemRunTime){
        synchronized (lockSRT){
            this.systemRunTime = systemRunTime;
        }
    }

    public void addDetectedObjects(int amount){
        synchronized (lockNDO){
            numDetectedObjects+=amount;
        }
    }

    public void addTrackedObjects(int amount){
        synchronized (lockNTO){
            numTrackedObjects+=amount;
        }
    }

    public void addLandmarks(int amount){
        synchronized (lockNL){
            numLandmarks+=amount;
        }
    }
}
