package bgu.spl.mics;

import bgu.spl.mics.application.objects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

public class TestCamera {
    private Camera camera;

    @BeforeEach
    public void setUp(){
        StatisticalFolder statisticalFolder = new StatisticalFolder();
        LinkedList<StampedDetectedObjects> testStampedObjects = new LinkedList<>();
        LinkedList<DetectedObject> testObjects1 = new LinkedList<>();
        testObjects1.add(new DetectedObject("object1", "test"));
        testObjects1.add(new DetectedObject("object2", "test"));
        LinkedList<DetectedObject> testObjects2 = new LinkedList<>();
        testObjects2.add(new DetectedObject("ERROR", "error test"));
        testObjects2.add(new DetectedObject("object2", "test"));
        testStampedObjects.add(new StampedDetectedObjects(1, testObjects1));
        testStampedObjects.add(new StampedDetectedObjects(2, testObjects2));
        camera = new Camera(0, 0, "testCamera", testStampedObjects, statisticalFolder);
    }

    @Test
    public void testGetDetectedObjectList1(){
        StampedDetectedObjects receivedList = camera.getDetectedObjectList(1);
        assertFalse(camera.isDone());
        assertEquals("object1", receivedList.getDetectedObjects().get(0).id());
        assertEquals("object2", receivedList.getDetectedObjects().get(1).id());
    }

    @Test
    public void testDetectedObjectList2(){
        StampedDetectedObjects receivedList = camera.getDetectedObjectList(2);
        assertSame(STATUS.ERROR, camera.getStatus());
        assertEquals(1, receivedList.getNumOfDetectedObjects());
        assertEquals("object2",receivedList.getDetectedObjects().get(0).id());
    }


}
