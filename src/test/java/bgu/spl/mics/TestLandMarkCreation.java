package bgu.spl.mics;

import bgu.spl.mics.application.objects.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestLandMarkCreation {
    private LinkedList<TrackedObject> TOList;
    private TrackedObject TO1;
    private TrackedObject TO2;
    private Pose pose1;
    private Pose pose2;

    @BeforeEach
    public void setUp(){
        /**
         * get data from example_input
         * pose <- pose at time 1
         * TrackedObject <- object found at time 1. Should be in lidar_data
         * put trackObject in list
         * FusionSlam.getInstance().setPose(pose)
         * FusionSlam.getInstance().calculate(list)
         * LandMark result = FusionSlam.getInstance().getLandMarksList().get(0)
         * compare result data to landmark calculation.
         *
         * test2:
         * same as first test, this time add 2nd pose, same tracked object at time
         * of pose 2 , calculate again. result should be average of both calculations.
         */
        StatisticalFolder statisticalFolder = new StatisticalFolder();
        DetectedObject testDO = new DetectedObject("object1", "test");
        Double[][] cp = new Double[2][3];
        cp[0][0] = 0.1176;
        cp[0][1] = 3.6969;
        cp[0][2] = 0.104;
        cp[1][0] = 0.11362;
        cp[1][1] = 3.6039;
        cp[1][2] = 0.104;
        StampedCloudPoints testSCP = new StampedCloudPoints(1, "object1", cp);
        TO1 = new TrackedObject(testDO.id(), 1, testDO.description(), testSCP.getCloudPoint());
        pose1 = new Pose((float)-3.2076, (float)0.0755,(float) -87.48, 1);
        FusionSlam.getInstance().setStatisticalFolder(statisticalFolder);

        TOList = new LinkedList<>();

        cp[0][0] = 0.5;
        cp[0][1] = 3.9;
        cp[1][0] = 0.2;
        cp[1][1] = 3.7;
        StampedCloudPoints testSCP2= new StampedCloudPoints(2,"object1", cp);
        pose2 = new Pose((float)0.0,(float)3.6, (float)57.3, 2);
        TO2 = new TrackedObject(testDO.id(), 2, testDO.description(), testSCP2.getCloudPoint());

    }

    @AfterEach
    public void tearDown(){
        TOList = new LinkedList<>();
        TO1 = null;
        TO2 = null;
        FusionSlam.getInstance().resetLandMarks();
        FusionSlam.getInstance().resetPoses();
        FusionSlam.getInstance().clearBackUp();
    }

    @Test
    public void testLandMarkCreation(){

        FusionSlam.getInstance().setPose(pose1);
        TOList.add(TO1);
        FusionSlam.getInstance().calculate(TOList);
        LandMark result = FusionSlam.getInstance().getLandMarkLinkedList().get("object1");
        assertNotNull(result);
        LinkedList<CloudPoint> resultCoordinates = result.getCoordinates();
 //       System.out.println(resultCoordinates);
        assertTrue(resultCoordinates.get(0).x() <= 0.6 && resultCoordinates.get(0).x() >= 0.4);
        assertTrue(resultCoordinates.get(0).y() <= 0.2 && resultCoordinates.get(0).y() >= 0);
        assertTrue(resultCoordinates.get(1).x() <= 0.5 && resultCoordinates.get(1).x() >= 0.3);
        assertTrue(resultCoordinates.get(1).y() <= 0.2 && resultCoordinates.get(1).y() >= 0);
    }
    @Test
    public void testLandMarkUpdate(){
//        System.out.println(FusionSlam.getInstance().getLandMarkLinkedList().size() +
//                "\n" + FusionSlam.getInstance().getPose(2));
        TOList.add(TO1);
        FusionSlam.getInstance().setPose(pose1);
        FusionSlam.getInstance().calculate(TOList);
        LandMark result = FusionSlam.getInstance().getLandMarkLinkedList().get("object1");
        assertNotNull(result);
        LinkedList<CloudPoint> resultCoordinates = result.getCoordinates();
 //       System.out.println(resultCoordinates);
        TOList.clear();
        TOList.add(TO2);
        FusionSlam.getInstance().setPose(pose2);
        FusionSlam.getInstance().calculate(TOList);
        result = FusionSlam.getInstance().getLandMarkLinkedList().get("object1");
        assertNotNull(result);
        resultCoordinates = result.getCoordinates();
//        System.out.println(resultCoordinates);
//        System.out.println(FusionSlam.getInstance().getLandMarkLinkedList().size() +
//                "\n" + FusionSlam.getInstance().getPose(2));
        assertTrue(resultCoordinates.get(0).x() <= -1.1 && resultCoordinates.get(0).x() >= -1.4);
        assertTrue(resultCoordinates.get(0).y() <= 3.2 && resultCoordinates.get(0).y() >= 3);
        assertTrue(resultCoordinates.get(1).x() <= -1.2 && resultCoordinates.get(1).x() >= -1.4);
        assertTrue(resultCoordinates.get(1).y() <= 3.1 && resultCoordinates.get(1).y() >= 2.9);

    }

    @Test
    public void testLandMarkDelayedPose(){
        TOList.add(TO1);
        FusionSlam.getInstance().calculate(TOList);
        FusionSlam.getInstance().setPose(pose1);
        TOList.remove();
        TOList.add(TO2);
        FusionSlam.getInstance().calculate(TOList);
        LandMark result = FusionSlam.getInstance().getLandMarkLinkedList().get("object1");
        assertNotNull(result);
        LinkedList<CloudPoint> resultCoordinates = result.getCoordinates();
  //      System.out.println(resultCoordinates);
        assertTrue(resultCoordinates.get(0).x() <= 0.6 && resultCoordinates.get(0).x() >= 0.4);
        assertTrue(resultCoordinates.get(0).y() <= 0.2 && resultCoordinates.get(0).y() >= 0);
        assertTrue(resultCoordinates.get(1).x() <= 0.5 && resultCoordinates.get(1).x() >= 0.3);
        assertTrue(resultCoordinates.get(1).y() <= 0.2 && resultCoordinates.get(1).y() >= 0);
    }
}
