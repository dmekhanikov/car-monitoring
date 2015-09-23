package carmon.events;

public class DriverSeatOccupied extends Event {
    private final boolean occupied;

    public DriverSeatOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public boolean isOccupied() {
        return occupied;
    }
}
