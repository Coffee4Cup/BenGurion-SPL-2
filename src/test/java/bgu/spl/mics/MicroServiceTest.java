package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;


import static org.junit.jupiter.api.Assertions.*;


class MicroServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(MicroServiceTest.class);

    MessageBusSingleton messageBusSingleton;
    Thread thread;
    MicroService microService;

    @BeforeEach
    void setUp() {
        messageBusSingleton.setInstance(new MessageBusTestImpl());

    }

    @Test
    void subscribeEvent() {
        for(int i = 0; i < 10; i++){
            MicroService microService = new MicroService("test" + i) {
                @Override
                protected void initialize() {
                    subscribeBroadcast(DummyBroadcast.class, c -> {
                        logger.info(()->"DummyBroadcast received by"  + getName());
                    });
                }
            };
            messageBusSingleton.getInstance().register(microService);
        }

    }

    @Test
    void subscribeBroadcast() {
    }

    @Test
    void sendEvent() {
    }

    @Test
    void sendBroadcast() {
    }

    @Test
    void complete() {
    }

    @Test
    void initialize() {
    }

    @Test
    void terminate() {
    }

    @Test
    void getName() {
    }

    @Test
    void run() {
    }
}