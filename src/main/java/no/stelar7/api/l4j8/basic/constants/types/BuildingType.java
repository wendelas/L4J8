package no.stelar7.api.l4j8.basic.constants.types;

import java.util.*;
import java.util.stream.*;

public enum BuildingType implements CodedEnum

{
    INHIBITOR_BUILDING,
    TOWER_BUILDING;
    
    /**
     * Returns an BuildingType from the provided value
     *
     * @param type the type to check
     * @return BuildingType
     */
    public Optional<BuildingType> getFromCode(final String type)
    {
        return Stream.of(BuildingType.values()).filter(t -> t.name().equalsIgnoreCase(type)).findFirst();
    }
    
    /**
     * The value used to map strings to objects
     *
     * @return String
     */
    public String getCode()
    {
        return this.name();
    }
}
