package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;

public class TestLandMarkCreation {

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
    }
}
