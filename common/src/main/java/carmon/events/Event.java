package carmon.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CurrentSpeed.class),
        @JsonSubTypes.Type(value = DriverSeatOccupied.class),
        @JsonSubTypes.Type(value = TirePressure.class)
})
public abstract class Event {}
