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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class EventService {
    private static final String TIRE_PRESSURE = TirePressure.class.getSimpleName();
    private static final String CURRENT_SPEED = CurrentSpeed.class.getSimpleName();
    private static final String DRIVER_SEAT_OCCUPIED = DriverSeatOccupied.class.getSimpleName();

    private static final int MILLISECONDS_IN_SECOND = 1000;

    @Value("${pressure.low}")
    private double lowPressureThreshold;
    @Value("${pressure.normal}")
    private double normalPressureThreshold;
    @Value("${pressure.max-loss-speed}")
    private double lossOfPressureThreshold;
    @Value("${pressure.eps}")
    private double pressureEps;
    @Value("${speed.eps}")
    private double speedEps;
    @Value("${seat.normal-exit-time}")
    private int normalExitTime;

    private EPServiceProvider epService;

    @Autowired
    private IndicatorPanel indicatorPanel;

    @Autowired
    private void init() {
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
        String lowPressureExpression =
                "select * from "+ TIRE_PRESSURE +" where pressure < " + lowPressureThreshold;
        createStatement(lowPressureExpression).addListener(((newEvents, oldEvents) -> {
            if (!indicatorPanel.isLossOfTirePressure()) {
                indicatorPanel.setLossOfTirePressure(true);
                notify("Loss of tire pressure!");
            }
        }));

        String normalPressureExpression =
                "select * from " + TIRE_PRESSURE + " where pressure > " + normalPressureThreshold;
        createStatement(normalPressureExpression).addListener(((newEvents, oldEvents) -> {
            if (indicatorPanel.isLossOfTirePressure()) {
                indicatorPanel.setLossOfTirePressure(false);
                notify("Normal tire pressure");
            }
        }));
    }

    private void createBlownOutTireStatements() {
        String blownOutTireExpression = String.format(Locale.ENGLISH,
                "select * from pattern [" +
                        "every tp1 = %s -> " +
                        "%s((tp1.pressure - pressure) / ((timestamp - tp1.timestamp) / %d) > %.2f)]",
                TIRE_PRESSURE, TIRE_PRESSURE, MILLISECONDS_IN_SECOND, lossOfPressureThreshold);
        createStatement(blownOutTireExpression).addListener((newEvents, oldEvents) ->{
            if (!indicatorPanel.isBlownOutTire()) {
                indicatorPanel.setBlownOutTire(true);
                notify("A tire has blown out!");
            }
        });

        String fixedTireExpression = String.format(Locale.ENGLISH,
                "select * from pattern [" +
                        "every tp1 = %s(pressure > %.2f) -> %s(tp1.pressure - pressure < %.2f)]",
                TIRE_PRESSURE, normalPressureThreshold, TIRE_PRESSURE, pressureEps);
        createStatement(fixedTireExpression).addListener((newEvents, oldEvents) -> {
            if (indicatorPanel.isBlownOutTire()) {
                indicatorPanel.setBlownOutTire(false);
                notify("A tire has been fixed");
            }
        });
    }

    private void createCarStoppedStatements() {
        String carStoppedExpression = "select * from "+ CURRENT_SPEED +" where speed < " + speedEps;
        createStatement(carStoppedExpression).addListener((newEvents, oldEvents) -> {
            if (!indicatorPanel.isCarStopped()) {
                indicatorPanel.setCarStopped(true);
                notify("Car stopped");
            }
        });

        String carMovedExpression = "select * from " + CURRENT_SPEED + " where speed > " + 2 * speedEps;
        createStatement(carMovedExpression).addListener((newEvents, oldEvents) -> {
            if (indicatorPanel.isCarStopped()) {
                indicatorPanel.setCarStopped(false);
                notify("Car moved");
            }
        });
    }

    private void createOccupantThrownStatements() {
        String occupantThrownExpression = String.format(Locale.ENGLISH,
                "select * from pattern [" +
                        "every cs = %s(speed > %.2f) -> %s(occupied = false and timestamp - cs.timestamp < %d)]",
                CURRENT_SPEED, speedEps, DRIVER_SEAT_OCCUPIED, normalExitTime);
        createStatement(occupantThrownExpression).addListener((newEvents, oldEvents) -> {
            if (!indicatorPanel.isOccupantThrown()) {
                indicatorPanel.setOccupantThrown(true);
                notify("Occupant thrown accident!");
            }
        });

        String driverReturnedExpression = String.format(Locale.ENGLISH,
                "select * from pattern[" +
                        "every %s(occupied = false) -> %s(occupied = true)]",
                DRIVER_SEAT_OCCUPIED, DRIVER_SEAT_OCCUPIED
        );
        createStatement(driverReturnedExpression).addListener((newEvents, oldEvents) -> {
            if (indicatorPanel.isOccupantThrown()) {
                indicatorPanel.setOccupantThrown(false);
                notify("Driver returned");
            }
        });
    }
}
