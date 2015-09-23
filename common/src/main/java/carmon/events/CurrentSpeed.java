package carmon.events;

public class CurrentSpeed extends Event {
    private final double speed;

    public CurrentSpeed(double speed) {
        this.speed = speed;
    }

    public double getSpeed() {
        return speed;
    }
}
