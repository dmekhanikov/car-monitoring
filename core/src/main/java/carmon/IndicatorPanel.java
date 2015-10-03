package carmon;

import org.springframework.stereotype.Component;

@Component
public class IndicatorPanel {
    private boolean lossOfTirePressure;
    private boolean blownOutTire;
    private boolean carStopped = true;
    private boolean occupantThrown;

    public boolean isLossOfTirePressure() {
        return lossOfTirePressure;
    }

    public void setLossOfTirePressure(boolean lossOfTirePressure) {
        this.lossOfTirePressure = lossOfTirePressure;
    }

    public boolean isBlownOutTire() {
        return blownOutTire;
    }

    public void setBlownOutTire(boolean blownOutTire) {
        this.blownOutTire = blownOutTire;
    }

    public boolean isCarStopped() {
        return carStopped;
    }

    public void setCarStopped(boolean carStopped) {
        this.carStopped = carStopped;
    }

    public boolean isOccupantThrown() {
        return occupantThrown;
    }

    public void setOccupantThrown(boolean occupantThrown) {
        this.occupantThrown = occupantThrown;
    }
}
