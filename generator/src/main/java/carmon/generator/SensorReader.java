package carmon.generator;

import carmon.events.CurrentSpeed;
import carmon.events.DriverSeatOccupied;
import carmon.events.TirePressure;

import java.util.Timer;
import java.util.TimerTask;

public class SensorReader {
    private Car car;

    public SensorReader(Car car) {
        this.car = car;
    }

    public void schedule(long period) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                printSensors();
            }
        }, period, period);
    }

    private void printSensors() {
        CurrentSpeed speed = new CurrentSpeed(car.getCurrentSpeed());
        TirePressure pressure = new TirePressure(car.getTirePressure());
        DriverSeatOccupied driverSeatOccupied = new DriverSeatOccupied(car.isDriverSeatOccupied());
        System.out.println(speed.toJSON());
        System.out.println(pressure.toJSON());
        System.out.println(driverSeatOccupied.toJSON());
        System.out.println();
    }
}
