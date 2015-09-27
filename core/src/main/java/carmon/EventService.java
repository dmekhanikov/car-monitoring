package carmon;

import carmon.events.CurrentSpeed;
import carmon.events.DriverSeatOccupied;
import carmon.events.Event;
import carmon.events.TirePressure;
import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class EventService {
    private static final String TIRE_PRESSURE = TirePressure.class.getSimpleName();
    private static final String CURRENT_SPEED = CurrentSpeed.class.getSimpleName();
    private static final String DRIVER_SEAT_OCCUPIED = DriverSeatOccupied.class.getSimpleName();

    private static final double LOW_PRESSURE_THRESHOLD = 1.5;
    private static final double NORMAL_PRESSURE_THRESHOLD = 1.6;
    private static final double LOSS_OF_PRESSURE_SPEED_THRESHOLD = 0.3;
    private static final double PRESSURE_EPS = 0.01;
    private static final double SPEED_EPS = 2;
    private static final int MILLISECONDS_IN_SECOND = 1000;
    private static final int NORMAL_EXIT_TIME = 1000;

    private static final String LOW_PRESSURE =
            "select * from "+ TIRE_PRESSURE +" where pressure < " + LOW_PRESSURE_THRESHOLD;
    private static final String NORMAL_PRESSURE =
            "select * from " + TIRE_PRESSURE + " where pressure > " + NORMAL_PRESSURE_THRESHOLD;
    private static final String BLOWN_OUT_TIRE = String.format(Locale.ENGLISH,
                    "select 1 from pattern [" +
                            "every tp1 = %s -> " +
                            "%s((tp1.pressure - pressure) / ((timestamp - tp1.timestamp) / %d) > %.2f)]",
            TIRE_PRESSURE, TIRE_PRESSURE, MILLISECONDS_IN_SECOND, LOSS_OF_PRESSURE_SPEED_THRESHOLD);
    private static final String FIXED_TIRE = String.format(Locale.ENGLISH,
            "select * from pattern [" +
                    "every tp1 = %s(pressure > %.2f) -> %s(tp1.pressure - pressure < %.2f)]",
            TIRE_PRESSURE, NORMAL_PRESSURE_THRESHOLD, TIRE_PRESSURE, PRESSURE_EPS);
    private static final String CAR_STOPPED = "select 1 from "+ CURRENT_SPEED +" where speed < " + SPEED_EPS;
    private static final String CAR_MOVED = "select 1 from " + CURRENT_SPEED + " where speed > " + 2 * SPEED_EPS;
    private static final String OCCUPANT_THROWN = String.format(Locale.ENGLISH,
            "select * from pattern [" +
                "every cs = %s(speed > %.2f) -> %s(occupied = false and timestamp - cs.timestamp < %d)]",
            CURRENT_SPEED, SPEED_EPS, DRIVER_SEAT_OCCUPIED, NORMAL_EXIT_TIME);
    private static final String DRIVER_RETURNED = String.format(Locale.ENGLISH,
            "select * from pattern[" +
                    "every %s(occupied = false) -> %s(occupied = true)]",
            DRIVER_SEAT_OCCUPIED, DRIVER_SEAT_OCCUPIED
    );

    private final EPServiceProvider epService;

    @Autowired
    private IndicatorPanel indicatorPanel;

    private EventService() {
        Configuration configuration = new Configuration();
        configuration.addEventTypeAutoName(Event.class.getPackage().getName());
        epService = EPServiceProviderManager.getDefaultProvider(configuration);
        createTirePressureStatements();
        createBlownOutTireStatements();
        createCarStoppedStatements();
        createOccupantThrownStatements();
    }

    public EPStatement createStatement(String expression) {
        return epService.getEPAdministrator().createEPL(expression);
    }

    public void sendEvent(Event event) {
        epService.getEPRuntime().sendEvent(event);
    }

    private void notify(String message) {
        System.out.println(message);
    }

    private void createTirePressureStatements() {
        createStatement(LOW_PRESSURE).addListener(((newEvents, oldEvents) -> {
            if (!indicatorPanel.isLossOfTirePressure()) {
                indicatorPanel.setLossOfTirePressure(true);
                notify("Loss of tire pressure!");
            }
        }));
        createStatement(NORMAL_PRESSURE).addListener(((newEvents, oldEvents) -> {
            if (indicatorPanel.isLossOfTirePressure()) {
                indicatorPanel.setLossOfTirePressure(false);
                notify("Normal tire pressure");
            }
        }));
    }

    private void createBlownOutTireStatements() {
        createStatement(BLOWN_OUT_TIRE).addListener((newEvents, oldEvents) ->{
            if (!indicatorPanel.isBlownOutTire()) {
                indicatorPanel.setBlownOutTire(true);
                notify("A tire has blown out!");
            }
        });
        createStatement(FIXED_TIRE).addListener((newEvents, oldEvents) -> {
            if (indicatorPanel.isBlownOutTire()) {
                indicatorPanel.setBlownOutTire(false);
                notify("A tire has been fixed");
            }
        });
    }

    private void createCarStoppedStatements() {
        createStatement(CAR_STOPPED).addListener((newEvents, oldEvents) -> {
            if (!indicatorPanel.isCarStopped()) {
                indicatorPanel.setCarStopped(true);
                notify("Car stopped");
            }
        });
        createStatement(CAR_MOVED).addListener((newEvents, oldEvents) -> {
            if (indicatorPanel.isCarStopped()) {
                indicatorPanel.setCarStopped(false);
                notify("Car moved");
            }
        });
    }

    private void createOccupantThrownStatements() {
        createStatement(OCCUPANT_THROWN).addListener((newEvents, oldEvents) -> {
            if (!indicatorPanel.isOccupantThrown()) {
                indicatorPanel.setOccupantThrown(true);
                notify("Occupant thrown accident!");
            }
        });
        createStatement(DRIVER_RETURNED).addListener((newEvents, oldEvents) -> {
            if (indicatorPanel.isOccupantThrown()) {
                indicatorPanel.setOccupantThrown(false);
                notify("Driver returned");
            }
        });
    }
}
