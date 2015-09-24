package carmon.generator;

import carmon.events.Event;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

public interface CarmonApiService {
    @POST("/carmon")
    void sendEvent(@Body Event event, Callback<Event> callback);
}
