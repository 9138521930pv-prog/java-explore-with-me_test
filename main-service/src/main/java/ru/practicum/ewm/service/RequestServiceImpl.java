package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.UncorrectedParametersException;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.enums.EventStatus;
import ru.practicum.ewm.model.enums.RequestStatus;
import ru.practicum.ewm.model.mappers.RequestMapper;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Override
    public ParticipationRequestDto addNewRequest(Long userId, Long eventId) {
        User user = checkUser(userId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found: id = " + eventId));
        LocalDateTime createdOn = LocalDateTime.now();
        validateNewRequest(event, userId, eventId);
        Request request = new Request();
        request.setCreated(createdOn);
        request.setRequester(user);
        request.setEvent(event);


        request.setStatus(
                event.isRequestModeration() && event.getParticipantLimit() != 0
                        ? RequestStatus.PENDING
                        : RequestStatus.CONFIRMED
        );

        requestRepository.save(request);

         return requestMapper.toParticipationRequestDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByUserId(Long userId) {
        checkUser(userId);
        List<Request> result = requestRepository.findAllByRequesterId(userId);
        return result.stream().map(requestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        checkUser(userId);
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId).orElseThrow(
                () -> new NotFoundException("No request found for ID " + requestId));
        if (request.getStatus().equals(RequestStatus.CANCELED) || request.getStatus().equals(RequestStatus.REJECTED)) {
            throw new UncorrectedParametersException("The request is not confirmed");
        }
        request.setStatus(RequestStatus.CANCELED);
        Request requestAfterSave = requestRepository.save(request);
        return requestMapper.toParticipationRequestDto(requestAfterSave);
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
            new NotFoundException("Category with id = " + userId + " does not exist"));
    }


    private void validateNewRequest(Event event, Long userId, Long eventId) {
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("User with id = " + userId + " is not the event initiator");
        }
        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() <= requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED)) {
            throw new ConflictException("The event participant limit has been exceeded");
        }
        if (!event.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new ConflictException("The event is not published");
        }
        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ConflictException("Attempt to add a duplicate");
        }
    }
}