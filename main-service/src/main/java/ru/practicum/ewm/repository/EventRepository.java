package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.enums.EventStatus;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    Optional<Event> findByInitiatorIdAndId(Long userId, Long eventId);

    Page<Event> findByInitiatorId(Long userId, Pageable pageable);

    List<Event> findByCategory(Category category);

    List<Event> findAllByIdIn(List<Long> ids);

    @Query("select e from Event e " +
            "join fetch e.category " +
            "join fetch e.initiator " +
            "where e.initiator.id in " +
            "(select us.targetUser.id from UserSubscription us " +
            "where us.subscriber.id = :subscriberId) " +
            "and e.eventStatus = :status")
    List<Event> findSubscribedUsersEvents(@Param("subscriberId") Long subscriberId, @Param("status") EventStatus status);

}