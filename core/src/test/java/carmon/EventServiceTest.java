package carmon;

import carmon.events.CurrentSpeed;
import carmon.events.DriverSeatOccupied;
import carmon.events.TirePressure;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class)
@TestPropertySource("classpath:/test.yml")
public class EventServiceTest {
    @Configuration
    public static class TestConfig {
        @Bean
        public EventService eventService() {
            return new EventService();
        }

        @Bean
        public IndicatorPanel indicatorPanel() {
            return new IndicatorPanel();
        }

        @Bean
        public PropertySourcesPlaceholderConfigurer propertyPlaceholder() {
            return new PropertySourcesPlaceholderConfigurer();
        }
    }

    @Autowired
    private EventService eventService;

    @Autowired
    private IndicatorPanel indicatorPanel;

    private void resetPanel() {
        indicatorPanel.setLossOfTirePressure(false);
        indicatorPanel.setBlownOutTire(false);
        indicatorPanel.setCarStopped(true);
        indicatorPanel.setOccupantThrown(false);
    }

    private static long now() {
        return System.currentTimeMillis();
    }

    @Test
    public void lowPressure() {
        resetPanel();
        eventService.sendEvent(new TirePressure(0.7, now()));
        assertTrue(indicatorPanel.isLossOfTirePressure());
        eventService.sendEvent(new TirePressure(1.8, now()));
        assertFalse(indicatorPanel.isLossOfTirePressure());
        eventService.sendEvent(new TirePressure(1.48, now()));
        assertTrue(indicatorPanel.isLossOfTirePressure());
    }

    @Test
    public void blownOutTire() {
        resetPanel();
        long start = now();
        eventService.sendEvent(new TirePressure(2.2, start));
        eventService.sendEvent(new TirePressure(2.1, start + 1000));
        assertFalse(indicatorPanel.isBlownOutTire());
        eventService.sendEvent(new TirePressure(2.0, start + 1200));
        assertTrue(indicatorPanel.isBlownOutTire());
        eventService.sendEvent(new TirePressure(2.0, start + 2200));
        assertFalse(indicatorPanel.isBlownOutTire());
        eventService.sendEvent(new TirePressure(1.3, start + 3200));
        assertTrue(indicatorPanel.isBlownOutTire());
        eventService.sendEvent(new TirePressure(1.3, start + 4200));
        assertTrue(indicatorPanel.isBlownOutTire());
    }

    @Test
    public void stop() {
        resetPanel();
        eventService.sendEvent(new CurrentSpeed(20, now()));
        assertFalse(indicatorPanel.isCarStopped());
        eventService.sendEvent(new CurrentSpeed(1, now()));
        assertTrue(indicatorPanel.isCarStopped());
        eventService.sendEvent(new CurrentSpeed(3, now()));
        assertTrue(indicatorPanel.isCarStopped());
    }

    @Test
    public void occupantThrownAccident() {
        resetPanel();
        long start = now();
        eventService.sendEvent(new CurrentSpeed(120, start));
        eventService.sendEvent(new DriverSeatOccupied(true, start));
        eventService.sendEvent(new DriverSeatOccupied(false, start + 500));
        assertTrue(indicatorPanel.isOccupantThrown());
        eventService.sendEvent(new DriverSeatOccupied(true, start + 1000));
        assertFalse(indicatorPanel.isOccupantThrown());
        eventService.sendEvent(new DriverSeatOccupied(false, start + 2500));
        assertFalse(indicatorPanel.isOccupantThrown());
    }
}
