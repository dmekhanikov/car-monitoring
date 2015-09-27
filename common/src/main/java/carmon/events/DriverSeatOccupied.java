package carmon.events;

public class DriverSeatOccupied extends Event {
    private boolean occupied;

    public DriverSeatOccupied() {}

    public DriverSeatOccupied(boolean occupied, long timestamp) {
        super(timestamp);
        this.occupied = occupied;
    }

    public boolean isOccupied() {
        return occupied;
    }
}
