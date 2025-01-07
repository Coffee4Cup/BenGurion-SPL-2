package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    public static final Type SCP_TYPE = new TypeToken<LinkedList<StampedCloudPoints>>() {
    }.getType();
    private static final Gson gson = new Gson();
    private static Type scpType;
    private static LinkedList<StampedCloudPoints> scpList;
    private HashMap<String, StampedCloudPoints> cloudPoints;
    private static volatile LiDarDataBase instance;
    private int finalTick;
    private boolean isDone;

    public synchronized static void initialize(String dataPath){
        try {
            Reader reader = new FileReader(dataPath);
            scpType = SCP_TYPE;
            scpList = gson.fromJson(reader, scpType);
            instance = new LiDarDataBase(scpList);
            //     System.out.println(instance);
        } catch (FileNotFoundException e) {
            System.out.println("File not found at: " + dataPath);
        }
    }

    private LiDarDataBase(LinkedList<StampedCloudPoints> cloudPoints) {
        this.cloudPoints = new HashMap<>();
        for(StampedCloudPoints scp: cloudPoints){
            if(scp.getTime() > finalTick){
                finalTick = scp.getTime();
            }
            this.cloudPoints.put(scp.getTime() + scp.getId(), scp);
        }
    }

    public boolean isDone(){
        return isDone;
    }
    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @return The singleton instance of LiDarDataBase.
     */
    public static LiDarDataBase getInstance(){
        return instance;
    }

    public StampedCloudPoints getStampedCloudPoints(String id){
        StampedCloudPoints scp = cloudPoints.get(id);
        if(scp != null && scp.getTime() == finalTick){
            isDone = true;
        }
        return scp;
    }

    public String toString(){
        return "CloudPoints: "+cloudPoints;
    }
}
