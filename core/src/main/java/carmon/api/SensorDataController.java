package carmon.api;

import carmon.events.Event;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SensorDataController {

    @RequestMapping(value = "/carmon", method = RequestMethod.POST)
    public void carmon(@RequestBody Event event) {
        System.out.println(event.toJSON());
    }
}
