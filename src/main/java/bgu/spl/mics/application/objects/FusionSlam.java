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
 * @post: lidarCounter = {@Pre: lidarCounter}
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

    /**
 * @post: statisticalFolder.lastPoses updated with poseList values.
 * @post: statisticalFolder.finalLandMarks updated with landMarkLinkedList values.
 * @Pre: StatisticalFolder object must be initialized before calling this method.
     */
    public void crash() {
        statisticalFolder.setLastPoses(new LinkedList<>(poseList.values()));
        statisticalFolder.setFinalLandMarks(new LinkedList<>(landMarkLinkedList.values()));
        interruptClock();
    }

    /**
 * @post: GPSSTATUS = false.
     */
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

    /**
 * @post: cameraNumber = count.
     */
    public void setCameraCount(int count){
        cameraNumber = count;
    }
    /**
 * @post: lidarNumber = count.
     */
    public void setLidarCount(int count){
        lidarNumber = count;
    }

    /**
 * @post: cameraCounter = {@Pre: cameraCounter} + 1.
     */
    public void cameraInitialized(){
        cameraCounter++;
    }
    /**
 * @post: lidarCounter = {@Pre: lidarCounter} + 1.
     */
    public void lidarInitialized(){
        lidarCounter++;
    }

    /**
 * @post: cameraCounter = {@Pre: cameraCounter} - 1.
     */
    public void cameraTerminated(){
  //      System.out.println("cameraTerminated");
        cameraCounter--;
    }
    /**
 * @post: lidarCounter = {@Pre: lidarCounter} - 1.
     */
    public void lidarTerminated(){
 //       System.out.println("lidarTerminated");
        lidarCounter--;
    }

    /**
 * @post: Returns true and ensures clock is interrupted if all sensor counters and GPSSTATUS are down.
     */
    public boolean checkAndTerminate(){
        if(cameraCounter == 0  && lidarCounter == 0 && !GPSSTATUS){
            interruptClock();
            return true;
        }
        return false;
    }

    /**
 * @post: statisticalFolder.landMarks incremented by amount.
    */
    public void addLandMarks(int amount){
        statisticalFolder.addLandMarks(amount);
    }

    /**
 * @post: sysLock is set to argument value.
     */
    public void setSysLock(Object sysLock){
        this.sysLock = sysLock;
    }

    /**
 * @post: Returns singleton instance of FusionSlam.
     */
    public static FusionSlam getInstance() {
        return FusionSlam.FusionSlamHolder.instance;
    }

    /**
 * @post: Adds or updates a Pose object in poseList synchronized on poseList.
     */
    public void setPose(Pose pose) {
        synchronized (poseList) {
            poseList.put(pose.getTime(), pose);
        }
    }
    /**
 * @post: statisticalFolder set to argument value.
     */
    public void setStatisticalFolder(StatisticalFolder statisticalFolder) {
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

    /**
 * @post: sysLock.notifyAll called within a synchronized block.
     */
    public void interruptClock(){
        synchronized (sysLock){
            sysLock.notifyAll();
        }
    }

    /**
 * @post: Returns Pose associated with tick or null if nonexistent.
     */
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

    /**
 * @post: statisticalFolder.finalLandMarks set to current landMarkLinkedList values.
     */
    public void finish(){
        statisticalFolder.setFinalLandMarks(new LinkedList<>(landMarkLinkedList.values()));
        interruptClock();
    }

    /**
 * @post: Updates landMarkLinkedList with a new or updated LandMark and updates coordinates of matched landmarks.
 *       Returns true if a new LandMark is added, false otherwise.
     */
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

    /**
 * @post: Updates relevant initialization indicators (e.g., camera/lidar counters, statuses) and returns
 *       InitializedEvent if preconditions for system initialization are met.
 * @Pre: All required services (time, GPS, camera, LiDAR) should be registered/in use.
     */
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

    /**
 * @post: Processes trackedObjects to compute global CloudPoints and update map.
 *       Adds unprocessed objects to backup.
     */
    public void calculate(LinkedList<TrackedObject> trackedObjects){
        if(trackedObjects == null || trackedObjects.isEmpty())
            return;
        if(!backup.isEmpty()) {
            trackedObjects.addAll(backup);
            backup = new LinkedList<>();
        }

        for(TrackedObject to : trackedObjects) {
            Pose currentPose = getPose(to.getTime());
       //     System.out.println("pose: "+currentPose);
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
   //         System.out.println("yaw: " + yawRad + " cos: " + cosRad + " sin: " + sinRad);
            //new logic for more than 2 coordinates
            for (CloudPoint local : localCloudPoints) {
 //               System.out.println(" local: " + local);
                CloudPoint global = new CloudPoint(
                        currentPose.getX() + ((local.x() * cosRad) - (local.y() * sinRad)),
                        currentPose.getY() + ((local.x() * sinRad) + (local.y() * cosRad)));
   //             System.out.println(" global: " + global);
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

    //TEST METHODS
    /**
 * @post: Returns all landmarks as a HashMap.
     */
    public HashMap<String, LandMark> getLandMarkLinkedList(){
        return landMarkLinkedList;
    }

    /**
 * @post: Clears all landmarks from landMarkLinkedList.
     */
    public void resetLandMarks(){
        landMarkLinkedList.clear();
    }
    /**
 * @post: Clears all poses from poseList.
     */
    public void resetPoses(){
        poseList.clear();
    }
    /**
 * @post: Clears the backup list of tracked objects.
     */
    public void clearBackUp(){
       backup.clear();
    }

}
