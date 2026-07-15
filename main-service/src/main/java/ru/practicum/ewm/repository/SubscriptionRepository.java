package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.model.UserSubscription;
import ru.practicum.ewm.model.UserSubscriptionId;


import java.util.List;

public interface SubscriptionRepository extends JpaRepository<UserSubscription, UserSubscriptionId> {
    boolean existsBySubscriberIdAndTargetUserId(Long subscriberId, Long targetUserId);

    List<UserSubscription> findAllBySubscriberId(Long subscriberId);

    @Modifying
    @Query("delete from UserSubscription us " +
            "where us.targetUser.id = :targetUserId")
    void deleteAllByTargetUserId(@Param("targetUserId") Long targetUserId);
}
