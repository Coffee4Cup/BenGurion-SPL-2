package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

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

    private class Cameras{
        private LinkedList<CameraConfig> CamerasConfigurations;
        private String camera_datas_path;

        private Cameras(LinkedList<CameraConfig> cameraConfigurations, String camera_datas_path) {
            this.CamerasConfigurations = CamerasConfigurations;
            this.camera_datas_path = camera_datas_path;
        }
    }

    private class CameraConfig{
        private int id;
        private int frequency;
        private String camera_key;

        private CameraConfig(int id, int frequency, String camera_key) {
            this.id = id;
            this.frequency = frequency;
            this.camera_key = camera_key;
        }
    }

    private class LidarWorkers{
        private LinkedList<LiDarConfig> LidarConfigurations;
        private String lidars_data_path;

        private LidarWorkers(LinkedList<LiDarConfig> LidarConfigurations, String lidars_data_path) {
            this.LidarConfigurations = LidarConfigurations;
            this.lidars_data_path = lidars_data_path;
        }
    }

    private class LiDarConfig{
        private int id;
        private int frequency;

        private LiDarConfig(int id, int frequency) {
            this.id = id;
            this.frequency = frequency;
        }
    }
    private class Config {
        private Cameras Cameras;
        private LidarWorkers LidarWorkers;
        private String poseJsonFile;
        private int TickTime;
        private int Duration;

        private Config(Cameras Cameras, LidarWorkers LidarWorkers, String poseJsonFile, int TickTime, int Duration) {
            this.Cameras = Cameras;
            this.LidarWorkers = LidarWorkers;
            this.poseJsonFile = poseJsonFile;
            this.TickTime = TickTime;
            this.Duration = Duration;
        }
    }
    public static void main(String[] args) {
        Gson gson = new Gson();
        try {
            StatisticalFolder statisticalFolder = new StatisticalFolder();
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
            TimeService timeService = new TimeService(config.TickTime, config.Duration);
            //Adding new Camera for each configuration using new class CameraConfig:
            //fields are id, frequency and String camera_key which represents its KEY in Map<String, List<SDO> we created
            for(CameraConfig cfg : config.Cameras.CamerasConfigurations){
                myCameras.add(new Camera(cfg.id, cfg.frequency,  STATUS.UP, cameraData.get(cfg.camera_key), statisticalFolder));
            }
            System.out.println(myCameras.getFirst());
            LinkedList<LiDarWorkerTracker> myLidars = new LinkedList<>();
            for(LiDarConfig cfg: config.LidarWorkers.LidarConfigurations){
                myLidars.add(new LiDarWorkerTracker(cfg.id, cfg.frequency, STATUS.UP, config.LidarWorkers.lidars_data_path, statisticalFolder));
            }
            System.out.println(myLidars.getFirst());
            configReader = new FileReader(config.poseJsonFile);
            Type poseListType = new TypeToken<LinkedList<Pose>>() {}.getType();
            GPSIMU gpsimu = new GPSIMU(gson.fromJson(configReader, poseListType));
            System.out.println(gpsimu +" \ntick:" + config.TickTime + " duration:" + config.Duration);
            MicroService m1, m2, m3, m4, m5;
            //TODO implement threads with executor service. Make sure TimeService runs last!
            m1 = new CameraService(myCameras.getFirst());
            m5 = new TimeService(config.TickTime, config.Duration);
            m3 = new LiDarService(myLidars.getFirst());
            m4 = new FusionSlamService(FusionSlam.getInstance(statisticalFolder));
            m2 = new PoseService(gpsimu);
            Thread t1 = new Thread(m1);
            Thread t2 = new Thread(m2);
            Thread t3 = new Thread(m3);
            Thread t4 = new Thread(m4);
            Thread t5 = new Thread(m5);
            t1.start();
            t2.start();
            t3.start();
            t4.start();
            t5.start();
            //output.json creation:
            //TODO implement output.json creation
            try{
                t1.join();
                t2.join();
                t3.join();
                t4.join();
                t5.join();

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(statisticalFolder);


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
