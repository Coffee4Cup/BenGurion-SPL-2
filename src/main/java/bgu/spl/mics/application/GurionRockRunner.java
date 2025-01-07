package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.FileSystems;
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

    private class LiDarWorkers{
        private LinkedList<LiDarConfig> LidarConfigurations;
        private String lidars_data_path;

        private LiDarWorkers(LinkedList<LiDarConfig> LidarConfigurations, String lidars_data_path) {
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
        private LiDarWorkers LiDarWorkers;
        private String poseJsonFile;
        private int TickTime;
        private int Duration;

        private Config(Cameras Cameras, LiDarWorkers LiDarWorkers, String poseJsonFile, int TickTime, int Duration) {
            this.Cameras = Cameras;
            this.LiDarWorkers = LiDarWorkers;
            this.poseJsonFile = poseJsonFile;
            this.TickTime = TickTime;
            this.Duration = Duration;
        }
    }
    public static void main(String[] args) {
        String confDir = args[0].substring(0, args[0].lastIndexOf(FileSystems.getDefault().getSeparator()));
        Gson gson = new Gson();
        try {
            StatisticalFolder statisticalFolder = new StatisticalFolder();
            //Reader from java IO to read config file
            Reader configReader = new FileReader(args[0]);
            //New class Config with fields corresponding to configuration_file.json
            Config config = gson.fromJson(configReader, Config.class);
            //Reading from new file addressed in configuration_file.json
            configReader = new FileReader(confDir +  config.Cameras.camera_datas_path.substring(1));
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
                myCameras.add(new Camera(cfg.id, cfg.frequency, cfg.camera_key,  cameraData.get(cfg.camera_key), statisticalFolder));
            //    System.out.println(myCameras.getLast());
            }
            LinkedList<LiDarWorkerTracker> myLidars = new LinkedList<>();
            int index = 1;
            for(LiDarConfig cfg: config.LiDarWorkers.LidarConfigurations){
                myLidars.add(new LiDarWorkerTracker(cfg.id, cfg.frequency, "lidar"+index++, confDir + config.LiDarWorkers.lidars_data_path.substring(1), statisticalFolder));
            }
        //    System.out.println(myLidars.getFirst());
            configReader = new FileReader(confDir + config.poseJsonFile.substring(1));
            Type poseListType = new TypeToken<LinkedList<Pose>>() {}.getType();
            GPSIMU gpsimu = new GPSIMU(gson.fromJson(configReader, poseListType));
    //        System.out.println(gpsimu +" \ntick:" + config.TickTime + " duration:" + config.Duration);

            //TODO implement threads with executor service. Make sure TimeService runs last!
            LinkedList<MicroService> services = new LinkedList<>();
            for(Camera camera : myCameras){
                CameraService cameraService = new CameraService(camera);
                services.add(cameraService);
         //       FusionSlam.getInstance().addCamera(camera);
            }
            for(LiDarWorkerTracker lidar : myLidars){
                LiDarService liDarService = new LiDarService(lidar);
                services.add(liDarService);
        //        FusionSlam.getInstance().addLidar(lidar);
            }
            PoseService poseService = new PoseService(gpsimu);
            services.add(poseService);
            FusionSlamService fusionSlamService = new FusionSlamService(FusionSlam.getInstance());
            services.add(fusionSlamService);
            FusionSlam.getInstance().setStatisticalFolder(statisticalFolder);
      //      FusionSlam.getInstance().setTimeService(timeService);
    //        FusionSlam.getInstance().setGpsimu(gpsimu);
            Object sysLock = new Object();
            FusionSlam.getInstance().setSysLock(sysLock);
            FusionSlam.getInstance().setCameraCount(myCameras.size());
            FusionSlam.getInstance().setLidarCount(myLidars.size());
            LinkedList<Thread> threads = new LinkedList<>();
            for(MicroService service : services){
                Thread t = new Thread(service);
                threads.add(t);
                t.start();
            }
            Thread timeThread = new Thread(timeService);
            timeThread.start();
     /**       m1 = new CameraService(myCameras.getFirst());
            m5 = new TimeService(config.TickTime, config.Duration);
            m3 = new LiDarService(myLidars.getFirst());
            m4 = new FusionSlamService(FusionSlam.getInstance());
            FusionSlam.getInstance().setStatisticalFolder(statisticalFolder);
            FusionSlam.getInstance().setTimeService(timeService);
            FusionSlam.getInstance().setGpsimu(gpsimu);
            FusionSlam.getInstance().addCamera(myCameras.getFirst());
            FusionSlam.getInstance().addLidar(myLidars.getFirst());
            Object sysLock = new Object();
            FusionSlam.getInstance().setSysLock(sysLock);
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
            t5.start();*/
            //output.json creation:
            //TODO implement output.json creation
            try{
                synchronized(sysLock) {
                    sysLock.wait();
                }
                timeThread.interrupt();
                statisticalFolder.setSystemRunTime(timeService.getCurrentTick());

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            statisticalFolder.output(new GsonBuilder().setPrettyPrinting().create(), confDir);
         //   System.out.println(statisticalFolder);


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
