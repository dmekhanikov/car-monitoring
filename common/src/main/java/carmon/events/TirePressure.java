package carmon.events;

public class TirePressure extends Event {
    private double pressure;

    public TirePressure() {
    }

    public TirePressure(double pressure) {
        this.pressure = pressure;
    }

    public double getPressure() {
        return pressure;
    }
}
