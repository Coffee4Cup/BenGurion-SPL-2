package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.CameraService;
import bgu.spl.mics.application.services.TimeService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.Map;

/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 * <p>
 * This class initializes the system and starts the simulation by setting up
 * services, objects, and configurations.
 * </p>
 */
public class GurionRockRunner {

    /**
     * The main method of the simulation.
     * This method sets up the necessary components, parses configuration files,
     * initializes services, and starts the simulation.
     *
     * @param args Command-line arguments. The first argument is expected to be the path to the configuration file.
     */

    public class Cameras{
        public LinkedList<CameraConfig> CamerasConfigurations;
        public String camera_datas_path;

        public Cameras(LinkedList<CameraConfig> cameraConfigurations, String camera_datas_path) {
            this.CamerasConfigurations = CamerasConfigurations;
            this.camera_datas_path = camera_datas_path;
        }
    }

    public class CameraConfig{
        public int id;
        public int frequency;
        public String camera_key;

        public CameraConfig(int id, int frequency, String camera_key) {
            this.id = id;
            this.frequency = frequency;
            this.camera_key = camera_key;
        }
    }

    public class LidarWorkers{
        public LinkedList<LiDarConfig> LidarConfigurations;
        public String lidars_data_path;

        public LidarWorkers(LinkedList<LiDarConfig> LidarConfigurations, String lidars_data_path) {
            this.LidarConfigurations = LidarConfigurations;
            this.lidars_data_path = lidars_data_path;
        }
    }

    public class LiDarConfig{
        public int id;
        public int frequency;

        public LiDarConfig(int id, int frequency) {
            this.id = id;
            this.frequency = frequency;
        }
    }
    public class Config {
        public Cameras Cameras;
        public LidarWorkers LidarWorkers;
        public String poseJsonFile;
        public int ticktime;
        public int duration;

        public Config(Cameras Cameras, LidarWorkers LidarWorkers, String poseJsonFile, int ticktime, int duration) {
            this.Cameras = Cameras;
            this.LidarWorkers = LidarWorkers;
            this.poseJsonFile = poseJsonFile;
            this.ticktime = ticktime;
            this.duration = duration;
        }
    }
    public static void main(String[] args) {
        Gson gson = new Gson();
        try {
            //Reader from java IO to read config file
            Reader configReader = new FileReader("./configuration_file.json");
            //New class Config with fields corresponding to configuration_file.json
            Config config = gson.fromJson(configReader, Config.class);
            //Reading from new file addressed in configuration_file.json
            configReader = new FileReader(config.Cameras.camera_datas_path);
            //creating a new TypeToken to parse camera_data.json into Map<String, List<StampedDetectedObject>>
            Type cameraDataType = new TypeToken<Map<String, LinkedList<StampedDetectedObjects>>>() {}.getType();
            Map<String, LinkedList<StampedDetectedObjects>> cameraData = gson.fromJson(configReader, cameraDataType);
            //Creating a main list<Camera> to use
            LinkedList<Camera> myCameras = new LinkedList<>();
            //Creating a main TickService with parameters directly from configuration_file.json
            TimeService timeService = new TimeService(config.ticktime, config.duration);
            //Adding new Camera for each configuration using new class CameraConfig:
            //fields are id, frequency and String camera_key which represents its KEY in Map<String, List<SDO> we created
            for(CameraConfig cfg : config.Cameras.CamerasConfigurations){
                myCameras.add(new Camera(cfg.id, cfg.frequency,  STATUS.UP, cameraData.get(cfg.camera_key)));
            }
            System.out.println(myCameras.getFirst());
            LinkedList<LiDarWorkerTracker> myLidars = new LinkedList<>();
            for(LiDarConfig cfg: config.LidarWorkers.LidarConfigurations){
                myLidars.add(new LiDarWorkerTracker(cfg.id, cfg.frequency, STATUS.UP, config.LidarWorkers.lidars_data_path));
            }
            System.out.println(myLidars.getFirst());
            configReader = new FileReader(config.poseJsonFile);
            Type poseListType = new TypeToken<LinkedList<Pose>>() {}.getType();
            GPSIMU gpsimu = new GPSIMU(gson.fromJson(configReader, poseListType));
            System.out.println(gpsimu);

            MessageBusImpl msgbs;
            MicroService m1, m2;
            m1 = new CameraService(myCameras.getFirst());
            m2 = new TimeService(1000, 10);
            Thread t1 = new Thread(m1);
            Thread t2 = new Thread(m2);
            t1.start();
            t2.start();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
