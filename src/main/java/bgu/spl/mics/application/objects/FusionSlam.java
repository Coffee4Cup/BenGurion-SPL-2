package bgu.spl.mics.application.objects;

import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.InitializedEvent;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.services.CameraService;
import bgu.spl.mics.application.services.LiDarService;
import bgu.spl.mics.application.services.PoseService;
import bgu.spl.mics.application.services.TimeService;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
    private final HashMap<Integer, Pose> poseList;
    private final HashMap<String, LandMark> landMarkLinkedList;
    private StatisticalFolder statisticalFolder;
    private LinkedList<TrackedObject> backup;
 //   private final LinkedList<Camera> cameras;
 //   private final LinkedList<LiDarWorkerTracker> lidars;
 //   private GPSIMU gpsimu;
 //   private TimeService timeService;
    private InitializedEvent initialization;
    public Object sysLock;
    private int cameraNumber;
    private int lidarNumber;
    private int cameraCounter;
    private int lidarCounter;
    private boolean GPSSTATUS;
    private boolean CLOCKSTATUS;

    public void crash() {
        statisticalFolder.setLastPoses(new LinkedList<>(poseList.values()));
        statisticalFolder.setFinalLandMarks(new LinkedList<>(landMarkLinkedList.values()));
        interruptClock();
    }

    public void GPSTerminated() {
 //       System.out.println("GPSTerminated");
        GPSSTATUS = false;
    }

    // Singleton instance holder
    private static class FusionSlamHolder {
        private static final FusionSlam instance = new FusionSlam();

    }

    private FusionSlam() {
        poseList = new HashMap<>();
        landMarkLinkedList = new HashMap<>();
 //       cameras = new LinkedList<>();
  //      lidars = new LinkedList<>();
        cameraCounter = 0;
        lidarCounter = 0;
        GPSSTATUS = false;
        CLOCKSTATUS = false;
        backup = new LinkedList<>();
    }

    public void setCameraCount(int count){
        cameraNumber = count;
    }
    public void setLidarCount(int count){
        lidarNumber = count;
    }

    public void cameraInitialized(){
        cameraCounter++;
    }
    public void lidarInitialized(){
        lidarCounter++;
    }

    public void cameraTerminated(){
  //      System.out.println("cameraTerminated");
        cameraCounter--;
    }
    public void lidarTerminated(){
 //       System.out.println("lidarTerminated");
        lidarCounter--;
    }

    public boolean checkAndTerminate(){
        if(cameraCounter == 0  && lidarCounter == 0 && !GPSSTATUS){
            interruptClock();
            return true;
        }
        return false;
    }

    public void addLandMarks(int amount){
        statisticalFolder.addLandMarks(amount);
    }

    public void setSysLock(Object sysLock){
        this.sysLock = sysLock;
    }

    public static FusionSlam getInstance() {
        return FusionSlam.FusionSlamHolder.instance;
    }

    public void setPose(Pose pose) {
        synchronized (poseList) {
            poseList.put(pose.getTime(), pose);
        }
    }

    public void setStatisticalFolder(StatisticalFolder statisticalFolder) {
        this.statisticalFolder = statisticalFolder;
    }
    /**
    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
    }
    public void setGpsimu(GPSIMU gpsimu) {
        this.gpsimu = gpsimu;
    }
    public void addLidar(LiDarWorkerTracker lidar) {
        synchronized (lidars) {
            lidars.add(lidar);
        }

    }
    public void addCamera(Camera camera) {
        synchronized (cameras) {
            cameras.add(camera);
        }

    }*/
    /**
    public boolean termination(){
        for(Camera camera : cameras){
            if(camera.getStatus() != STATUS.DOWN)
                return false;
        }
        for(LiDarWorkerTracker lidar : lidars){
            if(lidar.getStatus() != STATUS.DOWN)
                return false;
        }
        if (gpsimu.getStatus() != STATUS.DOWN)
            return false;
        interruptClock();
        return true;
    }*/

    public void interruptClock(){
        synchronized (sysLock){
            sysLock.notifyAll();
        }
    }

    public Pose getPose(int tick) {
       synchronized (poseList) {
           if (!poseList.containsKey(tick)) {
               return null;
           }
           return poseList.get(tick);
       }
    }
/**
    public void addLandMark(LandMark landMark) {
        synchronized (landMarkLinkedList) {
            landMarkLinkedList.put(landMark.getId(), landMark);
        }
    }

    public boolean isLandMarkExist(String id){
        synchronized (landMarkLinkedList) {
            return landMarkLinkedList.containsKey(id);
        }
    }*/

    public void finish(){
        statisticalFolder.setFinalLandMarks(new LinkedList<>(landMarkLinkedList.values()));
    }

    public boolean updateMap(LandMark landMark) {
        synchronized (landMarkLinkedList) {
            if(!landMarkLinkedList.containsKey(landMark.getId())){
                landMarkLinkedList.put(landMark.getId(), landMark);
         //       System.out.println("fslm added new Landmark: "+landMark);
                return true;
            }
            else{
                landMarkLinkedList.get(landMark.getId()).updateCoordinates(landMark);
       //         System.out.println("fslm updated old Landmark: "+landMarkLinkedList.get(landMark.getId()));
                return false;
            }
        }
    }

    public InitializedEvent startProcessUsingIndicators(InitializedEvent e){
        if(e.getService() instanceof TimeService){
            initialization = e;
            CLOCKSTATUS = true;
        }
        if(e.getService() instanceof CameraService){
            cameraInitialized();
        }
        else if(e.getService() instanceof LiDarService){
            lidarInitialized();
        }
        else if(e.getService() instanceof PoseService){
            GPSSTATUS = true;
        }
        if( cameraCounter < cameraNumber || lidarCounter < lidarNumber || !GPSSTATUS || !CLOCKSTATUS)
            return null;
        else
            return initialization;
    }
/**    public InitializedEvent startProcess(InitializedEvent e){
        if(e.getService() instanceof TimeService){
            initialization = e;
        }
        for(Camera c : cameras)
            if(c.getStatus() == STATUS.DOWN)
                return null;
        for(LiDarWorkerTracker l : lidars)
            if(l.getStatus() == STATUS.DOWN)
                return null;
        if(gpsimu.getStatus() == STATUS.DOWN)
            return null;
        else
            return initialization;
    }*/

    public void calculate(LinkedList<TrackedObject> trackedObjects){
        if(trackedObjects == null || trackedObjects.isEmpty())
            return;
        trackedObjects.addAll(backup);
        backup = new LinkedList<>();

        for(TrackedObject to : trackedObjects) {
            Pose currentPose = getPose(to.getTime());
            if(currentPose == null) {
                backup.add(to);
                continue;
            }
         //   System.out.print("CurrentPose: " + currentPose);
            LinkedList<CloudPoint> localCloudPoints = to.getCoordinates();
            LinkedList<CloudPoint> globalCloudPoints = new LinkedList<>();
            //Calculating landmarks in global coordinates
            float yawRad = (currentPose.getYaw() * (float) Math.PI) / 180;
            float cosRad = (float) Math.cos(yawRad);
            float sinRad = (float) Math.sin(yawRad);

            //new logic for more than 2 coordinates
            for (CloudPoint local : localCloudPoints) {
                CloudPoint global = new CloudPoint(
                        currentPose.getX() + ((local.x() * cosRad) - (local.y() * sinRad)),
                        currentPose.getY() + ((local.x() * sinRad) + (local.y() * cosRad)));
                globalCloudPoints.add(global);
            }

/**
            CloudPoint local1 = localCloudPoints.get(0);
            CloudPoint local2 = localCloudPoints.get(1);
       //     System.out.println("yaw: " + yawRad + " cos: " + cosRad + " sin: " + sinRad + " local1: " + local1 + " local2: " + local2);
            CloudPoint global1 = new CloudPoint(
                    currentPose.getX() + ((local1.x() * cosRad) - (local1.y() * sinRad)),
                    currentPose.getY() + ((local1.x() * sinRad) + (local1.y() * cosRad)));
            CloudPoint global2 = new CloudPoint(
                    currentPose.getX() + ((local2.x() * cosRad) - (local2.y() * sinRad)),
                    currentPose.getY() + ((local2.x() * sinRad) + (local2.y() * cosRad)));*/
        //    System.out.println("global1: " + global1 + " global2: " + global2);
            //fusion.updateMap returns true if new landmark, else false
            //if previously detected, updates coordinates to averages
            if (updateMap(new LandMark(to.getId(), to.getDescription(), globalCloudPoints)))
                addLandMarks(1);
        }
    }

}
