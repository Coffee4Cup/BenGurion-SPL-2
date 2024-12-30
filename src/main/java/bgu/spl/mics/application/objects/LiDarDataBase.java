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
    private HashMap<String, StampedCloudPoints> cloudPoints;
    private static volatile LiDarDataBase instance;


    private LiDarDataBase(LinkedList<StampedCloudPoints> cloudPoints) {
        this.cloudPoints = new HashMap<>();
        for(StampedCloudPoints scp: cloudPoints){
            this.cloudPoints.put(scp.getId(), scp);
        }
    }
    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */
    public synchronized static LiDarDataBase getInstance(String filePath){
        if (instance == null) {
            try {
                Gson gson = new Gson();
                Reader reader = new FileReader(filePath);
                Type SCPType = new TypeToken<LinkedList<StampedCloudPoints>>() {}.getType();
                LinkedList<StampedCloudPoints> scpList = gson.fromJson(reader, SCPType);
                instance = new LiDarDataBase(scpList);
                System.out.println(instance);
            } catch (FileNotFoundException e) {
                System.out.println("File not found at: " + filePath);
                return null;
            }

        }
        return instance;
    }

    public StampedCloudPoints getStampedCloudPoints(String id){
        return cloudPoints.get(id);
    }

    public String toString(){
        return "CloudPoints: "+cloudPoints;
    }
}
