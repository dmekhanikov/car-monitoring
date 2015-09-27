package carmon.events;

public class TirePressure extends Event {
    private double pressure;

    public TirePressure() {}

    public TirePressure(double pressure, long timestamp) {
        super(timestamp);
        this.pressure = pressure;
    }

    public double getPressure() {
        return pressure;
    }
}
