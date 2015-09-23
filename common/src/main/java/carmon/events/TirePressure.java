package carmon.events;

public class TirePressure extends Event {
    private final double pressure;

    public TirePressure(double pressure) {
        this.pressure = pressure;
    }

    public double getPressure() {
        return pressure;
    }
}
