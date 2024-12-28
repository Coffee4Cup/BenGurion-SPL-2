package bgu.spl.mics.application.objects;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.LinkedList;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    private LinkedList<StampedCloudPoints> cloudPoints;
    private static LiDarDataBase instance;


    private LiDarDataBase(LinkedList<StampedCloudPoints> cloudPoints) {
        this.cloudPoints = cloudPoints;
    }
    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */
    public synchronized static LiDarDataBase getInstance(String filePath) throws FileNotFoundException {
        if (instance == null) {
            try {
                Gson gson = new Gson();
                Reader reader = new FileReader(filePath);
                instance = gson.fromJson(reader, LiDarDataBase.class);
            } catch (FileNotFoundException e) {
                System.out.println("File not found at: " + filePath);
                return null;
            }

        }
        return instance;
    }
}
