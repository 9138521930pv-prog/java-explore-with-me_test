package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.dto.RequestsEventCountDto;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.model.enums.RequestStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByEventId(Long eventId);

    Optional<Request> findByEventIdAndId(Long eventId, Long id);

    int countByEventIdAndStatus(Long eventId, RequestStatus status);

    List<Request> findAllByEventIdInAndStatus(List<Long> eventIds, RequestStatus status);

    Boolean existsByEventIdAndRequesterId(Long eventId, Long userId);

    Optional<Request> findByIdAndRequesterId(Long id, Long requesterId);

    List<Request> findAllByRequesterId(Long userId);

    Optional<List<Request>> findByEventIdAndIdIn(Long eventId, List<Long> id);

    @Query("""
    SELECT new ru.practicum.ewm.dto.RequestsEventCountDto(r.event.id, COUNT(r))
    FROM requests r
    WHERE r.event.id IN :eventIds
      AND r.status = :status
    GROUP BY r.event.id
    """)
    List<RequestsEventCountDto> countsByEventIdInAndStatus(
            @Param("eventIds") List<Long> eventIds,
            @Param("status") RequestStatus status
    );

}