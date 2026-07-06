
package ru.practicum.ewm.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.LocationDto;
import ru.practicum.ewm.model.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    @Mapping(target = "id", ignore = true)
    Location toLocation(LocationDto locationDto);

    LocationDto toLocationDto(Location location);
}