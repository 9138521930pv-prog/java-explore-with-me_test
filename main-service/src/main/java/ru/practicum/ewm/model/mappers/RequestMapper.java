package ru.practicum.ewm.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.model.Request;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(source = "event.id", target = "event")
    @Mapping(source = "requester.id", target = "requester")
    ParticipationRequestDto toParticipationRequestDto(Request request);

    @Mapping(target = "event", ignore = true)
    @Mapping(target = "requester", ignore = true)
    Request toRequest(ParticipationRequestDto participationRequestDto);
}