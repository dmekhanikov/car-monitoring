package carmon.api;

import carmon.EventService;
import carmon.events.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SensorDataController {

    @Autowired
    EventService eventService;

    @RequestMapping(value = "/carmon", method = RequestMethod.POST)
    public Event carmon(@RequestBody Event event) {
        eventService.sendEvent(event);
        return event;
    }
}
