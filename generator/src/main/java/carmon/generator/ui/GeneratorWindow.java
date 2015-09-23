package carmon.generator.ui;

import carmon.generator.Car;
import carmon.generator.SensorReader;

import javax.swing.*;
import java.awt.*;
import java.util.Hashtable;

public class GeneratorWindow extends JFrame implements Car {

    private static final int SPEED_MIN = 0;
    private static final int SPEED_MAX = 200;
    private static final int SPEED_INIT = 0;

    private static final int PRESSURE_MIN = 0;
    private static final int PRESSURE_MAX = 3;
    private static final int PRESSURE_INIT = 2;
    private static final int PRESSURE_MULTIPLIER = 10;

    private static final int THROW_OUT_SPEED = 100;

    private static final long SENSOR_READ_PERIOD = 200;

    private boolean isDriverSeatOccupied;

    private JButton getIntoCarButton;
    private JLabel speedLabel;
    private JSlider speedSlider;
    private JLabel pressureLabel;
    private JSlider pressureSlider;
    private JButton bumpButton;

    private GeneratorWindow() {
        initLayout();
        setTitle("Sensor Data Generator");
        setSize(300, 300);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void initLayout() {
        Container pane = getContentPane();
        GridLayout layout = new GridLayout(0, 1);
        pane.setLayout(layout);

        initComponents();
        add(getIntoCarButton);
        add(speedLabel);
        add(speedSlider);
        add(bumpButton);
        add(pressureLabel);
        add(pressureSlider);
        validateState();
    }

    private void initComponents() {
        getIntoCarButton = new JButton("Get into car");

        speedLabel = new JLabel("Speed");
        speedSlider = new JSlider(JSlider.HORIZONTAL, SPEED_MIN, SPEED_MAX, SPEED_INIT);
        speedSlider.setMinorTickSpacing(10);
        speedSlider.setMajorTickSpacing(100);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);

        pressureLabel = new JLabel("Pressure");
        pressureSlider = new JSlider(JSlider.HORIZONTAL,
                PRESSURE_MIN * PRESSURE_MULTIPLIER,
                PRESSURE_MAX * PRESSURE_MULTIPLIER,
                PRESSURE_INIT * PRESSURE_MULTIPLIER);
        pressureSlider.setMinorTickSpacing(1);
        pressureSlider.setMajorTickSpacing(10);
        pressureSlider.setPaintTicks(true);
        pressureSlider.setPaintLabels(true);
        Hashtable<Integer, JLabel> pressureLabels = new Hashtable<>();
        for (int i = PRESSURE_MIN; i <= PRESSURE_MAX; i++) {
            pressureLabels.put(i * PRESSURE_MULTIPLIER, new JLabel(i + ".0"));
        }
        pressureSlider.setLabelTable(pressureLabels);

        bumpButton = new JButton("Bump");
        initHandlers();
    }

    private void initHandlers() {
        getIntoCarButton.addActionListener((e) -> {
            isDriverSeatOccupied = true;
            validateState();
        });
        bumpButton.addActionListener((e) -> {
            if (getCurrentSpeed() >= THROW_OUT_SPEED) {
                isDriverSeatOccupied = false;
                validateState();
            }
            speedSlider.setValue(SPEED_MIN);
        });
    }

    private void validateState() {
        getIntoCarButton.setEnabled(!isDriverSeatOccupied);
        speedLabel.setEnabled(isDriverSeatOccupied);
        speedSlider.setEnabled(isDriverSeatOccupied);
        if (!isDriverSeatOccupied) {
            speedSlider.setValue(SPEED_INIT);
        }
        bumpButton.setEnabled(isDriverSeatOccupied);
    }

    @Override
    public double getCurrentSpeed() {
        return speedSlider.getValue();
    }

    @Override
    public double getTirePressure() {
        return (double) pressureSlider.getValue() / PRESSURE_MULTIPLIER;
    }

    @Override
    public boolean isDriverSeatOccupied() {
        return isDriverSeatOccupied;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            GeneratorWindow generator = new GeneratorWindow();
            generator.setVisible(true);
            new SensorReader(generator).schedule(SENSOR_READ_PERIOD);
        });
    }
}
