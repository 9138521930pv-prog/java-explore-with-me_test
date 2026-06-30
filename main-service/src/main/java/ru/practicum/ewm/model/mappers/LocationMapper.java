
package ru.practicum.ewm.model.mappers;

import org.mapstruct.Mapper;
import ru.practicum.ewm.dto.LocationDto;
import ru.practicum.ewm.model.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    Location toLocation(LocationDto locationDto);

    LocationDto toLocationDto(Location location);
}