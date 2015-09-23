package carmon.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class Event {
    private static ObjectMapper mapper = new ObjectMapper();

    public String toJSON() {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
