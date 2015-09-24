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

public class EventSender {
    private static final String API_URL = "http://localhost:8080/";

    private Car car;
    private CarmonApiService carmon;

    public EventSender(Car car) {
        this.car = car;
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setConverter(new JacksonConverter())
                .build();
        this.carmon = restAdapter.create(CarmonApiService.class);
    }

    public void schedule(long period) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendEvents();
            }
        }, period, period);
    }

    private void sendEvents() {
        Callback<Event> callback = new Callback<Event>() {
            @Override
            public void success(Event event, Response response) {}

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        };
        carmon.sendEvent(new CurrentSpeed(car.getCurrentSpeed()), callback);
        carmon.sendEvent(new TirePressure(car.getTirePressure()), callback);
        carmon.sendEvent(new DriverSeatOccupied(car.isDriverSeatOccupied()), callback);
    }
}
