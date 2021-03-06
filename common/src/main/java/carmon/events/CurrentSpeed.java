package carmon.events;

public class CurrentSpeed extends Event {
    private double speed;

    public CurrentSpeed() {}

    public CurrentSpeed(double speed, long timestamp) {
        super(timestamp);
        this.speed = speed;
    }

    public double getSpeed() {
        return speed;
    }
}
