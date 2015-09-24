package carmon.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CurrentSpeed.class),
        @JsonSubTypes.Type(value = DriverSeatOccupied.class),
        @JsonSubTypes.Type(value = TirePressure.class)
})
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
