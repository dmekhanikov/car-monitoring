package carmon.generator;

import carmon.events.CurrentSpeed;
import carmon.events.DriverSeatOccupied;
import carmon.events.Event;
import carmon.events.TirePressure;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.JacksonConverter;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class EventSender {
    private static final String API_URL = "http://localhost:8080/";
    private static final int THREADS_COUNT = 1;

    private final Car car;
    private final CarmonApiService carmon;

    public EventSender(Car car) {
        this.car = car;
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setConverter(new JacksonConverter())
                .build();
        this.carmon = restAdapter.create(CarmonApiService.class);
    }

    public void schedule(long period) {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(THREADS_COUNT);
        executor.scheduleWithFixedDelay(this::sendEvents, period, period, TimeUnit.MILLISECONDS);
    }

    private void sendEvents() {
        long timestamp = System.currentTimeMillis();
        CurrentSpeed currentSpeed = new CurrentSpeed(car.getCurrentSpeed(), timestamp);
        TirePressure tirePressure = new TirePressure(car.getTirePressure(), timestamp);
        DriverSeatOccupied driverSeatOccupied = new DriverSeatOccupied(car.isDriverSeatOccupied(), timestamp);
        Callback<Event> callback = new Callback<Event>() {
            @Override
            public void success(Event event, Response response) {}

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        };
        carmon.sendEvent(currentSpeed, callback);
        carmon.sendEvent(tirePressure, callback);
        carmon.sendEvent(driverSeatOccupied, callback);
    }
}
